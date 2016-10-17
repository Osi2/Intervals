import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Yegor on 10/17/2016.
 */
public class Extents {

    private int _offset = 50000;
    private boolean _writeOutput = false;


    private class Point {

        public char Type; // type A - start of interval, type B - end
        public int Value; // value of point
        public long Count; // count of extents for some point
        public int Position; // initial position of point

        //constructor for array of extents
        public Point(char c, String value){this.Type = c; this.Value = Integer.valueOf(value);}

        //constructors for array of points
        public Point(String value, int position){this.Value = Integer.valueOf(value); this.Position = position;}
        public Point(int value, int position, long count){this.Value = value; this.Position = position; this.Count = count;}

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

        List<Point> extents = ReadExtentsFromFile(fileExtents);

        List<String> pointsList;

        try(BufferedReader reader = new BufferedReader(new FileReader(filePoints)))
        {

            while (!(pointsList = ReadPointsFromFile(reader, _offset)).isEmpty())
            {
                List<Point> points = ConvertArrayToList(pointsList);
                ProcessPoints(extents, points, fileResult);
            }

        }
        catch (IOException e)
        {
            logError(e.toString());
        }


        long estimatedTime = System.currentTimeMillis() - startTime;

        log("program completed");
        log("spent time, sec: " + String.valueOf((float)estimatedTime/1000));


    }

    private List<Point> ConvertArrayToList(List<String> strings) {

        final List<Point> points = new ArrayList<>();

        int i = 0;
        for (String s: strings) {
            points.add(new Point(s,i));
            i++;
        }

        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Integer.compare(o1.Value,o2.Value);
            }
        });

        return points;
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
            logError(e.toString());
        }
    }


    // Read extents array and modify them to array of points
    // Then sort those points in ascending order
    // And then for each point count inside how many extents it is

    private List<Point> ReadExtentsFromFile(String fileExtents)
    {
        try{

            List<String> extents = Files.readAllLines(Paths.get(fileExtents), Charset.defaultCharset());

            List<Point> points = new ArrayList<>();

            for (String s : extents) {

                if (s.isEmpty()) continue;

                String[] s1 = s.split(" ");

                points.add(new Point('A', s1[0].trim()));
                points.add(new Point('B', s1[1].trim()));
            }

            Collections.sort(points, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return Integer.compare(o1.Value, o2.Value);
                }
            });


            long counter = 0;
            List<Point> sortedPoints = new ArrayList<>();

            for (Point p : points) {
                if (p.Type == 'A') counter++;
                if (p.Type == 'B') counter--;

                p.Count = counter;

                sortedPoints.add(p);
            }

            return sortedPoints;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }

    }

    // read specific amount of lines from file
    public List<String> ReadPointsFromFile(BufferedReader reader, int count)
    {
        List<String> strings = new ArrayList<>();
        String line;

        try {

            while (strings.size() < count && (line = reader.readLine()) != null) {
                strings.add(line);
            }
            return strings;
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            System.out.println("extents file doesn't exist, exiting");

        if (!existsPoints)
            System.out.println("extents file doesn't exist, exiting");

        if (existsResults && !deletedResults)
            System.out.println("results file couldn't be deleted, exiting");

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
