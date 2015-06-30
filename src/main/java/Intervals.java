import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by Yegor on 6/28/2015.
 */
public class Intervals {
    public static void main(String[] args) {
        log("Intervals started");
        double x = Multiply(200.0,22.0);
        log("multiply: " + String.valueOf(x));
        log("Intervals completed");
    }

    private static void log(String s){
        System.out.println(s);
    }

    public static double Multiply(double a, double b){return a*b;}

    public static String[] ReadNumbersFromFile(String file)
    {
        BufferedReader reader = null;
        String[] list = new String[1000000];
        try {

            String line = null;
            reader = new BufferedReader(new FileReader(file));
            int i = 0;

            while ((line = reader.readLine()) !=null)
            {
                list[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                return list;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
