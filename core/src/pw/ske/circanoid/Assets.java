package pw.ske.circanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Assets {
    public static Music dududududu = Gdx.audio.newMusic(Gdx.files.internal("dududududu.ogg"));

    public static SoundEffect blip = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("blip.wav")), 0.05f);
    public static SoundEffect boom = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("boom.wav")), 0.05f);
    public static SoundEffect deal = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("deal.wav")), 0.2f);
    public static SoundEffect ow = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("ow.wav")), 0.2f);
    public static SoundEffect swoopIn = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("swoop_in.wav")), 0f);
    public static SoundEffect swoopOut = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("swoop_out.wav")), 0f);
    public static SoundEffect thump = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("thump.wav")), 0.5f);
    public static SoundEffect win = new SoundEffect(Gdx.audio.newSound(Gdx.files.internal("win.wav")), 0f);
}
