
import Game.LogicBoard;
import Game.Constants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class YouAreSoSquare extends JFrame {

    private JPanel sachovnice;
    private LogicBoard hraciSachovnice;
    private ArrayList<Integer> moznosti;
    private JButton[][] policka;

    private int radekFigury;
    private int sloupecFigury;
    private int kliknutoStejne;

    public YouAreSoSquare() {
        policka = new JButton[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        hraciSachovnice = new LogicBoard();
        moznosti = new ArrayList();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //mainFrame.setLocationByPlatform(true);
        add(createPanel());
        pack();
        setMinimumSize(getSize());
        setVisible(true);
    }

    private JPanel createPanel() {
        JPanel gui = new JPanel(new GridBagLayout()){ 
            @Override
            public Dimension getPreferredSize() {
                int newSize = Math.min(getHeight(), getWidth());
                return new Dimension(newSize, newSize);
            }
        };
        JPanel squareComponent = new JPanel();
        sachovnice();
        squareComponent = sachovnice;
        GridBagConstraints g = new java.awt.GridBagConstraints();
        g.weighty = 1.0;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.BOTH;
        gui.add(squareComponent, g);
        return gui;
    }

    public void setPoziceFigury(int radek, int sloupec, int puvodRadek, int puvodSloupec) {
        policka[radek][sloupec].setIcon(null);
        policka[radek][sloupec].setIcon(policka[puvodRadek][puvodSloupec].getIcon());
        policka[puvodRadek][puvodSloupec].setIcon(null);
        hraciSachovnice.setChessboard(radek, sloupec, puvodRadek, puvodSloupec);
    }

    public void sachovnice() {
        this.sachovnice = new JPanel();
        policka = new JButton[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        Border border = BorderFactory.createLineBorder(Color.black, 10);
        sachovnice.setBorder(border);
        sachovnice.setLayout(new GridLayout(8, 8, 0, 0));
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

                button.addActionListener((ActionEvent e) -> {
                    for (int i = 0; i < Constants.BOARD_SIZE; ++i) {
                        for (int d = 0; d < Constants.BOARD_SIZE; ++d) {
                            if (policka[i][d].equals(e.getSource())) {
                                for (int p = 0; p < moznosti.size(); ++p) {
                                    policka[moznosti.get(p) / Constants.BOARD_SIZE][moznosti.get(p) % Constants.BOARD_SIZE].setText("");
                                }
                                if ((radekFigury + sloupecFigury) % 2 == 0) {
                                    policka[radekFigury][sloupecFigury].setBackground(Constants.WHITE);
                                } else {
                                    policka[radekFigury][sloupecFigury].setBackground(Constants.DARK);
                                }
                                if (moznosti.contains(i * Constants.BOARD_SIZE + d)) {
                                    setPoziceFigury(i, d, radekFigury, sloupecFigury);
                                }
                                if (hraciSachovnice.arePossibleMoves()) {
                                    if (i != radekFigury || d != sloupecFigury || kliknutoStejne % 2 == 0) {
                                        kliknutoStejne = 1;
                                        moznosti = hraciSachovnice.moveReduction(i, d);
                                        if (hraciSachovnice.getChessboard(i, d) != null && hraciSachovnice.whiteMoves() == hraciSachovnice.getChessboard(i, d).isWhite()) {
                                            if ((i + d) % 2 == 0) {
                                                policka[i][d].setBackground(Constants.WHITE_CHANGED);
                                            } else if (hraciSachovnice.getChessboard(i, d) != null) {
                                                policka[i][d].setBackground(Constants.DARK_CHANGED);
                                            }
                                        }
                                        for (int s = 0; s < moznosti.size(); ++s) {
                                            policka[moznosti.get(s) / Constants.BOARD_SIZE][moznosti.get(s) % Constants.BOARD_SIZE].setText(Constants.CIRCLE);
                                        }
                                    } else {
                                        kliknutoStejne++;
                                    }

                                    radekFigury = i;
                                    sloupecFigury = d;
                                } else {
                                    System.exit(0);
                                }
                            }
                        }
                    }
                });
                policka[y][x] = button;
            }
        }
        for (int y = 0; y < Constants.BOARD_SIZE; ++y) {
            for (int x = 0; x < Constants.BOARD_SIZE; ++x) {
                sachovnice.add(policka[y][x]);
                if (hraciSachovnice.getChessboard(y, x) != null) {
                    Image img = hraciSachovnice.getChessboard(y, x).getPiece().getImage();
                    Image newImage = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    policka[y][x].setIcon(new ImageIcon(newImage));
                }
            }
        }
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {
            public void run() {
                new YouAreSoSquare().setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}
