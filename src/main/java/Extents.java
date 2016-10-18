import java.io.*;
import java.util.*;

/**
 * Created by Yegor on 10/17/2016.
 */
public class Extents {

    private int _offsetPoints = 50000; // read by this count from numbers.txt
    private int _offsetExtents = 5000000; // read by this count from extents.txt and save to temp file
    private boolean _writeOutput = false;
    private int _numPartitions = 10;
    private String fileExtentsRes =  "D:\\_Projects\\Intervals\\data\\extents_res.txt";
    private String fileExtentsTmpA = "D:\\_Projects\\Intervals\\data\\tmp\\extents_tmp_a_%d.txt";
    private String fileExtentsTmpB = "D:\\_Projects\\Intervals\\data\\tmp\\extents_tmp_b_%d.txt";

    private List<Long> _extentsA;
    private List<Long> _extentsB;


    private class Point {

        public char Type; // type A - start of interval, type B - end
        public long Value; // value of point
        public int Count; // count of extents for some point
        public int Position; // initial position of point

        //constructor for array of extents
        public Point(char c, String value){this.Type = c; this.Value = Long.valueOf(value);}

        //constructors for array of points
        public Point(String value, int position){this.Value = Long.valueOf(value); this.Position = position;}
        public Point(long value, int position, int count){this.Value = value; this.Position = position; this.Count = count;}

    }

    public static void main(String[] args) {

        String fileExtents = "extents.txt";
        String filePoints = "numbers.txt";
        String fileExpected = "expected.txt";
        String fileResult = "result.txt";

        Extents extents = new Extents();

        extents.Run(fileExtents,filePoints,fileResult);

    }


    public void Run(String fileExtents, String filePoints, String fileResult)
    {

        log("program started");

        if (!checkFiles(fileExtents,filePoints,fileResult)) return;

        long startTime = System.currentTimeMillis();

        List<Point> extents, points;

        // read points from numbers.txt sequentially by row count = _offsetExtents
        while (!(extents = ReadAndSortExtents(fileExtents, _offsetExtents)).isEmpty())
        {
            // read points from numbers.txt sequentially by row count = _offsetPoints
            try (BufferedReader reader = new BufferedReader(new FileReader(filePoints))) {

                while (!(points = ReadPointsFromFile(reader, _offsetPoints, false)).isEmpty()) {
                    ProcessPoints(extents, points, fileResult);
                }

            } catch (IOException e) {
                logError(e.toString());
            }
        }


        long estimatedTime = System.currentTimeMillis() - startTime;

        log("program completed");
        log("spent time, sec: " + String.valueOf((float)estimatedTime/1000));


    }


    private void ProcessPoints(List<Point> extents, List<Point> points, String outFile) {

        List<Point> resultList = new ArrayList<>();

        try (FileWriter fw = new FileWriter(outFile,true)){

            int k = 0;
            Point pe;

            for (Point p: points) {

                for (int j = k; j < extents.size(); j++) {

                    pe = extents.get(j);

                    if (p.Value <= pe.Value) {

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

            // Output results
            for (Point p: resultList) {
                fw.write(String.valueOf(p.Count) + "\n");
            }

        } catch (IOException e) {
            logError(e.getMessage());
        }
    }


    // Read extents array and modify them to array of points
    // Then sort those points in ascending order
    // And then for each point count inside how many extents it is

    private List<Point> ReadAndSortExtents(String fileExtents, int count)
    {
        int rows;

        try(BufferedReader reader = new BufferedReader(new FileReader(fileExtents))) {

            for(int i = 0; i< _numPartitions; i++){

                try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(String.format(fileExtentsTmpA, i)));
                     BufferedWriter bw2 = new BufferedWriter(new FileWriter(String.format(fileExtentsTmpB, i)))) {

                    rows = ReadExtentsFromFile(reader, count);

                    if (rows > 0) {

                        for(long l: _extentsA)
                        {
                            bw1.write(String.valueOf(l) + "\n");
                        }
                        bw1.flush();

                        for(long l: _extentsB)
                        {
                            bw2.write(String.valueOf(l) + "\n");
                        }
                        bw2.flush();
                    }

                    if (rows < count)
                        break;

                } catch (Exception e) {
                    logError(e.getMessage());
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            logError(e.getMessage());
            return null;
        }

        /// TODO: make merge sort in one large extentsRes.txt file

//        try(BufferedReader reader = new BufferedReader(new FileReader(fileExtents))){
//
//            List<Point> extents = ReadPointsFromFile(reader,_offsetPoints,false);
//
//            int counter = 0;
//
//            for (Point p : extents) {
//                if (p.Type == 'A') counter++;
//                if (p.Type == 'B') counter--;
//
//                p.Count = counter;
//            }
//            return extents;
//
//        }
//        catch (Exception e)
//        {
//            return  null;
//        }

        return null;

    }

    public int ReadExtentsFromFile(BufferedReader reader, int count)
    {
        _extentsA = new ArrayList<>();
        _extentsB = new ArrayList<>();

        String line;
        String s[];

        try {

            int i = 0;
            while (i < count && (line = reader.readLine()) != null) {

                    s = line.split(" ");
                    if (s.length != 2) continue;

                    _extentsA.add(Long.valueOf(s[0]));
                    _extentsB.add(Long.valueOf(s[1]));

                i++;
            }

            Collections.sort(_extentsA);
            Collections.sort(_extentsB);

            return i;
        }
        catch (Exception e)
        {
            logError(e.getMessage());
            return 0;
        }

    }

    // read specific amount of lines from file
    public List<Point> ReadPointsFromFile(BufferedReader reader, int count, boolean doExtents )
    {
        List<Point> points = new ArrayList<>();

        String line;

        try {

            int i = 0;
            while (i < count && (line = reader.readLine()) != null) {

                points.add(new Point(line, i));

                i++;
            }

            Collections.sort(points, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return Long.compare(o1.Value,o2.Value);
                }
            });

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

    private long[] merge(long[] a, long[] b) {

        long[] answer = new long[a.length + b.length];
        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length)
        {
            if (a[i] < b[j])
                answer[k++] = a[i++];

            else
                answer[k++] = b[j++];
        }

        while (i < a.length)
            answer[k++] = a[i++];


        while (j < b.length)
            answer[k++] = b[j++];

        return answer;
    }


}
