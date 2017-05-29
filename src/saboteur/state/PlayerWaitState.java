package saboteur.state;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.Card.Card;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;
import saboteur.view.PlayerRoleContainer;
import saboteur.view.TrashAndPickStackContainer;

public class PlayerWaitState extends State{
	
	private TrashAndPickStackContainer trashAndPickStackContainer;
	private GameCardContainer gameCardContainer;
	private PlayerRoleContainer playerRoleContainer;
	private int round;
	
    public PlayerWaitState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
        this.round = this.game.getRound();
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
    	this.gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playerRoleContainer = (PlayerRoleContainer) this.primaryStage.getScene().lookup("#playerRoleContainer");
    	
    	GameBoardGridPane gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
    	gameBoardGridPane.generateBoard();
    	
    	PlayerArc playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
		playersArc.refreshPlayersArcsAndCircles();
		
		if(this.round != this.game.getRound()) {
			this.round = this.game.getRound();
			Text roundNum = (Text) this.primaryStage.getScene().lookup("#roundNum");
			roundNum.setText("Manche " + this.round);
		}
		

		if (this.game.getCurrentPlayer().isAI()){
			//TODO change pause
			PauseTransition pt = new PauseTransition(Duration.millis(2000));
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
    		this.trashAndPickStackContainer.enableTrashButton();
    	}
    }

    private void initControlForHuman(){
		//Image and Label of player role
		this.playerRoleContainer.refreshPlayerRole();
		this.playerRoleContainer.setPlayerRoleComponentsVisible(true);

		this.trashAndPickStackContainer.disablePickAndEndTurnButton();
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Operation op = game.getCurrentPlayer().playCard();
				gsm.changePeek("playerPlayCard", op);

				trashAndPickStackContainer.disableTrashButton();
			}
		};
		this.trashAndPickStackContainer.setEventToTrashButton(event);
		

		this.gameCardContainer.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectCardButtonAction(event);
			}
		});

		this.gameCardContainer.showCards();
	}
}
