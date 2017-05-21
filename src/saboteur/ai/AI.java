package saboteur.ai;
import saboteur.model.Game;
import saboteur.model.Operation;

import java.util.Arrays;
import java.util.HashMap;
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


public class AI extends Player {
	static final long serialVersionUID = 6519358301134674963L;

	protected final int AVERAGE_TRUST = 50;
	
	protected Map<Player,Float> isDwarf;	
	protected Difficulty difficulty;
	protected Map<Position,Float> estimatedGoldCardPosition;
	protected Map<Operation, Float> operationsWeight;
	

	public AI(Game game, String name, Difficulty difficulty) {
		super(game, name);
		this.isDwarf = new HashMap<Player,Float>();
		this.difficulty = difficulty;
		this.operationsWeight = new HashMap<Operation, Float>();
		this.estimatedGoldCardPosition = new HashMap<Position, Float>();
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
	}

	@Override
	public void notify(Operation o){
		if(o.isOperationActionCardToBoard())
			updateTrust((OperationActionCardToBoard) o);
		else if(o.isOperationActionCardToPlayer())
			updateTrust((OperationActionCardToPlayer) o);
		else if(o.isOperationPathCard())
			updateTrust((OperationPathCard) o);
		else System.err.println("Opération non reconnue");
	}

	// Plan & Collapse card
	public void updateTrust(OperationActionCardToBoard o){
		if(o.getCard().isPlanCard()){
			// Nothing to update
		}
		else if(o.getCard().isCollapseCard()){
			if(((PathCard) o.getDestinationCard()).isCulDeSac()){
				this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
			}
			else{
				this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 20);
			}
		}
		else{
			System.err.println("Operation ActionCardToBoard undetected");
		}
	}
	
	// Sabotage & Rescue card
	public void updateTrust(OperationActionCardToPlayer o){
		if(o.getCard().isSabotageCard()){
			if(this.isDwarf.get(o.getSourcePlayer()) > this.isDwarf.get(o.getDestinationPlayer()) && (this.isDwarf.get(o.getDestinationPlayer()) <= 40)){
				// Ennemies of our ennemies are our allies
				this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
			}
			else if(this.isDwarf.get(o.getSourcePlayer()) <= this.isDwarf.get(o.getDestinationPlayer()) && (this.isDwarf.get(o.getDestinationPlayer()) >= 60)){
				// Ennemies of our allies are our ennemies
				this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 10);
			}
		}
		else if(o.getCard().isRescueCard() || o.getCard().isDoubleRescueCard()){
			if(!o.getSourcePlayer().equals(o.getDestinationPlayer())){
				if(this.isDwarf.get(o.getDestinationPlayer()) <= 40){
					// Allies of our ennemies are our ennemies
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) - 10);
				}
				else if(this.isDwarf.get(o.getDestinationPlayer()) >= 60){
					// Allies of our allies are our allies
					this.isDwarf.put(o.getSourcePlayer(), this.isDwarf.get(o.getSourcePlayer()) + 10);
				}
			}
		}
		else{
			System.err.println("Operation ActionCarToPlayer undetected");
		}
	}
	
	// Path card
	public void updateTrust(OperationPathCard o){
		int taxiDistance = o.getP().getTaxiDistance(getEstimatedGoldCardPosition());
		int neighborsAmount = this.getGame().getBoard().numberOfNeighbors(o.getP());
		
		if(((PathCard) o.getCard()).isCulDeSac()){ 
			// The closer the gold card, the heavier is the card.
			// The more neighbors, the merr... heavier is the card.
			this.isDwarf.put(o.getSourcePlayer(), (float) (this.isDwarf.get(o.getSourcePlayer()) - (40/(Math.pow(2, taxiDistance)))*(0.75+(neighborsAmount/4)) - 2));
		}
		else{
			this.isDwarf.put(o.getSourcePlayer(), (float) (this.isDwarf.get(o.getSourcePlayer()) + 40/(Math.pow(2, taxiDistance)) + 3));
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
		Random r = new Random(getGame().getSeed());
		
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(this.estimatedGoldCardPosition.get(p) > max){
				max = this.estimatedGoldCardPosition.get(p);
				//System.out.println("max = " + max);
			}
		}
		for(Position p : this.estimatedGoldCardPosition.keySet()){
			if(this.estimatedGoldCardPosition.get(p) == max){
				equiprobableGoldCardPosition.add(p);
			}
		}
		return equiprobableGoldCardPosition.get(r.nextInt(equiprobableGoldCardPosition.size()));
	}
	
	public void changeEstimatedGoldCardPosition(Position p, Boolean b){
		if(b){
			this.estimatedGoldCardPosition.put(p, 1f);
			this.setAllEstimatedGoldCardPositionExept(p, 0f);
		}else{
			this.estimatedGoldCardPosition.put(p, 0f);
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
		return this.estimatedGoldCardPosition.containsValue(1f);
	}
	
	@Override
	public void viewGoalCard(PathCard card){
		changeEstimatedGoldCardPosition(getGame().getBoard().getPosition(card), card.hasGold());
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
		Map<Operation, Float> cloneOperationsWeight = new HashMap<Operation,Float>(this.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			if(!o.isOperationTrash()){
				if(o.getCard().isPathCard()){
					if(((OperationPathCard) o).getP() == null)
						this.operationsWeight.remove((OperationPathCard) o);
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
	
	//TODO move this method somewhere
	public float positiveOrZero(float i){
		if(i>0){
			return i;
		}
		return 0;
	}
	
	//TODO move this method somewhere
	public float ifNegativeZeroElseOne(float i){
		if(i<=0){
			return 0;
		}
		else{
			return 1;
		}
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
	
	//If parameter is true, it'll add the AI who calls the method anyway
	public LinkedList<Player> getAllMostLikelyDwarfPlayersHardAI(boolean withAI) {
		int minAmountOfDwarf = game.minimumAmountOfDwarf();
		int minTrust = Coefficients.MINIMUM_TRUST_DWARF_HARD;
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
	
	//If parameter is true, it'll add the AI who calls the method anyway
	public LinkedList<Player> getAllMostLikelySaboteurPlayersHardAI(boolean withAI) {
		int maxAmountOfSaboteur = game.maximumAmountOfSaboteur();
		int maxTrust = Coefficients.MINIMUM_TRUST_DWARF_HARD;
		LinkedList<Player> likelyDwarf = new LinkedList<Player>();
		
		do{
			likelyDwarf.clear();
			for(Player p : this.isDwarf.keySet()){
				if(this.isDwarf.get(p) <= maxTrust || (withAI && p == this)){
					likelyDwarf.add(p);
				}
			}
			maxTrust = maxTrust - 5;
		}while(likelyDwarf.size() >= maxAmountOfSaboteur);
		
		return likelyDwarf;
	}

	public boolean isAI(){
		return true;
	}
	
	protected Operation bestOperationToPlay(){
		float max = -435365;
		LinkedList<Operation> bestOperations = new LinkedList<Operation>();
		Random r = new Random(getGame().getSeed());
		
		for(Operation o : this.operationsWeight.keySet()){
			////System.out.println(o.getClass().getName());
			if(this.operationsWeight.get(o) > max){
				max = this.operationsWeight.get(o);
			}
		}
		for(Operation o : this.operationsWeight.keySet()){
			if(this.operationsWeight.get(o) == max){
				bestOperations.add(o);
			}
		}
		Operation o = bestOperations.get(r.nextInt(bestOperations.size()));
		System.out.print("Opération joué par " + this.name + " ==> ");
		o.displayOperationInformation();
		System.out.println(" ==> rôle = " + this.getTeam() + " with weight = "+ operationsWeight.get(o));
		return o;
	}
	
	@Override
	public void playCard(){
		Operation o = selectOperation();
		//System.out.println("AI " + this.name +" turn " + getGame().getTurn() + " played operation " + o.getClass().getName() + " with card + "+ o.getCard().getClassName());
		this.getGame().playOperation(o);
		//System.out.println("It now has " + hand.size() + " cards");
	}
	
	protected Operation selectOperation(){
		resetProbabilitiesToPlayEachOperation();
		switch(this.team){
		case DWARF:
			switch(this.getDifficulty()){
			case EASY:
				//DwarfAI.computeOperationWeightEasyAI(this);
				DwarfAI.computeOperationWeightHardAI(this);
				break;
			case MEDIUM:
				DwarfAI.computeOperationWeightMediumAI(this);
				break;
			case HARD:
				DwarfAI.computeOperationWeightHardAI(this);
				break;
			}
			break;
		case SABOTEUR:
			switch(this.getDifficulty()){
			case EASY:
				SaboteurAI.computeOperationWeightEasyAI(this);
				break;
			case MEDIUM:
				SaboteurAI.computeOperationWeightMediumAI(this);
				break;
			case HARD:
				SaboteurAI.computeOperationWeightHardAI(this);
				break;
			}
		}
		removeOperationWithNullTarget();
		return bestOperationToPlay();
	}

	public void noGoldThere(Position p) {
		this.estimatedGoldCardPosition.put(p, 0f);
	}

	protected Map<Player,Float> getIsDwarf(){
		return this.isDwarf;
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
	
}
