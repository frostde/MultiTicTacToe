/*The purpose of the Point class is to establish some logic that help in debugging,
* and represent the possible location on the board.  It contains two member variables x and y
* X and Y simply represent the coordinates of a point on the board.  There are nine possible
* points on a board.*/
public class Point {
    int x, y;

    /*The constructor must takes both coordinates.*/
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*toString() is overriden so that we can aid in debugging, and also if we wanted to print a point
    * to the console at some point*/
    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

}
