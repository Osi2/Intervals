import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IntervalsTest {


    @Test
    public void TestReadNumbersFromFile() {
        String[] listResult = Intervals.ReadNumbersFromFile("D:/_Projects/Intervals/data/result.txt");
        String[] listExpected = Intervals.ReadNumbersFromFile("D:/_Projects/Intervals/data/expected.txt");
        assertArrayEquals(listResult,listExpected);

    }

}