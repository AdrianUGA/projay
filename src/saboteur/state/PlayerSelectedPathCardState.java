package saboteur.state;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;

public class PlayerSelectedPathCardState extends State{

	private GridPane boardGridPane;
	
    public PlayerSelectedPathCardState(GameStateMachine gsm, Game game, Stage primaryStage){
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
        System.out.println("path card");
        PathCard card = (PathCard) param;
        List<Position> possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(card);
        this.boardGridPane = (GridPane) this.primaryStage.getScene().lookup("#boardGridPane");
        this.boardGridPane.toFront();
        this.boardGridPane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		});
    }

    @Override
    public void onExit() {

    }
    
    private void selectPositionOnBoard(MouseEvent event) {
    	System.out.println(event.getSource());
    	System.out.println(event.getTarget());
    	System.out.println();
    }
    
}
