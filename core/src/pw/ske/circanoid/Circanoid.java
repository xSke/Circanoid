package pw.ske.circanoid;

import com.badlogic.gdx.Game;

public class Circanoid extends Game {
    public static Circanoid i;

    @Override
    public void create() {
        i = this;
        setScreen(new MainMenuScreen());
    }
}
