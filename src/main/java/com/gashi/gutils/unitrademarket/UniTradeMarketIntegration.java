package com.gashi.gutils.unitrademarket;

import com.gashi.gutils.unitrademarket.network.*;
import com.gashi.gutils.unitrademarket.screen.TradeInputScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration module for UniTradeMarket server support
 * Provides automatic input screens for price/quantity/search requests
 */
public class UniTradeMarketIntegration {
    private static final Logger LOGGER = LoggerFactory.getLogger("gutils-unitrademarket");
    private static boolean initialized = false;

    /**
     * Initialize UniTradeMarket integration
     * Should be called from GUtils client initializer
     */
    public static void initialize() {
        if (initialized) {
            LOGGER.warn("UniTradeMarket integration already initialized");
            return;
        }

        LOGGER.info("Initializing UniTradeMarket integration...");
        registerPacketReceivers();
        initialized = true;
        LOGGER.info("UniTradeMarket integration initialized successfully!");
    }

    private static void registerPacketReceivers() {
        // Register price input request
        ClientPlayNetworking.registerGlobalReceiver(PriceInputRequestPayload.ID, (payload, context) -> {
            try {
                String promptMessage = payload.promptMessage();

                context.client().execute(() -> {
                    try {
                        MinecraftClient.getInstance().setScreen(
                            new TradeInputScreen(NetworkConstants.InputType.PRICE, promptMessage, 0)
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to open price input screen", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to handle price input packet", e);
            }
        });

        // Register quantity input request
        ClientPlayNetworking.registerGlobalReceiver(QuantityInputRequestPayload.ID, (payload, context) -> {
            try {
                String promptMessage = payload.promptMessage();
                int maxQuantity = payload.maxQuantity();

                context.client().execute(() -> {
                    try {
                        MinecraftClient.getInstance().setScreen(
                            new TradeInputScreen(NetworkConstants.InputType.QUANTITY, promptMessage, maxQuantity)
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to open quantity input screen", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to handle quantity input packet", e);
            }
        });

        // Register search input request
        ClientPlayNetworking.registerGlobalReceiver(SearchInputRequestPayload.ID, (payload, context) -> {
            try {
                String promptMessage = payload.promptMessage();

                context.client().execute(() -> {
                    try {
                        MinecraftClient.getInstance().setScreen(
                            new TradeInputScreen(NetworkConstants.InputType.SEARCH, promptMessage, 0)
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to open search input screen", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to handle search input packet", e);
            }
        });

        LOGGER.info("Registered {} UniTradeMarket packet receivers", 3);
    }
}
