package saboteur.view;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.text.LabelView;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.model.Card.SabotageCard;
import saboteur.model.Card.Tool;
import saboteur.tools.GameComponentsSize;

public class PlayerArc extends Pane{
	private LinkedList<Player> players;
	private Hashtable<Player, Path> playerArc = new Hashtable<Player, Path>();
	private Hashtable<Player, Circle[]> playerCircles = new Hashtable<Player, Circle[]>();
	private Player previousPlayer;
	private Game game;
	private GameComponentsSize gameComponentsSize;
	private final static Color color = Color.web("#a4a4a4");
	private final static Color colorCurrentPlayer = Color.web("#72e307");

	public PlayerArc(Game game) {
		this.gameComponentsSize = GameComponentsSize.getGameComponentSize();
		this.game = game;
		this.players = this.game.getPlayerList();
		this.setPadding(new Insets(0, 0, 0, 0));
		
		int nbPlayers = this.game.getPlayerList().size();
		for(Player p : this.players) {
			playerCircles.put(p, new Circle[3]);
		}
		
		//Arc of players
		double spacing = 1.5;
        double lengthOfArc = (360.0 - nbPlayers * spacing) / nbPlayers;
        double startAngle = 0.0;
        for(Player player : this.players) {
        	startAngle = startAngle - lengthOfArc - spacing;
			createArc(lengthOfArc, startAngle, player);
			createCircle(lengthOfArc, startAngle, player);
        }
	}
	
	public void refreshPlayersArcsAndCircles(){
		if(this.previousPlayer == null){
			this.previousPlayer = this.game.getCurrentPlayer();
		}
		else{
			Path path = this.playerArc.get(this.previousPlayer);
			path.setFill(PlayerArc.color);
			this.previousPlayer = this.game.getCurrentPlayer();
		}
		
		Player currentPlayer = this.game.getCurrentPlayer();
		Path path = this.playerArc.get(currentPlayer);
		path.setFill(PlayerArc.colorCurrentPlayer);
		
		
		for(Player player : this.players) {
			for(int circle = 0; circle < 3; circle++) {
				this.refreshCircles(this.playerCircles.get(player)[circle], circle, false);
			}
		}
		
		for(Player player : this.players) {
			ArrayList<SabotageCard> sabotage = player.getHandicaps();
			for(SabotageCard sabotageCard : sabotage) {
		    	Tool tool = sabotageCard.getSabotageType();
		    	this.refreshCircles(this.playerCircles.get(player)[tool.getValue()], tool.getValue(), true);
			}
		}
	}
	
	public void refreshCircles(Circle circle, int toolValue, boolean isSabotage) {
		String picto = "/resources/picto/";
		
		if(isSabotage){
			picto = picto + "broken_";
		}
		
    	switch(toolValue) {
    		case 0 : 
            	picto = picto + "pickaxe_picto.png";
            	break;
    		case 1 : 
    			picto = picto + "lantern_picto.png";
            	break;
    		case 2 : 
    			picto = picto + "cart_picto.png";
            	break;
    	}        	
		Image img = new Image(picto);
    	circle.setFill(new ImagePattern(img));
	}
	
	private void createCircle(double lengthOfArc, double startAngle, Player player) {
		double radius = this.gameComponentsSize.getPlayerArcRadius();
		double center = this.gameComponentsSize.getCenterOfGameTable();
		
		double circleRadius = this.gameComponentsSize.getMiniCircleRadius();
		double centerDistance = radius - circleRadius*1.5;
		double x =lengthOfArc/3.5;
		double y = (lengthOfArc - x*3)/2;
		
		for (int i = 0; i<3; i++) {  
			double angle = -startAngle-x*i-x/2-y;
			this.playerCircles.get(player)[i] = new Circle(center + centerDistance, center, circleRadius, PlayerArc.color);
            Rotate rotate = new Rotate(angle, center, center);
            this.playerCircles.get(player)[i].getTransforms().add(rotate);
            this.playerCircles.get(player)[i].setStroke(Color.BLACK);
            this.playerCircles.get(player)[i].setStrokeWidth(3);
            this.getChildren().add(this.playerCircles.get(player)[i]);
		}		
	}
	
