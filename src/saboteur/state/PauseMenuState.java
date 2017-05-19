package saboteur.state;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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
            Scene scene = new Scene(modalPane, 900, primaryStage.getHeight(), Color.TRANSPARENT);

            String stylesheet = App.class.getResource("/resources/style.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);

            this.modalStage.setScene(scene);

            this.modalStage.setX(primaryStage.getWidth()/2d - 900/2d);
            this.modalStage.setY(0);

            this.modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {
        this.modalStage.close();
    }

    @FXML
    private void mouseOn(MouseEvent event){
        HBox hb = (HBox)event.getSource();
        for(Node nodeIn:hb.getChildren()){
            if(nodeIn instanceof ImageView){
                FadeTransition ft = new FadeTransition(Duration.millis(200), nodeIn);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            }
        }
    }

    @FXML
    private void mouseOut(MouseEvent event){
        HBox hb = (HBox)event.getSource();
        for(Node nodeIn:hb.getChildren()){
            if(nodeIn instanceof ImageView){
                FadeTransition ft = new FadeTransition(Duration.millis(500), nodeIn);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
            }
        }
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

    }

    @FXML
    private void mainMenuButtonAction(){
        this.gsm.change("mainMenu");
    }
}
