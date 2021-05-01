package Game;

import Pieces.Piece;
import Pieces.Queen;
import Pieces.Knight;
import Pieces.King;
import Pieces.Pawn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import Communication.DataParsing;
import java.util.Map.Entry;

/**
 * Implements object representation of board, determines who is on move, saves
 * every move in game etc...
 *
 * @author vavra
 */
public class LogicBoard {

    private Piece[][] chessboard;
    private int whiteLostMaterial;
    private int blackLostMaterial;
    private boolean whiteMoves;
    private ArrayList<Integer> possibleMoves;
    private Piece[] whitePieces;
    private Piece[] blackPieces;
    private boolean isCheck;
    private ArrayList<Integer> allowedPart;
    private ArrayList<Integer> invalidPart;
    private double moveNumber;
    private String lastMove = "";
    private int max50;
    private HashMap<String, Integer> fen;

    /**
     * creates LogicBoard object
     * @param gameType game setting type(Normal / Fischer)
     */
    public LogicBoard(String gameType) {
        this.possibleMoves = new ArrayList();
        initPieces();
        whiteMoves = true;
        if (gameType.equals("Normal")) {
            chessboard = normalSetting();
        } else {
            chessboard = fischerChess();
        }
        findKingsRooks();
        invalidPart = new ArrayList();
        moveNumber = 0;
        entireMaterial();
        fen = new HashMap();
        addFen(DataParsing.outFen(this));
    }

    public HashMap<String, Integer> getFen() {
        return fen;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public King getBlackKing() {
        return (King) blackPieces[5];
    }

    public King getWhiteKing() {
        return (King) whitePieces[5];
    }

    public boolean whiteMoves() {
        return whiteMoves;
    }

    public void whiteMoves(boolean whiteMoves) {
        this.whiteMoves = whiteMoves;
    }

    public int getWhiteLostMaterial() {
        return whiteLostMaterial;
    }

    public int getBlackLostMaterial() {
        return blackLostMaterial;
    }

    public Piece getChessboard(int row, int col) {
        return chessboard[row][col];
    }

    public void setChessboard(Piece[][] chessboard) {
        this.chessboard = chessboard;
    }

    public Piece[][] getChessboard() {
        return chessboard;
    }

    public String getStringMoveNumber() {
        return String.valueOf((int) (Math.floor(this.moveNumber + 1))) + ". ";
    }

    /**
     * increases value of moveNumber by 0.5 (then can program easily calculate
     * current move by rounding it)
     */
    public void halfIncrementMoveNumber() {
        moveNumber += 0.5;
    }
    
    public void halfDecrementTheNumber(){
        moveNumber -= 0.5;
    }

    public String getLastMove() {
        return lastMove;
    }

    public int getMax50() {
        return max50;
    }

    public void setMax50(int max50) {
        this.max50 = max50;
    }
    
    public double getMoveNumber(){
        return moveNumber;
    }
    
    public void setMoveNumber(double moveNumber){
        this.moveNumber = moveNumber;
    }

    /**
     * This method calculates whether can certain pawn do en passant move
     *
     * @param a ArrayList of all coordinates where can pawn go
     * @param row of piece
     * @param col of piece
     * @return true if it can or no if it can't
     */
    public boolean canEnPassant(ArrayList<Integer> a, int row, int col) {
        for (int coor : a) {
            if ((row == Constants.BOARD_SIZE - 4 || row == 3) && Math.abs(coor / Constants.BOARD_SIZE - row) == 1 && Math.abs(coor % Constants.BOARD_SIZE - col) == 1 && chessboard[coor / Constants.BOARD_SIZE][coor % Constants.BOARD_SIZE] == null) {
                return true;
            }
        }
        return false;
    }

    private void addFen(String fen) {
        String[] parts = fen.split(" ");
        String tempFen = parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3];
        int howManyTimes = 0;
        for (Entry<String, Integer> entry : this.fen.entrySet()) {
            String[] entryParts = entry.getKey().split(" ");
            if (tempFen.equals(entryParts[0] + " " + entryParts[1] + " " + entryParts[2] + " " + entryParts[3])) {
                howManyTimes++;
            }
        }
        this.fen.put(fen, howManyTimes);
    }

