package mcjty.lib.sync;

import mcjty.lib.tileentity.GenericTileEntity;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class AnnotationSyncScanner {

    public static <T extends GenericTileEntity> void scanGuiSync(Class teClass, T instance, BiConsumer<GuiSync, Field> consumer) {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(teClass, GuiSync.class);
        Arrays.stream(fields).forEach(field -> {
            GuiSync annotation = field.getAnnotation(GuiSync.class);
            consumer.accept(annotation, field);
        });
    }
}
