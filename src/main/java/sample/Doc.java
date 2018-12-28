package sample;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.json.JSONObject;

import java.util.*;

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
    private String maxTerm="";
    private int maxtf;
    private int numOfWords;
    private int length;
    private HashSet<String> topFiveTerms = new HashSet<>();

    HashSet <String> tmplist=new HashSet<>();
    private PriorityQueue<PostEntry> maxFive = new PriorityQueue<>(5, new Comparator<PostEntry>() {
        @Override
        public int compare(PostEntry o1, PostEntry o2) {
            if (o1.getTf()>o2.getTf())
                return 1;
            return -1;
        }
    });
    public String getCity() {
        return city;
    }

    public String getMaxTerm() {
        return maxTerm;
    }

    public void setMaxTerm(String maxTerm) {
        this.maxTerm = maxTerm;
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
            if(Main.citycorp.get(city).getCountry()=="") {
                Main.cityIndexer.put(docNumber,new City(city,"","","",docNumber));
                Main.nonCapital.add(city);
            }
            else {
                Main.cityIndexer.put(docNumber,new City(city,Main.citycorp.get(city).getCurrency(),Main.con.parseNumber(Main.citycorp.get(city).getPop()),Main.citycorp.get(city).getCountry(),docNumber));
                Main.Capital.add(city);
            }


        }
    }

    public void setCity(String city) {
        this.city = city;
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

    public void setNumOfWords(int numOfWords) {
        this.numOfWords = numOfWords;
    }

    public void increaseNumOfWords() {
        this.numOfWords++;
    }
    public int getNumOfWords() {
        return numOfWords;
    }

    public int getLength() {
        if(length == 0) {
            StringTokenizer tokens = new StringTokenizer(bodyText);
            length = tokens.countTokens();
        }
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void addToHeap(PostEntry postEntry) {
        if(Main.con.isAlpha(postEntry.getTerm())) {
            if (postEntry.getTerm().compareTo(postEntry.getTerm().toLowerCase()) == 0 &&
                    topFiveTerms.contains(postEntry.getTerm()))
                System.out.println("think again!");
            if (postEntry.getTerm().compareTo(postEntry.getTerm().toUpperCase()) == 0) {
                if (topFiveTerms.contains(postEntry.getTerm())) {
                    for (PostEntry post : maxFive) {
                        if (post.getTerm().compareTo(postEntry.getTerm()) == 0) {
                            maxFive.remove(post);
                            topFiveTerms.remove(postEntry.getTerm());
                            break;
                        }
                    }
                }

                if (maxFive.size() < 5) {
                    maxFive.add(postEntry);
                    topFiveTerms.add(postEntry.getTerm());
                } else if (maxFive.peek().getTf() < postEntry.getTf()) {
                    topFiveTerms.remove(maxFive.peek().getTerm());
                    maxFive.poll();
                    maxFive.add(postEntry);
                    topFiveTerms.add(postEntry.getTerm());
                }
            }
        }

        /*if (postEntry.getTerm().compareTo(postEntry.getTerm().toUpperCase()) == 0&&!tmplist.contains(postEntry.getTerm().toLowerCase())) {
            if (topFiveTerms.contains(postEntry.getTerm())) {
                for (PostEntry post : maxFive) {
                    if (post.getTerm().compareTo(postEntry.getTerm()) == 0) {
                        maxFive.remove(post);
                        topFiveTerms.remove(postEntry.getTerm());
                        break;
                    }
                }
            }
            if (maxFive.size() < 5) {
                maxFive.add(postEntry);
                topFiveTerms.add(postEntry.getTerm());
            }
            else if (maxFive.peek().getTf() < postEntry.getTf()) {
                topFiveTerms.remove(maxFive.peek().getTerm());
                maxFive.poll();
                maxFive.add(postEntry);
                topFiveTerms.add(postEntry.getTerm());
            }
        }
        if(postEntry.getTerm().compareTo(postEntry.getTerm().toLowerCase())==0)
            tmplist.add(postEntry.getTerm());
        */
    }
    @Override
    public String toString() {
        return docNumber + " " + "maxtf=" + maxtf + " " + "uniqueWords=" + numOfWords + " " + "length=" + getLength() + " " +
                (!city.equals("") ? ("city=" + city) : "") + " " + "maxFive="+ " "+"[" +printMaxFive()+ "]"+" "+maxTerm;
    }

    private String printMaxFive() {
        String res="";
        for (PostEntry post:maxFive) {
            res=res+ post.getTerm()+" ";
        }

            return res;
    }

}
