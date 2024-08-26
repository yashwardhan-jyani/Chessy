package pieces;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import main.Board;

public class Piece {
    public int col, row;
    public int xPos, yPos;
    public boolean isWhite;
    public String name;
    public int value;
    public boolean isFirstMove = true;

    Image image;
    Board board;

    BufferedImage sheet;
    {
        try {
            sheet = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("./res/pieces.png")));
//            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("./res/pieces.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    protected int sheetScale = sheet.getWidth()/6;

    public Piece(Board board) {
        this.board = board;
    }

    public boolean isValidMovement(int col, int row) {return true;}
    public boolean moveCollidesWithPiece(int col, int row) {return false;}

    public void paint(Graphics2D g2) {
        g2.drawImage(image, xPos, yPos, null);
    }
}
