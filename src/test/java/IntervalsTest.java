import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IntervalsTest {

    @Test
    public void TestMultiply()
    {
        double a = 5.5;
        double b = 4.0;
        double expected = 22.0;
        double result = Intervals.Multiply(a,b);
        assertEquals(expected,result,0.0000001);
    }

    @Test
    public void TestReadNumbersFromFile() {
        String[] listResult = Intervals.ReadNumbersFromFile("D:/_Projects/Intervals/data/result.txt");
        String[] listExpected = Intervals.ReadNumbersFromFile("D:/_Projects/Intervals/data/expected.txt");
        assertArrayEquals(listResult,listExpected);


    }

}