package sample;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.util.*;

public class Searcher {
    /**
     * key - the query
     * value - a set of parsed tokens that are in the key query
     */
    HashMap<Queryy, HashSet<String>> finalTokens = new HashMap<>();
    Parse parse;

    /**
     * read the queries in the given queue, separate to tokens and parse them
     * @param queryys - queue of queries
     * @param isStem
     * @param isSemantic
     * @return a hash map with the query as the key and its parsed tokens as the value
     * @throws UnirestException
     */
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

    /**
     * remove stop words from the query's tokens
     */
    private void removeStopwords() {
        for (int i = 0; i < Main.stopWords.size(); i++) {
            finalTokens.remove(Main.stopWords.toArray()[i].toString().toLowerCase());
            finalTokens.remove(Main.stopWords.toArray()[i].toString().toUpperCase());
            String tmp = Character.toUpperCase((Main.stopWords.toArray()[i].toString().toCharArray()[0])) + (Main.stopWords.toArray()[i].toString()).substring(1).toLowerCase();
            finalTokens.remove(tmp);

        }
    }

    /**
     * add to each query 5 words that are semantics for the original words in the query
     * @param words - hash map that stores for every query its list of tokens
     * @throws UnirestException
     */
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