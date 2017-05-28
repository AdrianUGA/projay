package saboteur.view;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import saboteur.model.Game;
import saboteur.model.Team;

public class PlayerRoleContainer extends HBox{

	private Text playerNameText;
	private Text playerRoleText;
	private VBox textContainer;
	private ImageView playerRoleImage;
	private Game game;

	public PlayerRoleContainer(Game game){
		this.game = game;
		this.playerNameText = new Text();
		this.playerNameText.getStyleClass().add("player-name");
    	this.playerNameText.setTextAlignment(TextAlignment.CENTER);
		
		this.playerRoleText = new Text();
		this.playerRoleText.getStyleClass().add("player-role");
    	this.playerRoleText.setTextAlignment(TextAlignment.CENTER);
    	
    	this.textContainer = new VBox(5);
    	this.textContainer.getChildren().addAll(this.playerNameText, this.playerRoleText);
    	this.textContainer.setAlignment(Pos.CENTER_RIGHT);
		
		this.playerRoleImage = new ImageView();
    	this.playerRoleImage.setFitHeight(200.0*1.25);
    	this.playerRoleImage.setFitWidth(141.0*1.25);
    	
    	this.getChildren().addAll(this.textContainer, this.playerRoleImage);
		
		this.setAlignment(Pos.CENTER_RIGHT);
	}
	
	public void refreshPlayerRole() {
		this.playerNameText.setText(this.game.getCurrentPlayer().getName());
		if(this.game.getCurrentPlayer().getTeam() == Team.DWARF) {
			this.playerRoleText.setText("Chercheur d'or");
			this.playerRoleImage.setImage(new Image("/resources/nainchercheurdor.png"));
		}
		else {
			this.playerRoleText.setText("Saboteur");
			this.playerRoleImage.setImage(new Image("/resources/nainsaboteur.png"));
		}
	}
	
	public void setPlayerRoleComponentsVisible(boolean visible) {
		this.playerRoleImage.setVisible(visible);
		this.playerRoleText.setVisible(visible);
		this.playerNameText.setVisible(visible);
	}
	
	
}
