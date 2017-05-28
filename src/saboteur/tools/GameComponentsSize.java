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
	
	private final double addToSize = 40.0;
	private final double defaultMargin = 30.0;
	private final double placeForName = 30.0;
	private final double defaultSpacing = 20.0;

	private double gameBorderPaneHeight;
	private double gameBorderPaneWidth;
	
	private double gameCardContainerWidth;

	private double layoutXOfTrashStack;
	private double layoutYOfTrashStack;

	private double layoutXOfPickStack;
	private double layoutYOfPickStack;
	
	private static GameComponentsSize size;
	
	private GameComponentsSize(){
		//screen and board
		this.primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.gameTableSize = primaryScreenBounds.getHeight() + this.addToSize;
        this.gameTableHalfSize = gameTableSize/2.0;
        
        //circles and arc
        this.middleCircleRadius = gameTableHalfSize/2.0;
        this.centerOfGameTable = gameTableSize/2.0;
        
        this.miniCircleRadius = this.gameTableHalfSize/15.0;
        this.innerRadiusOfArc = this.gameTableHalfSize - this.miniCircleRadius*3.0 - this.defaultMargin - this.placeForName;
        this.playerArcRadius = this.gameTableHalfSize - this.defaultMargin - this.placeForName;
        
        //cards
        this.cardHeight = 166.0;
        this.cardWidth = 108.0;
        
        //gameBorderPane
        this.gameBorderPaneHeight = this.getScreenHeight();
        this.gameBorderPaneWidth = this.getScreenWidth()-this.gameTableSize;
        
        //gameCardContainer
        this.gameCardContainerWidth = this.getScreenWidth() - this.gameTableSize - this.defaultMargin;
        
        //X and Y of trash stack
        this.layoutXOfTrashStack = this.gameTableSize + this.gameBorderPaneWidth - this.defaultMargin - this.cardWidth;
        this.layoutYOfTrashStack = this.defaultMargin;
        
        //X and Y of pick stack
        this.layoutXOfPickStack = this.gameTableSize + this.gameBorderPaneWidth - this.defaultMargin - this.cardWidth;
        this.layoutYOfPickStack = this.defaultMargin + this.cardHeight + this.defaultSpacing;
         
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

	public double getDefaultMargin() {
		return defaultMargin;
	}

	public double getDefaultSpacing() {
		return defaultSpacing;
	}
	
	public double getGameCardContainerWidth() {
		return gameCardContainerWidth;
	}

	public double getGameBorderPaneHeight() {
		return gameBorderPaneHeight;
	}

	public double getGameBorderPaneWidth() {
		return gameBorderPaneWidth;
	}
	
	public double getLayoutXOfTrashStack() {
		return layoutXOfTrashStack;
	}

	public double getLayoutYOfTrashStack() {
		return layoutYOfTrashStack;
	}

	public double getLayoutXOfPickStack() {
		return layoutXOfPickStack;
	}

	public double getLayoutYOfPickStack() {
		return layoutYOfPickStack;
	}
}
