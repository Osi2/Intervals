import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Yegor on 10/20/2016.
 */
public class ExtentsSolver {

    private int EXTENTS_COUNT;
    private double[] countByPoint;
    private int _counter = 0;

    public ExtentsSolver(String inputDir) throws IOException{
        EXTENTS_COUNT = getFileLineCount(Paths.get(inputDir + "/extents.txt"));
        countByPoint = new double[EXTENTS_COUNT];
    }

    public static void main(String[] args) throws IOException {
        String inputDir = args.length > 0 ? args[0] : ".//data";

        ExtentsSolver extentsSolver = new ExtentsSolver(inputDir);
        extentsSolver.addExtents(Paths.get(inputDir + "/extents.txt"));
        extentsSolver.sortExtentsAndCalcCounts();
        extentsSolver.processPoints(Paths.get(inputDir + "/numbers.txt"));


    }

    private void addExtents(Path path) throws IOException {
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNext()) {
                addExtent(scanner.nextDouble(), scanner.nextDouble());
            }
        }
    }

    private void addExtent(Double a, Double b){
        countByPoint[_counter++] = Double.valueOf(a.intValue() + ".1");
        countByPoint[_counter++] = Double.valueOf(b.intValue() + ".2");
    }

    private void sortExtentsAndCalcCounts(){
        Arrays.sort(countByPoint);

        double prev = countByPoint[0];
        for (int i = 0; i < countByPoint.length; i++) {
            changeCounter(countByPoint[i],prev);
        }
    }

    private double changeCounter(Double curr, Double prev){
        byte type = (byte)curr.doubleValue();
        int count = 45;//(String.valueOf(prev.doubleValue()).substring(1, 2));

        if (type == 1)
            return Double.valueOf(curr.intValue() + "." + count++);
        else
            return Double.valueOf(curr.intValue() + "." + count--);
    }

    private void processPoints(Path path) throws IOException{
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNext()) {
                //addExtent(scanner.nextInt(), scanner.nextInt());
            }
        }
    }

    private int getFileLineCount(Path path)throws IOException{
        int lines = 0;
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNext()) {
                lines++;
            }
        }
        return lines;
    }
}
