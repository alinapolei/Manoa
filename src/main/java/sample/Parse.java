package sample;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Parse {
    public Parse(HashSet<String> docs) {
        //docs is the documents after readFile separate them all
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        List<String> months = Arrays.asList(dateFormatSymbols.getMonths());
        months.replaceAll(String::toLowerCase);
        List<String> shortMonths = Arrays.asList(dateFormatSymbols.getShortMonths());
        shortMonths.replaceAll(String::toLowerCase);

        for (String doc : docs){

            String[] tokens = doc.split(" ");
            for(int i=0; i<tokens.length; i++){
                String tok = tokens[i];
                boolean isDollar = false;

                if(!tok.equals("") && (tok.charAt(0) == '$' || tok.charAt(tok.length()-1) == '$')) {
                    tok = tok.replace("$", "");
                    isDollar = true;
                }

                if(tok.matches("[0-9]+\\.?[0-9]?+") && i+1<tokens.length){
                    //number + Thousand/Million/Billion/Trillion
                    if(tokens[i+1].matches("[0-9]+\\/[0-9]+"))
                        continue;
                    else if(tokens[i+1].toLowerCase().matches("thousand"))
                        tok = tok + "K";
                    else if(tokens[i+1].toLowerCase().matches("million"))
                        tok = tok + "M";
                    else if(tokens[i+1].toLowerCase().matches("billion"))
                        tok = tok + "B";
                    else if(tokens[i+1].toLowerCase().matches("trillion"))
                        tok = tok +"000B";
                    else if(tokens[i+1].matches("percent") || tokens[i+1].matches("percentage"))
                        tok = tok + "%";
                    else if(months.contains(tokens[i+1].toLowerCase()) || shortMonths.contains(tokens[i+1].toLowerCase())) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
                            Date date = new Date();
                            date.setMonth(new SimpleDateFormat("MMMM").parse(tokens[i+1]).getMonth());
                            date.setDate(Integer.parseInt(tok));
                            tok = simpleDateFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    else
                        continue;
                    tokens[i+1] = "";
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

                if(!tok.equals("") && i+1<tokens.length && !tokens[i+1].equals("") && (months.contains(tok.toLowerCase()) || shortMonths.contains(tok.toLowerCase()))){
                    SimpleDateFormat simpleDateFormat;
                    Calendar date = new GregorianCalendar();

                    if(tokens[i+1].length() == 4){//the date is with year
                        date.set(Calendar.YEAR, Integer.parseInt(tokens[i+1]));
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                    }
                    else {//the date is with month
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

                if(!tok.equals("") && tok.charAt(tok.length()-1) == 'm')
                    tok = tok.replace('m', 'M');
                else if(tok.length()>=2 && tok.charAt(tok.length()-2) == 'b' && tok.charAt(tok.length()-1) == 'n')
                    tok = tok.replace("bn", "B");

                    if(tok.equals("U.S.") && !tokens[i+1].equals("") && tokens[i+1].equals("dollars")) {
                    tok = "Dollars";
                    tokens[i+1] = "";
                }

                if(isDollar)
                    tok = tok + " Dollars";
                tokens[i] = tok;
            }

            doc = String.join(" ", tokens);
            System.out.println(doc);
        }
    }
}
