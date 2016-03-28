package pw.ske.circanoid;

import com.badlogic.gdx.audio.Sound;

public class SoundEffect {
    private Sound sound;
    private float pitchDeviation;

    public SoundEffect(Sound sound, float pitchDeviation) {
        this.sound = sound;
        this.pitchDeviation = pitchDeviation;
    }

    public void play() {
        float pitch = 1.0f;
        pitch += (Math.random() * pitchDeviation) - pitchDeviation / 2;
        sound.play(1, pitch, 0);
    }
}
