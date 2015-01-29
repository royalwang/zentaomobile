package com.cnezsoft.zentao.colorswatch;

import android.graphics.Color;

/**
 * Created by Catouse on 2015/1/29.
 */
public class MaterialColor {
    public static final int Black = Color.parseColor("#000000");
    public static final int White = Color.parseColor("#FFFFFF");

    private int color;
    private boolean inverse;

    public int value() {
        return color;
    }

    public int getColor() {
        return color;
    }

    public boolean isInverse() {
        return inverse;
    }

    public int getInverseColor() {
        return inverse? White :Black;
    }

    public MaterialColor(int color, boolean inverse) {
        this.color = color;
        this.inverse = inverse;
    }

    public MaterialColor(int color) {
        this.color = color;
        this.inverse = brightness() < 0.4f;
    }

    public MaterialColor(String colorString) {
        if(colorString.startsWith("i") || colorString.startsWith("I")) {
            this.inverse = true;
            this.color = Color.parseColor(colorString.substring(1));
        } else {
            this.inverse = false;
            this.color = Color.parseColor(colorString);
        }
    }

    /**
     * Returns the brightness component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    public float brightness() {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int V = Math.max(b, Math.max(r, g));

        return (V / 255.f);
    }

    /**
     * Returns the hue component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    public float hue() {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));

        float H;

        if (V == temp) {
            H = 0;
        } else {
            final float vtemp = (float) (V - temp);
            final float cr = (V - r) / vtemp;
            final float cg = (V - g) / vtemp;
            final float cb = (V - b) / vtemp;

            if (r == V) {
                H = cb - cg;
            } else if (g == V) {
                H = 2 + cr - cb;
            } else {
                H = 4 + cg - cr;
            }

            H /= 6.f;
            if (H < 0) {
                H++;
            }
        }

        return H;
    }

    /**
     * Returns the saturation component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    public float saturation() {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;


        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));

        float S;

        if (V == temp) {
            S = 0;
        } else {
            S = (V - temp) / (float) V;
        }

        return S;
    }
}
