package sample;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.util.*;

public class Searcher {
    HashMap<Queryy, HashSet<String>> finalTokens = new HashMap<>();
    Parse parse;

    public HashMap<Queryy, HashSet<String>> search(Queue<Queryy> queryys,boolean isStem,boolean isSemantic) throws UnirestException {
        parse=new Parse();
        while(!queryys.isEmpty()) {
            Queryy query=queryys.poll();
            parse.doQuereyparse(query, isStem, finalTokens);
        }
        removeStopwords();
        if (isSemantic)
            getSemanitcWord(finalTokens);
        return finalTokens;
    }

    private void removeStopwords() {
        for (int i = 0; i < Main.stopWords.size(); i++) {
            finalTokens.remove(Main.stopWords.toArray()[i].toString().toLowerCase());
            finalTokens.remove(Main.stopWords.toArray()[i].toString().toUpperCase());
            String tmp = Character.toUpperCase((Main.stopWords.toArray()[i].toString().toCharArray()[0])) + (Main.stopWords.toArray()[i].toString()).substring(1).toLowerCase();
            finalTokens.remove(tmp);

        }
    }

    private void getSemanitcWord( HashMap<Queryy, HashSet<String>> words) throws UnirestException {
        HashSet<String> tmp=new HashSet<>();
        String addWord;
        for (HashSet<String> x:words.values()) {
            for(String y : x) {
                HttpResponse<JsonNode> response = Unirest.get("https://api.datamuse.com/words?ml=" + y)
                        .header("X-Mashape-Key", "<required>")
                        .header("Accept", "application/json")
                        .asJson();
                Object map = (response.getBody().getArray());
                if(map!=null&&((JSONArray) map).length()!=0)
                    if(((JSONArray) map).length()>5)
                        for (int i = 0; i<5; i++)
                        {
                             addWord=((JSONArray) map).getJSONObject(i).get("word").toString();
                            tmp.add(addWord);
                        }
                    else
                        for(int i=0;i<((JSONArray) map).length();i++)
                        {
                            addWord=((JSONArray) map).getJSONObject(i).get("word").toString();
                            tmp.add(addWord);
                        }
            }
            for (String i:tmp)
                x.add(i);
            tmp.clear();
        }

    }
}