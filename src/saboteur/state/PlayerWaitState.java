package saboteur.state;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.Team;
import saboteur.model.Card.Card;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;

public class PlayerWaitState extends State{
	
	private GameCardContainer gameCardContainer;
	private PlayerArc playersArc;
	
	private Label playerRoleLabel;
	private ImageView playerRoleImage;

	private Button trashButton;
	private Button endOfTurnButton;

	private ImageView trash;
	private ImageView stack;

	
    public PlayerWaitState(GameStateMachine gsm, Game game, Stage primaryStage){
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
    	this.gameCardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	this.playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");
    	this.trash = (ImageView) this.primaryStage.getScene().lookup("#trash");
    	this.stack = (ImageView) this.primaryStage.getScene().lookup("#stack");

		this.playersArc.refreshPlayersArcsAndCircles();

		if (this.game.getCurrentPlayer().isAI()){
			PauseTransition pt = new PauseTransition(Duration.INDEFINITE.millis(1000));
			pt.setOnFinished(event -> {
				Operation o = this.game.getCurrentPlayer().playCard();
				this.gsm.changePeek("playerPlayCard", o);
			});
			pt.play();
		} else {
			initControlForHuman();
		}
    }

    @Override
    public void onExit() {
    	
    }
        
    private void selectCardButtonAction(MouseEvent event) {
    	ImageView imgSelectedCard;
    	HBox hb = (HBox)event.getSource();
    	if (event.getTarget() != this.gameCardContainer) {

			imgSelectedCard = (ImageView)event.getTarget();
            
            if(imgSelectedCard != this.gameCardContainer.getImgSelectedCard()) {

            	this.gameCardContainer.removeSelection();

				Card selectedCard = null;
        		//take the ref. of the card.
	        	int i = 0;
	        	for(Node nodeIn:hb.getChildren()) {
	                if( (ImageView)nodeIn == event.getTarget() ){
	                	selectedCard = this.game.getCurrentPlayer().getHand().get(i);
	                	this.game.getCurrentPlayer().setSelectedCard(selectedCard);
	                }
	                else {
	                	i++;
	                }
	            }

	            this.gameCardContainer.addSelection(selectedCard, imgSelectedCard);
	        	

	        	if(selectedCard.isSabotageCard() || selectedCard.isRescueCard() || selectedCard.isDoubleRescueCard()) {	        		
                	this.gsm.changePeek("playerSelectedAction", selectedCard);
	    		}
	        	else if(selectedCard.isPathCard() || selectedCard.isCollapseCard()) {
	        		this.gsm.changePeek("playerSelectedPath", selectedCard);
	    		}
	        	else if(selectedCard.isPlanCard()) {
                	this.gsm.changePeek("playerSelectedPlan", selectedCard);
	    		}
            }
        }
    	
    	if(this.gameCardContainer.getImgSelectedCard() != null) {
    		this.trashButton.setDisable(false);
    	}
    }

    private void initControlForHuman(){
		//Image and Label of player role

		if(this.game.getCurrentPlayer().getTeam() == Team.DWARF) {
			this.playerRoleLabel.setText("Chercheur d'or");
			this.playerRoleImage.setImage(new Image("/resources/nainchercheurdor.png"));
		}
		else {
			this.playerRoleLabel.setText("Saboteur");
			this.playerRoleImage.setImage(new Image("/resources/nainsaboteur.png"));
		}
		this.playerRoleImage.setVisible(true);
		this.playerRoleLabel.setVisible(true);

		this.endOfTurnButton.setDisable(true);

		this.trashButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				Operation op = game.getCurrentPlayer().playCard();
				gsm.changePeek("playerPlayCard", op);
				trashButton.setDisable(true);
			}
		});

		this.gameCardContainer.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectCardButtonAction(event);
			}
		});

		this.gameCardContainer.showCards();
	}
}
