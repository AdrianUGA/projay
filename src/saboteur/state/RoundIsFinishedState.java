package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.view.ResultPlayer;

import java.io.IOException;
import java.util.LinkedList;

public class RoundIsFinishedState extends State{

    @FXML private Text text;
    @FXML private Text roundText;
    @FXML private GridPane resultRound;

    private Pane modalPane;

	public RoundIsFinishedState(GameStateMachine gsm, Game game, Stage primaryStage){
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
            loader.setLocation(App.class.getResource("/saboteur/view/modalRoundIsFinished.fxml"));
            loader.setController(this);
            this.modalPane = loader.load();

            if (this.game.dwarfsWon()){
                this.text.setText("Les chercheurs d'or ont gagnés !!");
            } else{
                this.text.setText("Les saboteurs ont gagnés !!");
            }

            this.roundText.setText("Manche " + this.game.getRound() + " terminée.");

            LinkedList<Player> players = this.game.getPlayerList();
            for (int i = 0; i < players.size(); i++) {
                ResultPlayer player = new ResultPlayer(players.get(i));
                resultRound.getRowConstraints().add(new RowConstraints(50, 50, 50, Priority.NEVER, VPos.CENTER, true));
                this.resultRound.add(player.getPlayerIcon(), 0, i);
                this.resultRound.add(player.getPlayerName(), 1, i);
                this.resultRound.add(player.getPlayerRole(), 2, i);
            }

            this.modalPane.setPrefHeight(primaryStage.getHeight());
            this.modalPane.setPrefWidth(primaryStage.getWidth());

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
        if (this.game.gameIsFinished()){
            this.gsm.push("gameIsFinished");
        } else{
            this.game.newRound();
        }
    }

    @FXML
    private void nextRoundAction(){
	    this.gsm.pop();
    }
}
