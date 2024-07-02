package mcjty.lib.varia;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;

import javax.annotation.Nonnull;
import java.util.Set;

public class FakePlayerConnection extends ServerGamePacketListenerImpl {

    public FakePlayerConnection(MinecraftServer server, ServerPlayer player) {
        super(server, new Connection(PacketFlow.CLIENTBOUND), player);
    }

    @Override
    public void disconnect(@Nonnull Component textComponent) {
    }

    @Override
    public void onDisconnect(@Nonnull Component reason) {
    }

    @Override
    public void teleport(double x, double y, double z, float yaw, float pitch) {
    }

    @Override
    public void handleLockDifficulty(@Nonnull ServerboundLockDifficultyPacket packetIn) {
    }

    @Override
    public void handleChangeDifficulty(@Nonnull ServerboundChangeDifficultyPacket packetIn) {
    }

    @Override
    public void handleSetJigsawBlock(@Nonnull ServerboundSetJigsawBlockPacket packetIn) {
    }

    @Override
    public void handleAnimate(@Nonnull ServerboundSwingPacket packetIn) {
    }

    @Override
    public void handleClientCommand(@Nonnull ServerboundClientCommandPacket packetIn) {
    }

    @Override
    public void handleMovePlayer(@Nonnull ServerboundMovePlayerPacket packetIn) {
    }

    @Override
    public void handleContainerButtonClick(@Nonnull ServerboundContainerButtonClickPacket packetIn) {
    }

    @Override
    public void handleContainerClose(@Nonnull ServerboundContainerClosePacket packetIn) {
    }

    @Override
    public void handleSeenAdvancements(@Nonnull ServerboundSeenAdvancementsPacket packetIn) {
    }

    @Override
    public void handleMoveVehicle(@Nonnull ServerboundMoveVehiclePacket packetIn) {
    }

    @Override
    public void handleResourcePackResponse(@Nonnull ServerboundResourcePackPacket packetIn) {
    }


    @Override
    public void handleChat(@Nonnull ServerboundChatPacket packetIn) {
    }

    @Override
    public void handleTeleportToEntityPacket(@Nonnull ServerboundTeleportToEntityPacket packetIn) {
    }

    @Override
    public void handleClientInformation(@Nonnull ServerboundClientInformationPacket packetIn) {
    }

    @Override
    public void handleContainerClick(@Nonnull ServerboundContainerClickPacket packetIn) {
    }

    @Override
    public void handleCustomPayload(@Nonnull ServerboundCustomPayloadPacket packetIn) {
    }

    @Override
    public void handleSetCreativeModeSlot(@Nonnull ServerboundSetCreativeModeSlotPacket packetIn) {
    }

    @Override
    public void handleAcceptTeleportPacket(@Nonnull ServerboundAcceptTeleportationPacket packetIn) {
    }

    @Override
    public void handlePlayerCommand(@Nonnull ServerboundPlayerCommandPacket packetIn) {
    }


    @Override
    public void handlePlayerInput(@Nonnull ServerboundPlayerInputPacket packetIn) {
    }

    @Override
    public void handleEditBook(@Nonnull ServerboundEditBookPacket packetIn) {
    }

    @Override
    public void handleSetCarriedItem(@Nonnull ServerboundSetCarriedItemPacket packetIn) {
    }

    @Override
    public void handlePlaceRecipe(@Nonnull ServerboundPlaceRecipePacket packetIn) {
    }

    @Override
    public void handleKeepAlive(@Nonnull ServerboundKeepAlivePacket packetIn) {
    }

    @Override
    public void handlePlayerAction(@Nonnull ServerboundPlayerActionPacket packetIn) {
    }

    @Override
    public void handleSelectTrade(@Nonnull ServerboundSelectTradePacket packetIn) {
    }

    @Override
    public void handlePickItem(@Nonnull ServerboundPickItemPacket packetIn) {
    }

    @Override
    public void handleCustomCommandSuggestions(@Nonnull ServerboundCommandSuggestionPacket packetIn) {
    }

    @Override
    public void handlePlayerAbilities(@Nonnull ServerboundPlayerAbilitiesPacket packetIn) {
    }

    @Override
    public void handleUseItemOn(@Nonnull ServerboundUseItemOnPacket packetIn) {
    }

    @Override
    public void handlePaddleBoat(@Nonnull ServerboundPaddleBoatPacket packetIn) {
    }

    @Override
    public void handleSetCommandBlock(@Nonnull ServerboundSetCommandBlockPacket packetIn) {
    }

    @Override
    public void handleRenameItem(@Nonnull ServerboundRenameItemPacket packetIn) {
    }

    @Override
    public void handleSignUpdate(@Nonnull ServerboundSignUpdatePacket packetIn) {
    }

    @Override
    public void handleUseItem(@Nonnull ServerboundUseItemPacket packetIn) {
    }

    @Override
    public void handleInteract(@Nonnull ServerboundInteractPacket packetIn) {
    }

    @Override
    public void handleSetCommandMinecart(@Nonnull ServerboundSetCommandMinecartPacket packetIn) {
    }

    @Override
    public void handleSetStructureBlock(@Nonnull ServerboundSetStructureBlockPacket packetIn) {
    }

    @Override
    public void handleSetBeaconPacket(@Nonnull ServerboundSetBeaconPacket packetIn) {
    }

    @Override
    public void teleport(double x, double y, double z, float jaw, float pitch, Set<RelativeMovement> set) {
    }

    @Override
    public void send(@Nonnull Packet<?> packetIn) {
    }

    @Override
    public void tick() {
    }
}
