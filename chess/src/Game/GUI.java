package Game;

import Communication.Client;
import Communication.Engine;
import Communication.Server;
import Communication.DataParsing;
import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Implements graphical looks of game
 *
 * @author vavra
 */
public class GUI extends JFrame {

    public LogicBoard logicBoard;
    private JButton[][] buttons;
    private JLabel[][] lostPieces;
    private Container container;
    private JLabel whoWins;
    private Timer timer;
    private int PieceRow;
    private int PieceCol;
    private int clickedTwoTimes;
    private TextArea[] moves;
    private JPanel[] movesAndPromotion;
    private JPanel[] promotion;
    private JButton[][] promotingChoice;
    private boolean allowedMove;
    private int whiteGUI;
    private boolean twoInOne;
    private JPanel chessboard;
    private EngineThread engineThread;
    private boolean engineCanMove;
    private JLabel[][] rowsAndCols;
    private Server server;
    private Client client;
    private String gameType; // important for client-server (Fischer/Normal)
    private String time; // also important only for client-server game (hoursMinutesSecondsIncreaseByMove)
    private JPanel[][] gameStateGraphic;
    private JLabel[][] whyWasItDrawOrMate;
    private JPanel[] drawOffers;
    private boolean drawOffered;

    /**
     * Creates GUI of one player
     *
     * @param hour number of hours to 0
     * @param minute number of minutes to 0
     * @param second number of seconds to 0
     * @param increaseByMove number of seconds those are added to clock when
     * player moves
     * @param whiteGUI true if GUI belongs to white, otherwise it's false
     * @param opponentType 0 = engine, 1 = client + server on one PC, 2 =
     * client, 3 = 2 players on one computer
     * @param port port for client - server communication
     * @param IP IP of computer, where server is running
     * @param engineDifficulty difficulty of engine (0(the weakest) - 20(the
     * strongest))
     * @param gameType possible chessboard setting(Normal, Fischer)
     */
    public GUI(int hour, int minute, int second, int increaseByMove, boolean whiteGUI, int opponentType, int port, String IP, int engineDifficulty, String gameType) {
        this.whiteGUI = whiteGUI ? 0 : Constants.BOARD_SIZE - 1;
        time = hour + "" + minute + "" + second + "" + increaseByMove;
        logicBoard = new LogicBoard(gameType);
        this.gameType = gameType;
        engineCanMove = false;
        twoInOne = false;
        timer = new Timer(hour, minute, second, this, increaseByMove);
        allowedMove = true;
        initComponents();
        if (opponentType != 1 && opponentType != 2) {
            startTimer();
        }
        switch (opponentType) {
            case 0:
                if (!whiteGUI) {
                    engineCanMove = true;
                }
                engineThread = new EngineThread(engineDifficulty);
                engineThread.start();
                break;
            case 1:
                initClientServer(true, IP, port);
                break;
            case 2:
                initClientServer(false, IP, port);
                break;
            case 3:
                twoInOne = true;
                break;
        }
    }

    public void startTimer() {
        timer.start();
    }

    /**
     * switch active timer to non-active and non-active to active
     */
    public void playerChange() {
        timer.playerChange();
    }

    private void initClientServer(boolean withServer, String IP, int port) {
        if (withServer) {
            try {
                server = new Server(25565);
                client = new Client("localhost", port, this);
            } catch (IOException ex) {
            }
        } else {
            try {
                client = new Client(IP, port, this);
            } catch (IOException ex) {
            }
        }
    }

    private void sendToClient(int row, int col, int firstRow, int firstCol, int promoting, boolean castling) {
        if (client != null && logicBoard.whiteMoves() != (whiteGUI == 0)) {
            client.sendToClientHandler(DataParsing.outMove(row, col, firstRow, firstCol, promoting, castling, logicBoard.whiteMoves()) + "synchronize" + getTimer().getTime(isWhite()));
        }
    }

    private void sendToClient(String move) {
        if (client != null) {
            client.sendToClientHandler(move);
        }
    }

    public Timer getTimer() {
        return timer;
    }

    private class EngineThread extends Thread {

        private Engine stockfish;

