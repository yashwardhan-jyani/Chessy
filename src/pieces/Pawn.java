package pieces;

import java.awt.image.BufferedImage;

import main.Board;

public class Pawn extends Piece {
    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.row = row;
        this.col = col;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;

        this.isWhite = isWhite;
        this.name = "Pawn";
        
        this.image = sheet.getSubimage(5*sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1;

        // push pawn 1
        if(this.col == col && row == this.row-colorIndex && board.getPiece(col, row) == null) {
            return true;
        }

        // push pawn 2
        if(this.row == (isWhite ? 6 : 1) && this.col == col && row == this.row-(colorIndex*2) && board.getPiece(col, row) == null && board.getPiece(col, row+colorIndex) == null) {
            return true;
        }

        //capture left
        if(col == this.col-1 && row == this.row-colorIndex && board.getPiece(col, row) != null) {
            return true;
        }

        //capture right
        if(col == this.col+1 && row == this.row-colorIndex && board.getPiece(col, row) != null) {
            return true;
        }

        //en passant left
        if(board.getTileNum(col, row) == board.enPassentTile && col == this.col-1 && row == this.row-colorIndex && board.getPiece(col, row+colorIndex) != null) {
            return true;
        }

        //en passant right
        if(board.getTileNum(col, row) == board.enPassentTile && col == this.col+1 && row == this.row-colorIndex && board.getPiece(col, row+colorIndex) != null) {
            return true;
        }

        return false;
    }
}
