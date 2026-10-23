package dev.polymarket.client;

//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//
//import com.polymarket.client.dto.api.reponse.ApiKeysResponse;
//import com.polymarket.client.dto.api.reponse.order.CancelOrderResponse;
//import com.polymarket.client.dto.api.reponse.order.CreateAndPlaceOrderResponse;
//import com.polymarket.client.dto.api.request.order.UserOrderRequest;
//import com.polymarket.client.enums.TradeSide;
//import com.polymarket.client.utils.MarketUtils;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestMethodOrder;
//import tools.jackson.databind.ObjectMapper;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PolymarketClientIntegrationTest {

//    private final PolymarketClient polymarketClient = new PolymarketClient();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Nested
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    class AuthL2Api {
//
//        @Test
//        public void getApiKeysTest() {
//            ApiKeysResponse response = polymarketClient.getPrivateApi().getAuthL2Api().getApiKeys().join();
//
//            assertNotNull(response.getApiKeys());
//            for (String key : response.getApiKeys()) {
//                assertTrue(key != null && !key.isBlank(), "API key should not be blank");
//            }
//        }
//    }
//
//    @Nested
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    class MarketsApi {
//
//        @Test
//        public void getMarketBySlugTest() {
//            String slug = "bitcoin-up-or-down-november-17-12am-et";
//
//            var market = polymarketClient.getPublicApi().getMarketsApi().getMarketBySlug(slug).join();
//
//            assertAll(
//                    () -> assertEquals(slug, market.getSlug(), "Market slug should match the requested slug"),
//                    () -> assertTrue(market.getId() != null && !market.getId().isBlank(), "Market ID should not be blank")
//            );
//        }
//    }
//
//    @Nested
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//    class OrderApi {
//        private String orderId;
//
//        @Test
//        @Order(1)
//        public void createAndPlaceOrderTest() throws Exception {
//            OffsetDateTime nowPlus2Hour = OffsetDateTime.now(ZoneOffset.UTC).plusHours(2);
//            String slug = MarketUtils.build15MinMarketSlug(nowPlus2Hour);
//
//            var market = polymarketClient.getPublicApi().getMarketsApi().getMarketBySlug(slug).join();
//
//            // Deserialize standard JSON array string into List<String>
//            List<String> tokenIds = objectMapper.readValue(market.getClobTokenIds(), new TypeReference<List<String>>() {});
//            String assetId = tokenIds.get(0);
//
//            UserOrderRequest userOrder = new UserOrderRequest(
//                    assetId,
//                    TradeSide.BUY,
//                    BigDecimal.valueOf(0.3),
//                    BigDecimal.valueOf(5)
//            );
//
//            CreateAndPlaceOrderResponse response = polymarketClient.getPrivateApi().getOrderApi().createAndPlaceOrder(userOrder).join();
//
//            assertAll(
//                    () -> assertTrue(response.isSuccess()),
//                    () -> assertNotNull(response.getOrderId(), "Order ID should not be null")
//            );
//
//            this.orderId = response.getOrderId();
//        }
//
//        @Test
//        @Order(2)
//        public void cancelOrder() {
//            assertNotNull(orderId, "Setup failure: orderId is null from previous step execution.");
//
//            CancelOrderResponse response = polymarketClient.getPrivateApi().getOrderApi().cancelOrder(orderId).join();
//
//            assertEquals(1, response.getCanceled().size(), "There should be exactly one canceled order");
//            assertEquals(orderId, response.getCanceled().get(0), "Canceled order ID should match the requested order ID");
//        }
//    }
}
