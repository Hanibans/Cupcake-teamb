package entities;

import java.util.ArrayList;
import java.util.List;

public class Orderline {
    private int orderlineId;
    private Cupcakes cupcakes;
    private CupcakeTopping cupcakeTopping;
    private CupcakeBase cupcakeBase;
    private int quantity;
    private List<Orderline> orderlines = new ArrayList<>();

    public Orderline(Cupcakes cupcakes, int quantity) {
        this.cupcakes = cupcakes;
        this.quantity = quantity;
    }

    public Orderline(CupcakeBase cupcakeBase, CupcakeTopping cupcakeTopping, int quantity) {
        this.cupcakeBase = cupcakeBase;
        this.cupcakeTopping = cupcakeTopping;
        this.quantity = quantity;
    }

    public CupcakeBase getCupcakeBase() {
        return cupcakeBase;
    }

    public CupcakeTopping getCupcakeTopping() {
        return cupcakeTopping;
    }

    public int getOrderlineId() {
        return orderlineId;
    }

    public void setOrderlineId(int orderlineId) {
        this.orderlineId = orderlineId;
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

    public void addOrderline(Orderline orderline) {
        orderlines.add(orderline);
    }
}
