package saboteur.state;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Card.Card;
import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.OperationPick;
import saboteur.model.Team;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;

public class PlayerPlayCardState extends State{

	private GameCardContainer cardContainer;
	private PlayerArc playersArc;
	private GameBoardGridPane gameBoardGridPane;

	private Label playerRoleLabel;
	private ImageView playerRoleImage;

	private Button trashButton;
	private Button endOfTurnButton;

	private ImageView trash;
	private ImageView stack;


    public PlayerPlayCardState(GameStateMachine gsm, Game game, Stage primaryStage){
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
    	Operation o = (Operation) param;

    	this.cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
    	this.trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	this.playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");
    	this.trash = (ImageView) this.primaryStage.getScene().lookup("#trash");
    	this.stack = (ImageView) this.primaryStage.getScene().lookup("#stack");

		Button trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
		trashButton.setDisable(true);

		cardContainer.setOnMouseClicked(null);

		this.gameBoardGridPane.generateBoard();
		this.playersArc.refreshPlayersArcsAndCircles();

		//Operation card animation

    	//Pick card animation
		OperationPick op = (OperationPick) this.game.getCurrentPlayer().pickCard();

		if (this.game.getCurrentPlayer().isHuman()){
			cardContainer.showCards();
		}

		ScaleTransition st = new ScaleTransition(javafx.util.Duration.INDEFINITE.millis(500), this.stack);
		st.setByX(0.3f);
		st.setByY(0.3f);
		st.setCycleCount(2);
		st.setAutoReverse(true);
		st.setInterpolator(Interpolator.EASE_OUT);
		st.setOnFinished(event -> {
			this.gsm.pop();
		});
		st.play();
	}

    @Override
    public void onExit() {
    	this.game.nextPlayer();
    }
}
