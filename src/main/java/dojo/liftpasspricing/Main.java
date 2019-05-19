package dojo.liftpasspricing;

import dojo.liftpasspricing.domain.PriceCalculator;
import dojo.liftpasspricing.domain.PricesDAO;
import dojo.liftpasspricing.infra.PricesServer;
import dojo.liftpasspricing.infra.SqlPricesDAO;

public class Main {

    public static void main(String[] args) {

        try (PricesDAO pricesDAO = new SqlPricesDAO()) {
            PriceCalculator priceCalculator = new PriceCalculator(pricesDAO);
            PricesServer.createApp(pricesDAO, priceCalculator);
            System.out.println("LiftPassPricing Api started on 4567,\n"
                    + "you can open http://localhost:4567/prices?type=night&age=23&date=2019-02-18 in a navigator\n"
                    + "and you'll get the price of the list pass for the day.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
