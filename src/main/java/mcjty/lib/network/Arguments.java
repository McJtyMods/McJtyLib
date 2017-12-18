package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Arguments {

    private final List<Pair<ArgumentType, Object>> parameters;
    private int idx = 0;

    public static final Arguments EMPTY = Arguments.builder().build();

    private Arguments(Builder builder) {
        this.parameters = builder.parameters;
    }

    public Arguments(ByteBuf buf) {
        this.parameters = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            ArgumentType type = ArgumentType.getType(buf.readByte());
            switch (type) {
                case TYPE_STRING:
                    parameters.add(Pair.of(type, NetworkTools.readStringUTF8(buf)));
                    break;
                case TYPE_INTEGER:
                    parameters.add(Pair.of(type, buf.readInt()));
                    break;
                case TYPE_BLOCKPOS:
                    int cx = buf.readInt();
                    int cy = buf.readInt();
                    int cz = buf.readInt();
                    if (cx == -1 && cy == -1 && cz == -1) {
                        parameters.add(Pair.of(type, null));
                    } else {
                        parameters.add(Pair.of(type, new BlockPos(cx, cy, cz)));
                    }
                    break;
                case TYPE_BOOLEAN:
                    parameters.add(Pair.of(type, buf.readByte() == 1));
                    break;
                case TYPE_DOUBLE:
                    parameters.add(Pair.of(type, buf.readDouble()));
                    break;
                case TYPE_STACK:
                    if (buf.readBoolean()) {
                        parameters.add(Pair.of(type, NetworkTools.readItemStack(buf)));
                    } else {
                        parameters.add(Pair.of(type, ItemStack.EMPTY));
                    }
                    break;
            }
        }

    }

    public void reset() {
        idx = 0;
    }

    private void checkType(ArgumentType type) {
        if (parameters.get(idx).getKey() != type) {
            throw new RuntimeException("Bad argument type at position " + idx);
        }
    }

    public int getInt() {
        checkType(ArgumentType.TYPE_INTEGER);
        return (Integer) parameters.get(idx++).getValue();
    }

    public double getDouble() {
        checkType(ArgumentType.TYPE_DOUBLE);
        return (Double) parameters.get(idx++).getValue();
    }

    public boolean getBoolean() {
        checkType(ArgumentType.TYPE_BOOLEAN);
        return (Boolean) parameters.get(idx++).getValue();
    }

    public BlockPos getBlockPos() {
        checkType(ArgumentType.TYPE_BLOCKPOS);
        return (BlockPos) parameters.get(idx++).getValue();
    }

    public ItemStack getItemStack() {
        checkType(ArgumentType.TYPE_STACK);
        return (ItemStack) parameters.get(idx++).getValue();
    }

    public String getString() {
        checkType(ArgumentType.TYPE_STRING);
        return (String) parameters.get(idx++).getValue();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(parameters.size());
        for (Pair<ArgumentType, Object> pair : parameters) {
            buf.writeByte(pair.getKey().ordinal());
            Object arg = pair.getValue();
            switch (pair.getKey()) {
                case TYPE_STRING:
                    NetworkTools.writeStringUTF8(buf, (String) arg);
                    break;
                case TYPE_INTEGER:
                    buf.writeInt((Integer) arg);
                    break;
                case TYPE_BLOCKPOS:
                    BlockPos c = (BlockPos) arg;
                    if (c == null) {
                        buf.writeInt(-1);
                        buf.writeInt(-1);
                        buf.writeInt(-1);
                    } else {
                        buf.writeInt(c.getX());
                        buf.writeInt(c.getY());
                        buf.writeInt(c.getZ());
                    }
                    break;
                case TYPE_BOOLEAN:
                    buf.writeByte((Boolean) arg ? 1 : 0);
                    break;
                case TYPE_DOUBLE:
                    buf.writeDouble((Double) arg);
                    break;
                case TYPE_STACK:
                    if (((ItemStack) arg).isEmpty()) {
                        buf.writeBoolean(false);
                    } else {
                        buf.writeBoolean(true);
                        NetworkTools.writeItemStack(buf, (ItemStack) arg);
                    }
                    break;
            }
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        final List<Pair<ArgumentType, Object>> parameters = new ArrayList<>();

        Builder() {
        }

        public Arguments build() {
            return new Arguments(this);
        }

        public Builder value(int i) {
            parameters.add(Pair.of(ArgumentType.TYPE_INTEGER, i));
            return this;
        }
        public Builder value(double i) {
            parameters.add(Pair.of(ArgumentType.TYPE_DOUBLE, i));
            return this;
        }
        public Builder value(boolean i) {
            parameters.add(Pair.of(ArgumentType.TYPE_BOOLEAN, i));
            return this;
        }
        public Builder value(ItemStack i) {
            parameters.add(Pair.of(ArgumentType.TYPE_STACK, i));
            return this;
        }
        public Builder value(BlockPos i) {
            parameters.add(Pair.of(ArgumentType.TYPE_BLOCKPOS, i));
            return this;
        }
        public Builder value(String i) {
            parameters.add(Pair.of(ArgumentType.TYPE_STRING, i));
            return this;
        }
    }
}
