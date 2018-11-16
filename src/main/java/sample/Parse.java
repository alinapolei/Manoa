package sample;
import org.jsoup.helper.StringUtil;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Parse {
    DateFormatSymbols dateFormatSymbols;
    List<String> months;
    List<String> shortMonths;
    Stemmer stemmer;
    HashSet<String> terms;
    HashSet<String> lowCharterms;
    HashSet<String> upCharterms;
    HashSet<String> mixterms;
    String[] tokens;
    public Parse() { //docs is the documents after readFile separate them all
        dateFormatSymbols = new DateFormatSymbols(new Locale("en","US"));
        months = Arrays.asList(dateFormatSymbols.getMonths());
        months.replaceAll(String::toLowerCase);
        shortMonths = Arrays.asList(dateFormatSymbols.getShortMonths());
        shortMonths.replaceAll(String::toLowerCase);
        stemmer = new Stemmer();
        terms = new HashSet<>();
        mixterms=new HashSet<>();upCharterms=new HashSet<>();lowCharterms=new HashSet();





        //System.out.println("done");
    }

    public void doParse(HashSet<Doc> docs){
        for(Doc doc : docs) {
            tokens= doc.docToString().split(" ");
            for (int i = 0; i < tokens.length; i++) {
                String tok = tokens[i];
                boolean isDollar = false;
                //while ((!tok.matches("^\\w.*") || tok.startsWith("\n")) && !tok.equals("") && !tok.startsWith("$") && !tok.startsWith("\""))
                while (tok.length() > 0 && ((!Character.isLetter(tok.charAt(0)) && !Character.isDigit(tok.charAt(0))) || tok.startsWith("\n")) && !tok.equals("") && !tok.startsWith("$") && !tok.startsWith("\""))
                    tok = tok.substring(1);
                //while ((!tok.matches(".*\\w$") || tok.endsWith("\n")) && !tok.endsWith("%") && !tok.endsWith("$") && !tok.equals("") && !tok.startsWith("\""))
                while (tok.length() > 0 && ((!Character.isLetter(tok.charAt(tok.length() - 1)) && !Character.isDigit(tok.charAt(tok.length() - 1))) || tok.endsWith("\n")) && !tok.endsWith("%") && !tok.endsWith("$") && !tok.equals("") && !tok.startsWith("\""))
                    tok = tok.substring(0, tok.length() - 1);
                if (tok.equals("")) continue;


                if (tok.startsWith("\"")) {
                    i++;
                    while (i < tokens.length && !tokens[i].endsWith("\"")) {
                        tok += (" " + tokens[i]);
                        i++;
                    }
                    if (i < tokens.length)
                        tok = tok + " " + tokens[i];

                } else if (tok.contains("-")) {
                    terms.add(tok);

                    continue;
                }


                else{
                    if(isAlpha(tok)){

                        //------put here stop words and parse for words
                        //if (tok.startsWith("\n"))
                        //    tok = tok.replace("\n", "");
                        if (tok.equals("between") && i + 3 < tokens.length && tokens[i + 2].equals("and")) {
                            tok = tok + tokens[i + 1] + tokens[i + 2] + tokens[i + 3];
                            tokens[i + 1] = "";
                            tokens[i + 2] = "";
                            tokens[i + 3] = "";

                        }
                        else if (!tok.equals("") && i + 1 < tokens.length && !tokens[i + 1].equals("") && StringUtil.isNumeric(tokens[i + 1])) {

                            if(months.contains(tok.toLowerCase())){
                                if (tokens[i + 1].length() == 4) //May 1994 -> 1994-05
                                    tok = tokens[i+1]+"-"+(months.indexOf(tok)+1);
                                else //JUNE 4 -> 06-04
                                    tok = (months.indexOf(tok)+1)+"-"+tokens[i+1];
                                tokens[i + 1] = "";
                            }
                            else if (shortMonths.contains(tok.toLowerCase())){
                                if (tokens[i + 1].length() == 4) //May 1994 -> 1994-05
                                    tok = tokens[i+1]+"-"+(shortMonths.indexOf(tok)+1);
                                else //JUNE 4 -> 06-04
                                    tok = (shortMonths.indexOf(tok)+1)+"-"+tokens[i+1];
                                tokens[i + 1] = "";
                            }

                        }
                        else {

                            tok = stemmer.stem(tok);
                            String tmp = Character.toUpperCase(tok.toCharArray()[0]) + (tok.substring(1).toLowerCase());
                            if (tok.equals(tmp))
                                mixterms.add(tmp);
                            else if (tok.equals(tok.toLowerCase()))
                                lowCharterms.add(tok);
                            else
                                upCharterms.add(tok);

                            continue;
                        }

                            /*String tmp = Character.toUpperCase(tok.toCharArray()[0]) + (tok.substring(1).toLowerCase());
                            if (tok.equals(tok.toUpperCase())) {
                                if (terms.contains(tok.toLowerCase()) || terms.contains(tmp)) {
                                    terms.remove(tok.toLowerCase());
                                    terms.remove(tmp);
                                    tok = (tok.toLowerCase());
                                }
                            } else if (tok.equals(tok.toLowerCase())) {
                                if (terms.contains(tok.toUpperCase()) || terms.contains(tmp)) {
                                    terms.remove(tok.toUpperCase());
                                    terms.remove(tmp);
                                    tok = (tok.toLowerCase());
                                }
                            } else if (tok.equals(tmp)) {
                                if (terms.contains(tok.toUpperCase()) || terms.contains(tok.toLowerCase())) {
                                    terms.remove(tok);
                                    tok = (tok.toLowerCase());
                                }
                            }*/
                    }
                    else{
                        if (tok.endsWith("$") || tok.startsWith("$")) {
                            tok = tok.replace("$", "");
                            if (tok.matches("\\d{1,3}\\,\\d\\d\\d"))
                                tok = tok + " Dollars";
                            else
                                isDollar = true;
                        }
                        else if (i + 3 < tokens.length && tokens[i + 2].equals("U.S.") && tokens[i + 3].equals("dollars")) {
                            isDollar = true;
                            tokens[i + 2] = "";
                            tokens[i + 3] = "";
                        }
                        boolean isNumWithDot = tok.matches("[0-9]+\\.[0-9]+");
                        boolean isNum = isNum(tok);
                        if ((isNum || isNumWithDot)) {
                            if(i + 1 < tokens.length) {
                                //number + Thousand/Million/Billion/Trillion
                                if (tokens[i + 1].toLowerCase().matches("thousand")) {
                                    tok = tok + "K";
                                    tokens[i + 1] = "";
                                } else if (tokens[i + 1].toLowerCase().matches("million")) {
                                    tok = tok + "M";
                                    tokens[i + 1] = "";
                                } else if (tokens[i + 1].toLowerCase().matches("billion")) {
                                    if (isDollar)
                                        tok = tok + "000M";
                                    else
                                        tok = tok + "B";
                                    tokens[i + 1] = "";
                                } else if (tokens[i + 1].toLowerCase().matches("trillion")) {
                                    if (isDollar)
                                        tok = tok + "000000M";
                                    else
                                        tok = tok + "000B";
                                    tokens[i + 1] = "";
                                } else if (tokens[i + 1].matches("percent") || tokens[i + 1].matches("percentage")) {
                                    tok = tok + "%";
                                    tokens[i + 1] = "";
                                } else if (tokens[i + 1].matches("[1-9]+\\/[1-9]+")) {
                                    tok = tok + " " + tokens[i + 1];
                                    tokens[i + 1] = "";
                                    if (i + 2 < tokens.length && tokens[i + 2].equals("Dollars")) {
                                        tok = tok + " Dollars";
                                        tokens[i + 2] = "";
                                    }
                                } else if (tokens[i + 1].equals("Dollars")) {
                                    tok = tok + " Dollars";
                                    tokens[i + 1] = "";
                                } else if (isNum && !tokens[i + 1].equals("") ) {
                                    //14 May -> 05-14
                                    if(months.contains(tok.toLowerCase())){
                                        tok = (months.indexOf(tokens[i+1])+1)+"-"+tok;
                                        tokens[i + 1] = "";
                                    }
                                    else if (shortMonths.contains(tok.toLowerCase())){
                                        tok = (shortMonths.indexOf(tokens[i+1])+1)+"-"+tok;
                                        tokens[i + 1] = "";
                                    }
                                }
                            }


                            if (isNumWithDot) {

                                String[] parts = tok.split("\\.");
                                if (parts[0].length() > 3 && parts[0].length() <= 6) //1010.56 -> 1.01056K
                                    tok = parts[0].substring(0, parts[0].length() - 3) + "." + parts[0].substring(parts[0].length() - 3) + parts[1] + "K";
                                else if (parts[0].length() > 6 && parts[0].length() <= 9)
                                    tok = parts[0].substring(0, parts[0].length() - 6) + "." + parts[0].substring(parts[0].length() - 6) + parts[1] + "M";
                                else if (parts[0].length() > 9 && parts[0].length() <= 12)
                                    tok = parts[0].substring(0, parts[0].length() - 9) + "." + parts[0].substring(parts[0].length() - 9) + parts[1] + "B";

                            }
                        }

                        else{

                            if (tok.matches("\\d{1,3}\\,\\d\\d\\d")) {
                                //10,123 ->10.123K
                                String[] parts = tok.split(",");
                                parts[1] = parts[1].replaceAll("0*$", "");
                                tok = parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + "K";
                            } else if (tok.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d")) {
                                //10,123,000 ->10.123M
                                String[] parts = tok.split(",");
                                parts[1] = parts[1].replaceAll("0*$", "");
                                parts[2] = parts[2].replaceAll("0*$", "");
                                tok = parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + parts[2] + "M";
                                if(i+1<tokens.length && tokens[i+1].equals("Dollars")){
                                    tok = tok + " Dollars";
                                    tokens[i+1] = "";
                                }
                            } else if (tok.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d\\,\\d\\d\\d")) {
                                //10,123,000 ->10.123M
                                String[] parts = tok.split(",");
                                parts[1] = parts[1].replaceAll("0*$", "");
                                parts[2] = parts[2].replaceAll("0*$", "");
                                parts[3] = parts[3].replaceAll("0*$", "");
                                tok = parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + parts[2] + parts[3] + "B";
                            }

                        }

                        if(i+1<tokens.length && tokens[i+1].equals("Dollars")) {
                            if (tok.endsWith("m") && tok.substring(0, tok.length() - 1).matches("\\d+\\.?\\d+?")) {
                                tok = tok.replace('m', 'M');
                                tok = tok + " Dollars";
                                tokens[i + 1] = "";
                            }
                            else if (tok.endsWith("bn") && tok.substring(0, tok.length() - 2).matches("\\d+\\.?\\d+?")) {
                                tok = tok.replace("bn", "") + "000M";
                                tok = tok + " Dollars";
                                tokens[i + 1] = "";
                            }

                        }

                        if (isDollar)
                            tok = tok + " Dollars";

                    }
                }

                //tokens[i] = tok;
                terms.add(tok);
            }

            //String docc = String.join(" ", tokens);
            //System.out.println(docc);
            //docnumber++;
        }
   }

    public void removeStopWords( Set<String> stopWords) {
        for (int i=0;i<stopWords.size();i++)
            if(terms.contains(stopWords.toArray()[i]))
                terms.remove(stopWords.toArray()[i]);
    }

    private boolean isAlpha(String name) {

        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;

/*
        if(name.contains("[0-9]"))
            if(name.contains(("[$&+,:;=?@#|'<>.-^*()%!]")))
                return true;
       return false;
*/
    }
    private boolean isNum(String name) {

        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;

        /*if(!name.contains("[a-zA-Z]"))
            if(!    name.contains(("[$&+,:;=?@#|'<>.-^*()%!]")))
                return true;
        return false;*/
    }

    public void setAllTerms(){
        Iterator<String> iter = mixterms.iterator();
        while (iter.hasNext()) {
            String tok = iter.next();
            if(upCharterms.contains(tok.toLowerCase())||lowCharterms.contains(tok)) {
                terms.add(tok.toLowerCase());
               // mixterms.remove(tok);
                upCharterms.remove(tok.toUpperCase());
               lowCharterms.remove(tok.toLowerCase());
            }
            else
            {
                terms.add(tok.toUpperCase());
               // mixterms.remove(tok);
            }
            iter.remove();
        }
       iter = upCharterms.iterator();
        while (iter.hasNext()) {
            String tok = iter.next();
            if (lowCharterms.contains(tok.toLowerCase())) {
                terms.add(tok.toLowerCase());
               lowCharterms.remove(tok.toLowerCase());
                //upCharterms.remove(tok);
            }
            else {
                terms.add(tok);
               // upCharterms.remove(tok);
            }
           iter.remove();
        }
        terms.addAll(lowCharterms);

    }

}



