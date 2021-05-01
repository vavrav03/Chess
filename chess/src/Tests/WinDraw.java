package Tests;

import Game.Constants;
import java.util.Map.Entry;
import Game.LogicBoard;

/**
 * Checks for current board state of the games
 *
 * @author vavra
 */
public class WinDraw extends Check {

    private LogicBoard lb;
    private String why;

    public WinDraw(LogicBoard lb) {
        this.lb = lb;
    }
    
    /**
     *
     * @return reason why game ended as it ended
     */
    public String why(){
        return why;
    }
    
    /**
     *
     * @param why reason why game ended as it ended
     */
    public void setWhy(String why){
        this.why = why;
    }

    private boolean possibleMoves() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (lb.getChessboard(i, j) != null && lb.getChessboard(i, j).isWhite() == lb.whiteMoves() && !lb.moveReduction(i, j).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * checks for draw by insufficient material only Kings, only Kings and n of
     * Bishops on same color, only Kings and max 1 Knight on each side
     *
     * @return true if game is drawn by insufficient material based on FIDE
     * rules, otherwise returns false
     */
    public boolean notEnoughMaterial() {
        int[][] pieces = new int[2][3];
        int white;
        int col;
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (lb.getChessboard(i, j) != null) {
                    if (lb.getChessboard(i, j).isWhite()) {
                        white = 0;
                    } else {
                        white = 1;
                    }
                    if (lb.getChessboard(i, j).getVALUE() == 1 || lb.getChessboard(i, j).getVALUE() == 5 || lb.getChessboard(i, j).getVALUE() == 9) {
                        return false;
                    } else if (lb.getChessboard(i, j).getVALUE() == 3) {
                        if (lb.getChessboard(i, j).getNotation().equals(Constants.KNIGHT_NOT)) {
                            col = 0;
                        } else if ((i + j) % 2 == 0) {
                            col = 1;
                        } else {
                            col = 2;
                        }
                        if (pieces[white][0] != 0) {
                            return false;
                        } else {
                            pieces[white][col] += 1;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            if (pieces[i][1] != 0 && pieces[i][2] != 0) {
                return false;
            }
        }
        why = "Not enough material";
        return true;
    }

    /**
     * Checks whether the game is drawn by repeating board state 3 times in the
     * entire game
     * @return true if game is drawn, otherwise false
     */
    public boolean threefoldRep() {
        for (Entry<String, Integer> map : lb.getFen().entrySet()) {
            if (map.getValue() >= 3) {
                why = "Threefold repetition";
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether is game drawn by 50 moves rule, i.e no player has played
     * with pawn or took opponent's piece in last 50 movess
     * @return true if game is drawn
     */
    public boolean fiftyMovesDraw() {
        if(lb.getMax50() >= 50){
            why = "fifty moves rule";
            return true;
        }
        return false;
    }

    /**
     * checks the game state at current position
     * @return 0 = still not ended / 1 = white won / 2 = black won / 3 = draw
     */
    public int gameState() {
        if (fiftyMovesDraw() || threefoldRep()) {
            return 3;
        }
        if (possibleMoves()) {
            why = "";
            return 0;
        } else if (isCheck == true) {
            why = "checkmate";
            return !lb.whiteMoves() ? 1 : 2;
        } else {
            return 3;
        }
    }
}
