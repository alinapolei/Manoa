package sample;

import java.util.HashSet;

public class Parse {
    public Parse(HashSet<String> docs) {
        //docs is the documents after readFile separate them all
        for (String doc : docs){

            String[] tokens = doc.split(" ");
            for(int i=0; i<tokens.length; i++){
                String tok = tokens[i];
                if(tok.startsWith("|"))
                    tok = tok.substring(1);
                if(tok.matches("\\d{1,3}\\,\\d\\d\\d")) {
                    String[] parts = tok.split(",");
                    tokens[i] = parts[0] + "$" + parts[1];
                }
            }
            doc = String.join(" ", tokens);
            System.out.println(doc);
        }
    }
}
