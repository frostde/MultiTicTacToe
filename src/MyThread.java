import java.net.Socket;


/*This class inherits from the Thread class, and performs the process of creating a Board (game), passing the socket to it
* and calling the playGame method that handles the rest.  The two methods included are:
*
* . A Constructor that takes a socket as a parameter
* . A method overload of run() that creates the board instance and calls playGame() on it to start the game.*/
public class MyThread extends Thread {
        private Socket socket;


        public MyThread(Socket c) {
            socket = c;
        }


        public void run() {
            Board b = new Board(socket);
            b.playGame();
        }






}
