package sample;

public class PostEntry {
    /**
     * class that represents specific post of a term in one document
     */
    private String term;
    private String docNumber;
    private int tf=1;
    private boolean isTitle;

    public PostEntry(String term,String name, boolean isTitle) {
        this.term=term;
        this.isTitle = isTitle;
        this.docNumber = name;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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
        return "PostEntry{" +
                "term='" + term + '\'' +
                ", docNumber='" + docNumber + '\'' +
                ", tf=" + tf +
                ", isTitle=" + isTitle +
                '}';
    }
}
