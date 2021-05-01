package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import Tests.WinDraw;
import Game.Constants;
import Game.LogicBoard;

public class King extends Piece {

    public WinDraw check;
    private final boolean isWhite;
    private ImageIcon whitePiece;
    private ImageIcon blackPiece;
    private static final int VALUE = 90;
    private int currentCoor;
    private int firstCoor;
    private boolean leftCastling;
    private boolean rightCastling;
    private int leftRookCoordinate;
    private int rightRookCoordinate;
    private ArrayList<Integer> possibilities;
    private static final String NOTATION = "K";
    private int leftCastlingCoordinate;
    private int rightCastlingCoordinate;

    public King(boolean isWhite, LogicBoard lb) {
        this.check = new WinDraw(lb);
        this.possibilities = new ArrayList<>();
        this.whitePiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_klt60.png"));
        this.blackPiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_kdt60.png"));
        this.isWhite = isWhite;
        this.leftRookCoordinate = -1;
        this.rightRookCoordinate = -1;
        this.leftCastling = true;
        this.rightCastling = true;
    }

    @Override
    public String getNotation() {
        return NOTATION;
    }

    /**
     * setting rook coordinate if first coordinate wasn't set it sets the
     * leftRookCoordinate, otherwise it sets rightRookCoordinate
     *
     * @param row row of rook
     * @param col col of rook
     */
    public void setRookCoordinates(int row, int col) {
        if (leftRookCoordinate == -1) {
            this.leftRookCoordinate = row * Constants.BOARD_SIZE + col;
        } else {
            this.rightRookCoordinate = row * Constants.BOARD_SIZE + col;
        }
    }

    public void resetRookCoordinates() {
        leftRookCoordinate = -1;
        rightRookCoordinate = -1;
    }

    /**
     * this method is called only once it sets where the king is now and where
     * the king can castle
     *
     * @param row row of king
     * @param col row of king
     */
    public void setFirstCoordinates(int row, int col) {
        firstCoor = row * Constants.BOARD_SIZE + col;
        setCoordinates(row, col);
        this.leftCastlingCoordinate = getCurrentRow() * Constants.BOARD_SIZE + 2;
        this.rightCastlingCoordinate = getCurrentRow() * Constants.BOARD_SIZE + 6;
    }

    /**
     * changes coordinate of king based on row and col values. This method when
     * whiteMoves is changed
     *
     * @param row real row of the king
     * @param col real col of the king
     */
    public void setCoordinates(int row, int col) {
        currentCoor = row * Constants.BOARD_SIZE + col;
        if (currentCoor != firstCoor) {
            leftCastling = false;
            rightCastling = false;
        }
    }

    /**
     *
     * @return coordinate where king moves when doing left Castling
     */
    public int getLCC() {
        return leftCastlingCoordinate;
    }

    /**
     *
     * @return coordinate where king moves when doing right Castling
     */
    public int getRCC() {
        return rightCastlingCoordinate;
    }

    /**
     *
     * @return coordinate of left rook
     */
    public int getLRC() {
        return leftRookCoordinate;
    }

    /**
     *
     * @return coordinate of right rook
     */
    public int getRRC() {
        return rightRookCoordinate;
    }

    /**
     *
     * @return row where is King now
     */
    public int getCurrentRow() {
        return currentCoor / Constants.BOARD_SIZE;
    }

    /**
     *
     * @return col where is King now
     */
    public int getCurrentCol() {
        return currentCoor % Constants.BOARD_SIZE;
    }

