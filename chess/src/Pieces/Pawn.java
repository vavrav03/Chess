package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;
import Game.Constants;

public class Pawn extends Piece {

    private final boolean isWhite;
    ImageIcon whitePiece;
    ImageIcon blackPiece;
    private static final int VALUE = 1;
    private static final String NOTATION = "";

    public Pawn(boolean isWhite) {
        this.whitePiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_plt60.png"));
        this.blackPiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_pdt60.png"));
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        return "Pawn";
    }

    @Override
    public String getNotation() {
        return NOTATION;
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

    @Override
    public ImageIcon getPiece() {
        if (isWhite() == true) {
            return whitePiece;
        } else {
            return blackPiece;
        }
    }

    /**
     * tests whether last move allowed to do en-passant move and if yes, calculates coordinates of it
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @lastMove last move String representation (short algebraic notation
     * that displays on the moves JPanel in GUI)
     * @return en-passant move
     */
    private ArrayList<Integer> enPassant(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        ArrayList<Integer> enPassant = new ArrayList();
        int i;
        if (playsWhite) {
            i = -1;
        } else {
            i = 1;
        }
        if (col < Constants.BOARD_SIZE - 1 && chessboard[row][col + 1] != null && chessboard[row][col + 1].getVALUE() == 1 && chessboard[row][col + 1].isWhite() != playsWhite) {
            if (Character.isLowerCase((lastMove.charAt(3))) && (lastMove.charAt(3) - '\u0061' - 1) == col && Math.abs(lastMove.charAt(4) - lastMove.charAt(6)) == 2) {
                enPassant.add((row + i) * Constants.BOARD_SIZE + col + 1);
            }
        }
        if (col > 0 && chessboard[row][col - 1] != null && chessboard[row][col - 1].getVALUE() == 1 && chessboard[row][col - 1].isWhite() != playsWhite) {
            if (Character.isLowerCase((lastMove.charAt(3))) && (lastMove.charAt(3) - '\u0061' + 1) == col && Math.abs(lastMove.charAt(4) - lastMove.charAt(6)) == 2) {
                enPassant.add((row + i) * Constants.BOARD_SIZE + col - 1);
            }
        }
        return enPassant;
    }

    /**
     * tests whether pawn on certain square can take another pawn and do kill mod
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current piece row
     * @param col current piece col
     * @return List of kill moves
     */
    public ArrayList<Integer> kill(Piece[][] chessboard, boolean playsWhite, int row, int col) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        if (!playsWhite) {
            if (row != Constants.BOARD_SIZE - 1) {
                if (col != 0 && chessboard[row + 1][col - 1] != null && chessboard[row + 1][col - 1].isWhite() != playsWhite) {
                    possibilities.add((row + 1) * Constants.BOARD_SIZE + col - 1);
                }
                if (col != 7 && chessboard[row + 1][col + 1] != null && chessboard[row + 1][col + 1].isWhite() != playsWhite) {
                    possibilities.add((row + 1) * Constants.BOARD_SIZE + col + 1);
                }
            }
        } else {
            if (row != 0) {
                if (col != 0 && chessboard[row - 1][col - 1] != null && chessboard[row - 1][col - 1].isWhite() != playsWhite) {
                    possibilities.add((row - 1) * Constants.BOARD_SIZE + col - 1);
                }
                if (col != 7 && chessboard[row - 1][col + 1] != null && chessboard[row - 1][col + 1].isWhite() != playsWhite) {
                    possibilities.add((row - 1) * Constants.BOARD_SIZE + col + 1);
                }
            }
        }
        return possibilities;
    }

    @Override
    public ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        ArrayList<Integer> moves = new ArrayList();
        moves.clear();
        if (!playsWhite) {
            if (chessboard[row + 1][col] == null) {
                moves.add((row + 1) * Constants.BOARD_SIZE + col);
                if (row == 1 && chessboard[row + 2][col] == null) {
                    moves.add((row + 2) * Constants.BOARD_SIZE + col);
                }
            }
        } else {
            if (chessboard[row - 1][col] == null) {
                moves.add((row - 1) * Constants.BOARD_SIZE + col);
                if (row == 6 && chessboard[row - 2][col] == null) {
                    moves.add((row - 2) * Constants.BOARD_SIZE + col);
                }
            }
        }
        moves.addAll(enPassant(chessboard, playsWhite, row, col, lastMove));
        moves.addAll(kill(chessboard, playsWhite, row, col));
        return moves;
    }
}
