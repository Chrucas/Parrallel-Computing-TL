import org.jetbrains.annotations.Contract;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class ObserverThread extends Thread{

    private int[][] board;
    private final String direction;

    final BlockingQueue<Message> inQ = new LinkedBlockingQueue<>();
    final BlockingQueue<Message> outQ;


    ObserverThread(int[][] board, String direction, BlockingQueue<Message> q) {
        this.direction=direction;
        this.board=board;
        this.outQ = q;
    }

    @Contract(pure = true)
    private synchronized boolean isSafe(int row, int col){
            //checks \ <-this way if the given coordinate is safe
            int i,j;
            if(direction.equals("LD")){
                //Check Left Upper Diagonal
                for (i = row, j = col; i >= 0 && j >= 0; i--, j--) {
                    if(board[i][j] == 1){
                        return false;
                    }
                }
                //Check Right Downward Diagonal
                for (i = row+1, j = col+1; i < board.length && j < board.length; i++, j++) {

                    if(board[i][j] == 1){
                        return false;
                    }
                }
            }
            //checks / <-this way if the given coordinate is safe
            if(direction.equals("RD")){
                //Check Right Upper Diagonal
                for (i = row, j = col; i >= 0 && j < board.length; i--, j++) {
                    if(board[i][j] == 1){
                        return false;
                    }
                }
                //Check Left Downward Diagonal
                for (i = row+1, j = col-1; i < board.length && j >= 0; i++, j--) {
                    if(board[i][j] == 1){
                        return false;
                    }
                }
            }
            //checks | <-this way if the given coordinate is safe
            if(direction.equals("V")){
                //Check in same Column
                for (i = 0; i < board.length; i++) {
                    if(board[i][col] == 1){
                        return false;
                    }
                }
            }
            //checks _ <-this way if the given coordinate is safe
            if(direction.equals("H")){
                //Check in same Column
                for (i = 0; i < board.length; i++) {
                    if(board[row][i] == 1){
                        return false;
                    }
                }
            }
        return true;
    }


    private synchronized void setBoard(int[][] board) {
        this.board = board;
    }

    @Override
    public void run() {
        while(true){
            Message m;
            while((m = inQ.poll()) != null) {
                if(m instanceof UpdateMessage){
                    setBoard(((UpdateMessage)(m)).getBoard());
                }
                if(m instanceof RequestMessage){
                    Boolean b = this.isSafe(((RequestMessage)(m)).getRow(), ((RequestMessage)(m)).getCol());
                    Message rpl = new ReplyMessage(b);
                    this.outQ.add(rpl);
                }
                if(m instanceof StopMessage){
                        return;
                }
            }
        }
    }

    //This method is used for testing purposes
//    private static void printBoard(int[][] board){
//        long startTime = System.currentTimeMillis();
//        for (int[] ints : board) {
//            for (int col = 0; col < board.length; col++) {
//                if (ints[col] == 1) {
//                    System.out.print("Q ");
//                } else {
//                    System.out.print("_ ");
//                }
//            }
//            System.out.println();
//        }
//        System.out.println();
//        long endTime = System.currentTimeMillis();
//
//        long duration = (endTime - startTime);
//        System.out.println("Time to print:" + duration);
//    }
}
