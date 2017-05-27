package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Resources;
import saboteur.view.GameCardContainer;

import java.io.IOException;

public class PlayerBeginOfTurnState extends State{

    private Stage modalStage;

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
            Pane modalPane = loader.load();
            Scene scene = new Scene(modalPane, 500, 300, Color.TRANSPARENT);

            Text text = (Text)modalPane.lookup("#text");
            if (this.game.getTurn() == 1){
                text.setText("c'est au tour de " + this.game.getNextPlayer().getName() + " de jouer");
            } else{
                text.setText(this.game.getCurrentPlayer().getName() + " a fini de jouer. c'est au tour de " + this.game.getNextPlayer().getName() + " de jouer");
            }

            scene.getStylesheets().add(Resources.getStylesheet());

            this.modalStage.setScene(scene);

            this.modalStage.setX(primaryStage.getWidth()/2 - 500/2);
            this.modalStage.setY(primaryStage.getHeight()/2 - 300/2);

            this.modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onExit() {
        this.modalStage.close();
        this.game.nextPlayer();
    }

    @FXML
    private void goButtonAction(){
	    this.gsm.changePeek("playerWait");
    }
}
