package sample;
import org.jsoup.helper.StringUtil;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Parse {
    DateFormatSymbols dateFormatSymbols;
    List<String> months;
    List<String> shortMonths;
    Stemmer stemmer;
    String [] tokens;

    public Parse() {
        //docs is the documents after readFile separate them all
        //index =new Indexer();
    }

    /**
     * parse all the tokens in the docs queue and enters the terms to appropriate dictionaries in the indexere
     * @param dc - the document's to parse
     * @param isStem - if the parser need to do stemming
     */
    public void doParse(Queue<Doc> dc, boolean isStem) {
        initParameters();
          while(!dc.isEmpty()){
              Doc doc=dc.poll();
              Main.allDocs.put(doc.getDocNumber(),doc);
              String[] docParts = doc.docToString();
            for (int j=0; j<docParts.length; j++) {
                String part = docParts[j];
                boolean isTitle = (j==0);
                tokens = part.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    StringBuilder tok = new StringBuilder(tokens[i]);
                    boolean isDollar = false;
                    tok = new StringBuilder(cleanTok(tok.toString()));
                    if (tok.toString().equals("") || tok.toString().equals(" ")) continue;
                    if (tok.toString().startsWith("\"")) {
                        tok=new StringBuilder( tok.toString().replaceFirst("\"", ""));
                        int c = i;
                        StringBuilder cc = new StringBuilder(tok);
                        if (!tok.toString().endsWith("\"")) {
                            tok = getStringBuilder(i, tok);
                            Main.indexer.setDic(tok.toString(), doc, isTitle);
                            i=c;
                            tok = cc;
                        }
                        tok=new StringBuilder(tok.toString().replace("\"", ""));
                        if (tok.toString().equals("") || tok.toString().equals(" ")) continue;
                        word(tok.toString(), isStem, doc, isTitle, i);
                        continue;
                    } else if (tok.toString().contains("-")) {
                        Main.indexer.setDic(tok.toString(), doc, isTitle);
                        continue;
                    } else {
                        if (Main.con.isAlpha(tok.toString())) {
                            //------put here stop words and parse for words
                            if (tok.toString().equals("between") && i + 3 < tokens.length && tokens[i + 2].equals("and")) {
                                B(i, tok);
                            } else if (!tok.toString().equals("") && i + 1 < tokens.length && !tokens[i + 1].equals("") && StringUtil.isNumeric(tokens[i + 1])
                                    && (months.contains(tok.toString().toLowerCase()) || shortMonths.contains(tok.toString().toLowerCase()))) {
                                tok = A(i, tok);
                            } else {
                                word(tok.toString(), isStem, doc, isTitle, i);
                                continue;
                            }
                        } else {
                            tok = C(i, tok, isDollar);

                        }
                    }
                    Main.indexer.setDic(tok.toString(), doc, isTitle);
                }
            }
        }
    }

    private void initParameters() {
        dateFormatSymbols = new DateFormatSymbols(new Locale("en", "US"));
        months = Arrays.asList(dateFormatSymbols.getMonths());
        months.replaceAll(String::toLowerCase);
        shortMonths = Arrays.asList(dateFormatSymbols.getShortMonths());
        shortMonths.replaceAll(String::toLowerCase);
        stemmer = new Stemmer();
    }

    /**
     *
     * @param tok
     * @return a cleaned string from all characters that are not letter or number, from the begging and the end of the string
     */
    private String cleanTok(String tok){
        while (tok.length() > 0 && ((!Character.isLetter(tok.charAt(0)) && !Character.isDigit(tok.charAt(0))) || tok.startsWith("\n")) && !tok.equals("") && !tok.startsWith("$") && !tok.startsWith("\""))
            tok = tok.substring(1);
        while (tok.length() > 0 && ((!Character.isLetter(tok.charAt(tok.length() - 1)) && !Character.isDigit(tok.charAt(tok.length() - 1))) || tok.endsWith("\n") || tok.endsWith("'s")) && !tok.endsWith("%") && !tok.endsWith("$") && !tok.equals("")&& !tok.endsWith("\""))
            tok = tok.substring(0, tok.length() - 1);

        return tok;
    }

    /**
     * decides which dictionary to put the word
     * @param tok - the word
     * @param isStem - if to do stem for the word
     * @param doc - the document where this word was found
     * @param isTitle - if the wordss was found in the title of the document
     * @param i - the place of the word in the document
     */
    private void word(String tok, boolean isStem, Doc doc, boolean isTitle, int i){
        tok.replace("\"", "");
        tok = cleanTok(tok);
        /*
        if(doc.getCity()!="" && tok.toUpperCase().equals(doc.getCity())){
            Main.cityIndexer.get(doc.getDocNumber()).getDocplace().add(i);
        }*/
        if(doc.getCity()!="" && Main.citycorp.containsKey(tok.toUpperCase())){
            if (Main.citycorp.get((tok.toUpperCase())).getDocplacs().containsKey((doc.getDocNumber())))
                     Main.citycorp.get(tok.toUpperCase()).getDocplacs().get(doc.getDocNumber()).add(i);
            else {
                Main.citycorp.get(tok.toUpperCase()).getDocplacs().put(doc.getDocNumber(), new ArrayList<Integer>());
                Main.citycorp.get(tok.toUpperCase()).getDocplacs().get(doc.getDocNumber()).add(i);
            }
        }

        if(!tok.equals("")) {
            if (isStem)
                tok = stemmer.stem(tok);
            //String tmp = Character.toUpperCase(tok.toCharArray()[0]) + (tok.substring(1).toLowerCase());
            if (tok.equals(Character.toUpperCase(tok.toCharArray()[0]) + (tok.substring(1).toLowerCase())))
                Main.indexer.setMixedTerms(tok, doc, isTitle);
                //mixterms.add(tmp);
            else if (tok.equals(tok.toLowerCase()))
                Main.indexer.setLowerTerms(tok, doc, isTitle);
                //lowCharterms.add(tok);
            else
                Main.indexer.setUpperTerms(tok, doc, isTitle);
            //upCharterms.add(tok);
        }
    }

    /**
     * transfers the chunk to the disk
     * @param path - destination path to write
     */
    public void transferDisk(String path) {
        try {
            Main.indexer.transferToDisk(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doQuereyparse(Queryy query, boolean isStem,HashMap<Queryy, HashSet<String>> finalTokens) {
        initParameters();
        HashSet<String> toks = new HashSet<>();
        tokens = (query.getTitle()+" "+query.getDesc()).split(" ");
        for (int i = 0; i < tokens.length; i++) {
            StringBuilder tok = new StringBuilder(tokens[i]);
            boolean isDollar = false;
            tok = new StringBuilder(cleanTok(tok.toString()));
            if (tok.toString().equals("") || tok.toString().equals(" ")) continue;
            if (tok.toString().startsWith("\"")) {
                tok=new StringBuilder( tok.toString().replaceFirst("\"", ""));
                int c = i;
                StringBuilder cc = new StringBuilder(tok);
                if (!tok.toString().endsWith("\"")) {
                    tok = getStringBuilder(i, tok);
                    toks.add(tok.toString());
                    i=c;
                    tok = cc;
                }
                tok=new StringBuilder(tok.toString().replace("\"", ""));
                if (tok.toString().equals("") || tok.toString().equals(" ")) continue;
                quereyWordThreat(tok.toString(),isStem,toks);
                continue;
            } else if (tok.toString().contains("-")) {
                toks.add(tok.toString());
                continue;
            } else {
                if (Main.con.isAlpha(tok.toString())) {
                    //------put here stop words and parse for words
                    if (tok.toString().equals("between") && i + 3 < tokens.length && tokens[i + 2].equals("and")) {
                        B(i, tok);
                    } else if (!tok.toString().equals("") && i + 1 < tokens.length && !tokens[i + 1].equals("") && StringUtil.isNumeric(tokens[i + 1])
                            && (months.contains(tok.toString().toLowerCase()) || shortMonths.contains(tok.toString().toLowerCase()))) {
                        tok = A(i, tok);
                    } else {
                        quereyWordThreat(tok.toString(),isStem,toks);
                        continue;
                    }
                } else {
                    tok = C(i, tok, isDollar);

                }
            }
            toks.add(tok.toString());
        }
        finalTokens.put(query, toks);
    }

    private StringBuilder C(int i, StringBuilder tok, boolean isDollar) {
        if (tok.toString().endsWith("$") || tok.toString().startsWith("$")) {
            tok=new StringBuilder(tok.toString().replace("$", ""));
            if (tok.toString().matches("\\d{1,3}\\,\\d\\d\\d"))
                tok.append(" Dollars");
            else
                isDollar = true;
        } else if (i + 3 < tokens.length && tokens[i + 2].equals("U.S.") && tokens[i + 3].equals("dollars")) {
            isDollar = true;
            tokens[i + 2] = "";
            tokens[i + 3] = "";
        }
        boolean isNumWithDot = tok.toString().matches("[0-9]+\\.[0-9]+");
        boolean isNum = Main.con.isNum(tok.toString());
        if ((isNum || isNumWithDot)) {
            if (i + 1 < tokens.length) {
                //number + Thousand/Million/Billion/Trillion
                if (tokens[i + 1].toLowerCase().matches("thousand")) {
                    tok .append( "K");
                    tokens[i + 1] = "";
                } else if (tokens[i + 1].toLowerCase().matches("million")) {
                    tok.append( "M");
                    tokens[i + 1] = "";
                } else if (tokens[i + 1].toLowerCase().matches("billion")) {
                    if (isDollar)
                        tok .append( "000M");
                    else
                        tok .append("B");
                    tokens[i + 1] = "";
                } else if (tokens[i + 1].toLowerCase().matches("trillion")) {
                    if (isDollar)
                        tok.append( "000000M");
                    else
                        tok .append("000B");
                    tokens[i + 1] = "";
                } else if (tokens[i + 1].matches("percent") || tokens[i + 1].matches("percentage")) {
                    tok .append("%");
                    tokens[i + 1] = "";
                } else if (tokens[i + 1].matches("[1-9]+\\/[1-9]+")) {
                    tok .append(" " + tokens[i + 1]);
                    tokens[i + 1] = "";
                    if (i + 2 < tokens.length && tokens[i + 2].equals("Dollars")) {
                        tok.append( " Dollars");
                        tokens[i + 2] = "";
                    }
                } else if (tokens[i + 1].equals("Dollars")) {
                    tok .append( " Dollars");
                    tokens[i + 1] = "";
                } else if (isNum && !tokens[i + 1].equals("") && (months.contains(tokens[i + 1].toLowerCase()) || shortMonths.contains(tokens[i + 1].toLowerCase()))) {
                    //14 May -> 05-14
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
                        Calendar date = new GregorianCalendar();
                        date.set(Calendar.MONTH, new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(tokens[i + 1]).getMonth());
                        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tok.toString()));
                        tok =new StringBuilder( simpleDateFormat.format(date.getTime()));
                        tokens[i + 1] = "";
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isNumWithDot) {
                String[] parts = tok.toString().split("\\.");
                if (parts[0].length() > 3 && parts[0].length() <= 6) //1010.56 -> 1.01056K
                    tok=new StringBuilder( parts[0].substring(0, parts[0].length() - 3) + "." + parts[0].substring(parts[0].length() - 3) + parts[1] + "K");
                else if (parts[0].length() > 6 && parts[0].length() <= 9)
                    tok = new StringBuilder(parts[0].substring(0, parts[0].length() - 6) + "." + parts[0].substring(parts[0].length() - 6) + parts[1] + "M");
                else if (parts[0].length() > 9 && parts[0].length() <= 12)
                    tok =new StringBuilder( parts[0].substring(0, parts[0].length() - 9) + "." + parts[0].substring(parts[0].length() - 9) + parts[1] + "B");
            }
        } else {
            if (tok.toString().matches("\\d{1,3}\\,\\d\\d\\d")) {
                if (i + 1 < tokens.length && tokens[i + 1].equals("Dollars")) {
                    tok .append( " Dollars");
                    tokens[i + 1] = "";
                } else {
                    //10,123 ->10.123K
                    String[] parts = tok.toString().split(",");
                    parts[1] = parts[1].replaceAll("0*$", "");
                    tok =new StringBuilder( parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + "K");
                }
            } else if (tok.toString().matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d")) {
                //10,123,000 ->10.123M
                String[] parts = tok.toString().split(",");
                parts[2] = parts[2].replaceAll("0*$", "");
                if (parts[2].equals(""))
                    parts[1] = parts[1].replaceAll("0*$", "");
                tok = new StringBuilder(parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + parts[2] + "M");
                if (i + 1 < tokens.length && tokens[i + 1].equals("Dollars")) {
                    tok.append( " Dollars");
                    tokens[i + 1] = "";
                }
            } else if (tok.toString().matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d\\,\\d\\d\\d")) {
                //10,123,000 ->10.123M
                String[] parts = tok.toString().split(",");
                parts[3] = parts[3].replaceAll("0*$", "");
                if (parts[3].equals("")) {
                    parts[2] = parts[2].replaceAll("0*$", "");
                    if (parts[2].equals(""))
                        parts[1] = parts[1].replaceAll("0*$", "");
                }
                tok = new StringBuilder(parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + parts[2] + parts[3] + "B");
            }

        }

        if (i + 1 < tokens.length && tokens[i + 1].equals("Dollars")) {
            if (tok.toString().endsWith("m") && tok.substring(0, tok.length() - 1).matches("\\d+\\.?\\d?+")) {
                tok=new StringBuilder( tok.toString().replace('m', 'M'));
                tok.append(  " Dollars");
                tokens[i + 1] = "";
            } else if (tok.toString().endsWith("bn") && tok.substring(0, tok.length() - 2).matches("\\d+\\.?\\d?+")) {
                tok =new StringBuilder( tok.toString().replace("bn", "") + "000M");
                tok.append( " Dollars");
                tokens[i + 1] = "";
            }

        }

        if (isDollar)
            tok.append(" Dollars");
        return tok;
    }

    private void B(int i, StringBuilder tok) {
        tok.append( tokens[i + 1] + tokens[i + 2] + tokens[i + 3]);
        tokens[i + 1] = "";
        tokens[i + 2] = "";
        tokens[i + 3] = "";
    }

    private StringBuilder A(int i, StringBuilder tok) {
        SimpleDateFormat simpleDateFormat;
        Calendar date = new GregorianCalendar();
        if (tokens[i + 1].length() == 4) {//May 1994 -> 1994-05
            date.set(Calendar.YEAR, Integer.parseInt(tokens[i + 1]));
            simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        } else {//JUNE 4 -> 06-04
            simpleDateFormat = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
            try {
                date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tokens[i + 1]));
            }catch (Exception e){
                System.out.println(tokens[i+1]);
            }
        }
        try {
            if (tok.toString().compareTo("")!=0) {
                date.set(Calendar.MONTH, new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(tok.toString()).getMonth());
                tok = new StringBuilder(simpleDateFormat.format(date.getTime()));
                tokens[i + 1] = "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tok;
    }

    private StringBuilder getStringBuilder(int i, StringBuilder tok) {
        i++;
        while (i < tokens.length && !tokens[i].endsWith("\"")) {
            tok .append (" " + tokens[i]);
            i++;
        }
        if (i < tokens.length) {
            tok.append( " " + tokens[i]);
            tokens[i] = tokens[i].replace("\"", "");
        }
        tok=new StringBuilder(tok.toString().replace("\"", ""));
        return tok;
    }

    private void quereyWordThreat(String tok,boolean isStem,HashSet<String>finalTokens) {

        if (Main.indexer == null) {
            System.out.println("here");
        } else {
            if (isStem)
                tok = stemmer.stem(tok);
            if (Main.indexer.getDic().containsKey(tok.toLowerCase()))
                finalTokens.add(tok.toLowerCase());
            else if (Main.indexer.getDic().containsKey(tok.toUpperCase()))
                finalTokens.add(tok.toUpperCase());
            else
                finalTokens.add(tok.toUpperCase());
        }
    }
}








