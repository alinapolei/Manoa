package sample;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.json.JSONObject;

import java.util.Map;

public class Doc {
    /**
     * class that represents a document
     */
    private String docNumber;//name of the document
    private String publishDate;
    private String header;
    private  String headLine;
    private String bodyText;
    private String city="";
    private int maxtf;
    private int numOfWords;

    public String getCity() {
        return city;
    }

    /**
     * sets the value of the document's city according to the tag <f p=104></f>
     * @param arry - an array of all the tags <F....></F> that exists in the document
     * @throws Exception
     */
    public void setCity(Object[] arry) throws Exception {
        Object[] array = arry;
        for (int i =array.length-1; i >-1 ; i--) {
            if ((array[i].toString()).contains("<f p=\"104\">")) {
                String[] tmpp= array[i].toString().split("<f p=\"104\">")[1].split(" ");
                for (int k=0;k<tmpp.length;k++)
                    if(!tmpp[k].equals("")&&!tmpp[k].equals("\n")&&tmpp[k].compareTo("</f>")!=0) {
                        city = tmpp[k].toUpperCase();
                        break;
                    }
                break;
            }

        }
        if (city!=("")&&city.compareTo("\n")!=0) {
            if(!Main.CityStorage.containsKey(city)) {
                Main.cityIndexer.put(docNumber,new City(city,"","","",docNumber));
                Main.nonCapital.add(city);
            }
            else {
                Main.cityIndexer.put(docNumber,new City(city,Main.CityStorage.get(city).getCurrency(),Main.con.parseNumber(Main.CityStorage.get(city).getPop()),Main.CityStorage.get(city).getCountry(),docNumber));
                Main.Capital.add(city);
            }


        }
    }
    public Doc() {}

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String[] docToString(){
        return new String[]{this.headLine, this.bodyText};
    }

    public int getMaxtf() {
        return maxtf;
    }
    public void setMaxtf(int maxtf) {
        this.maxtf = maxtf;
    }
    public void increaseNumOfWords() {
        this.numOfWords++;
    }
    public int getNumOfWords() {
        return numOfWords;
    }
    @Override
    public String toString() {
        return docNumber + " " + "maxtf=" + maxtf + " " + "length=" + numOfWords + " " +
                (!city.equals("") ? ("city=" + city) : "");
    }
}
