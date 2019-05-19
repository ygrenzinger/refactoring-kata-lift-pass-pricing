package dojo.liftpasspricing.infra;

import dojo.liftpasspricing.domain.PriceCalculator;
import dojo.liftpasspricing.domain.PricesDAO;
import spark.Request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static spark.Spark.*;

public class PricesServer {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void createApp(PricesDAO pricesDAO, PriceCalculator priceCalculator) {

        port(4567);

        put("/prices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = req.queryParams("type");
            pricesDAO.updateCost(liftPassType, liftPassCost);
            return "";
        });

        get("/prices", (req, res) -> {
            String type = req.queryParams("type");
            boolean isNight = req.queryParams("type").equals("night");
            Optional<Integer> requestAge = retrieveRequestAge(req);
            Optional<LocalDate> requestDate = retrieveRequestDate(req);

            int initialCost = pricesDAO.retrieveCost(type);
            int cost = priceCalculator.computeReduction(initialCost, isNight, requestAge, requestDate);
            return "{ \"cost\": " + cost + "}";
        });

        after((req, res) -> {
            res.type("application/json");
        });
    }

    private static Optional<LocalDate> retrieveRequestDate(Request req) {
        return Optional.ofNullable(req.queryParams("date")).map(param -> LocalDate.parse(param, dateFormatter));
    }

    private static Optional<Integer> retrieveRequestAge(Request req) {
        return Optional.ofNullable(req.queryParams("age")).map(Integer::valueOf);
    }

}
