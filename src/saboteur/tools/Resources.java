package saboteur.tools;

import javafx.scene.image.Image;
import saboteur.App;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Resources {

    private Path pathImageCardFolder;

    private HashMap<String, Image> imageCard;

	public Resources(){
        this.imageCard = new HashMap<String, Image>();
        try{
            this.pathImageCardFolder = Paths.get(App.class.getResource("/resources/cards/").toURI());
        } catch(Exception e){

        }
    }

    public void loadImage(){
        try{
            Files.walk(this.pathImageCardFolder)
                    .filter(Files::isRegularFile)
                    .forEach(path -> imageCard.put(path.getFileName().toString(), new Image("/resources/cards/"+path.getFileName().toString())));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public HashMap<String, Image> getImageCard() {
		return imageCard;
	}
    
}
