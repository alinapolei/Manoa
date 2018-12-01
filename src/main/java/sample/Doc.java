package sample;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.json.JSONObject;

import java.util.Map;

public class Doc {

    private String docNumber;
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

    public void setCity(Object[] arry) throws Exception {
        Object[] array = arry;
        for (int i = 0; i < array.length; i++) {
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
                HttpResponse<JsonNode> response = Unirest.get("http://getcitydetails.geobytes.com/GetCityDetails?fqcn=" + city.toLowerCase())
                        .header("X-Mashape-Key", "<required>")
                        .header("Accept", "application/json")
                        .asJson();
                Object map = (response.getBody().getArray().get(0));
                String cur = ((JSONObject) map).get("geobytescurrency").toString();
                String Cuntry = ((JSONObject) map).get("geobytescountry").toString();
                String pop = ((JSONObject) map).get("geobytespopulation").toString();
                Conditions con = new Conditions();
                pop = con.parseNumber(pop);
                Main.cityIndexer.put(docNumber, new City(city, cur, pop, docNumber));
                Main.CityStorage.put(city,new City(city,cur,pop,""));
            }
            else {
                String cur = Main.CityStorage.get(city).getCurrency();
                String pop = Main.CityStorage.get(city).getPop();
                Main.cityIndexer.put(docNumber,new City(city,cur,pop,docNumber));
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
