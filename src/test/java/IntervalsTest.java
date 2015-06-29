import org.junit.Test;

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

}