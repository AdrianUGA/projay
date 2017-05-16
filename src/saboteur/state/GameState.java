package saboteur.state;

import java.io.IOException;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.ai.AI;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.tools.Resources;
import saboteur.view.PlayerArc;

public class GameState implements State{

	@FXML private AnchorPane boardAndCardContainer;
	@FXML private Pane boardContainer;
	@FXML private HBox cardContainer;
	
	@FXML private Circle gameBoard;
	
	@FXML private ImageView firstCard;
	@FXML private ImageView secondCard;
	@FXML private ImageView thirdCard;
	@FXML private ImageView fourthCard;
	@FXML private ImageView fifthCard;
	@FXML private ImageView sixthCard;
	
	private ImageView selectedCard = new ImageView();
	
    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
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
    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
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
        
        Resources resources = new Resources();
        resources.loadImage();
        resources.loadPicto();
        HashMap<String, Image> allCards = resources.getImageCard();
        
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
            
            //Take size of screen
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double gameTableSize = primaryScreenBounds.getHeight();
            double gameTableHalfSize = gameTableSize/2;
            double boardSize = gameTableHalfSize/2;
            
            //For center cards
            this.cardContainer.setPrefWidth(gameTableSize);
            
            //The game board
            GridPane p = new GridPane();
            
            double cardWidth = 108/3;
            double cardHeight = 166/3;
			for(int i = 0; i < Board.getGridSize(); i++) {
				for (int j = 0; j < Board.getGridSize(); j++) {
					ImageView img = new ImageView();
            		if(i>30 && i<40 && j>30 && j<40) {
            			img.setFitHeight(cardHeight);
    					img.setFitWidth(cardWidth);
            			img.setImage(allCards.get("broken_cart_card.png"));
            		}
            		p.add(img, i, j);
            	}
            }
			
			double innerRadius = gameTableHalfSize/2;
	        double radians = Math.toRadians(135);
	        double XstartInner = (int)Math.round((Math.cos(radians) * innerRadius + gameTableHalfSize));
	        double YstartInner = (int)Math.round((Math.sin(-radians) * innerRadius + gameTableHalfSize));
            p.setLayoutX(XstartInner);
            p.setLayoutY(YstartInner);
			
            this.boardContainer.getChildren().add(p);
            
            //Create Specific arc for first player
//            currentPlayerArcAndMiniCircle(gameTableHalfSize, gameTableHalfSize, boardSize);
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
            
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        this.firstCard.setImage(allCards.get("broken_cart_card.png"));
        this.secondCard.setImage(allCards.get("collapse_card.png"));
        this.thirdCard.setImage(allCards.get("path_card_17.png"));
        this.fourthCard.setImage(allCards.get("path_card_29.png"));
        this.fifthCard.setImage(allCards.get("path_card_40.png"));
        this.sixthCard.setImage(allCards.get("plan_card.png"));
    }
    
    @Override
    public void onExit() {

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
    	System.out.println("options");
    }
    
    @FXML
    private void selectCardButtonAction(MouseEvent event){
    	ImageView newCard = (ImageView)event.getSource();
    	if(!newCard.equals(this.selectedCard)) {
    		this.selectedCard.setStyle(null);
    		this.selectedCard = newCard;
    		this.selectedCard.setStyle("-fx-effect : dropshadow(gaussian, black, 2, 2, 2, 2);");
    	}
    	
    	//TODO : si la carte est une carte sabotage
    	
    	
    }
        
    private void addPlayerOnTheBoard(double sizeOfArc, double center, double length, double startAngle){
    	PlayerArc arc = new PlayerArc(sizeOfArc, center, length, startAngle);
    	this.boardContainer.getChildren().add(arc);
    }
}
