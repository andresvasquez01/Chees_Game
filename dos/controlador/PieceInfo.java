package controlador;

import java.io.Serializable;

public class PieceInfo implements Serializable {
    private Type type;
    private int col;
    private int row;
    private int color;

    public PieceInfo(Type type, int col, int row, int color) {
        this.type = type;
        this.col = col;
        this.row = row;
        this.color = color;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
