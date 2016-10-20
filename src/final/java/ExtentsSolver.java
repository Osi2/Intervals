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

    private int EXTENTS_COUNT = 100;
    private double[] countByPoint;
    private int _counter = 0;

    public ExtentsSolver(){
        countByPoint = new double[EXTENTS_COUNT];
    }

    public static void main(String[] args) throws IOException {



        String inputDir = args.length > 0 ? args[0] : ".//data";

        ExtentsSolver extentsSolver = new ExtentsSolver();
//        extentsSolver.test();
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
        countByPoint[_counter++] = Double.valueOf(a.intValue() + ".1000011");
        countByPoint[_counter++] = Double.valueOf(b.intValue() + ".2000011");
    }

    private void sortExtentsAndCalcCounts(){
        Arrays.sort(countByPoint);

        for (int i = 0; i < countByPoint.length; i++) {
            if (countByPoint[i] == 0.0) continue;

            double prev = countByPoint[0];
            for (int j = i; j < countByPoint.length; j++) {
                countByPoint[j] = changeCounter(String.valueOf(countByPoint[j]), String.valueOf(prev));
                prev = countByPoint[j];
            }
            break;
        }
    }

    private double changeCounter(String curr, String prev){

        int dotCurr = curr.indexOf('.');
        int dotPrev = prev.indexOf('.');

        int _int = Integer.parseInt(curr.substring(0, dotCurr));
        byte type = Byte.parseByte(curr.substring(dotCurr + 1, dotCurr + 2));
        int count = prev.equals("0.0") ? 0 : Integer.parseInt(prev.substring(dotPrev + 2, prev.length() - 1));

        if (type == 1)
            count++;
        else
            count--;
        int len = String.valueOf(count).length();

        return Double.valueOf(String.valueOf(_int) + "." + String.valueOf(type) + "000000".substring(1, 6 - len) + String.valueOf(count) + "1");
    }

    private void processPoints(Path path) throws IOException{
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNext()) {
                //addExtent(scanner.nextInt(), scanner.nextInt());
            }
        }
    }

}