        /**
         * Creates chess engine object
         *
         * @param difficulty Skill of chess engine from 0 to 20
         */
        public EngineThread(int difficulty) {
            try {
                this.stockfish = new Engine(difficulty);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(200);
                    if (whiteGUI == 0 != logicBoard.whiteMoves() && engineCanMove) {
                        DataParsing.inMove(stockfish.sendFen(DataParsing.outFen(logicBoard)), logicBoard.getChessboard(), GUI.this);
                        engineCanMove = false;
                    }
                } catch (IOException /* | InterruptedException*/ e) {
                    System.err.println("Something bad had happened :D");
                } catch (InterruptedException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     *
     * @return true if this GUI belongs to white, otherwise false
     */
    public boolean isWhite() {
        return whiteGUI == 0;
    }

    public String getGameType() {
        return gameType;
    }

    public String getTime() {
        return time;
    }

    public boolean isAllowedMove() {
        return allowedMove;
    }

    public void setDrawOfferedTrue() {
        drawOffered = true;
    }

    private void initComponents() {
        lostPieces();
        initFrame();
        container();
    }

    /**
     * implements the move in graphical way and send it to logicBoard
     *
     * @param row row where piece went
     * @param col col where piece went
     * @param firstRow row where piece came from
     * @param firstCol col where piece came from
     * @param type 0 = normal, 4-7 promotions from engine 4 = queen, 5 = rook, 6
     * = Bishop, 7 = Knight, 8 = en passant
     */
    public void setPiecePosition(int row, int col, int firstRow, int firstCol, int type) {
        if (type >= 4 && type <= 7) {
            promotion(type - 4);
            return;
        }
        if (isCastling(row, col, firstRow, firstCol)) {
            return;
        }
        writeKilledPiece(logicBoard.getChessboard(row, col));
        buttons[row][col].setIcon(null);
        buttons[row][col].setIcon(buttons[firstRow][firstCol].getIcon());
        buttons[firstRow][firstCol].setIcon(null);
        if ((firstRow + firstCol) % 2 == 0) {
            buttons[firstRow][firstCol].setBackground(Constants.WHITE);
        } else {
            buttons[firstRow][firstCol].setBackground(Constants.DARK);
        }
        boolean isCapturing = false;
        if (logicBoard.getChessboard(row, col) != null) {
            isCapturing = true;
        }
        logicBoard.setChessboard(row, col, firstRow, firstCol, null);
        leadingScore();
        makeRedCheck();
        promotionEnPassantTest(row, col, firstRow, firstCol, type, isCapturing);
        if (allowedMove) {
            engineCanMove = true; // tell engine that it can move 
            if (type != 8) {
                timer.playerChange();
                rotateBoard();
            }
        }
        sendToClient(row, col, firstRow, firstCol, 0, false);
        clickedTwoTimes = 2;
        showDrawMate(logicBoard.getWhiteKing().check.gameState());
        drawOffer();
    }

    private boolean isCastling(int row, int col, int firstRow, int firstCol) {
        if (logicBoard.getChessboard(firstRow, firstCol).getVALUE() == 90) {
            King k = (King) logicBoard.getChessboard(firstRow, firstCol);
            if (k.getLRC() == row * Constants.BOARD_SIZE + col && k.canLeftCastling()) {
                castling(k.getLCC() / Constants.BOARD_SIZE, k.getLCC() % Constants.BOARD_SIZE, firstRow, firstCol,
                        k.getLCC() / Constants.BOARD_SIZE, k.getLCC() % Constants.BOARD_SIZE + 1, k.getLRC() / Constants.BOARD_SIZE, k.getLRC() % Constants.BOARD_SIZE, true);
                if (gameType.equals("Normal")) {
                    sendToClient(row, col, firstRow, firstCol, 0, true);
                } else {
                    sendToClient(DataParsing.outFen(logicBoard));
                }
                return true;
            } else if (k.getRRC() == row * Constants.BOARD_SIZE + col && k.canRightCastling()) {
                castling(k.getRCC() / Constants.BOARD_SIZE, k.getRCC() % Constants.BOARD_SIZE, firstRow, firstCol,
                        k.getRCC() / Constants.BOARD_SIZE, k.getRCC() % Constants.BOARD_SIZE - 1, k.getRRC() / Constants.BOARD_SIZE, k.getRRC() % Constants.BOARD_SIZE, false);
                if (gameType.equals("Normal")) {
                    sendToClient(row, col, firstRow, firstCol, 0, true);
                } else {
                    sendToClient(DataParsing.outFen(logicBoard));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * method that helps setPiecePosition method
     *
     * @param row row where is piece now
     * @param col col where is piece now
     * @param firstRow row where piece came from
     * @param firstCol col where piece came from
     * @param type special type of move described in javadoc of method
     * setPiecePosition
     * @param isCapturing true if there was another piece on row, col coordinate
     * before this piece
     */
    private void promotionEnPassantTest(int row, int col, int firstRow, int firstCol, int type, boolean isCapturing) {
        if (logicBoard.isEnPassant(row, col, firstRow, firstCol)) {
            writeMove(row, col, firstRow, firstCol, logicBoard.whiteMoves(), true, 0);
            setPiecePosition(firstRow, col, firstRow, col, 8);
        } else if (logicBoard.isPromotion(row, logicBoard.getChessboard(row, col))) {
            if (logicBoard.whiteMoves() != (whiteGUI == 0)) {
                int i = 0;
                logicBoard.setLastMove(row, col, firstCol, firstRow, logicBoard.whiteMoves(), isCapturing, 0);
                if (logicBoard.whiteMoves()) {
                    i = 1;
                }
                movesAndPromotion[i].remove(moves[i]);
                movesAndPromotion[i].add(promotion[i], gbcOne());
            }
            allowedMove = false;
        } else if (type != 8) {
            writeMove(row, col, firstRow, firstCol, logicBoard.whiteMoves(), isCapturing, 0);
        }
    }

    /**
     * creates new chessboard and reseting sidebars etc.. This is also used when
     * player wants to take move back against engine
     *
     * @param FEN type of chess notation
     */
    public void newChessboard(String FEN) {
        int[][] numberOfPieces = new int[2][5];
        int white;
        makeButtonDefaultColor(logicBoard.getKingRow(true), logicBoard.getKingCol(true));
        repaint();
        revalidate();
        DataParsing.inFen(FEN, logicBoard);
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                buttons[i][j].setIcon(null);
            }
        }
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                for (int x = 0; x < 5; x++) {
                    if (logicBoard.getChessboard(i, j) != null && logicBoard.getPiece(x, logicBoard.getChessboard(i, j).isWhite()).equals(logicBoard.getChessboard(i, j))) {
                        white = logicBoard.getChessboard(i, j).isWhite() ? 0 : 1;
                        numberOfPieces[white][x]++;
                    }
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                lostPieces[i][j].setText("0");
                numberOfPieces[i][j] = Constants.PIECE_COUNT[i][j] - numberOfPieces[i][j];
            }
        }
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                if (logicBoard.getChessboard(i, j) != null) {
                    buttons[i][j].setIcon(scaledIcon(logicBoard.getChessboard(i, j).getPiece().getImage(), 100, 100));
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                boolean w = i == 0;
                if (numberOfPieces[i][j]-- != 0) {
                    writeKilledPiece(logicBoard.getPiece(j, w));
                }
            }
        }
        logicBoard.entireMaterial();
        leadingScore();
        for (int i = 0; i < 2; i++) {
            BufferedReader br = new BufferedReader(new StringReader(moves[i].getText()));
            String line;
            String text = "";
            double x = 0.5;
            if (!logicBoard.whiteMoves() && i == 0) {
                x = 1;
            }
            try {
                while ((line = br.readLine()) != null && getNumberFromString(line) < Math.ceil(logicBoard.getMoveNumber() + x)) {
                    text += line + Constants.NEW_LINE;
                }
                text = text.substring(0, text.length() - 1);
            } catch (IOException ex) {

            } catch (StringIndexOutOfBoundsException ex) {
                text = "";
            }
            moves[i].setText(text);
        }
        makeButtonDefaultColor(logicBoard.getKingRow(true), logicBoard.getKingCol(true));
        allowedMove = true;
        movesAndPromotion[0].removeAll();
        movesAndPromotion[1].removeAll();
        movesAndPromotion[0].add(moves[0], gbcOne());
        movesAndPromotion[1].add(moves[1], gbcOne());
    }

    private int getNumberFromString(String line) {
        try {
            return Integer.parseInt(line.substring(0, 3));
        } catch (Exception e) {
            try {
                return Integer.parseInt(line.substring(0, 2));
            } catch (Exception x) {
                return Integer.parseInt(line.substring(0, 1));
            }
        }
    }

    /**
     * If game is drawn or won, this method changes 2 JPanels with moves to
     * JPanels with game result
     *
     * @param gameState
     */
    public void showDrawMate(int gameState) {
        int index = -1;
        switch (gameState) {
            case 0:
                return;
            case 1:
                index = 0;
                break;
            case 2:
                index = 2;
                break;
            case 3:
                index = 1;
                break;
        }
        allowedMove = false;
        for (int i = 0; i < 2; i++) {
            movesAndPromotion[i].removeAll();
            movesAndPromotion[i].add(gameStateGraphic[i][index], gbcOne());
            for (int x = 0; x < 3; x++) {
                whyWasItDrawOrMate[i][x].setText(logicBoard.getWhiteKing().check.why());
            }
        }
        revalidate();
        repaint();
    }

    private void castling(int KRow, int KCol, int KFirstRow, int KFirstCol, int RRow, int RCol, int RFirstRow, int RFirstCol, boolean left) {
        Icon King = buttons[KFirstRow][KFirstCol].getIcon();
        Icon Rook = buttons[RFirstRow][RFirstCol].getIcon();
        buttons[KFirstRow][KFirstCol].setIcon(null);
        buttons[RFirstRow][RFirstCol].setIcon(null);
        buttons[KRow][KCol].setIcon(King);
        buttons[RRow][RCol].setIcon(Rook);
        logicBoard.castling(KRow, KCol, KFirstRow, KFirstCol, RRow, RCol, RFirstRow, RFirstCol);
        makeRedCheck();
        int i = 1;
        if (left) {
            i = 2;
        }
        writeMove(KRow, KCol, KFirstRow, KFirstCol, logicBoard.whiteMoves(), false, i);
        engineCanMove = true;
        timer.playerChange();
        showDrawMate(logicBoard.getWhiteKing().check.gameState());
        drawOffer();
        rotateBoard();
    }

    /**
     * implements promotion in graphical way
     *
     * @param promoting if engine promotes it sends some values described in
     * javadoc of setPiecePosition method
     */
    public void promotion(int promoting) {
        int row;
        int previousRow;
        if (logicBoard.whiteMoves()) {
            row = 7;
            previousRow = 6;
        } else {
            row = 0;
            previousRow = 1;
        }
        for (int y = 0; y < Constants.BOARD_SIZE; y++) {
            if (logicBoard.getChessboard(row, y) != null && logicBoard.getChessboard(row, y).getVALUE() == 1) {
                Piece piece = new Queen(!logicBoard.whiteMoves(), true);
                if (promoting == 1) {
                    piece = new Rook(!logicBoard.whiteMoves(), true);
                } else if (promoting == 2) {
                    piece = new Bishop(!logicBoard.whiteMoves(), true);
                } else if (promoting == 3) {
                    piece = new Knight(!logicBoard.whiteMoves(), true);
                }
                logicBoard.setChessboard(row, y, row, y, piece);
                rotateBoard();
                buttons[row][y].setIcon(scaledIcon(piece.getPiece().getImage(), 100, 100));
                logicBoard.halfIncrementMoveNumber();
                writeMove(row, y, previousRow, y, logicBoard.whiteMoves(), false, 3);
            }
        }
        writeKilledPiece(logicBoard.getPiece(0, logicBoard.whiteMoves()));
        leadingScore();
        makeRedCheck();
        allowedMove = true;
        sendToClient(0, 0, 0, 0, promoting + 4, false);
        timer.playerChange();
        showDrawMate(logicBoard.getWhiteKing().check.gameState());
        drawOffer();
        engineCanMove = true;
    }

    private void writeKilledPiece(Piece piece) {
        if (piece == null) {
            return;
        }
        int white;
        boolean isWhite;
        if (piece.isWhite()) {
            white = 0;
            isWhite = true;
        } else {
            white = 1;
            isWhite = false;
        }
        for (int i = 0; i < 5; i++) {
            if (piece.getRealValue() == 1) {
                lostPieces[white][0].setText(String.valueOf(Integer.parseInt(lostPieces[white][0].getText()) + 1));
                return;
            } else if (piece.equals(logicBoard.getPiece(i, isWhite))) {
                lostPieces[white][i].setText(String.valueOf(Integer.parseInt(lostPieces[white][i].getText()) + 1));
                return;
            }
        }
    }

    /**
     *
     * @param row row where is piece now
     * @param col col where is piece now
     * @param previousRow row where piece came from
     * @param previousCol col where piece came from
     * @param whitePlays true if white is on the move, otherwise false
     * @param isCapturing true if last move was captured a piece
     * @param specialities 0 = nothing special, 1 = King side castling, 2 =
     * Queen side castling, 3 = promotion, 6 = en passant from engine
     */
    public void writeMove(int row, int col, int previousRow, int previousCol, boolean whitePlays, boolean isCapturing, int specialities) {
        byte i = 0;
        if (whitePlays) {
            i = 1;
        }
        logicBoard.setLastMove(row, col, previousCol, previousRow, whitePlays, isCapturing, specialities);
        moves[i].setText(moves[i].getText() + (!moves[i].getText().equals("") ? Constants.NEW_LINE : "") + logicBoard.getLastMove());
    }

    private void leadingScore() {
        if (logicBoard.getWhiteLostMaterial() > logicBoard.getBlackLostMaterial()) {
            int vedeCerny = logicBoard.getWhiteLostMaterial() - logicBoard.getBlackLostMaterial();
            this.whoWins.setText(Integer.toString(vedeCerny));
            this.whoWins.setForeground(Color.BLACK);
            this.whoWins.setVisible(true);
        } else if (logicBoard.getWhiteLostMaterial() == logicBoard.getBlackLostMaterial()) {
            this.whoWins.setVisible(false);
        } else {
            int vedeBily = logicBoard.getBlackLostMaterial() - logicBoard.getWhiteLostMaterial();
            this.whoWins.setForeground(Color.WHITE);
            this.whoWins.setText(Integer.toString(vedeBily));
            this.whoWins.setVisible(true);
        }
    }

    /**
     * creates main container with all components
     */
    private void container() {
        container = new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1.0;
        g.weighty = 1.0;
        g.fill = GridBagConstraints.BOTH;
        container.add(lostPieces(), g);

        g.gridx = 1;
        g.weightx = 8.0;
        container.add(chessboard(), g);

        g.gridx = 2;
        g.weightx = 3.0;
        container.add(rightSide(), g);
        container.setBackground(Color.BLACK);
        getContentPane().add(container);
    }

    private JPanel chessboard() {
        chessboard = new JPanel();
        Border border = BorderFactory.createLineBorder(Color.black, 10);
        chessboard.setBorder(border);
        chessboard.setLayout(new GridLayout(8, 8, 2, 2));
        chessboard.setBackground(Color.black);
        chessboard.setPreferredSize(chessboard.getSize());
        createButtons();
        for (int y = 0; y < Constants.BOARD_SIZE; ++y) {
            for (int x = 0; x < Constants.BOARD_SIZE; ++x) {
                chessboard.add(buttons[Math.abs(whiteGUI - y)][Math.abs(whiteGUI - x)]);
                if (logicBoard.getChessboard(y, x) != null) {
                    buttons[y][x].setIcon(scaledIcon(logicBoard.getChessboard(y, x).getPiece().getImage(), 100, 100));
                }
                buttons[y][x].setLayout(new GridBagLayout());
                buttons[y][x].setMargin(new Insets(0, 0, 0, 0));
            }
        }
        rowColNumbers();
        return chessboard;
    }

    /**
     * rotates the board by 180° when 2 players play on one computer
     */
    private void rotateBoard() {
        if (twoInOne) {
            whiteGUI = logicBoard.whiteMoves() ? 0 : Constants.BOARD_SIZE - 1;
            for (int y = Constants.BOARD_SIZE - 1; y >= 0; --y) {
                for (int x = Constants.BOARD_SIZE - 1; x >= 0; --x) {
                    chessboard.remove(buttons[Math.abs(whiteGUI - y)][Math.abs(whiteGUI - x)]);
                }
            }
            for (int y = 0; y < Constants.BOARD_SIZE; ++y) {
                for (int x = 0; x < Constants.BOARD_SIZE; ++x) {
                    chessboard.add(buttons[Math.abs(whiteGUI - y)][Math.abs(whiteGUI - x)]);
                }
            }
            for (int i = 0; i < Constants.BOARD_SIZE; i++) {
                if (rowsAndCols[2][i].isVisible()) {
                    rowsAndCols[0][i].setVisible(true);
                    rowsAndCols[1][i].setVisible(true);
                    rowsAndCols[2][i].setVisible(false);
                    rowsAndCols[3][i].setVisible(false);
                } else {
                    rowsAndCols[0][i].setVisible(false);
                    rowsAndCols[1][i].setVisible(false);
                    rowsAndCols[2][i].setVisible(true);
                    rowsAndCols[3][i].setVisible(true);
                }
            }
        }
    }

    /**
     * analyzes mouse clicks and sends moves to setPiecePosition method
     */
    private ActionListener movingPieces() {
        return (ActionEvent e) -> {
            if (allowedMove) {
                for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
                    for (int d = 0; d < Constants.BOARD_SIZE; ++d) {
                        if (buttons[i][d].equals(e.getSource()) && logicBoard.whiteMoves() == (whiteGUI == 0)) {
                            //hold = true;
                            for (int p = 0; p < logicBoard.getPossibleMoves().size(); ++p) {
                                buttons[logicBoard.getPossibleMoves().get(p) / Constants.BOARD_SIZE][logicBoard.getPossibleMoves().get(p) % Constants.BOARD_SIZE].setText("");
                            }
                            makeButtonDefaultColor(PieceRow, PieceCol);
                            if (logicBoard.getPossibleMoves().contains(i * Constants.BOARD_SIZE + d) && clickedTwoTimes != 2) {
                                setPiecePosition(i, d, PieceRow, PieceCol, 0);
                            }
                            if ((i != PieceRow || d != PieceCol || clickedTwoTimes % 2 == 0)/*hold*/) {
                                logicBoard.moveReduction(i, d);
                                logicBoard.getPossibleMoves();
                                clickedTwoTimes = 1;
                                if (logicBoard.getChessboard(i, d) != null && logicBoard.whiteMoves() == logicBoard.getChessboard(i, d).isWhite()) {
                                    if ((i + d) % 2 == 0) {
                                        buttons[i][d].setBackground(Constants.WHITE_CHANGED);
                                    } else if (logicBoard.getChessboard(i, d) != null) {
                                        buttons[i][d].setBackground(Constants.DARK_CHANGED);
                                    }
                                }
                                for (int s = 0; s < logicBoard.getPossibleMoves().size(); ++s) {
                                    buttons[logicBoard.getPossibleMoves().get(s) / Constants.BOARD_SIZE][logicBoard.getPossibleMoves().get(s) % Constants.BOARD_SIZE].setText(Constants.CIRCLE);
                                }
                            } else {
                                clickedTwoTimes++;
                            }
                            PieceRow = i; //row where player clicked before this click
                            PieceCol = d; // col where player clicked before this click
                        }
                    }
                }
            }
        };
    }

    private void makeButtonDefaultColor(int row, int col) {
        if ((row + col) % 2 == 0) {
            buttons[row][col].setBackground(Constants.WHITE);
        } else {
            buttons[row][col].setBackground(Constants.DARK);
        }
        if (logicBoard.isCheck()) {
            makeRedCheck();
        }
    }

    private void makeRedCheck() {
        if (logicBoard.isCheck()) {
            buttons[logicBoard.getKingRow(true)][logicBoard.getKingCol(true)].setBackground(Color.red);
        } else {
            makeButtonDefaultColor(logicBoard.getKingRow(false), logicBoard.getKingCol(false));
        }
    }

    private void createButtons() {
        buttons = new JButton[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        for (int y = 0; y < Constants.BOARD_SIZE; ++y) {
            for (int x = 0; x < Constants.BOARD_SIZE; ++x) {
                JButton button = new JButton("");
                if ((y + x) % 2 == 0) {
                    button.setBackground(Constants.WHITE);
                } else {
                    button.setBackground(Constants.DARK);
                }
                button.setHorizontalTextPosition(JButton.CENTER);
                button.setVerticalTextPosition(JButton.CENTER);
                button.setFont(Constants.CIRCLE_FONT);
                button.addActionListener(movingPieces());
                buttons[y][x] = button;
            }
        }
    }

    private void rowColNumbers() {
        GridBagConstraints gbcNumbers = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(
                        0, 0, 0, 0), 0, 0);
        rowsAndCols = new JLabel[4][Constants.BOARD_SIZE];
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            JLabel letter = new JLabel(String.valueOf((char) ('\u0061' + i)));
            JLabel letter2 = new JLabel(String.valueOf((char) ('\u0061' + i)));
            rowsAndCols[0][i] = letter;
            rowsAndCols[2][i] = letter2;
            JLabel number = new JLabel(String.valueOf(i + 1));
            JLabel number2 = new JLabel(String.valueOf(i + 1));
            rowsAndCols[1][i] = number;
            rowsAndCols[3][i] = number2;
            if (buttons[i][whiteGUI].getBackground().equals(Constants.DARK)) {
                letter.setForeground(Constants.DARK);
                number.setForeground(Constants.DARK);
                letter2.setForeground(Constants.WHITE);
                number2.setForeground(Constants.WHITE);
            } else {
                letter.setForeground(Constants.WHITE);
                number.setForeground(Constants.WHITE);
                letter2.setForeground(Constants.DARK);
                number2.setForeground(Constants.DARK);
            }
            rowsAndCols[2][i].setVisible(false);
            rowsAndCols[3][i].setVisible(false);
            buttons[Constants.BOARD_SIZE - 1 - i][whiteGUI].add(rowsAndCols[1][i], gbcNumbers);
            buttons[Constants.BOARD_SIZE - 1 - i][Constants.BOARD_SIZE - 1 - whiteGUI].add(rowsAndCols[3][i], gbcNumbers);
            buttons[Math.abs(whiteGUI - (Constants.BOARD_SIZE - 1))][i].add(rowsAndCols[0][i], gbcDown());
            buttons[whiteGUI][i].add(rowsAndCols[2][i], gbcDown());
        }
    }

