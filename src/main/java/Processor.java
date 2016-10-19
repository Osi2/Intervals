import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * Created by Yegor on 10/19/2016.
 */
public class Processor {
    private int numFiles = 10;
    private BufferedReader[] readersA,readersB;
    private int[] indexesA,prevIndexesA;
    private int[] indexesB, prevIndexesB;
    private long[] currArrayA;
    private long[] currArrayB;
    private long resValue=-1;
    private int resCount=0;
    private int prevCount;
    private long resA;
    private long resAPrev;
    private long resB;
    private long resBPrev;
    private String resFile = "D:\\_Projects\\Intervals\\data\\tmp\\result.txt";

    public Processor() throws FileNotFoundException
    {
        List<String> filesA = new ArrayList<>();
        List<String> filesB = new ArrayList<>();
        String fileA,fileB;
        for (int i = 0; i < numFiles; i++) {
            fileA = String.format("D:\\_Projects\\Intervals\\data\\tmp\\data_a_%d.txt",i);
            fileB = String.format("D:\\_Projects\\Intervals\\data\\tmp\\data_b_%d.txt",i);
            if (new File(fileA).exists() && new File(fileB).exists()) {
                filesA.add(fileA);
                filesB.add(fileB);
            }
            else
            {
                numFiles = i;
                break;
            }
        }
        currArrayA = new long[numFiles];
        currArrayB = new long[numFiles];
        indexesA = new int[numFiles];
        indexesB = new int[numFiles];
        prevIndexesA = new int[numFiles];
        prevIndexesB = new int[numFiles];
        readersA = new BufferedReader[numFiles];
        readersB = new BufferedReader[numFiles];

        for (int i = 0; i <numFiles; i++) {
            readersA[i] = new BufferedReader(new FileReader(filesA.get(i)));
            readersB[i] = new BufferedReader(new FileReader(filesB.get(i)));
            prevIndexesA[i]=-1;
            prevIndexesB[i]=-1;
        }
    }

    private long[] getFirstLine(BufferedReader[] readers, int[] indexes, int[] prevIndexes, long[] array) throws  IOException
    {
        String[] line = new String[numFiles];
        for (int i = 0; i < numFiles ; i++) {

                if (indexes[i] > prevIndexes[i]) {
                    line[i] = readers[i].readLine();

                    if (line[i] != null && !line[i].trim().isEmpty()) {
                        array[i] = Long.valueOf(line[i]);
                    }
                    else
                        array[i]=Long.MAX_VALUE;

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
            if (array[i] < min) {min=array[i];result[0]=i;break;}
        }
        result[1]=min;
        return result;
    }

    public void Run() throws IOException
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resFile))){

            currArrayB = getFirstLine(readersB, indexesB, prevIndexesB, currArrayB);
            long[] res = getMinValue(currArrayB);
            resB = res[1];

            for (int i = 0; i < 1000; i++) {
                currArrayA = getFirstLine(readersA, indexesA, prevIndexesA, currArrayA);

                res = getMinValue(currArrayA);
                if (res[1] == Long.MAX_VALUE) {
                    writeValue(bw, resA, resCount);
                    break;
                }
                indexesA[(int) res[0]]++;

                if (resA != res[1]) {

                    if (resA !=-1) {
                        writeValue(bw,resA, resCount);
                        resCount = 1;
                    }
                    resA = res[1];
                } else
                    resCount++;

            }

        }
    }

    private void writeValue(BufferedWriter bw, long value, int count) throws IOException{
        int resCount=0;

        if(resB >= resAPrev && resB <= value) {

            bw.write(resB + "|" + (count + prevCount - resCount) + "\n");

            while (resB >= resAPrev || resB <= resA) {
                currArrayB = getFirstLine(readersB, indexesB, prevIndexesB, currArrayB);
                long[] res = getMinValue(currArrayB);
                resB = res[1];

                res = getMinValue(currArrayB);
                if (res[1] == Long.MAX_VALUE) {
                    bw.write(resB + "|" + (count + prevCount - resCount) + "\n");
//                writeValue(bw, resA, resCount);
                    break;
                }
                indexesB[(int) res[0]]++;

                if (resB != res[1]) {

                    if (resB != -1) {
                        bw.write(resB + "|" + (count + prevCount - resCount) + "\n");
//                    writeValue(bw,resA, resCount);
                        resCount = 1;
                    }
                    resB = res[1];
                } else
                    resCount++;
            }
        }
        else {

            bw.write(value + "|" + (count + prevCount - resCount) + "\n");
            prevCount += count;
            resAPrev = value;
        }
    }
}
