import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
* Daniel Frost
* Operating Systems
* Dr. Newell
* Spring 2017
*
* This program was assigned by Dr. Newell in Operating Systems at the Northern Kentucky University
* The purpose of this program is to implement a client/server, multi-threaded Tic Tac Toe game.
* The Driver class has no methods outside of main.  It's purpose is to initiate the server and to wait for client requests.
* When a client request is received, the server spins off a new thread, and handles the game from start to finish.
*
* Four other classes are present that represent the Overloaded thread class, A point class to aid in AI, a Board class
* that represents all of the actual logic of the game, and ScoreBoard which holds the w*/
public class Driver {


    public static void main(String[] args) throws IOException{

        ServerSocket server = new ServerSocket(7788);
        Socket toClientSocket;

        /*This infinite loop allows the server to repeatedly wait for new client connections, and to pass these to new threads.*/
        while (true) {
            toClientSocket = null;
            System.out.println("Waiting for next connection.");
            toClientSocket = server.accept();
            MyThread newThread = new MyThread(toClientSocket);
            Thread t = new Thread(newThread);
            t.start();
            System.out.println("Connected");
        }
    }
}

