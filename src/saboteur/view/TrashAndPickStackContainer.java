package saboteur.view;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import saboteur.model.Game;
import saboteur.tools.GameComponentsSize;

public class TrashAndPickStackContainer extends VBox{
	
	private StackPane trashPane, pickAndEndTurnPane;
	private Button trashButton, pickAndEndTurnButton;
	private Game game;
	
	public TrashAndPickStackContainer(Game game) {
		this.game = game;
		this.trashButton = new Button("Défausser\net\nfinir le\ntour");
		this.pickAndEndTurnButton = new Button("Finir le\ntour");
		
		this.initButton(trashButton);
		this.initButton(pickAndEndTurnButton);
		
		this.disableTrashButton();
		this.disablePickAndEndTurnButton();
		
		this.trashPane = new StackPane(this.trashButton);
		this.pickAndEndTurnPane = new StackPane(this.pickAndEndTurnButton);
		
		this.getChildren().addAll(this.trashPane, this.pickAndEndTurnPane);
		this.setSpacing(20.0);
		VBox.setMargin(this.pickAndEndTurnPane, new Insets(10.0, 10.0, 0.0, 0.0));
		VBox.setMargin(this.trashPane, new Insets(10.0, 10.0, 0.0, 0.0));
	}
	
	private void initButton(Button button){
		button.setPrefWidth(GameComponentsSize.getGameComponentSize().getCardWidth()*1.6);
		button.setPrefHeight(GameComponentsSize.getGameComponentSize().getCardHeight()*1.6);
		button.setTextAlignment(TextAlignment.CENTER);
		button.getStyleClass().add("btn-card");
	}

	public void disablePickAndEndTurnButton() {
		this.pickAndEndTurnButton.setDisable(true);
	}
	
	public void enablePickAndEndTurnButton() {
		this.pickAndEndTurnButton.setDisable(false);
	}
	
	public void disableTrashButton() {
		this.trashButton.setDisable(true);
	}
	
	public void enableTrashButton() {
		this.trashButton.setDisable(false);
	}
	
	public void setEventToTrashButton(EventHandler<MouseEvent> event){
		this.trashButton.setOnMouseClicked(event);
	}
	
	public void setEventToPickAndEndTurnButton(EventHandler<MouseEvent> event){
		this.pickAndEndTurnButton.setOnMouseClicked(event);
	}
}
