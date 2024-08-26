package main;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setTitle("Jyani Chess");
        frame.setResizable(false);
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(700, 720));
        frame.setLocationRelativeTo(null);

        Board board = new Board();
        frame.add(board);
        frame.setVisible(true);
    }
}