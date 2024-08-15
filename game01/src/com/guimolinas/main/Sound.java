package com.guimolinas.main;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {

    public static class Clips {
        public Clip[] clips;
        private int p;
        private int count;

        public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
            if (buffer == null) {
                return;
            }

            clips = new Clip[count];
            this.count = count;

            for (int i = 0; i < count; i++) {
                clips[i] = AudioSystem.getClip();
                clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
            }
        }

        // Tocar o áudio apenas uma vez
        public void play() {
            if (clips == null) {
                return;
            }

            clips[p].stop();
            clips[p].setFramePosition(0);
            clips[p].start();
            p++;

            if (p >= count) {
                p = 0;
            }
        }

        public void loop() {
            if (clips == null) {
                return;
            }

            clips[p].loop(300);
        }

        // Ajustar o volume
        public void setVolume(float volume) {
            if (clips == null) {
                return;
            }

            for (Clip clip : clips) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);
            }
        }
    }

    public static Clips background = load("/background.wav", 1);
    public static Clips hurt = load("/hurt.wav", 2);
    public static Clips selection = load("/check.wav", 3);
    public static Clips heal = load("/heal.wav", 4);
    public static Clips damage = load("/damage.wav", 5);
    public static Clips ammo = load("/ammo.wav", 6);
    public static Clips kill = load("/kill.wav", 7);
    public static Clips jump = load("/jump.wav", 8);

    private static Clips load(String name, int count) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));

            byte[] buffer = new byte[1024];
            int read = 0;

            while ((read = dis.read(buffer)) >= 0) {
                baos.write(buffer, 0, read);
            }

            dis.close();

            byte[] data = baos.toByteArray();

            return new Clips(data, count);
        } catch (Exception e) {
            try {
                return new Clips(null, 0);
            } catch (Exception ee) {
                return null;
            }
        }
    }
}
