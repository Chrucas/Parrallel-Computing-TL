abstract class Message {
    Message() {
    }
}

class RequestMessage extends Message {
    private final int col;
    private final int row;

    public RequestMessage(int row, int col) {
        super();
        this.col=col;
        this.row=row;
    }
    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }
}
class ReplyMessage extends Message {
    private final Boolean ok;

    public ReplyMessage(Boolean ok) {
        super();
        this.ok=ok;
    }

    public Boolean isOK() {
        return ok;
    }
}
class UpdateMessage extends Message {
    private final int[][] board;

    public UpdateMessage(int[][] board) {
        super();
        this.board=board;
    }

    public int[][] getBoard() {
        return board;
    }
}
class StopMessage extends Message {
    public StopMessage() {
        super();
    }
}