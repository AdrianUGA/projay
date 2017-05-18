package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;

import java.io.IOException;

public class SaveGameState extends State {

    private Stage modalStage;
    private Stage parentStage;

    public SaveGameState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        this.parentStage = (Stage) param;
        this.modalStage = new Stage();
        this.modalStage.initStyle(StageStyle.TRANSPARENT);

        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.initOwner(this.primaryStage);
        this.modalStage.setTitle("modal");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalSave.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout, 400, 500, Color.TRANSPARENT);
            this.modalStage.setScene(scene);

            this.modalStage.setX(primaryStage.getWidth()/2d - 400/2d);
            this.modalStage.setY(primaryStage.getHeight()/2d - 500/2d);

            this.modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onExit() {
        this.modalStage.close();
        this.parentStage.show();
    }

    @FXML
    private void cancelSaveModalButtonAction(){
        this.gsm.pop();
    }

    @FXML
    private void okSaveModalButtonAction(){

    }
}
