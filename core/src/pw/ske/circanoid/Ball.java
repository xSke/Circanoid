package pw.ske.circanoid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class Ball extends TrailHolder {
    private Color color;
    private float speed;
    private boolean hitPaddleThisFrame = false;

    public Ball(Level level, Color color) {
        super(level, color, 100);
        setTrailWidth(0.05f);

        getBody().setType(BodyDef.BodyType.DynamicBody);
        getBody().setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.1f);

        Util.fix(getBody().createFixture(shape, 1f), Util.BALL_CAT, Util.BALL_MASK);
        shape.dispose();

        this.color = color;
    }

    @Override
    public void hit(Entity other) {
        Assets.thump.play();

        Color otherColor = null;
        if (other instanceof Brick) otherColor = ((Brick) other).getColor();
        //if (other instanceof LevelBorder) otherColor = getLevel().getPalette().getOutOfBounds();
        if (other instanceof Paddle) {
            otherColor = ((Paddle) other).getColor();

            if (hitPaddleThisFrame) return;
            hitPaddleThisFrame = true;
        }

        if (otherColor != null) {
            for (int i = 0; i < 10; i++) {
                TrailParticle tp = new TrailParticle(getLevel(), otherColor, 0.2f, 25, 0.5f);
                tp.getBody().setLinearVelocity(new Vector2(3, 0).rotate((float) (Math.random() * 360)));
                tp.getBody().setTransform(getBody().getPosition(), 0);

                getLevel().getEntities().add(tp);
            }
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        getBody().setLinearVelocity(getBody().getLinearVelocity().nor().scl(speed));
        hitPaddleThisFrame = false;

        if (getBody().getPosition().len() > getLevel().getLevelRadius() + 0.25f) {
            getBody().setTransform(0, 0, 0);
        }

        speed += delta * 0.07f;
    }

    @Override
    public void draw(ShapeRenderer sr) {
        super.draw(sr);

        sr.setColor(color);
        sr.circle(getBody().getPosition().x, getBody().getPosition().y, 0.1f, 32);
    }

    public Color getColor() {
        return color;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
