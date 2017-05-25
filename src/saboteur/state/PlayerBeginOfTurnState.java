package saboteur.state;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.view.GameCardContainer;

import java.io.IOException;

public class PlayerBeginOfTurnState extends State{

    @FXML private HBox goButtonContainer;

    private Stage modalStage;
    private Pane modalPane;

	public PlayerBeginOfTurnState(GameStateMachine gsm, Game game, Stage primaryStage){
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
    	GameCardContainer cardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#cardContainer");
    	cardContainer.hideCards();

    	Label playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	ImageView playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");
    	playerRoleLabel.setVisible(false);
    	playerRoleImage.setVisible(false);
    	
        this.modalStage = new Stage();
        this.modalStage.initStyle(StageStyle.TRANSPARENT);

        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.initOwner(primaryStage);
        this.modalStage.setTitle("Fin de tour");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalBeginOfTurn.fxml"));
            loader.setController(this);
            this.modalPane = loader.load();

            Text text = (Text)modalPane.lookup("#text");
            if (this.game.getTurn() == 1){
                text.setText("C'est au tour de " + this.game.getCurrentPlayer().getName() + " de jouer");
            } else{
                text.setText(this.game.getPreviousPlayer().getName() + " a fini de jouer. C'est au tour de " + this.game.getCurrentPlayer().getName() + " de jouer");
            }

            modalPane.setPrefHeight(300);
            modalPane.setPrefWidth(500);

            modalPane.setLayoutX(primaryStage.getWidth()/2 - 500/2);
            modalPane.setLayoutY(primaryStage.getHeight()/2 - 300/2);

            StackPane root = (StackPane) primaryStage.getScene().getRoot();

            if(this.game.getCurrentPlayer().isAI()){
                this.goButtonContainer.setVisible(false);
                PauseTransition pt = new PauseTransition(Duration.INDEFINITE.millis(2000));
                pt.setOnFinished(event -> {
                    goButtonAction();
                });
                pt.play();
            }

            root.getChildren().add(modalPane);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onExit() {
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().remove(this.modalPane);
    }

    @FXML
    private void goButtonAction(){
	    this.gsm.changePeek("playerWait");
    }
}
