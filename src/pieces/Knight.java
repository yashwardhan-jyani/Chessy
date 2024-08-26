package pieces;

import java.awt.image.BufferedImage;

import main.Board;

public class Knight extends Piece {
    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.row = row;
        this.col = col;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;

        this.isWhite = isWhite;
        this.name = "Knight";

        this.image = sheet.getSubimage(3*sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}
