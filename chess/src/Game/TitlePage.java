package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Creates the first page that player sees when they run the program
 * @author vavra
 */
public class TitlePage extends JPanel {

    JFrame frame;
    JComboBox[] choices;
    JButton[] startButtons;

    /**
     * index 0 = hours, 1 = minutes, 2 = seconds, 3 = number of seconds
     * increased by move, 4 = game type (0 = singleplayer, 1 = clientServer, 2 =
     * only client, 3 = 2 players / 1 computer), 5 = port, 6 = engine difficulty
     */
    private int[] gameSetting;
    private String IP;
    private String gameType;
    private boolean whiteGUI;

    public TitlePage() {
        gameSetting = new int[7];
        for (int i = 0; i < gameSetting.length; i++) {
            gameSetting[i] = -1;
        }
        initFrame();
        initActionPart();
        initPanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon(this.getClass().getResource("/PieceImages/title_page_without_Buttons.png")).getImage(), 0, 0, getWidth(), getHeight(), null);
    }

    public void initPanel() {
        setBackground(Color.RED);
        frame.add(this);
        setLayout(null);
        resizePanel();
        componentListener();
    }

    public void initActionPart() {
        choices = new JComboBox[4];
        startButtons = new JButton[2];
        for (int i = 0; i < choices.length; i++) {
            choices[i] = new JComboBox();
            choices[i].setBounds((int) (xRatio(Constants.CHOICE_X) * (Constants.FRAME_MIN_WIDTH - 7)), (int) (yRatio(i) * (Constants.FRAME_MIN_HEIGHT - 45)),
                    (int) (widthRatio(400.0) * (Constants.FRAME_MIN_WIDTH - 7)), (int) (heightRatio(100.0) * (Constants.FRAME_MIN_HEIGHT - 45)));
            choices[i].setBackground(Constants.TITLE_PAGE_CHOICES_COLOR);
            choices[i].setFont(new Font("Calibri", Font.BOLD, 72));
            choices[i].setAlignmentX(CENTER_ALIGNMENT);
            choices[i].setAlignmentY(CENTER_ALIGNMENT);
            choices[i].setForeground(Color.BLACK);
            choices[i].setSelectedIndex(-1);
            ((JLabel) choices[i].getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); //https://stackoverflow.com/questions/11008431/how-to-center-items-in-a-java-combobox
            ((JLabel) choices[i].getRenderer()).setVerticalAlignment(SwingConstants.CENTER);
            add(choices[i]);
        }
        choicesActions();
        buttons();
    }

    private void choicesActions() {
        addAll(choices[0], Constants.newGame);
        choices[0].addActionListener((ActionEvent e) -> {
            gameSetting[4] = -1;
            choices[1].removeAllItems();
            choices[2].removeAllItems();
            String selected = (String) choices[0].getSelectedItem();
            switch (selected) {
                case "Singleplayer":
                    gameSetting[4] = 0;
                    choices[1].addItem("Normal");
                    addAll(choices[2], Constants.engineDifficulty);
                    choices[2].setEditable(false);
                    break;
                case "2P/1C":
                    gameSetting[4] = 3;
                    choices[2].setEditable(false);
                    addAll(choices[1], Constants.gameChoose);
                    addAll(choices[3], Constants.times);
                    break;
                case "2P/2C":
                    choices[2].setEditable(true);
                    addAll(choices[1], Constants.gameChoose);
                    addAll(choices[2], Constants.multiPlayer);
            }
        });
        choices[1].addActionListener((ActionEvent e) -> {
            gameType = (String) choices[1].getSelectedItem();
        });
        choices[2].addActionListener((ActionEvent e) -> {
            addAll(choices[3], Constants.times);
            String selected = (String) choices[2].getSelectedItem();
            if (selected == null) {
                selected = "";
            }
            try {
                if (selected.length() <= 2) {
                    gameSetting[6] = Integer.parseInt(selected);
                } else {
                    if (selected.contains(":")) {
                        String[] parts = selected.split(": ");
                        IP = parts[0];
                        gameSetting[4] = 2;
                        gameSetting[5] = Integer.parseInt(parts[1]);
                    } else {
                        gameSetting[4] = 1;
                        IP = "localhost";
                        gameSetting[5] = Integer.parseInt(selected);
                    }
                }
            } catch (Exception ex) {

            }
        });
        choices[3].addActionListener((ActionEvent e) -> {
            String selected = (String) choices[3].getSelectedItem();
            String[] parts = selected.split(" \\+ ");
            if (parts.length == 2) {
                gameSetting[3] = Integer.parseInt(parts[1]);
            }
            String[] timeSetting = parts[0].split(":");
            if (timeSetting.length == 3) {
                gameSetting[0] = Integer.parseInt(timeSetting[0]);
                gameSetting[1] = Integer.parseInt(timeSetting[1]);
                gameSetting[2] = Integer.parseInt(timeSetting[2]);
            } else {
                gameSetting[0] = 0;
                gameSetting[1] = Integer.parseInt(timeSetting[0]);
                gameSetting[2] = Integer.parseInt(timeSetting[1]);
            }
        });
    }

    private void buttons() {
        for (int i = 0; i < startButtons.length; i++) {
            startButtons[i] = new JButton();
            startButtons[i].setBackground(i == 0 ? Color.WHITE : Color.BLACK);
            startButtons[i].setBounds((int) (xRatio(Constants.CHOICE_X - 130) * (Constants.FRAME_MIN_WIDTH - 7)), (int) (yRatio(0) * (Constants.FRAME_MIN_HEIGHT - 45) + i * heightRatio(260.0) * (Constants.FRAME_MIN_HEIGHT - 45)),
                    100, (int) (heightRatio(260.0) * (Constants.FRAME_MIN_HEIGHT - 45)));
            add(startButtons[i]);
        }
        startButtons[0].addActionListener((ActionEvent e) -> {
            whiteGUI = true;
            run();
        });
        startButtons[1].addActionListener((ActionEvent e) -> {
            whiteGUI = gameSetting[4] == 3;
            run();
        });
    }

    private boolean canRun() {
        return gameSetting[0] != -1 && gameSetting[1] != -1 && gameSetting[2] != -1 && gameSetting[4] != -1;
    }

    private void run() {
        if (canRun()) {
            GUI g = new GUI(gameSetting[0], gameSetting[1], gameSetting[2], gameSetting[3], whiteGUI, gameSetting[4], gameSetting[5], IP, gameSetting[6], gameType);
            if(gameSetting[4] != 1 && gameSetting[4] != 2){
                g.setVisible(true);
            }
        }
    }

    private double xRatio(double x) {
        return x / (Constants.FRAME_MIN_WIDTH - 7);
    }

    private void addAll(JComboBox choice, String[] array) {
        for (String x : array) {
            choice.addItem(x);
        }
    }

    private double yRatio(int type) {
        switch (type) {
            case 0:
                return 0.224;
            case 1:
                return 0.418;
            case 2:
                return 0.607;
            case 3:
                return 0.796;
            default:
                return 1;
        }
    }

    private double widthRatio(double width) {
        return width / frame.getMinimumSize().width;
    }

    private double heightRatio(double height) {
        return height / frame.getMinimumSize().height;
    }

    private void resizePanel() {
        int width;
        int height;
        if (frame.getHeight() * 1.5 > frame.getWidth()) {
            width = frame.getWidth();
            height = (int) (frame.getWidth() * 0.666);
        } else {
            width = (int) (frame.getHeight() * 1.5);
            height = (int) frame.getHeight();
        }
        setBounds((frame.getWidth() - width) / 2 - 7, (frame.getHeight() - height) / 2, width - 7, height - 45);
    }

    private void componentListener() {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePanel();
                revalidate();
                resizeButtons();
            }
        });
    }

    private void resizeButtons() {
        for (int i = 0; i < choices.length; i++) {
            choices[i].setBounds((int) (xRatio(Constants.CHOICE_X) * getWidth()), (int) (yRatio(i) * getHeight()),
                    (int) (widthRatio(400.0) * getWidth()), (int) (heightRatio(100.0) * getHeight()));
        }
        for (int i = 0; i < startButtons.length; i++) {
            startButtons[i].setBounds((int) (xRatio(Constants.CHOICE_X - 130) * getWidth()), (int) (yRatio(0) * getHeight() + i * (heightRatio(260.0) * getHeight())),
                    (int) (widthRatio(100.0) * getWidth()), (int) (heightRatio(260.0) * getHeight()));
        }
    }

    private void initFrame() {
        frame = new JFrame("sachy");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new java.awt.Dimension(Constants.FRAME_MIN_WIDTH, Constants.FRAME_MIN_HEIGHT));
        frame.getContentPane().setBackground(Constants.BACKGROUND_COLOR);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null);
        frame.setTitle("chess");
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        TitlePage cb = new TitlePage();
    }
}
