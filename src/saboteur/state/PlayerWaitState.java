package saboteur.state;

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
import saboteur.model.Game;
import saboteur.model.Team;
import saboteur.model.Card.Card;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;

public class PlayerWaitState extends State{
	
	private GameCardContainer cardContainer;
	private PlayerArc playersArc;
	
	private Label playerRoleLabel;
	private ImageView playerRoleImage;
	
	private ImageView imgSelectedCard = new ImageView();
	private Card selectedCard = null;

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
    	System.out.println("waitState");
		if (this.game.getCurrentPlayer().isAI()){
			this.game.getCurrentPlayer().playCard();
			this.game.getCurrentPlayer().pickCard();
			this.gsm.pop();
		}
    	this.cardContainer = (GameCardContainer)this.primaryStage.getScene().lookup("#cardContainer");
    	this.playersArc= (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
    	this.trashButton = (Button)this.primaryStage.getScene().lookup("#trashButton");
    	this.endOfTurnButton = (Button) this.primaryStage.getScene().lookup("#endOfTurnButton");
    	this.playerRoleLabel = (Label) this.primaryStage.getScene().lookup("#playerRoleLabel");
    	this.playerRoleImage = (ImageView) this.primaryStage.getScene().lookup("#playerRoleImage");
    	   	
    	//Image and Label of player role
    	this.playerRoleLabel.setFont(new Font("Arial", 30));
    	this.playerRoleLabel.setTextFill(Color.WHITE);
    	this.playerRoleLabel.setTextAlignment(TextAlignment.CENTER);
    	this.playerRoleImage.setFitHeight(282.0);
    	this.playerRoleImage.setFitWidth(400.0);
    	
    	if(this.game.getCurrentPlayer().getTeam() == Team.DWARF) {
    		this.playerRoleLabel.setText("Chercheur d'or");
    		this.playerRoleImage.setImage(new Image("/resources/nainchercheurdor.png"));
    	}
    	else {
    		this.playerRoleLabel.setText("Saboteur");
    		this.playerRoleImage.setImage(new Image("/resources/nainsaboteur.png"));
    	}
    	
    	
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
			    	GameCardContainer cardContainer = (GameCardContainer)primaryStage.getScene().lookup("#cardContainer");
			    	cardContainer.setOnMouseClicked(null);
			    	cardContainer.generateHandCardImage(); 
			    	
			    	endOfTurnButton.setDisable(false);
			    	endOfTurnButton.setOnAction(new EventHandler<ActionEvent>() {
			    	    @Override public void handle(ActionEvent e) {
			    	    	endOfTurnButton.setOnAction(null);
			    	    	gsm.pop();
			    	    }
			    	});
				}
			});
    	    	        
        this.cardContainer.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectCardButtonAction(event);
			}
		});
        
        this.cardContainer.generateHandCardImage(); 
    }

    @Override
    public void onExit() {
    	
    }
        
    private void selectCardButtonAction(MouseEvent event) {
    	ImageView newCard = null;
    	HBox hb= (HBox)event.getSource();    	
    	if (event.getTarget() != this.cardContainer) {
    		
        	newCard = (ImageView)event.getTarget();
            
            if(newCard != this.imgSelectedCard) {
        		cancelEffectOfPreviousSelection();
        		
            	//style on the image of the selected card
        		this.imgSelectedCard = newCard;
        		this.imgSelectedCard.setStyle("-fx-effect : dropshadow(gaussian, black, 2, 2, 2, 2);");
        		
        		//take the ref. of the card.
	        	int i = 0;
	        	for(Node nodeIn:hb.getChildren()) {
	                if( (ImageView)nodeIn == event.getTarget() ){
	                	this.selectedCard = this.game.getCurrentPlayer().getHand().get(i);
	                	this.game.getCurrentPlayer().setSelectedCard(this.selectedCard);
	                }
	                else {
	                	i++;
	                }
	            }
	        	
	        	if(this.selectedCard.isSabotageCard() || this.selectedCard.isRescueCard() || this.selectedCard.isDoubleRescueCard()) {	        		
                	this.gsm.changePeek("playerSelectedAction", this.selectedCard);
	    		}
	        	else if(this.selectedCard.isPathCard() || this.selectedCard.isCollapseCard()) {
	        		this.gsm.changePeek("playerSelectedPath", this.selectedCard);
	    		}
	        	else if(this.selectedCard.isPlanCard()) {
                	this.gsm.changePeek("playerSelectedPlan", this.selectedCard);
	    		}
            }
        }
    	
    	if(this.imgSelectedCard != null) {
    		this.trashButton.setDisable(false);
    	}
    	
    }
    
    private void cancelEffectOfPreviousSelection() {
    	if(this.selectedCard != null) {
			this.imgSelectedCard.setStyle(null);
			this.imgSelectedCard = null;
			this.selectedCard = null;
    	}
	}
}
