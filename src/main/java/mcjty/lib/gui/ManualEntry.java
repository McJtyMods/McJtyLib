package mcjty.lib.gui;

import net.minecraft.resources.ResourceLocation;

public record ManualEntry(ResourceLocation manual, ResourceLocation entry, int page) {

    public static final ManualEntry EMPTY = new ManualEntry(null, null);

    public ManualEntry(ResourceLocation manual, ResourceLocation entry) {
        this(manual, entry, 0);
    }

    public ManualEntry(ResourceLocation manual, ResourceLocation entry, int page) {
        this.manual = manual;
        this.entry = entry;
        this.page = page;
    }
}
