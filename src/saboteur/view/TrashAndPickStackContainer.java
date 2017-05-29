package saboteur.view;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import saboteur.tools.GameComponentsSize;

public class TrashAndPickStackContainer extends VBox{
	
	private StackPane trashPane, pickAndEndTurnPane;
	private Button trashButton, pickAndEndTurnButton;
	
	public TrashAndPickStackContainer() {
		this.trashButton = new Button("Défausser\net\nfinir le\ntour");
		this.pickAndEndTurnButton = new Button("Finir le\ntour");
		
		this.initButton(trashButton);
		this.initButton(pickAndEndTurnButton);
		
		this.disableTrashButton();
		this.disablePickAndEndTurnButton();
		
		this.trashPane = new StackPane(this.trashButton);
		this.pickAndEndTurnPane = new StackPane(this.pickAndEndTurnButton);
		
		this.trashPane.setAlignment(Pos.CENTER_RIGHT);
		this.pickAndEndTurnPane.setAlignment(Pos.CENTER_RIGHT);
		
		this.getChildren().addAll(this.trashPane, this.pickAndEndTurnPane);
		this.setSpacing(GameComponentsSize.getGameComponentSize().getDefaultSpacing());
	}
	
	private void initButton(Button button){
		button.setPrefWidth(GameComponentsSize.getGameComponentSize().getCardWidth()*GameComponentsSize.getGameComponentSize().getTrashAndPickStackContainerMultiplier());
		button.setPrefHeight(GameComponentsSize.getGameComponentSize().getCardHeight()*GameComponentsSize.getGameComponentSize().getTrashAndPickStackContainerMultiplier());
		button.setTextAlignment(TextAlignment.CENTER);
		button.getStyleClass().add("btn-card");
	}

	public void updateStackText(int nbCard){
		String text = ""+nbCard;
		if (nbCard <= 1){
			text += " carte\nrestante";
		} else{
			text += " cartes\nrestantes";
		}
		this.pickAndEndTurnButton.setText("Finir\n le tour\n\n" + text);
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

	public void setEmptyTrash(boolean isEmpty){
		System.out.println(isEmpty);
		if (isEmpty){
			this.trashButton.getStyleClass().remove("has-card");
		} else{
			this.trashButton.getStyleClass().add("has-card");
		}
	}

	public void setEmptyStack(boolean isEmpty){
		if (isEmpty) {
			this.pickAndEndTurnButton.getStyleClass().remove("has-card");
		} else{
			this.pickAndEndTurnButton.getStyleClass().add("has-card");
		}
	}
}
