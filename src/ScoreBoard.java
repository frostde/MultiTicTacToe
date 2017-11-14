import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/*This class simply holds the values for the ScoreBoard including wins, losses, and ties.
* These are the system statistics that can be read from the server console as well as
* when you begin a game on the client-side.  These variables are private so they can only be
  * accessed through methods.  The four methods contained are:
*
* . incrementWins()
* . incrementLosses(), and
* . incrementTies()
*
* These methods are synchronized, in that only one thread can access this class (the scoreboard)
* at a time.  This helps us prevent a race condition with the incrementation of the various game results.*/
public class ScoreBoard {
    public static int wins = 0;
    public static int losses = 0;
    public static int ties = 0;

    public static synchronized void incrementWins() {
        wins++;
    }

    public static synchronized void incrementLosses() {
        losses++;
    }

    public static synchronized void incrementTies() {
        ties++;
    }



}
