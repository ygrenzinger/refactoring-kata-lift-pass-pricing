package dojo.liftpasspricing.domain;

import java.time.LocalDate;
import java.util.List;

public interface PricesDAO extends AutoCloseable {

    int retrieveCost(String type);

    void updateCost(String type, int cost);

    List<LocalDate> retrieveHolidays();

    @Override
    void close() throws Exception;
}
