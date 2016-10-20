import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Yegor on 10/20/2016.
 */
public class ExtentsSolver1 {

    private int POINTS_COUNT = 10000;
    private double[] countByPoint;
    private int _counter = 0;

    public ExtentsSolver1(Path path) throws IOException{
        int count = readFileLinesCount(path);
        countByPoint = new double[count * 2];
    }

    public static void main(String[] args) throws IOException {

        String inputDir = args.length > 0 ? args[0] : ".//data";

        long startTime = System.currentTimeMillis();

        ExtentsSolver1 extentsSolver = new ExtentsSolver1(Paths.get(inputDir + "/extents.txt"));
        extentsSolver.addExtents(inputDir + "/extents.txt");
        extentsSolver.sortExtentsAndCalcCounts();
        extentsSolver.processPoints(inputDir + "/numbers.txt", inputDir + "/result.txt");

        long estimatedTime0 = System.currentTimeMillis() - startTime;

        System.out.println("processing completed, sec: " + String.valueOf((float)estimatedTime0/1000));

    }

    private void addExtents(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            String[] s;
            while ((line = reader.readLine())!=null) {
                s = line.split(" ");
                double d1 = Double.valueOf(s[0]);
                double d2 = Double.valueOf(s[1]);
                addExtent(d1, d2);
            }
        }
    }

    private void addExtent(Double a, Double b){
        countByPoint[_counter++] = Double.valueOf(a.intValue() + ".100000011");
        countByPoint[_counter++] = Double.valueOf(b.intValue() + ".200000011");
    }

    private void sortExtentsAndCalcCounts(){
        Arrays.sort(countByPoint);

        double prev = 0.0;
        for (int j = 0; j < countByPoint.length; j++) {
            countByPoint[j] = changeCounter(String.valueOf(countByPoint[j]), String.valueOf(prev));
            prev = countByPoint[j];
        }

    }

    private double changeCounter(String curr, String prev){

        int dotCurr = curr.indexOf('.');
        int dotPrev = prev.indexOf('.');

        int intCurr = Integer.parseInt(curr.substring(0, dotCurr));
        int intPrev = Integer.parseInt(prev.substring(0, dotPrev));
        byte type = Byte.parseByte(curr.substring(dotCurr + 1, dotCurr + 2));
        int count = prev.equals("0.0") ? 0 : Integer.parseInt(prev.substring(dotPrev + 2, prev.length() - 1));

        if (intCurr == intPrev && count != 0)
            count = intPrev;
        else {
            if (type == 1)
                count++;
            else
                count--;
        }

        int len = String.valueOf(count).length();
        if (count < 0) count = 0;

        String s = "";

        try {
            s = String.valueOf(intCurr) + "." + String.valueOf(type) + "00000000".substring(1, 8 - len) + String.valueOf(count) + "1";
        } catch (Exception e)
        {
            System.out.println("error occured for curr:" + curr + " , len: " + len + ", type: " + type + ", count: " + count + ", intCurr: " + intCurr + "\n");
        }
//        System.out.println(s);
        return Double.valueOf(s);
    }

    private void processPoints(String input, String output) throws IOException{
        String line;
        String[] s;
        int i=0;
        List<Point> points = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(input));
             BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {

            while ((line = reader.readLine())!=null){

                int point = Integer.valueOf(line);
                points.add(new Point(point,0,i));

                if (i > POINTS_COUNT) {
                    writeOutput(bw, points);
                    points.clear();
                    i=0;
                }

                i++;
            }
            writeOutput(bw,points);
        }
    }

    private void writeOutput(BufferedWriter bw, List<Point> list) throws IOException{
        List<Point> _points = readPointCount(sortListByValue(list));

        for(Point p: _points){
            bw.write(String.valueOf(p.count) + "\n");
        }
    }

    private List<Point> readPointCount(List<Point> points) {
        int k = 1;

        for (Point p:points){

            double prev = countByPoint[k-1];

            for (int j = k; j < countByPoint.length; j++) {
                if (p.value >= prev && p.value <= countByPoint[j]) {
                    String _prev = String.valueOf(prev);
                    int dot = _prev.indexOf('.');
                    p.count = _prev.equals("0.0") ? 1 : Integer.parseInt(_prev.substring(dot + 2, _prev.length() - 1));
                    k = j;
                    break;
                }
                prev = countByPoint[j];
            }
        }
        return sortListByPosition(points);
    }

    private List<Point> sortListByPosition(List<Point> list){

        Collections.sort(list, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Integer.compare(o1.position, o2.position);
            }
        });

        return list;
    }

    private List<Point> sortListByValue(List<Point> list){

        Collections.sort(list, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Integer.compare(o1.value, o2.value);
            }
        });

        return list;
    }

    private int readFileLinesCount(Path path) throws IOException{
        String line;
        int count = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
            while((line = br.readLine())!=null){
                count++;
            }
        }
        return count;
    }

    private class Point{

        public int value;
        public int count;
        public int position;

        public Point(int value, int count, int position){
            this.count = count;
            this.value = value;
            this.position = position;
        }
    }
}
