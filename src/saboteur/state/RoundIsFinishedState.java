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

public class RoundIsFinishedState extends State{

    @FXML private Text text;
    @FXML private GridPane resultRound;

    private Stage modalStage;

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
        this.modalStage = new Stage();
        this.modalStage.initStyle(StageStyle.TRANSPARENT);

        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.initOwner(primaryStage);
        this.modalStage.setTitle("Fin de la manche");

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/modalRoundIsFinished.fxml"));
            loader.setController(this);
            Pane modalPane = loader.load();
            Scene scene = new Scene(modalPane, 900, primaryStage.getHeight(), Color.TRANSPARENT);

            if (this.game.dwarfsWon()){
                this.text.setText("Les chercheurs d'or ont gagnés !!");
            } else{
                this.text.setText("Les saboteurs ont gagnés !!");
            }

            LinkedList<Player> players = this.game.getPlayerList();
            for (int i = 0; i < players.size(); i++) {
                ResultPlayer player = new ResultPlayer(players.get(i));
                resultRound.getRowConstraints().add(new RowConstraints(50, 50, 50, Priority.NEVER, VPos.CENTER, true));
                this.resultRound.add(player.getPlayerIcon(), 0, i);
                this.resultRound.add(player.getPlayerName(), 1, i);
                this.resultRound.add(player.getPlayerRole(), 2, i);
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
