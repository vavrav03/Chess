package Game;

import Pieces.Bishop;
import Pieces.Knight;
import java.awt.Color;
import java.awt.Font;

/**
 * Contains all static Constants used in other classes
 *
 * @author vavra
 */
public class Constants {

    /**
     * Color of dark square which was clicked
     */
    public static final Color DARK_CHANGED = new Color(193, 153, 3);

    /**
     * Color of white square which was clicked
     */
    public static final Color WHITE_CHANGED = new Color(217, 210, 79);

    /**
     * Default color of dark square
     */
    public static final Color DARK = new Color(128, 64, 16).brighter();

    /**
     * Default color of white square
     */
    public static final Color WHITE = new Color(255, 223, 191);

    /**
     * Size of chessboard I.E. piece array
     */
    public static final byte BOARD_SIZE = 8;

    /**
     * HTML styled unicode character stored in String that appears when player
     * click on piece. It represents where piece can go
     */
    public static final String CIRCLE = "<html><p style =\"color:gray\">\u25CF<p></html>";

    /**
     * Font of CIRCLE Constant
     */
    public static final Font CIRCLE_FONT = new Font("", Font.PLAIN, 40);

    /**
     * Font of number that represents ammount of pieces that were taken by opponent
     */
    public static final Font LOST_PIECES_FONT = new Font("", Font.BOLD, 20);
    
    /**
     * Font of number that represents material value of winning player - material value of losing player
     */
    public static final Font LEADING_PLAYER = new Font("", Font.BOLD, 80);
    
    /**
     * Color of area that is behind GUI and background color of JPanel that stores moves
     */
    public static final Color BACKGROUND_COLOR = new Color(72, 72, 72);
    
    /**
     * \n
     */
    public static final String NEW_LINE = "\n";
    
    /**
     * Letter that represents KNIGHT in chess algebraic notation. This is used when we need to distiguish between Bishop and Knight, because they have same material value
     */
    public static final String KNIGHT_NOT = new Knight(true, false).getNotation();
    
    /**
     * Letter that represents BISHOP in chess algebraic notation. This is used when we need to distiguish between Bishop and Knight, because they have same material value
     */
    public static final String BISHOP_NOT = new Bishop(true, false).getNotation();
    
    /**
     * summ of all pieces values according to FIDE rules those belong to one player
     */
    public static final int NORMAL_SETTING_VALUE = 129;
    
    /**
     * number of certain piece on chessboard in the beginning of the game (0 = Pawns, 1 = Knights, 2 = Bishops, 3 = Rooks, 4 = Queens)
     */
    public static final int[][] PIECE_COUNT = {{8, 2, 2, 2, 1}, {8, 2, 2, 2, 1}};
    
    /**
     * Color of Comboboxes these appear in the TitlePage
     */
    public static final Color TITLE_PAGE_CHOICES_COLOR = new Color(210, 210, 190);
    
    /**
     * Minimal width of all JFrames those are created by this programme
     */
    public static final int FRAME_MIN_WIDTH = 1095;
    
    /**
     * Minimal height of all JFrames those are created by this programme
     */
    public static final int FRAME_MIN_HEIGHT = 730;
    
    /**
     * Array of possible chessboard settings (0 = normal setting according to FIDE rules, 1 = fischer chessboard setting)
     */
    public static String[] gameChoose = {"Normal", "Fischer"};
    
    /**
     * Default X position where Comboboxes in TitlePage are placed
     */
    public static final int CHOICE_X = 650;
    
    /**
     * Array of possible time settings (n + k, n = clock time, k = number of seconds those are added to time when player moves)
     */
    public static String[] times = {"1:00", "3:00", "5:00", "10:00", "1:00:00", "1:00 + 1", "3:00 + 3", "5:00 + 5", "10:00 + 5"};
    
    /**
     * Height of Combobox in TitlePage
     */
    public static final int CHOICE_HEIGHT = 100;
    
    /**
     * Array of possible skill levels of stockfish chess engine
     */
    public static String[] engineDifficulty = fillDifficultyArray();
    
    /**
     * first Y coordinate of first Combobox in TitlePage
     */
    public static final int CHOICE_Y = 153;
    
    /**
     * Array of possible game opponent types
     */
    public static String[] newGame = {"Singleplayer", "2P/1C", "2P/2C"};
    
    /**
     * Array that activates when client-server opponent is chosen, (IP + port means only client, port means client + server)
     */
    public static String[] multiPlayer = {"enter IP: port", "enter port"};
    
    /**
     * Width of combobox in TitlePage
     */
    public static final int CHOICE_WIDTH = 400;
    
    
    private static String[] fillDifficultyArray() {
        String[] x = new String[20];
        for (int i = 1; i <= 20; i++) {
            x[i - 1] = String.valueOf(i);
        }
        return x;
    }

}
