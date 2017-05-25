package saboteur.tools;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class GameComponentsSize {

	private Rectangle2D primaryScreenBounds;

	private double gameTableSize;
	private double gameTableHalfSize;
	private double middleCircleRadius;
	private double centerOfGameTable;

	private double cardHeight;
	private double cardWidth;
	
	private static GameComponentsSize size;
	
	private GameComponentsSize(){
		this.primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.gameTableSize = primaryScreenBounds.getHeight();
        this.gameTableHalfSize = gameTableSize/2;
        this.middleCircleRadius = gameTableHalfSize/2;
        this.centerOfGameTable = gameTableSize/2;
        
        this.cardHeight = 166.0;
        this.cardWidth = 108.0;
	}
	
	public static GameComponentsSize getGameComponentSize(){
		if (size == null){
			size = new GameComponentsSize();
		}
		return size;
	}
	
	public double getScreenHeight(){
		return this.primaryScreenBounds.getHeight();
	}
	
	public double getScreenWidth(){
		return this.primaryScreenBounds.getWidth();
	}
	
	public double getGameTableSize(){
		return this.gameTableSize;
	}
	
	public double getGameTableHalfSize(){
		return this.gameTableHalfSize;
	}
	
	public double getMiddleCircleRadius(){
		return this.middleCircleRadius;
	}
	
	public double getCenterOfGameTable(){
		return this.centerOfGameTable;
	}
	
	public double getCardHeight() {
		return this.cardHeight;
	}

	public double getCardWidth() {
		return this.cardWidth;
	}

}
