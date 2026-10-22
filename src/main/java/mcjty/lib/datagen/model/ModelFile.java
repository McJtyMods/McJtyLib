package mcjty.lib.datagen.model;
import net.minecraft.resources.Identifier;
public class ModelFile {
    private final Identifier location;
    public ModelFile(Identifier location) { this.location = location; }
    public Identifier getLocation() { return location; }
    public static class UncheckedModelFile extends ModelFile { public UncheckedModelFile(Identifier id) { super(id); } }
}
