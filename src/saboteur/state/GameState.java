package saboteur.state;

import java.io.IOException;
import java.util.LinkedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.PlayerArc;

public class GameState extends State{

	@FXML private AnchorPane boardAndCardContainer;
	@FXML private Pane boardContainer;
	@FXML private Circle gameBoard;
	@FXML private VBox goalCardContainer;

	private FXMLLoader loader;
	
	private PlayerArc playersArc;
	
	private GameBoardGridPane gameBoardGridPane;
	private GameCardContainer cardContainer;

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
                  this.gsm.push("playerEndOfTurn");
                }
                else{
                    System.out.println("je suis humain");
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
        
        this.game.initAI();
        
        try{
            this.loader = new FXMLLoader();
            this.loader.setLocation(App.class.getResource("/saboteur/view/boardGame.fxml"));
            this.loader.setController(this);
            Pane pane = this.loader.load();
            
            //Take size of screen
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            double gameTableSize = primaryScreenBounds.getHeight();
            double gameTableHalfSize = gameTableSize/2;
            double boardSize = gameTableHalfSize/2;
         
            //Create the goal card for the planCardActcion
            this.goalCardContainer.setPrefSize(gameTableSize, gameTableSize);
            for (int i = 0; i < 3; i++) {
            	ImageView img = new ImageView();
            	img.setFitWidth(108/1.5);
            	img.setFitHeight(166/1.5);
            	
            	StackPane p = new StackPane(img);
            	p.setAlignment(Pos.CENTER);
            	this.goalCardContainer.getChildren().add(p);
            }
            
            //For center cards hand Image
            this.cardContainer = new GameCardContainer(this.game, gameTableSize);
            this.cardContainer.setId("cardContainer");
            this.boardAndCardContainer.getChildren().add(this.cardContainer);
            
            //The game board
            double innerRadius = gameTableHalfSize/2;
	        double radians = Math.toRadians(135);
	        double XstartInner = (int)Math.round((Math.cos(radians) * innerRadius + gameTableHalfSize));
	        double YstartInner = (int)Math.round((Math.sin(-radians) * innerRadius + gameTableHalfSize));
            this.gameBoardGridPane = new GameBoardGridPane(this.game, XstartInner, YstartInner);
            this.gameBoardGridPane.setId("gameBoardGridPane");
            this.boardContainer.getChildren().add(this.gameBoardGridPane);
            
            this.playersArc = new PlayerArc(gameTableHalfSize, gameTableHalfSize, this.game.getPlayerList());
            this.playersArc.setId("playersArc");
            this.boardContainer.getChildren().add(this.playersArc);

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

}
