package saboteur.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class NewPlayerHBox {
	
	private String svgpc = "M6.5 20c-1.375 0-2.5-1.125-2.5-2.5v-11c0-1.375 1.125-2.5 2.5-2.5h17c1.375 0 2.5 1.125 2.5 2.5v11c0 1.375-1.125 2.5-2.5 2.5h-17zM6 6.5v11c0 0.266 0.234 0.5 0.5 0.5h17c0.266 0 0.5-0.234 0.5-0.5v-11c0-0.266-0.234-0.5-0.5-0.5h-17c-0.266 0-0.5 0.234-0.5 0.5zM27.5 21h2.5v1.5c0 0.828-1.125 1.5-2.5 1.5h-25c-1.375 0-2.5-0.672-2.5-1.5v-1.5h27.5zM16.25 22.5c0.141 0 0.25-0.109 0.25-0.25s-0.109-0.25-0.25-0.25h-2.5c-0.141 0-0.25 0.109-0.25 0.25s0.109 0.25 0.25 0.25h2.5z";
	private String svguse = "M18.766 12.25c2.203 0.641 5.234 2.812 5.234 9.922 0 3.219-2.391 5.828-5.328 5.828h-13.344c-2.938 0-5.328-2.609-5.328-5.828 0-7.109 3.031-9.281 5.234-9.922-0.781-1.234-1.234-2.688-1.234-4.25 0-4.406 3.594-8 8-8s8 3.594 8 8c0 1.563-0.453 3.016-1.234 4.25zM12 2c-3.313 0-6 2.688-6 6s2.688 6 6 6 6-2.688 6-6-2.688-6-6-6zM18.672 26c1.828 0 3.328-1.703 3.328-3.828 0-4.922-1.656-8-4.75-8.156-1.406 1.234-3.234 1.984-5.25 1.984s-3.844-0.75-5.25-1.984c-3.094 0.156-4.75 3.234-4.75 8.156 0 2.125 1.5 3.828 3.328 3.828h13.344z";
	private String svgtrash = "M8 21.5v-11c0-0.281-0.219-0.5-0.5-0.5h-1c-0.281 0-0.5 0.219-0.5 0.5v11c0 0.281 0.219 0.5 0.5 0.5h1c0.281 0 0.5-0.219 0.5-0.5zM12 21.5v-11c0-0.281-0.219-0.5-0.5-0.5h-1c-0.281 0-0.5 0.219-0.5 0.5v11c0 0.281 0.219 0.5 0.5 0.5h1c0.281 0 0.5-0.219 0.5-0.5zM16 21.5v-11c0-0.281-0.219-0.5-0.5-0.5h-1c-0.281 0-0.5 0.219-0.5 0.5v11c0 0.281 0.219 0.5 0.5 0.5h1c0.281 0 0.5-0.219 0.5-0.5zM7.5 6h7l-0.75-1.828c-0.047-0.063-0.187-0.156-0.266-0.172h-4.953c-0.094 0.016-0.219 0.109-0.266 0.172zM22 6.5v1c0 0.281-0.219 0.5-0.5 0.5h-1.5v14.812c0 1.719-1.125 3.187-2.5 3.187h-13c-1.375 0-2.5-1.406-2.5-3.125v-14.875h-1.5c-0.281 0-0.5-0.219-0.5-0.5v-1c0-0.281 0.219-0.5 0.5-0.5h4.828l1.094-2.609c0.313-0.766 1.25-1.391 2.078-1.391h5c0.828 0 1.766 0.625 2.078 1.391l1.094 2.609h4.828c0.281 0 0.5 0.219 0.5 0.5z";
	
	private HBox hbox;
	private SVGPath svg = new SVGPath();
	private TextField playerName;
	private MenuButton selectPlayerMenu = new MenuButton();;
	private MenuItem itemPlayer;
	private MenuItem itemIaEasy;
	private MenuItem itemIaMedium;
	private MenuItem itemIaHard;
	private Pane svgContainerPane;
	private boolean humanOrAI; // TRUE -> Human / FALSE -> AI
	private int playerNumber;
	
	//TODO : Attribuer les bon noms au joueur / bot
	public NewPlayerHBox(int nbplayer, boolean playerOrNot) {
		this.playerNumber = nbplayer;
		this.humanOrAI = playerOrNot;
		if(humanOrAI) {
			this.playerName = new TextField("Joueur " + this.playerNumber);
			this.selectPlayerMenu.setText("Joueur humain");
			this.svg.setContent(svguse);
		}
		else {
			this.playerName = new TextField("IA-" + this.playerNumber);
			this.selectPlayerMenu.setText("IA Facile");
			this.svg.setContent(svgpc);
		}
		initBox();
	}
	
	private void initBox(){
		// Image
		this.svg.setFill(Color.WHITE);
		HBox.setMargin(this.svg,new Insets(5,70,5,25));
		
		// Name
		this.playerName.setFont(Font.font("System", FontPosture.REGULAR, 24));
		this.playerName.setAlignment(Pos.CENTER_LEFT);
		this.playerName.setPrefSize(215, 25);
		this.playerName.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.playerName.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		
		// Items of Split Menu
		this.itemPlayer = new MenuItem("Joueur humain");
		this.itemPlayer.setStyle("-fx-font-size : 21pt");
		this.itemPlayer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent t) {
				if(!humanOrAI) {
			    	svg.setContent(svguse);
			    	selectPlayerMenu.setText("Joueur humain");
			    	playerName.setText("Joueur " + playerNumber);
			    	humanOrAI = true;
				}
		    }
		});
		
		this.itemIaEasy = new MenuItem("IA Facile");
		this.itemIaEasy.setStyle("-fx-font-size : 21pt");
		this.itemIaEasy.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	selectPlayerMenu.setText("IA Facile");
		    	if(humanOrAI) {
			    	svg.setContent(svgpc);
			    	playerName.setText("IA-"+playerNumber);
			    	humanOrAI = false;
		    	}
		    }
		});
		
		this.itemIaMedium = new MenuItem("IA Moyenne");
		this.itemIaMedium.setStyle("-fx-font-size : 21pt");
		this.itemIaMedium.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	selectPlayerMenu.setText("IA Moyenne");
		    	if(humanOrAI) { 
			    	svg.setContent(svgpc);
			    	playerName.setText("IA-"+playerNumber);
			    	humanOrAI = false;
		    	}
		    }
		});
		
		this.itemIaHard = new MenuItem("IA Difficile");
		this.itemIaHard.setStyle("-fx-font-size : 21pt");
		this.itemIaHard.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	selectPlayerMenu.setText("IA Difficile");
		    	if(humanOrAI) {
		    		svg.setContent(svgpc);
			    	playerName.setText("IA-"+playerNumber);
			    	humanOrAI = false;
		    	}
		    }
		});
		
		// Split Menu
		this.selectPlayerMenu.setTextFill(Color.BLACK);
		this.selectPlayerMenu.setPrefSize(255, 25);
		this.selectPlayerMenu.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.selectPlayerMenu.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.selectPlayerMenu.setFont(Font.font("System", FontPosture.REGULAR, 24));
		HBox.setMargin(this.selectPlayerMenu, new Insets(0,200,0,30));
		
		this.selectPlayerMenu.getItems().addAll(this.itemPlayer, this.itemIaEasy, this.itemIaMedium, this.itemIaHard);
		
		
		// HBox (container)
		this.hbox = new HBox();
		this.hbox.setPrefSize(840, Control.USE_COMPUTED_SIZE);
		this.hbox.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.hbox.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.hbox.setPadding(new Insets(5,0,5,0));
		this.hbox.setAlignment(Pos.CENTER_RIGHT);
		this.hbox.getChildren().addAll(this.svg, this.playerName, this.selectPlayerMenu);
		VBox.setMargin(this.hbox, new Insets(25,0,25,0));
		
		// if more than 3 player
		if(this.playerNumber > 3) {
			SVGPath svgTrash = new SVGPath();
			svgTrash.setContent(this.svgtrash);
			svgTrash.setFill(Color.WHITE);
			this.svgContainerPane = new Pane(svgTrash);
			this.hbox.getChildren().add(this.svgContainerPane);
			HBox.setMargin(this.svgContainerPane,new Insets(10,80,5,0));
			HBox.setMargin(this.selectPlayerMenu, new Insets(0,98,0,30));
		}
	}
	
	public void setRemovePlayerAction(EventHandler<MouseEvent> event) {
		this.svgContainerPane.setOnMousePressed(event);
	}
	
	public HBox getHBox() {
		return this.hbox;
	}
	
	
}
