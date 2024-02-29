package model;

import java.awt.*;

public class Segments {
    private int SID;
    private String name;
    private int limit;
    private Color color;
    private int base_address;

public Segments(int SID, String name, int limit, Color color, int base_address) {
        this.SID = SID;
        this.name = name;
        this.limit = limit;
        this.color = color;
        this.base_address = base_address;
    }

    public int getSID() {
        return SID;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getBase_address() {
        return base_address;
    }

    public void setBase_address(int base_address) {
        this.base_address = base_address;
    }
}