    private JPanel lostPieces() {
        JPanel LostPieces = new JPanel();
        LostPieces.setLayout(new GridBagLayout());
        LostPieces.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel[] panels = reallyLostPieces();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        gbc.weighty = 5.0;
        LostPieces.add(panels[0], gbc);
        gbc.gridy = 1;
        gbc.weighty = 1.4;
        LostPieces.add(whoWins(), gbc);
        gbc.gridy = 2;
        gbc.weighty = 5.0;
        LostPieces.add(panels[1], gbc);
        return LostPieces;
    }

    private JPanel[] reallyLostPieces() {
        JPanel[] lostPieces = new JPanel[2];
        boolean white = true;
        JLabel[][] iconsLostPieces = new JLabel[2][5];
        this.lostPieces = new JLabel[2][5];
        Color backgroundColor;
        for (int i = 0; i < 2; i++) {
            if (white) {
                backgroundColor = Color.WHITE.darker();
            } else {
                backgroundColor = Color.WHITE.darker().darker().darker();
            }
            lostPieces[i] = new JPanel();
            lostPieces[i].setBackground(backgroundColor);
            lostPieces[i].setLayout(new GridLayout(5, 1, 2, 2));
            lostPieces[i].setPreferredSize(lostPieces[i].getSize());
            for (int x = 0; x < 5; x++) {
                iconsLostPieces[i][x] = new JLabel();
                this.lostPieces[i][x] = new JLabel("0");
            }
            for (int x = 0; x < 5; x++) {
                this.lostPieces[i][x].setFont(Constants.LOST_PIECES_FONT);
                this.lostPieces[i][x].setForeground(Color.WHITE);
                iconsLostPieces[i][x].setHorizontalAlignment(JLabel.CENTER);
                iconsLostPieces[i][x].setVerticalAlignment(JLabel.CENTER);
                iconsLostPieces[i][x].setLayout(new GridBagLayout());
                iconsLostPieces[i][x].add(this.lostPieces[i][x], gbcDown());
                lostPieces[i].add(iconsLostPieces[i][4 - x]);
                iconsLostPieces[i][x].setIcon(scaledIcon(logicBoard.getPiece(x, white).getPiece().getImage(), 70, 70));
            }
            white = false;
        }
        return lostPieces;
    }

