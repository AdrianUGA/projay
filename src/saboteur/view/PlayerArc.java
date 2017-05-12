package saboteur.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeType;

public class PlayerArc extends Arc{
	
	public PlayerArc(double sizeAndCenter, double length, double startAngle) {
		 this.setRadiusX(sizeAndCenter);
         this.setRadiusY(sizeAndCenter);
         this.setCenterX(sizeAndCenter);
         this.setCenterY(sizeAndCenter);
         this.setStartAngle(startAngle);
         this.setLength(length);
         this.setFill(Color.web("#a4a4a4"));
         this.setStrokeWidth(2);
         this.setStroke(Color.BLACK);
         this.setType(ArcType.ROUND);
         this.setStrokeType(StrokeType.OUTSIDE);
	}
}
