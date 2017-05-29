package saboteur.state;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Card.PathCard;
import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.Position;
import saboteur.tools.Icon;
import saboteur.tools.Resources;
import saboteur.view.GameBoardGridPane;
import saboteur.view.GameCardContainer;
import saboteur.view.TrashAndPickStackContainer;
import saboteur.view.PlayerArc;

public class PlayerSelectedPlanCardState extends State{

	private Object[] svgEyes = new Object[3];
	private StackPane[] paneOfGoalCard = new StackPane[3];
	private VBox goalCardContainer;
	private TrashAndPickStackContainer trashAndPickStackContainer;
	private boolean goalCardSelect;

	private PathCard selectedGoalCard;
	private GameCardContainer gameCardContainer;
	private GameBoardGridPane gameBoardGridPane;
	private PlayerArc playersArc;

	public PlayerSelectedPlanCardState(GameStateMachine gsm, Game game, Stage primaryStage){
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

		this.playersArc = (PlayerArc) this.primaryStage.getScene().lookup("#playersArc");
		this.playersArc.refreshPlayersArcsAndCircles();
		this.gameCardContainer = (GameCardContainer) this.primaryStage.getScene().lookup("#gameCardContainer");
        this.goalCardContainer = (VBox) this.primaryStage.getScene().lookup("#goalCardContainer");
    	this.trashAndPickStackContainer = (TrashAndPickStackContainer) this.primaryStage.getScene().lookup("#trashAndPickStackContainer");
    	this.gameBoardGridPane = (GameBoardGridPane) this.primaryStage.getScene().lookup("#gameBoardGridPane");
        this.goalCardSelect = false;
        
        
        this.playersArc.circleToFront();
        this.goalCardContainer.toFront();
    	this.goalCardContainer.setVisible(true);
    	this.gameBoardGridPane.setVisible(false);
    	
    	
    	
        //Put verso of goal card on created ImageView, create eye svg and for each MouseClick event.
        int i = 0;
    	for (Node n : this.goalCardContainer.getChildren()) {
    		this.paneOfGoalCard[i] = (StackPane) n;
    		
    		//Change Image
    		ImageView img = (ImageView) this.paneOfGoalCard[i].getChildren().get(0);
        	img.setImage(new Image("/resources/cards/goal_card_verso.png"));
        	
        	//Add SVG
        	SVGPath svg = new SVGPath();
        	svg.setFill(Color.WHITE);
        	svg.setContent(Icon.eye);
        	this.svgEyes[i] = svg;
        	this.paneOfGoalCard[i].getChildren().add(svg);
        	
        	//Add Action
        	this.paneOfGoalCard[i].setOnMouseClicked(event -> selectGoalCard(event));
        	i++;        	
    	}
    }

    @Override
    public void onExit() {
		this.trashAndPickStackContainer.disablePickAndEndTurnButton();
		this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(null);

    	//Delete SVG and Mouse event
    	if(!this.goalCardSelect) {
	    	for(int i = 0; i < 3; i++){
	    		this.paneOfGoalCard[i].getChildren().remove(this.svgEyes[i]);
	    		this.paneOfGoalCard[i].setOnMouseClicked(null);
	    	}

	        this.playersArc.circleToBack();
			this.goalCardContainer.setVisible(false);
			this.gameBoardGridPane.setVisible(true);
    	}
    }
    
    private void selectGoalCard(MouseEvent event) {
		int i = 2;
		for (Node n : this.goalCardContainer.getChildren()) {
			StackPane p = (StackPane) n;
			
			//Turn selected card
			if(event.getSource() == p) {
				ImageView img = (ImageView) p.getChildren().get(0);
				Position posi = this.game.getBoard().getGoalCards().get(i%3);
				img.setImage( Resources.getImage().get(this.game.getBoard().getCard(posi).getFrontImage()) );
				this.selectedGoalCard = this.game.getBoard().getCard(posi);
				this.beforEnd();
				this.goalCardSelect = true;
				break;
			}
			i++;
		}
    }
    
    private void beforEnd() {
		this.gameCardContainer.setOnMouseClicked(null);
    	
    	for(int i = 0; i < 3; i++){
    		this.paneOfGoalCard[i].getChildren().remove(this.svgEyes[i]);
    		this.paneOfGoalCard[i].setOnMouseClicked(null);
    	}

    	this.trashAndPickStackContainer.disableTrashButton();
		this.trashAndPickStackContainer.setEventToTrashButton(null);
    	this.trashAndPickStackContainer.enablePickAndEndTurnButton();
    	this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(e -> endOfTurn());
    }
    
    private void endOfTurn() {
        this.playersArc.toBack();
		this.goalCardContainer.setVisible(false);
		
		this.gameBoardGridPane.setVisible(true);
    	this.trashAndPickStackContainer.setEventToPickAndEndTurnButton(null);
    	
    	Operation op = this.game.getCurrentPlayer().playCard(this.selectedGoalCard);
    	this.gsm.changePeek("playerPlayCard", op);
	}
}
