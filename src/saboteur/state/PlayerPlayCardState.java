package saboteur.state;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import saboteur.GameStateMachine;
import saboteur.model.*;
import saboteur.tools.Resources;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;
import saboteur.view.TrashAndPickStackContainer;

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

		this.cardContainer.setOnMouseClicked(null);
		this.cardContainer.removeSelection();

		this.gameBoardGridPane.generateBoard();
		this.playersArc.refreshPlayersArcsAndCircles();

		EventHandler<ActionEvent> pickCardEvent = event -> this.pickCard();

		EventHandler<ActionEvent> operationAnimationEvent = event -> this.operationAnimation(o, pickCardEvent);


		if (o.isOperationTrash()){
			OperationTrash op = (OperationTrash) o;
			this.cardContainer.animateCardToTrash(op.getIndexOfCardInHandPlayer(), pickCardEvent);
		} else{
			this.cardContainer.animateCard(o.getCard(), o.getIndexOfCardInHandPlayer(), operationAnimationEvent);
		}
	}

    @Override
    public void onExit() {
    	this.game.nextPlayer();
    }

    private void pickCard(){
		OperationPick op = (OperationPick) this.game.getCurrentPlayer().pickCard();
		this.trashAndPickStackContainer.updateStackText(this.game.getNumberOfCardInStack());
		this.trashAndPickStackContainer.setEmptyStack(this.game.stackIsEmpty());
		//Pick card animation
		if (op != null){
			ImageView clone = this.trashAndPickStackContainer.getCloneOfCard();
			EventHandler<ActionEvent> onFinished = event -> {
				if (this.game.getCurrentPlayer().isHuman()){
					cardContainer.showCards();
					clone.setImage(Resources.getImage().get(op.getCardPicked().getFrontImage()));
				} else{
					cardContainer.hideCards();
				}

				PauseTransition pt = new PauseTransition(Duration.millis(1500));
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

	private void operationAnimation(Operation o, EventHandler<ActionEvent> onFinished){
		//Operation card animation
		if (o.isOperationPathCard()){
			OperationPathCard op = (OperationPathCard) o;
			this.gameBoardGridPane.animatePathCard(op.getP(), onFinished);
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
	}
}
