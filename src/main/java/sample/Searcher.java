package sample;


import java.util.*;

public class Searcher {
    HashSet<String> finalTokens = new HashSet<>();
    Parse parse;

    public HashSet<String> search(Queue<Queryy> query,boolean isStem) {
        parse=new Parse();
        parse.doQuereyparse(query,isStem,finalTokens);
        removeStopwords();
        return finalTokens;
    }

    private void removeStopwords() {
        for (int i =0;i<Main.stopWords.size();i++)
        {
            finalTokens.remove(Main.stopWords.toArray()[i].toString().toLowerCase());
            finalTokens.remove(Main.stopWords.toArray()[i].toString().toUpperCase());
            String tmp=Character.toUpperCase((Main.stopWords.toArray()[i].toString().toCharArray()[0]))+(Main.stopWords.toArray()[i].toString()).substring(1).toLowerCase();
            finalTokens.remove(tmp);

        }
    }

}