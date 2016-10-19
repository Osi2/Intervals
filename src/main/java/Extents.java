import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Yegor on 10/17/2016.
 */
public class Extents {

    private int _offsetPoints = 50000; // read by this count from numbers.txt
    private int _offsetExtents = 10000; // read by this count from extents.txt and save to temp file
    private boolean _writeOutput = true;
    private int _numPartitions = 100;
    private String fileExtentsRes =  "D:\\_Projects\\Intervals\\data\\tmp\\data_counts.txt";
    private String fileExtentsTmpA = "D:\\_Projects\\Intervals\\data\\tmp\\data_a_%d.txt";
    private String fileExtentsTmpB = "D:\\_Projects\\Intervals\\data\\tmp\\data_b_%d.txt";

    private long[] _extentsA2;
    private long[] _extentsB2;

    private class Point {

        public char Type; // type A - start of interval, type B - end
        public long Value; // value of point
        public int Count; // count of extents for some point
        public int Position; // initial position of point
        public boolean Processed;

        //constructor for array of extents
        //public Point(char c, String value){this.Type = c; this.Value = Long.valueOf(value);}

        //constructors for array of points
        public Point(String value, int position){this.Value = Long.valueOf(value); this.Position = position;}
        public Point(long value, int position, int count){this.Value = value; this.Position = position; this.Count = count;}

    }

    public static void main(String[] args) {

        String fileExtents = "extents.txt";
        String filePoints = "numbers.txt";
        String fileExpected = "expected.txt";
        String fileResult = "D:\\_Projects\\Intervals\\data\\tmp\\result.txt";

        Extents extents = new Extents();

        extents.Run(fileExtents,filePoints,fileResult);

    }


    public void Run(String fileExtents, String filePoints, String fileResult)
    {

        log("program started");

        if (!checkFiles(fileExtents,filePoints,fileResult)) return;

        long startTime = System.currentTimeMillis();

        List<Point> extents, points;


        try {

            ReadAndSortExtents(fileExtents, _offsetExtents);

            Processor processor = new Processor();
            processor.Run();

            long estimatedTime0 = System.currentTimeMillis() - startTime;

            log("files created, sec: " + String.valueOf((float)estimatedTime0/1000));

            List<Point> results=new ArrayList<>();
            int i = 0;

            // read points from numbers.txt sequentially by row count = _offsetExtents
            try (BufferedReader br1 = new BufferedReader(new FileReader(filePoints));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(fileResult, true))) {

                while (!(points = ReadPointsFromFile(br1, _offsetPoints, false, i++)).isEmpty()) {
                    log("read number of points: " + points.size() + " start point: " + points.get(0).Position);

                    int j = 0;

                    try (FileReader f = new FileReader(fileExtentsRes);
                            BufferedReader br2 = new BufferedReader(f)){

                        while (!(extents = ReadPointsFromFile(br2, _offsetExtents, true, j++)).isEmpty()) {
                            log("read number of extents: " + extents.size() + " start point: " + extents.get(0).Position);
                            List<Point> l = ProcessPoints(extents, points);
                            log("processed number of points: " + l.size());

                            results.addAll(l);
                        }

                        for (Point p : results) {
                            bw.write(String.valueOf(p.Count) + "\n");
                        }
                        results.clear();

                        f.close();
                        br2.close();
                    }
                    catch (Exception e){
                        logError(e.getMessage());
                    }
                }

            } catch (IOException e) {
                logError(e.toString());
            }

        }
        catch (Exception e){
            logError(e.getMessage());
            e.printStackTrace();
        }


        long estimatedTime = System.currentTimeMillis() - startTime;

