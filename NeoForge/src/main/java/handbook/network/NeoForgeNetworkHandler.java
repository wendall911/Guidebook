package handbook.network;

import org.jspecify.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import handbook.api.HandbookAPI;
import handbook.network.handler.NeoForgeClientPayloadHandler;
import handbook.network.handler.NeoForgeServerPayloadHandler;

public class NeoForgeNetworkHandler {

    public static void setupPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(HandbookAPI.MODID);

        registrar.playToClient(MessageOpenBookGui.TYPE, MessageOpenBookGui.CODEC, NeoForgeClientPayloadHandler.getInstance()::handleData);
        registrar.playToClient(MessageReloadBookContents.TYPE, MessageReloadBookContents.CODEC, NeoForgeClientPayloadHandler.getInstance()::handleData);
        registrar.playToServer(FetchRecipe.TYPE, FetchRecipe.STREAM_CODEC, NeoForgeServerPayloadHandler.getInstance()::processFetchRecipe);
        registrar.playToClient(SendRecipe.TYPE, SendRecipe.STREAM_CODEC, NeoForgeClientPayloadHandler.getInstance()::processRecipe);
    }

    public static void sendOpenBook(ServerPlayer player, Identifier book, @Nullable Identifier entry, int page) {
        player.connection.send(new MessageOpenBookGui(book, entry, page));
    }

    public static void sendReloadBookContents(MinecraftServer server) {
        PacketDistributor.sendToAllPlayers(new MessageReloadBookContents());
    }

    public static void sendReloadBookContents(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new MessageReloadBookContents());
    }

}
