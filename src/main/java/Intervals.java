/**
 * Created by Yegor on 6/28/2015.
 */
public class Intervals {
    public static void main(String[] args) {
        log("Intervals started");
        long x = Multiply(100,22);
        log("multiply: " + String.valueOf(x));
        log("Intervals completed");
    }

    private static void log(String s){
        System.out.println(s);
    }

    private static long Multiply(int a, int b){return a*b;}

}
