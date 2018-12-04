package sample;

import java.util.ArrayList;

public class City {
    private String doc;
    private String name;
    private String Currency;
    private String pop;
    private String Country;
    private ArrayList<Integer> docplace;




    public City(String name, String currency, String pop,String country,String doc) {
        this.doc=doc;
        this.name = name;
        Currency = currency;
        this.pop = pop;
        //docplace=new ArrayList<>();
        this.Country=country;
    }

    public ArrayList<Integer> getDocplace() {
        if (docplace==null)
            docplace=new ArrayList<>();
        return docplace;
    }

    public void setDocplace(Integer index) {
        docplace.add(index);
    }


    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {

        this.pop = pop;
    }

    @Override
    public String toString() {
        if (docplace==null)
            return name + " " +doc+" "+"[]";
        return name + " " +doc+" "+docplace.toString();
    }
}
