package pw.ske.circanoid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class TrailParticle extends TrailHolder {
    private float stopIn;
    private float finishTime;

    public TrailParticle(Level level, Color trailColor, float time, int trailLength, float finishTime) {
        super(level, trailColor, trailLength);
        this.finishTime = finishTime;
        this.stopIn = time;
        setTrailWidth(0.02f);

        getBody().setType(BodyDef.BodyType.DynamicBody);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.01f);

        Util.fix(getBody().createFixture(shape, 1f), Util.PARTICLE_CAT, Util.PARTICLE_MASK);
        shape.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        stopIn -= delta;

        if (stopIn < 0) {
            setTrailEnabled(false);
        }
        if (stopIn < -finishTime) {
            // Give time for trail to finish
            remove();
        }
    }
}
