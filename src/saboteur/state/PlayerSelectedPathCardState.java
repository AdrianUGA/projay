package saboteur.state;

import java.util.LinkedList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;
import saboteur.tools.Icon;

public class PlayerSelectedPathCardState extends State{

	private GridPane boardGridPane;
	private List<Position> possiblePositionList;
	private List<SVGPath> svgBoardEffect;
	private List<ImageView> imgOfPossiblePosition;
	private PathCard selectedCard;
	
	
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
        double cardWidth = 108/3;
        double cardHeight = 166/3;
        
        this.selectedCard = (PathCard) param;
        this.possiblePositionList = this.game.getBoard().getPossiblePositionPathCard(this.selectedCard);
        this.boardGridPane = (GridPane) this.primaryStage.getScene().lookup("#boardGridPane");
        this.boardGridPane.toFront();
        this.svgBoardEffect = new LinkedList<>();
		this.imgOfPossiblePosition = new LinkedList<>();
        
		for(Position posiCard : possiblePositionList) {
			SVGPath svg = new SVGPath();
			svg.setFill(Color.GREEN);
			svg.setContent(Icon.plus);
			GridPane.setHalignment(svg, HPos.CENTER);
			
			ImageView img = new ImageView("/resources/cards/"+this.selectedCard.getFrontImage());			
			img.setFitHeight(cardHeight);
			img.setFitWidth(cardWidth);
			img.setOpacity(0);
			
			this.svgBoardEffect.add(svg);
			this.imgOfPossiblePosition.add(img);

			this.boardGridPane.add(svg, posiCard.getcX(), posiCard.getcY());
			this.boardGridPane.add(img, posiCard.getcX(), posiCard.getcY());
		}
        
        this.boardGridPane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				selectPositionOnBoard(event);
			}
		});
    }

    @Override
    public void onExit() {
		for(SVGPath obj : this.svgBoardEffect) {
			this.boardGridPane.getChildren().remove(obj);
		}
//		this.boardEffect = null;
    }
    
    private void selectPositionOnBoard(MouseEvent event) {
    	System.out.println(event.getSource());
    	System.out.println(event.getTarget());
    	
    	if(event.getTarget() instanceof ImageView) {
    		int i = 0;
        	for (ImageView img : this.imgOfPossiblePosition) {
        		if(event.getTarget() == img) {
//        			ImageView img = (ImageView) p.getChildren().get(0);
//        			Position posi = this.game.getBoard().getGoalCards().get(i);
//        			String name = this.game.getBoard().getCard(posi).getFrontImage();
//        			Image im = new Image("resources/cards/" + this.game.getBoard().getCard(posi).getFrontImage());
//        			img.setImage(im);
        		}
        		i++;
        	}
    	}
    	
    	System.out.println();
    }
    
}
