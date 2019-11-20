package mcjty.lib.network;

import com.google.common.collect.Lists;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TestPackets {

    private static final Key<Integer> PARAM_INT = new Key<>("int", Type.INTEGER);
    private static final Key<String> PARAM_STRING = new Key<>("string", Type.STRING);
    private static final Key<Boolean> PARAM_BOOL = new Key<>("bool", Type.BOOLEAN);
    private static final Key<Double> PARAM_DOUBLE = new Key<>("double", Type.DOUBLE);
    private static final Key<List<String>> PARAM_STRINGLIST = new Key<>("stringlist", Type.STRING_LIST);
    private static final Key<List<BlockPos>> PARAM_POSLIST = new Key<>("poslist", Type.POS_LIST);

    private static final TypedMap PARAMETERS = TypedMap.builder()
            .put(PARAM_INT, 123)
            .put(PARAM_STRING, "Hello world")
            .put(PARAM_BOOL, false)
            .put(PARAM_DOUBLE, 3.5)
            .put(PARAM_STRINGLIST, Lists.newArrayList("abc", "def", "ghi"))
            .put(PARAM_POSLIST, Lists.newArrayList(new BlockPos(1, 1, 1), new BlockPos(2, 2, 1)))
            .build();

//    @Test
//    public void testDataFromServer() {
//        PacketDataFromServer packet1 = new PacketDataFromServer(new BlockPos(100, 10, 33), "command", PARAMETERS);
//        PacketDataFromServer packet2 = testPacket(packet1, PacketDataFromServer::toBytes, PacketDataFromServer::new);
//        Assert.assertEquals(packet1.command, packet2.command);
//        Assert.assertEquals(packet1.pos, packet2.pos);
//        Assert.assertEquals(packet1.result, packet2.result);
//    }
//
//    @Test
//    public void testRequestDataFromServer() {
//        PacketRequestDataFromServer packet1 = new PacketRequestDataFromServer(new BlockPos(50, 11, 333), "command", PARAMETERS);
//        PacketRequestDataFromServer packet2 = testPacket(packet1, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new);
//        Assert.assertEquals(packet1.command, packet2.command);
//        Assert.assertEquals(packet1.pos, packet2.pos);
//        Assert.assertEquals(packet1.params, packet2.params);
//    }
//
//    @Test
//    public void testSendClientCommand() {
//        PacketSendClientCommand packet1 = new PacketSendClientCommand("modid", "command", PARAMETERS);
//        PacketSendClientCommand packet2 = testPacket(packet1, PacketSendClientCommand::toBytes, PacketSendClientCommand::new);
//        Assert.assertEquals(packet1.command, packet2.command);
//        Assert.assertEquals(packet1.modid, packet2.modid);
//        Assert.assertEquals(packet1.arguments, packet2.arguments);
//    }
//
//    @Test
//    public void testSendServerCommand() {
//        PacketSendServerCommand packet1 = new PacketSendServerCommand("modid", "command", PARAMETERS);
//        PacketSendServerCommand packet2 = testPacket(packet1, PacketSendServerCommand::toBytes, PacketSendServerCommand::new);
//        Assert.assertEquals(packet1.command, packet2.command);
//        Assert.assertEquals(packet1.modid, packet2.modid);
//        Assert.assertEquals(packet1.arguments, packet2.arguments);
//    }
//
//    @Test
//    public void testServerCommandTyped() {
//        PacketServerCommandTyped packet1 = new PacketServerCommandTyped(new BlockPos(11, 22, 33), "command", PARAMETERS);
//        PacketServerCommandTyped packet2 = testPacket(packet1, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new);
//        Assert.assertEquals(packet1.command, packet2.command);
//        Assert.assertEquals(packet1.pos, packet2.pos);
//        Assert.assertEquals(packet1.dimensionId, packet2.dimensionId);
//        Assert.assertEquals(packet1.params, packet2.params);
//    }
//
//    @Test
//    public void testSetGuiStyle() {
//        PacketSetGuiStyle packet1 = new PacketSetGuiStyle("Style");
//        PacketSetGuiStyle packet2 = testPacket(packet1, PacketSetGuiStyle::toBytes, PacketSetGuiStyle::new);
//        Assert.assertEquals(packet1.style, packet2.style);
//    }
//
//    @Test
//    public void testSendPreferencestoClient() {
//        PacketSendPreferencesToClient packet1 = new PacketSendPreferencesToClient(100, 200, GuiStyle.STYLE_FLAT_GRADIENT);
//        PacketSendPreferencesToClient packet2 = testPacket(packet1, PacketSendPreferencesToClient::toBytes, PacketSendPreferencesToClient::new);
//        Assert.assertEquals(packet1.getBuffX(), packet2.getBuffX());
//        Assert.assertEquals(packet1.getBuffY(), packet2.getBuffY());
//        Assert.assertEquals(packet1.getStyle(), packet2.getStyle());
//    }
//
//    private <T> T testPacket(T packet, BiConsumer<T, PacketBuffer> toBytes, Function<PacketBuffer, T> supplier) {
//        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
//        toBytes.accept(packet, buf);
//        T packet2 = supplier.apply(buf);
//        Assert.assertEquals(0, buf.readableBytes());
//        buf.release();
//        return packet2;
//    }

}
