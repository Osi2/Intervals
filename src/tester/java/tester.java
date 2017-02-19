/**
 * Created by Yegor on 10/17/2016.
 * Some Changes made by me
 * New changes
 */
import java.lang.Integer;
import java.lang.Long;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TreeMap;
import java.util.Random;

public class tester {

    public static void main(String[] args) {

        System.out.println("started tester");
//        testList();
//        testMap();

//        try {
//            Processor processor = new Processor();
//            processor.Run();
//        }
//        catch (Exception e){
//            System.out.println(e.toString());
//        }

        String fileExtents = "D:\\_Projects\\Intervals\\data\\extents.txt";
        String filePoints = "D:\\_Projects\\Intervals\\data\\numbers.txt";
        String fileExpected = "D:\\_Projects\\Intervals\\data\\expected.txt";
        String fileResult = "D:\\_Projects\\Intervals\\data\\result.txt";

        Extents extents = new Extents();
        extents.Run(fileExtents,filePoints,fileResult);


        try {

            List<String> listResult = Files.readAllLines(Paths.get(fileResult), Charset.defaultCharset());
            List<String> listExpected = Files.readAllLines(Paths.get(fileExpected), Charset.defaultCharset());

            Boolean areEquals = Arrays.equals(listResult.toArray(), listExpected.toArray());

            System.out.println("arrays are equal: " + areEquals.toString());



            System.exit(0);
        }
        catch (Exception e)
        {

        }

    }

    private static void testList()
    {
        List<Long> list = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < 1000000; i++){
            list.add(random.nextLong());
        }

        Collections.sort(list);

    }

    private static void testMap()
    {
        TreeMap<Long,Integer> m = new TreeMap<>();
        Random random = new Random();

        for(int i = 0; i < 1000000; i++){
            m.put(random.nextLong(),random.nextInt());

        }

    }
}
