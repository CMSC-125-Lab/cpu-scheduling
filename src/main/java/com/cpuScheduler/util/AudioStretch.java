package com.cpuScheduler.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Loads a WAV file and produces a time-stretched Clip by resampling.
 *
 * Lowering the declared sample rate tricks the audio system into playing
 * the same PCM frames more slowly — pure Java, no external libraries.
 *
 * Example: original 44100 Hz clip, 0.9 s long (≈39690 frames).
 *   To stretch to 5 s → stretchedRate = 39690 / 5 ≈ 7938 Hz
 *   Same bytes, declared at 7938 Hz → plays 5× slower.
 */
public class AudioStretch {

    /**
     * Load the WAV at the given classpath resource path and return a Clip
     * whose playback duration matches targetSeconds.
     *
     * Returns null silently if audio is unavailable or the format is unsupported.
     *
     * @param resourcePath  e.g. "/sounds/progressBar.wav"
     * @param targetSeconds desired playback duration in seconds
     */
    public static Clip createStretched(String resourcePath, double targetSeconds) {
        try {
            URL url = AudioStretch.class.getResource(resourcePath);
            if (url == null) return null;

            // 1. Read original audio and decode to PCM_SIGNED 16-bit
            AudioInputStream original = AudioSystem.getAudioInputStream(url);
            AudioFormat originalFormat = original.getFormat();

            AudioFormat pcmFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalFormat.getSampleRate(),
                16,                                        // 16-bit samples
                originalFormat.getChannels(),
                originalFormat.getChannels() * 2,          // frameSize = channels * 2 bytes
                originalFormat.getSampleRate(),
                false                                      // little-endian
            );

            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFormat, original);
            byte[] pcmBytes = pcmStream.readAllBytes();
            pcmStream.close();

            // 2. Calculate total frames and original duration
            int  frameSize    = pcmFormat.getFrameSize();   // bytes per frame
            long totalFrames  = pcmBytes.length / frameSize;
            if (totalFrames <= 0 || targetSeconds <= 0) return null;

            // 3. Calculate stretched sample rate
            //    stretchedRate = totalFrames / targetSeconds
            //    Playing `totalFrames` frames at this rate takes exactly targetSeconds.
            float stretchedRate = (float)(totalFrames / targetSeconds);

            // Clamp to range most audio systems support
            stretchedRate = Math.max(4000f, Math.min(192000f, stretchedRate));

            // 4. Build new AudioFormat with the stretched rate
            AudioFormat stretchedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                stretchedRate,
                pcmFormat.getSampleSizeInBits(),   // ← correct method name
                pcmFormat.getChannels(),
                frameSize,
                stretchedRate,
                pcmFormat.isBigEndian()
            );

            // 5. Wrap raw bytes in a stream using the stretched format
            ByteArrayInputStream bais = new ByteArrayInputStream(pcmBytes);
            AudioInputStream stretchedStream = new AudioInputStream(bais, stretchedFormat, totalFrames);

            // 6. Open and return the Clip
            Clip clip = AudioSystem.getClip();
            clip.open(stretchedStream);
            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            return null; // degrade silently — no audio is not a fatal error
        }
    }
}