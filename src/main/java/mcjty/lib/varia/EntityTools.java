package mcjty.lib.varia;

public class EntityTools {

//    private static final EntityId FIXER = new EntityId();

    /**
     * This method attempts to fix an old-style (1.10.2) entity Id and convert it to the
     * string representation of the new ResourceLocation. The 1.10 version of this function will just return
     * the given id
     * This does not work for modded entities.
     * @param id an old-style entity id as used in 1.10
     * @return
     */
    public static String fixEntityId(String id) {
        return id;
        // @todo 1.14
//        CompoundNBT nbt = new CompoundNBT();
//        nbt.setString("id", id);
//        nbt = FIXER.fixTagCompound(nbt);
//        return nbt.getString("id");
    }
}
