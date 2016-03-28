package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class PlayScreen extends ScreenAdapter {
    public static PlayScreen i;

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public float getSpeed() {
        return speed;
    }

    public void setEscPaused(boolean escPaused) {
        this.escPaused = escPaused;
    }

    public enum GameState {
        WAIT_BRICK_SPAWN,
        FADE_SPEED_IN,
        PLAY,
        FADE_SPEED_OUT,
        NEW_LEVEL,
        FADE_OUT_DIE,
        EXPLODE
    }

    private Level level;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Palette palette;
    private SpringingContext2D cameraPosition = new SpringingContext2D(8, 1);

    private GameState state = GameState.WAIT_BRICK_SPAWN;
    private float timer;
    private float timer2;
    private Array<Brick> toRemove = new Array<Brick>();

    private float speed = 1;
    private int levelNo = 1;
    private boolean escPaused = false;
    private PauseScreenUI psui = new PauseScreenUI();

    public PlayScreen() {
        i = this;

        Gdx.input.setInputProcessor(null);

        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        palette = new Palette();

        level = new Level(palette, 7);

        level1();

        psui.hide();
    }

    @Override
    public void render(float delta) {
        HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(palette.getOutOfBounds().r, palette.getOutOfBounds().g, palette.getOutOfBounds().b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            escPaused = !escPaused;

            if (escPaused) {
                psui.show();
            } else {
                psui.hide();
            }
        }

        if (!escPaused) {
            cameraPosition.update(delta);
            camera.position.set(cameraPosition.getPosition(), 0f);
            camera.update();

            handleState(delta);

            level.update(delta * speed);
            Gdx.input.setInputProcessor(null);
        } else {
            Gdx.input.setInputProcessor(psui);
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        level.draw(shapeRenderer);

        psui.act(delta);
        psui.draw();

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof Brick) {
                    entity.remove();
                }
            }
        }*/
    }


    public void level1() {
        Array<Brick> level1 = new Array<Brick>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 5; y++) {
                Color color = null;
                if (y == 0) color = palette.getBrick1();
                if (y == 1) color = palette.getBrick2();
                if (y == 2) color = palette.getBrick3();
                if (y == 3) color = palette.getBrick4();
                if (y == 4) color = palette.getBrick5();

                Brick brick = new Brick(level, new Vector2(0.6f, 0.3f), color, true, false);
                brick.getBody().setTransform(x * 0.8f - 2.8f, y * 0.5f - 1.1f, 0f);
                level1.add(brick);
            }
        }
        level.applyBricks(level1);
    }

    public void level2() {
        Array<Brick> level2 = new Array<Brick>();
        for (int row = 0; row < 6; row++) {
            int bricksInRing = 1 + row * 4;
            for (int ring = 0; ring < bricksInRing; ring++) {
                Color color = null;
                if (row == 5) color = palette.getBrick1();
                else if (row == 4) color = palette.getBrick2();
                else if (row == 3) color = palette.getBrick3();
                else if (row == 2) color = palette.getBrick4();
                else color = palette.getBrick5();

                Brick brick = new Brick(level, new Vector2(0.3f + (row * 0.06f), 0.3f), color, true, false);
                brick.getBody().setTransform(
                        new Vector2(row * 0.5f, 0).rotate((ring / (float) bricksInRing) * 360f),
                        (0.25f + ring / (float) bricksInRing) * MathUtils.PI2);
                level2.add(brick);
            }
        }
        level.applyBricks(level2);
    }


    public void level3() {
        Array<Brick> level3 = new Array<Brick>();
        int bricksInRing = 64;

        for (int i = 0; i < bricksInRing; i++) {
            Color color = null;
            if (i % 8 == 0) color = palette.getBrick1();
            else if (i % 8 == 1 || i % 8 == 7) color = palette.getBrick2();
            else if (i % 8 == 2 || i % 8 == 6) color = palette.getBrick3();
            else if (i % 8 == 3 || i % 8 == 5) color = palette.getBrick4();
            else color = palette.getBrick5();

            Brick brick = new Brick(level, new Vector2(0.6f, 0.3f), color, true, false);
            brick.getBody().setTransform(
                    new Vector2(6.5f, 0).rotate((i / (float) bricksInRing) * 360f),
                    (0.25f + i / (float) bricksInRing) * MathUtils.PI2);
            level3.add(brick);
        }
        level.applyBricks(level3);
    }

    public void level4() {
        //level.setMultiBall(true);
        Array<Brick> level4 = new Array<Brick>();

        int width = 11;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < 8; y++) {
                if ((x + y) % 2 == 0) {
                    Color color = null;
                    if (y == 0) color = palette.getBrick1();
                    if (y == 1 || y == 7) color = palette.getBrick2();
                    if (y == 2 || y == 6) color = palette.getBrick3();
                    if (y == 3 || y == 5) color = palette.getBrick4();
                    if (y == 4) color = palette.getBrick5();

                    Brick brick = new Brick(level, new Vector2(0.6f, 0.3f), color, true, false);
                    float wScale = 0.8f;
                    brick.getBody().setTransform(x * wScale - (width - 1) * wScale / 2, y * 0.5f - 0.5f, 0f);
                    level4.add(brick);
                }
            }
        }
        level.applyBricks(level4);
    }

    public void level5() {
        Array<Brick> level5 = new Array<Brick>();

        for (int i = 0; i < 100; i++) {
            Color color = null;
            int ii = MathUtils.random(4);

            if (ii == 0) color = palette.getBrick1();
            if (ii == 1) color = palette.getBrick2();
            if (ii == 2) color = palette.getBrick3();
            if (ii == 3) color = palette.getBrick4();
            if (ii == 4) color = palette.getBrick5();

            Brick brick = new Brick(level, new Vector2(0.5f, 0.5f), color, true, true);
            brick.getBody().setTransform(new Vector2(MathUtils.random(6f) - 3f, MathUtils.random(6f) - 3f), (float) (Math.random() * MathUtils.PI2));
            level5.add(brick);
        }
        level.applyBricks(level5);
    }

    private void handleState(float delta) {
        if (state == GameState.WAIT_BRICK_SPAWN) {
            level.setPause(true);

            for (Entity entity : level.getEntities()) {
                if (entity instanceof Paddle) {
                    ((Paddle) entity).getWobble().target = ((Paddle) entity).getRadius();
                }

                if (entity instanceof Ball) {
                    ((Ball) entity).setSpeed(7);

                    if (entity.getBody().getLinearVelocity().len() < 0.01f) {
                        entity.getBody().setLinearVelocity(1, 1);
                    }
                }
            }

            level.setWallHitCount(0);
            if (timer > 2f) {
                state = GameState.FADE_SPEED_IN;
                Assets.swoopIn.play();
                timer = 0;
            }
        } else if (state == GameState.FADE_SPEED_IN) {
            level.setPause(false);
            speed = Interpolation.exp5In.apply(timer);
            if (timer > 1.0f) {
                state = GameState.PLAY;
                timer = 0;
            }
        } else if (state == GameState.PLAY) {
            level.setPause(false);

            int bricks = 0;
            for (Entity entity : level.getEntities()) {
                if (entity instanceof Brick) bricks++;
            }
            if (bricks == 0) {
                Assets.win.play();
                Assets.swoopOut.play();
                state = GameState.FADE_SPEED_OUT;
                timer = 0;
            }
        } else if (state == GameState.FADE_SPEED_OUT) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof Paddle) {
                    ((Paddle) entity).getWobble().target = 0;
                }
            }

            speed = Interpolation.exp5In.apply(1 - timer);
            if (timer > 1.0f) {
                state = GameState.NEW_LEVEL;
                timer = 0;
            }
        } else if (state == GameState.NEW_LEVEL) {
            level.setPause(true);

            if (timer > 2f) {
                state = GameState.WAIT_BRICK_SPAWN;
                timer = 0;
                speed = 1;
                levelNo++;

                startLevel();
            }
        } else if (state == GameState.FADE_OUT_DIE) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof Paddle) {
                    ((Paddle) entity).getWobble().target = ((Paddle) entity).getRadius() * 1.1f;
                }
            }

            speed = Interpolation.exp5In.apply(1 - timer);

            for (Entity entity : new Array<Entity>(level.getEntities())) {
                if (entity instanceof Brick) {
                    entity.getPositionSpring().getVelocity().add(new Vector2(timer * 5, 0).rotate((float) (Math.random() * 360)));
                }
            }
            cameraPosition.getVelocity().add(new Vector2(timer * 30, 0).rotate((float) (Math.random() * 360)));
            if (timer > 1.0f) {
                state = GameState.EXPLODE;
                timer = 0;
                timer2 = 0;

                cameraPosition.setVelocity(new Vector2(70, 0).rotate((float) (Math.random() * 360)));

                toRemove.clear();
                Assets.boom.play();

                final Vector2 ballPos = new Vector2();
                for (Entity entity : new Array<Entity>(level.getEntities())) {
                    if (entity instanceof Ball) {
                        for (int i = 0; i < 100; i++) {
                            TrailParticle tp = new TrailParticle(level, ((Ball) entity).getColor(), 1f, 25, 0.5f);
                            tp.getBody().setTransform(entity.getBody().getPosition(), 0);
                            tp.getBody().setLinearVelocity(new Vector2(5, 0).rotate((float) (Math.random() * 360)));
                            level.getEntities().add(tp);
                        }
                        ballPos.set(entity.getBody().getPosition());

                        entity.remove();
                    }

                    if (entity instanceof Brick) {
                        toRemove.add((Brick) entity);
                    }
                }

                toRemove.sort(new Comparator<Brick>() {
                    @Override
                    public int compare(Brick o1, Brick o2) {
                        float a = cornerProximity(o1);
                        float b = cornerProximity(o2);
                        return ((int) a < (int) b) ? -1 : (((int) a == (int) b) ? 0 : 1);
                    }

                    private float cornerProximity(Brick brick) {
                        return brick.getBody().getPosition().dst(ballPos);
                    }
                });
            }
        } else if (state == GameState.EXPLODE) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof Paddle) {
                    ((Paddle) entity).getWobble().target = 0f;
                }
            }

            if (toRemove.size > 0) {
                timer2 += delta;

                float f = 1f / toRemove.size;
                f *= 0.25f;
                while (timer2 > f) {
                    timer2 -= f;
                    if (toRemove.size > 0) {
                        cameraPosition.getVelocity().add(new Vector2(20, 0).rotate((float) (Math.random() * 360)));
                        Brick entity = toRemove.removeIndex(0);
                        entity.setDestroying(true);
                        entity.getPositionSpring().getVelocity().set(entity.getBody().getPosition().nor().scl(20f));
                    }
                }
            }

            speed = MathUtils.clamp(Interpolation.exp5In.apply(1 - (timer / 3)), 0.3f, 1);
            if (timer > 3) {
                level.setWallHitCount(0);
                speed = 1;
                level.spawnBall();
                startLevel();
                timer = 0;
                state = GameState.WAIT_BRICK_SPAWN;
            }
        }

        timer += delta;
    }

    private void startLevel() {
        if (levelNo == 1) {
            level1();
        } else if (levelNo == 2) {
            level2();
        } else if (levelNo == 3) {
            level3();
        } else if (levelNo == 4) {
            level4();
        } else if (levelNo == 5) {
            level5();
        } else {
            Circanoid.i.setScreen(new WinnerScreen());
        }
    }

    @Override
    public void resize(int width, int height) {
        float ratio = width * 1f / height;
        float padding = 1.3f;

        float maxDimension = level.getLevelRadius() * 2f;
        camera.setToOrtho(false, maxDimension * padding * ratio, maxDimension * padding);
        camera.position.set(0, 0, 0);
        camera.update();

        psui.getViewport().update(width, height, true);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public SpringingContext2D getCameraPosition() {
        return cameraPosition;
    }
}
