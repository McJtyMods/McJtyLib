package mcjty.lib.gui.icons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageIcon implements IIcon, Cloneable {

    private ResourceLocation image = null;
    private int u;
    private int v;
    private int width;
    private int height;
    private final String id;

    private List<IIcon> overlays;
    private Map<String, Object> dataMap;

    public ResourceLocation getImage() {
        return image;
    }

    public ImageIcon(String id) {
        this.id = id;
    }

    @Override
    public void addOverlay(IIcon icon) {
        if (overlays == null) {
            overlays = new ArrayList<>();
        }
        overlays.add(icon);
    }

    @Override
    public void removeOverlay(String id) {
        if (overlays == null) {
            return;
        }
        IIcon toRemove = null;
        for (IIcon icon : overlays) {
            if (id.equals(icon.getID())) {
                toRemove = icon;
                break;
            }
        }
        if (toRemove != null) {
            overlays.remove(toRemove);
        }
    }

    @Override
    public boolean hasOverlay(String id) {
        if (overlays == null) {
            return false;
        }
        for (IIcon icon : overlays) {
            if (id.equals(icon.getID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearOverlays() {
        overlays = null;
    }

    @Override
    public void addData(String name, Object data) {
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
        dataMap.put(name, data);
    }

    @Override
    public void removeData(String name) {
        if (dataMap == null) {
            return;
        }
        dataMap.remove(name);
    }

    @Override
    public void clearData() {
        dataMap = null;
    }

    @Override
    public Map<String, Object> getData() {
        return dataMap;
    }

    public ImageIcon setImage(ResourceLocation image, int u, int v) {
        this.image = image;
        this.u = u;
        this.v = v;
        return this;
    }

    public ImageIcon setDimensions(int w, int h) {
        this.width = w;
        this.height = h;
        return this;
    }

    @Override
    public void draw(Screen gui, PoseStack matrixStack, int x, int y) {
        // @todo 1.17
//        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        Minecraft.getInstance().getTextureManager().bind(image);
        gui.blit(matrixStack, x, y, u, v, width, height);
        if (overlays != null) {
            for (IIcon icon : overlays) {
                icon.draw(gui, matrixStack, x, y);
            }
        }
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public IIcon clone() {
        ImageIcon imageIcon = new ImageIcon(id)
                .setImage(image, u, v)
                .setDimensions(width, height);
        if (overlays != null) {
            imageIcon.overlays = new ArrayList<>();
            for (IIcon icon : overlays) {
                imageIcon.overlays.add(icon.clone());
            }
        }
        if (dataMap != null) {
            imageIcon.dataMap = new HashMap<>(dataMap);
        }
        return imageIcon;
    }
}
