package pw.ske.circanoid;

public class BrickApplyEntry {
    private Brick brick;
    private float time;

    public BrickApplyEntry(Brick brick, float time) {
        this.brick = brick;
        this.time = time;
    }

    public Brick getBrick() {
        return brick;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
