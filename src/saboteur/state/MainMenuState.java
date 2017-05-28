package saboteur.state;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Resources;

public class MainMenuState extends State {

    @FXML private Button noQuitGameBtn;
    @FXML private Button yesQuitGameBtn;
    @FXML private HBox quitGameButton;

    private Pane confirmModalPane;
    private StackPane rootPane;

    public MainMenuState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/mainMenu.fxml"));
            loader.setController(this);
            Pane pane = loader.load();
            this.changeLayout(pane);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

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
    private void newGameButtonAction(){
    	this.gsm.change("newGameMenu");
    }
    
    @FXML
    private void loadGameButtonAction(){
    	this.gsm.change("loadGame");
    }
    
    @FXML
    private void optionsButtonAction() {
    	this.gsm.change("options");
	}
    
    @FXML
    private void scoreButtonAction() {
    	this.gsm.change("score");
	}
    
    @FXML
    private void helpButtonAction() {
    	this.gsm.change("help");
	}
    
    @FXML
    private void quitGameButtonAction(){
       //add a modal box from builder (exit game confirmation)
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalQuitGame.fxml"));
            loader.setController(this);
            this.confirmModalPane = loader.load();

            this.confirmModalPane.setPrefHeight(primaryStage.getHeight());
            this.confirmModalPane.setPrefWidth(primaryStage.getWidth());

            this.rootPane = (StackPane) primaryStage.getScene().getRoot();
            this.rootPane.getChildren().add(this.confirmModalPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void noQuitGameBtnAction(){
        this.rootPane.getChildren().remove(this.confirmModalPane);
    }
    @FXML
    private void yesQuitGameBtnAction(){
        this.primaryStage.close();
    }
}