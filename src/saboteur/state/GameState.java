package saboteur.state;

import java.io.IOException;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.ai.AI;
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
import saboteur.model.Card.Tool;
import saboteur.tools.Resources;
import saboteur.view.PlayerArc;

public class GameState implements State{

	@FXML private Pane boardContainer;
	@FXML private HBox cardContainer;
	@FXML private Circle gameBoard;
	
	private PlayerArc[] players;
	private ImageView[] handCards = new ImageView[6];
	private Card selectedCard;
	private ImageView imgSelectedCard = new ImageView();
	private Resources resources = new Resources();
	private HashMap<String, Image> allCards;
	
	private GridPane boardGridPane;
	
    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    private boolean pause;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
    }

    @Override
    public void update() {
        if (this.game.gameIsFinished()){
            //fin de la partie
            //this.gsm.change("annonce vainqueur");
        	if(game.dwarfsWon())System.out.println("Nains ont gagné");
        	//System.out.println("Le joueur "+this.game.getWinner().getName()+" a gagné ! (Avec "+this.game.getWinner().getGold()+ " or).");
            //System.out.println("fin de partie");
        } else {
            if (this.game.roundIsFinished()){
                //fin de la manche
                //Distribution des cartes gold
            	if(game.dwarfsWon())System.out.println("Nains ont gagné");
                this.game.newRound();
            } else{
                //la manche continue
                if (this.game.getCurrentPlayer().isAI()){
                    this.game.getCurrentPlayer().playCard();
                    this.game.getCurrentPlayer().pickCard();
                    //System.out.println("AI has played");
                /*try{
                    Thread.sleep(3000);
                } catch (Exception e){
                    e.printStackTrace();
                }*/
                    this.game.nextPlayer();
                }
            }
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
    	initGame();
    }
    
    @Override
    public void onExit() {

    }

    private void initGame(){
        /*
    	 * Pour Emmanuel qui fait des tests avec les IA
    	 * Pour pas être embêter par le problème de setTeam(),
    	 * commente son appel dans la méthode newRound de la classe Game
    	 */

    	//Début du bloc à commenter
    	
    	this.game.getPlayerList().clear();
    	this.game.addPlayer(new AI(this.game, "Yves"));
    	this.game.addPlayer(new AI(this.game, "Philippe"));
    	this.game.addPlayer(new AI(this.game, "Jean-Marie"));
		
    	//Fin du bloc à commenter
    	
        this.game.newGame();
        
        this.resources.loadImage();
        this.resources.loadPicto();
        this.allCards = this.resources.getImageCard();
        
        //NE PAS COMMENTER CETTE BOUCLE !!
        //IMPORTANT DANS TOUS LES CAS OU IL Y A UNE AI
        
        for(Player p : this.game.getPlayerList()){
    		if(p.isAI()){
    			((AI)p).initializeAI();
    		}
    	}

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);

            this.players = new PlayerArc[this.game.getPlayerList().size()];
            //Take size of screen
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double gameTableSize = primaryScreenBounds.getHeight();
            double gameTableHalfSize = gameTableSize/2;
            double boardSize = gameTableHalfSize/2;

            //For center cards hand Image
            this.cardContainer.setPrefWidth(gameTableSize);
            for(int i = 0; i < 6; i++) {
            	this.handCards[i] = new ImageView();
            	this.handCards[i].setFitWidth(108);
            	this.handCards[i].setFitHeight(166);
            	this.cardContainer.getChildren().add(this.handCards[i]);
            }
            generateHandCardImage();
            
            //The game board
            double innerRadius = gameTableHalfSize/2;
	        double radians = Math.toRadians(135);
	        double XstartInner = (int)Math.round((Math.cos(radians) * innerRadius + gameTableHalfSize));
	        double YstartInner = (int)Math.round((Math.sin(-radians) * innerRadius + gameTableHalfSize));
            this.boardGridPane = new GridPane();
            generateBoard();
	        this.boardGridPane.setLayoutX(XstartInner);
	        this.boardGridPane.setLayoutY(YstartInner);
            this.boardContainer.getChildren().add(this.boardGridPane);
            
            //Create Specific arc for first player
        	addPlayerOnTheBoard(gameTableHalfSize, gameTableHalfSize, 100, -140.0, 0);

            //Create arc for other player
            int nbPlayer = this.game.getPlayerList().size()-1;
            double length = 260.0 / nbPlayer; // 260 = 360 - 100 (100 degree of the 1st player)
            double startAngle = -140.0;
            for (int i = 1; i<=nbPlayer ; i++) {
                startAngle = startAngle - length;
                addPlayerOnTheBoard(gameTableHalfSize, gameTableHalfSize, length, startAngle, i);
            }

            //Create the circle of the game board
            this.gameBoard.setCenterX(gameTableHalfSize);
            this.gameBoard.setCenterY(gameTableHalfSize);
            this.gameBoard.setRadius(boardSize);

            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    //End of turn of the current player.
    @FXML
    private void endOfTurnButtonAction(){
    	System.out.println("end of turn");
    }
    @FXML
    private void takeCardOfStackButtonAction(){
    	System.out.println("pioche");
    }
    @FXML
    private void trashCardButtonAction(){
    	System.out.println("jetter");
    }
    
    @FXML
    private void optionsButtonAction(){
        this.pause = true;
        this.gsm.push("pauseMenu");
    }
    
    @FXML void selectCardButtonAction(MouseEvent event) {
    	ImageView newCard = null;
    	HBox hb= (HBox)event.getSource();    	
    	if (event.getTarget() != this.cardContainer) {
    		
        	newCard = (ImageView)event.getTarget();
//            System.out.println("hBox Ignored! " + event.getTarget());
            
            if(!newCard.equals(this.imgSelectedCard)) {
        		this.imgSelectedCard.setStyle(null);
        		this.imgSelectedCard = newCard;
        		this.imgSelectedCard.setStyle("-fx-effect : dropshadow(gaussian, black, 2, 2, 2, 2);");
        	}
            
        	int i = 0;
        	for(Node nodeIn:hb.getChildren()){
                if(((ImageView)nodeIn).equals(event.getTarget())){
//                	System.out.println(this.game.getCurrentPlayer().getHand().get(i));
                	this.selectedCard = this.game.getCurrentPlayer().getHand().get(i);
                }
                else{
                	i++;
                }
            }
        	        	
        	if(this.selectedCard.isSabotageCard()) {
        		ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
        		int malus = ((SabotageCard)this.selectedCard).getTool().getValue();
        		for(PlayerArc player : this.players) {
        			for(Player p : this.game.getPlayers(card)) {
        				if(p == player.getPlayer()) {
        					player.getCircle()[malus].setStroke(Color.RED);
        					break;
        				}
        			}
        		}
    		}
        	
    		if(this.selectedCard.isRescueCard()) {
    			ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
    			int malus = ((RescueCard)this.selectedCard).getTool().getValue();
    			for(PlayerArc player : this.players) {
        			for(Player p : this.game.getPlayers(card)) {
        				if(p == player.getPlayer()) {
        					player.getCircle()[malus].setStroke(Color.GREEN);
        					break;
        				}
        			}
        		}
    		}
        	
        	//TODO : A revoir ici
    		if(this.selectedCard.isDoubleRescueCard()) {
    			ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
    			int malus1 = ((DoubleRescueCard)this.selectedCard).getTool1().getValue();
    			int malus2 = ((DoubleRescueCard)this.selectedCard).getTool2().getValue();
    			
    			for(PlayerArc player : this.players) {
        			for(Player p : this.game.getPlayers(card)) {
        				if(p == player.getPlayer()) {
        					player.getCircle()[malus1].setStroke(Color.GREEN);
        					player.getCircle()[malus2].setStroke(Color.GREEN);
        					break;
        				}
        			}
        		}
    		}
        	
        	
    		if(this.selectedCard.isPathCard()) {
    			
    		}
//        	
//        	
//        	
//    		if(this.selectedCard.isPlanCard()) {
//    			
//    		}
//    		if(this.selectedCard.isCollapseCard()) {
//    			
//    		}
        }
    	else {
            System.out.println("hBox! " + event.getTarget());
        }
    }
    
    private void addPlayerOnTheBoard(double sizeOfArc, double center, double length, double startAngle, int idPlayer){
    	this.players[idPlayer] = new PlayerArc(sizeOfArc, center, length, startAngle, this.game.getPlayerList().get(idPlayer));
    	this.boardContainer.getChildren().add(this.players[idPlayer]);
    }
    
    private void generateHandCardImage() {
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCards[i].setImage(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getFrontImage()));
        }
    }
    
    private void generateBoard() {
        double cardWidth = 108/3;
        double cardHeight = 166/3;
        
        int xmin = Board.getGridSize();;
        int xmax = 0;
        int ymin = Board.getGridSize();
        int ymax = 0;
        
        for(int i = 0; i < Board.getGridSize(); i++) {
			for (int j = 0; j < Board.getGridSize(); j++) {
				PathCard card = this.game.getBoard().getCard(new Position(i,j));
				if( card != null) {
					if(xmin > i) {
						xmin = i;
					}
					if(xmax < i) {
						xmax = i;
					}
					if(ymin > j) {
						ymin = j;
					}
					if(ymax < j) {
						ymax = j;
					}
				}
			}
        }
        xmin--;
        xmax++;
        ymin--;
        ymax++; 
        
    	for(int i = xmin; i <= xmax; i++) {
    		for (int j = ymin; j <= ymax; j++) {
				ImageView img = new ImageView();
				PathCard card = this.game.getBoard().getCard(new Position(i,j));
				
				if(i>=xmin && i<=xmax && j>=ymin && j<=ymax) {
					img.setFitHeight(cardHeight);
					img.setFitWidth(cardWidth);
					if( card != null) {
						if(card.isVisible()) {
	    					img.setImage(this.allCards.get(card.getFrontImage()));
						}
						else {
	    					img.setImage(this.allCards.get(card.getBackImage()));
						}
					}
					else{
						img.setImage(this.allCards.get("broken_cart_card.png"));
					}
				}
				this.boardGridPane.add(img, i, j);
        	}
        }
    }
}
