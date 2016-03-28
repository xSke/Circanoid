package pw.ske.circanoid;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class TrailHolder extends Entity {
    private Color trailColor;

    private boolean trailEnabled = true;

    private Array<Vector2> pastPositions = new Array<Vector2>();
    private float timer;
    private float trailWidth;

    public TrailHolder(Level level, Color trailColor, int trailLength) {
        super(level);
        this.trailColor = trailColor;
        for (int i = 0; i < trailLength; i++) {
            pastPositions.add(new Vector2(-1000, -1000));
        }
    }

    @Override
    public void draw(ShapeRenderer sr) {
        super.draw(sr);

        sr.setColor(trailColor);
        Vector2 last = new Vector2();
        int lastI = 0;
        for (int i = 0; i < pastPositions.size; i++) {
            float a = lastI / (float) pastPositions.size;
            float b = i / (float) pastPositions.size;

            float x1 = pastPositions.get(lastI).x;
            float y1 = pastPositions.get(lastI).y;
            float x2 = pastPositions.get(i).x;
            float y2 = pastPositions.get(i).y;

            if (x1 != -1000 && y1 != -1000 && x2 != -1000 && y2 != -1000) {
                Vector2 t = new Vector2().set(y2 - y1, x1 - x2).nor();

                float w = trailWidth;

                float widthA = a * w;
                float txA = t.x * widthA;
                float tyA = t.y * widthA;
                float widthB = b * w;
                float txB = t.x * widthB;
                float tyB = t.y * widthB;

                sr.triangle(x1 + txA, y1 + tyA, x1 - txA, y1 - tyA, x2 + txB, y2 + tyB);
                sr.triangle(x2 - txB, y2 - tyB, x2 + txB, y2 + tyB, x1 - txA, y1 - tyA);
                sr.circle(x2, y2, b * w, 16);
            }

            last.set(pastPositions.get(i));
            lastI = i;
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        float speed = 1;
        if (PlayScreen.i != null) {
            speed = PlayScreen.i.getSpeed();
        }
        timer += delta / speed;

        while (timer > 0.01f) {
            timer -= 0.01f;

            updatePosition();
        }
    }

    public void updatePosition() {
        for (int i = 0; i < pastPositions.size - 1; i++) {
            pastPositions.get(i).set(pastPositions.get(i + 1));
        }
        if (trailEnabled) {
            pastPositions.get(pastPositions.size - 1).set(getBody().getPosition());

        } else {
            pastPositions.get(pastPositions.size - 1).set(new Vector2(-1000, -1000));
        }
    }

    public boolean isTrailEnabled() {
        return trailEnabled;
    }

    public void setTrailEnabled(boolean trailEnabled) {
        this.trailEnabled = trailEnabled;
    }

    public float getTrailWidth() {
        return trailWidth;
    }

    public void setTrailWidth(float trailWidth) {
        this.trailWidth = trailWidth;
    }

    public Color getTrailColor() {
        return trailColor;
    }
}