    /**
     * This method is used when player wants to return his last move.
     *
     * @return FEN string of chessboard before last player move
     */
    public String getLastFen() {
        int lookingForNumber = (int) Math.ceil(moveNumber - (whiteMoves() ? 0.5 : 1));
        String wanted = "";
        String lookingForLetter = whiteMoves() ? "w" : "b";
        ArrayList<String> toRemove = new ArrayList();
        for (Entry<String, Integer> map : fen.entrySet()) {
            String[] s = map.getKey().split(" ");
            if (Integer.parseInt(s[5]) == lookingForNumber) {
                if (s[1].equals(lookingForLetter)) {
                    wanted = map.getKey();
                } else if (whiteMoves()) {
                    toRemove.add(map.getKey());
                }
            } else if (Integer.parseInt(s[5]) > lookingForNumber) {
                toRemove.add(map.getKey());
            }
        }
        for (String x : toRemove) {
            this.fen.remove(x);
        }
        return wanted;
    }

    /**
     *
     * @param row row where piece ended
     * @param col col where piece ended
     * @param previousCol col where piece came from
     * @param previousRow row where piece came from
     * @param whitePlays yes if white moved in this move
     * @param isCapturing yes if there was another piece on row and col before
     * this piece moved there
     * @param specialities 0 = normal, 1 = right castling, 2 = left castling, 3
     * = promotion, 6 = en passant from engine
     */
    public void setLastMove(int row, int col, int previousCol, int previousRow, boolean whitePlays, boolean isCapturing, int specialities) {
        if (specialities != 6 && specialities != 3) {
            if (specialities < 1 || specialities > 3) {
                lastMove = getStringMoveNumber() + getChessboard(row, col).getNotation() + String.valueOf((char) ('\u0061' + previousCol)) + "" + (Constants.BOARD_SIZE - (previousRow));
                if (isCapturing) {
                    lastMove += " x ";
                }
                lastMove += String.valueOf((char) ('\u0061' + col)) + "" + (Constants.BOARD_SIZE - (row));
            }
        } else {
            lastMove = getLastMove() + getChessboard(row, col).getNotation();
        }
        if (specialities == 1) {
            lastMove = getStringMoveNumber() + "0-0";
        } else if (specialities == 2) {
            lastMove = getStringMoveNumber() + "0-0-0";
        }
        if (specialities != 6) {
            if (getInvalidPart().size() == 1 && getInvalidPart().get(0) == -1) {
                lastMove += "++";
            } else if (isCheck() == true) {
                lastMove += "+";
            }
        }
        halfIncrementMoveNumber();
        addFen(DataParsing.outFen(this));
    }

    /**
     *
     * @param first if true, it's chosen the king that moves now
     * @return current row of king based on first
     */
    public int getKingRow(boolean first) {
        if (whiteMoves()) {
            if (first) {
                return getWhiteKing().getCurrentRow();
            } else {
                return getBlackKing().getCurrentRow();
            }
        } else if (first) {
            return getBlackKing().getCurrentRow();
        } else {
            return getWhiteKing().getCurrentRow();
        }
    }

    /**
     *
     * @param first if true, it's chosen the king that moves now
     * @return current col of king based on first
     */
    public int getKingCol(boolean first) {
        if (whiteMoves()) {
            if (first) {
                return getWhiteKing().getCurrentCol();
            } else {
                return getBlackKing().getCurrentCol();
            }
        } else if (first) {
            return getBlackKing().getCurrentCol();
        } else {
            return getWhiteKing().getCurrentCol();
        }
    }

    private void initPieces() {
        this.whitePieces = new Piece[6];
        this.blackPieces = new Piece[6];
        whitePieces[0] = new Pawn(true);
        whitePieces[1] = new Knight(true, false);
        whitePieces[2] = new Queen(true, false).Bishop;
        whitePieces[3] = new Queen(true, false).Rook;
        whitePieces[4] = new Queen(true, false);
        whitePieces[5] = new King(true, this);
        blackPieces[0] = new Pawn(false);
        blackPieces[1] = new Knight(false, false);
        blackPieces[2] = new Queen(false, false).Bishop;
        blackPieces[3] = new Queen(false, false).Rook;
        blackPieces[4] = new Queen(false, false);
        blackPieces[5] = new King(false, this);
    }

