package sample;

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

    /*public ReadFile(String path, HashSet<Doc> hashSet,Set<String> stopWords) {
        //List<File> allFiles = new ArrayList<File>();
        //getAllFiles(path, allFiles);
        separateDocuments(allFiles, hashSet);
        System.out.println("num of files" + allFiles.size());
        try {
            setStopWords(stopWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(hashSet.size());
    }*/


    public void separateDocuments(File file, HashSet<Doc> hashSet) {
            try {
                int i=0;
                Document doc = Jsoup.parse(file, "utf-8");
                Elements x = doc.getElementsByTag("Doc");
                //System.out.println(x.size());
                for (i=0;i<x.size();i++) {
                    Doc temp=new Doc();
                    temp.setDocNumber(x.get(i).getElementsByTag("DOCNO").text());
                   if (temp.getDocNumber().startsWith("FB"))
                       setDocParrameters(temp,x.get(i),"date1","TI");
                   else if(temp.getDocNumber().startsWith("FT"))
                       setDocParrameters(temp,x.get(i),"Date","HEADLINE");
                   else if(temp.getDocNumber().startsWith("LA"))
                       setDocParrameters(temp,x.get(i),"Date","HEADLINE");


                    hashSet.add(temp);
                    //System.out.println(hashSet.size());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void setDocParrameters(Doc temp,Element x,String dateTag,String headTag)
    {
        temp.setPublishDate(x.getElementsByTag(dateTag).text());
        if(x.getElementsByTag("text").text().indexOf("[Text]")!=-1) {
            int k = x.getElementsByTag("text").text().indexOf("[Text]") + 6;
            temp.setBodyText(x.getElementsByTag("text").text().substring(k));
        }
        else
            temp.setBodyText(x.getElementsByTag("text").text());
        temp.setHeadLine(x.getElementsByTag(headTag).text());

    }

    public void setStopWords(Set<String> stopWords) throws IOException {
            File file=new File("C:\\Users\\alina\\Desktop\\Stop_Words.txt");
        String []stopWordsArray =(String.join("\n", Files.readAllLines(file.toPath()))).split("\n");
        for (int i=0;i<stopWordsArray.length;i++)
            stopWords.add(stopWordsArray[i]);
    }
}
