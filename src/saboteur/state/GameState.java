package saboteur.state;

import java.io.IOException;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;
import saboteur.tools.Resources;
import saboteur.view.PlayerArc;

public class GameState extends State{

	@FXML private Pane boardContainer;
	@FXML private HBox cardContainer;
	@FXML private Circle gameBoard;
		
	ImageView[] handCards = new ImageView[6];
	private Card selectedCard;
	private ImageView imgSelectedCard = new ImageView();
	private Resources resources = new Resources();
	private HashMap<String, Image> allCards;
	
	private GridPane boardGridPane;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {
        if (this.game.gameIsFinished()){
            //fin de la partie
            if(game.dwarfsWon())System.out.println("Nains ont gagné");
            //this.gsm.change("annonce vainqueur");
            //System.out.println("fin de partie");
        } else {
            if (this.game.roundIsFinished()){
                //fin de la manche
                //Distribution des cartes gold
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
        System.out.println("coucou");
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
    	/*
    	this.game.getPlayerList().clear();
    	this.game.addPlayer(new AI(this.game, "Yves"));
    	this.game.addPlayer(new AI(this.game, "Philippe"));
    	this.game.addPlayer(new AI(this.game, "Jean-Marie"));
		*/
    	//Fin du bloc à commenter
    	
        this.game.newGame();
        
        this.resources.loadImage();
        this.resources.loadPicto();
        this.allCards = this.resources.getImageCard();
        
        //NE PAS COMMENTER CETTE BOUCLE !!
        //IMPORTANT DANS TOUS LES CAS OU IL Y A UNE AI
        /*
        for(Player p : this.game.getPlayerList()){
    		if(p.isAI()){
    			((AI)p).initializeAI();
    		}
    	}*/

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            loader.setController(this);
            Pane pane = loader.load();

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
        	addPlayerOnTheBoard(gameTableHalfSize, gameTableHalfSize, 100, -140.0);

            //Create arc for other player
            int nbPlayer = this.game.getPlayerList().size()-1;
            double length = 260.0 / nbPlayer; // 260 = 360 - 100 (100 degree of the 1st player)
            double startAngle = -140.0;
            for (int i = 0; i<nbPlayer ; i++) {
                startAngle = startAngle - length;
                addPlayerOnTheBoard(gameTableHalfSize, gameTableHalfSize, length, startAngle);
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
    	if (event.getTarget() == this.cardContainer) {
            System.out.println("hBox! " + event.getTarget());
        } else {
        	newCard = (ImageView)event.getTarget();
            System.out.println("hBox Ignored! " + event.getTarget());
        }
    	
    	if(!newCard.equals(this.imgSelectedCard)) {
    		this.imgSelectedCard.setStyle(null);
    		this.imgSelectedCard = newCard;
    		this.imgSelectedCard.setStyle("-fx-effect : dropshadow(gaussian, black, 2, 2, 2, 2);");
    	}
    	
    	int i = 0;
    	for(Node nodeIn:hb.getChildren()){
            if(((ImageView)nodeIn).equals(event.getSource())){
            	this.selectedCard = this.game.getCurrentPlayer().getHand().get(i);
            }
            else{
            	i++;
            }
        }
    	
//	    if(this.selectedCard.isSabotage()) {
//			
//		}
//		if(this.selectedCard.isRescue()) {
//			
//		}
//		if(this.selectedCard.isDoubleRescue()) {
//			
//		}
//		if(this.selectedCard.isPath()) {
//			
//		}
//		if(this.selectedCard.isPlan()) {
//			
//		}
//		if(this.selectedCard.isCollapse()) {
//			
//		}
    }
    
    private void addPlayerOnTheBoard(double sizeOfArc, double center, double length, double startAngle){
    	PlayerArc arc = new PlayerArc(sizeOfArc, center, length, startAngle);
    	this.boardContainer.getChildren().add(arc);
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
