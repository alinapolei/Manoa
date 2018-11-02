package sample;

import java.util.HashSet;

public class Parse {
    public Parse(HashSet<String> docs) {
        //docs is the documents after readFile separate them all
        for (String doc : docs){

            String[] tokens = doc.split(" ");
            for(int i=0; i<tokens.length; i++){
                String tok = tokens[i];

                if(tok.matches("[0-9]+\\.?[0-9]?+") && i+1<tokens.length){
                    //number + Thousand/Million/Billion/Trillion
                    if(tokens[i+1].matches("[0-9]+\\/[0-9]+"))
                        continue;
                    else if(tokens[i+1].matches("Thousand"))
                        tok = tok + "K";
                    else if(tokens[i+1].matches("Million"))
                        tok = tok + "M";
                    else if(tokens[i+1].matches("Billion"))
                        tok = tok + "B";
                    else if(tokens[i+1].matches("Trillion"))
                        tok = tok +"00B";
                    else if(tokens[i+1].matches("percent") || tokens[i+1].matches("percentage"))
                        tok = tok + "%";
                    tokens[i+1] = "";
                }
                if(tok != "" && (tok.charAt(0) == '$' || tok.charAt(tok.length()-1) == '$')) {
                    tok = tok.replace("$", "");

                    tok = tok + " Dollars";
                }

                if(tok.matches("\\d{1,3}\\,\\d\\d\\d")) {
                    //10,123 ->10.123K
                    String[] parts = tok.split(",");
                    parts[1] = parts[1].replaceAll("0*$", "");
                    tok = parts[0] + ( !parts[1].equals("") ? "." + parts[1] : parts[1] ) + "K";
                }
                else if(tok.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d")) {
                    //10,123,000 ->10.123M
                    String[] parts = tok.split(",");
                    parts[1] = parts[1].replaceAll("0*$", "");
                    parts[2] = parts[2].replaceAll("0*$", "");
                    tok = parts[0] + "." + ( !parts[1].equals("") ? "." + parts[1] : parts[1] ) + ( !parts[2].equals("") ? "." + parts[2] : parts[2]) + "M";
                }
                else if(tok.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d\\,\\d\\d\\d")){
                    //10,123,000 ->10.123M
                    String[] parts = tok.split(",");
                    parts[1] = parts[1].replaceAll("0*$", "");
                    parts[2] = parts[2].replaceAll("0*$", "");
                    parts[3] = parts[3].replaceAll("0*$", "");
                    tok = parts[0] + "." + ( !parts[1].equals("") ? "." + parts[1] : parts[1] ) + ( !parts[2].equals("") ? "." + parts[2] : parts[2] ) + ( !parts[3].equals("") ? "." + parts[3] : parts[3] ) + "B";
                }

                if(tok.matches("\\d+\\.\\d+")){
                    String[] parts = tok.split("\\.");
                    if(parts[0].length()>3 && parts[0].length()<=6) //1010.56 -> 1.01056K
                        tok = parts[0].substring(0, parts[0].length()-3) + "." + parts[0].substring(parts[0].length()-3) + parts[1] + "K";
                    else if (parts[0].length()>6 && parts[0].length()<=9)
                        tok = parts[0].substring(0, parts[0].length()-6) + "." + parts[0].substring(parts[0].length()-6) + parts[1] + "M";
                    else if(parts[0].length()>9 && parts[0].length()<=12)
                        tok = parts[0].substring(0, parts[0].length()-9) + "." + parts[0].substring(parts[0].length()-9) + parts[1] + "B";
                }
                tokens[i] = tok;
            }

            doc = String.join(" ", tokens);
            System.out.println(doc);
        }
    }
}
