package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import Game.Constants;

public class Knight extends Piece {

    private final boolean isWhite;
    ImageIcon whitePiece;
    ImageIcon blackPiece;
    private static final int VALUE = 3;
    private ArrayList<Integer> possibilities;
    private static final String NOTATION = "N";
    private final boolean promoted;

    public Knight(boolean isWhite, boolean promoted) {
        this.possibilities = new ArrayList<>();
        this.blackPiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_ndt60.png"));
        this.whitePiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_nlt60.png"));
        this.isWhite = isWhite;
        this.promoted = promoted;
    }

    @Override
    public String toString() {
        return "Knight";
    }

    @Override
    public String getNotation() {
        return NOTATION;
    }

    public ArrayList<Integer> getMoznosti() {
        return possibilities;
    }

    public void clearMoznosti() {
        possibilities.clear();
    }

    @Override
    public int getVALUE() {
        return VALUE;
    }

    @Override
    public int getRealValue() {
        if (promoted) {
            return 1;
        }
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

    private boolean goodIndex(int row, int col) {
        return row < 8 && row >= 0 && col < 8 && col >= 0;
    }

    @Override
    public ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        possibilities.clear();
        if (goodIndex(row + 1, col + 2) && (chessboard[row + 1][col + 2] == null || chessboard[row + 1][col + 2].isWhite() != playsWhite)) {
            possibilities.add((row + 1) * Constants.BOARD_SIZE + col + 2);
        }
        if (goodIndex(row + 2, col + 1) && (chessboard[row + 2][col + 1] == null || chessboard[row + 2][col + 1].isWhite() != playsWhite)) {
            possibilities.add((row + 2) * Constants.BOARD_SIZE + col + 1);
        }
        if (goodIndex(row - 1, col + 2) && (chessboard[row - 1][col + 2] == null || chessboard[row - 1][col + 2].isWhite() != playsWhite)) {
            possibilities.add((row - 1) * Constants.BOARD_SIZE + col + 2);
        }
        if (goodIndex(row - 2, col + 1) && (chessboard[row - 2][col + 1] == null || chessboard[row - 2][col + 1].isWhite() != playsWhite)) {
            possibilities.add((row - 2) * Constants.BOARD_SIZE + col + 1);
        }
        if (goodIndex(row + 1, col - 2) && (chessboard[row + 1][col - 2] == null || chessboard[row + 1][col - 2].isWhite() != playsWhite)) {
            possibilities.add((row + 1) * Constants.BOARD_SIZE + col - 2);
        }
        if (goodIndex(row + 2, col - 1) && (chessboard[row + 2][col - 1] == null || chessboard[row + 2][col - 1].isWhite() != playsWhite)) {
            possibilities.add((row + 2) * Constants.BOARD_SIZE + col - 1);
        }
        if (goodIndex(row - 1, col - 2) && (chessboard[row - 1][col - 2] == null || chessboard[row - 1][col - 2].isWhite() != playsWhite)) {
            possibilities.add((row - 1) * Constants.BOARD_SIZE + col - 2);
        }
        if (goodIndex(row - 2, col - 1) && (chessboard[row - 2][col - 1] == null || chessboard[row - 2][col - 1].isWhite() != playsWhite)) {
            possibilities.add((row - 2) * Constants.BOARD_SIZE + col - 1);
        }
        return possibilities;
    }
}
