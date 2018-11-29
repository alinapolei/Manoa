package sample;

public class City {
    String name;
    String Currency;
    String pop;

    public City(String name, String currency, String pop) {
        this.name = name;
        Currency = currency;
        this.pop = pop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }
}
