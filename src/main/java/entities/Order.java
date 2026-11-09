package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private User user;
    private int orderId;
    private LocalDateTime date;
    private int totalPrice;
    private String orderStatus;
    private List<Orderline> orderlines = new ArrayList<>();
    private Payment payment;

    public Order(int orderId, LocalDateTime date, int totalPrice, String orderStatus) {
        this.orderId = orderId;
        this.date = date;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public Order(LocalDateTime date, int totalPrice, String orderStatus) {
        this.date = date;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public Order(int orderId, LocalDateTime date, int totalPrice, String orderStatus, User user) {
        this.orderId = orderId;
        this.date = date;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.user = user;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }


}
