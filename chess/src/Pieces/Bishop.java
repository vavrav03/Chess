package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import Game.Constants;

public class Bishop extends Piece {

    private final boolean isWhite;
    ImageIcon whitePiece;
    ImageIcon blackPiece;
    private static final int VALUE = 3;
    private static final String NOTATION = "B";
    int i, j;
    private final boolean promoted;

    public Bishop(boolean bily, boolean promoted) {
        this.whitePiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_blt60.png"));
        this.blackPiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_bdt60.png"));
        this.isWhite = bily;
        this.promoted = promoted;
    }

    @Override
    public String toString() {
        return "Bishop";
    }

    @Override
    public String getNotation() {
        return NOTATION;
    }

    @Override
    public int getRealValue() {
        if (promoted) {
            return 1;
        }
        return VALUE;
    }

    @Override
    public int getVALUE() {
        return VALUE;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public ImageIcon getPiece() {
        if (isWhite() == true) {
            return whitePiece;
        } else {
            return blackPiece;
        }
    }

    /**
     * contributes to final arrayList of moves with subArrayList with moves for
     * specific direction
     *
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return ArrayList of moves in north-east direction
     */
    public ArrayList<Integer> NEMovement(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = row - 1, j = col + 1; i >= 0 && j < Constants.BOARD_SIZE && chessboard[i][j] == null; i--, j++) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        if (i >= 0 && j < Constants.BOARD_SIZE && chessboard[i][j].isWhite() != playsWhite) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        return possibilities;
    }

    /**
     * contributes to final arrayList of moves with subArrayList with moves for
     * specific direction
     *
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return ArrayList of moves in south-east direction
     */
    public ArrayList<Integer> SEMovement(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = row + 1, j = col + 1; i < Constants.BOARD_SIZE && j < Constants.BOARD_SIZE && chessboard[i][j] == null; i++, j++) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        if (i < Constants.BOARD_SIZE && j < Constants.BOARD_SIZE && chessboard[i][j].isWhite() != playsWhite) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        return possibilities;
    }

    /**
     * contributes to final arrayList of moves with subArrayList with moves for
     * specific direction
     *
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return ArrayList of moves in south-west direction
     */
    public ArrayList<Integer> SWMovement(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = row + 1, j = col - 1; i < Constants.BOARD_SIZE && j >= 0 && chessboard[i][j] == null; i++, j--) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        if (i < Constants.BOARD_SIZE && j >= 0 && chessboard[i][j].isWhite() != playsWhite) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        return possibilities;
    }

    /**
     * contributes to final arrayList of moves with subArrayList with moves for
     * specific direction
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return ArrayList of moves in north-west direction
     */
    public ArrayList<Integer> NWMovement(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = row - 1, j = col - 1; i >= 0 && j >= 0 && chessboard[i][j] == null; i--, j--) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        if (i >= 0 && j >= 0 && chessboard[i][j].isWhite() != playsWhite) {
            possibilities.add(i * Constants.BOARD_SIZE + j);
        }
        return possibilities;
    }

    @Override
    public ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        possibilities.addAll(NEMovement(chessboard, playsWhite, row, col));
        possibilities.addAll(SEMovement(chessboard, playsWhite, row, col));
        possibilities.addAll(SWMovement(chessboard, playsWhite, row, col));
        possibilities.addAll(NWMovement(chessboard, playsWhite, row, col));
        return possibilities;
    }
}
