package saboteur.ai;
import saboteur.model.Board;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Team;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;

//									 ___
//                                  /   \
//                                 | | | |
//                                 |  _  |
//                                 |_____|
//                           ____ ___|_|___ ____
//                          ()___)         ()___)
//                          // /|   I.U.T   |\ \\
//                         // / |  VALENCE  | \ \\
//                        (___) |___________| (___)
//                        (___)   (_______)   (___)
//                        (___)     (___)     (___)
//                        (___)      |_|      (___)
//                        (___)  ___/___\___   | |
//                         | |  |           |  | |
//                         | |  |___________| /___\
//                        /___\  |||     ||| //   \\
//                       //   \\ |||     ||| \\   //
//                       \\   // |||     |||  \\ //
//                        \\ // ()__)   (__()
//                              ///       \\\
//                             ///         \\\
//                           _///___     ___\\\_
//                          |_______|   |_______|
//
// Tribute to C.Metge, my favorite cousin English teacher

public class AI extends Player {
	static final long serialVersionUID = 6519358301134674963L;

	protected final int AVERAGE_TRUST = 50;
	
	protected LinkedHashMap<Player,Float> isDwarf;	
	protected Difficulty difficulty;
	protected LinkedHashMap<Position,Float> estimatedGoldCardPosition;
	protected LinkedHashMap<Operation, Float> operationsWeight;
	protected int currentMinDistance;
	protected final long seedAI;
	private Computer computer;
	

	public AI(Game game, String name, Difficulty difficulty, long seedAI) {
		super(game, name);
		this.isDwarf = new LinkedHashMap<Player,Float>();
		this.difficulty = difficulty;
		this.operationsWeight = new LinkedHashMap<Operation, Float>();
		this.estimatedGoldCardPosition = new LinkedHashMap<Position, Float>();
		this.seedAI = seedAI;
	}
	
	public void initializeAI(){
		this.operationsWeight.clear();
		this.isDwarf.clear();
		this.estimatedGoldCardPosition.clear();
		//Initialize estimated gold card position
		for (Position position : game.getBoard().getGoalCards()){
			this.estimatedGoldCardPosition.put(position, 1f/3f);
		}
		//Initialize trust
		for(Player p : game.getPlayerList()){
			this.isDwarf.put(p, (float) AVERAGE_TRUST);
		}
		if(getTeam() == Team.SABOTEUR){
			this.isDwarf.put(this, (float) -1073741824);
		}
		else{
			this.isDwarf.put(this, (float) 1073741824);
		}
		this.currentMinDistance = this.getGame().getBoard().aStarOnEmptyCard(Board.getStart(), getEstimatedGoldCardPosition());
		this.computer = ComputerFactory.getComputer(this);
	}

	@Override
	public void notify(Operation o){
		if(this != o.getSourcePlayer()){
			//Doesn't update trust on itself
			if(o.isOperationActionCardToBoard())
				updateTrust((OperationActionCardToBoard) o);
			else if(o.isOperationActionCardToPlayer())
				updateTrust((OperationActionCardToPlayer) o);
			else if(o.isOperationPathCard())
				updateTrust((OperationPathCard) o);
			else System.err.println("Opération non reconnue");
		}
		if(o.isOperationPathCard() || o.isOperationActionCardToBoard()){
			this.currentMinDistance = this.getGame().getBoard().minFromAnyEmptyPositionToGoldCard(getEstimatedGoldCardPosition());
		}
	}
	
	protected Operation bestOperationToPlay(){
		float max = -435365;
		LinkedList<Operation> bestOperations = new LinkedList<Operation>();
		Random r = new Random(getGame().getSeed());
		
		for(Operation o : this.operationsWeight.keySet()){
			if(this.operationsWeight.get(o) > max){
				max = this.operationsWeight.get(o);
			}
		}
		for(Operation o : this.operationsWeight.keySet()){
			if(this.operationsWeight.get(o) == max){
				bestOperations.add(o);
			}
		}
		
		if(bestOperations.size() == 0){
			System.out.println("This threw illegalArgument exeption in nextInt : " 
				+ bestOperations.size());
		}
		Operation o = bestOperations.get(r.nextInt(bestOperations.size()));
		System.out.print("Opération joué par " + this.name + " ==> ");
		o.displayOperationInformation();
		if(o.isOperationPathCard() && ((OperationPathCard)o).getReversed()){
			System.out.print(" !R");
		}
		System.out.println(" ==> rôle = " + this.getTeam() + " with weight = "+ operationsWeight.get(o));
		return o;
	}
	
