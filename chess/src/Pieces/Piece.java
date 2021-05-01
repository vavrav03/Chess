package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * represent chess piece as object
 * @author vavra
 */
public abstract class Piece {
    /**
     * Creates arrayList of moves where can Piece go according to chess rules
     * @param chessboard current board state
     * @param playsWhite true if white is on the move
     * @param row current row of piece
     * @param col Currect col of piece
     * @param lastMove - last move String representation (short algebraic notation that displays on the moves JPanel in GUI)
     * @return All coordinates where can piece move regardless any further move reducation
     */
    public abstract ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove);

    /**
     *
     * @return if piece is white, then returns true.
     */
    public abstract boolean isWhite();
    
    /**
     *
     * @return ImageIcon of chess piece.
     */
    public abstract ImageIcon getPiece();
    
    /**
     *
     * @return Value of piece according to FIDE.
     */
    public abstract int getVALUE();
    
    /**
     *
     * @return When the piece was promoted, it's value stays 1, this method returns 1 if piece was promoted or normal value if not.
     */
    public abstract int getRealValue();
    
    /**
     *
     * @return One letter representing piece in lastMove String.
     */
    public abstract String getNotation();
}
