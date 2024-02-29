package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Process {
    private final List<Page> pages;
    private int PID;
    private String name;
    private int size;

    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Process(int PID, String name, int size, Color processColor) {
        this.PID = PID;
        this.name = name;
        this.size = size;
        this.pages = new ArrayList<>();
        this.color = processColor;
    }

    // Method to add a page to the process
    public void addPage(Page page) {
        pages.add(page);
    }

    // Getters and Setters
    public List<Page> getPages() {
        return pages;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
