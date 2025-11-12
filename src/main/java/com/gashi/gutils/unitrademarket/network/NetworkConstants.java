package com.gashi.gutils.unitrademarket.network;

import net.minecraft.util.Identifier;

/**
 * Network packet identifiers for UniTradeMarket server integration
 * NOTE: These must match the server-side packet IDs exactly
 */
public class NetworkConstants {
    public static final String MOD_ID = "unitrademarket";

    // Server -> Client packets (requests from server)
    public static final Identifier REQUEST_PRICE_INPUT = Identifier.of(MOD_ID, "request_price");
    public static final Identifier REQUEST_QUANTITY_INPUT = Identifier.of(MOD_ID, "request_quantity");
    public static final Identifier REQUEST_SEARCH_INPUT = Identifier.of(MOD_ID, "request_search");

    // Client -> Server packets (responses from client)
    public static final Identifier INPUT_RESPONSE_PACKET = Identifier.of(MOD_ID, "input_response");

    // Input types - must match server-side enum values
    public enum InputType {
        PRICE("price"),
        QUANTITY("quantity"),
        SEARCH("search");

        private final String value;

        InputType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static InputType fromString(String value) {
            for (InputType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }
}
