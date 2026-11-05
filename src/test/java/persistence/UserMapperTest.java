package persistence;

import entities.User;
import exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "cupcake";

    private ConnectionPool connectionPool;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() throws SQLException {
        connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
        userMapper = new UserMapper(connectionPool);

        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE SCHEMA IF NOT EXISTS test");
            stmt.execute("DROP TABLE IF EXISTS test.users CASCADE");
            stmt.execute("""
                CREATE TABLE test.users (
                    user_id SERIAL PRIMARY KEY,
                    "isAdmin" BOOLEAN DEFAULT false,
                    name VARCHAR(55),
                    mail VARCHAR(55) UNIQUE,
                    password VARCHAR(255),
                    phone_number VARCHAR(20),
                    account_balance INTEGER DEFAULT 0
                )
                """);
            stmt.execute("""
                INSERT INTO test.users (name, mail, password, phone_number, account_balance)
                VALUES ('Tester', 'test@gmail.dk', '1234', '12345678', 100)
                """);
        }
    }

    @Test
    void createUser() throws DatabaseException {
        User user = new User(1, "gab@gmail.com", "12345678");

        User created = userMapper.createUser(user);

        assertNotNull(created);
        assertEquals("gab@gmail.com", created.getEmail());
        assertFalse(created.getUserId() > 3);
    }

}