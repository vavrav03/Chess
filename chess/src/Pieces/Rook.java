package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import Game.Constants;

public class Rook extends Piece {

    public final boolean isWhite;
    ImageIcon whitePiece;
    ImageIcon blackPiece;
    private static final int VALUE = 5;
    private static final String NOTATION = "R";
    private final boolean promoted;
    int i;

    public Rook(boolean isWhite, boolean promoted) {
        this.isWhite = isWhite;
        this.whitePiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_rlt60.png"));
        this.blackPiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_rdt60.png"));
        this.promoted = promoted;
    }

    @Override
    public String toString() {
        return "Rook";
    }

    @Override
    public String getNotation() {
        return NOTATION;
    }

    public int getI() {
        return i;
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
     * @return ArrayList of moves in north direction
     */
    public ArrayList<Integer> NMove(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = row - 1; i >= 0 && chessboard[i][col] == null; i--) {
            possibilities.add(i * Constants.BOARD_SIZE + col);
        }
        if (i >= 0 && chessboard[i][col].isWhite() != playsWhite) {
            possibilities.add(i * Constants.BOARD_SIZE + col);
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
     * @return ArrayList of moves in east direction
     */
    public ArrayList<Integer> EMove(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = col + 1; i < Constants.BOARD_SIZE && chessboard[row][i] == null; i++) {
            possibilities.add(row * Constants.BOARD_SIZE + i);
        }
        if (i < Constants.BOARD_SIZE && chessboard[row][i].isWhite() != playsWhite) {
            possibilities.add(row * Constants.BOARD_SIZE + i);
        }
        return possibilities;
    }
    
    /**
     * contributes to final arrayList of moves with subArrayList with moves for specific direction
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return ArrayList of moves in south direction
     */
    public ArrayList<Integer> SMove(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = row + 1; i < Constants.BOARD_SIZE && chessboard[i][col] == null; i++) {
            possibilities.add(i * Constants.BOARD_SIZE + col);
        }
        if (i < Constants.BOARD_SIZE && chessboard[i][col].isWhite() != playsWhite) {
            possibilities.add(i * Constants.BOARD_SIZE + col);
        }
        return possibilities;
    }
    
    /**
     * contributes to final arrayList of moves with subArrayList with moves for specific direction
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return ArrayList of moves in wests direction
     */
    public ArrayList<Integer> WMove(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        for (i = col - 1; i >= 0 && chessboard[row][i] == null; i--) {
            possibilities.add(row * Constants.BOARD_SIZE + i);
        }
        if (i >= 0 && chessboard[row][i].isWhite() != playsWhite) {
            possibilities.add(row * Constants.BOARD_SIZE + i);
        }
        return possibilities;
    }

    @Override
    public ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        possibilities.addAll(NMove(chessboard, playsWhite, row, col));
        possibilities.addAll(EMove(chessboard, playsWhite, row, col));
        possibilities.addAll(SMove(chessboard, playsWhite, row, col));
        possibilities.addAll(WMove(chessboard, playsWhite, row, col));
        return possibilities;
    }
}