    /**
     *
     * @param index 0 = Pawn, 1 = Knight, 2 = Bishop, 3 = Rook, 4 = Queen, 5 =
     * King
     * @param isWhite
     * @return Object of chosen piece
     */
    public Piece getPiece(int index, boolean isWhite) {
        if (isWhite) {
            return whitePieces[index];
        } else {
            return blackPieces[index];
        }
    }

    /**
     *
     * @param row current row of piece
     * @param col current col of piece
     * @return ArrayList of moves that can piece make according to checks and
     * other reducations..
     */
    public ArrayList<Integer> moveReduction(int row, int col) {
        possibleMoves.clear();
        if (LogicBoard.this.getChessboard(row, col) != null && LogicBoard.this.getChessboard(row, col).isWhite() == whiteMoves()) {
            allowedPart = getWhiteKing().check.canMove(chessboard, whiteMoves(), getKingRow(true), getKingCol(true), row, col, lastMove);
            possibleMoves = LogicBoard.this.getChessboard(row, col).movement(getChessboard(), whiteMoves(), row, col, lastMove);
            if (!allowedPart.isEmpty()) {
                possibleMoves.retainAll(allowedPart);
            }
            if (LogicBoard.this.getChessboard(row, col).getVALUE() == 90) {
                for (int i = 0; i < possibleMoves.size(); i++) {
                    if (Math.abs(possibleMoves.get(i) / Constants.BOARD_SIZE - getKingRow(false)) <= 1 && Math.abs(possibleMoves.get(i) % Constants.BOARD_SIZE - getKingCol(false)) <= 1) {
                        possibleMoves.remove(i);
                        --i;
                    }
                }
            }
            if (!invalidPart.isEmpty() && chessboard[row][col].getVALUE() != 90) {
                possibleMoves.retainAll(invalidPart);
            }
            if (invalidPart.contains(-1) && chessboard[row][col].getVALUE() != 90) {
                possibleMoves.clear();
            }
        }
        return possibleMoves;
    }

    /**
     *
     * @return ArrayList of moves that can piece make according to checks and
     * other reducations. Use only if you have olready called moveReducation
     * method
     */
    public ArrayList<Integer> getPossibleMoves() {
        return possibleMoves;
    }

    /**
     *
     * @return ArrayList of all coordinates where can piece go and don't get a
     * check if it's not doublechecked
     */
    public ArrayList<Integer> getInvalidPart() {
        return invalidPart;
    }

