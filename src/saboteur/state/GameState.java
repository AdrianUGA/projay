package saboteur.state;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import saboteur.view.PlayerRoleContainer;
import saboteur.view.TrashAndPickStackContainer;
import saboteur.view.UndoRedoButtonContainer;

public class GameState extends State{
	
	@FXML private BorderPane gameBorderPane;
	@FXML private Pane mainContainer;
	@FXML private VBox goalCardContainer;
	@FXML private HBox menuButtonContainer;
	
	private FXMLLoader loader;
	
	private PlayerArc playersArc;
	private GameBoardGridPane gameBoardGridPane;
	private GameCardContainer gameCardContainer;
	private TrashAndPickStackContainer trahAndPickStackContainer;
	private PlayerRoleContainer playerRoleContainer;
	private UndoRedoButtonContainer undoRedoButtonContainer;


    public GameState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {
        if (this.game.roundIsFinished()){
            this.gsm.push("roundIsFinished");
        } else{
            this.undoRedoButtonContainer.manageUndoRedoButton();
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
                       
            GameComponentsSize gameComponentsSize = GameComponentsSize.getGameComponentSize();
            
            // ******************** Left ********************
            this.playersArc = new PlayerArc(this.game);
            this.playersArc.setId("playersArc");
            this.playersArc.refreshPlayersArcsAndCircles();
            this.mainContainer.getChildren().add(this.playersArc);
            
            //The game board
            this.gameBoardGridPane = new GameBoardGridPane(this.game);
            this.gameBoardGridPane.setId("gameBoardGridPane");
            this.gameBoardGridPane.toBack();
            this.mainContainer.getChildren().add(this.gameBoardGridPane);
            
            //Create the goal card for the planCardAction
            this.goalCardContainer.setPrefSize(gameComponentsSize.getGameTableSize(), gameComponentsSize.getGameTableSize());
            for (int i = 0; i < 3; i++) {
            	ImageView img = new ImageView();
            	img.setFitWidth(gameComponentsSize.getCardWidth()/1.5);
            	img.setFitHeight(gameComponentsSize.getCardHeight()/1.5);
            	
            	StackPane p = new StackPane(img);
            	p.setAlignment(Pos.CENTER);
            	this.goalCardContainer.getChildren().add(p);
            }
            
            // ******************** Right ********************
            double margin = gameComponentsSize.getDefaultMargin();
                        
            //Cards of current player
            this.gameCardContainer = new GameCardContainer(this.game, gameComponentsSize.getScreenWidth() - gameComponentsSize.getGameTableSize() - margin);
            this.gameCardContainer.setId("gameCardContainer");
            this.gameBorderPane.setBottom(this.gameCardContainer);
            BorderPane.setMargin(this.gameCardContainer, new Insets(0.0, margin, margin, 0.0));
            
        	//Image and Label of player role
        	this.playerRoleContainer = new PlayerRoleContainer(this.game);
        	this.playerRoleContainer.setId("playerRoleContainer");
        	this.gameBorderPane.setCenter(this.playerRoleContainer);
            BorderPane.setMargin(this.playerRoleContainer, new Insets(0.0, margin, 0.0, 0.0));
        	
            // trash and pick stacks
        	this.trahAndPickStackContainer = new TrashAndPickStackContainer();
        	this.trahAndPickStackContainer.setId("trashAndPickStackContainer");
        	this.trahAndPickStackContainer.setAlignment(Pos.CENTER_RIGHT);
            
            //undo redo button action
            this.undoRedoButtonContainer = new UndoRedoButtonContainer(this.game);
            this.undoRedoButtonContainer.setId("undoRedoButtonContainer");
            this.undoRedoButtonContainer.setUndoButtonAction(event -> undoButtonAction());
            this.undoRedoButtonContainer.setRedoButtonAction(event -> redoButtonAction());
            this.undoRedoButtonContainer.setAlignment(Pos.CENTER_LEFT);
            
            AnchorPane stackAndButtonContainer = new AnchorPane(this.undoRedoButtonContainer, this.trahAndPickStackContainer);
            AnchorPane.setRightAnchor(this.trahAndPickStackContainer, 30.0);
            AnchorPane.setTopAnchor(this.trahAndPickStackContainer, 30.0);
            AnchorPane.setLeftAnchor(this.undoRedoButtonContainer, 0.0);
            AnchorPane.setTopAnchor(this.undoRedoButtonContainer, 30.0);
            this.gameBorderPane.setTop(stackAndButtonContainer);
           
            this.gameBorderPane.setLayoutX(gameComponentsSize.getGameTableSize());
            this.gameBorderPane.setPrefHeight(gameComponentsSize.getScreenHeight());
            
            // ******************** end ********************
            this.menuButtonContainer.toFront();
            pane.setMaxHeight(gameComponentsSize.getScreenHeight());
            pane.setMaxWidth(gameComponentsSize.getScreenWidth());
                        
            this.changeLayout(pane);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void undoButtonAction() {
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
        	this.undoRedoButtonContainer.disableUndoButton();
    	}
    }
    
    private void redoButtonAction() {
    	this.game.redo();
    	this.game.nextPlayer();    	
        this.gameBoardGridPane.generateBoard();
        this.playersArc.refreshPlayersArcsAndCircles();
        this.gameCardContainer.generateHandCardImage();
        this.gsm.changePeek("playerBeginOfTurn");
    	if(this.game.historyRedoIsEmpty()){
    		this.undoRedoButtonContainer.disableRedoButton();
    	}
    }
        
    @FXML
    private void pauseButtonAction(){
    	this.gsm.push("pauseMenu");
    }
}
