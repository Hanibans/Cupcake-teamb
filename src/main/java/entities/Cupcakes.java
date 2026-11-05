package entities;

public abstract class Cupcakes {

    private int cupcakeId;
    private String flavor;
    private int price;

    public Cupcakes(int cupcakeId, String flavor, int price) {
        this.cupcakeId = cupcakeId;
        this.flavor = flavor;
        this.price = price;
    }

    public int getCupcakeId() {
        return cupcakeId;
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


