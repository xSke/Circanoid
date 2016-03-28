package pw.ske.circanoid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Brick extends Entity {
    private final boolean extremeWobblyPowerrrrr;
    private float wobbleTimer = MathUtils.random(10f);

    private Vector2 size;
    private Color color;
    private float alpha = 0;
    private SpringingContext1D scale = new SpringingContext1D(4, 0.2f);

    private boolean isDestroying;
    private float destroyTimer;
    private float initTimer = 0;

    public Brick(Level level, Vector2 size, Color color, boolean anchor, boolean extremeWobblyPowerrrrr) {
        super(level);
        this.extremeWobblyPowerrrrr = extremeWobblyPowerrrrr;

        scale.value = 0;
        scale.target = 1;

        if (!anchor) {
            getBody().setType(BodyDef.BodyType.DynamicBody);
        }

        //getBody().setType(BodyDef.BodyType.DynamicBody);
        getPositionSpring().setFrequency(4);
        getPositionSpring().setDamping(0.5f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / 2f, size.y / 2f);
        Util.fix(getBody().createFixture(shape, 1f), Util.BRICK_CAT, Util.BRICK_MASK);

        this.size = size;
        this.color = color;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        scale.update(delta);
        initTimer = 1f;
        alpha = 1f;
        if (isDestroying) {
            destroyTimer += delta;

            float destroyFrac = destroyTimer / 0.2f;
            alpha = 1 - destroyFrac;
            scale.target = 0;

            if (destroyFrac > 1f) {
                remove();
            }
        }

        if (extremeWobblyPowerrrrr) {
            wobbleTimer += delta;

            scale.target = (MathUtils.sin(wobbleTimer * 5) / 4) + 1f;
        }
    }

    @Override
    public void updatePaused(float delta) {
        super.updatePaused(delta);
        scale.update(delta);
        initTimer += delta;
        alpha = Math.min(initTimer / 0.5f, 1f);
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Ball) {
            if (!isDestroying) {
                if (getLevel().isMultiBall()) {
                    getLevel().spawnBall().getBody().setTransform(getBody().getPosition(), 0);
                }

                Assets.blip.play();

                isDestroying = true;
                for (Fixture fix : getBody().getFixtureList()) {
                    fix.setSensor(true);
                }

                for (int i = 0; i < 10; i++) {
                    TrailParticle tp = new TrailParticle(getLevel(), color, 0.2f, 25, 0.5f);
                    tp.getBody().setLinearVelocity(new Vector2(3, 0).rotate((float) (Math.random() * 360)));
                    tp.getBody().setTransform(other.getBody().getPosition(), 0);

                    getLevel().getEntities().add(tp);
                }
            }

            getPositionSpring().getVelocity().add(other.getBody().getLinearVelocity().nor().scl(-10f));

            for (Entity entity : getLevel().getEntities()) {
                if (entity instanceof Brick) {
                    ((Brick) entity).scale.velocity = 2f;
                    if (entity != this) {
                        entity.getPositionSpring().getVelocity().add(entity.getBody().getPosition().sub(getBody().getPosition()).nor().scl(5f));
                    }
                }
            }
        }
    }

    @Override
    public void draw(ShapeRenderer sr) {
        super.draw(sr);

        sr.setColor(color);
        Vector2 p = getPositionSpring().getPosition();
        Vector2 s = size;

        float _s = scale.value;
        sr.getColor().a = alpha;
        sr.rect(
                p.x - s.x / 2f * _s,
                p.y - s.y / 2f * _s,
                s.x * _s / 2f,
                s.y * _s / 2f,
                s.x * _s,
                s.y * _s,
                1, 1,
                getBody().getAngle() * MathUtils.radiansToDegrees);
        sr.getColor().a = 1f;
    }

    public Color getColor() {
        return color;
    }

    public void setDestroying(boolean destroying) {
        isDestroying = destroying;
    }

    public void setDestroyTimer(float destroyTimer) {
        this.destroyTimer = destroyTimer;
    }
}
