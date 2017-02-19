/**
 * Created by Yegor on 10/17/2016.
 * Some Changes made by me
 */
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class tester {

    public static void main(String[] args) {

        System.out.println("started tester");

        String fileExtents = "D:\\_Projects\\Intervals\\data\\extents.txt";
        String filePoints = "D:\\_Projects\\Intervals\\data\\numbers.txt";
        String fileExpected = "D:\\_Projects\\Intervals\\data\\expected.txt";
        String fileResult = "D:\\_Projects\\Intervals\\data\\result.txt";

        Intervals intervals = new Intervals();
        intervals.Run(fileExtents,filePoints,fileResult);


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
}
