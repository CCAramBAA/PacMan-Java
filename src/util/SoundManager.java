package util;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> soundClips;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private float masterVolume;
    private Clip backgroundMusic;
    private long lastEatDotTime;
    private static final long EAT_DOT_INTERVAL = 150;

    public SoundManager() {
        this.soundClips = new HashMap<>();
        this.soundEnabled = true;
        this.musicEnabled = true;
        this.masterVolume = 0.7f;
        this.lastEatDotTime = 0;
        loadSounds();
    }

    private void loadSounds() {
        try {
            File eatDotFile = new File("src/resources/sounds/pacman_chomp.wav");
            if (eatDotFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(eatDotFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume);
                soundClips.put("eat_dot", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载音效: eat_dot");
        }

        try {
            File eatPowerFile = new File("src/resources/sounds/pacman_chomp.wav");
            if (eatPowerFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(eatPowerFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume);
                soundClips.put("eat_power", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载音效: eat_power");
        }

        try {
            File eatGhostFile = new File("src/resources/sounds/pacman_eatghost.wav");
            if (eatGhostFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(eatGhostFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume);
                soundClips.put("eat_ghost", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载音效: eat_ghost");
        }

        try {
            File eatFruitFile = new File("src/resources/sounds/pacman_eatfruit.wav");
            if (eatFruitFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(eatFruitFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume);
                soundClips.put("eat_fruit", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载音效: eat_fruit");
        }

        try {
            File deathFile = new File("src/resources/sounds/pacman_death.wav");
            if (deathFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(deathFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume);
                soundClips.put("death", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载音效: death");
        }

        try {
            File bgmFile = new File("src/resources/sounds/pacman_beginning.wav");
            if (bgmFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bgmFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume * 0.3f);
                soundClips.put("background_music", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载背景音乐");
        }

        try {
            File levelCompleteFile = new File("src/resources/sounds/pacman_extrapac.wav");
            if (levelCompleteFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(levelCompleteFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                setVolume(clip, masterVolume);
                soundClips.put("level_complete", clip);
            }
        } catch (Exception e) {
            System.out.println("无法加载音效: level_complete");
        }
    }

    public void playSound(String name) {
        if (!soundEnabled) return;

        if (name.equals("eat_dot")) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEatDotTime < EAT_DOT_INTERVAL) {
                return;
            }
            lastEatDotTime = currentTime;
        }

        Clip clip = soundClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void playBackgroundMusic() {
        if (!musicEnabled || !soundEnabled) return;

        stopBackgroundMusic();

        Clip clip = soundClips.get("background_music");
        if (clip != null) {
            backgroundMusic = clip;
            clip.setFramePosition(0);
            setVolume(clip, masterVolume * 0.3f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            if (backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }
            backgroundMusic.setFramePosition(0);
            backgroundMusic = null;
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));

        for (Clip clip : soundClips.values()) {
            setVolume(clip, masterVolume);
        }

        if (backgroundMusic != null) {
            setVolume(backgroundMusic, masterVolume * 0.3f);
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;

        if (!enabled) {
            stopBackgroundMusic();
            for (Clip clip : soundClips.values()) {
                if (clip.isRunning()) {
                    clip.stop();
                }
            }
        } else if (musicEnabled && backgroundMusic == null) {
            playBackgroundMusic();
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;

        if (!enabled) {
            stopBackgroundMusic();
        } else if (soundEnabled && backgroundMusic == null) {
            playBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    private void setVolume(Clip clip, float volume) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}
