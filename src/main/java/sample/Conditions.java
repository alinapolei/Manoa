package sample;

public class Conditions {

    public Conditions() {
    }

    /**
     *
     * @param term
     * @return true if the given term is a word(consists only from letters)
     */
    public boolean isAlpha(String term) {
        char[] chars = term.toCharArray();
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param name
     * @return true if the given name is a number
     */
    public boolean isNum(String name) {

        char[] chars = name.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * unique parsing for population numbers
     * @param number
     * @return parsed number according to the rules of M, B, T as a string
     */
    public String parseNumber(String number){
        boolean isPsik = number.contains(",");

        if(!isPsik) {
            String[] parts = number.split("\\.");
            if (parts[0].length() > 3 && parts[0].length() <= 6) //1010.56 -> 1.01056K
                number = parts[0].substring(0, parts[0].length() - 3) + "." + parts[0].substring(parts[0].length() - 3) + (parts.length == 2 ? parts[1] : "") + "K";
            else if (parts[0].length() > 6 && parts[0].length() <= 9)
                number = parts[0].substring(0, parts[0].length() - 6) + "." + parts[0].substring(parts[0].length() - 6) + (parts.length == 2 ? parts[1] : "") + "M";
            else if (parts[0].length() > 9 && parts[0].length() <= 12)
                number = parts[0].substring(0, parts[0].length() - 9) + "." + parts[0].substring(parts[0].length() - 9) + (parts.length == 2 ? parts[1] : "") + "B";
        }
        else {
            if (number.matches("\\d{1,3}\\,\\d\\d\\d")) {
                //10,123 ->10.123K
                number = getFix2(number);
            } else if (number.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d")) {
                //10,123,000 ->10.123M
                String[] parts = number.split(",");
                parts[2] = parts[2].replaceAll("0*$", "");
                if (parts[2].equals(""))
                    parts[1] = parts[1].replaceAll("0*$", "");
                number = parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + parts[2] + "M";
            } else {
                number = getFix3(number);
            }
        }
        return number;
    }

    static String getFix3(String number) {
        if (number.matches("\\d{1,3}\\,\\d\\d\\d\\,\\d\\d\\d\\,\\d\\d\\d")) {
            //10,123,000 ->10.123M
            String[] parts = number.split(",");
            parts[3] = parts[3].replaceAll("0*$", "");
            if (parts[3].equals("")) {
                parts[2] = parts[2].replaceAll("0*$", "");
                if (parts[2].equals(""))
                    parts[1] = parts[1].replaceAll("0*$", "");
            }
            number = parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + parts[2] + parts[3] + "B";
        }
        return number;
    }

    static String getFix2(String number) {
        String[] parts = number.split(",");
        parts[1] = parts[1].replaceAll("0*$", "");
        number = parts[0] + (!parts[1].equals("") ? "." + parts[1] : parts[1]) + "K";
        return number;
    }
}