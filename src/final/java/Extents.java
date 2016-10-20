//import java.io.*;
//import java.nio.file.Files;
//import java.util.*;
//
///**
// * Created by Yegor on 10/17/2016.
// */
//public class Extents {
//
//    private static int NUMBER_OF_POINTS = 50000; // read by this count from numbers.txt
//    private static int NUMBER_OF_EXTENTS = 100000; // read by this count from numbers.txt
//    private static int FILES_COUNT = 100;
//
//    private String fileExtentsTmpA = "data_a_%d.txt";
//    private String fileExtentsTmpB = "data_b_%d.txt";
//
//    private long[] _extentsA2;
//    private long[] _extentsB2;
//    private Point _prevExtent;
//
//    private class Point {
//
//        public long value;
//        public int count;
//        public int position;
//        public boolean processed;
//
//        public Point(String value, int position){
//            this.value = Long.valueOf(value);
//            this.position = position;
//        }
//        public Point(long value, int position, int count){
//            this.value = value;
//            this.position = position;
//            this.count = count;
//        }
//
//    }
//
//    public static void main(String[] args) throws IOException{
//
//        String inputDir = args.length > 0 ? args[0] : ".//";
//
//        String fileExtentsA = "data_a_%d.txt";
//        String fileExtentsB = "data_a_%d.txt";
//
//        Extents extents = new Extents();
//        FileProcessor fileProcessor = new FileProcessor(fileExtentsA,fileExtentsB,fileExtentsRes);
//        fileProcessor.MergeFiles();
//
//        extents.createPoints(inputDir + "/extents.txt", NUMBER_OF_EXTENTS,fileExtentsA,fileExtentsB );
//    }
//
//    private void createPoints(String file, int count, String file1, String file2) throws IOException {
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//
//            for (int i = 0; i < FILES_COUNT; i++) {
//
//                int rows = readExtentsFromFile(reader, count);
//
//                if (rows > 0) {
//
//                    try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(String.format(file1, i)));
//                         BufferedWriter bw2 = new BufferedWriter(new FileWriter(String.format(file2, i)))) {
//
//
//                        for (long l : _extentsA2) {
//                            bw1.write(String.valueOf(l) + "\n");
//                        }
//
//                        for (long l : _extentsB2) {
//                            bw2.write(String.valueOf(l) + "\n");
//                        }
//
//                        if (rows < count)
//                            break;
//                    }
//                }
//            }
//        }
//    }
//
//
//    public void Run(String fileExtents, String filePoints, String fileResult) throws IOException
//    {
//
//        log("program started");
//
//        String tmpDir = System.getProperty("user.dir") + "\\tmp";
//        File dir = new File(tmpDir);
//
//        if (!Files.exists(dir.toPath()))
//            Files.createDirectory(dir.toPath());
//
//        for(File f: dir.listFiles())
//            f.delete();
//
//
//        fileExtentsTmpA = tmpDir + "\\" + fileExtentsTmpA;
//        fileExtentsTmpB = tmpDir + "\\" + fileExtentsTmpB;
//        fileExtentsRes = tmpDir + "\\" + fileExtentsRes;
//
//        if (!checkFiles(fileExtents,filePoints,fileResult)) return;
//
//        long startTime = System.currentTimeMillis();
//
//        List<Point> extents, points;
//
//
//        try {
//
//            ReadAndSortExtents(fileExtents, _offsetExtents);
//
//            FileProcessor fileProcessor = new FileProcessor(fileExtentsTmpA,fileExtentsTmpB,fileExtentsRes);
//            fileProcessor.MergeFiles();
//
//            long estimatedTime0 = System.currentTimeMillis() - startTime;
//
//            log("files created, sec: " + String.valueOf((float)estimatedTime0/1000));
//
//            List<Point> results=new ArrayList<>();
//            int i = 0;
//
//            // read points from numbers.txt sequentially by row count = _offsetExtents
//            try (BufferedReader br1 = new BufferedReader(new FileReader(filePoints));
//                 BufferedWriter bw = new BufferedWriter(new FileWriter(fileResult, true))) {
//
//                while (!(points = ReadPointsFromFile(br1, _offsetPoints, false, i++)).isEmpty()) {
//                    log("\nread number of points: " + points.size() + " start point: " + points.get(0).position + "\n");
//
//                    int j = 0;
//
//                    try (FileReader f = new FileReader(fileExtentsRes);
//                         BufferedReader br2 = new BufferedReader(f)){
//
//                        while (!(extents = ReadPointsFromFile(br2, _offsetExtents, true, j++)).isEmpty()) {
//                            log("read number of extents: " + extents.size() + " start point: " + extents.get(0).position);
//                            List<Point> l = ProcessPoints(extents, points);
//                            log("processed number of points: " + l.size());
//
//                            results.addAll(l);
//                        }
//
//                        for (Point p : results) {
//                            bw.write(String.valueOf(p.count) + "\n");
//                            //log_(String.valueOf(p.count) + "\n");
//                        }
//                        results.clear();
//
//                        f.close();
//                        br2.close();
//                    }
//                    catch (Exception e){
//                        logError(e.getMessage());
//                    }
//                }
//
//            } catch (IOException e) {
//                logError(e.toString());
//            }
//
//        }
//        catch (Exception e){
//            logError(e.getMessage());
//            e.printStackTrace();
//        }
//
//
//        long estimatedTime = System.currentTimeMillis() - startTime;
//
//        log("program completed");
//        log("spent time, sec: " + String.valueOf((float)estimatedTime/1000));
//
//
//    }
//
//
//    private List<Point> ProcessPoints(List<Point> extents, List<Point> points) {
//
//        List<Point> resultList = new ArrayList<>();
//        int k = 0;
//        Point pe;
//
//        for (Point p: points) {
//
//            if (p.processed) continue;
//
//            if (_prevExtent == null) {
//                _prevExtent = extents.get(0);
//                k = 1;
//            }
//
//            for (int j = k; j < extents.size(); j++) {
//
//                pe = extents.get(j);
//
//                if (p.value >= _prevExtent.value &&  p.value <= pe.value) {
//                    resultList.add(new Point(p.value, p.position, _prevExtent.count));
//                    p.processed = true;
//                    k = j;
//                    break;
//                }
//
//                _prevExtent = pe;
//            }
//        }
//
//        // Sort array by initial position
//        Collections.sort(resultList, new Comparator<Point>() {
//            @Override
//            public int compare(Point o1, Point o2) {
//                return Integer.compare(o1.position, o2.position);
//            }
//        });
//
//        return resultList;
//    }
//
//
//    // Read extents array and modify them to array of points
//    // Then sort those points in ascending order
//    // And then for each point count inside how many extents it is
//
//    private void ReadAndSortExtents(String fileExtents, int count) throws IOException
//    {
//        int rows;
//
//        try(BufferedReader reader = new BufferedReader(new FileReader(fileExtents))) {
//
//            for(int i = 0; i< FILES_COUNT; i++){
//
//                rows = readExtentsFromFile(reader, count);
//
//                if (rows > 0) {
//
//                    try (BufferedWriter bw1 = new BufferedWriter(new FileWriter(String.format(fileExtentsTmpA, i)));
//                         BufferedWriter bw2 = new BufferedWriter(new FileWriter(String.format(fileExtentsTmpB, i)))) {
//
//                        for (long l : _extentsA2) {
//                            bw1.write(String.valueOf(l) + "\n");
//                        }
//
//                        for (long l : _extentsB2) {
//                            bw2.write(String.valueOf(l) + "\n");
//                        }
//
//                        // check if it was last file
//                        if (rows < count)
//                            break;
//                    }
//                }
//            }
//        }
//    }
//
//    public int readExtentsFromFile(BufferedReader reader, int count)
//    {
//        _extentsA2 = new long[count];
//        _extentsB2 = new long[count];
//
//        String line;
//        String s[];
//
//        try {
//
//            int i = 0;
//            while (i < count && (line = reader.readLine()) != null) {
//
//                s = line.split(" ");
//                if (s.length != 2) continue;
//
//                _extentsA2[i]=Long.valueOf(s[0]);
//                _extentsB2[i]=Long.valueOf(s[1]);
//
//                i++;
//            }
//
//            Arrays.sort(_extentsA2);
//            Arrays.sort(_extentsB2);
//
//            return i;
//        }
//        catch (Exception e)
//        {
//            logError(e.getMessage());
//            return 0;
//        }
//
//    }
//
//    // read specific amount of lines from file
//    public List<Point> ReadPointsFromFile(BufferedReader reader, int count, boolean doExtents, int k) throws IOException
//    {
//        List<Point> points = new ArrayList<>();
//        String line;
//        String[] s;
//        int i = 0;
//
//        while (i < count && (line = reader.readLine()) != null) {
//
//            if (line.trim().isEmpty()) continue;
//
//            if(doExtents) {
//                s=line.split(" ");
//                if (s.length != 2) continue;
//                points.add(new Point(Long.valueOf(s[0]),k*count + i,Integer.valueOf(s[1])));
//            }
//            else
//                points.add(new Point(line, k*count + i));
//
//            i++;
//        }
//
//        if(!doExtents) {
//            Collections.sort(points, new Comparator<Point>() {
//                @Override
//                public int compare(Point o1, Point o2) {
//                    return Long.compare(o1.value, o2.value);
//                }
//            });
//        }
//
//        return points;
//    }
//
//    private void log(String s){
//        System.out.println(s);
//    }
//
//    //
//    // This class is specialised for sorted merge into one single file of all points from extents.txt
//    // Also it calculates number of extents for each point
//    //
//    private class FileProcessor {
//        private int numFiles = 100;
//        private BufferedReader[] readersA,readersB;
//        private int[] indexesA,prevIndexesA;
//        private int[] indexesB, prevIndexesB;
//        private long[] currArrayA;
//        private long[] currArrayB;
//        private int resCount=0;
//        private int prevCount;
//        private long resA;
//        private long resAPrev;
//        private long resB;
//        private String resFile;
//
//        public FileProcessor(String file1, String file2, String fileRes) throws FileNotFoundException
//        {
//            List<String> filesA = new ArrayList<>();
//            List<String> filesB = new ArrayList<>();
//            String fileA,fileB;
//            resFile = fileRes;
//            for (int i = 0; i < numFiles; i++) {
//                fileA = String.format(file1,i);
//                fileB = String.format(file2,i);
//                if (new File(fileA).exists() && new File(fileB).exists()) {
//                    filesA.add(fileA);
//                    filesB.add(fileB);
//                }
//                else
//                {
//                    numFiles = i;
//                    break;
//                }
//            }
//            currArrayA = new long[numFiles];
//            currArrayB = new long[numFiles];
//            indexesA = new int[numFiles];
//            indexesB = new int[numFiles];
//            prevIndexesA = new int[numFiles];
//            prevIndexesB = new int[numFiles];
//            readersA = new BufferedReader[numFiles];
//            readersB = new BufferedReader[numFiles];
//
//            for (int i = 0; i <numFiles; i++) {
//                readersA[i] = new BufferedReader(new FileReader(filesA.get(i)));
//                readersB[i] = new BufferedReader(new FileReader(filesB.get(i)));
//                prevIndexesA[i]=-1;
//                prevIndexesB[i]=-1;
//            }
//        }
//
//        private long[] getFirstLine(BufferedReader[] readers, int[] indexes, int[] prevIndexes, long[] array) throws  IOException
//        {
//            String[] line = new String[numFiles];
//
//            for (int i = 0; i < numFiles ; i++) {
//
//                if (indexes[i] > prevIndexes[i]) {
//                    line[i] = readers[i].readLine();
//
//                    if (line[i] != null && !line[i].trim().isEmpty()) {
//                        array[i] = Long.valueOf(line[i]);
//                    }
//                    else
//                        array[i] = Long.MAX_VALUE;
//
//                    prevIndexes[i]=indexes[i];
//                }
//            }
//            return array;
//        }
//
//        private long[] getMinValue(long[] array)
//        {
//            long[] result = new long[2];
//
//            long min = array[0];
//            result[0]=0;
//            for (int i = 1; i < array.length; i++) {
//                if (array[i] < min) {min=array[i];result[0]=i;}
//            }
//            result[1]=min;
//            return result;
//        }
//
//        public void MergeFiles() throws IOException
//        {
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(resFile))){
//
//                currArrayB = getFirstLine(readersB, indexesB, prevIndexesB, currArrayB);
//                long[] res = getMinValue(currArrayB);
//                resB = res[1];
//
//                for (int i = 0; i < numFiles; i++) {
//                    indexesB[i]=-1;
//                    prevIndexesB[i]=-1;
//                }
//
//                while(true) {
//
//                    currArrayA = getFirstLine(readersA, indexesA, prevIndexesA, currArrayA);
//
//                    res = getMinValue(currArrayA);
//                    if (res[1] == Long.MAX_VALUE) {
//                        writeValue(bw, resA, resCount);
//                        break;
//                    }
//
//                    if (resA != res[1]) {
//
//                        if (resA !=-1) {
//                            writeValue(bw,resA, resCount);
//                            resCount = 1;
//                        }
//                        resA = res[1];
//                    } else
//                        resCount++;
//
//                    indexesA[(int) res[0]]++;
//
//                }
//
//                boolean foundCount=true;
//
//                while (foundCount) {
//                    foundCount = calcCountB(bw,0);
//                }
//
//            }
//        }
//
//        private void writeValue(BufferedWriter bw, long value, int count) throws IOException{
//            int resCount=0;
//            boolean foundCount=false;
//
//            if(resB >= resAPrev && resB <= value) {
//
//                while (resB >= resAPrev && resB <= value) {
//                    while (!foundCount) {
//                        foundCount = calcCountB(bw,count);
//                    }
//                }
//            }
//            else {
//                bw.write(value + " " + (count + prevCount - resCount) + "\n");
//                prevCount += count;
//                resAPrev = value;
//            }
//        }
//
//        private boolean calcCountB(BufferedWriter bw,int count) throws IOException{
//
//            int resCount = 0;
//            while(true) {
//
//                currArrayB = getFirstLine(readersB, indexesB, prevIndexesB, currArrayB);
//
//                long[] res = getMinValue(currArrayB);
//
//                if (res[1] == Long.MAX_VALUE) {
//                    bw.write(resB + " " + (count + prevCount - resCount) + "\n");
//                    return false;
//                }
//
//                if (resB != res[1]) {
//
//                    if (resB != -1) {
//                        bw.write(resB + " " + (count + prevCount - resCount) + "\n");
//                        prevCount += (count - resCount);
//                        resAPrev = resB;
//                        resB = res[1];
//                        return true;
//                    }
//                } else{
//                    resCount++;
//                    indexesB[(int) res[0]]++;
//                }
//            }
//        }
//    }
//}
//
