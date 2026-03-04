error id: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/AudioStretch.java:FloatControl/Type#MASTER_GAIN#
file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/AudioStretch.java
empty definition using pc, found symbol in pc: FloatControl/Type#MASTER_GAIN#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3870
uri: file:///C:/Software%20Projects/Academics/CMSC%20125/cpu-scheduling/src/main/java/com/cpuScheduler/AudioStretch.java
text:
```scala
package com.cpuScheduler;

public package com.cpuScheduler;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Loads a WAV file and produces a stretched (slowed-down) Clip
 * by writing the original PCM frames at a lower sample rate.
 *
 * Lowering the sample rate tricks the audio system into playing
 * the same number of frames more slowly — pure Java, no libraries.
 *
 * Example: original 44100 Hz clip at 0.9 s.
 *   To stretch to 5 s  →  targetRate = 44100 * (0.9 / 5) ≈ 7938 Hz
 *   The clip still has the same bytes but plays ~5.5× slower.
 */
public class AudioStretch {

    /**
     * Load the WAV at the given resource path and return a Clip
     * stretched to the requested duration in seconds.
     *
     * Returns null if audio is unavailable or the format is unsupported.
     *
     * @param resourcePath  e.g. "/sounds/progressBar.wav"
     * @param targetSeconds desired playback duration in seconds
     */
    public static Clip createStretched(String resourcePath, double targetSeconds) {
        try {
            URL url = AudioStretch.class.getResource(resourcePath);
            if (url == null) return null;

            // --- 1. Read original audio ---
            AudioInputStream original = AudioSystem.getAudioInputStream(url);
            AudioFormat originalFormat = original.getFormat();

            // Convert to PCM_SIGNED if necessary (e.g. MP3, ULAW, etc.)
            AudioFormat pcmFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                originalFormat.getSampleRate(),
                16,
                originalFormat.getChannels(),
                originalFormat.getChannels() * 2,
                originalFormat.getSampleRate(),
                false
            );
            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFormat, original);

            // Read all PCM bytes
            byte[] pcmBytes = pcmStream.readAllBytes();
            pcmStream.close();

            // --- 2. Calculate original duration ---
            // frames = bytes / frameSize
            int frameSize = pcmFormat.getFrameSize();
            long totalFrames = pcmBytes.length / frameSize;
            double originalDuration = totalFrames / pcmFormat.getSampleRate(); // seconds

            if (originalDuration <= 0 || targetSeconds <= 0) return null;

            // --- 3. Calculate the stretched sample rate ---
            // We want the SAME bytes to play for targetSeconds.
            // new_rate = original_frames / targetSeconds
            float stretchedRate = (float)(totalFrames / targetSeconds);

            // Clamp to a reasonable range (most systems support 4000–192000 Hz)
            stretchedRate = Math.max(4000f, Math.min(192000f, stretchedRate));

            // --- 4. Build a new AudioFormat at the stretched rate ---
            AudioFormat stretchedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                stretchedRate,
                pcmFormat.getSampleBits(),
                pcmFormat.getChannels(),
                frameSize,
                stretchedRate,
                pcmFormat.isBigEndian()
            );

            // --- 5. Wrap bytes in a stream with the stretched format ---
            ByteArrayInputStream bais = new ByteArrayInputStream(pcmBytes);
            AudioInputStream stretchedStream = new AudioInputStream(
                bais, stretchedFormat, totalFrames
            );

            // --- 6. Open a Clip with this stream ---
            Clip clip = AudioSystem.getClip();
            clip.open(stretchedStream);

            // Slightly reduce volume
            if (clip.isControlSupported(FloatControl.Type.MAS@@TER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(Math.max(gain.getMinimum(), -6.0f));
            }

            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            return null; // degrade silently
        }
    }
} {
  
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: FloatControl/Type#MASTER_GAIN#