    @Override
    public ImageIcon getPiece() {
        if (isWhite() == true) {
            return whitePiece;
        } else {
            return blackPiece;
        }
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public int getVALUE() {
        return VALUE;
    }

    @Override
    public int getRealValue() {
        return VALUE;
    }

    /**
     *
     * @return true if King is able to do left Castling
     */
    public boolean canLeftCastling() {
        return leftCastling;
    }

    /**
     * sets ability to do left castling
     *
     * @param leftCastling true if King should be able to do left castling
     */
    public void leftCastling(boolean leftCastling) {
        this.leftCastling = leftCastling;
    }

    /**
     *
     * @return true if King is able to do right Castling
     */
    public boolean canRightCastling() {
        return rightCastling;
    }

    /**
     * sets ability to do left castling
     *
     * @param rightCastling true if King should be able to do right castling
     */
    public void rightCastling(boolean rightCastling) {
        this.rightCastling = rightCastling;
    }

    /**
     * This method is called when FEN string is created, it creates castling
     * possibilities according to this rule: "K" - king side castling is avaible
     * "Q" - queen side castling is avaible
     *
     * @return String of possible castlings
     */
    public String castlingPoss() {
        String s = "";
        s += rightCastling ? "K" : "";
        s += leftCastling ? "Q" : "";
        if (!isWhite) {
            s = s.toLowerCase();
        }
        return s;
    }

    @Override
    public String toString() {
        return "King";
    }

    private ArrayList<Integer> leftCastling(Piece[][] chessboard, String lastMove) {
        ArrayList<Integer> leftCastling = new ArrayList();
        int min = Math.min(getLRC() % Constants.BOARD_SIZE, getLCC() % Constants.BOARD_SIZE);
        int max = Math.max(getCurrentCol(), getLCC() % Constants.BOARD_SIZE);
        if (chessboard[getLCC() / Constants.BOARD_SIZE][getLCC() % Constants.BOARD_SIZE + 1] == null || chessboard[getLCC() / Constants.BOARD_SIZE][getLCC() % Constants.BOARD_SIZE + 1].getVALUE() == 90 || getLCC() == getLRC()) {
            for (int i = min; i <= max; i++) {
                if ((chessboard[getCurrentRow()][i] != null && chessboard[getCurrentRow()][i].getVALUE() != 5 && i != getCurrentCol()) || !check.isCheck(chessboard, isWhite(), getCurrentRow(), i, lastMove).isEmpty()) {
                    return leftCastling;
                }
            }
        } else {
            return leftCastling;
        }
        leftCastling.add(getLRC());
        return leftCastling;
    }

    private ArrayList<Integer> rightCastling(Piece[][] chessboard, String lastMove) {
        ArrayList<Integer> rightCastling = new ArrayList();
        int min = Math.min(getCurrentCol(), getRCC() % Constants.BOARD_SIZE);
        int max = Math.max(getRRC() % Constants.BOARD_SIZE, getRCC() % Constants.BOARD_SIZE);
        if (chessboard[getRCC() / Constants.BOARD_SIZE][getRCC() % Constants.BOARD_SIZE - 1] == null || chessboard[getRCC() / Constants.BOARD_SIZE][getRCC() % Constants.BOARD_SIZE - 1].getVALUE() == 90 || getRCC() == getRRC()) {
            for (int i = min; i <= max; i++) {
                if ((chessboard[getCurrentRow()][i] != null && chessboard[getCurrentRow()][i].getVALUE() != 5 && i != getCurrentCol()) || !check.isCheck(chessboard, isWhite(), getCurrentRow(), i, lastMove).isEmpty()) {
                    return rightCastling;
                }
            }
        } else {
            return rightCastling;
        }
        rightCastling.add(getRRC());
        return rightCastling;
    }

    @Override
    public ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        possibilities.clear();
        if (firstCoor == currentCoor) {
            try {
                if (chessboard[leftRookCoordinate / Constants.BOARD_SIZE][leftRookCoordinate % Constants.BOARD_SIZE] == null || chessboard[leftRookCoordinate / Constants.BOARD_SIZE][leftRookCoordinate % Constants.BOARD_SIZE].getVALUE() != 5) {
                    leftCastling = false;
                } else if (leftCastling == true) {
                    possibilities.addAll(leftCastling(chessboard, lastMove));
                }
                if (chessboard[rightRookCoordinate / Constants.BOARD_SIZE][rightRookCoordinate % Constants.BOARD_SIZE] == null || chessboard[rightRookCoordinate / Constants.BOARD_SIZE][rightRookCoordinate % Constants.BOARD_SIZE].getVALUE() != 5) {
                    rightCastling = false;
                } else if (rightCastling == true) {
                    possibilities.addAll(rightCastling(chessboard, lastMove));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else {
            leftCastling = false;
            rightCastling = false;
        }
        Piece k = chessboard[row][col];
        chessboard[row][col] = null;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if ((row != i || col != j) && i >= 0 && i < Constants.BOARD_SIZE && j >= 0 && j < Constants.BOARD_SIZE
                        && (chessboard[i][j] == null || chessboard[i][j].isWhite() != playsWhite) && check.isCheck(chessboard, isWhite(), i, j, lastMove).isEmpty()) {
                    possibilities.add(i * Constants.BOARD_SIZE + j);
                }
            }
        }
        chessboard[row][col] = k;
        return possibilities;
    }
}
