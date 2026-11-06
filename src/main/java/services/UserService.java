package services;

import entities.User;
import exceptions.DatabaseException;
import exceptions.IllegalUserDataException;
import persistence.ConnectionPool;
import persistence.UserMapper;

public class UserService {
    private ConnectionPool connectionPool;
    private UserMapper userMapper;

    public UserService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.userMapper = new UserMapper(connectionPool);
    }

    public User createUser(String name, String email, String phoneNumber, String password) throws IllegalUserDataException {
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validatePassword(password);

        try {
            if (userMapper.userExists(email, phoneNumber)) {
                throw new IllegalUserDataException("Der findes allerede en konto med denne email eller dette telefonnummer.");
            }
            return userMapper.createUser(new User(name, email, phoneNumber, password));
        } catch (DatabaseException e) {
            throw new IllegalUserDataException("Kunne ikke oprette brugeren. Prøv igen senere.");
        }
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        try {
            User user = userMapper.getUserByMail(email);
            if (user == null || !user.getPassword().equals(password)) {
                return null;
            }

            return user;
        } catch (DatabaseException e) {
            return null;
        }
    }

    public void validateEmail(String email) throws IllegalUserDataException {
        if (email == null || !email.contains("@")) {
            throw new IllegalUserDataException("Indtast en gyldig email.");
        }
    }

    public void validatePhoneNumber(String phoneNumber) throws IllegalUserDataException {
        if (phoneNumber == null || phoneNumber.length() != 8 || !phoneNumber.matches("\\d{8}")) {
            throw new IllegalUserDataException("Telefonnummer skal bestå af præcis 8 cifre.");
        }
    }

    public void validatePassword(String password) throws IllegalUserDataException {
        if (password == null || password.length() < 8 || password.length() > 15) {
            throw new IllegalUserDataException("Adgangskoden skal være mellem 8 og 15 tegn.");
        }
    }
}
