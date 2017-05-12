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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Resources;
import saboteur.view.PlayerArc;

public class GameState implements State{

	@FXML private AnchorPane boardAndCardContainer;
	@FXML private Pane boardContainer;
	@FXML private HBox cardContainer;
	@FXML private Arc currentPlayerArc;
	@FXML private Circle gameBoard;
	@FXML private ImageView firstCard;
	@FXML private ImageView secondCard;
	@FXML private ImageView thirdCard;
	@FXML private ImageView fourthCard;
	@FXML private ImageView fifthCard;
	@FXML private ImageView sixthCard;
	
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

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            loader.setController(this);
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double boardSize = primaryScreenBounds.getHeight();
            double halfBoardSize = boardSize/2;
            
            this.cardContainer.setPrefWidth(boardSize);
            
            this.currentPlayerArc.setRadiusX(halfBoardSize);
            this.currentPlayerArc.setRadiusY(halfBoardSize);
            this.currentPlayerArc.setCenterX(halfBoardSize);
            this.currentPlayerArc.setCenterY(halfBoardSize);
            
            int nbPlayer = this.game.getPlayerList().size()-1;
            double length = 260.0 / nbPlayer;
            double startAngle = -140.0;
            for (int i = 0; i<nbPlayer ; i++) {
            	startAngle = startAngle - length;
            	addPlayerOnTheBoard(halfBoardSize, length, startAngle);
            }
            
//            this.gameBoard.setCenterX(halfBoardSize);
//            this.gameBoard.setCenterY(halfBoardSize);
//            this.gameBoard.setRadius(halfBoardSize/2);
//            this.gameBoard.toFront();
                        
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        } catch (IOException e){
            e.printStackTrace();
        }
        Resources resources = new Resources();
        resources.loadImage();
        HashMap<String, Image> allCards = resources.getImageCard();
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
    
    private void addPlayerOnTheBoard(double size, double length, double startAngle){
    	PlayerArc arc = new PlayerArc(size, length, startAngle);
    	this.boardContainer.getChildren().add(arc);
    }
}
