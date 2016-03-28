package pw.ske.circanoid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Util {
    public static int BALL_CAT = 0x02;
    public static int BALL_MASK = 0x0F;

    public static int PADDLE_CAT = 0x04;
    public static int PADDLE_MASK = 0x0F;

    public static int BRICK_CAT = 0x08;
    public static int BRICK_MASK = 0x1F;

    public static int PARTICLE_CAT = 0x10;
    public static int PARTICLE_MASK = 0x1F;

    // Hue: 0-360
    // Sat: 0-100
    // Bgt: 0-100
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        hue /= 360f;
        saturation /= 100f;
        brightness /= 100f;

        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b);
    }

    public static Color newHsbColor(float hue, float saturation, float brightness) {
        Color color = new Color();
        Color.rgb888ToColor(color, HSBtoRGB(hue, saturation, brightness));
        color.a = 1f;
        return color;
    }

    public static void fix(Fixture in, int cat, int mask) {
        in.setRestitution(1.0001f);
        in.setFriction(0);

        Filter f = new Filter();
        f.categoryBits = (short) cat;
        f.maskBits = (short) mask;
        in.setFilterData(f);
    }
}
