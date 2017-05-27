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

	private double miniCircleRadius;
	private double innerRadiusOfArc;
	private double playerArcRadius;
	
	private final double addToSize = 40;
	private final int defaultMargin = 30;
	
	private static GameComponentsSize size;
	
	private GameComponentsSize(){
		this.primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.gameTableSize = primaryScreenBounds.getHeight() + this.addToSize;
        this.gameTableHalfSize = gameTableSize/2;
        
        this.middleCircleRadius = gameTableHalfSize/2;
        this.centerOfGameTable = gameTableSize/2;
        
        this.miniCircleRadius = this.gameTableHalfSize/15;
        this.innerRadiusOfArc = this.gameTableHalfSize - this.miniCircleRadius*3 - this.defaultMargin;
        this.playerArcRadius = this.gameTableHalfSize - this.defaultMargin;
        
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
		return this.primaryScreenBounds.getHeight() + this.addToSize;
	}
	
	public double getScreenWidth(){
		return this.primaryScreenBounds.getWidth() + this.addToSize;
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
	
	
	public double getMiniCircleRadius(){
		return this.miniCircleRadius;
	}
	
	public double getInnerRadiusOfArc(){
		return this.innerRadiusOfArc;
	}
	
	public double getPlayerArcRadius(){
		return this.playerArcRadius;
	}
	
	
	public double getCardHeight() {
		return this.cardHeight;
	}

	public double getCardWidth() {
		return this.cardWidth;
	}

	public int getDefaultMargin() {
		return defaultMargin;
	}

}
