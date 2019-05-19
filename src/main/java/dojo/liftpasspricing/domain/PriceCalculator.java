package dojo.liftpasspricing.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PriceCalculator {

    private final PricesDAO pricesDAO;

    public PriceCalculator(PricesDAO pricesDAO) {
        this.pricesDAO = pricesDAO;
    }

    public int computeReduction(int initialCost, boolean isNight, Optional<Integer> requestAge, Optional<LocalDate> requestDate) {
        if (isNight) {
            return computeCostForNight(initialCost, requestAge);
        }
        return computeCostForDay(initialCost, requestAge, requestDate);
    }

    private int computeCostForDay(int initialCost, Optional<Integer> requestAge, Optional<LocalDate> requestDate) {
        return requestAge
                .map(age -> computeCostForDay(initialCost, age, requestDate))
                .orElseGet(() -> computeCostForDayWithoutAge(initialCost, requestDate));
    }

    private Integer computeCostForDayWithoutAge(int initialCost, Optional<LocalDate> requestDate) {
        double reduction = retrieveReductionIfMondayAndNotHoliday(requestDate);
        return roundUp(initialCost * reduction);
    }

    private int computeCostForDay(int initialCost, Integer age, Optional<LocalDate> requestDate) {
        if (age < 6) {
            return 0;
        }
        if (age < 15) {
            return roundUp(initialCost * .7);
        }

        double reduction = retrieveReductionIfMondayAndNotHoliday(requestDate);
        if (age > 64) {
            double cost = initialCost * .75 * reduction;
            return roundUp(cost);
        }

        return roundUp(initialCost * reduction);
    }

    private int computeCostForNight(int initialCost, Optional<Integer> requestAge) {
        return requestAge
                .map(age -> (age > 64) ? roundUp(initialCost * .4) : initialCost)
                .orElse(0);
    }

    private double retrieveReductionIfMondayAndNotHoliday(Optional<LocalDate> requestDate) {
        return requestDate
                .filter(this::isMondayAndNotHoliday)
                .map(d -> (1 - 35 / 100.0))
                .orElse(1.0);
    }

    private boolean isMondayAndNotHoliday(LocalDate date) {
        return !pricesDAO.retrieveHolidays().contains(date) && date.getDayOfWeek() == DayOfWeek.MONDAY;
    }

    private int roundUp(double cost) {
        return (int) Math.ceil(cost);
    }
}