	private void createArc(double lengthOfArc, double startAngle, Player player) {
		
		Path path = new Path();
		
		//Prepare all different positions 
		double radius = this.gameComponentsSize.getPlayerArcRadius();
		double center = this.gameComponentsSize.getCenterOfGameTable();
		
		double innerRadius = this.gameComponentsSize.getInnerRadiusOfArc();
        double radians = Math.toRadians(startAngle);
        double XstartOuter = (int)Math.round((Math.cos(radians) * radius + center));
        double YstartOuter = (int)Math.round((Math.sin(-radians)* radius + center));
        double XstartInner = (int)Math.round((Math.cos(radians) * innerRadius + center));
        double YstartInner = (int)Math.round((Math.sin(-radians) * innerRadius + center));
		
        radians = Math.toRadians(startAngle + lengthOfArc);
        double XendOuter = (int)Math.round((Math.cos(radians) * radius + center));
        double YendOuter = (int)Math.round((Math.sin(-radians) * radius + center));
        double XendInner = (int)Math.round((Math.cos(radians) * innerRadius + center));
        double YendInner = (int)Math.round((Math.sin(-radians) * innerRadius + center));
                       
                
        path.setFill(PlayerArc.color);
        path.setStroke(Color.BLACK);
        path.setStrokeWidth(2);
        path.setStrokeType(StrokeType.OUTSIDE);
        path.setFillRule(FillRule.EVEN_ODD);
        
        MoveTo moveTo = new MoveTo(XstartInner, YstartInner);

        ArcTo arcToInner = new ArcTo();
        arcToInner.setX(XendInner);
        arcToInner.setY(YendInner);
        arcToInner.setRadiusX(innerRadius);
        arcToInner.setRadiusY(innerRadius);

        MoveTo moveTo2 = new MoveTo(XstartInner, YstartInner);
        
        LineTo line = new LineTo(XstartOuter, YstartOuter);
        
        ArcTo arcTo = new ArcTo();
        arcTo.setX(XendOuter);
        arcTo.setY(YendOuter);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);

        LineTo line2 = new LineTo(XendInner, YendInner);
        
        path.getElements().add(moveTo);
        path.getElements().add(arcToInner);
        path.getElements().add(moveTo2);
        path.getElements().add(line);
        path.getElements().add(arcTo);
        path.getElements().add(line2);

        this.playerArc.put(player, path);
        this.getChildren().add(path);
        
        Label name = new Label(player.getName());
        double anglePosition = startAngle + lengthOfArc/2;
        radians = Math.toRadians(anglePosition);
        anglePosition = Math.abs(anglePosition);
        
        double distanceToCenter = radius;
       
        if( 0 <= anglePosition && anglePosition < 40 ) {
        	distanceToCenter-=20;
        }
        else if( 40 <= anglePosition && anglePosition < 70 ) {
        	distanceToCenter-=10;
        }
        else if( 70 <= anglePosition && anglePosition < 100 ) {
        	distanceToCenter+=5;
        }
        else if( 100 <= anglePosition && anglePosition < 110 ) {
        	distanceToCenter+=20;
        }
        else if( 110 <= anglePosition && anglePosition < 130 ) {
        	distanceToCenter+=30;
        }
        else if( 130 <= anglePosition && anglePosition < 180 ) {
        	distanceToCenter+=20;
        }
        else if( 180 <= anglePosition && anglePosition < 270 ) {
        	distanceToCenter+=40;
        }
        else if( 270 <= anglePosition && anglePosition < 300 ) {
        	distanceToCenter+=25;
        }
        else if( 300 <= anglePosition && anglePosition < 325 ) {
        	distanceToCenter+=15;
        }
        XstartInner = (int)Math.round((Math.cos(radians) * distanceToCenter + center));
        YstartInner = (int)Math.round((Math.sin(-radians) * distanceToCenter + center));
        
        name.setFont(new Font("Arial", 20));
        name.setTextFill(Color.WHITE);
        name.setLayoutX(XstartInner);
        name.setLayoutY(YstartInner);
        
        double angle = Math.abs(anglePosition)%180 - 90;
        name.setRotate(angle);
        
        this.getChildren().add(name);
	}
	
	public Circle[] getCircles(Player player) {
		return playerCircles.get(player);
	}
}
