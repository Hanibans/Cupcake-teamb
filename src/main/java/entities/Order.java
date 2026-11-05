package entities;

import java.time.LocalDate;

public class Order {

    private int orderID;
    private LocalDate Date;
    private int Price;
    private String orderStatus;

    public Order(int orderID, LocalDate date, int price, String orderStatus) {
        this.orderID = orderID;
        Date = date;
        Price = price;
        this.orderStatus = orderStatus;
    }


    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public LocalDate getDate() {
        return Date;
    }

    public void setDate(LocalDate date) {
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

}
