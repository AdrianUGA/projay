package saboteur.state;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerRoleContainer;

import java.io.IOException;

public class PlayerBeginOfTurnState extends State{

    @FXML private HBox goButtonContainer;
    @FXML private Text text;
	private PlayerRoleContainer playerRoleContainer;

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
    	GameCardContainer gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
    	gameCardContainer.hideCards();
    	
    	this.playerRoleContainer = (PlayerRoleContainer) this.primaryStage.getScene().lookup("#playerRoleContainer");
		this.playerRoleContainer.setPlayerRoleComponentsVisible(false);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalBeginOfTurn.fxml"));
            loader.setController(this);
            this.modalPane = loader.load();

            if (this.game.getTurn() == 1){
                this.text.setText("C'est au tour de " + this.game.getCurrentPlayer().getName() + " de jouer");
            } else{
                this.text.setText(this.game.getPreviousPlayer().getName() + " a fini de jouer. C'est au tour de " + this.game.getCurrentPlayer().getName() + " de jouer");
            }

            this.modalPane.setPrefHeight(primaryStage.getWidth());
            this.modalPane.setPrefWidth(primaryStage.getHeight());

            if(this.game.getCurrentPlayer().isAI()){
                this.goButtonContainer.setVisible(false);
                PauseTransition pt = new PauseTransition(Duration.millis(2000));
                pt.setOnFinished(event -> goButtonAction());
                pt.play();
            }

            StackPane root = (StackPane) primaryStage.getScene().getRoot();
            root.getChildren().add(this.modalPane);

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