    /**
     * calculates the entire material of black and white and stores the values
     */
    public void entireMaterial() {
        int entireValueWhite = 0;
        int entireValueBlack = 0;
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int j = 0; j < Constants.BOARD_SIZE; ++j) {
                if (getChessboard(i, j) != null) {
                    if (!chessboard[i][j].isWhite()) {
                        entireValueBlack += chessboard[i][j].getVALUE();
                    } else {
                        entireValueWhite += chessboard[i][j].getVALUE();
                    }
                }
            }
        }
        this.blackLostMaterial = Constants.NORMAL_SETTING_VALUE - entireValueBlack;
        this.whiteLostMaterial = Constants.NORMAL_SETTING_VALUE - entireValueWhite;
    }

    /**
     * it changes the logical chessboard based on move and do other logical
     * stuff like changing whiteMoves() variable.
     *
     * @param row row where is piece now
     * @param col col where is piece now
     * @param previousRow row where piece came from
     * @param previousCol col where piece came from
     * @param piece if pawn promotes, the piece is initialized as wanted piece
     * that should be pawn replaced by, else it's null
     */
    public void setChessboard(int row, int col, int previousRow, int previousCol, Piece piece) {
        if (chessboard[row][col] != null) {
            if (chessboard[row][col].isWhite()) {
                if (piece != null) {
                    increaseBLostMaterial(piece.getVALUE());
                }
                increaseWLostMaterial(chessboard[row][col].getVALUE());
            } else {
                if (piece != null) {
                    increaseWLostMaterial(piece.getVALUE());
                }
                increaseBLostMaterial(chessboard[row][col].getVALUE());
            }
        }
        chessboard[row][col] = chessboard[previousRow][previousCol];
        chessboard[previousRow][previousCol] = null;
        if (piece != null) {
            chessboard[row][col] = piece;
        }
        if (chessboard[row][col] != null && chessboard[row][col].getVALUE() == 90) {
            King k = (King) chessboard[row][col];
            k.setCoordinates(row, col);
        }
        if (row != previousRow || col != previousCol) {
            whiteMoves = !whiteMoves;
        }
        checkForCheck();
    }

    /**
     * checks whterher is King of the player that is on the move in check and
     * sets variables that indicates check to other methods
     */
    public void checkForCheck() {
        invalidPart = getWhiteKing().check.isCheck(getChessboard(), whiteMoves(), getKingRow(true), getKingCol(true), lastMove);
        isCheck = !invalidPart.isEmpty();
    }

    /**
     * implements castling on logical chessboard
     *
     * @param KRow row where king went
     * @param KCol col where king went
     * @param KFirstRow row where king came from
     * @param KFirstCol col where king came from
     * @param RRow row where rook went
     * @param RCol col where rook went
     * @param RFirstRow row where rook came from
     * @param RFirstCol col where rook came from
     */
    public void castling(int KRow, int KCol, int KFirstRow, int KFirstCol, int RRow, int RCol, int RFirstRow, int RFirstCol) {
        King k = (King) chessboard[KFirstRow][KFirstCol];
        Piece rook = chessboard[RFirstRow][RFirstCol];
        chessboard[KFirstRow][KFirstCol] = null;
        chessboard[RFirstRow][RFirstCol] = null;
        chessboard[KRow][KCol] = k;
        chessboard[RRow][RCol] = rook;
        k.setCoordinates(KRow, KCol);
        whiteMoves = !whiteMoves;
        invalidPart = getWhiteKing().check.isCheck(getChessboard(), whiteMoves(), getKingRow(true), getKingCol(true), lastMove);
        isCheck = !invalidPart.isEmpty();
    }

    /**
     *
     * @param row row where pawn went
     * @param col col where pawn went
     * @param firstRow row where pawn came from
     * @param firstCol col where pawn came from
     * @return true if the last move was en passant move else false
     */
    public boolean isEnPassant(int row, int col, int firstRow, int firstCol) {
        return (row == Constants.BOARD_SIZE - 3 || row == 2) && chessboard[firstRow][col] != null && chessboard[firstRow][col].isWhite() != chessboard[row][col].isWhite() && chessboard[row][col] != null && chessboard[row][col].getVALUE() == 1 && Math.abs(row - firstRow) == 1 && Math.abs(col - firstCol) == 1;
    }

    /**
     *
     * @param row row where piece went
     * @param piece piece that made the move
     * @return true if the last move was promotion of the pawn
     */
    public boolean isPromotion(int row, Piece piece) {
        return (row == Constants.BOARD_SIZE - 1 || row == 0) && piece.getVALUE() == 1;
    }

    /**
     * increase the value of whiteLostMaterial
     *
     * @param pieceValue value of killed piece
     */
    public void increaseWLostMaterial(int pieceValue) {
        this.whiteLostMaterial += pieceValue;
    }

    /**
     * inreases the value of black lost material
     *
     * @param pieceValue value of killed piece
     */
    public void increaseBLostMaterial(int pieceValue) {
        this.blackLostMaterial += pieceValue;
    }

    /**
     *
     * @return true if lost material of white is less than lost material of
     * black
     */
    public boolean whiteLeads() {
        return getWhiteLostMaterial() < getBlackLostMaterial();
    }

    /**
     * put a piece to a specific coordinate
     *
     * @param row
     * @param col
     * @param piece
     */
    public void changePiece(int row, int col, Piece piece) {
        chessboard[row][col] = piece;
    }

    /**
     *
     * @return chessboard of normal chess game
     */
    public Piece[][] normalSetting() {
        Piece[][] chessboard = {
            {blackPieces[3], blackPieces[1], blackPieces[2], blackPieces[4], blackPieces[5], blackPieces[2], blackPieces[1], blackPieces[3]},
            {blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0]},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0]},
            {whitePieces[3], whitePieces[1], whitePieces[2], whitePieces[4], whitePieces[5], whitePieces[2], whitePieces[1], whitePieces[3]}
        };
        return chessboard;
    }

    /**
     *
     * @return chessboard respecting rules of Fischer chess(960 game)
     */
    public Piece[][] fischerChess() {
        Piece[][] chessboard = {
            {null, null, null, null, null, null, null, null},
            {blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0], blackPieces[0]},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0], whitePieces[0]},
            {null, null, null, null, null, null, null, null}
        };
        Random random = new Random();
        ArrayList<Integer> list = new ArrayList();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            list.add(i);
        }
        int sudyStrelec = random.nextInt(4) * 2;
        chessboard[0][sudyStrelec] = blackPieces[2];
        chessboard[Constants.BOARD_SIZE - 1][sudyStrelec] = whitePieces[2];
        list.remove((Integer) sudyStrelec);
        int lichyStrelec = random.nextInt(4) * 2 + 1;
        chessboard[0][lichyStrelec] = blackPieces[2];
        chessboard[Constants.BOARD_SIZE - 1][lichyStrelec] = whitePieces[2];
        list.remove((Integer) lichyStrelec);
        int jezdec1 = list.get(random.nextInt(list.size()));
        chessboard[0][jezdec1] = blackPieces[1];
        chessboard[Constants.BOARD_SIZE - 1][jezdec1] = whitePieces[1];
        list.remove((Integer) jezdec1);
        int jezdec2 = list.get(random.nextInt(list.size()));
        chessboard[0][jezdec2] = blackPieces[1];
        chessboard[Constants.BOARD_SIZE - 1][jezdec2] = whitePieces[1];
        list.remove((Integer) jezdec2);
        int dama = list.get(random.nextInt(list.size()));
        chessboard[0][dama] = blackPieces[4];
        chessboard[Constants.BOARD_SIZE - 1][dama] = whitePieces[4];
        list.remove((Integer) dama);
        chessboard[0][list.get(0)] = blackPieces[3];
        chessboard[Constants.BOARD_SIZE - 1][list.get(0)] = whitePieces[3];
        chessboard[0][list.get(1)] = blackPieces[5];
        chessboard[Constants.BOARD_SIZE - 1][list.get(1)] = whitePieces[5];
        chessboard[0][list.get(2)] = blackPieces[3];
        chessboard[Constants.BOARD_SIZE - 1][list.get(2)] = whitePieces[3];
        return chessboard;
    }

    private void findKingsRooks() {
        for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
            for (int x = 0; x < Constants.BOARD_SIZE; ++x) {
                if (getWhiteKing().equals(chessboard[i][x])) {
                    getWhiteKing().setFirstCoordinates(i, x);
                } else if (getBlackKing().equals(chessboard[i][x])) {
                    getBlackKing().setFirstCoordinates(i, x);
                } else if (whitePieces[3].equals(chessboard[i][x])) {
                    getWhiteKing().setRookCoordinates(i, x);
                } else if (blackPieces[3].equals(chessboard[i][x])) {
                    getBlackKing().setRookCoordinates(i, x);
                }
            }
        }
        if (getWhiteKing().getLRC() == -1) {
            getWhiteKing().setRookCoordinates(0, 0);
        }
        if (getWhiteKing().getRRC() == -1) {
            getWhiteKing().setRookCoordinates(0, 0);
        }
        if (getBlackKing().getLRC() == -1) {
            getBlackKing().setRookCoordinates(0, 0);
        }
        if (getBlackKing().getRRC() == -1) {
            getBlackKing().setRookCoordinates(0, 0);
        }
    }
}
