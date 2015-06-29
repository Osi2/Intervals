/**
 * Created by Yegor on 6/28/2015.
 */
public class Intervals {
    public static void main(String[] args) {
        log("Intervals started");
        double x = Multiply(100.0,22.0);
        log("multiply: " + String.valueOf(x));
        log("Intervals completed");
    }

    private static void log(String s){
        System.out.println(s);
    }

    public static double Multiply(double a, double b){return a*b;}

}
