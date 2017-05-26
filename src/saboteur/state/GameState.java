package saboteur.state;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.GameComponentsSize;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;
import saboteur.view.TrashAndPickStackContainer;

public class GameState extends State{
	
	@FXML private BorderPane gameBorderPane;
	
	@FXML private Pane boardContainer;
	@FXML private Circle gameBoard;
	@FXML private VBox goalCardContainer;
	
	@FXML private Label playerRoleLabel;
	@FXML private ImageView playerRoleImage;
	
	@FXML private Button undoButton;
	@FXML private Button redoButton;
	
	private FXMLLoader loader;
	
	private PlayerArc playersArc;
	
	private GameBoardGridPane gameBoardGridPane;
	
	private GameCardContainer gameCardContainer;
	private TrashAndPickStackContainer trahAndPickStackContainer;

    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {
        if (this.game.roundIsFinished()){
            this.gsm.push("roundIsFinished");
        } else{
            this.gsm.push("playerBeginOfTurn");
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
        
        this.game.initAI();
        
        try{
            this.loader = new FXMLLoader();
            this.loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            this.loader.setController(this);
            Pane pane = this.loader.load();
                       
            GameComponentsSize gameComponentSize = GameComponentsSize.getGameComponentSize();
            double gameTableSize = gameComponentSize.getGameTableSize();
            
            // ******************** Right ********************
        	//Image and Label of player role
        	this.playerRoleLabel.setFont(new Font("Arial", 30));
        	this.playerRoleLabel.setTextFill(Color.WHITE);
        	this.playerRoleLabel.setTextAlignment(TextAlignment.CENTER);
        	this.playerRoleImage.setFitHeight(282.0);
        	this.playerRoleImage.setFitWidth(400.0);
        	
            // trash and pick stacks
        	this.trahAndPickStackContainer = new TrashAndPickStackContainer(this.game);
        	this.trahAndPickStackContainer.setId("trashAndPickStackContainer");
//        	this.gameBorderPane.setTop(this.trahAndPickStackContainer);
        	this.gameBorderPane.setRight(this.trahAndPickStackContainer);
            
            //Cards of current player
            this.gameCardContainer = new GameCardContainer(this.game, gameComponentSize.getScreenWidth() - gameTableSize - 100);
            this.gameCardContainer.setId("gameCardContainer");
            this.gameBorderPane.setBottom(this.gameCardContainer);
            
            
            // ******************** Left ********************
            //The game board
            this.gameBoardGridPane = new GameBoardGridPane(this.game);
            this.gameBoardGridPane.setId("gameBoardGridPane");
            this.boardContainer.getChildren().add(this.gameBoardGridPane);
            
            this.playersArc = new PlayerArc(this.game);
            this.playersArc.setId("playersArc");
            this.playersArc.refreshPlayersArcsAndCircles();
            this.boardContainer.getChildren().add(this.playersArc);
            
            //Create the goal card for the planCardAction
            this.goalCardContainer.setPrefSize(gameComponentSize.getGameTableSize(), gameComponentSize.getGameTableSize());
            for (int i = 0; i < 3; i++) {
            	ImageView img = new ImageView();
            	img.setFitWidth(gameComponentSize.getCardWidth()/1.5);
            	img.setFitHeight(gameComponentSize.getCardHeight()/1.5);
            	
            	StackPane p = new StackPane(img);
            	p.setAlignment(Pos.CENTER);
            	this.goalCardContainer.getChildren().add(p);
            }
            
            this.boardContainer.toBack();

            //Create the circle of the game board
            this.gameBoard.setCenterX(gameComponentSize.getCenterOfGameTable());
            this.gameBoard.setCenterY(gameComponentSize.getCenterOfGameTable());
            this.gameBoard.setRadius(gameComponentSize.getInnerRadiusOfArc()-10);

            this.changeLayout(pane);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    @FXML
    private void undoButtonAction(){
    	this.game.undo();
    	this.game.previousPlayer();
    	
    	while(this.game.getCurrentPlayer().isAI()){
    		this.game.undo();
        	this.game.previousPlayer();
    	}
    	
        this.gameBoardGridPane.generateBoard();
        this.playersArc.refreshPlayersArcsAndCircles();
        this.gameCardContainer.generateHandCardImage();
        this.gsm.changePeek("playerBeginOfTurn");
    	if(this.game.historyUndoIsEmpty()) {
        	this.undoButton.setDisable(true);
    	}
    }
    
    @FXML
    private void redoButtonAction(){
    	this.game.redo();
    	this.game.nextPlayer();    	
        this.gameBoardGridPane.generateBoard();
        this.playersArc.refreshPlayersArcsAndCircles();
        this.gameCardContainer.generateHandCardImage();
        this.gsm.changePeek("playerBeginOfTurn");
    	if(this.game.historyRedoIsEmpty()){
    		this.redoButton.setDisable(true);
    	}
    }
    
    @FXML
    private void pauseButtonAction(){
    	this.gsm.push("pauseMenu");
    }
    
}
