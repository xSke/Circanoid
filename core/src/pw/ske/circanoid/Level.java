package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class Level {
    private final Box2DDebugRenderer box2DDebugRenderer;
    private World world;

    private Array<Entity> entities;

    private float levelRadius;
    private Palette palette;

    private float brickApplyTimer = 0;
    private Array<BrickApplyEntry> toApply = new Array<BrickApplyEntry>();

    private boolean pause;
    private boolean multiBall;
    private int wallHitCount;

    public Level(Palette palette, float levelRadius) {
        entities = new Array<Entity>();
        this.palette = palette;
        this.levelRadius = levelRadius;
        world = new World(new Vector2(0, 0), false);
        world.setContactListener(new CollisionListener(this));

        entities.add(new LevelBorder(this, 128));

        Paddle paddle = new Paddle(this, palette.getPaddle(), 3, 20f, 0.2f);
        paddle.getBody().setTransform(0f, 6f, MathUtils.PI);

        for (int i = 0; i < 1; i++) {
            entities.add(createBall(palette));
        }

        entities.add(paddle);

        box2DDebugRenderer = new Box2DDebugRenderer();
    }

    private Ball createBall(Palette palette) {
        Ball ball = new Ball(this, palette.getBall());
        ball.getBody().setTransform(0, -3, 0);
        ball.getBody().setLinearVelocity(5, 5);
        return ball;
    }

    public void applyBricks(Array<Brick> bricks) {
        toApply = new Array<BrickApplyEntry>();

        bricks = new Array<Brick>(bricks);
        bricks.sort(new Comparator<Brick>() {
            @Override
            public int compare(Brick o1, Brick o2) {
                float a = cornerProximity(o1);
                float b = cornerProximity(o2);
                return ((int) a < (int) b) ? -1 : (((int) a == (int) b) ? 0 : 1);
            }

            private float cornerProximity(Brick brick) {
                return brick.getBody().getPosition().dst(new Vector2(-levelRadius, levelRadius));
            }
        });

        float i = 0;
        float time = 1f / bricks.size;
        for (Brick brick : bricks) {
            toApply.add(new BrickApplyEntry(brick, i));
            i += time;
        }

        brickApplyTimer = 0;
    }

    public void update(float deltaTime) {
        int iterations = 1;

        if (!pause) {
            for (int i = 0; i < iterations; i++) {
                float delta = deltaTime / iterations;
                world.step(delta, 5, 5);

                for (Entity entity : new Array<Entity>(entities)) {
                    entity.update(delta);
                }
            }
        } else {
            for (Entity entity : new Array<Entity>(entities)) {
                entity.updatePaused(deltaTime);
            }
        }

        brickApplyTimer += deltaTime;
        for (BrickApplyEntry bae : toApply) {
            if (bae.getTime() < brickApplyTimer) {
                toApply.removeValue(bae, true);

                entities.add(bae.getBrick());
                Assets.deal.play();
            }
        }
    }

    public void draw(ShapeRenderer sr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(palette.getBackground());
        sr.circle(0, 0, levelRadius, 128);
        sr.circle(0, 0, 0.05f, 16);
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : entities) {
            entity.draw(sr);
        }
        sr.end();

        //box2DDebugRenderer.render(world, sr.getProjectionMatrix());
    }

    public World getWorld() {
        return world;
    }

    public float getLevelRadius() {
        return levelRadius;
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public Palette getPalette() {
        return palette;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isMultiBall() {
        return multiBall;
    }

    public void setMultiBall(boolean multiBall) {
        this.multiBall = multiBall;
    }

    public Ball spawnBall() {
        Ball ball = createBall(palette);
        entities.add(ball);
        return ball;
    }

    public int getWallHitCount() {
        return wallHitCount;
    }

    public void setWallHitCount(int wallHitCount) {
        this.wallHitCount = wallHitCount;
    }
}
