package saboteur.view;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import saboteur.model.Game;
import saboteur.model.Team;

public class PlayerRoleContainer extends HBox{

	private Text playerRoleLabel;
	private ImageView playerRoleImage;
	private Game game;

	public PlayerRoleContainer(Game game){
		this.game = game;
		this.playerRoleLabel = new Text();
    	this.playerRoleLabel.setFont(new Font("Arial", 30));
    	this.playerRoleLabel.setFill(Color.WHITE);
    	this.playerRoleLabel.setTextAlignment(TextAlignment.CENTER);
		
		this.playerRoleImage = new ImageView();
    	this.playerRoleImage.setFitHeight(200.0);
    	this.playerRoleImage.setFitWidth(141.0);
    	
    	this.getChildren().addAll(this.playerRoleLabel, this.playerRoleImage);
		
		this.setAlignment(Pos.CENTER_RIGHT);
	}
	
	public void refreshPlayerRole() {
		if(this.game.getCurrentPlayer().getTeam() == Team.DWARF) {
			this.playerRoleLabel.setText("Chercheur d'or");
			this.playerRoleImage.setImage(new Image("/resources/nainchercheurdor.png"));
		}
		else {
			this.playerRoleLabel.setText("Saboteur");
			this.playerRoleImage.setImage(new Image("/resources/nainsaboteur.png"));
		}
	}
	
	public void setPlayerRoleComponentsVisible(boolean visible) {
		this.playerRoleImage.setVisible(visible);
		this.playerRoleLabel.setVisible(visible);
	}
	
	
}
