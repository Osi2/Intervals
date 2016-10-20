import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Yegor on 10/20/2016.
 */
public class ExtentsSolver {

    private int EXTENTS_COUNT = 500000;
    private double[] countByPoint;
    private int _counter = 0;

    public ExtentsSolver(){
        countByPoint = new double[EXTENTS_COUNT];
    }

    public static void main(String[] args) throws IOException {

        String inputDir = args.length > 0 ? args[0] : ".//data";

        ExtentsSolver extentsSolver = new ExtentsSolver();
        extentsSolver.addExtents(Paths.get(inputDir + "/extents.txt"));
        extentsSolver.sortExtentsAndCalcCounts();
        extentsSolver.processPoints(Paths.get(inputDir + "/numbers.txt"), Paths.get(inputDir + "/result.txt"));

    }

    private void addExtents(Path path) throws IOException {
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNext()) {
                addExtent(scanner.nextDouble(), scanner.nextDouble());
            }
        }
    }

    private void addExtent(Double a, Double b){
        countByPoint[_counter++] = Double.valueOf(a.intValue() + ".10000011");
        countByPoint[_counter++] = Double.valueOf(b.intValue() + ".20000011");
    }

    private void sortExtentsAndCalcCounts(){
        Arrays.sort(countByPoint);

        for (int i = 0; i < countByPoint.length; i++) {
            if (countByPoint[i] == 0.0) continue;

            double prev = 0.0;
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

        String s = String.valueOf(intCurr) + "." + String.valueOf(type) + "0000000".substring(1, 7 - len) + String.valueOf(count) + "1";
//        System.out.println(s);
        return Double.valueOf(s);
    }

    private void processPoints(Path input, Path output) throws IOException{
        try (Scanner scanner = new Scanner(input);
             BufferedWriter bw = new BufferedWriter(new FileWriter(output.toString()))) {
            while (scanner.hasNext()) {
                int point = scanner.nextInt();
                int count = readPointCount(point);
                bw.write(String.valueOf(count) + "\n");
            }
        }
    }

    private int readPointCount(int p) {
        int count = 0;
        int i;

        for (i = 0; i < countByPoint.length; i++) {
            if (countByPoint[i] == 0.0)
                continue;
            else
                break;
        }

        double prev = countByPoint[i];

        for (int j = i + 1; j < countByPoint.length; j++) {
            if (p >= prev && p <= countByPoint[j]) {
                String _prev = String.valueOf(prev);
                int dot = _prev.indexOf('.');
                count = _prev.equals("0.0") ? 1 : Integer.parseInt(_prev.substring(dot + 2, _prev.length() - 1));
                break;
            }
            prev = countByPoint[j];
        }
        return count;
    }

}
