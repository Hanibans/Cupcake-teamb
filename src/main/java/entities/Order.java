package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;
    private LocalDateTime Date;
    private int Price;
    private String orderStatus;
    private List<Orderline> orderlines = new ArrayList<>();

    public Order(int orderId, LocalDateTime date, int price, String orderStatus) {
        this.orderId = orderId;
        Date = date;
        Price = price;
        this.orderStatus = orderStatus;
    }

    public Order(LocalDateTime date, int price, String orderStatus) {
        Date = date;
        Price = price;
        this.orderStatus = orderStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getDate() {
        return Date;
    }

    public void setDate(LocalDateTime date) {
        Date = date;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void addOrderline(Orderline orderline) {
        orderlines.add(orderline);
    }

    public void removeOrderline(Orderline orderline) {
        orderlines.remove(orderline);
    }

    public List<Orderline> getOrderlines() {
        return orderlines;
    }

}
