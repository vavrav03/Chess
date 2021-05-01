package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Creates Timer object that counts from cetrain time to zero. When zero is
 * reached, winner is player that wasn't on move when timer reached 0.
 *
 * @author vavra
 */
public class Timer extends Thread {

    private int[] time;
    private JLabel[] texts;
    private JPanel WhitesTimer;
    private JPanel BlacksTimer;
    private int increaseByMove;
    private GUI g;
    private boolean whiteMoves;

    /**
     * @param hour number of hours to value 0, maximum 24
     * @param minute number of minutes to value 0, maximum 60
     * @param second number of seconds to value 0, maximum 60
     * @param g GUI chessboard object that is this timer part of
     * @param increaseByMove if player makes move, the time will increase by
     * given time
     */
    public Timer(int hour, int minute, int second, GUI g, int increaseByMove) {
        super("Time");
        whiteMoves = g.logicBoard.whiteMoves();
        int seconds = hour * 3600 + minute * 60 + second;
        time = new int[2];
        time[0] = seconds;
        time[1] = seconds;
        this.g = g;
        this.increaseByMove = increaseByMove;
        createLabels(seconds);
        WhitesTimer();
        BlacksTimer();
    }

    public JPanel getWhiteTimer() {
        return WhitesTimer;
    }

    public JPanel getBlackTimer() {
        return BlacksTimer;
    }
    
    /**
     *
     * @param black true if timer belongs to black
     * @return remaining time in seconds
     */
    public int getTime(boolean black){
        return time[black ? 0 : 1];
    }
    
    public void setTime(int time, boolean white){
        this.time[white ? 1 : 0] = time;
    }

    /**
     * @param hour number of hours to value 0, maximum 24
     * @param minute number of minutes to value 0, maximum 60
     * @param second number of seconds to value 0, maximum 60 60.
     * @return is time valid
     */
    public boolean isValid(int hour, int minute, int second) {
        return hour <= 24 && minute < 60 && second < 60 && hour >= 0 && minute >= 0 && second >= 0;
    }

    private void createLabels(int seconds) {
        texts = new JLabel[this.time.length];
        texts[0] = new JLabel(clockTime(seconds));
        texts[0].setForeground(Color.WHITE);
        texts[0].setFont(new Font("Calibri", Font.BOLD, 40));
        texts[1] = new JLabel(clockTime(seconds));
        texts[1].setForeground(Color.BLACK);
        texts[1].setFont(new Font("Calibri", Font.BOLD, 40));
    }

    private String clockTime(int seconds) {
        int temporary = seconds / 3600;
        String time = "";
        if (temporary != 0) {
            time += temporary + ":";
        }
        temporary = (seconds - temporary * 3600) / 60;
        if (temporary < 10) {
            time += 0;
        }
        time += temporary + ":";
        temporary = seconds % 60;
        if (temporary < 10) {
            time += 0;
        }
        time += temporary;
        return time;
    }

    private JLabel getBlacksText() {
        return texts[0];
    }

    private JLabel getWhitesText() {
        return texts[1];
    }

    /**
     * creates white timer JPanel
     */
    public void WhitesTimer() {
        WhitesTimer = new JPanel();
        WhitesTimer.add(getWhitesText());
        WhitesTimer.setLayout(new GridBagLayout());
        WhitesTimer.setBackground(Color.WHITE);
        WhitesTimer.setPreferredSize(new java.awt.Dimension(0, 34));
    }

    /**
     * creates black timer JPanel
     */
    public void BlacksTimer() {
        BlacksTimer = new JPanel();
        BlacksTimer.add(getBlacksText());
        BlacksTimer.setBackground(Color.BLACK);
        BlacksTimer.setLayout(new GridBagLayout());
        BlacksTimer.setPreferredSize(new java.awt.Dimension(0, 34));
        colorChange();
    }

    /**
     * changes the timer of player that is on the move
     */
    public void playerChange() {
        increaseByMove(whiteMoves);
        whiteMoves = !whiteMoves;
        colorChange();
    }

    private void colorChange() {
        if (whiteMoves) {
            WhitesTimer.setBackground(Color.WHITE);
            BlacksTimer.setBackground(ChangedColor(BlacksTimer.getBackground()));
            texts[0].setForeground(ChangedColor(WhitesTimer.getBackground()));
            texts[1].setForeground(Color.BLACK);
        } else {
            WhitesTimer.setBackground(ChangedColor(WhitesTimer.getBackground()));
            BlacksTimer.setBackground(Color.BLACK);
            texts[0].setForeground(Color.WHITE);
            texts[1].setForeground(ChangedColor(BlacksTimer.getBackground()));
        }
    }

    /**
     * changes color of timer when it's active
     *
     * @param color color of timer
     * @return
     */
    public Color ChangedColor(Color color) {
        if (color.equals(Color.BLACK)) {
            return Color.GRAY.darker();
        }
        return Color.GRAY.brighter();
    }

    /**
     * Adds value of increaseByMove to the time
     * @param whiteMoves true if white is on the move now
     */
    public void increaseByMove(boolean whiteMoves) {
        int w = whiteMoves ? 1 : 0;
        time[w] += increaseByMove;
        texts[w].setText(clockTime(time[w]));
    }

    @Override
    public void run() {
        byte white = whiteMoves ? (byte) 1 : 0;
        while (time[white] >= 0) {
            if (g.isAllowedMove()) {
                white = whiteMoves ? (byte) 1 : 0;
                texts[white].setText(clockTime(time[white]));
                time[white]--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        g.logicBoard.getWhiteKing().check.setWhy("time expired");
        g.showDrawMate(whiteMoves ? 2 : 1);
    }
}
