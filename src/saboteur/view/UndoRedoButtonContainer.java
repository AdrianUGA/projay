package saboteur.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import saboteur.model.Game;

public class UndoRedoButtonContainer extends VBox{

	private Game game;
	private Button undoButton;
	private Button redoButton;

	public UndoRedoButtonContainer(Game game) {
		this.game = game;
		this.undoButton = new Button("Undo");
		this.redoButton = new Button("Redo");
				
		this.getChildren().addAll(this.undoButton, this.redoButton);
	}
	
    public void manageUndoRedoButton(){
        if(this.game.historyUndoIsEmpty()) {
            this.disableUndoButton();
        }
        else {
            this.enableUndoButton();
        }

        if(this.game.historyRedoIsEmpty()) {
            this.disableRedoButton();
        }
        else {
        	this.enableRedoButton();
        }
    }
    
    public void setUndoButtonAction(EventHandler<ActionEvent> event) {
    	this.undoButton.setOnAction(event);
    }
    
    public void setRedoButtonAction(EventHandler<ActionEvent> event) {
    	this.redoButton.setOnAction(event);
    }
    
	public void disableUndoButton() {
		this.undoButton.setDisable(true);
	}
	
	public void enableUndoButton() {
		this.undoButton.setDisable(false);
	}
	
	public void disableRedoButton() {
		this.redoButton.setDisable(true);
	}
	
	public void enableRedoButton() {
		this.redoButton.setDisable(false);
	}
}
