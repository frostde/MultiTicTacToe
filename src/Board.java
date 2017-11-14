import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*Didnt have time to comment this class before the deadline, but the methods are pretty self explanatory.  They
* include all of the methods for the AI and the actual game logic.*/
public class Board {
    List<Point> availablePoints;
    int[][] board = new int[3][3];
    List<PointsAndScores> rootsChildrenScores;
    private int lastColumn, lastRow;
    private BufferedReader reader;
    private PrintWriter writer;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    int numMoves = 0;
    private Socket sock;
    private Random rand = new Random();

    public Board(Socket c) {
        this.sock = c;
    }


    public boolean hasXWon() {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 1) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 1)) {
            //System.out.println("X Diagonal Win");
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if (((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 1)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 1))) {
                // System.out.println("X Row or Column win");
                return true;
            }
        }
        return false;
    }

    public boolean hasOWon() {
        if ((board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == 2) || (board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == 2)) {
            // System.out.println("O Diagonal Win");
            return true;
        }
        for (int i = 0; i < 3; ++i) {
            if ((board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == 2)
                    || (board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == 2)) {
                //  System.out.println("O Row or Column win");
                return true;
            }
        }

        return false;
    }

    public List<Point> getAvailableStates() {
        availablePoints = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == 0) {
                    availablePoints.add(new Point(i, j));
                }
            }
        }
        return availablePoints;
    }

    public void placeAMove(Point point, int player) {
        board[point.x][point.y] = player;   //player = 1 for X, 2 for O
    }

    public Point returnBestMove() {
        int MAX = -100000;
        int best = -1;

        for (int i = 0; i < rootsChildrenScores.size(); ++i) {
            if (MAX < rootsChildrenScores.get(i).score) {
                MAX = rootsChildrenScores.get(i).score;
                best = i;
            }
        }
        return rootsChildrenScores.get(best).point;
    }


    public int returnMin(List<Integer> list) {
        int min = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) < min) {
                min = list.get(i);
                index = i;
            }
        }
        return list.get(index);
    }

    public int returnMax(List<Integer> list) {
        int max = Integer.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) > max) {
                max = list.get(i);
                index = i;
            }
        }
        return list.get(index);
    }

    public void callMinimax(int depth, int turn){
        rootsChildrenScores = new ArrayList<>();
        minimax(depth, turn);
    }

    public int minimax(int depth, int turn) {

        if (hasXWon()) return +1;
        if (hasOWon()) return -1;

        List<Point> pointsAvailable = getAvailableStates();
        if (pointsAvailable.isEmpty()) return 0;

        List<Integer> scores = new ArrayList<>();

        for (int i = 0; i < pointsAvailable.size(); ++i) {
            Point point = pointsAvailable.get(i);

            if (turn == 1) { //X's turn select the highest from below minimax() call
                placeAMove(point, 1);
                int currentScore = minimax(depth + 1, 2);
                scores.add(currentScore);

                if (depth == 0)
                    rootsChildrenScores.add(new PointsAndScores(currentScore, point));

            } else if (turn == 2) {//O's turn select the lowest from below minimax() call
                placeAMove(point, 2);
                scores.add(minimax(depth + 1, 1));
            }
            board[point.x][point.y] = 0; //Reset this point
        }
        return turn == 1 ? returnMax(scores) : returnMin(scores);
    }


    public void playGame() {
        Random rand = new Random();
        try {

            inStream = new DataInputStream(sock.getInputStream());
            outStream = new DataOutputStream(sock.getOutputStream());

            writer = new PrintWriter(outStream, true);
            reader = new BufferedReader(new InputStreamReader(inStream));

        } catch (IOException ex) {}

        try {
            //generate turn order, if 1; computer, if 2; player
            int turn = rand.nextInt(2) + 1;
            System.out.println("Wins: " + ScoreBoard.wins + "  Ties: " + ScoreBoard.ties + "  Losses: " + ScoreBoard.losses);

            writer.println(ScoreBoard.wins + " " + ScoreBoard.ties + " " + ScoreBoard.losses);

            while (true) {
                switch(turn) {
                    case 1: {
                        getComputerMove();
                        if (hasXWon()) {
                            writer.println("MOVE" + " #" + lastRow + " #" + lastColumn  + " LOSS");
                            ScoreBoard.incrementLosses();
                            break;
                        }
                        if (numMoves == 9) {
                            writer.println("MOVE" + " #" + lastRow + " #" + lastColumn + " TIE");
                            ScoreBoard.incrementTies();
                            break;
                        }
                        writer.println("MOVE" + " #" + lastRow + " #" + lastColumn);
                        numMoves++;
                        turn = 2;
                        break;
                    }
                    case 2: {
                        if (numMoves == 0) {
                            writer.println("NONE");
                            getPlayerMove();
                            numMoves++;
                            turn = 1;
                            break;
                        } else {
                            getPlayerMove();
                            numMoves++;
                            turn = 1;
                            if (hasOWon()) {
                                writer.println("MOVE 0 0 WIN");
                                ScoreBoard.incrementWins();
                                break;
                            }
                        }
                        if (numMoves == 9) {
                            writer.println("MOVE 0 0 TIE");
                            ScoreBoard.incrementTies();
                            break;
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {}

    }

    public void getComputerMove() {
        if (numMoves == 0) {
            Point p = new Point(rand.nextInt(3), rand.nextInt(3));
            placeAMove(p, 1);
            lastRow = p.x;
            lastColumn = p.y;
        } else {
            callMinimax(0, 1);
            Point computerMove = returnBestMove();
            lastRow = computerMove.x;
            lastColumn = computerMove.y;
            placeAMove(computerMove, 1);
        }

    }

    public void getPlayerMove() {
        String line;
        try {
            if ((line = reader.readLine()) != null) {
                String[] playerMoveArray = line.split(" ");

                String[] playerRowArray = playerMoveArray[1].split("#");
                String[] playerColumnArray = playerMoveArray[2].split("#");

                board[Integer.parseInt(playerRowArray[1])][Integer.parseInt(playerColumnArray[1])] = 'O';
                lastRow = Integer.parseInt(playerRowArray[1]);
                lastColumn = Integer.parseInt(playerColumnArray[1]);
            }
        } catch (IOException e) {}

    }

}
