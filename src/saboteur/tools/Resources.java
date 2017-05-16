package saboteur.tools;

import javafx.scene.image.Image;
import saboteur.App;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Resources {

    private final static String pictoFolder = "/resources/picto/";
    private final static String ImageCardFolder = "/resources/cards/";

    private Path pathImageCardFolder;
    private Path pathPictoFolder;

    private HashMap<String, Image> imageCard;

    private HashMap<String, Image> picto;

	public Resources(){
        this.imageCard = new HashMap<>();
        this.picto = new HashMap<>();
        try{
            this.pathImageCardFolder = Paths.get(App.class.getResource(ImageCardFolder).toURI());
            this.pathPictoFolder = Paths.get(App.class.getResource(pictoFolder).toURI());
        } catch(Exception e){

        }
    }

    public void loadImage(){
        try{
            Files.walk(this.pathImageCardFolder)
                    .filter(Files::isRegularFile)
                    .forEach(path -> imageCard.put(path.getFileName().toString(), new Image(ImageCardFolder + path.getFileName().toString())));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadPicto(){
        try{
            Files.walk(this.pathPictoFolder)
                    .filter(Files::isRegularFile)
                    .forEach(path -> picto.put(path.getFileName().toString(), new Image(pictoFolder + path.getFileName().toString())));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public HashMap<String, Image> getImageCard() {
		return imageCard;
	}
    
}
