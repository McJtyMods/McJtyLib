package mcjty.lib.client;

import net.minecraft.client.renderer.RenderType;

import static net.minecraft.client.renderer.LightTexture.FULL_BLOCK;

public record RenderSettings(
    int brightness,
    int r,
    int g,
    int b,
    int a,
    float width,
    RenderType renderType) {

    private RenderSettings(Builder builder) {
        this(builder.brightness, builder.r, builder.g, builder.b, builder.a, builder.width, builder.renderType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int brightness = FULL_BLOCK;
        private int r = 255;
        private int g = 255;
        private int b = 255;
        private int a = 255;
        private float width = 1.0f;
        private RenderType renderType = CustomRenderTypes.translucent();

        public Builder brightness(int brightness) {
            this.brightness = brightness;
            return this;
        }

        public Builder red(int r) {
            this.r = r;
            return this;
        }

        public Builder green(int g) {
            this.g = g;
            return this;
        }

        public Builder blue(int b) {
            this.b = b;
            return this;
        }

        public Builder alpha(int a) {
            this.a = a;
            return this;
        }

        public Builder color(int color) {
            r = (color >> 16 & 0xFF);
            g = (color >> 8 & 0xFF);
            b = (color & 0xFF);
            a = (color >> 24 & 0xFF);
            return this;
        }

        public Builder color(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            return this;
        }

        public Builder width(float width) {
            this.width = width;
            return this;
        }

        public Builder renderType(RenderType renderType) {
            this.renderType = renderType;
            return this;
        }

        public RenderSettings build() {
            return new RenderSettings(this);
        }
    }

}
