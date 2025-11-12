package com.gashi.gutils.unitrademarket.screen;

import com.gashi.gutils.unitrademarket.network.NetworkConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

/**
 * Client-side input screen for UniTradeMarket server integration
 */
public class TradeInputScreen extends Screen {
    // GLFW key codes
    private static final int KEY_ENTER = 257;
    private static final int KEY_ESCAPE = 256;

    private final NetworkConstants.InputType inputType;
    private final String promptMessage;
    private final int maxValue;
    private TextFieldWidget inputField;
    private Text errorMessage;

    public TradeInputScreen(NetworkConstants.InputType inputType, String promptMessage, int maxValue) {
        super(Text.translatable("gutils.screen.trade_input.title"));
        this.inputType = inputType;
        this.promptMessage = promptMessage;
        this.maxValue = maxValue;
        this.errorMessage = null;
    }

    @Override
    protected void init() {
        super.init();

        // Create input field
        int fieldWidth = 200;
        int fieldHeight = 20;
        int fieldX = this.width / 2 - fieldWidth / 2;
        int fieldY = this.height / 2 - 10;

        inputField = new TextFieldWidget(
            this.textRenderer,
            fieldX,
            fieldY,
            fieldWidth,
            fieldHeight,
            Text.literal("")
        );
        inputField.setMaxLength(16);
        inputField.setFocused(true);
        inputField.setEditable(true);

        // Set text predicate based on input type
        if (inputType == NetworkConstants.InputType.PRICE || inputType == NetworkConstants.InputType.QUANTITY) {
            // Allow only numbers and decimal point for price
            inputField.setTextPredicate(text -> {
                if (text.isEmpty()) return true;
                if (inputType == NetworkConstants.InputType.PRICE) {
                    return text.matches("^\\d*\\.?\\d*$"); // Numbers and decimal
                } else {
                    return text.matches("^\\d*$"); // Numbers only for quantity
                }
            });
        }

        addDrawableChild(inputField);

        // Confirm button
        addDrawableChild(ButtonWidget.builder(Text.translatable("gutils.screen.trade_input.button.confirm"), button -> {
            if (validateAndSend()) {
                close();
            }
        }).dimensions(this.width / 2 - 100, this.height / 2 + 30, 95, 20).build());

        // Cancel button
        addDrawableChild(ButtonWidget.builder(Text.translatable("gutils.screen.trade_input.button.cancel"), button -> {
            sendCancelResponse();
            close();
        }).dimensions(this.width / 2 + 5, this.height / 2 + 30, 95, 20).build());
    }

    private boolean validateAndSend() {
        String input = inputField.getText().trim();

        if (input.isEmpty()) {
            errorMessage = Text.translatable("gutils.screen.trade_input.error.empty");
            return false;
        }

        // Validate based on input type
        switch (inputType) {
            case PRICE:
                try {
                    double price = Double.parseDouble(input);
                    if (price <= 0) {
                        errorMessage = Text.translatable("gutils.screen.trade_input.error.price.negative");
                        return false;
                    }
                    if (price > 999999999) {
                        errorMessage = Text.translatable("gutils.screen.trade_input.error.price.too_high");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    errorMessage = Text.translatable("gutils.screen.trade_input.error.price.invalid_format");
                    return false;
                }
                break;

            case QUANTITY:
                try {
                    int quantity = Integer.parseInt(input);
                    if (quantity <= 0) {
                        errorMessage = Text.translatable("gutils.screen.trade_input.error.quantity.negative");
                        return false;
                    }
                    if (quantity > maxValue) {
                        errorMessage = Text.translatable("gutils.screen.trade_input.error.quantity.too_high", maxValue);
                        return false;
                    }
                } catch (NumberFormatException e) {
                    errorMessage = Text.translatable("gutils.screen.trade_input.error.quantity.invalid_format");
                    return false;
                }
                break;

            case SEARCH:
                if (input.length() < 2) {
                    errorMessage = Text.translatable("gutils.screen.trade_input.error.search.too_short");
                    return false;
                }
                break;
        }

        // Send to server
        sendInputResponse(input);
        return true;
    }

    private void sendInputResponse(String input) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(inputType.getValue());
        buf.writeString(input);
        buf.writeBoolean(true); // success

        ClientPlayNetworking.send(NetworkConstants.INPUT_RESPONSE_PACKET, buf);
    }

    private void sendCancelResponse() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(inputType.getValue());
        buf.writeString("");
        buf.writeBoolean(false); // cancelled

        ClientPlayNetworking.send(NetworkConstants.INPUT_RESPONSE_PACKET, buf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        this.renderBackground(context, mouseX, mouseY, delta);

        // Draw title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            "§e§l" + promptMessage,
            this.width / 2,
            this.height / 2 - 40,
            0xFFFFFF
        );

        // Draw hint
        Text hint = switch (inputType) {
            case PRICE -> Text.translatable("gutils.screen.trade_input.hint.price");
            case QUANTITY -> Text.translatable("gutils.screen.trade_input.hint.quantity", maxValue);
            case SEARCH -> Text.translatable("gutils.screen.trade_input.hint.search");
        };
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("§7").append(hint),
            this.width / 2,
            this.height / 2 - 25,
            0xAAAAAA
        );

        // Draw error message if any
        if (errorMessage != null) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                errorMessage,
                this.width / 2,
                this.height / 2 + 55,
                0xFF5555
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Enter key = confirm
        if (keyCode == KEY_ENTER) {
            if (validateAndSend()) {
                close();
            }
            return true;
        }
        // Escape key = cancel
        if (keyCode == KEY_ESCAPE) {
            sendCancelResponse();
            close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }
}
