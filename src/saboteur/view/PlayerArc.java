package saboteur.view;

import java.util.Hashtable;
import java.util.LinkedList;

import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import saboteur.model.Player;

public class PlayerArc extends Pane{
	private LinkedList<Player> players;
//	private Circle circles[][];
	private Hashtable<Player, Circle[]> circles = new Hashtable<Player, Circle[]>();

	public PlayerArc(double radius, double center, LinkedList<Player> playerslist) {
		
		this.players = playerslist;
		this.setPadding(new Insets(0, 0, 0, 0));
		
		int nbPlayers = playerslist.size();
		for(Player p : playerslist) {
			circles.put(p, new Circle[3]);
		}
		
		
		//Arc of 1st player
		createArc(radius, center, 100, -140); 
		createCircle(radius, center, 100, -140, this.players.get(0));
		
		//Arc of other players
        double lengthOfArc = 260.0 / (nbPlayers-1); // 260 = 360 - 100 (100 degree of the 1st player)
        double startAngle = -140.0;		
        for (int i = 1; i<nbPlayers ; i++) {
            startAngle = startAngle - lengthOfArc;
			createArc(radius, center, lengthOfArc, startAngle);
			createCircle(radius, center, lengthOfArc, startAngle, this.players.get(i));
        }
	}
	
	private void createCircle(double radius, double center, double lengthOfArc, double startAngle, Player player) {
		double circleRadius = lengthOfArc / 1.5  ; // half of the length of arc
		double centerDistance = (radius/2) + circleRadius*1.2; // game board size + circle radius
		double x =lengthOfArc/3;
		
		for (int i = 0; i<3; i++) {  
			double angle = -startAngle-x*i-x/2;
			this.circles.get(player)[i] = new Circle(center + centerDistance, center, circleRadius, Color.web("#a4a4a4"));
            Rotate rotate = new Rotate(angle, center, center);
            this.circles.get(player)[i].getTransforms().add(rotate);
            this.circles.get(player)[i].setStroke(Color.BLACK);
            this.circles.get(player)[i].setStrokeWidth(3);
            this.getChildren().add(this.circles.get(player)[i]);
		}		
	}
	
	private void createArc(double radius, double center, double lengthOfArc, double startAngle) {
		
		Path path = new Path();
		
		//Prepare all different positions 
		double innerRadius = radius/2;
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
                       
                
        path.setFill(Color.web("#a4a4a4"));
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

        this.getChildren().add(path);
	}
	
	public Circle[] getCircles(Player player) {
		return circles.get(player);
	}
}
