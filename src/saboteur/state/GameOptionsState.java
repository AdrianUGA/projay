package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;

import java.io.IOException;

public class GameOptionsState extends State {

    private Stage modalStage;

    public GameOptionsState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        this.modalStage = new Stage();
        this.modalStage.initStyle(StageStyle.TRANSPARENT);

        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.initOwner(primaryStage);
        this.modalStage.setTitle("modal");

        //add a modal box from builder (exit game confirmation)
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalOptions.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout,400, 400, Color.TRANSPARENT);
            this.modalStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.modalStage.setX(primaryStage.getWidth() - (primaryStage.getWidth()/2) - 200);
        this.modalStage.setY(primaryStage.getHeight() - (primaryStage.getHeight()/2) - 200);

        this.modalStage.show();
    }

    @Override
    public void onExit() {
        this.modalStage.close();
    }
    
    @FXML
    private void backButtonAction() {
    	this.gsm.change("mainMenu");
    }
}