	@Override
	public Operation playCard(){
		System.out.print("ma main : ");
		printHand();
		System.out.println(getEstimatedGoldCardPosition());
		Operation o = selectOperation();
		//System.out.println("AI " + this.name +" turn " + getGame().getTurn() + " played operation " + o.getClass().getName() + " with card + "+ o.getCard().getClassName());
		this.getGame().playOperation(o);
		//System.out.println("It now has " + hand.size() + " cards");
		return o;
	}
	
	protected Operation selectOperation(){
		resetProbabilitiesToPlayEachOperation();
		this.computer.compute();
		removeOperationWithNullTarget();
		return bestOperationToPlay();
	}
	
	
/* Tests. No side effects */
	
	public boolean isAI(){
		return true;
	}
	
	protected boolean canPlayThere(Position position) {
		for(Card card : this.hand){
			if(card.isPathCard()){
				if(this.getGame().getBoard().isPossible((PathCard) card, position) 
						|| this.getGame().getBoard().isPossible(((PathCard) card).reversed(), position)) return true;
			}
		}
		return false;
	}
	
	
/* Getters LinkedHashSetters Modifiers */
	
	// Plan & Collapse card
	public void updateTrust(OperationActionCardToBoard o){
		if(o.getCard().isPlanCard()){
			// Nothing to update
		}
		else if(o.getCard().isCollapseCard()){
			if(((PathCard) o.getDestinationCard()).isCulDeSac()){
				this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 20);
			}
			else{
				int nextMinDistance = this.getGame().getBoard().minFromAnyEmptyPositionToGoldCard(getEstimatedGoldCardPosition());
				if(nextMinDistance > this.currentMinDistance){
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 20);
				}else if(nextMinDistance < this.currentMinDistance){
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
				}
			}
		}
		else{
			System.err.println("Operation ActionCardToBoard undetected");
		}
	}
	
	// Sabotage & Rescue card
	public void updateTrust(OperationActionCardToPlayer o){
		System.out.println("Je suis " + this.getName() + " et j'update " + o.getDestinationPlayer().getName() + " et source est " + o.getSourcePlayer().getName());
		if(o.getDestinationPlayer() != this){
			if(o.getCard().isSabotageCard()){
				if(this.isDwarf.get(o.getSourcePlayer()) > this.isDwarf.get(o.getDestinationPlayer()) && (this.isDwarf.get(o.getDestinationPlayer()) <= 40)){
					// Enemies of saboteurs are dwarfs 
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
				}
				else if(this.isDwarf.get(o.getSourcePlayer()) <= this.isDwarf.get(o.getDestinationPlayer()) && (this.isDwarf.get(o.getDestinationPlayer()) >= 60)){
					// Enemies of dwarfs are saboteurs
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 10);
				}
			}
			else if(o.getCard().isRescueCard() || o.getCard().isDoubleRescueCard()){
				if(!o.getSourcePlayer().equals(o.getDestinationPlayer())){
					if(this.isDwarf.get(o.getDestinationPlayer()) <= 40){
						// Allies saboteurs are saboteurs
						this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 10);
					}
					else if(this.isDwarf.get(o.getDestinationPlayer()) >= 60){
						// Allies of dwarfs are dwarfs
						this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
					}
				}
			}
			else{
				System.err.println("Operation ActionCarToPlayer undetected");
			}
		}else{
			if(o.getCard().isSabotageCard()){
				if(this.getTeam() == Team.DWARF){
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 10);
				}else{
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
				}
			}else if(o.getCard().isRescueCard() || o.getCard().isDoubleRescueCard()){
				if(this.getTeam() == Team.DWARF){
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
				}else{
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 10);
				}
			}else{
				System.err.println("Operation ActionCarToPlayer undetected");
			}
		}
	}
	
	// Path card
	public void updateTrust(OperationPathCard o){
		
		int nextMinDistance = this.getGame().getBoard().aStarOnEmptyCard(o.getP(), getEstimatedGoldCardPosition());	
		
		if(((PathCard) o.getCard()).isCulDeSac()){
			if(this.currentMinDistance < nextMinDistance){
				int neighborsAmount = this.getGame().getBoard().numberOfNeighbors(o.getP());
				this.isDwarf.put(o.getSourcePlayer(), (float) (this.isDwarf.get(o.getSourcePlayer()) - ((nextMinDistance - currentMinDistance)*5) - neighborsAmount));
			}
		}
		else{
			if(this.currentMinDistance > nextMinDistance){
				//Distance get smaller
				this.isDwarf.put(o.getSourcePlayer(), (float) (this.isDwarf.get(o.getSourcePlayer()) + 5 / currentMinDistance));
			}
		}
	}
	
	public AI setDifficulty(Difficulty difficulty){
		this.difficulty = difficulty;
		return this;
	}
	
	public Difficulty getDifficulty(){
		return this.difficulty;
	}
	
	public Position getEstimatedGoldCardPosition(){
		float max = -28091994;
		LinkedList<Position> equiprobableGoldCardPosition = new LinkedList<Position>();
		Random r = new Random(this.seedAI);
		
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(Math.round((this.estimatedGoldCardPosition.get(p))*1000) > max){
				max = Math.round(this.estimatedGoldCardPosition.get(p)*1000);
				//System.out.println("max = " + max);
			}
		}
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(Math.round(this.estimatedGoldCardPosition.get(p)*1000) == max){
				equiprobableGoldCardPosition.add(p);
			}
		}
		Position estimatedGoldCardPosition = equiprobableGoldCardPosition.get(r.nextInt(equiprobableGoldCardPosition.size()));
		return estimatedGoldCardPosition;
	}
	
	public void changeEstimatedGoldCardPosition(Position p, Boolean b){
		if(b){
			this.estimatedGoldCardPosition.put(p, 1f);
			this.setAllEstimatedGoldCardPositionExept(p, 0f);
		}else{
			int greaterThanZero = 0;
			
			this.estimatedGoldCardPosition.put(p, 0f);
			for(Position currentP : estimatedGoldCardPosition.keySet()){
				if(estimatedGoldCardPosition.get(currentP)>0){
					greaterThanZero++;
				}
			}
			for(Position currentP : estimatedGoldCardPosition.keySet()){
				if(estimatedGoldCardPosition.get(currentP)>0){
					estimatedGoldCardPosition.put(currentP, 1f/greaterThanZero);
				}
			}
		}
	}
	
	public void setAllEstimatedGoldCardPositionExept(Position position, Float probability){
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(!p.equals(position)){
				this.estimatedGoldCardPosition.put(p, probability);
			}
		}
	}
	
	public void setAllEstimatedGoldCardPositionExept(List<Position> positions, Float probability){
		for(Position position : positions){
			this.setAllEstimatedGoldCardPositionExept(position, probability);
		}
	}
	
	public Position getMostProbableGoldCard(){
		Position[] positions = (Position[]) this.estimatedGoldCardPosition.values().toArray();
		Arrays.sort(positions);
		return positions[0];
	}
	
	public boolean knowsTheGoldCardPosition(){
		if(estimatedGoldCardPosition.containsValue(1f))
			System.out.println(this.name + " connait la position de la carte or");
		return this.estimatedGoldCardPosition.containsValue(1f);
	}
	
	@Override
	public void viewGoalCard(PathCard card){
		changeEstimatedGoldCardPosition(getGame().getBoard().getPosition(card), card.hasGold());
	}
	
	protected Player mostLikelyADwarf(){
		float maxTrust=-1073741824;
		Player mostTrustfulPlayer = null;
		for(Player p : this.isDwarf.keySet()){
			if(this.isDwarf.get(p) > maxTrust && p != this){
				maxTrust = this.isDwarf.get(p);
				mostTrustfulPlayer = p;
			}
		}
		return mostTrustfulPlayer;
	}
	
	protected void resetProbabilitiesToPlayEachOperation(){
		this.operationsWeight.clear();
		for(Card c : getHand()){
			if(c.isPathCard()){
				this.operationsWeight.put(new OperationPathCard(this, c, null), 0f);
			}
			else if(c.isCollapseCard() || c.isPlanCard()){
				this.operationsWeight.put(new OperationActionCardToBoard(this, c, null), 0f);
			}
			else if(c.isRescueCard() || c.isDoubleRescueCard() || c.isSabotageCard()){
				this.operationsWeight.put(new OperationActionCardToPlayer(this, c, null), 0f);
			}	
		}
	}
	
	protected void removeOperationWithNullTarget(){
		Map<Operation, Float> cloneOperationsWeight = new LinkedHashMap<Operation,Float>(this.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			if(!o.isOperationTrash()){
				if(o.getCard().isPathCard()){
					if(((OperationPathCard) o).getP() == null){
						this.operationsWeight.remove((OperationPathCard) o);
					}
				}
				else if(o.getCard().isCollapseCard() || o.getCard().isPlanCard()){
					if(((OperationActionCardToBoard) o).getDestinationCard() == null)
						this.operationsWeight.remove((OperationActionCardToBoard) o);
				}
				else if(o.getCard().isRescueCard() || o.getCard().isDoubleRescueCard() || o.getCard().isSabotageCard()){
					if(((OperationActionCardToPlayer) o).getDestinationPlayer() == null)
						this.operationsWeight.remove((OperationActionCardToPlayer) o);
				}
			}
		}
	}
	
	protected Player mostLikelyASaboteur(){
		float leastTrust=1073741824;
		Player leastTrustfulPlayer = null;
		for(Player p : this.isDwarf.keySet()){
			if(this.isDwarf.get(p) < leastTrust && p != this){
				leastTrust = this.isDwarf.get(p);
				leastTrustfulPlayer = p;
			}
		}
		return leastTrustfulPlayer;
	}

	protected Map<Player,Float> getIsDwarf(){
		return this.isDwarf;
	}

	
	//If parameter is true, it'll add the AI who calls the method anyway
	public LinkedList<Player> getAllMostLikelySaboteurPlayersHardAI(boolean withAI) {
		int maxAmountOfSaboteur = game.maximumAmountOfSaboteur();
		int maxTrust = Computer.MINIMUM_TRUST_SABOTEUR_HARD;
		LinkedList<Player> likelyDwarf = new LinkedList<Player>();
		
		do{
			likelyDwarf.clear();
			for(Player p : this.isDwarf.keySet()){
				if(this.isDwarf.get(p) <= maxTrust || (withAI && p == this)){
					likelyDwarf.add(p);
				}
			}
			maxTrust = maxTrust - 5;
		}while(likelyDwarf.size() >= maxAmountOfSaboteur + 1);
		
		return likelyDwarf;
	}
	
	//If parameter is true, it'll add the AI who calls the method anyway
	public LinkedList<Player> getAllMostLikelyDwarfPlayersHardAI(boolean withAI) {
		int minAmountOfDwarf = game.minimumAmountOfDwarf();
		int minTrust = Computer.MINIMUM_TRUST_DWARF_HARD;
		LinkedList<Player> likelyDwarf = new LinkedList<Player>();
		
		do{
			likelyDwarf.clear();
			for(Player p : this.isDwarf.keySet()){
				if(this.isDwarf.get(p) >= minTrust || (withAI && p == this)){
					likelyDwarf.add(p);
				}
			}
			minTrust = minTrust + 5;
		}while(likelyDwarf.size() >= minAmountOfDwarf);
		
		return likelyDwarf;
	}
	
	public Map<Operation, Float> getOperationWeight() {
		return this.operationsWeight;
	}
	
	
/* Printing */
	public String handToString(){
		String hand = "";
		for(Card c: this.hand){
			hand += c + " ";
		}
		return hand;	
	}

	
	public void printHand(){
			System.out.println(this.handToString());
	}

	public void printIsDwarf() {
		for(Player p : this.game.getPlayerList()){
			System.out.println(p.getName() + " : " + isDwarf.get(p));
		}
		
	}


}
