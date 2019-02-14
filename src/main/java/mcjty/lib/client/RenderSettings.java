package mcjty.lib.client;

public class RenderSettings {

    private final int brightness;
    private final int r;
    private final int g;
    private final int b;
    private final int a;
    private final float width;

    private RenderSettings(Builder builder) {
        this.brightness = builder.brightness;
        this.r = builder.r;
        this.g = builder.g;
        this.b = builder.b;
        this.a = builder.a;
        this.width = builder.width;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getA() {
        return a;
    }

    public float getWidth() {
        return width;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int brightness = 240;
        private int r = 255;
        private int g = 255;
        private int b = 255;
        private int a = 255;
        private float width = 1.0f;

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

        public RenderSettings build() {
            return new RenderSettings(this);
        }
    }

}
