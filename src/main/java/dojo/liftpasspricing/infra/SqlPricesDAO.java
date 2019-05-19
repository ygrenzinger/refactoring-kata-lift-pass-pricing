package dojo.liftpasspricing.infra;

import dojo.liftpasspricing.domain.PricesDAO;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqlPricesDAO implements PricesDAO {

    private final Connection connection;

    public SqlPricesDAO() {
        connection = creatingConnectionToDatabase();
    }

    @Override
    public int retrieveCost(String type) {
        String query = "SELECT cost FROM base_price WHERE type = ?";
        try (PreparedStatement costStmt = connection.prepareStatement(query)) {
            costStmt.setString(1, type);
            ResultSet result = costStmt.executeQuery();
            result.next();
            return result.getInt("cost");
        } catch (SQLException e) {
            throw new RuntimeException("Impossible to retrieve cost", e);
        }
    }

    @Override
    public void updateCost(String type, int cost) {
        String query = "INSERT INTO base_price (type, cost) VALUES (?, ?) ON DUPLICATE KEY UPDATE cost = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setInt(2, cost);
            stmt.setInt(3, cost);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<LocalDate> retrieveHolidays() {
        try (PreparedStatement holidayStmt = connection.prepareStatement("SELECT * FROM holidays");
             ResultSet results = holidayStmt.executeQuery()
        ) {
            List<LocalDate> holidays = new ArrayList<>();
            while (results.next()) {
                Date holiday = results.getDate("holiday");
                holidays.add(holiday.toLocalDate());
            }
            return holidays;
        } catch (SQLException e) {
            throw new RuntimeException("Impossible to retrieve holidays", e);
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    private static Connection creatingConnectionToDatabase() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:sample;MODE=MYSQL;INIT=RUNSCRIPT FROM 'classpath:script/initDatabase.sql'");
        ds.setUser("sa");
        ds.setPassword("sa");
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Impossible to create connection to H2", e);
        }
    }
}
