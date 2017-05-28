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
import saboteur.model.*;
import saboteur.model.Card.Card;
import saboteur.tools.Resources;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;
import saboteur.view.TrashAndPickStackContainer;

import javax.swing.*;

public class PlayerPlayCardState extends State{

	private Pane mainContainer;
	private GameCardContainer cardContainer;
	private PlayerArc playersArc;
	private GameBoardGridPane gameBoardGridPane;
	private TrashAndPickStackContainer trashAndPickStackContainer;

	private Label playerRoleLabel;
	private ImageView playerRoleImage;

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

    	this.mainContainer = (Pane)this.primaryStage.getScene().lookup("#mainContainer");
    	this.cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
    	this.playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	this.playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");

		this.trashAndPickStackContainer.disableTrashButton();
		this.trashAndPickStackContainer.setEmptyTrash(this.game.trashIsEmpty());

		cardContainer.setOnMouseClicked(null);

		this.cardContainer.removeSelection();

		this.gameBoardGridPane.generateBoard();
		this.playersArc.refreshPlayersArcsAndCircles();

		EventHandler<ActionEvent> onFinished = event -> {
			this.pickCard();
		};

		System.out.println(o);
		//Operation card animation
		if (o.isOperationPathCard()){
			OperationPathCard op = (OperationPathCard) o;
			this.gameBoardGridPane.animatePathCard(op.getP(), onFinished);
		} else if (o.isOperationTrash()){
			OperationTrash op = (OperationTrash) o;
//			this.cardContainer.animateCardToTrash(op.getIndexOfCardInHandPlayer(), onFinished);
		} else if (o.isOperationActionCardToPlayer()){
			OperationActionCardToPlayer op = (OperationActionCardToPlayer) o;
			this.playersArc.animateCircle(op.getDestinationPlayer(), op.getToolDestination(), onFinished);
		} else if (o.isOperationActionCardToBoard()){
			OperationActionCardToBoard op = (OperationActionCardToBoard) o;
			if (op.getCard().isCollapseCard()){
				this.gameBoardGridPane.animateCollapseCard(op.getDestinationCard(), op.getPositionDestination(), onFinished);
			} else{
				this.gameBoardGridPane.animateGoalCard(op.getPositionDestination(), onFinished);
			}
		}

		this.trashAndPickStackContainer.updateStackText(this.game.getNumberOfCardInStack());
		this.trashAndPickStackContainer.setEmptyStack(this.game.stackIsEmpty());
	}

    @Override
    public void onExit() {
    	this.game.nextPlayer();
    }

    private void pickCard(){
		OperationPick op = (OperationPick) this.game.getCurrentPlayer().pickCard();
		//Pick card animation
		if (op != null){
			ImageView clone = this.trashAndPickStackContainer.getCloneOfCard();
			EventHandler<ActionEvent> onFinished = event -> {
				if (this.game.getCurrentPlayer().isHuman()){
					cardContainer.showCards();
					clone.setImage(Resources.getImage().get(op.getCardPicked().getFrontImage()));
				}
				PauseTransition pt = new PauseTransition(Duration.millis(2000));
				pt.setOnFinished(event1 -> {
					((Pane)clone.getParent()).getChildren().remove(clone);
					gsm.pop();
				});
				pt.play();
			};
			this.trashAndPickStackContainer.animateStack(clone, onFinished);
		} else{
			gsm.pop();
		}
	}
}
