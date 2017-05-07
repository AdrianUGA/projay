package saboteur.tool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;

import java.io.IOException;

public abstract class ModalBox {

    private Stage stage;

    public ModalBox(Stage parentStage, String pathToFxml){
        this.stage = new Stage();
        this.stage.initStyle(StageStyle.TRANSPARENT);

        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.initOwner(parentStage);
        this.stage.setTitle("modal");

        //add a modal box from builder (exit game confirmation)
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(pathToFxml));
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout,400, 400, Color.TRANSPARENT);
            this.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.stage.setX(parentStage.getWidth()-1150);
        this.stage.setY(parentStage.getHeight()-800);
    }

    public void show(){
        this.stage.show();
    }
}
