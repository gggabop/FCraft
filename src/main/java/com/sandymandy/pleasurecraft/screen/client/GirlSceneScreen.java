package com.sandymandy.pleasurecraft.screen.client;

import com.sandymandy.pleasurecraft.networking.C2S.StartSceneC2SPacket;
import com.sandymandy.pleasurecraft.scene.SceneOption;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GirlSceneScreen extends Screen {
    private final int entityId;
    private final List<SceneOption> options;

    public GirlSceneScreen(int entityId, List<SceneOption> options) {
        super(Component.literal("Scene Options"));
        this.entityId = entityId;
        this.options = options;
    }

    @Override
    protected void init() {
        int y = this.height / 4;
        for (SceneOption option : options) {
            this.addRenderableWidget(Button.builder(Component.literal(option.name()), button -> {
                ClientPlayNetworking.send(new StartSceneC2SPacket(
                        this.entityId,
                        option.introAnim(),
                        option.slowAnim(),
                        option.fastAnim(),
                        option.cumAnim()
                ));
                Minecraft.getInstance().gui.setScreen(null); // close after sending
            }).bounds(this.width / 2 - 100, y, 200, 20).build());
            y += 25;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // Oscurece toda la pantalla (antes los argumentos de ancho y alto estaban cambiados)
        context.fillGradient(0, 0, this.width, this.height, 0xAA000000, 0xAA000000);
    }

}