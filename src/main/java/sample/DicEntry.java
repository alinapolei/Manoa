package sample;

public class DicEntry {

   private String term;
   private int df;
   private int tfCourpus;
   private int ptr;

    public DicEntry(String term) {
        this.term = term;
        df=1;
        tfCourpus=1;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public int getTfCourpus() {
        return tfCourpus;
    }

    public void setTfCourpus(int fCourpus) {
       tfCourpus = fCourpus;
    }

    public int getPtr() {
        return ptr;
    }

    public void setPtr(int ptr) {
        this.ptr = ptr;
    }
}