package mcjty.lib.font;

/**
 * TrueTyper: Open Source TTF implementation for Minecraft.
 * Copyright (C) 2013 - Mr_okushama
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class FontLoader {

    public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias) {
        return loadSystemFont(name, defSize, antialias, Font.TRUETYPE_FONT);

    }

    public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias, int type) {
        Font font;
        TrueTypeFont out = null;
        try {
            font = new Font(name, type, (int) defSize);
            font = font.deriveFont(defSize);
            out = new TrueTypeFont(font, antialias);
        } catch (RuntimeException e) {
            Logging.logError("Error loading font!", e);
        }
        return out;
    }

    public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias) {
        return createFont(res, defSize, antialias, Font.TRUETYPE_FONT);
    }

    public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias, int type) {
        return createFont(res, defSize, antialias, type, null);
    }

    public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias, int type, char[] additionalChars) {
        Font font;
        TrueTypeFont out = null;
        try {
            font = Font.createFont(type, Minecraft.getInstance().getResourceManager().getResource(res).getInputStream());
            font = font.deriveFont(defSize);
            out = new TrueTypeFont(font, antialias, additionalChars);
        } catch (RuntimeException | FontFormatException | IOException e) {
            Logging.logError("Error loading font!", e);
        }
        return out;
    }

}