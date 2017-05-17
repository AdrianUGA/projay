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

public class PauseMenuState extends State {

    private Stage modalStage;

    public PauseMenuState(GameStateMachine gsm, Game game, Stage primaryStage){
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

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalPauseMenu.fxml"));
            loader.setController(this);
            Pane modalPane = loader.load();
            Scene scene = new Scene(modalPane, Color.TRANSPARENT);
            this.modalStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.modalStage.setX(primaryStage.getWidth()/2 - 200);
        this.modalStage.setY(primaryStage.getHeight()/2 - 200);

        this.modalStage.show();
    }

    @Override
    public void onExit() {
        this.modalStage.close();
    }

    @FXML
    private void resumeButtonAction(){
        this.gsm.pop();
    }

    @FXML
    private void saveButtonAction(){
        this.modalStage.close();
        this.gsm.push("saveGame", this.modalStage);
    }

    @FXML
    private void optionButtonAction(){
        this.gsm.push("saveGame");
    }

    @FXML
    private void mainMenuButtonAction(){
        this.gsm.change("mainMenu");
    }
}
