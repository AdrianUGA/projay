package saboteur.view;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import javafx.geometry.Insets;
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
import javafx.scene.transform.Rotate;
import saboteur.model.Game;
import saboteur.model.Player;
import saboteur.model.Card.SabotageCard;
import saboteur.model.Card.Tool;

public class PlayerArc extends Pane{
	private LinkedList<Player> players;
	private Hashtable<Player, Path> playerArc = new Hashtable<Player, Path>();
	private Hashtable<Player, Circle[]> playerCircles = new Hashtable<Player, Circle[]>();
	private Player previousPlayer;
	private Game game;
	private final static Color color = Color.web("#a4a4a4");
	private final static Color colorCurrentPlayer = Color.web("#72e307");

	public PlayerArc(Game game, double radius, double center) {
		this.game = game;
		this.players = this.game.getPlayerList();
		this.setPadding(new Insets(0, 0, 0, 0));
		
		int nbPlayers = this.game.getPlayerList().size();
		for(Player p : this.players) {
			playerCircles.put(p, new Circle[3]);
		}
		
		//Arc of players
        double lengthOfArc = 360.0 / nbPlayers;
        double startAngle = 0.0;	
        for(Player player : this.players) {
        	startAngle = startAngle - lengthOfArc;
			createArc(radius, center, lengthOfArc, startAngle, player);
			createCircle(radius, center, lengthOfArc, startAngle, player);
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
	
	private void createCircle(double radius, double center, double lengthOfArc, double startAngle, Player player) {
		double circleRadius = radius / 12  ; // half of the length of arc
		double centerDistance = radius - circleRadius*1.2; // game board size + circle radius
		double x =lengthOfArc/3;
		
		for (int i = 0; i<3; i++) {  
			double angle = -startAngle-x*i-x/2;
			this.playerCircles.get(player)[i] = new Circle(center + centerDistance, center, circleRadius, PlayerArc.color);
            Rotate rotate = new Rotate(angle, center, center);
            this.playerCircles.get(player)[i].getTransforms().add(rotate);
            this.playerCircles.get(player)[i].setStroke(Color.BLACK);
            this.playerCircles.get(player)[i].setStrokeWidth(3);
            this.getChildren().add(this.playerCircles.get(player)[i]);
		}		
	}
	
	private void createArc(double radius, double center, double lengthOfArc, double startAngle, Player player) {
		
		Path path = new Path();
		
		//Prepare all different positions 
		double innerRadius = radius/1.5;
        double radians = Math.toRadians(startAngle);
        double XstartOuter = (int)Math.round((Math.cos(radians) * radius + center));
        double YstartOuter = (int)Math.round((Math.sin(-radians)* radius + center));
        double XstartInner = (int)Math.round((Math.cos(radians) * innerRadius + center));
        double YstartInner = (int)Math.round((Math.sin(-radians) * innerRadius + center));
		
        radians = Math.toRadians(startAngle + lengthOfArc);
        int XendOuter = (int)Math.round((Math.cos(radians) * radius + center));
        int YendOuter = (int)Math.round((Math.sin(-radians) * radius + center));
        int XendInner = (int)Math.round((Math.cos(radians) * innerRadius + center));
        int YendInner = (int)Math.round((Math.sin(-radians) * innerRadius + center));
                       
                
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
	}
	
	public Circle[] getCircles(Player player) {
		return playerCircles.get(player);
	}
}
