package pw.ske.circanoid;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import net.dermetfan.utils.Pair;

public class MainMenuScreen extends ScreenAdapter {
    private Palette palette = new Palette();
    private Stage stage;
    private Array<Pair<Actor, SpringingContext1D>> springs = new Array<Pair<Actor, SpringingContext1D>>();

    public MainMenuScreen() {
        //Assets.dududududu.stop();
        stage = new Stage(new ScalingViewport(Scaling.fillY, 1, 1));

        for (int i = 0; i < 9; i++) {
            Image logoBit = new Image(new Texture("logo/" + (i + 1) + ".png"));
            //logoBit.setAlign(Align.center);

            logoBit.setSize(1, logoBit.getHeight() / logoBit.getWidth());
            logoBit.setPosition(0, 1f);

            logoBit.setOrigin(Align.center);

            MoveToAction mta = new MoveToAction();
            mta.setPosition(0, 0.8f);
            mta.setDuration(1f);
            mta.setInterpolation(Interpolation.elasticOut);

            DelayAction da = new DelayAction(i * 0.1f);

            RunnableAction rba = new RunnableAction();
            rba.setRunnable(new Runnable() {
                @Override
                public void run() {
                    Assets.deal.play();
                }
            });


            SequenceAction sa = new SequenceAction(da, rba, mta);
            logoBit.addAction(sa);

            DelayAction da2 = new DelayAction(1.5f + (i * 0.01f));

            ScaleByAction sba = new ScaleByAction();
            sba.setDuration(0.2f);
            sba.setInterpolation(Interpolation.exp5Out);
            sba.setAmount(0.2f);

            SequenceAction sa2 = new SequenceAction(da2, sba);
            logoBit.addAction(sa2);

            stage.addActor(logoBit);
        }

        makeButton("play_up.png", "play_down.png", 1.5f, 0.5f, 0f, 0).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Circanoid.i.setScreen(new PlayScreen());
            }
        });

        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            makeButton("quit_up.png", "quit_down.png", 1.6f, 0.35f, 1f, 0).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.exit();
                }
            });
        }

        //stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);
    }

    private ImageButton makeButton(String upPath, String downPath, float delay, float y, float start, float pp) {
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
        button.setSize(0.4f, button.getHeight() / button.getWidth() * 0.4f);
        button.setPosition(start - (button.getWidth() / 2f), y);
        button.getColor().a = 0;
        button.setOrigin(Align.center);
        button.setTransform(true);
        stage.addActor(button);

        DelayAction da1 = new DelayAction(delay);

        MoveToAction mta1 = new MoveToAction();
        mta1.setDuration(0.4f);
        mta1.setPosition(0.5f - (button.getWidth() / 2f), y);
        mta1.setInterpolation(Interpolation.exp5Out);

        AlphaAction aa1 = new AlphaAction();
        aa1.setAlpha(1);
        aa1.setDuration(0.4f);
        aa1.setInterpolation(Interpolation.exp5Out);

        RunnableAction rba = new RunnableAction();
        rba.setRunnable(new Runnable() {
            @Override
            public void run() {
                Assets.thump.play();
            }
        });

        ParallelAction pa1 = new ParallelAction(mta1, aa1);
        SequenceAction sa1 = new SequenceAction(da1, rba, pa1);
        button.addAction(sa1);

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
    public void render(float delta) {
        Gdx.gl.glClearColor(palette.getOutOfBounds().r, palette.getOutOfBounds().g, palette.getOutOfBounds().b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (Pair<Actor, SpringingContext1D> p : springs) {
            p.getValue().update(delta);

            p.getKey().setScale(p.getValue().value);
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
