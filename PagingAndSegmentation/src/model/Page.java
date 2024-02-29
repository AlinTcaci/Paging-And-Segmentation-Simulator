package model;

public class Page {
    private int address;
    private int frame;
    private int pageNr;

    public Page(int address, int frame, int pageNr) {
        this.address = address;
        this.frame = frame;
        this.pageNr = pageNr;
    }

    // Getters and Setters
    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getPageNr() {
        return pageNr;
    }

    public void tsetPageNr(int pageNr) {
        this.pageNr = pageNr;
    }
}
