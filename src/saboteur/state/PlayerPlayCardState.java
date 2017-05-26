package saboteur.state;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import saboteur.GameStateMachine;
import saboteur.model.Card.Card;
import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.OperationPick;
import saboteur.model.Team;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;
import saboteur.view.TrashAndPickStackContainer;

public class PlayerPlayCardState extends State{

	private GameCardContainer cardContainer;
	private PlayerArc playersArc;
	private GameBoardGridPane gameBoardGridPane;
	private TrashAndPickStackContainer trashAndPickStackContainer;

	private Label playerRoleLabel;
	private ImageView playerRoleImage;

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
    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
    	this.playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	this.playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");
    	this.trash = (ImageView) this.primaryStage.getScene().lookup("#trash");
    	this.stack = (ImageView) this.primaryStage.getScene().lookup("#stack");

		this.trashAndPickStackContainer.disableTrashButton();

		cardContainer.setOnMouseClicked(null);

		this.cardContainer.removeSelection();

		this.gameBoardGridPane.generateBoard();
		this.playersArc.refreshPlayersArcsAndCircles();

		//Operation card animation

    	//Pick card animation
		OperationPick op = (OperationPick) this.game.getCurrentPlayer().pickCard();

		if (this.game.getCurrentPlayer().isHuman()){
			cardContainer.showCards();
		}

//		ScaleTransition st = new ScaleTransition(Duration.millis(400), this.stack);
//		st.setByX(0.3f);
//		st.setByY(0.3f);
//		st.setCycleCount(2);
//		st.setAutoReverse(true);
//		st.setInterpolator(Interpolator.EASE_IN);
//		st.play();
//		ImageView cloneStack = new ImageView(this.stack.getImage());
//		cloneStack.setLayoutX(this.stack.getLayoutX());
//		cloneStack.setLayoutY(this.stack.getLayoutY());
//		((Pane)this.stack.getParent()).getChildren().add(cloneStack);
//		TranslateTransition tt = new TranslateTransition(Duration.millis(400), cloneStack);
//		tt.setFromX(0);
//		tt.setFromY(0);
//		tt.setToX(-500);
//
//		RotateTransition rt = new RotateTransition(Duration.millis(600), cloneStack);
//		rt.setAxis(Rotate.Y_AXIS);
//		rt.setFromAngle(0);
//		rt.setToAngle(360);
//		rt.setCycleCount(3);
//		rt.setInterpolator(Interpolator.LINEAR);
//
//		ParallelTransition pt = new ParallelTransition(rt, tt);
//		pt.setOnFinished(event -> {
//			((Pane)this.stack.getParent()).getChildren().remove(cloneStack);
//			this.gsm.pop();
//		});
//		pt.play();
		this.gsm.pop();
	}

    @Override
    public void onExit() {
    	this.game.nextPlayer();
    }
}
