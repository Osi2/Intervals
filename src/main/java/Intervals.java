import java.io.*;
import java.util.*;

/**
 * Created by Yegor on 6/28/2015.
 */
public class Intervals {
    public static void main(String[] args) {

        String fileExtents = args[0];
        String filePoints = args[1];
        String resultFile = args[2];

        Intervals intervals = new Intervals();
        intervals.Run(fileExtents,filePoints,resultFile);
    }

    public void Run(String fileExtents, String filePoints, String fileResult)
    {

        log("Intervals started");

        log("fileExtents: " + fileExtents);
        log("filePoints: " + filePoints);
        log("fileResult: " + fileResult);

        File f = new File(fileResult);
        if (f.exists()) f.delete();

        long startTime = System.currentTimeMillis();

        List<Point> list = ReadExtents(fileExtents);
        String[] PointsArray = ReadNumbersFromFile(filePoints);
        List<Point> PointsList = ConvertArrayToList(PointsArray);

        ProcessPoints(list, PointsList, fileResult);

        long estimatedTime = System.currentTimeMillis() - startTime;

        log("Intervals completed");
        log("spent time, sec: " + String.valueOf((float)estimatedTime/1000));


    }

    private static List<Point> ConvertArrayToList(String[] pointsArray) {

        List<Point> points = new ArrayList<Point>();

        for (int i = 0; i < pointsArray.length; i++) {
            points.add(new Point(Integer.valueOf(pointsArray[i]),i));
        }

        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Integer.compare(o1.Value,o2.Value);
            }
        });

        return points;
    }

    private static void ProcessPoints(List<Point> Extentslist, List<Point> PointsList, String outFile) {

        List<Point> finalList = new ArrayList<Point>();

        try (FileWriter fw = new FileWriter(outFile)){

            int k = 0;
            for (int i = 0; i < PointsList.size(); i++) {


                long PrevCount = 0;

                for (int j = k; j < Extentslist.size(); j++) {

                    if (Integer.valueOf(PointsList.get(i).Value) > Extentslist.get(j).Value) {
                        PrevCount = Extentslist.get(j).Count;
                    }
                    else {
                        finalList.add(new Point(PointsList.get(i).Value, PointsList.get(i).Position, Extentslist.get(j).Count + 1));
                        k = j;
                        break;
                    }
                }
            }

            Collections.sort(finalList, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return Integer.compare(o1.Position, o2.Position);
                }
            });

            for (int i = 0; i < finalList.size(); i++) {
                fw.write(String.valueOf(finalList.get(i).Count) + "\n");
            }
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void log(String s){
        System.out.println(s);
    }

    private static List<Point> ReadExtents(String fileExtents)
    {
        String[] Extentslist = ReadNumbersFromFile(fileExtents);

        List<Point> list = Collections.synchronizedList(new ArrayList<Point>());


        for (int i = 0; i < Extentslist.length; i++) {
            if (Extentslist[i] == null ||  Extentslist[i].length() == 0) break;
            String[] s = Extentslist[i].split(" ");
            int a = Integer.valueOf(s[0]);
            int b = Integer.valueOf(s[1]);
            list.add(new Point('A',a) );
            list.add(new Point('B',b));
        }

        Collections.sort(list, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Integer.compare(o1.Value,o2.Value);
            }
        });
        long counter = 0;
        List<Point> syncList = new ArrayList<Point>();

        synchronized (list){
            Iterator<Point> iterator = list.iterator();
            while(iterator.hasNext()){

                Point p = iterator.next();
                char type = p.Type;
                int value = p.Value;

                //log(String.valueOf(value));
                //log(String.valueOf(type));


                if (type == 'A') ++counter;
                if (type == 'B') --counter;

                //log(" counter: " + String.valueOf(counter));
                //log("\n");
                p.Count = counter;

                syncList.add(p);
            }
        }

        return syncList;

    }


    public static String[] ReadNumbersFromFile(String file)
    {
        String[] list = new String[1000000];

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){

            String line = null;
            int i = 0;

            while ((line = reader.readLine()) !=null)
            {
                list[i] = line;
                i++;
            }

            String[] listResult = new String[i];
            System.arraycopy(list,0,listResult,0,i);
            return  listResult;

        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

}
