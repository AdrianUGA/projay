package saboteur.state;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.ActionCardToPlayer;
import saboteur.model.Card.Card;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;
import saboteur.tools.Icon;
import saboteur.tools.Resources;
import saboteur.view.PlayerArc;

public class PlayerWaitState extends State{
	
	private HBox cardContainer;
	private Circle gameBoard;
	private VBox goalCardContainer;
	
	private ImageView[] handCardsImages = new ImageView[6];
	private ImageView imgSelectedCard = new ImageView();
	private Card selectedCard = null;
	private List<Object> boardEffect;
	
	private PlayerArc playersArc;
	private Resources resources = new Resources();
	private HashMap<String, Image> allCards;

	private GridPane boardGridPane;
	
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
        System.out.println("wait");
        
        this.resources.loadImage();
        this.resources.loadPicto();
        this.allCards = this.resources.getImageCard();

    	this.cardContainer = (HBox)this.primaryStage.getScene().lookup("#cardContainer");
    	this.gameBoard = (Circle)this.primaryStage.getScene().lookup("#gameBoard");
    	this.goalCardContainer = (VBox)this.primaryStage.getScene().lookup("#goalCardContainer");
    	this.boardGridPane = (GridPane)this.primaryStage.getScene().lookup("#boardGridPane");
    	
        for(int i = 0; i < 6; i++) {
        	this.handCardsImages[i] = new ImageView();
        	this.handCardsImages[i].setFitWidth(108);
        	this.handCardsImages[i].setFitHeight(166);
        	this.cardContainer.getChildren().add(this.handCardsImages[i]);
        }
        
