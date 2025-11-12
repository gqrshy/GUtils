package com.gashi.gutils.unitrademarket;

import com.gashi.gutils.unitrademarket.network.NetworkConstants;
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
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.REQUEST_PRICE_INPUT, (client, handler, buf, responseSender) -> {
            try {
                String promptMessage = buf.readString();

                client.execute(() -> {
                    try {
                        MinecraftClient.getInstance().setScreen(
                            new TradeInputScreen(NetworkConstants.InputType.PRICE, promptMessage, 0)
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to open price input screen", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to read price input packet", e);
            }
        });

        // Register quantity input request
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.REQUEST_QUANTITY_INPUT, (client, handler, buf, responseSender) -> {
            try {
                String promptMessage = buf.readString();
                int maxQuantity = buf.readInt();

                client.execute(() -> {
                    try {
                        MinecraftClient.getInstance().setScreen(
                            new TradeInputScreen(NetworkConstants.InputType.QUANTITY, promptMessage, maxQuantity)
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to open quantity input screen", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to read quantity input packet", e);
            }
        });

        // Register search input request
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.REQUEST_SEARCH_INPUT, (client, handler, buf, responseSender) -> {
            try {
                String promptMessage = buf.readString();

                client.execute(() -> {
                    try {
                        MinecraftClient.getInstance().setScreen(
                            new TradeInputScreen(NetworkConstants.InputType.SEARCH, promptMessage, 0)
                        );
                    } catch (Exception e) {
                        LOGGER.error("Failed to open search input screen", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to read search input packet", e);
            }
        });

        LOGGER.info("Registered {} UniTradeMarket packet receivers", 3);
    }
}
