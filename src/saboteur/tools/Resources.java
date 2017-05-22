package saboteur.tools;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import saboteur.App;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Resources {

    private final static String pictoFolder = "/resources/picto/";
    private final static String ImageCardFolder = "/resources/cards/";
    private final static String musicFolder = "/resources/music/";

    private Path pathImageCardFolder;
    private Path pathPictoFolder;

    private static int j = 0;
    private static int k = 0;
    public static  double volume=0.5;

    private LinkedHashMap<String, Image> imageCard;

    private LinkedHashMap<String, Image> picto;

    private static ArrayList<MediaPlayer> music;

    public Resources() {
        this.imageCard = new LinkedHashMap<>();
        this.picto = new LinkedHashMap<>();
        try {
            this.pathImageCardFolder = Paths.get(App.class.getResource(ImageCardFolder).toURI());
            this.pathPictoFolder = Paths.get(App.class.getResource(pictoFolder).toURI());
        } catch (Exception e) {

        }
    }

    public void loadImage() {
        try {
            Files.walk(this.pathImageCardFolder)
                    .filter(Files::isRegularFile)
                    .forEach(path -> imageCard.put(path.getFileName().toString(), new Image(ImageCardFolder + path.getFileName().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPicto() {
        try {
            Files.walk(this.pathPictoFolder)
                    .filter(Files::isRegularFile)
                    .forEach(path -> picto.put(path.getFileName().toString(), new Image(pictoFolder + path.getFileName().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String, Image> getImageCard() {
        return imageCard;
    }

    public static MediaPlayer loadMusic() {
        if (music == null) {
            music = new ArrayList<>();
            try {
                Path pathMusicFolder = Paths.get(App.class.getResource(musicFolder).toURI());
                Files.walk(pathMusicFolder)
                        .filter(Files::isRegularFile)
                        .forEach(path -> music.add(new MediaPlayer(new Media((path.toUri().toString())))));

                for (int i = 0; i < music.size(); i++) {
                    music.get(i).setOnEndOfMedia(new Runnable() {
                        @Override
                        public void run() {
                            music.get(j).stop();
                            j= (j+1) % (music.size()-1);
                            music.get(j).play();
                        }
                    });
                    music.get(i).setOnPlaying(new Runnable() {
                        @Override
                        public void run() {
                            music.get(k).setVolume(volume);
                            k++;
                        }
                    });
                }

                return music.get(0);
                // mp.setVolume(0.5);
                //mp.setCycleCount(MediaPlayer.INDEFINITE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (MediaPlayer mp : music) {
            if (mp.getStatus() == MediaPlayer.Status.PLAYING) {
                System.out.println(music.indexOf(mp));
                return mp;
            }
        }
        return null;
    }
}
