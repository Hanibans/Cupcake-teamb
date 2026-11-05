package entities;

public class Payment {

    private User user;
    private Order order;
    private double totalCost;
    private boolean isPaid;

    public Payment(Order order, double totalCost, User user, boolean isPaid) {
        this.order = order;
        this.totalCost = totalCost;
        this.user = user;
        this.isPaid = isPaid;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



}
