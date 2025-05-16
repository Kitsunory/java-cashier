package com.duta.library.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private static Database instance;
    private static Connection connection;
    private static final String url = "jdbc:mysql://127.0.0.1:3306/DutaLibrary";
    private static final String user = "root";
    private static final String password = "ahoge";

    private Database() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Successfully connected to the database!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(
                    "Error closing connection: " + e.getMessage()
                );
            }
        }
    }

    public static void init() {
        getInstance();
    }

    public boolean create(String table, String[] columns, Object[] values) {
        if (columns.length != values.length) {
            System.err.println(
                "Error: Number of columns and values must be the same."
            );
            return false;
        }

        String sql = "INSERT INTO " + table + " (";
        for (int i = 0; i < columns.length; i++) {
            sql += columns[i];
            if (i < columns.length - 1) {
                sql += ",";
            }
        }
        sql += ") VALUES (";
        for (int i = 0; i < values.length; i++) {
            sql += "?";
            if (i < values.length - 1) {
                sql += ",";
            }
        }
        sql += ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating data: " + e.getMessage());
            return false;
        }
    }

    public ResultSet read(String table, String columns, String whereClause) {
        String sql = "SELECT " + columns + " FROM " + table;
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error reading data: " + e.getMessage());
            return null;
        }
    }

    public boolean update(
        String table,
        String[] columns,
        Object[] values,
        String whereClause
    ) {
        if (columns.length != values.length) {
            System.err.println(
                "Error: Number of columns and values must be the same."
            );
            return false;
        }

        String sql = "UPDATE " + table + " SET ";
        for (int i = 0; i < columns.length; i++) {
            sql += columns[i] + " = ?";
            if (i < columns.length - 1) {
                sql += ", ";
            }
        }
        sql += " WHERE " + whereClause;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating data: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String table, String whereClause) {
        String sql = "DELETE FROM " + table + " WHERE " + whereClause;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting data: " + e.getMessage());
            return false;
        }
    }

    public boolean execute(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error executing SQL: " + e.getMessage());
            return false;
        }
    }
}
