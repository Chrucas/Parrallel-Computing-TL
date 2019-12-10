import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

class NQP {

    private static final int QUEENS = 32;//never <4 !
    private static final int[][] board = new int[QUEENS][QUEENS];
    private static final int threadNumber = 4;


    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        //set up empty board
        setupBoard(board);
        //set up threads
        String[] dirs = new String[4];
        dirs[0]="H";dirs[1]="V";dirs[2]="RD";dirs[3]="LD";
        ArrayList<ObserverThread> threads = new ArrayList<>();
        ArrayList<BlockingQueue<Message>> outQs = new ArrayList<>();
        ArrayList<point> successes= new ArrayList<>();
        for (int i=0; i<threadNumber; i++) {
            outQs.add(new LinkedBlockingQueue<>());
            threads.add(new ObserverThread(board,dirs[i%4], outQs.get(i)));
            threads.get(i).start();
        }
        Random r = new Random();
        int placedQueens = 0;
        int tries=0;
//        int col = 0;
        int row = 0;
        search:
        while(placedQueens<QUEENS) {
            int col = r.nextInt(QUEENS);
//            int row = r.nextInt(QUEENS);
            for (int i=0; i<threadNumber; i++) {
                threads.get(i).inQ.add(new RequestMessage(row, col));
            }
            Boolean allOK = true;
            for (int i=0; i<threadNumber; i++) {
                ReplyMessage m = (ReplyMessage)threads.get(i).outQ.take();
                allOK = allOK && m.isOK();
            }
            if (!allOK) {
                if (tries++>32) {
                    int x = successes.size()-1;
                    row = successes.get(x).getRow();
                    col = successes.get(x).getCol();
                    successes.remove(x);
                    placedQueens=successes.size();
                    reset(row, col, board);
                    UpdateMessage update = new UpdateMessage(board);
                    for (int i=0; i<4; i++) {
                        threads.get(i).inQ.add(update);
                    }
                }
                //noinspection UnnecessaryLabelOnContinueStatement
                continue search;
            }
            set(row, col, board);
            successes.add(new point(row, col));
            tries = 0;
            row++;
            UpdateMessage update = new UpdateMessage(board);
            for (int i=0; i<threadNumber; i++) {
                threads.get(i).inQ.add(update);
            }
            placedQueens=successes.size();
            System.out.println("Queen's placed:" + placedQueens);
        }
        StopMessage stop = new StopMessage();
        for (int i=0; i<threadNumber; i++) {
            threads.get(i).inQ.add(stop);
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("Time to place queens: " + duration +" milliseconds");
        printBoard(board);
    }


    private static void setupBoard(int[][] board){
        for(int[] row : board){
            Arrays.fill(row, 0);
        }
    }

    private static void set(int row, int col, int[][] board){
        System.out.println("setting: row["+row+"] col["+col+"] to 1");
        board[row][col] = 1;
    }

    private static void reset(int row, int col, int[][] board){
        System.out.println("setting: row["+row+"] col["+col+"] to 0");
        board[row][col] = 0;
    }

    private static void printBoard(int[][] board){
        for (int[] ints : board) {
            for (int col = 0; col < board.length; col++) {
                if (ints[col] == 1) {
                    System.out.print("Q ");
                } else {
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
class point{
    private final int row;
    private final int col;

    public point(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
}
