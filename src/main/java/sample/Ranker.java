package sample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Ranker {
    private String postingPath;
    private int c_w_q;
    private int numDocs;
    private int df_w;
    private double avdl;
    private int dLength;
    private int c_w_d;
    private double b = 0.75;
    private double k = 1.2;
    private double sum =0;

    public Ranker(String postingPath) {
        this.postingPath = postingPath;
    }

    public HashMap<Queryy, HashMap<String, Double>> rank(HashMap<Queryy, HashSet<String>> queries,List<String>cities) {
        readDictionaryToMemory();
        HashMap<Queryy, HashMap<String, Double>> finalresults = new HashMap<>();
        for(Queryy query : queries.keySet()) {
            HashSet<String> finalTokens = queries.get(query);
            HashMap<String, Integer> finalUniqueTokens = getUniqueTokens(finalTokens);
            HashMap<String, Double> rankedDocs = new HashMap<>();
            HashMap<String, HashMap<String, PostEntry>> posts = getPostEntery(finalUniqueTokens);
            HashMap<String, Doc> docs = readDocsFromDisc();

            avdl = docs.values().stream().mapToInt((x) -> x.getLength()).average().orElse(-1);
            numDocs = docs.size();
            for (Doc doc : docs.values()) {
                if(cities!=null &&!cities.isEmpty())
                    if(!cities.contains(doc.getCity()))
                        continue;

                int count = 0;
                boolean isMaxterm=false;
                sum = 0;
                for (String term : posts.keySet()) {
                    PostEntry postEntry = posts.get(term).get(doc.getDocNumber());
                    if (postEntry != null) {
                        dLength = doc.getLength();
                        c_w_d = postEntry.getTf();
                        c_w_q = finalUniqueTokens.get(term);
                        df_w = Main.indexer.getDic().get(term).getDf();

                        //the formula BM25
                        double tmp = ((k + 1) * c_w_d) / (c_w_d + k * (1 - b + b * dLength / avdl));
                        double tmp1 = Math.log((numDocs + 1) / df_w);
                        sum += (c_w_q * tmp * tmp1);
                        if(postEntry.isTitle())
                            count++;
                        if(postEntry.getTerm().toLowerCase().compareTo(doc.getMaxTerm().toLowerCase())==0)
                            isMaxterm=true;
                    }
                }
                sum=sum*Math.pow(1.2,count);
                if(isMaxterm)
                    sum=sum*1.1;
                rankedDocs.put(doc.getDocNumber(), sum);
            }
            HashMap<String, Double> top50=new HashMap<>() ;
            sortRankedDocs(rankedDocs);
            if(rankedDocs.size()>50)
                for (int i=0;i<50;i++) {
                    if(rankedDocs.get(i)>0)
                        top50.put(rankedDocs.keySet().toArray()[i].toString(), rankedDocs.get(i));
                }
            else
                for(int i=0;i<rankedDocs.size();i++)
                    if(rankedDocs.get(i)>0)
                        top50.put(rankedDocs.keySet().toArray()[i].toString(), rankedDocs.get(i));

            finalresults.put(query,top50);
        }
        return finalresults;
    }

    private HashMap<String, Double> sortRankedDocs(HashMap<String, Double> unsortMap) {
        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue())*(-1);
            }
        });

        HashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private HashMap<String, Integer> getUniqueTokens(HashSet<String> finaltokens){
        HashMap<String, Integer> uniqueTokens = new HashMap<>();
        for(String token : finaltokens) {
            if (uniqueTokens.containsKey(token)) {
                Integer integer = new Integer(uniqueTokens.get(token).intValue()+1);
                uniqueTokens.put(token, integer);
            } else
                uniqueTokens.put(token, 1);
        }

        return uniqueTokens;
    }

    private HashMap<String, Doc> readDocsFromDisc(){
        return separate(readFromDisc("Documents.txt"));
    }

    private HashSet<String> readFromDisc(String file){
        String line = null;
        try {
            HashSet<String> lines = new HashSet<>();
            FileReader fileReader = new FileReader(postingPath + "\\" + file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines;
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + postingPath + "'");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private HashMap<String, Doc> separate(HashSet<String> lines){
        HashMap<String, Doc> docs = new HashMap<>();
        for(String line : lines){
            String[] parts = line.split(" ");
            Doc doc = new Doc();
            doc.setDocNumber(parts[0]);
            for(int i=1; i<parts.length; i++){
                String[] parts2 = parts[i].split("=");
                if(parts2[0].equals("maxtf"))
                    doc.setMaxtf(Integer.valueOf(parts2[1]));
                if(parts2[0].equals("uniqueWords"))
                    doc.setNumOfWords(Integer.valueOf(parts2[1]));
                if(parts2[0].equals("length"))
                    doc.setLength(Integer.valueOf(parts2[1]));
                if(parts2[0].equals("city"))
                    doc.setCity(parts2[1]);
            }
            docs.put(doc.getDocNumber(), doc);
        }
        return docs;
    }

    private HashMap<String, HashMap<String, PostEntry>> getPostEntery(HashMap<String, Integer> terms){
        String line = null;
        HashMap<String, HashMap<String, PostEntry>> allPosts = new HashMap<>();
        for(String term : terms.keySet()) {
            try {
                FileReader fileReader = new FileReader(postingPath + "\\" + term.charAt(0) + ".txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(" ->");
                    if (terms.containsKey(parts[0])) {
                        HashMap<String, PostEntry> posts = new HashMap<>();
                        String[] d = parts[1].split(", ");
                        for (int i = 0; i < d.length; i++) {
                            d[i].substring(1, parts[1].length()-1);
                            String[] parts2 = d[i].split("|");
                            PostEntry postEntry = new PostEntry(term,parts2[0], parts2[2].equals("V") ? true : false);
                            postEntry.setTf(Integer.valueOf(parts2[1]));
                            posts.put(postEntry.getDocNumber(), postEntry);
                        }
                        allPosts.put(parts[0], posts);
                    }
                }
                bufferedReader.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Unable to open file '" + postingPath + "'");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return allPosts;
    }

    private void readDictionaryToMemory() {
        HashSet<String> lines = readFromDisc("Dictionary.txt");
        for (String line : lines) {
            String[] parts = line.split(" ");
        }
    }
}
