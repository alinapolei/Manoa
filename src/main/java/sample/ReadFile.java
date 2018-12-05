package sample;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ReadFile {
    public ReadFile() {
    }

    /**
     * separate the geven file to documents into the given hashset, according to tag <DOC></DOC>
     *
     * @param file
     * @param hashSet
     * @throws Exception
     */
    public void separateDocuments(File file, Queue<Doc> hashSet) throws Exception {
        try {
            int i = 0;
            Document doc = Jsoup.parse(file, "utf-8");
            Elements x = doc.getElementsByTag("Doc");
            Doc temp = new Doc();
            for (i = 0; i < x.size(); i++) {
                temp.setDocNumber(x.get(i).getElementsByTag("DOCNO").text());
                if (temp.getDocNumber().startsWith("FB"))
                    setDocParrameters(temp, x.get(i), "date1", "TI");
                else if (temp.getDocNumber().startsWith("FT"))
                    setDocParrameters(temp, x.get(i), "Date", "HEADLINE");
                else if (temp.getDocNumber().startsWith("LA"))
                    setDocParrameters(temp, x.get(i), "Date", "HEADLINE");

                try {
                    temp.setCity(x.get(i).getElementsByTag("F").toArray());
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
                hashSet.add(temp);
                temp = new Doc();
                //System.out.println(hashSet.size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the value of the doc's parameters according to the appropriate tags
     *
     * @param temp
     * @param x
     * @param dateTag
     * @param headTag
     */
    public void setDocParrameters(Doc temp, Element x, String dateTag, String headTag) {
        temp.setPublishDate(x.getElementsByTag(dateTag).text());
        if (x.getElementsByTag("text").text().indexOf("[Text]") != -1) {
            int k = x.getElementsByTag("text").text().indexOf("[Text]") + 6;
            temp.setBodyText(x.getElementsByTag("text").text().substring(k));
        } else
            temp.setBodyText(x.getElementsByTag("text").text());
        temp.setHeadLine(x.getElementsByTag(headTag).text());

    }

    /**
     * reads the file of stop words and put the words into the given set
     *
     * @param stopWords
     * @param path
     * @throws IOException
     */
    public void setStopWords(Set<String> stopWords, String path) throws IOException {
        File file = new File(path);
        String[] stopWordsArray = (String.join("\n", Files.readAllLines(file.toPath()))).split("\n");
        for (int i = 0; i < stopWordsArray.length; i++)
            stopWords.add(stopWordsArray[i]);
    }


    public void setLangANDccITY(File file) throws Exception {
        int i = 0;
        Document doc = Jsoup.parse(file, "utf-8");
        Elements x = doc.getElementsByTag("Doc");
        Object [] arr;
        String[] tmp;
        for (i=0;i<x.size();i++) {
            arr=x.get(i).getElementsByTag("F").toArray();
            for (int j=0;j<arr.length;j++)
            {
                if ((arr[j].toString()).contains("<f p=\"104\">")) {
                    tmp = arr[j].toString().split("<f p=\"104\">")[1].split(" ");
                    for (int k = 0; k < tmp.length; k++)
                        if (!tmp[k].equals("") && !tmp[k].equals("\n") && tmp[k].compareTo("</f>") != 0) {
                            if(Main.CityStorage.containsKey(tmp[k].toUpperCase()))
                                Main.citycorp.put(tmp[k].toUpperCase(),new City(Main.CityStorage.get(tmp[k].toUpperCase()).getName(),
                                                                                Main.CityStorage.get(tmp[k].toUpperCase()).getCurrency(),
                                                                                Main.con.parseNumber(Main.CityStorage.get(tmp[k].toUpperCase()).getPop()),
                                                                                Main.CityStorage.get(tmp[k].toUpperCase()).getCountry(),""));
                            else
                                Main.citycorp.put(tmp[k].toUpperCase(),new City(tmp[k].toUpperCase(),"","","",""));
                            break;
                        }
                }

                if ((arr[j].toString()).contains("<f p=\"105\">")) {
                    tmp = arr[j].toString().split("<f p=\"105\">")[1].split(" ");
                    for (int k = 0; k < tmp.length; k++)
                        if (!tmp[k].equals("") && !tmp[k].equals("\n") && tmp[k].compareTo("</f>") != 0) {
                            if(Main.lang.containsKey(tmp[k].toLowerCase()))
                                Main.lang.get(tmp[k].toLowerCase()).add(x.get(i).getElementsByTag("DOCNO").text());
                            else {
                                Main.lang.put(tmp[k].toLowerCase(),new ArrayList<>());
                                Main.lang.get(tmp[k].toLowerCase()).add(x.get(i).getElementsByTag("DOCNO").text());
                            }
                            break;
                        }
                }

            }
               ///
        }
        //System.out.println("here");
    }
}
