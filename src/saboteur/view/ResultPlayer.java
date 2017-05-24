package saboteur.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import saboteur.model.Player;
import saboteur.model.Team;
import saboteur.tools.Icon;

public class ResultPlayer {

	private SVGPath playerIcon;
	private Text playerName;
	private Text playerRole;
	private Text playerGold;

	public ResultPlayer(Player player) {
		playerIcon = new SVGPath();
		if (player.isAI()){
			playerIcon.setContent(Icon.computer);
		} else{
			playerIcon.setContent(Icon.computer);
		}
		playerIcon.setFill(Color.WHITE);

		this.playerName = new Text(player.getName());
		this.playerName.setStyle("-fx-font-size: 1.8em; -fx-fill: white");

		this.playerRole = new Text();
		this.playerRole.setStyle("-fx-font-size: 1.8em; -fx-fill: white");
		if (player.getTeam() == Team.DWARF){
			this.playerRole.setText("Chercheur d'or");
		} else{
			this.playerRole.setText("Saboteur");
		}

		this.playerGold = new Text(player.getGold() + " pépites d'or");
		this.playerGold.setStyle("-fx-font-size: 1.8em; -fx-fill: white");
	}

	public SVGPath getPlayerIcon(){
		return this.playerIcon;
	}

	public Text getPlayerName(){
		return this.playerName;
	}

	public Text getPlayerRole(){
		return this.playerRole;
	}

	public Text getPlayerGold(){
		return this.playerGold;
	}
}
