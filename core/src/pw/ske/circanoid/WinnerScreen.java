package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import net.dermetfan.utils.Pair;

public class WinnerScreen extends ScreenAdapter {
    private Stage stage;
    private Palette palette = new Palette();
    private Array<Image> bits = new Array<Image>();
    private ObjectMap<Actor, Vector2> realPos = new ObjectMap<Actor, Vector2>();
    private Array<Pair<Actor, SpringingContext1D>> springs = new Array<Pair<Actor, SpringingContext1D>>();
    private float timer;
    private float lastFw = 0;
    private ShapeRenderer sr;
    private ObjectFloatMap<TrailParticle> fireworkTimers = new ObjectFloatMap<TrailParticle>();

    private Level level = new Level(palette, 0f);
    private final int screenWidth;


    public WinnerScreen() {
        Assets.dududududu.setLooping(true);
        Assets.dududududu.setVolume(0.4f);
        Assets.dududududu.play();

        level.getEntities().clear();
        level.getWorld().setGravity(new Vector2(0, -20));

        sr = new ShapeRenderer();
        screenWidth = 640;
        int screenHeight = 480;
        stage = new Stage(new ScalingViewport(Scaling.fillY, screenWidth, screenHeight));

        int total = 6;
        for (int i = 0; i < total; i++) {
            Texture tex = new Texture("winrar/" + (i + 1) + ".png");
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Image logoBit = new Image(tex);

            int width = 600;
            logoBit.setHeight(width * (logoBit.getHeight() / logoBit.getWidth()));
            logoBit.setWidth(width);
            logoBit.setOrigin(Align.center);
            logoBit.setAlign(Align.center);

            logoBit.setX(screenWidth / 2 - logoBit.getWidth() / 2);
            logoBit.setX(logoBit.getX() + (i - (total - 1) / 2f) * 20);
            logoBit.setY(screenHeight - 200);

            stage.addActor(logoBit);

            realPos.put(logoBit, new Vector2(logoBit.getX(), logoBit.getY()));
            bits.add(logoBit);
        }

        makeButton("quit_up.png", "quit_down.png", 100, screenWidth / 2).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Circanoid.i.setScreen(new MainMenuScreen());
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.input.setInputProcessor(stage);

        Gdx.gl.glClearColor(palette.getOutOfBounds().r, palette.getOutOfBounds().g, palette.getOutOfBounds().b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;
        int i = 0;
        for (Image img : bits) {
            Vector2 rp = realPos.get(img);
            Vector2 newPos = rp.cpy().add(new Vector2(10, 0).rotate(-timer * 300 + i * 50));
            img.setPosition(newPos.x, newPos.y);
            img.setScale(MathUtils.sin(timer * 3 + i * 100) / 2 + 1f);

            i++;
        }

        for (Pair<Actor, SpringingContext1D> p : springs) {
            p.getValue().update(delta);
            p.getKey().setScale(p.getValue().value);
        }

        stage.act(delta);
        stage.draw();

        sr.setProjectionMatrix(stage.getCamera().combined);
        level.update(delta);
        level.draw(sr);

        if (timer > lastFw + 2.5f) {
            lastFw = timer;

            spawnFirework();
        }

        for (TrailParticle fw : fireworkTimers.keys()) {
            float timer = fireworkTimers.getAndIncrement(fw, 0, delta);
            if (timer > 2) {
                fireworkTimers.remove(fw, 0);

                Assets.boom.play();

                for (int ii = 0; ii < 10; ii++) {
                    TrailParticle tp = new TrailParticle(level, fw.getTrailColor(), 1f, 200, 5f);
                    tp.setTrailWidth(1);
                    tp.getBody().setTransform(fw.getBody().getPosition(), 0);
                    tp.getBody().setLinearVelocity(new Vector2(50, 0).rotate(MathUtils.random(360f)));
                    level.getEntities().add(tp);
                }
            }
        }
    }

    private void spawnFirework() {
        Assets.swoopOut.play();

        Color color = null;
        int i = MathUtils.random(4);
        if (i == 0) color = palette.getBrick1();
        if (i == 1) color = palette.getBrick2();
        if (i == 2) color = palette.getBrick3();
        if (i == 3) color = palette.getBrick4();
        if (i == 4) color = palette.getBrick5();

        TrailParticle tp = new TrailParticle(level, color, 2f, 200, 5f);
        tp.setTrailWidth(2);
        tp.getBody().setTransform(MathUtils.random(150, screenWidth - 150), MathUtils.random(100f, 200f), 0);
        tp.getBody().setLinearVelocity(MathUtils.random(-200, 100), 800);
        level.getEntities().add(tp);

        fireworkTimers.put(tp, 0f);
    }

    private ImageButton makeButton(String upPath, String downPath, float y, float start) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        Texture up = new Texture(upPath);
        up.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.up = new TextureRegionDrawable(new TextureRegion(up));
        Texture down = new Texture(downPath);
        down.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.over = new TextureRegionDrawable(new TextureRegion(down));
        style.down = new TextureRegionDrawable(new TextureRegion(down));

        final ImageButton button = new ImageButton(
                style
        );
        button.setSize(200f, button.getHeight() / button.getWidth() * 200f);
        button.setPosition(start - (button.getWidth() / 2f), y);
        button.getColor().a = 1;
        button.setOrigin(Align.center);
        button.setTransform(true);
        stage.addActor(button);

        final SpringingContext1D scale = new SpringingContext1D(4, 0.5f);
        scale.target = 1f;
        scale.value = 1f;
        springs.add(new Pair<Actor, SpringingContext1D>(button, scale));

        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                scale.target = 0.9f;

                if (Gdx.input.isButtonPressed(event.getButton())) {
                    scale.target = 0.8f;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                scale.target = 1f;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                scale.target = 1.2f;
            }
        });

        return button;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
