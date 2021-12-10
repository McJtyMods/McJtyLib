package mcjty.lib.gui;

import net.minecraft.resources.ResourceLocation;

public class ManualEntry {

    private final ResourceLocation manual;
    private final ResourceLocation entry;
    private final int page;

    public static final ManualEntry EMPTY = new ManualEntry(null, null);

    public ManualEntry(ResourceLocation manual, ResourceLocation entry) {
        this.manual = manual;
        this.entry = entry;
        this.page = 0;
    }

    public ManualEntry(ResourceLocation manual, ResourceLocation entry, int page) {
        this.manual = manual;
        this.entry = entry;
        this.page = page;
    }

    public ResourceLocation getManual() {
        return manual;
    }

    public ResourceLocation getEntry() {
        return entry;
    }

    public int getPage() {
        return page;
    }
}
