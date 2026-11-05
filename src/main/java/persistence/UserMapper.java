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



}
