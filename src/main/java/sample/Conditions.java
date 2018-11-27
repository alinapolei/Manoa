package sample;

public class Conditions {

    public Conditions() {}

    public boolean isAlpha(String term) {
        char[] chars = term.toCharArray();
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    public boolean isNum(String name) {

        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;

         }
}
