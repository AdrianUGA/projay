package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

public class PlayerEndOfTurnState extends State{

    private Stage modalStage;

	public PlayerEndOfTurnState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        this.modalStage.setTitle("Fin de tour");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalEndOfTurn.fxml"));
            loader.setController(this);
            Pane modalPane = loader.load();
            Scene scene = new Scene(modalPane, 500, 300, Color.TRANSPARENT);

            Text text = (Text)modalPane.lookup("#text");
            text.setText(this.game.getCurrentPlayer().getName() + " a fini de jouer. c'est au tour de " + this.game.getNextPlayer().getName() + " de jouer");

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
	    this.gsm.pop();
    }
}
