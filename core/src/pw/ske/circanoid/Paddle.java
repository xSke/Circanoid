package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Paddle extends Entity {
    private float radius;
    private float angle;
    private float thickness;
    private Color color;

    private SpringingContext1D wobble = new SpringingContext1D(8, 0.2f);

    public Paddle(Level level, Color color, float radius, float angle, float thickness) {
        super(level);
        wobble.target = radius;

        this.radius = radius;
        this.angle = angle;
        this.color = color;
        this.thickness = thickness;

        getPositionSpring().setFrequency(16f);
        getPositionSpring().setDamping(0.8f);

        getBody().setType(BodyDef.BodyType.KinematicBody);

        /*PolygonShape sh = new PolygonShape();
        sh.setAsBox(bounds.x / 2, bounds.y / 2);
        Util.fix(getBody().createFixture(sh, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
        sh.dispose();

        CircleShape cs = new CircleShape();
        cs.setRadius(bounds.y / 2f);
        cs.setPosition(new Vector2(-bounds.x / 2, 0));
        Util.fix(getBody().createFixture(cs, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
        cs.setPosition(new Vector2(bounds.x / 2, 0));
        Util.fix(getBody().createFixture(cs, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
        cs.dispose();*/

        /*int segs = 32;

        EdgeShape shape = new EdgeShape();

        makeCollisionSegment(radius - thickness / 2, segs, angle, shape);
        makeCollisionSegment(radius + thickness / 2, segs, angle, shape);

        CircleShape cs = new CircleShape();
        cs.setRadius(thickness / 2f);
        cs.setPosition(new Vector2(0, radius).rotate(-angle).add(0, -radius));
        Util.fix(getBody().createFixture(cs, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
        cs.setPosition(new Vector2(0, radius).rotate(angle).add(0, -radius));
        Util.fix(getBody().createFixture(cs, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
        cs.dispose();*/

        PolygonShape shape = new PolygonShape();
        Array<Vector2> verts = new Array<Vector2>();

        int segs = 8;
        Vector2 last = new Vector2(-1000, -1000);
        for (int i = -1; i < segs; i++) {
            float frac = i / ((float) segs - 1);

            Vector2 vec = new Vector2(0, radius + thickness / 2f).rotate(MathUtils.lerp(angle, -angle, frac));
            vec.add(0, -radius);
            if (last.x != -1000) {
                verts.add(vec);
            }
            last.set(vec);
        }

        shape.set(verts.<Vector2>toArray(Vector2.class));
        Util.fix(getBody().createFixture(shape, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);

        shape.setAsBox(last.x, 1, new Vector2(0, -1.08f), 0);
        Util.fix(getBody().createFixture(shape, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
        shape.dispose();
    }

    /*private void makeCollisionSegment(float width, int segs, float angle, EdgeShape shape) {
        Vector2 last = new Vector2(-1000, -1000);
        for (int i = -1; i < segs; i++) {
            float frac = i / (float) segs;

            Vector2 vec = new Vector2(0, radius).rotate(MathUtils.lerp(-angle, angle, frac));
            vec.add(0, -width);
            if (last.x != -1000) {
                shape.set(last, vec);
                Util.fix(getBody().createFixture(shape, 1f), Util.PADDLE_CAT, Util.PADDLE_MASK);
            }
            last.set(vec);
        }
    }*/

    @Override
    public void hit(Entity other) {
        if (other instanceof Ball) {
            getPositionSpring().getVelocity().add(other.getBody().getLinearVelocity().nor().scl(-50f));
            wobble.velocity = 100f;
        }
    }

    @Override
    public void draw(ShapeRenderer sr) {
        super.draw(sr);

        Vector2 pp = getPositionSpring().getPosition();
        /*sr.rect(pp.x - ps.x / 2f, pp.y - ps.y / 2f, ps.x / 2f, ps.y / 2f, ps.x, ps.y, 1, 1, getBody().getAngle() * MathUtils.radiansToDegrees);
        Vector2 posA = new Vector2(ps.x / 2f, 0).rotateRad(getBody().getAngle()).add(pp);
        sr.circle(posA.x, posA.y, ps.y / 2f, 16);
        Vector2 posB = new Vector2(-ps.x / 2f, 0).rotateRad(getBody().getAngle()).add(pp);
        sr.circle(posB.x, posB.y, ps.y / 2f, 16);*/

        /*sr.translate(pp.x, pp.y, 0);
        sr.rotate(0, 0, 1, getBody().getAngle() * MathUtils.radiansToDegrees);
        sr.arc(0, 0, ps.x / 2f, 0, 180f, 64);
        sr.identity();*/

        sr.setColor(color);
        sr.translate(pp.x, pp.y, 0);
        sr.rotate(0, 0, 1, getBody().getAngle() * MathUtils.radiansToDegrees);

        int segs = 32;

        Vector2 last = new Vector2(-1000, -1000);
        for (int i = 0; i < segs + 1; i++) {
            float frac = i / (float) segs;

            Vector2 vec = new Vector2(0, wobble.value).rotate(MathUtils.lerp(-angle, angle, frac));
            vec.add(0, -wobble.value);
            if (last.x != -1000) {
                sr.rectLine(last, vec, thickness);
            }
            sr.circle(vec.x, vec.y, thickness / 2f, 16);
            last.set(vec);
        }

        sr.identity();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        wobble.update(delta);
        if (PlayScreen.i.getState() == PlayScreen.GameState.PLAY) {
            Vector3 r = PlayScreen.i.getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (!Float.isNaN(r.x)) {
                float rad = getLevel().getLevelRadius() * 0.9f;
                //rad *= 1 + (r.len() / 25f);
                rad = Math.min(rad, getLevel().getLevelRadius() - 0.25f);
                Vector2 vec = new Vector2(rad, 0).rotate(new Vector2(r.x, r.y).angle());
                getBody().setTransform(vec, vec.angleRad() + MathUtils.PI / 2f);
            }
        }
    }

    public Color getColor() {
        return color;
    }

    public SpringingContext1D getWobble() {
        return wobble;
    }

    public float getRadius() {
        return radius;
    }
}
