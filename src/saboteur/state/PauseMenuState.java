package saboteur.state;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;

import java.io.IOException;

public class PauseMenuState extends State {

    private Pane modalPane;
    private Pane confirmModalPane;
    private StackPane rootPane;

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
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalPauseMenu.fxml"));
            loader.setController(this);
            this.modalPane = loader.load();

            this.modalPane.setPrefHeight(primaryStage.getHeight());
            this.modalPane.setPrefWidth(primaryStage.getWidth());

            this.rootPane = (StackPane) primaryStage.getScene().getRoot();
            this.rootPane.getChildren().add(this.modalPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {
        this.rootPane.getChildren().remove(this.modalPane);
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
                FadeTransition ft = new FadeTransition(Duration.millis(200), nodeIn);
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
        this.rootPane.getChildren().remove(this.modalPane);
        this.gsm.push("saveGame", this.modalPane);
    }

    @FXML
    private void optionButtonAction(){

    }

    @FXML
    private void mainMenuButtonAction(){
        confirmReturnToMainMenu();
    }

    private void confirmReturnToMainMenu(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalReturnToMainMenuConfirm.fxml"));
            loader.setController(this);
            this.confirmModalPane = loader.load();

            this.confirmModalPane.setPrefHeight(primaryStage.getHeight());
            this.confirmModalPane.setPrefWidth(primaryStage.getWidth());

            this.rootPane.getChildren().add(this.confirmModalPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void yesConfirmButtonAction(){
        this.rootPane.getChildren().remove(this.confirmModalPane);
        this.gsm.change("mainMenu");
    }

    @FXML
    private void noConfirmButtonAction(){
        this.rootPane.getChildren().remove(this.confirmModalPane);
    }
}
