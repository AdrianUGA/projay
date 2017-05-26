package saboteur.state;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import saboteur.view.TrashAndPickStackContainer;

public class PlayerWaitState extends State{
	
	private TrashAndPickStackContainer trashAndPickStackContainer;
	private GameCardContainer gameCardContainer;
	private PlayerArc playersArc;
	
	private Label playerRoleLabel;
	private ImageView playerRoleImage;

	private Button trashButton;
	private Button endOfTurnButton;

	
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
    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
    	this.gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
    	this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.trashButton = (Button) this.primaryStage.getScene().lookup("#trashButton");
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	this.playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");
    	
    	Button undoButton = (Button) this.primaryStage.getScene().lookup("#undoButton");
    	Button redoButton = (Button) this.primaryStage.getScene().lookup("#redoButton");

    	//Manage undo and redo button
    	if(this.game.historyUndoIsEmpty()) {
    		undoButton.setDisable(true);
    	}
    	else {
    		undoButton.setDisable(false);
    	}
    	
    	if(this.game.historyRedoIsEmpty()) {
    		redoButton.setDisable(true);
    	}
    	else {
    		redoButton.setDisable(false);
    	}
    	
    	
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
    	ImageView newCard = null;
    	HBox hb= (HBox)event.getSource();    	
    	if (event.getTarget() != this.gameCardContainer) {
    		
        	newCard = (ImageView)event.getTarget();
            
            if(newCard != this.gameCardContainer.getImgSelectedCard()) {

            	removeOldSelection();
        		addNewSelection(newCard);

        		//take the ref. of the card.
	        	int i = 0;
	        	for(Node nodeIn:hb.getChildren()) {
	                if( (ImageView)nodeIn == event.getTarget() ){
	                	this.gameCardContainer.setSelectedCard( this.game.getCurrentPlayer().getHand().get(i) );
	                	this.game.getCurrentPlayer().setSelectedCard( this.gameCardContainer.getSelectedCard() );
	                }
	                else {
	                	i++;
	                }
	            }
	        	
	        	Card selectedCard = this.gameCardContainer.getSelectedCard();
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
    		this.trashAndPickStackContainer.enableTrashButton();
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


		this.playersArc.refreshPlayersArcsAndCircles();

		this.endOfTurnButton.setDisable(true);
		this.trashButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				game.getCurrentPlayer().playCard();
				gsm.changePeek("playerWait");
				trashButton.setDisable(true);

				System.out.println(game.stackIsEmpty());
				game.getCurrentPlayer().pickCard();
				GameCardContainer gameCardContainer = (GameCardContainer)primaryStage.getScene().lookup("#gameCardContainer");
				gameCardContainer.setOnMouseClicked(null);
				gameCardContainer.generateHandCardImage();

				endOfTurnButton.setDisable(false);
				endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent e) {
						endOfTurnButton.setOnAction(null);
						gsm.pop();
					}
				});
			}
		});
		
		
		this.trashAndPickStackContainer.disablePickAndEndTurnButton();
		EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				game.getCurrentPlayer().playCard();
				gsm.changePeek("playerWait");
				trashAndPickStackContainer.disableTrashButton();
				
				System.out.println(game.stackIsEmpty());
				game.getCurrentPlayer().pickCard();
				GameCardContainer gameCardContainer = (GameCardContainer)primaryStage.getScene().lookup("#gameCardContainer");
				gameCardContainer.setOnMouseClicked(null);
				gameCardContainer.generateHandCardImage();
				
				trashAndPickStackContainer.enablePickAndEndTurnButton();
				trashAndPickStackContainer.setEventToPickAndEndTurnButton(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						trashAndPickStackContainer.setEventToPickAndEndTurnButton(null);
						gsm.pop();
					}
				});
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
    
    private void removeOldSelection() {
    	if(this.gameCardContainer.getSelectedCard() != null) {
    		ImageView imgSelectedCard = this.gameCardContainer.getImgSelectedCard();
			TranslateTransition tt = new TranslateTransition(Duration.INDEFINITE.millis(200), imgSelectedCard);
			tt.setByY(30);
			ScaleTransition st = new ScaleTransition(Duration.INDEFINITE.millis(200), imgSelectedCard);
			st.setByX(-0.2f);
			st.setByY(-0.2f);
			ParallelTransition pt = new ParallelTransition(tt, st);
			pt.play();
			imgSelectedCard = null;
			this.gameCardContainer.setSelectedCard(null);
		}
	}

	private void addNewSelection(ImageView newCard){
		TranslateTransition tt = new TranslateTransition(Duration.INDEFINITE.millis(200), newCard);
		tt.setByY(-30);
		ScaleTransition st = new ScaleTransition(Duration.INDEFINITE.millis(200), newCard);
		st.setByX(0.2f);
		st.setByY(0.2f);
		ParallelTransition pt = new ParallelTransition(tt, st);
		pt.play();
		this.gameCardContainer.setImgSelectedCard(newCard);
	}
}
