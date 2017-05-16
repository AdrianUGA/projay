package saboteur.view;

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

public class PlayerArc extends Pane{
	private Path path = new Path();
	private Circle circle[] = new Circle[3];

	public PlayerArc(double sizeOfArc, double center, double lengthOfArc, double startAngle) {
		
		this.setPadding(new Insets(0, 0, 0, 0));
		createArc(sizeOfArc, center, lengthOfArc, startAngle);
		
		double circleRadius = lengthOfArc / 1.5  ; // half of the length of arc
		
		double centerDistance = (sizeOfArc/2) + circleRadius*1.2; // game board size + circle radius
						
		double x =lengthOfArc/3;
		
		for (int i = 0; i<3; i++) {  
			double angle = -startAngle-x*i-x/2;
            this.circle[i] = new Circle(center + centerDistance, center, circleRadius, Color.web("blue", 0.5));
            Rotate rotate = new Rotate(angle, center, center);
            this.circle[i].getTransforms().add(rotate);
            this.circle[i].setStroke(Color.BLACK);
            this.circle[i].setStrokeWidth(3);
            this.getChildren().add(this.circle[i]);
		}		
	}
	
	private void createArc(double radius, double center, double lengthOfArc, double startAngle) {
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
                       
                
        this.path.setFill(Color.web("#a4a4a4"));
        this.path.setStroke(Color.BLACK);
        this.path.setStrokeWidth(2);
        this.path.setStrokeType(StrokeType.OUTSIDE);
        this.path.setFillRule(FillRule.EVEN_ODD);
        
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
        
        this.path.getElements().add(moveTo);
        this.path.getElements().add(arcToInner);
        this.path.getElements().add(moveTo2);
        this.path.getElements().add(line);
        this.path.getElements().add(arcTo);
        this.path.getElements().add(line2);

        this.getChildren().add(this.path);
	}
	
	public Circle[] getCircle() {
		return circle;
	}
}