    private JPanel whoWins() {
        this.whoWins = new JLabel();
        JPanel whoWins = new JPanel();
        whoWins.setLayout(new GridBagLayout());
        this.whoWins.setFont(Constants.LEADING_PLAYER);
        whoWins.add(this.whoWins);
        whoWins.setBackground(Color.GRAY);
        whoWins.setPreferredSize(whoWins.getPreferredSize());
        leadingScore();
        return whoWins;
    }

    private ImageIcon scaledIcon(Image img, int height, int width) {
        Image newImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    private GridBagConstraints gbcDown() {
        return new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    }

    private GridBagConstraints gbcOne() {
        return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
    }

    private JPanel rightSide() {
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        movesAndPromotion();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        rightSide.add(timer.getBlackTimer(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 3.5;
        rightSide.add(movesAndPromotion[1], gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        rightSide.add(drawSurrender(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 3.5;
        rightSide.add(movesAndPromotion[0], gbc);

        gbc.weighty = 1.0;
        gbc.gridy = 4;
        rightSide.add(timer.getWhiteTimer(), gbc);

        rightSide.setPreferredSize(rightSide.getPreferredSize());
        return rightSide;
    }

    private void movesAndPromotion() {
        moves = new TextArea[2];
        movesAndPromotion = new JPanel[2];
        promotion = new JPanel[2];
        promotingChoice = new JButton[2][4];
        boolean white = true;
        for (int i = 0; i < moves.length; i++) {
            moves[i] = new TextArea();
            if (i == 1) {
                white = false;
            }
            promotion[i] = new JPanel();
            movesAndPromotion[i] = new JPanel();
            movesAndPromotion[i].setLayout(new GridBagLayout());
            promotion[i].setPreferredSize(new Dimension(0, 0));
            promotion[i].setBackground(Color.BLACK);
            promotion[i].setLayout(new GridLayout(2, 2, 2, 2));
            for (int x = 0; x < 4; x++) {
                promotingChoice[i][x] = new JButton();
                promotingChoice[i][x].setBackground(Constants.BACKGROUND_COLOR);
                promotingChoice[i][x].setIcon(scaledIcon(logicBoard.getPiece(4 - x, white).getPiece().getImage(), 110, 110));
                promotingChoice[i][x].addActionListener((ActionEvent e) -> {
                    for (int d = 0; d < promotingChoice.length; d++) {
                        for (int f = 0; f < promotingChoice[0].length; f++) {
                            if (promotingChoice[d][f].equals(e.getSource())) {
                                movesAndPromotion[d].remove(promotion[d]);
                                movesAndPromotion[d].add(moves[d], gbcOne());
                                promotion(f);
                            }
                        }
                    }
                });
                promotion[i].add(promotingChoice[i][x]);
            }
            moves[i].setBackground(Constants.BACKGROUND_COLOR);
            if (i == 0) {
                moves[i].setForeground(Color.WHITE);
            } else {
                moves[i].setForeground(Color.BLACK);
            }
            moves[i].setPreferredSize(new Dimension(0, 0));
            movesAndPromotion[i].add(moves[i], gbcOne());
        }
    }

    private void gameStateGraphic() {
        gameStateGraphic = new JPanel[2][3];
        whyWasItDrawOrMate = new JLabel[2][3];
        JLabel[][] labels = new JLabel[2][3];
        Color color = null;
        Color foreground = null;
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        for (int x = 0; x < 2; x++) {
            for (int i = 0; i < gameStateGraphic[0].length; i++) {
                if (i == 0) {
                    color = Color.WHITE;
                    foreground = Color.BLACK;
                } else if (i == 1) {
                    color = Color.GRAY.brighter();
                    foreground = Color.GRAY.darker();
                } else if (i == 2) {
                    color = Color.BLACK;
                    foreground = Color.WHITE;
                }
                labels[x][i] = new JLabel();
                labels[x][i].setFont(new Font("Calibri", Font.BOLD, 40));
                gameStateGraphic[x][i] = new JPanel();
                gameStateGraphic[x][i].setBackground(color);
                labels[x][i].setForeground(foreground);
                labels[x][i].setBackground(color);
                gameStateGraphic[x][i].setLayout(new GridBagLayout());
                gameStateGraphic[x][i].setPreferredSize(this.getPreferredSize());
                whyWasItDrawOrMate[x][i] = new JLabel();
                whyWasItDrawOrMate[x][i].setFont(new Font("Calibri", Font.PLAIN, 20));
                whyWasItDrawOrMate[x][i].setForeground(foreground);
                gbc.gridy = 1;
                gameStateGraphic[x][i].add(whyWasItDrawOrMate[x][i], gbc);
            }
            labels[x][0].setText("WHITE WON");
            labels[x][1].setText("DRAW");
            labels[x][2].setText("BLACK WON");
            gbc.gridy = 0;
            gameStateGraphic[x][0].add(labels[x][0], gbc);
            gameStateGraphic[x][1].add(labels[x][1], gbc);
            gameStateGraphic[x][2].add(labels[x][2], gbc);
        }
    }

    private void initDrawOffer() {
        drawOffers = new JPanel[2];
        JButton[] acceptDeny = new JButton[2];
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        for (int i = 0; i < drawOffers.length; i++) {
            drawOffers[i] = new JPanel();
            drawOffers[i].setLayout(new GridBagLayout());
            acceptDeny[i] = new JButton();
            acceptDeny[i].setFont(new Font("Calibri", Font.PLAIN, 30));
            drawOffers[i].add(acceptDeny[i], gbc);
            drawOffers[i].setPreferredSize(this.getPreferredSize());
        }
        acceptDeny[0].setText(acceptDeny[0].getText() + "ACCEPT DRAW");
        acceptDeny[0].setBackground(Color.GREEN);
        acceptDeny[0].addActionListener((ActionEvent e) -> {
            logicBoard.getWhiteKing().check.setWhy("by agreement");
            showDrawMate(3);
            client.sendToClientHandler("acceptedDraw");
        });
        acceptDeny[1].setText(acceptDeny[1].getText() + "DENY DRAW");
        acceptDeny[1].setBackground(Color.RED);
        acceptDeny[1].addActionListener((ActionEvent e) -> {
            for (int i = 0; i < movesAndPromotion.length; i++) {
                movesAndPromotion[i].removeAll();
                movesAndPromotion[i].add(moves[i], gbcOne());
                drawOffered = false;
            }
        });
    }

    /**
     * this method is called when opponent offered a draw
     */
    public void drawOffer() {
        if (drawOffered) {
            for (int i = 0; i < movesAndPromotion.length; i++) {
                movesAndPromotion[i].removeAll();
                movesAndPromotion[i].add(drawOffers[i], gbcOne());
            }
        }
    }

    private JPanel drawSurrender() {
        gameStateGraphic();
        initDrawOffer();
        JPanel drawSurrender = new JPanel();
        drawSurrender.setPreferredSize(new Dimension(0, 0));
        drawSurrender.setLayout(new GridLayout(1, 2));

        JButton declareDraw = new JButton();
        declareDraw.setBackground(Color.WHITE);
        declareDraw.setIcon(new ImageIcon(this.getClass().getResource("/PieceImages/draw.png")));
        declareDraw.addActionListener((ActionEvent e) -> {
            if (allowedMove) {
                if (client == null) {
                    drawOffered = true;
                } else {
                    sendToClient("drawOffer");
                }
            }
        });
        drawSurrender.add(declareDraw);

        JButton back = new JButton();
        back.setBackground(Color.WHITE);
        back.setIcon(scaledIcon(new ImageIcon(this.getClass().getResource("/PieceImages/back.png")).getImage(), 40, 40));
        back.addActionListener((ActionEvent e) -> {
            if (engineThread != null && (logicBoard.getMoveNumber() > 1 || (logicBoard.whiteMoves() && logicBoard.getMoveNumber() == 1)) && timer.getTime(!logicBoard.whiteMoves()) > 0) {
                newChessboard(logicBoard.getLastFen());
            }
        });
        drawSurrender.add(back);
        JButton surrender = new JButton();
        surrender.setBackground(Color.WHITE);
        surrender.setIcon(scaledIcon(new ImageIcon(this.getClass().getResource("/PieceImages/surrender.png")).getImage(), 58, 51));
        surrender.addActionListener((ActionEvent e) -> {
            surrender();
        });
        drawSurrender.add(surrender);
        return drawSurrender;
    }

    /**
     * this method is called when player that is on the move resigned
     */
    public void surrender() {
        if (allowedMove) {
            logicBoard.getWhiteKing().check.setWhy("opponent resigned");
            showDrawMate(!logicBoard.whiteMoves() ? 1 : 2);
            sendToClient("surrender");
        }
    }

    private void initFrame() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(Constants.FRAME_MIN_WIDTH, Constants.FRAME_MIN_HEIGHT));
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        setTitle("chess");
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width;
                int height;
                if (getHeight() * 1.5 > getWidth()) {
                    width = getWidth();
                    height = (int) (getWidth() * 0.666);
                } else {
                    width = (int) (getHeight() * 1.5);
                    height = (int) getHeight();
                }
                container.setBounds((getWidth() - width) / 2 - 7, (getHeight() - height) / 2, width - 7, height - 45);
                revalidate();
            }
        });
    }
}
