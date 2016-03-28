package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class CollisionListener implements ContactListener {
    private Level level;

    public CollisionListener(Level level) {
        this.level = level;
    }

    @Override
    public void beginContact(Contact contact) {
        run(contact.getFixtureA(), contact.getFixtureB());
        run(contact.getFixtureB(), contact.getFixtureA());
    }

    public void run(final Fixture a, Fixture b) {
        final Object au = a.getBody().getUserData();
        final Object bu = b.getBody().getUserData();

        if (au instanceof Entity && bu instanceof Entity) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    ((Entity) au).hit((Entity) bu);
                }
            });
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
