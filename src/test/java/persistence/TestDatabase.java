package persistence;

public class TestDatabase {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "cupcake";

    public static ConnectionPool getPool() {
        return ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    }
}
