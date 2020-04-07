package mcjty.lib.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;

public class NBTTools {

    public static <T> T getInfoNBT(ItemStack stack, BiFunction<CompoundNBT, String, T> getter, String name, T def) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        CompoundNBT info = tag.getCompound("BlockEntityTag").getCompound("Info");
        if (info.contains(name)) {
            return getter.apply(info, name);
        } else {
            return def;
        }
    }

    public static <T> void setInfoNBT(ItemStack stack, TriConsumer<CompoundNBT, String, T> setter, String name, T value) {
        CompoundNBT entityTag = stack.getOrCreateChildTag("BlockEntityTag");
        CompoundNBT info = entityTag.getCompound("Info");
        setter.accept(info, name, value);
        entityTag.put("Info", info);
    }

    public static boolean hasInfoNBT(ItemStack stack, String name) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        CompoundNBT info = tag.getCompound("BlockEntityTag").getCompound("Info");
        return info.contains(name);
    }

    public static int getInt(ItemStack stack, String name, int def) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        return tag.getInt(name);
    }

    public static String getString(ItemStack stack, String name, String def) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return def;
        }
        return tag.getString(name);
    }

    public static StringBuffer appendIndent(StringBuffer buffer, int indent) {
        return buffer.append(StringUtils.repeat(' ', indent));
    }

    public static void convertNBTtoJson(StringBuffer buffer, ListNBT tagList, int indent) {
        for (int i = 0 ; i < tagList.size() ; i++) {
            CompoundNBT compound = tagList.getCompound(i);
            appendIndent(buffer, indent).append("{\n");
            convertNBTtoJson(buffer, compound, indent + 4);
            appendIndent(buffer, indent).append("},\n");
        }
    }

    public static void convertNBTtoJson(StringBuffer buffer, CompoundNBT tagCompound, int indent) {
        boolean first = true;
        for (Object o : tagCompound.keySet()) {
            if (!first) {
                buffer.append(",\n");
            }
            first = false;

            String key = (String) o;
            INBT tag = tagCompound.get(key);
            appendIndent(buffer, indent).append(key).append(':');
            if (tag instanceof CompoundNBT) {
                CompoundNBT compound = (CompoundNBT) tag;
                buffer.append("{\n");
                convertNBTtoJson(buffer, compound, indent + 4);
                appendIndent(buffer, indent).append('}');
            } else if (tag instanceof ListNBT) {
                ListNBT list = (ListNBT) tag;
                buffer.append("[\n");
                convertNBTtoJson(buffer, list, indent + 4);
                appendIndent(buffer, indent).append(']');
            } else {
                buffer.append(tag);
            }
        }
        if (!first) {
            buffer.append("\n");
        }
    }


}
