package main;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

import pieces.*;

public class Board extends JPanel {
    public String fenStartingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public int tileSize = 85;

    int cols = 8;
    int rows = 8;
    ArrayList<Piece> pieceList = new ArrayList<>();

    public Piece selectedPiece;

    Input input = new Input(this);
    public CheckScanner checkScanner = new CheckScanner(this);

    public int enPassentTile = -1;

    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;

    public Board() {
        this.setPreferredSize(new Dimension(cols*tileSize, rows*tileSize));

        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        loadPositionFromFEN(fenStartingPosition);
    }

    public Piece getPiece(int col, int row) {
        for(Piece piece : pieceList) {
            if(piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        if(move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else {
            enPassentTile = -1;
        }
        if(move.piece.name.equals("King")) {
            moveKing(move);
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;
        
        move.piece.isFirstMove = false;
        
        capture(move.capture);

        isWhiteToMove = !isWhiteToMove;

        updateGameState();
    }

    private void moveKing(Move move) {
        if(Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            if(move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                rook.col = 5;
            } else {
                rook = getPiece(0, move.piece.row);
                rook.col = 3;
            }
            rook.xPos = rook.col * tileSize;
        }
    }

    public void movePawn(Move move) {
        //en passant
        int colorIndex = move.piece.isWhite ? 1 : -1;

        if(getTileNum(move.newCol, move.newRow) == enPassentTile) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);
        }
        if(Math.abs(move.piece.row - move.newRow) == 2) {
            enPassentTile = getTileNum(move.newCol, move.newRow + colorIndex);
        } else {
            enPassentTile = -1;
        }

        //promotions
        colorIndex = move.piece.isWhite ? 0 : 7;
        if(move.newRow == colorIndex) {
            promotePawn(move);
        }
    }

    private void promotePawn(Move move) {
        pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
        capture(move.piece);
    }

    public void capture(Piece piece) {
        pieceList.remove(piece);
    }

    public boolean isValidMove(Move move) {
        if(isGameOver) {
            return false;
        }

        if(move.piece.isWhite != isWhiteToMove) {
            return false;
        }

        if(sameTeam(move.piece, move.capture)) {
            return false;
        }
        if(!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }
        if(move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }
        if(checkScanner.isKingChecked(move)) {
            return false;
        }

        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if(p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }

    public int getTileNum(int col, int row) {
        return row * rows + col;
    }

    Piece findKing(boolean isWhite) {
        for(Piece piece : pieceList) {
            if(isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public void loadPositionFromFEN(String fenString) {
        pieceList.clear();
        String[] parts = fenString.split(" ");
        String position = parts[0];
		int row = 0, col = 0;
		for(char c : position.toCharArray()) {
			if(c == '/') {
				row++;
				col = 0;
			}
			if(Character.isLetter(c)) {
				if(Character.isLowerCase(c)) {
					addToBoard(col, row, c, false);
				} else {
					addToBoard(col, row, c, true);
				}
				col++;
			}
			if(Character.isDigit(c)) {
				col += Character.getNumericValue(c);
			}
		}
        isWhiteToMove = parts[1].equals("w");
    }

    public void addToBoard(int x, int y, char c, boolean isWhite) {
		switch(String.valueOf(c).toUpperCase()) {
		case "R":
			pieceList.add(new Rook(this, x, y, isWhite));
			break;
		case "N":
            pieceList.add(new Knight(this, x, y, isWhite));
			break;
		case "B":
            pieceList.add(new Bishop(this, x, y, isWhite));
			break;
		case "Q":
            pieceList.add(new Queen(this, x, y, isWhite));
			break;
		case "K":
			pieceList.add(new King(this, x, y, isWhite));
			break;
		case "P":
            pieceList.add(new Pawn(this, x, y, isWhite));
			break;
		}
	}

    private void updateGameState() {
        Piece king = findKing(isWhiteToMove);
        if(checkScanner.isGameOver(king)) {
            if(checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                JOptionPane.showMessageDialog(null, "check mate " + (isWhiteToMove ? "Black" : "White") + " wins");
                System.out.println(isWhiteToMove ? "Black Wins" : "White Wins");
            } else {
                JOptionPane.showMessageDialog(null, "stalemate");
                System.out.println("Stalemate");
            }
            isGameOver = true;
        } else if(inSufficientMaterial(true) && inSufficientMaterial(false)) {
            JOptionPane.showMessageDialog(null, "Insufficient Material");
            System.out.println("Insufficient Material");
            isGameOver = true;
        }
    }

    private boolean inSufficientMaterial(boolean isWhite) {
        ArrayList<String> names = pieceList.stream().filter(p -> p.isWhite == isWhite).map(p -> p.name).collect(Collectors.toCollection(ArrayList::new));
        if(names.contains("Queen") || names.contains("Pawn") || names.contains("Rook")) {
            return false;
        }
        return names.size() < 3;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Paint board
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                g2.setColor((c+r)%2 == 0 ? new Color(238, 238, 210) : new Color(181, 101, 29));
                g2.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
            }
        }

        // Paint highlights
        if(selectedPiece != null) {
            for(int r=0; r<rows; r++) {
                for(int c=0; c<cols; c++) {
                    if(isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2.setColor(new Color(68, 180, 57, 190));
                        // g2.setColor(new Color(173, 216, 230));
                        g2.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Paint pieces
        for(Piece piece : pieceList) {
            piece.paint(g2);
        }
    }
}
