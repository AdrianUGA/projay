package saboteur.state;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
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

    private Stage modalStage;

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
        this.modalStage = new Stage();
        this.modalStage.initStyle(StageStyle.TRANSPARENT);

        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.initOwner(primaryStage);
        this.modalStage.setTitle("Fin de la partie");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalGameIsFinished.fxml"));
            loader.setController(this);
            Pane modalPane = loader.load();
            Scene scene = new Scene(modalPane, 900, primaryStage.getHeight(), Color.TRANSPARENT);

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

                this.resultRound.add(new Text(""+(i+1)), 0, i);
                this.resultRound.add(player.getPlayerIcon(), 1, i);
                this.resultRound.add(player.getPlayerName(), 2, i);
                this.resultRound.add(player.getPlayerGold(), 3, i);
            }
            scene.getStylesheets().add(Resources.getStylesheet());

            this.modalStage.setScene(scene);

            this.modalStage.setX(primaryStage.getWidth()/2d - 900/2d);
            this.modalStage.setY(0);

            this.modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onExit() {
        this.modalStage.close();
    }

    @FXML
    private void returnToMainMenuAction(){
	    this.gsm.change("mainMenu");
    }
}