        this.cardContainer.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectCardButtonAction(event);
			}
		});
        
        this.playersArc = (PlayerArc)this.primaryStage.getScene().lookup("#playersArc");
        
        generateHandCardImage(); 
    }

    @Override
    public void onExit() {

    }
    
    private void generateHandCardImage() {
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCardsImages[i].setImage(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getFrontImage()));
        }
    }
    
    private void selectCardButtonAction(MouseEvent event) {
    	ImageView newCard = null;
    	HBox hb= (HBox)event.getSource();    	
    	if (event.getTarget() != this.cardContainer) {
    		
        	newCard = (ImageView)event.getTarget();
            
            if(!newCard.equals(this.imgSelectedCard)) {
        		cancelEffectOfPreviousSelection();
        		
            	//style on the image of the selected card
        		this.imgSelectedCard = newCard;
        		this.imgSelectedCard.setStyle("-fx-effect : dropshadow(gaussian, black, 2, 2, 2, 2);");
        		
        		//take the ref. of the card.
	        	int i = 0;
	        	for(Node nodeIn:hb.getChildren()) {
	                if(((ImageView)nodeIn).equals(event.getTarget())){
	                	this.selectedCard = this.game.getCurrentPlayer().getHand().get(i);
	                }
	                else {
	                	i++;
	                }
	            }
	        	        	
	        	if(this.selectedCard.isSabotageCard()) {
	        		ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
	        		int sabotage = ((SabotageCard)this.selectedCard).getTool().getValue();
        			for(Player p : this.game.getPlayers(card)) {
    					this.playersArc.getCircles(p)[sabotage].setStroke(Color.RED);
        			}
	        		
                	this.gsm.pop();
                	this.gsm.push("playerSelectedAction", this.selectedCard);
	    		}
	        	else if(this.selectedCard.isRescueCard()) {
	    			ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
	    			int rescue = ((RescueCard)this.selectedCard).getTool().getValue();
        			for(Player p : this.game.getPlayers(card)) {
        					this.playersArc.getCircles(p)[rescue].setStroke(Color.GREEN);
        			}
                	this.gsm.pop();
                	this.gsm.push("playerSelectedAction", this.selectedCard);
	    		}
	        	//TODO : A revoir ici
	        	else if(this.selectedCard.isDoubleRescueCard()) {
	    			ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
	    			int rescue1 = ((DoubleRescueCard)this.selectedCard).getTool1().getValue();
	    			int rescue2 = ((DoubleRescueCard)this.selectedCard).getTool2().getValue();
	    			
	    			for(Player p : this.game.getPlayers(card)) {
    					this.playersArc.getCircles(p)[rescue1].setStroke(Color.GREEN);
    					this.playersArc.getCircles(p)[rescue2].setStroke(Color.GREEN);
	    			}
                	this.gsm.pop();
                	this.gsm.push("playerSelectedAction", this.selectedCard);
	    		}
	        	else if(this.selectedCard.isPathCard()) {
	    			PathCard card = (PathCard) this.selectedCard;
	    			List<Position> possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(card);
	    			this.boardEffect = new LinkedList<>();
	    			for(Position posiCard : possiblePositionList) {
	    				SVGPath svg = new SVGPath();
						svg.setFill(Color.GREEN);
						svg.setContent(Icon.plus);
						GridPane.setHalignment(svg, HPos.CENTER);
						this.boardEffect.add(svg);
						this.boardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
	    			}
                	this.gsm.pop();
                	this.gsm.push("playerSelectedPath", this.selectedCard);
	    		}
	        	else if(this.selectedCard.isCollapseCard()) {
	    			List<Position> cantCollaps = this.game.getBoard().getGoalCards();
	    			cantCollaps.add(Board.START);

	    			this.boardEffect = new LinkedList<>();
	    			boolean cancollaps;
	    			for(int x = 0; x < Board.getGridSize(); x++) {
	    				for(int y = 0; y < Board.getGridSize(); y++) {
	    					cancollaps = true;
	    					Position posiCard = new Position(x, y);
	    					// if the position have card
	    					if(this.game.getBoard().getCard(posiCard) != null) {
	    						// Search if the card is a goal card or the start card
	    						for(Position p : cantCollaps) {
	    							if(posiCard.getcX() == p.getcX() && posiCard.getcY() == p.getcY()) {
	    								cancollaps = false;
	    							}
	    						}
	    						// if it's not one of this card :
	    						if(cancollaps) {
	    							SVGPath svg = new SVGPath();
	    							svg.setFill(Color.RED);
	    							svg.setContent(Icon.minus);
	    							GridPane.setHalignment(svg, HPos.CENTER);
	    							this.boardEffect.add(svg);
	    							this.boardGridPane.add(svg, x, y);
	    						}
	    					}
	    				}
	    			}
                	this.gsm.pop();
                	this.gsm.push("playerSelectedPath");
	    		}
	        	else if(this.selectedCard.isPlanCard()) {
	                this.gameBoard.toFront();
	                this.goalCardContainer.toFront();
                	this.goalCardContainer.setVisible(true);
                	this.gsm.pop();
                	this.gsm.push("playerSelectedPlan");
	    		}
            }
        }
    }
    
    private void cancelEffectOfPreviousSelection() {
    	if(this.selectedCard != null) {
			if(this.selectedCard.isRescueCard() || this.selectedCard.isDoubleRescueCard() || this.selectedCard.isSabotageCard()) {
				for(Player p : this.game.getPlayerList()) {
					for(int i = 0; i < 3; i++) {
						this.playersArc.getCircles(p)[i].setStroke(Color.BLACK);
					}
				}
			}
			if(this.selectedCard.isCollapseCard() || this.selectedCard.isPathCard()) {
				for(Object obj : this.boardEffect) {
					this.boardGridPane.getChildren().remove(obj);
				}
				this.boardEffect = null;
			}
			
			if(this.selectedCard.isPlanCard()) {
	            this.gameBoard.toBack();
	            this.goalCardContainer.setVisible(false);
			}
			
			this.imgSelectedCard.setStyle(null);
			this.imgSelectedCard = null;
			this.selectedCard = null;
    	}
	}
}
