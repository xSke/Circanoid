package pw.ske.circanoid;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class Entity {
    private Body body;
    private Level level;
    private SpringingContext2D positionSpring = new SpringingContext2D(8, 1);

    public Entity(Level level) {
        this.level = level;

        body = level.getWorld().createBody(new BodyDef());
        body.setUserData(this);
    }

    public Body getBody() {
        return body;
    }

    public SpringingContext2D getPositionSpring() {
        return positionSpring;
    }

    public Level getLevel() {
        return level;
    }

    public void draw(ShapeRenderer sr) {
    }

    public void update(float delta) {
        updateSpring(delta);
    }

    public void updatePaused(float delta) {
        updateSpring(delta);
    }

    public void updateSpring(float delta) {
        positionSpring.getTarget().set(body.getPosition());
        positionSpring.update(delta);
    }

    public void hit(Entity other) {

    }

    public void remove() {
        level.getEntities().removeValue(this, true);
        level.getWorld().destroyBody(body);
    }
}
