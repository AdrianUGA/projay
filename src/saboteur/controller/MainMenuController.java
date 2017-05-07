package saboteur.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private HBox quitGameButton;

    @FXML
    private Button noQuitGameBtn;

    @FXML
    private Button yesQuitGameBtn;

    public MainMenuController(Stage primaryStage){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/mainMenu.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        } catch (IOException e){
            e.printStackTrace();
        }
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
            loader.setLocation(App.class.getResource("/saboteur/view/modal.fxml"));
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
