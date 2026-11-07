package services;

import entities.CupcakeBase;
import entities.CupcakeTopping;
import entities.Orderline;
import exceptions.DatabaseException;
import persistence.ConnectionPool;
import persistence.CupcakeMapper;

import java.util.List;

public class CupcakeService {

    private ConnectionPool connectionPool;
    private CupcakeMapper cupcakeMapper;

    public CupcakeService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.cupcakeMapper = new CupcakeMapper(connectionPool);
    }

    public List<CupcakeBase> getAllBases() throws DatabaseException {
        return cupcakeMapper.getAllBases();
    }

    public List<CupcakeTopping> getAllToppings() throws DatabaseException {
        return cupcakeMapper.getAllToppings();
    }

    public Orderline createOrderline(int baseId, int toppingId, int quantity) throws DatabaseException {
        if (quantity < 1){
            throw new DatabaseException("Antal skal være mindst 1.");
        }

        CupcakeBase base = cupcakeMapper.getBaseById(baseId);
        CupcakeTopping topping = cupcakeMapper.getToppingById(toppingId);
        if (base == null || topping == null){
            throw new DatabaseException("Ugyldig bund eller topping.");
        }

        return new Orderline(base, topping, quantity);
    }


}
