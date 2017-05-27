package saboteur.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import saboteur.tools.Icon;

public class NewPlayerHBox extends HBox {

	private final static int MAX_LENGTH = 10;
	private SVGPath svg = new SVGPath();
	private TextField playerName;
	private ComboBox<String> selectPlayerMenu;
	private Pane svgContainerPane;
	private int playerNumber;
	
	//TODO : Attribuer les bon noms au joueur / bot
	public NewPlayerHBox(int nbplayer, boolean isHuman) {
		this.playerNumber = nbplayer;
		initBox();
		this.playerName.setText("Joueur " + this.playerNumber);
		if(isHuman) {
			this.selectPlayerMenu.setValue("Humain");
			this.svg.setContent(Icon.user);
		}
		else {
			this.selectPlayerMenu.setValue("IA Facile");
			this.svg.setContent(Icon.computer);
		}
	}
	
	private void initBox(){
		// Image
		this.svg.setFill(Color.WHITE);
		HBox.setMargin(this.svg,new Insets(5,70,5,25));
		
		// Name
		this.playerName = new TextField();
		this.playerName.setFont(Font.font("System", FontPosture.REGULAR, 24));
		this.playerName.setAlignment(Pos.CENTER_LEFT);
		this.playerName.setPrefSize(215, 25);
		this.playerName.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.playerName.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.playerName.textProperty().addListener((ov, oldValue, newValue) -> {
            if (playerName.getText().length() > MAX_LENGTH) {
                String s = playerName.getText().substring(0, MAX_LENGTH);
                playerName.setText(s);
            }
        });
		
		// Split Menu
		this.selectPlayerMenu = new ComboBox<>();
		this.selectPlayerMenu.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.equals("Humain")){
					svg.setContent(Icon.user);
				} else{
					svg.setContent(Icon.computer);
				}
			}
		});
		this.selectPlayerMenu.setPrefSize(255, 25);
		this.selectPlayerMenu.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.selectPlayerMenu.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.selectPlayerMenu.setStyle("-fx-font-size: 24px");
		HBox.setMargin(this.selectPlayerMenu, new Insets(0,200,0,30));
		this.selectPlayerMenu.getItems().addAll("Humain", "IA Facile", "IA Moyen", "IA Difficile");
		
		
		// HBox (container)
		this.setPrefSize(840, Control.USE_COMPUTED_SIZE);
		this.setMaxSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
		this.setPadding(new Insets(5,0,5,0));
		this.setAlignment(Pos.CENTER_RIGHT);
		this.getChildren().addAll(this.svg, this.playerName, this.selectPlayerMenu);
		VBox.setMargin(this, new Insets(25,0,25,0));
		
		// if more than 3 player
		if(this.playerNumber > 3) {
			SVGPath svgTrash = new SVGPath();
			svgTrash.setContent(Icon.trash);
			svgTrash.setFill(Color.WHITE);
			this.svgContainerPane = new Pane(svgTrash);
			this.getChildren().add(this.svgContainerPane);
			HBox.setMargin(this.svgContainerPane,new Insets(10,80,5,0));
			HBox.setMargin(this.selectPlayerMenu, new Insets(0,98,0,30));
		}
	}
	
	public void setRemovePlayerAction(EventHandler<MouseEvent> event) {
		this.svgContainerPane.setOnMousePressed(event);
	}

	public TextField getPlayerName(){
		return this.playerName;
	}

	public ComboBox<String> getSelectPlayerMenu(){
		return this.selectPlayerMenu;
	}
}
