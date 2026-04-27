package animation;

import java.awt.*;

public abstract class Animation {
    protected boolean playing;
    protected boolean finished;
    protected int currentFrame;
    protected int totalFrames;
    protected int frameDelay;
    protected long lastFrameTime;

    public Animation(int totalFrames, int frameDelay) {
        this.totalFrames = totalFrames;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.playing = false;
        this.finished = false;
        this.lastFrameTime = 0;
    }

    public void start() {
        this.playing = true;
        this.finished = false;
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void stop() {
        this.playing = false;
        this.finished = true;
    }

    public void reset() {
        this.currentFrame = 0;
        this.playing = false;
        this.finished = false;
    }

    public void update() {
        if (!playing || finished) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDelay) {
            currentFrame++;
            lastFrameTime = currentTime;

            if (currentFrame >= totalFrames) {
                finished = true;
                playing = false;
                onAnimationComplete();
            }
        }
    }

    public abstract void draw(Graphics2D g2d, int x, int y);

    protected void onAnimationComplete() {
    }

    public boolean isPlaying() { return playing; }
    public boolean isFinished() { return finished; }
    public int getCurrentFrame() { return currentFrame; }
    public float getProgress() { return (float)currentFrame / totalFrames; }
}
