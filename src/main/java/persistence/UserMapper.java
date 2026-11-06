package persistence;

import entities.User;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    ConnectionPool connectionPool;
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public UserMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public User getUserById(int userId) throws DatabaseException, SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
                prepareStatement.setInt(1, userId);
                ResultSet resultSet = prepareStatement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String mail = resultSet.getString("mail");
                    String password = resultSet.getString("password");

                    User user = new User(id, mail, password);
                    user.setUserId(resultSet.getInt("user_id"));

                    return user;
                }
            } catch (SQLException e) {
                throw new DatabaseException("Could not get users from the database", e.getMessage());
            }
        }
        return null;
    }

    public User getUserByMail(String mail) throws DatabaseException {
        String sql = "SELECT user_id, name, mail, password, phone_number, accountBalance " +
                "FROM users WHERE mail = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, mail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("mail"),
                            rs.getString("phone_number"),
                            rs.getString("password")
                    );

                    user.setUserId(rs.getInt("user_id"));

                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Fejl ved hentning af bruger", e);
            throw new DatabaseException("Fejl ved hentning af bruger", e.getMessage());
        }

        return null;
    }


    public User createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (name, mail, password, phone_number) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhoneNumber());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt("user_id"));
                } else {
                    throw new DatabaseException("Brugeren kunne ikke oprettes");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DatabaseException("Brugeren blev ikke gemt");
        }
        return user;
    }

    public List<User> getAllUsers() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(sql);
                ResultSet resultSet = prepareStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                String mail = resultSet.getString("mail");
                String password = resultSet.getString("password");
                String phonenumber = resultSet.getString("phoneNumber");
                users.add(new User(mail, password, phonenumber));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get users from the database", e.getMessage());
        }
        return users;
    }


    public boolean userExists(String email, String password) {
        if (email != null && password != null){}
        return false;
    }

    public void updateEmail(int userId, String newEmail)
            throws DatabaseException {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Kunne ikke ændre email", e);
            throw new DatabaseException("Kunne ikke ændre email. Kontrollér, om emailen allerede bruges.", e.getMessage());
        }
    }

    public void updatePhoneNumber(int userId, String newPhoneNumber)
            throws DatabaseException {
        String sql = "UPDATE users SET phone_number = ? WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPhoneNumber);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Kunne ikke ændre telefonnummer", e);
            throw new DatabaseException("Kunne ikke ændre telefonnummer.", e.getMessage());
        }
    }

    public void updatePassword(int userId, String newPassword)
            throws DatabaseException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, userId);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DatabaseException("Brugeren blev ikke fundet.");
            }

        } catch (SQLException e) {
            logger.error("Kunne ikke ændre adgangskode", e);
            throw new DatabaseException("Kunne ikke ændre adgangskoden.", e.getMessage());
        }
    }

}