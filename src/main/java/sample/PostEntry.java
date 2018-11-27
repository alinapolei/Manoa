package sample;

public class PostEntry {
    private String docNumber;
    private int tf=1;

    public PostEntry(String name) {
        this.docNumber = name;
        //tf = 1;
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

    @Override
    public String toString() {
        return "docNumber='" + docNumber + ' ' +
                "tf=" + tf + "\n";
    }
}
