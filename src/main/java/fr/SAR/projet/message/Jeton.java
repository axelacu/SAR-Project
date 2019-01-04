package fr.SAR.projet.message;

public class Jeton extends ToSend {

    private int val;

    public Jeton(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
