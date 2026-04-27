package util;

public class Timer {
    private long startTime;
    private long elapsedTime;
    private boolean running;
    private boolean paused;
    private long pauseStartTime;

    public Timer() {
        this.startTime = 0;
        this.elapsedTime = 0;
        this.running = false;
        this.paused = false;
        this.pauseStartTime = 0;
    }

    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
            paused = false;
        }
    }

    public void stop() {
        if (running) {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
            paused = false;
        }
    }

    public void reset() {
        startTime = 0;
        elapsedTime = 0;
        running = false;
        paused = false;
    }

    public void pause() {
        if (running && !paused) {
            pauseStartTime = System.currentTimeMillis();
            paused = true;
        }
    }

    public void resume() {
        if (running && paused) {
            long pauseDuration = System.currentTimeMillis() - pauseStartTime;
            startTime += pauseDuration;
            paused = false;
        }
    }

    public long getElapsed() {
        if (!running) {
            return elapsedTime;
        }

        if (paused) {
            return elapsedTime + (pauseStartTime - startTime);
        }

        return elapsedTime + (System.currentTimeMillis() - startTime);
    }

    public long getElapsedSeconds() {
        return getElapsed() / 1000;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean hasElapsed(long milliseconds) {
        return getElapsed() >= milliseconds;
    }
}
