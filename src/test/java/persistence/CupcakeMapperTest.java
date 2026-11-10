package persistence;

import entities.CupcakeBase;
import entities.CupcakeTopping;
import exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CupcakeMapperTest {

    private static ConnectionPool connectionPool;
    private static CupcakeMapper cupcakeMapper;

    @BeforeAll
    static void setUpClass() {
        connectionPool = TestDatabase.getPool();
        cupcakeMapper = new CupcakeMapper(connectionPool);
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            // orderline peger på de andre to så den skal fjernes først
            stmt.execute("DELETE FROM test.orderline");
            stmt.execute("DELETE FROM test.cupcake_base");
            stmt.execute("DELETE FROM test.cupcake_topping");

            stmt.execute("ALTER SEQUENCE test.cupcake_base_base_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE test.cupcake_topping_topping_id_seq RESTART WITH 1");

            stmt.execute("""
                INSERT INTO test.cupcake_base (base_flavor, base_price) VALUES
                ('Chocolate', 5),
                ('Vanilla', 5),
                ('Pistacio', 6)
                """);

            stmt.execute("""
                INSERT INTO test.cupcake_topping (topping_flavor, topping_price) VALUES
                ('Blueberry', 5),
                ('Lemon', 8)
                """);

        } catch (SQLException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @Test
    void getAllBases() throws DatabaseException {
        List<CupcakeBase> bases = cupcakeMapper.getAllBases();

        assertEquals(3, bases.size());
        assertEquals("Chocolate", bases.get(0).getFlavor());
        assertEquals(5, bases.get(0).getPrice());
    }

    @Test
    void getAllToppings() throws DatabaseException {
        List<CupcakeTopping> toppings = cupcakeMapper.getAllToppings();

        assertEquals(2, toppings.size());
        assertEquals("Blueberry", toppings.get(0).getFlavor());
    }

    @Test
    void getBaseById() throws DatabaseException {
        CupcakeBase base = cupcakeMapper.getBaseById(3);

        assertEquals(3, base.getCupcakeId());
        assertEquals("Pistacio", base.getFlavor());
        assertEquals(6, base.getPrice());
    }

    @Test
    void getBaseByIdNotFound() throws DatabaseException {
        assertNull(cupcakeMapper.getBaseById(99));
    }

    @Test
    void getToppingById() throws DatabaseException {
        CupcakeTopping topping = cupcakeMapper.getToppingById(2);

        assertEquals(2, topping.getCupcakeId());
        assertEquals("Lemon", topping.getFlavor());
        assertEquals(8, topping.getPrice());
    }

    @Test
    void getToppingByIdNotFound() throws DatabaseException {
        assertNull(cupcakeMapper.getToppingById(99));
    }
}