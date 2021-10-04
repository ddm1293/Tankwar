package tankwar;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Tools {

    public static Image getImage(String imageName) {
        return new ImageIcon("assets/images/" + imageName).getImage();
    }

    // make a sound based on the given string source of the sound
    public static void playSound(String audioFile) {
        Media sound = new Media(new File(audioFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}
