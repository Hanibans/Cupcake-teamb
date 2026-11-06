package services;

import entities.User;
import exceptions.IllegalUserDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.ConnectionPool;
import persistence.UserMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

        private UserService userService;

        @BeforeEach
        void setUp() {
            ConnectionPool connectionPool = null;
            userService = new UserService(connectionPool);
        }

        @Test
        void validateEmailWithoutSign() {
            assertThrows(IllegalUserDataException.class, ()-> userService.validateEmail("gabmail.com"));
        }

        @Test
        void validatePasswordWithLessThanEightSigns() {
            assertThrows(IllegalUserDataException.class, ()-> userService.validatePassword("ab34"));

        }

        @Test
        void validatePhonenumberWithAlphabet(){
            assertThrows(IllegalUserDataException.class, ()-> userService.validatePhoneNumber("abcdefgh"));
        }



}

