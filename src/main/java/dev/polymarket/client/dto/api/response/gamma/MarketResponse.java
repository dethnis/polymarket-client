package dev.polymarket.client.dto.api.response.gamma;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Gamma market description.
 * <pre>{@code
 * {
 * "id": "683595",
 * "question": "Bitcoin Up or Down - November 17, 12AM ET",
 * "conditionId": "0x1a7999c71f8b875bdc903734a2107fbcf8d7dfdcbc9d3c51731a8c1460e11898",
 * "slug": "bitcoin-up-or-down-november-17-12am-et",
 * "endDate": "2025-11-17T06:00:00Z",
 * "startDate": "2025-11-15T05:00:57.755897Z",
 * "outcomes": "[\"Up\", \"Down\"]",
 * "outcomePrices": "[\"0.5\", \"0.5\"]",
 * "active": true,
 * "closed": false,
 * "enableOrderBook": true,
 * "orderPriceMinTickSize": 0.01,
 * "orderMinSize": 5,
 * "clobTokenIds": "[\"97633282141727012715463055428185899320650283789416612830799232303088670062150\", \"51083518851574675893213781153121136313356956848703961741777739233450403802026\"]",
 * "negRisk": false,
 * "acceptingOrdersTimestamp": "2025-11-15T05:00:36Z",
 * "eventStartTime": "2025-11-15T10:15:00Z"
 * }
 * }</pre>
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @EqualsAndHashCode.Include
    private String id;
    private String question;
    private String conditionId;
    private String slug;
    private OffsetDateTime eventStartTime;
    private OffsetDateTime endDate;
    private String startDate;
    private String outcomes;
    private String outcomePrices;
    private boolean active;
    private boolean closed;
    private Boolean enableOrderBook;
    private BigDecimal orderPriceMinTickSize;
    private Integer orderMinSize;
    // private String clobTokenIds;
    private List<String> clobTokenIds;
    @JsonProperty("clobTokenIds")
    public void setClobTokenIds(String value) {
        this.clobTokenIds = MAPPER.readValue(
                value,
                new TypeReference<List<String>>() {});
    }
    private Boolean negRisk;
    private String acceptingOrdersTimestamp;

}
