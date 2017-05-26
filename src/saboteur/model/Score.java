package saboteur.model;

import java.io.Serializable;
import java.util.LinkedList;

public class Score implements Serializable {
	private LinkedList<String> listPlayersNames;
	
	private LinkedList<String> listWinnersNames;
	
	private int scoreWinner;
	
	public Score(){
		this.listPlayersNames = new LinkedList<>();
		this.listWinnersNames = new LinkedList<>();
		this.scoreWinner = 0;
	}

	public LinkedList<String> getListPlayersNames() {
		return listPlayersNames;
	}

	public void setListPlayersNames(LinkedList<String> listPlayersNames) {
		this.listPlayersNames = listPlayersNames;
	}

	public LinkedList<String> getListWinnersNames() {
		return listWinnersNames;
	}

	public void setListWinnersNames(LinkedList<String> listWinnersNames) {
		this.listWinnersNames = listWinnersNames;
	}

	public int getScoreWinner() {
		return scoreWinner;
	}

	public void setScoreWinner(int scoreWinner) {
		this.scoreWinner = scoreWinner;
	}
	
	public void addPlayerName(String name){
		this.listPlayersNames.add(name);
	}
	
	public void addWinnerName(String name){
		this.listWinnersNames.add(name);
	}
}
