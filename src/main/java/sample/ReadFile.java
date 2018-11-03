package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ReadFile {

    public ReadFile(String path, HashSet<Doc> hashSet,Set<String> stopWords) {
        List<File> allFiles = new ArrayList<File>();
        getAllFiles(path, allFiles);
        separateDocuments(allFiles, hashSet);
        System.out.println("num of files" + allFiles.size());
        try {
            setStopWords(stopWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(hashSet.size());
    }

    public void getAllFiles(String path, List<File> allFiles) {
        File directory = new File(path);
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isFile())
                    allFiles.add(file);
                else if (file.isDirectory())
                    getAllFiles(file.getAbsolutePath(), allFiles);
            }
        }
    }


    private void separateDocuments(List<File> allFiles, HashSet<Doc> hashSet) {

        for (File file : allFiles) {
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
                    System.out.println(hashSet.size());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

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
        File file=new File("C:\\Users\\alina\\Documents\\semester 5\\IR\\Stop_Words.txt");
        String []stopWordsArray =(String.join("\n", Files.readAllLines(file.toPath()))).split("\n");
        for (int i=0;i<stopWordsArray.length;i++)
            stopWords.add(stopWordsArray[i]);
    }




        /*int sumCounters = 0;
        String line = "";
        String content = "";
        for (File file : allFiles){
            int counter = 0;
            int nlines = 0;
            try {
                Scanner input = new Scanner(new BufferedReader(new FileReader(file.getPath())));
                while(input.hasNextLine()) {
                    String str = input.findInLine("<DOC>");
                    if(str != null){
                        if(content != "") {
                            hashSet.add(content);
                            content = "";
                        }
                        counter++;
                        content += "<DOC>\n";
                    }
                    content += line;
                    line = input.nextLine();
                    nlines++;
                }
                hashSet.add(content);
                sumCounters+=counter;
                System.out.println(file.getName()+" : "+counter);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("sum docs:" + sumCounters);
    }*/
}
