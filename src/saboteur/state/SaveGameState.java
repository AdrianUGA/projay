package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Loader;

import java.io.IOException;
import java.util.ArrayList;

public class SaveGameState extends State {

    private StackPane rootPane;
    private Pane modalPane;
    private Pane previousPane;
    private Pane confirmModalPane;
    @FXML private TextField newGameName;
    @FXML private ListView<String> listFile;

    private ArrayList<String> saveFiles;

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
        this.previousPane = (Pane) param;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalSave.fxml"));
            loader.setController(this);
            this.modalPane = loader.load();

            this.modalPane.setPrefHeight(primaryStage.getHeight());
            this.modalPane.setPrefWidth(primaryStage.getWidth());

            listFile.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> newGameName.setText(newValue));

            Loader loaderSaves = new Loader();
            this.saveFiles = loaderSaves.loadSavedFile();
            for (String savedFile : this.saveFiles) {
            	String name[] = savedFile.split(".save");
                listFile.getItems().add(name[0]);
            }

            this.rootPane = (StackPane) primaryStage.getScene().getRoot();
            this.rootPane.getChildren().add(this.modalPane);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onExit() {
        this.rootPane.getChildren().remove(this.modalPane);
        this.rootPane.getChildren().add(this.previousPane);
    }

    @FXML
    private void cancelSaveModalButtonAction(){
        this.gsm.pop();
    }

    @FXML
    private void okSaveModalButtonAction(){
        String newSaveFile = this.newGameName.getText();
        boolean isExist = false;
        for (String saveFile: saveFiles) {
            String name[] = saveFile.split(".save");
            if (newSaveFile.equals(name[0])){
                isExist = true;
            }
        }
        if (isExist){
            this.confirmModal();
        } else{
            this.game.save(this.newGameName.getText());
            this.gsm.pop();
        }
    }

    private void confirmModal(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalSaveConfirm.fxml"));
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
        this.game.save(this.newGameName.getText());
        this.rootPane.getChildren().remove(this.confirmModalPane);
        this.gsm.pop();
    }
    @FXML
    private void noConfirmButtonAction(){
        this.rootPane.getChildren().remove(this.confirmModalPane);
    }
}
