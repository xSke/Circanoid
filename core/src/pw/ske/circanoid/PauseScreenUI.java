package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.dermetfan.utils.Pair;

public class PauseScreenUI extends Stage {
    private final Table table;
    private final ImageButton goAway;
    private final ImageButton menu;
    private Image background;
    private Array<ImageButton> buttons;
    private Array<Pair<Actor, SpringingContext1D>> springs = new Array<Pair<Actor, SpringingContext1D>>();

    public PauseScreenUI() {
        super(new ScreenViewport());
        NinePatchDrawable outline = new NinePatchDrawable(new NinePatch(new Texture("border.png"), 7, 7, 7, 7));

        background = new Image(new Texture("pixel.png"));
        background.setBounds(0, 0, 10000, 10000);
        background.getColor().a = 0;
        addActor(background);

        table = new Table();
        table.background(outline);

        table.setSize(250, 300);
        addActor(table);

        Texture texture = new Texture("paused.png");
        //texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image paused = new Image(texture);
        paused.setOrigin(Align.center);
        table.add(paused).width(texture.getWidth()).height(texture.getHeight()).expandY().top().padTop(16).row();

        goAway = makeButton("quit2_up.png", "quit2_down.png");
        float aspect = goAway.getWidth() / goAway.getHeight();
        float w = 200;
        table.add(goAway).width(w).height(w / aspect).padBottom(24).row();

        menu = makeButton("menu_up.png", "menu_down.png");
        table.add(menu).width(w).height(w / aspect).padBottom(24).row();

        table.setTransform(true);
        table.setOrigin(Align.center);
        table.getColor().a = 0;

        buttons = Array.with(goAway, menu);

        goAway.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayScreen.i.setEscPaused(false);
                hide();
            }
        });

        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Circanoid.i.setScreen(new MainMenuScreen());
            }
        });


        //table.debug();
    }

    public void show() {
        AlphaAction aa = new AlphaAction();
        aa.setAlpha(1f);
        aa.setInterpolation(Interpolation.exp5);
        aa.setDuration(0.2f);

        ScaleToAction sta = new ScaleToAction();
        sta.setInterpolation(Interpolation.swingOut);
        sta.setDuration(0.5f);
        sta.setScale(1);

        AlphaAction bg = new AlphaAction();
        bg.setAlpha(0.6f);
        bg.setInterpolation(Interpolation.exp5);
        bg.setDuration(0.25f);
        background.addAction(bg);

        table.setScale(0.8f);

        table.addAction(aa);
        table.addAction(sta);

        int i = 0;
        for (ImageButton button : buttons) {
            button.setTransform(true);
            button.setScale(0.8f);
            button.setOrigin(Align.center);

            ScaleToAction staa = new ScaleToAction();
            staa.setScale(1f);
            staa.setDuration(0.2f);
            staa.setInterpolation(Interpolation.exp5Out);

            DelayAction da = new DelayAction((i + 1) * 0.05f);
            SequenceAction sa = new SequenceAction(da, staa);

            button.addAction(sa);

            i++;
        }
    }

    public void hide() {
        AlphaAction aa = new AlphaAction();
        aa.setAlpha(0f);
        aa.setInterpolation(Interpolation.exp5);
        aa.setDuration(0.2f);

        ScaleToAction sta = new ScaleToAction();
        sta.setInterpolation(Interpolation.exp5);
        sta.setDuration(0.2f);
        sta.setScale(0.8f);

        AlphaAction bg = new AlphaAction();
        bg.setAlpha(0f);
        bg.setInterpolation(Interpolation.exp5);
        bg.setDuration(0.25f);
        background.addAction(bg);

        table.setScale(1f);

        table.addAction(aa);
        table.addAction(sta);
    }

    private ImageButton makeButton(String upPath, String downPath) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        Texture up = new Texture(upPath);
        up.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.up = new TextureRegionDrawable(new TextureRegion(up));
        Texture down = new Texture(downPath);
        down.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.over = new TextureRegionDrawable(new TextureRegion(down));
        style.down = new TextureRegionDrawable(new TextureRegion(up));

        ImageButton button = new ImageButton(style);
        button.setOrigin(Align.center);
        button.setTransform(true);

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
        });

        return button;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        table.setPosition(getWidth() / 2 - (table.getWidth() / 2f), getHeight() / 2 - (table.getHeight() / 2f));

        for (Pair<Actor, SpringingContext1D> p : springs) {
            p.getValue().update(delta);
            p.getKey().setScale(p.getValue().value);
        }
    }
}
