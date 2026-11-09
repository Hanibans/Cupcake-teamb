package persistence;

import entities.CupcakeBase;
import entities.CupcakeTopping;
import entities.Cupcakes;
import exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper {

    private static final Logger logger = LoggerFactory.getLogger(CupcakeMapper.class);
    private final ConnectionPool connectionPool;

    public CupcakeMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<Cupcakes> getAllCupcakes() throws DatabaseException {
        List<Cupcakes> cupcakes = new ArrayList<>();
        String sql = "SELECT base_id, topping_id, flavor, price FROM cupcake";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cupcakes.add(new Cupcakes(rs.getInt("cupcake_id"), rs.getString("flavor"), rs.getInt("price")) {
                });
            }
        } catch (SQLException e) {
            logger.error("Could not get bases", e);
            throw new DatabaseException("Kunne ikke hente bunde", e.getMessage());
        }
        return cupcakes;
    }

    public List<CupcakeBase> getAllBases() throws DatabaseException {
        List<CupcakeBase> bases = new ArrayList<>();
        String sql = "SELECT base_id, base_flavor, base_price FROM cupcake_base";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bases.add(new CupcakeBase(rs.getInt("base_id"), rs.getString("base_flavor"), rs.getInt("base_price")));
            }
        } catch (SQLException e) {
            logger.error("Could not get bases", e);
            throw new DatabaseException("Kunne ikke hente bunde", e.getMessage());
        }
        return bases;
    }

    public List<CupcakeTopping> getAllToppings() throws DatabaseException {
        List<CupcakeTopping> toppings = new ArrayList<>();
        String sql = "SELECT topping_id, topping_flavor, topping_price FROM cupcake_topping";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                toppings.add(new CupcakeTopping(rs.getInt("topping_id"), rs.getString("topping_flavor"), rs.getInt("topping_price")));
            }
        } catch (SQLException e) {
            logger.error("Could not get toppings", e);
            throw new DatabaseException("Kunne ikke hente toppings", e.getMessage());
        }
        return toppings;
    }

    public CupcakeBase getBaseById(int id) throws DatabaseException {
        String sql = "SELECT base_id, base_flavor, base_price FROM cupcake_base WHERE base_id = ?";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CupcakeBase(rs.getInt("base_id"), rs.getString("base_flavor"), rs.getInt("base_price"));
                }
            }
        } catch (SQLException e) {
            logger.error("Could not get base", e);
            throw new DatabaseException("Kunne ikke hente bund", e.getMessage());
        }
        return null;
    }

    public CupcakeTopping getToppingById(int id) throws DatabaseException {
        String sql = "SELECT topping_id, topping_flavor, topping_price FROM cupcake_topping WHERE topping_id = ?";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CupcakeTopping(rs.getInt("topping_id"), rs.getString("topping_flavor"), rs.getInt("topping_price"));
                }
            }
        } catch (SQLException e) {
            logger.error("Could not get topping", e);
            throw new DatabaseException("Kunne ikke hente topping", e.getMessage());
        }
        return null;
    }


    public Cupcakes getCupcakeById(int id) throws DatabaseException {
        String sql = "SELECT topping_id, flavor, price FROM cupcake_topping WHERE cupcake_id = ?";
        try (Connection c = connectionPool.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cupcakes(rs.getInt("cupcake_id"), rs.getString("flavor"), rs.getInt("price")) {
                    };
                }
            }
        } catch (SQLException e) {
            logger.error("Could not get topping", e);
            throw new DatabaseException("Kunne ikke hente cupcake", e.getMessage());
        }
        return null;
    }

}