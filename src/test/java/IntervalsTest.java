import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;


public class IntervalsTest {


    @Test
    public void TestReadNumbersFromFile() {

        System.out.println("Intervals test started");

        String fileExtents = System.getProperty("FileExtents");
        String filePoints = System.getProperty("FilePoints");
        String fileExpected = System.getProperty("FileResult");
        String fileResult = System.getProperty("FileResult");

        Extents extents = new Extents();
        extents.Run(fileExtents, filePoints, fileResult);


        try {

        List<String> listResult = Files.readAllLines(Paths.get(fileResult), Charset.defaultCharset());
        List<String> listExpected = Files.readAllLines(Paths.get(fileExpected),Charset.defaultCharset());

        assertArrayEquals(listResult.toArray(), listExpected.toArray());

        }
        catch (Exception e)
        {

        }


    }

}