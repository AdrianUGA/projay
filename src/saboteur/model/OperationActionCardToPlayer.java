package saboteur.model;

import saboteur.model.Card.*;

public class OperationActionCardToPlayer extends Operation {
	private static final long serialVersionUID = -655657832529547829L;
	private Player destinationPlayer; //TO SAVE
	private Tool toolDestination; //TO SAVE
	private SabotageCard destinationCard; //TO SAVE
	
	public OperationActionCardToPlayer(Player sourcePlayer, Card card, Player destinationPlayer) {
		super(sourcePlayer, card);
		this.toolDestination = null;
		this.destinationPlayer = destinationPlayer;
		this.destinationCard = null;
	}
	public OperationActionCardToPlayer(Player sourcePlayer, Card card, Player destinationPlayer, Tool toolDestination) {
		super(sourcePlayer, card);
		this.toolDestination = toolDestination;
		this.destinationPlayer = destinationPlayer;
		this.destinationCard = null;
	}
	
	public Player getDestinationPlayer(){
		return destinationPlayer;
	}

	@Override
	public void exec(Game game) {
		ActionCardToPlayer card = (ActionCardToPlayer) this.getCard();
		this.getSourcePlayer().removeHandCard(this.getCard());
		switch (card.getType()){
			case DOUBLE_RESCUE:
				this.destinationCard = this.getDestinationPlayer().getCardCorrespondingToRescueType(this.toolDestination);
				this.destinationPlayer.removeHandCard(this.destinationCard);
				break;
			case RESCUE:
				this.destinationCard = this.getDestinationPlayer().getCardCorrespondingToRescueType(((RescueCard)card).getRescueType());
				this.destinationPlayer.removeHandCard(this.destinationCard);
				break;
			case SABOTAGE:
				this.destinationPlayer.addHandicapCard((SabotageCard)this.getCard());
				break;
			default:
		}
		
		game.notify(this);
	}
	
	@Override
	public void execReverse(Game game) {
		this.getSourcePlayer().addHandCard(this.getCard());
		
		if (destinationCard == null){ //It's a sabotage operation
			destinationPlayer.removeHandicapCard((SabotageCard)this.getCard());
		} else {
			this.destinationPlayer.addHandicapCard(this.destinationCard);
		}
	}

	public OperationActionCardToPlayer setDestinationPlayer(Player destinationPlayer) {
		this.destinationPlayer = destinationPlayer;
		return this;
	}
	
	@Override
	public boolean isOperationActionCardToPlayer(){
		return true;
	}
	
	@Override
	public void displayOperationInformation(){
		System.out.print("OperationActionCardToPlayer : destination Player = " + getDestinationPlayer().getName());
	}
	public Tool getToolDestination() {
		return toolDestination;
	}
	public void setToolDestination(Tool toolDestination) {
		this.toolDestination = toolDestination;
	}
	public SabotageCard getDestinationCard() {
		return destinationCard;
	}
	public void setDestinationCard(SabotageCard destinationCard) {
		this.destinationCard = destinationCard;
	}
	
}
