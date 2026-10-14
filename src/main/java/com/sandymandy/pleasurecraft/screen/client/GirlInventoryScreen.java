package com.sandymandy.pleasurecraft.screen.client;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import com.sandymandy.pleasurecraft.networking.C2S.MovementLockStateC2SPacket;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandler;
import com.sandymandy.pleasurecraft.screen.InventoryButtonAction;
import com.sandymandy.pleasurecraft.screen.InventoryButtonRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class GirlInventoryScreen extends AbstractContainerScreen<GirlInventoryScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "textures/gui/inventory.png");
    private float xMouse;
    private float yMouse;
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 170;
    private final AbstractGirlEntity girl;
    private final Player player;



    public GirlInventoryScreen(GirlInventoryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.girl = handler.getGirl();
        this.player = inventory.player;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        //Stops the container names from rendering
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // Oscurece toda la pantalla (antes los argumentos de ancho y alto estaban cambiados)
        context.fillGradient(0, 0, this.width, this.height, 0xAA000000, 0xAA000000);

        int centerX = (width - GUI_WIDTH) / 2;
        int centerY = (height - GUI_HEIGHT) / 2;
        int i = this.leftPos;
        int j = this.topPos;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX, centerY, 0f, 0f, GUI_WIDTH, GUI_HEIGHT, GUI_WIDTH, GUI_HEIGHT);
        InventoryScreen.extractEntityInInventoryFollowsMouse(context, i + 26, j + 8, i + 75, j + 78, this.girl.getSizeGUI(), this.girl.getYAxisGUI(), mouseX, mouseY, this.girl);
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientPlayNetworking.send(new MovementLockStateC2SPacket(this.girl.getId(),false));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;

        int buttonHeight = 22;
        int buttonWidth = 80;

        int paddingX = 10;
        int paddingY = 4;

        int startX = centerX - (buttonWidth + paddingX);
        int startY = centerY + 15;

        if (girl.isTame()){
            for (int i = 0; i < InventoryButtonRegistry.BUTTONS_LEFT.size(); i++) {
                InventoryButtonAction action = InventoryButtonRegistry.BUTTONS_LEFT.get(i);
                int y = startY + i * (buttonHeight + paddingY);
                Component dynamicLabel = action.label();

                if (action.label().getString().equals("Strip") && girl.isStripped()) {
                    dynamicLabel = Component.literal("Dress Up");
                }

                this.addRenderableWidget(Button.builder(
                    dynamicLabel,
                    btn -> {
                        if (girl != null && minecraft != null && player != null) {
                            action.action().accept(girl, player);  // Run the button's logic
                            this.minecraft.gui.setScreen(null);
                            ClientPlayNetworking.send(new MovementLockStateC2SPacket(this.girl.getId(),false));
                        }
                    }
                ).bounds(startX, y, buttonWidth, buttonHeight).build());
            }

            for (int i = 0; i < InventoryButtonRegistry.BUTTONS_RIGHT.size(); i++) {
                InventoryButtonAction action = InventoryButtonRegistry.BUTTONS_RIGHT.get(i);
                int y = startY + i * (buttonHeight + paddingY);
                Component dynamicLabel = action.label();

                if (action.label().getString().equals("Sit") && girl.isSittingdown()){
                    dynamicLabel = Component.literal("Stand");
                }
                else if (action.label().getString().equals("Follow Me") && girl.isFollowing()){
                    dynamicLabel = Component.literal("Stop Following");
                }
                else if (action.label().getString().equals("Freeze") && girl.isFrozenInPlace()){
                    dynamicLabel = Component.literal("Unfreeze");
                }

                this.addRenderableWidget(Button.builder(
                    dynamicLabel,
                    btn -> {
                        if (girl != null && minecraft != null && player != null) {
                            action.action().accept(girl, player);  // Run the button's logic
                            this.minecraft.gui.setScreen(null);
                        }
                    }
                ).bounds(centerX + 176 + paddingX, y, buttonWidth, buttonHeight).build());
            }
        }

    }

}