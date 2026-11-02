package com.potioncraft.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Procedural Audio Synthesizer utilizing Java Sound API (javax.sound.sampled).
 * Generates crisp 8-bit / fantasy sound effects entirely in memory
 * with zero external asset dependencies.
 */
public class SoundManager {
    private static final int SAMPLE_RATE = 44100;
    private static final ExecutorService AUDIO_EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "AudioSynthThread");
        t.setDaemon(true);
        return t;
    });

    private static boolean soundEnabled = true;

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static void playCoinSound() {
        if (!soundEnabled) return;
        AUDIO_EXECUTOR.submit(() -> {
            try {
                // Bright high chime: 987Hz (B5) followed by 1318Hz (E6)
                byte[] b1 = generateTone(987, 80, 0.45);
                byte[] b2 = generateTone(1318, 140, 0.55);
                byte[] combo = concatenate(b1, b2);
                playBuffer(combo);
            } catch (Exception ignored) {}
        });
    }

    public static void playBubbleSound() {
        if (!soundEnabled) return;
        AUDIO_EXECUTOR.submit(() -> {
            try {
                // Rising pitch pop: 350Hz up to 750Hz
                int durationMs = 70;
                int totalSamples = (SAMPLE_RATE * durationMs) / 1000;
                byte[] buffer = new byte[totalSamples];
                for (int i = 0; i < totalSamples; i++) {
                    double progress = (double) i / totalSamples;
                    double freq = 350 + (400 * progress * progress);
                    double angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE;
                    double envelope = Math.sin(Math.PI * progress); // smooth bell curve
                    buffer[i] = (byte) (Math.sin(angle) * 110 * envelope);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        });
    }

    public static void playBrewSuccessSound() {
        if (!soundEnabled) return;
        AUDIO_EXECUTOR.submit(() -> {
            try {
                // Magic chord arpeggio: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
                byte[] t1 = generateTone(523, 70, 0.4);
                byte[] t2 = generateTone(659, 70, 0.4);
                byte[] t3 = generateTone(784, 70, 0.4);
                byte[] t4 = generateTone(1046, 180, 0.5);
                playBuffer(concatenate(t1, t2, t3, t4));
            } catch (Exception ignored) {}
        });
    }

    public static void playErrorSound() {
        if (!soundEnabled) return;
        AUDIO_EXECUTOR.submit(() -> {
            try {
                // Low buzz
                int durationMs = 150;
                int totalSamples = (SAMPLE_RATE * durationMs) / 1000;
                byte[] buffer = new byte[totalSamples];
                for (int i = 0; i < totalSamples; i++) {
                    double angle = 2.0 * Math.PI * 130 * i / SAMPLE_RATE;
                    double sample = Math.sin(angle) > 0 ? 0.4 : -0.4; // Square wave
                    double decay = 1.0 - ((double) i / totalSamples);
                    buffer[i] = (byte) (sample * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        });
    }

    public static void playAngrySound() {
        if (!soundEnabled) return;
        AUDIO_EXECUTOR.submit(() -> {
            try {
                // Descending grunt: 260Hz down to 100Hz
                int durationMs = 220;
                int totalSamples = (SAMPLE_RATE * durationMs) / 1000;
                byte[] buffer = new byte[totalSamples];
                for (int i = 0; i < totalSamples; i++) {
                    double progress = (double) i / totalSamples;
                    double freq = 260 - (160 * progress);
                    double angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE;
                    double decay = 1.0 - progress;
                    buffer[i] = (byte) (Math.sin(angle) * 110 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        });
    }

    public static void playGameOverSound() {
        if (!soundEnabled) return;
        AUDIO_EXECUTOR.submit(() -> {
            try {
                // Melancholy descending minor cadence
                byte[] t1 = generateTone(440, 160, 0.4); // A4
                byte[] t2 = generateTone(370, 180, 0.4); // F#4
                byte[] t3 = generateTone(330, 220, 0.4); // E4
                byte[] t4 = generateTone(220, 450, 0.5); // A3
                playBuffer(concatenate(t1, t2, t3, t4));
            } catch (Exception ignored) {}
        });
    }

    private static byte[] generateTone(double freq, int durationMs, double volume) {
        int totalSamples = (SAMPLE_RATE * durationMs) / 1000;
        byte[] buffer = new byte[totalSamples];
        for (int i = 0; i < totalSamples; i++) {
            double angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE;
            double decay = 1.0 - ((double) i / totalSamples); // Linear fade out
            buffer[i] = (byte) (Math.sin(angle) * 127 * volume * decay);
        }
        return buffer;
    }

    private static byte[] concatenate(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] a : arrays) totalLength += a.length;
        byte[] result = new byte[totalLength];
        int pos = 0;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    private static void playBuffer(byte[] buffer) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
            }
        } catch (Exception ignored) {
            // Audio output device not present or busy
        }
    }
}
