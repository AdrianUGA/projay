package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.tools.Resources;
import saboteur.view.ResultPlayer;

import java.io.IOException;
import java.util.LinkedList;

public class GameIsFinishedState extends State{

    @FXML private Text text;
    @FXML private GridPane resultRound;

    private Pane modalPane;

	public GameIsFinishedState(GameStateMachine gsm, Game game, Stage primaryStage){
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
            loader.setLocation(App.class.getResource("/saboteur/view/modalGameIsFinished.fxml"));
            loader.setController(this);
            this.modalPane = loader.load();

            String winnerText = "";
            LinkedList<Player> winners =  this.game.getWinners();
            if (winners.size() > 1){
                winnerText += "Les joueurs ";
                for (Player p : winners){
                    if (winners.indexOf(p) == winners.size()-2)
                        winnerText += p.getName() + " et ";
                    else if (winners.indexOf(p) == winners.size()-1)
                        winnerText += p.getName() + " ";
                    else
                        winnerText += p.getName() + ", ";
                }
                winnerText += "sont gagnants ex aequo ";
            } else {
                winnerText += winners.getFirst().getName() + " a gagné la partie ";
            }
            winnerText += "avec " + winners.getFirst().getGold() + " pépites d'or !";
            this.text.setText(winnerText);

            LinkedList<Player> ranking = this.game.getRanking();
            for (int i = 0; i < ranking.size(); i++) {
                ranking.get(i).getGold();
                ResultPlayer player = new ResultPlayer(ranking.get(i));

                resultRound.getRowConstraints().add(new RowConstraints(50, 50, 50, Priority.NEVER, VPos.CENTER, true));

                Text playerRank = new Text(""+(i+1));
                playerRank.setStyle("-fx-font-size: 1.8em; -fx-fill: white");
                this.resultRound.add(playerRank, 0, i);
                this.resultRound.add(player.getPlayerIcon(), 1, i);
                this.resultRound.add(player.getPlayerName(), 2, i);
                this.resultRound.add(player.getPlayerGold(), 3, i);
            }

            modalPane.setPrefHeight(primaryStage.getWidth());
            modalPane.setPrefWidth(primaryStage.getHeight());

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
    private void returnToMainMenuAction(){
	    this.gsm.change("mainMenu");
    }
}
