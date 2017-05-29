package saboteur.view;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import saboteur.tools.GameComponentsSize;
import saboteur.tools.Resources;

public class TrashAndPickStackContainer extends VBox{
	
	private StackPane trashPane, pickAndEndTurnPane;
	private Button trashButton, pickAndEndTurnButton;
	private GameComponentsSize gameComponentsSize;
	
	public TrashAndPickStackContainer() {
		this.gameComponentsSize = GameComponentsSize.getGameComponentSize();
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
		this.setSpacing(this.gameComponentsSize.getDefaultSpacing());
	}
	
	private void initButton(Button button){
		button.setPrefWidth(this.gameComponentsSize.getCardWidth() * this.gameComponentsSize.getTrashAndPickStackContainerMultiplier());
		button.setPrefHeight(this.gameComponentsSize.getCardHeight() * this.gameComponentsSize.getTrashAndPickStackContainerMultiplier());
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

	public void animateStack(ImageView clone, EventHandler<ActionEvent> onFinished){
		ParallelTransition pt = new ParallelTransition();

		ScaleTransition st = new ScaleTransition(Duration.millis(400), clone);
		st.setByX(0.2f);
		st.setByY(0.2f);
		st.setInterpolator(Interpolator.EASE_IN);

		ScaleTransition st2 = new ScaleTransition(Duration.millis(400), clone);
		st2.setByX(-0.47f);
		st2.setByY(-0.47f);
		st2.setInterpolator(Interpolator.EASE_OUT);

		SequentialTransition sequence = new SequentialTransition(st, st2);

		TranslateTransition tt = new TranslateTransition(Duration.millis(800), clone);
		tt.setFromX(0);
		tt.setFromY(0);
		tt.setByY(520);
		tt.setToX(20);

		pt.getChildren().addAll(sequence, tt);
		pt.setOnFinished(onFinished);
		pt.play();
	}

	public ImageView getCloneOfCard(){
		ImageView clone = new ImageView(Resources.getImage().get("card_verso.png"));
		clone.setFitHeight(this.pickAndEndTurnButton.getHeight());
		clone.setFitWidth(this.pickAndEndTurnButton.getWidth());
		this.pickAndEndTurnPane.getChildren().add(clone);
		return clone;
	}
}
