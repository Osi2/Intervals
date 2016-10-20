import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Yegor on 10/20/2016.
 */
public class ExtentsSolver2 {

    private static int EXTENTS_COUNT = 100000;
    private int FILES_COUNT;
    private int[] _extentsA;
    private int[] _extentsB;
    private int _counter = 0;

    public ExtentsSolver2(String path) throws IOException {
        int count = readFileLinesCount(path);
        _extentsA = new int[count];
        _extentsB = new int[count];

        FILES_COUNT = Math.round(count/EXTENTS_COUNT);
    }

    public static void main(String[] args) throws IOException {

        String inputDir = args.length > 0 ? args[0] : ".//data";

        long startTime = System.currentTimeMillis();

        ExtentsSolver2 extentsSolver = new ExtentsSolver2(inputDir + "/extents.txt");
        extentsSolver.createPoints(inputDir + "/extents.txt", EXTENTS_COUNT, inputDir + "/data_a_%d.txt",inputDir + "/data_b_%d.txt");
//        extentsSolver.addExtents(Paths.get(inputDir + "/extents.txt"));
//        extentsSolver.sortExtentsAndCalcCounts();
//        extentsSolver.processPoints(Paths.get(inputDir + "/numbers.txt"), Paths.get(inputDir + "/result.txt"));

        long estimatedTime0 = System.currentTimeMillis() - startTime;

        System.out.println("processing completed, sec: " + String.valueOf((float)estimatedTime0/1000));

    }

