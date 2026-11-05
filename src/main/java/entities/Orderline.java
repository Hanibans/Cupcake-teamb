package entities;

public class Orderline {
    private User user;
    private Cupcakes cupcakes;
    private int quantity;

    public Orderline(User user, Cupcakes cupcakes, int quantity) {
        this.user = user;
        this.cupcakes = cupcakes;
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cupcakes getCupcakes() {
        return cupcakes;
    }

    public void setCupcakes(Cupcakes cupcakes) {
        this.cupcakes = cupcakes;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
