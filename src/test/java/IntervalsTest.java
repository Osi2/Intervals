import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class IntervalsTest {


    @Test
    public void TestReadNumbersFromFile() {

        String fileExtents = System.getProperty("FileExtents");
        String filePoints = System.getProperty("FilePoints");
        String fileExpected = System.getProperty("FileResult");
        String fileResult = System.getProperty("FileResult");

        Intervals intervals = new Intervals();
        intervals.Run(fileExtents,filePoints,fileResult);


        String[] listResult = Intervals.ReadNumbersFromFile(fileResult);
        String[] listExpected = Intervals.ReadNumbersFromFile(fileExpected);
        assertArrayEquals(listResult,listExpected);

    }

}