package saboteur.state;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.ai.AI;
import saboteur.ai.Difficulty;
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

public class GameState extends State{

	@FXML private Pane boardContainer;
	@FXML private HBox cardContainer;
	@FXML private Circle gameBoard;
	@FXML private VBox goalCardContainer;

	private FXMLLoader loader;
	
	private PlayerArc[] playersArc;
	private ImageView[] handCardsImages = new ImageView[6];
	private Card selectedCard = null;
	private ImageView imgSelectedCard = new ImageView();
	private List<Object> boardEffect;
	private Resources resources = new Resources();
	private HashMap<String, Image> allCards;
	
	private GridPane boardGridPane;
	private int xmin, xmax, ymin, ymax;

	GameStateMachine psm;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {
        if (this.game.gameIsFinished()){
            //fin de la partie
            //this.gsm.change("annonce vainqueur");
        	announceTeamWinner();
        	announcePlayerWinner();
        	//System.out.println("Le joueur "+this.game.getWinner().getName()+" a gagné ! (Avec "+this.game.getWinner().getGold()+ " or).");
            //System.out.println("fin de partie");
        } else {
            if (this.game.roundIsFinished()){
                announceTeamWinner();
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
                else{
                	this.gsm.push("playerWait");
				}
            }
        }
    }

    //For console mode
    private void announcePlayerWinner() {
		if (!this.game.isPlayerWinnerAlreadyAnnounced()){
			LinkedList<Player> winners =  this.game.getWinners();
			if (winners.size()>1){
				System.out.print("Les joueurs ");
				for (Player p : winners){
					if (winners.indexOf(p) == winners.size()-2)
						System.out.print(p.getName() + " et ");
					else if (winners.indexOf(p) == winners.size()-1)
						System.out.print(p.getName() + " ");
					else
						System.out.print(p.getName() + ", ");
				}
				System.out.print("sont gagnants ex aequo ");
			} else {
				System.out.print(winners.getFirst().getName() + " a gagné la partie ");
			}
			System.out.println("avec " + winners.getFirst().getGold() + " pépites d'or !");
			this.game.setPlayerWinnerAlreadyAnnounced(true);
		}
	}

