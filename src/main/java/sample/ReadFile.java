package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class ReadFile {

    public ReadFile(String path, HashSet<Element> hashSet) {
        List<File> allFiles = new ArrayList<File>();
        getAllFiles(path, allFiles);
        separateDocuments(allFiles, hashSet);
        System.out.println("num of files" + allFiles.size());
        System.out.println(hashSet.size());
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


    private void separateDocuments(List<File> allFiles, HashSet<Element> hashSet) {

        for (File file : allFiles) {
            try {
                int i=0;
                Document doc = Jsoup.parse(file, "utf-8");
                Elements x = doc.getElementsByTag("Doc");
                System.out.println(x.size());
                for (i=0;i<x.size();i++)
                    hashSet.add(x.get(i));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
