package sample;

public class PostEntry {
    private String docNumber;
    private int tf=1;
    private boolean isTitle;

    public PostEntry(String name, boolean isTitle) {
        this.isTitle = isTitle;
        this.docNumber = name;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public int getTf() {
        return tf;
    }

    public void increaseTf() {

        this.tf ++;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    @Override
    public String toString() {
        return docNumber + ' ' +
                 + tf + ' ' + (isTitle ? "V" : "X");
    }
}