	//For console mode
	private void announceTeamWinner() {
		if (!this.game.isTeamWinnerAlreadyAnnounced()){
			if(this.game.dwarfsWon()) System.out.println("Les nains ont gagné !");
			else System.out.println("Les saboteurs ont gagné !");
			this.game.setTeamWinnerAlreadyAnnounced(true);
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
    	this.game.addPlayer(new AI(this.game, "Yves", Difficulty.EASY));
    	this.game.addPlayer(new AI(this.game, "Philippe", Difficulty.EASY));
    	this.game.addPlayer(new AI(this.game, "Jean-Marie", Difficulty.EASY));
		
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
            this.loader = new FXMLLoader();
            this.loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            this.loader.setController(this);
            Pane pane = this.loader.load();

            this.playersArc = new PlayerArc[this.game.getPlayerList().size()];
            //Take size of screen
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double gameTableSize = primaryScreenBounds.getHeight();
            double gameTableHalfSize = gameTableSize/2;
            double boardSize = gameTableHalfSize/2;

            //For center cards hand Image
            this.cardContainer.setPrefWidth(gameTableSize);
            for(int i = 0; i < 6; i++) {
            	this.handCardsImages[i] = new ImageView();
            	this.handCardsImages[i].setFitWidth(108);
            	this.handCardsImages[i].setFitHeight(166);
            	this.cardContainer.getChildren().add(this.handCardsImages[i]);
            }
            generateHandCardImage(); 
            
            
            //Create the goal card for the planCardActcion
            this.goalCardContainer.setPrefSize(gameTableSize, gameTableSize);
            for (int i = 0; i < 3; i++) {
            	ImageView img = new ImageView("/resources/cards/goal_card_verso.png");
            	img.setFitWidth(108/1.5);
            	img.setFitHeight(166/1.5);
            	
            	SVGPath svg = new SVGPath();
            	svg.setFill(Color.WHITE);
            	svg.setContent(Icon.eye);
            	
            	StackPane p = new StackPane(img, svg);
            	p.setAlignment(Pos.CENTER);
            	this.goalCardContainer.getChildren().add(p);
            }
            
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

            this.changeLayout(pane);
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
        this.gsm.push("pauseMenu");
    }
    
    @FXML void selectCardButtonAction(MouseEvent event) {
    	ImageView newCard = null;
    	HBox hb= (HBox)event.getSource();    	
    	if (event.getTarget() != this.cardContainer) {
    		
        	newCard = (ImageView)event.getTarget();
            
            if(!newCard.equals(this.imgSelectedCard)) {
        		cancelPreviousSelection();
        		
            	//style on the image of the selected card
//        		this.imgSelectedCard.setStyle(null);
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
	        		for(PlayerArc player : this.playersArc) {
	        			for(Player p : this.game.getPlayers(card)) {
	        				if(p == player.getPlayer()) {
	        					player.getCircle()[sabotage].setStroke(Color.RED);
	        					break;
	        				}
	        			}
	        		}
	    		}
	        	
	    		if(this.selectedCard.isRescueCard()) {
	    			ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
	    			int rescue = ((RescueCard)this.selectedCard).getTool().getValue();
	    			for(PlayerArc player : this.playersArc) {
	        			for(Player p : this.game.getPlayers(card)) {
	        				if(p == player.getPlayer()) {
	        					player.getCircle()[rescue].setStroke(Color.GREEN);
	        					break;
	        				}
	        			}
	        		}
	    		}
	        	
	        	//TODO : A revoir ici
	    		if(this.selectedCard.isDoubleRescueCard()) {
	    			ActionCardToPlayer card = (ActionCardToPlayer) this.selectedCard;
	    			int sabotage1 = ((DoubleRescueCard)this.selectedCard).getTool1().getValue();
	    			int sabotage2 = ((DoubleRescueCard)this.selectedCard).getTool2().getValue();
	    			
	    			for(PlayerArc player : this.playersArc) {
	        			for(Player p : this.game.getPlayers(card)) {
	        				if(p == player.getPlayer()) {
	        					player.getCircle()[sabotage1].setStroke(Color.GREEN);
	        					player.getCircle()[sabotage2].setStroke(Color.GREEN);
	        					break;
	        				}
	        			}
	        		}
	    		}
	        	
	        	
	    		if(this.selectedCard.isPathCard()) {
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
	    		}
	    		
	    		if(this.selectedCard.isCollapseCard()) {
	    			List<Position> cantCollaps = this.game.getBoard().getGoalCards();
	    			cantCollaps.add(Board.START);

	    			this.boardEffect = new LinkedList<>();
	    			boolean cancollaps;
	    			//For all Posi between xmin/xman and ymin/ymax
	    			for(int x = this.xmin; x < this.xmax; x++) {
	    				for(int y = this.xmin; y < this.ymax; y++) {
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
	    		}
	    		
	    		if(this.selectedCard.isPlanCard()) {
	                this.gameBoard.toFront();
	                this.goalCardContainer.toFront();
                	this.goalCardContainer.setVisible(true);
	    		}
            }
        }
    }
    
    private void cancelPreviousSelection() {

    	if(this.selectedCard != null) {
			if(this.selectedCard.isRescueCard() || this.selectedCard.isDoubleRescueCard() || this.selectedCard.isSabotageCard()) {
				for(PlayerArc player : this.playersArc) {
					for(int i = 0; i < 3; i++) {
						player.getCircle()[i].setStroke(Color.BLACK);
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

	private void addPlayerOnTheBoard(double sizeOfArc, double center, double length, double startAngle, int idPlayer){
    	this.playersArc[idPlayer] = new PlayerArc(sizeOfArc, center, length, startAngle, this.game.getPlayerList().get(idPlayer));
    	this.boardContainer.getChildren().add(this.playersArc[idPlayer]);
    }
    
    private void generateHandCardImage() {
    	for(int i = 0; i < this.game.getCurrentPlayer().getHand().size(); i++) {
        	this.handCardsImages[i].setImage(this.allCards.get(this.game.getCurrentPlayer().getHand().get(i).getFrontImage()));
        }
    }
    
    private void generateBoard() {
        double cardWidth = 108/3;
        double cardHeight = 166/3;
        
        this.xmin = Board.getGridSize();
        this.xmax = 0;
        this.ymin = Board.getGridSize();
        this.ymax = 0;
        
        for(int i = 0; i < Board.getGridSize(); i++) {
			for (int j = 0; j < Board.getGridSize(); j++) {
				PathCard card = this.game.getBoard().getCard(new Position(i,j));
				if( card != null) {
					if(this.xmin > i) {
						this.xmin = i;
					}
					if(this.xmax < i) {
						this.xmax = i;
					}
					if(this.ymin > j) {
						this.ymin = j;
					}
					if(this.ymax < j) {
						this.ymax = j;
					}
				}
			}
        }
        this.xmin--;
        this.xmax++;
        this.ymin--;
        this.ymax++; 
        
    	for(int i = this.xmin; i <= this.xmax; i++) {
    		for (int j = this.ymin; j <= this.ymax; j++) {
				ImageView img = new ImageView();
				PathCard card = this.game.getBoard().getCard(new Position(i,j));
				
				if(i >= this.xmin && i <= this.xmax && j >= this.ymin && j <= this.ymax) {
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
				}
				this.boardGridPane.add(img, i, j);
        	}
        }
    }
    
    private int getIndexOfGridPane(Position posiCard) {
		int x = posiCard.getcX() - xmin;
		int y = posiCard.getcY() - ymin;
		int dx;
		if (x == 0){
			dx = x * (ymax-ymin);
		}
		else {
			dx = x * (ymax-ymin+1);
		}
		return dx + y;
    	
    }
}
