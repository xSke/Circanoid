package pw.ske.circanoid;

import com.badlogic.gdx.graphics.Color;

public class Palette {
    private Color background;
    private Color outOfBounds;
    private Color brick1;
    private Color brick2;
    private Color brick3;
    private Color brick4;
    private Color brick5;
    private Color ball;
    private Color paddle;

    public Palette() {
        background = Util.newHsbColor(210, 50, 80);
        outOfBounds = Util.newHsbColor(210, 50, 50);
        brick1 = Util.newHsbColor(0, 50, 50);
        brick2 = Util.newHsbColor(40, 50, 50);
        brick3 = Util.newHsbColor(80, 50, 50);
        brick4 = Util.newHsbColor(120, 50, 50);
        brick5 = Util.newHsbColor(160, 50, 50);
        ball = outOfBounds;
        paddle = outOfBounds;
    }

    public Color getBackground() {
        return background;
    }

    public Color getOutOfBounds() {
        return outOfBounds;
    }

    public Color getBrick1() {
        return brick1;
    }

    public Color getBrick2() {
        return brick2;
    }

    public Color getBrick3() {
        return brick3;
    }

    public Color getBrick4() {
        return brick4;
    }

    public Color getBrick5() {
        return brick5;
    }

    public Color getBall() {
        return ball;
    }

    public Color getPaddle() {
        return paddle;
    }
}
