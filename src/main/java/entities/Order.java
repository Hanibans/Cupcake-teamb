package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;
    private LocalDateTime Date;
    private int totalPrice;
    private String orderStatus;
    private List<Orderline> orderlines = new ArrayList<>();

    public Order(int orderId, LocalDateTime date, int totalPrice, String orderStatus) {
        this.orderId = orderId;
        Date = date;
        totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public Order(LocalDateTime date, int totalPrice, String orderStatus) {
        Date = date;
        this.totalPrice = totalPrice;
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

    public int getTotalPrice(){
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void addOrderline(Orderline orderline) {
        orderlines.add(orderline);
        totalPrice += orderline.getLinePrice();
    }

    public void removeOrderline(Orderline orderline) {
        orderlines.remove(orderline);
    }

    public List<Orderline> getOrderlines() {
        return orderlines;
    }



}
