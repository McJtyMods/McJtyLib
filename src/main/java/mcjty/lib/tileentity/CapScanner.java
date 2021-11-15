package mcjty.lib.tileentity;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

public class CapScanner {

    public static <T extends GenericTileEntity> Field[] scan(Class teClass, T instance) {
        return FieldUtils.getFieldsWithAnnotation(teClass, Cap.class);
    }
}