    private void createPoints(String inFile, int count, String outFile1, String outFile2) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(inFile))) {

            for (int i = 0; i < FILES_COUNT; i++) {

                int rows = readExtentsFromFile(reader, count);

                if (rows > 0) {

                    try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(String.format(outFile1, i)));
                         BufferedWriter bw2 = new BufferedWriter(new FileWriter(String.format(outFile2, i)))) {


                        for (long l : _extentsA) {
                            bw1.write(String.valueOf(l) + "\n");
                        }

                        for (long l : _extentsB) {
                            bw2.write(String.valueOf(l) + "\n");
                        }

                        if (rows < count)
                            break;
                    }
                }
            }
        }
    }

    public int readExtentsFromFile(BufferedReader reader, int count) throws  IOException
    {
        String line;
        String s[];

        int i = 0;
        while (i < count && (line = reader.readLine()) != null) {

            s = line.split(" ");
            if (s.length != 2) continue;

            _extentsA[i]=Integer.valueOf(s[0]);
            _extentsB[i]=Integer.valueOf(s[1]);

            i++;
        }

        Arrays.sort(_extentsA);
        Arrays.sort(_extentsB);

        return i;
    }


    private int readFileLinesCount(String path) throws IOException{
        String line;
        int count = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            while((line = br.readLine())!=null){
                count++;
            }
        }
        return count;
    }

        private class FileProcessor
        {
            private int FILE_COUNT;
            private BufferedReader[] readersA,readersB;
            private int[] indexesA,prevIndexesA;
            private int[] indexesB, prevIndexesB;
            private long[] currArrayA;
            private long[] currArrayB;
            private int resCount=0;
            private int prevCount;
            private long resA;
            private long resAPrev;
            private long resB;
            private String resFile;

            public FileProcessor(String file1, String file2, String fileRes) throws FileNotFoundException
            {
                List<String> filesA = new ArrayList<>();
                List<String> filesB = new ArrayList<>();

                resFile = fileRes;

                int i=0;

                while (true) {
                    String fileA = String.format(file1,i);
                    String fileB = String.format(file2,i);

                if (new File(fileA).exists() && new File(fileB).exists()) {
                        filesA.add(fileA);
                        filesB.add(fileB);
                    }
                    else
                        break;
                }
                FILE_COUNT = i;

                currArrayA = new long[FILE_COUNT];
                currArrayB = new long[FILE_COUNT];
                indexesA = new int[FILE_COUNT];
                indexesB = new int[FILE_COUNT];
                prevIndexesA = new int[FILE_COUNT];
                prevIndexesB = new int[FILE_COUNT];
                readersA = new BufferedReader[FILE_COUNT];
                readersB = new BufferedReader[FILE_COUNT];

                for (int j = 0; j < FILE_COUNT; j++) {
                    readersA[i] = new BufferedReader(new FileReader(filesA.get(j)));
                    readersB[i] = new BufferedReader(new FileReader(filesB.get(j)));
                    prevIndexesA[i]=-1;
                    prevIndexesB[i]=-1;
                }
            }

            private long[] getFirstLine(BufferedReader[] readers, int[] indexes, int[] prevIndexes, long[] array) throws  IOException
            {
                String[] line = new String[FILE_COUNT];

                for (int i = 0; i < FILE_COUNT ; i++) {

                    if (indexes[i] > prevIndexes[i]) {
                        line[i] = readers[i].readLine();

                        if (line[i] != null && !line[i].trim().isEmpty()) {
                            array[i] = Long.valueOf(line[i]);
                        }
                        else
                            array[i] = Long.MAX_VALUE;

                        prevIndexes[i]=indexes[i];
                    }
                }
                return array;
            }

            private long[] getMinValue(long[] array)
            {
                long[] result = new long[2];

                long min = array[0];
                result[0]=0;
                for (int i = 1; i < array.length; i++) {
                    if (array[i] < min) {min=array[i];result[0]=i;}
                }
                result[1]=min;
                return result;
            }

            public void MergeFiles() throws IOException
            {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(resFile))){

                    currArrayB = getFirstLine(readersB, indexesB, prevIndexesB, currArrayB);
                    long[] res = getMinValue(currArrayB);
                    resB = res[1];

                    for (int i = 0; i < FILE_COUNT; i++) {
                        indexesB[i]=-1;
                        prevIndexesB[i]=-1;
                    }

                    while(true) {

                        currArrayA = getFirstLine(readersA, indexesA, prevIndexesA, currArrayA);

                        res = getMinValue(currArrayA);
                        if (res[1] == Long.MAX_VALUE) {
                            writeValue(bw, resA, resCount);
                            break;
                        }

                        if (resA != res[1]) {

                            if (resA !=-1) {
                                writeValue(bw,resA, resCount);
                                resCount = 1;
                            }
                            resA = res[1];
                        } else
                            resCount++;

                        indexesA[(int) res[0]]++;

                    }

                    boolean foundCount=true;

                    while (foundCount) {
                        foundCount = calcCountB(bw,0);
                    }

                }
            }

            private void writeValue(BufferedWriter bw, long value, int count) throws IOException{
                int resCount=0;
                boolean foundCount=false;

                if(resB >= resAPrev && resB <= value) {

                    while (resB >= resAPrev && resB <= value) {
                        while (!foundCount) {
                            foundCount = calcCountB(bw,count);
                        }
                    }
                }
                else {
                    bw.write(value + " " + (count + prevCount - resCount) + "\n");
                    prevCount += count;
                    resAPrev = value;
                }
            }

            private boolean calcCountB(BufferedWriter bw,int count) throws IOException{

                int resCount = 0;
                while(true) {

                    currArrayB = getFirstLine(readersB, indexesB, prevIndexesB, currArrayB);

                    long[] res = getMinValue(currArrayB);

                    if (res[1] == Long.MAX_VALUE) {
                        bw.write(resB + " " + (count + prevCount - resCount) + "\n");
                        return false;
                    }

                    if (resB != res[1]) {

                        if (resB != -1) {
                            bw.write(resB + " " + (count + prevCount - resCount) + "\n");
                            prevCount += (count - resCount);
                            resAPrev = resB;
                            resB = res[1];
                            return true;
                        }
                    } else{
                        resCount++;
                        indexesB[(int) res[0]]++;
                    }
                }
            }
    }
}

    

