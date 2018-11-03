package sample;
import org.jsoup.helper.StringUtil;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Parse {
    public Parse(HashSet<Doc> docs,HashSet <String> stopWords) { //docs is the documents after readFile separate them all
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> months = Arrays.asList(dateFormatSymbols.getMonths());
        months.replaceAll(String::toLowerCase);
        List<String> shortMonths = Arrays.asList(dateFormatSymbols.getShortMonths());
        shortMonths.replaceAll(String::toLowerCase);




        Stemmer stemmer = new Stemmer();
        Set<String> terms = new HashSet<>();



        for (Doc doc : docs){
            String[] tokens = doc.docToString().split(" ");
            for(int i=0; i<tokens.length; i++){
                String tok = tokens[i];
                boolean isDollar = false;

                //clean the token -> remove , . | \n from the start and the end
                if(tok.equals("")) continue;
                if(tok.startsWith(".") || tok.startsWith(",") || tok.startsWith("|")  || tok.startsWith("(") || tok.startsWith(")"))
                    tok = tok.substring(1, tok.length());
                if(tok.endsWith(".") || tok.endsWith(",") || tok.endsWith("|") || tok.endsWith("(") || tok.endsWith(")"))
                    tok = tok.substring(0, tok.length()-1);

                //parsing for numbers, prises, percent
                if(!tok.equals("") && (tok.charAt(0) == '$' || tok.charAt(tok.length()-1) == '$')) {
                    tok = tok.replace("$", "");
                    isDollar = true;
                }

                if(tok.matches("[0-9]+\\.?[0-9]?+") && i+1<tokens.length && !tokens[i+1].matches("[0-9]+\\/[0-9]+")){
                    //number + Thousand/Million/Billion/Trillion
                    if(tokens[i+1].toLowerCase().matches("thousand")) {
                        tok = tok + "K";
                        tokens[i + 1] = "";
                    }
                    else if(tokens[i+1].toLowerCase().matches("million")) {
                        tok = tok + "M";
                        tokens[i + 1] = "";
                    }
                    else if(tokens[i+1].toLowerCase().matches("billion")) {
                        tok = tok + "B";
                        tokens[i + 1] = "";
                    }
                    else if(tokens[i+1].toLowerCase().matches("trillion")) {
                        tok = tok + "000B";
                        tokens[i + 1] = "";
                    }
                    else if(tokens[i+1].matches("percent") || tokens[i+1].matches("percentage")) {
                        tok = tok + "%";
                        tokens[i + 1] = "";
                    }
                    else if(!tokens[i+1].equals("") && (months.contains(tokens[i+1].toLowerCase()) || shortMonths.contains(tokens[i+1].toLowerCase()))) {
                        //14 May -> 05-14
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
                            Calendar date = new GregorianCalendar();
                            date.set(Calendar.MONTH, new SimpleDateFormat("MMMM").parse(tokens[i+1]).getMonth());
                            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tok));
                            tok = simpleDateFormat.format(date.getTime());
                            tokens[i+1] = "";
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
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
                    tok = parts[0] + ( !parts[1].equals("") ? "." + parts[1] : parts[1] ) + parts[2] + "M";
                }
                else if(tok.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d\\,\\d\\d\\d")){
                    //10,123,000 ->10.123M
                    String[] parts = tok.split(",");
                    parts[1] = parts[1].replaceAll("0*$", "");
                    parts[2] = parts[2].replaceAll("0*$", "");
                    parts[3] = parts[3].replaceAll("0*$", "");
                    tok = parts[0] + ( !parts[1].equals("") ? "." + parts[1] : parts[1] ) + parts[2] + parts[3] + "B";
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

                if(!tok.equals("") && i+1<tokens.length && !tokens[i+1].equals("") && StringUtil.isNumeric(tokens[i+1])
                        && (months.contains(tok.toLowerCase()) || shortMonths.contains(tok.toLowerCase()))){
                    SimpleDateFormat simpleDateFormat;
                    Calendar date = new GregorianCalendar();
                    if(tokens[i+1].length() == 4){//May 1994 -> 1994-05
                        date.set(Calendar.YEAR, Integer.parseInt(tokens[i+1]));
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                    }
                    else {//JUNE 4 -> 06-04
                        simpleDateFormat = new SimpleDateFormat("MM-dd");
                        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tokens[i+1]));
                    }
                    try {
                        date.set(Calendar.MONTH, new SimpleDateFormat("MMMM").parse(tok).getMonth());
                        tok = simpleDateFormat.format(date.getTime());
                        tokens[i + 1] = "";
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if(!tok.equals("") && tok.charAt(tok.length()-1) == 'm' && tok.substring(0, tok.length()-1).matches("\\d+\\.?\\d+?"))
                    tok = tok.replace('m', 'M');
                else if(tok.length()>=2 && tok.charAt(tok.length()-2) == 'b' && tok.charAt(tok.length()-1) == 'n' && tok.substring(0, tok.length()-2).matches("\\d+\\.?\\d+?"))
                    tok = tok.replace("bn", "B");

                if(tok.equals("U.S") && i+1<tokens.length && tokens[i+1].equals("dollars")) {
                    tok = "Dollars";
                    tokens[i+1] = "";
                }

                if(tok.equals("between") && i+3<tokens.length && tokens[i+2].equals("and")) {
                    tok = tok + tokens[i + 1] + tokens[i + 2] + tokens[i + 3];
                    tokens[i+1] = ""; tokens[i+2] = ""; tokens[i+3] = "";
                }

                if(isDollar)
                    tok = tok + " Dollars";

                //parsing for words
                if(tok.matches("[A-Za-z]+")) {
                    tok = stemmer.stem(tok);
                    //------put here stop words and parse for words
                        if (tok.startsWith("\n"))
                            tok = tok.replace("\n", "");
                        String tmp = Character.toUpperCase(tok.toCharArray()[0]) + tok.substring(1, tok.length());
                        if(tok.equals(tok.toUpperCase()))
                            if(terms.contains(tok.toLowerCase())||terms.contains(tmp))
                            {
                                terms.remove(tok);
                                tok=(tok.toLowerCase());
                            }
                        else if (tok.equals(tok.toLowerCase()))
                            if(terms.contains(tok.toUpperCase())||terms.contains(tmp))
                            {
                                terms.remove(tok);
                                tok=(tok.toLowerCase());
                            }
                        else if (tok.equals(tmp))
                            if(terms.contains(tok.toUpperCase())||terms.contains(tok.toLowerCase()))
                                tok=(tok.toUpperCase());
                             {
                                terms.remove(tok);
                                tok=(tok.toLowerCase());
                            }
                    }


                tokens[i] = tok;
                terms.add(tok);
            }
            removeStopWords(terms,stopWords);
            String docc = String.join(" ", tokens);
            System.out.println(docc);
        }
    }

    private void removeStopWords(Set<String> terms, HashSet<String> stopWords) {
            for (int i=0;i<stopWords.size();i++)
                if(terms.contains(stopWords.toArray()[i]))
                    terms.remove(stopWords.toArray()[i]);
    }
}
