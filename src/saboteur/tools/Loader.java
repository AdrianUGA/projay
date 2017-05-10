package saboteur.tools;


import javafx.scene.image.Image;
import org.json.JSONObject;
import saboteur.App;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Loader {

    private Path pathBddFile;

    public Loader(){
        try{
            this.pathBddFile = Paths.get(App.class.getResource("/resources/bdd.json").toURI());
        } catch(Exception e){

        }
    }

    public void loadCard(){
        try{
            String text = new String(Files.readAllBytes(this.pathBddFile), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(text);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}
