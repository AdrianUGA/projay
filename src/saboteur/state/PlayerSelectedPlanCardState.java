package saboteur.state;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.model.Position;
import saboteur.tools.Icon;

public class PlayerSelectedPlanCardState extends State{

	private Object[] svgEyes= new Object[3];
	private VBox goalCardContainer;
	
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
        System.out.println("plan card");
        this.goalCardContainer = (VBox) this.primaryStage.getScene().lookup("#goalCardContainer");
        int i = 0;
    	for (Node n : this.goalCardContainer.getChildren()) {
    		StackPane p = (StackPane) n;
    		ImageView img = (ImageView) p.getChildren().get(0);
        	img.setImage(new Image("/resources/cards/goal_card_verso.png"));
        	SVGPath svg = new SVGPath();
        	this.svgEyes[i] = svg;
        	svg.setFill(Color.WHITE);
        	svg.setContent(Icon.eye);
        	p.getChildren().add(svg);
        	
        	p.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					selectGoalCard(event);
				}
			});
        	i++;        	
    	}
    }

    @Override
    public void onExit() {

    }
    
    private void selectGoalCard(MouseEvent event) {
    	if(event.getTarget() instanceof ImageView || event.getTarget() instanceof SVGPath) {
    		int i = 0;
        	for (Node n : this.goalCardContainer.getChildren()) {
        		StackPane p = (StackPane) n;
        		p.getChildren().remove(svgEyes[i]);
        		
        		if(event.getSource() == p) {
        			ImageView img = (ImageView) p.getChildren().get(0);
        			Position posi = this.game.getBoard().getGoalCards().get(i);
        			String name = this.game.getBoard().getCard(posi).getFrontImage();
        			Image im = new Image("resources/cards/" + this.game.getBoard().getCard(posi).getFrontImage());
        			img.setImage(im);
        		}
        		i++;
        	}
    	}
    }
}
