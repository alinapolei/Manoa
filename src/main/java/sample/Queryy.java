package sample;

import java.util.Random;

public class Queryy {
    /**
     * class that represents a query
     */
    private String number;
    private String title;
    private String desc;
    private String narr;

    public Queryy() {
    }

    public Queryy(String title) {
        this.title = title;
        Random rand = new Random();
                this.number = rand.nextInt(500) + 1 + "";
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getNarr() {
        return narr;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setNarr(String narr) {
        this.narr = narr;
    }
}
