package Pieces;

import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Queen extends Piece {

    private final boolean isWhite;
    ImageIcon whitePiece;
    ImageIcon blackPiece;
    private static final int VALUE = 9;
    public Bishop Bishop;
    public Rook Rook;
    private final boolean promoted;
    private static final String NOTATION = "Q";

    public Queen(boolean isWhite, boolean promoted) {
        this.isWhite = isWhite;
        this.blackPiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_qdt60.png"));
        this.whitePiece = new ImageIcon(this.getClass().getResource("/PieceImages/Chess_qlt60.png"));
        this.Bishop = new Bishop(isWhite(), false);
        this.Rook = new Rook(isWhite(), false);
        this.promoted = promoted;
    }
        
    @Override
    public String toString() {
        return "Queen";
    }
    
    @Override
    public String getNotation(){
        return NOTATION;
    }
    
    @Override
    public int getVALUE(){
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
    public ImageIcon getPiece(){
        if(isWhite()==true){
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
    public ArrayList<Integer> movement(Piece[][] chessboard, boolean playsWhite, int row, int col, String lastMove) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        possibilities.addAll(Rook.movement(chessboard, playsWhite, row, col, lastMove));
        possibilities.addAll(Bishop.movement(chessboard, playsWhite, row, col, lastMove));
        return possibilities;
    }
}
