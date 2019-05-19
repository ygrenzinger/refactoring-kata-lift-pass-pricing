package dojo.liftpasspricing;

import dojo.liftpasspricing.domain.PriceCalculator;
import dojo.liftpasspricing.infra.PricesServer;
import dojo.liftpasspricing.infra.SqlPricesDAO;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import spark.Spark;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PricesServerTest {

    @BeforeAll
    public static void beforeAll() throws SQLException {
        SqlPricesDAO pricesDAO = new SqlPricesDAO();
        PricesServer.createApp(pricesDAO, new PriceCalculator(pricesDAO));
    }

    @AfterAll
    public static void afterAll() {
        Spark.stop();
    }

    @ParameterizedTest
    @MethodSource("requestParams")
    void testPriceRules(String type, Optional<Integer> age, Optional<String> date, Integer expectedCost) {
        String query = "/prices?type="+type;
        query += age.map(a -> "&age="+a).orElse("");
        query += date.map(d -> "&date="+d).orElse("");
        JsonPath response = RestAssured.
                given().
                port(4567).
                when().
                get(query).
                then().
                assertThat().statusCode(200).
                assertThat().contentType("application/json").
                extract().jsonPath();

        assertEquals(expectedCost, response.get("cost"));
    }

    static Stream<Arguments> requestParams() {
        return Stream.of(
                arguments("1jour", Optional.empty(), Optional.of("2019-02-18"), 35),
                arguments("1jour", Optional.empty(), Optional.of("2019-02-25"), 35),
                arguments("1jour", Optional.empty(), Optional.of("2019-03-04"), 35),
                arguments("1jour", Optional.empty(), Optional.of("2019-05-20"), 23),
                arguments("1jour", Optional.empty(), Optional.empty(), 35),
                arguments("1jour", Optional.of(5), Optional.empty(), 0),
                arguments("1jour", Optional.of(14), Optional.empty(), 25),
                arguments("1jour", Optional.of(65), Optional.empty(), 27),
                arguments("1jour", Optional.of(64), Optional.empty(), 35),
                arguments("night", Optional.empty(), Optional.empty(), 0),
                arguments("night", Optional.of(65), Optional.empty(), 8),
                arguments("night", Optional.of(64), Optional.empty(), 19)
        );
    }

}
