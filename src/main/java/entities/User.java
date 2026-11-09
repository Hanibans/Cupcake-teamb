package entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private int accountBalance;
    private boolean isAdmin;
    private List<Order> orders = new ArrayList<>();


    public User(int userId, String email, String password) {
        this.email = email;
        this.password = password;
        this.userId = userId;
    }
    public User(String email, String phoneNumber, String password) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public User(String name, String email, String phoneNumber, String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(int userId, String name, String email, String phoneNumber, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(int userId, String name, String email, String phoneNumber, String password, int accountBalance) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.accountBalance = accountBalance;
    }

    public User(int userId, String name, String email, String phoneNumber, int accountBalance, boolean isAdmin) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.accountBalance = accountBalance;
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrders(Order order) {
        orders.add(order);
    }
}
