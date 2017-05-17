package saboteur.state;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public class MainMenuState implements State {

    @FXML private Button noQuitGameBtn;

    @FXML private Button yesQuitGameBtn;
    
    @FXML private HBox quitGameButton;
    
    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public MainMenuState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
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
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
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
            	((ImageView)nodeIn).setVisible(true);
            }
        }
    }
    
    @FXML
    private void mouseOut(MouseEvent event){
    	HBox hb = (HBox)event.getSource();
    	for(Node nodeIn:hb.getChildren()){
            if(nodeIn instanceof ImageView){
            	((ImageView)nodeIn).setVisible(false);
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
        Stage stg = (Stage) this.quitGameButton.getScene().getWindow();
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(stg);
        stage.setTitle("modal");

       //add a modal box from builder (exit game confirmation)
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalQuitGame.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout,400, 400, Color.TRANSPARENT);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setX(stg.getWidth()-1150);
        stage.setY(stg.getHeight()-800);

        stage.show();
    }

    @FXML
    private void noQuitGameBtnAction(){
        Stage stgBtn = (Stage) this.noQuitGameBtn.getScene().getWindow();
        stgBtn.close();
    }
    @FXML
    private void yesQuitGameBtnAction(){
        Stage stgBtn = (Stage) this.yesQuitGameBtn.getScene().getWindow();
        ((Stage) stgBtn.getOwner()).close();
    }
}