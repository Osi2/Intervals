/**
 * Created by Yegor2 on 6/30/2015.
 */
public class Point {

    public char Type;
    public int Value;
    public long Count;
    public int Position;

    public Point(String value, int position){this.Value = Integer.valueOf(value); this.Position = position;}
    public Point(int value, int position, long count){this.Value = value; this.Position = position; this.Count = count;}
    public Point(char c, String value){this.Type = c; this.Value = Integer.valueOf(value);}

}
