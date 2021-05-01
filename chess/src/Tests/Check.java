package Tests;

import Pieces.Queen;
import java.util.ArrayList;
import Pieces.Piece;
import Pieces.Knight;
import Pieces.Pawn;
import Game.Constants;

/**
 * This class handles move reduction caused by checking the king.
 * @author vavral
 */
public class Check {

    boolean isCheck;
    private Queen whiteQueen = new Queen(true, false);
    private Queen blackQueen = new Queen(false, false);
    private Knight whiteKnight = new Knight(true, false);
    private Knight blackKnight = new Knight(false, false);
    private Pawn WhitePawn = new Pawn(true);

    /**
     * Checks whether is King of player that is on the move in check
     * @param chessboard Piece[][] representation of board 
     * @param playsWhite true if white is on the move
     * @param kingsRow current row of the king
     * @param kingsCol current col of the king
     * @param lastMove last move String representation (short algebraic notation that displays on the moves JPanel in GUI)
     * @return ArrayList If there is no check then returns empty ArrayList, if only one piece endangers King method returns all coordinates where piece that endangers the King can go. if more pieces endanger King, method returns -1 
     */
    public ArrayList<Integer> isCheck(Piece[][] chessboard, boolean playsWhite, int kingsRow, int kingsCol, String lastMove) {
        ArrayList<Integer> invalidPart = new ArrayList<>();
        ArrayList<Integer> helpList;
        int chessCount = 0;

        int helpSize = 0;
        helpList = whiteQueen.Rook.WMove(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrRook(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Rook.EMove(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrRook(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Rook.NMove(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrRook(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Rook.SMove(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrRook(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Bishop.NWMovement(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrBishop(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Bishop.NEMovement(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrBishop(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Bishop.SWMovement(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrBishop(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteQueen.Bishop.SEMovement(chessboard, playsWhite, kingsRow, kingsCol);
        if (!helpList.isEmpty() && isQueenOrBishop(chessboard, helpList.get(helpList.size() - 1) / Constants.BOARD_SIZE, helpList.get(helpList.size() - 1) % Constants.BOARD_SIZE)) {
            invalidPart.addAll(helpList);
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = whiteKnight.movement(chessboard, playsWhite, kingsRow, kingsCol, lastMove);
        for (int i = 0; i < helpList.size(); i++) {
            int row = helpList.get(i) / Constants.BOARD_SIZE;
            int col = helpList.get(i) % Constants.BOARD_SIZE;
            if (chessboard[row][col] != null && (chessboard[row][col].getClass() == whiteKnight.getClass() || chessboard[row][col].getClass() == blackKnight.getClass())) {
                invalidPart.add(helpList.get(i));
            }
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }

        helpSize = invalidPart.size();
        helpList = WhitePawn.kill(chessboard, playsWhite, kingsRow, kingsCol);
        for (int i = 0; i < helpList.size(); i++) {
            if (chessboard[helpList.get(i) / Constants.BOARD_SIZE][helpList.get(i) % Constants.BOARD_SIZE].getVALUE() == 1) {
                invalidPart.add(helpList.get(i));
            }
        }
        if (invalidPart.size() > helpSize) {
            chessCount++;
        }
        isCheck = chessCount != 0;
        if (chessCount > 1) {
            invalidPart.clear();
            invalidPart.add(-1);
        }
        return invalidPart;
    }

    /**
     * checks if the piece moves, king gets check  
     * @param chessboard Piece[][] representation of board 
     * @param playsWhite true if white is on the move
     * @param kingsRow current row of the king
     * @param kingsCol current col of the king
     * @param pieceRow current row of the piece
     * @param pieceCol current col of the piece
     * @param lastMove last move String representation (short algebraic notation that displays on the moves JPanel in GUI)
     * @return if piece can't move, it returns all coordinates where piece that forbids move can go, otherwise it returns empty arrayList
     */
    public ArrayList<Integer> canMove(Piece[][] chessboard, boolean playsWhite, int kingsRow, int kingsCol, int pieceRow, int pieceCol, String lastMove) {
        ArrayList<Integer> x = new ArrayList<>();
        if (!(kingsRow == pieceRow && pieceCol == kingsCol)) {
            Piece f = chessboard[pieceRow][pieceCol];
            chessboard[pieceRow][pieceCol] = null;
            x = isCheck(chessboard, playsWhite, kingsRow, kingsCol, lastMove);
            chessboard[pieceRow][pieceCol] = f;
        }
        return x;
    }

    private boolean isQueenOrRook(Piece[][] chessboard, int row, int col) {
        return (chessboard[row][col] != null && (chessboard[row][col].getVALUE() == 9 || chessboard[row][col].getVALUE() == 5));
    }

    private boolean isQueenOrBishop(Piece[][] chessboard, int row, int col) {
        return (chessboard[row][col] != null && (chessboard[row][col].getVALUE() == 9 || chessboard[row][col].getClass() == whiteQueen.Bishop.getClass() || chessboard[row][col].getClass() == blackQueen.Bishop.getClass()));
    }
}
