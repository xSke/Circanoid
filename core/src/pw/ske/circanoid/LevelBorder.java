package pw.ske.circanoid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;

public class LevelBorder extends Entity {
    public LevelBorder(Level level, int segments) {
        super(level);

        EdgeShape edge = new EdgeShape();

        for (int i = 0; i < segments; i++) {
            Vector2 a = new Vector2(level.getLevelRadius(), 0).rotate((i / (float) segments) * 360f);
            Vector2 b = new Vector2(level.getLevelRadius(), 0).rotate(((i + 1) / (float) segments) * 360f);

            edge.set(a, b);
            Util.fix(getBody().createFixture(edge, 0), 1, -1);
        }
        edge.dispose();
    }

    @Override
    public void hit(Entity other) {
        if (other instanceof Ball) {
            getLevel().setWallHitCount(getLevel().getWallHitCount() + 1);
            Assets.ow.play();
            if (getLevel().getWallHitCount() > 5) {
                getLevel().setWallHitCount(0);
                Assets.swoopOut.play();
                PlayScreen.i.setState(PlayScreen.GameState.FADE_OUT_DIE);
                PlayScreen.i.setTimer(0);
            }

            PlayScreen.i.getCameraPosition().getVelocity().add(other.getBody().getLinearVelocity().nor().scl(10f * getLevel().getWallHitCount() * getLevel().getWallHitCount()));

            for (Entity entity : getLevel().getEntities()) {
                if (entity instanceof Brick) {
                    entity.getPositionSpring().getVelocity().add(other.getBody().getLinearVelocity().nor().scl(5f * getLevel().getWallHitCount() * getLevel().getWallHitCount()));
                }
            }
        }
    }
}
