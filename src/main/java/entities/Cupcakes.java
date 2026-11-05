package entities;

public abstract class Cupcakes {

    private int id;
    private String flavor;
    private int price;

    public Cupcakes(int id, String flavor, int price) {
        this.id = id;
        this.flavor = flavor;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


}