        log("program completed");
        log("spent time, sec: " + String.valueOf((float)estimatedTime/1000));


      }


    private List<Point> ProcessPoints(List<Point> extents, List<Point> points) {

        List<Point> resultList = new ArrayList<>();

            int k = 0;
            Point pe;

            for (Point p: points) {

                if (p.Processed) continue;

                for (int j = k; j < extents.size(); j++) {

                    pe = extents.get(j);

                    if (p.Value <= pe.Value) {

                        p.Processed = true;
                        resultList.add(new Point(p.Value, p.Position, pe.Count + 1));

                        k = j;
                        break;
                    }
                }
            }

            // Sort array by initial position
            Collections.sort(resultList, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return Integer.compare(o1.Position, o2.Position);
                }
            });

        return resultList;
    }


    // Read extents array and modify them to array of points
    // Then sort those points in ascending order
    // And then for each point count inside how many extents it is

    private boolean ReadAndSortExtents(String fileExtents, int count)
    {
        int rows;

        try(BufferedReader reader = new BufferedReader(new FileReader(fileExtents))) {

            for(int i = 0; i< _numPartitions; i++){

                rows = ReadExtentsFromFile(reader, count);

                if (rows > 0) {

                    try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(String.format(fileExtentsTmpA, i)));
                         BufferedWriter bw2 = new BufferedWriter(new FileWriter(String.format(fileExtentsTmpB, i)))) {


                        for (long l : _extentsA2) {
                            bw1.write(String.valueOf(l) + "\n");
                        }
                        bw1.flush();
                        bw1.close();

                        for (long l : _extentsB2) {
                            bw2.write(String.valueOf(l) + "\n");
                        }
                        bw2.flush();
                        bw1.close();


                        _extentsA2 = null;
                        _extentsB2 = null;

                        System.gc();
                        System.runFinalization();

                        // check if it was last file
                        if (rows < count)
                            break;

                    } catch (Exception e) {
                        logError(e.getMessage());
                        return false;
                    }
                }
            }
            return true;
        }
        catch (Exception e)
        {
            logError(e.getMessage());
            return false;
        }
    }

    public int ReadExtentsFromFile(BufferedReader reader, int count)
    {
        _extentsA2 = new long[count];
        _extentsB2 = new long[count];

        String line;
        String s[];

        try {

            int i = 0;
            while (i < count && (line = reader.readLine()) != null) {

                s = line.split(" ");
                if (s.length != 2) continue;

                _extentsA2[i]=Long.valueOf(s[0]);
                _extentsB2[i]=Long.valueOf(s[1]);

                i++;
            }

            Arrays.sort(_extentsA2);
            Arrays.sort(_extentsB2);

            return i;
        }
        catch (Exception e)
        {
            logError(e.getMessage());
            return 0;
        }

    }

    // read specific amount of lines from file
    public List<Point> ReadPointsFromFile(BufferedReader reader, int count, boolean doExtents, int k)
    {
        List<Point> points = new ArrayList<>();

        String line;
        String[] s;

        try {

            int i = 0;
            while (i < count && (line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                if(doExtents) {

                    s=line.split(" ");
                    if (s.length != 2) continue;
                    points.add(new Point(Long.valueOf(s[0]),k*count + i,Integer.valueOf(s[1])));

                }
                else
                    points.add(new Point(line, k*count + i));

                i++;
            }

            if(!doExtents) {
                Collections.sort(points, new Comparator<Point>() {
                    @Override
                    public int compare(Point o1, Point o2) {
                        return Long.compare(o1.Value, o2.Value);
                    }
                });
            }

            return points;
        }
        catch (Exception e)
        {
            logError(e.getMessage());
            return  null;
        }
    }


    /// check that source file exist
    /// and result file is deleted
    /// otherwise exit
    private Boolean checkFiles(String fileExtents, String filePoints, String fileResult)
    {
        Boolean existsExtents = (new File(fileExtents).exists());
        Boolean existsPoints = (new File(filePoints).exists());

        Boolean existsResults = (new File(fileResult).exists());
        Boolean deletedResults = (new File(fileResult).delete());

        if (!existsExtents)
            logError("extents file doesn't exist, exiting");

        if (!existsPoints)
            logError("extents file doesn't exist, exiting");

        if (existsResults && !deletedResults)
            logError("results file couldn't be deleted, exiting");

        return existsExtents && existsPoints;

    }

    private void log(String s){
        if (_writeOutput)
            System.out.println(s);
    }

    private void logError(String s){
        if (_writeOutput)
            System.out.println("Error: " + s);
    }
}
