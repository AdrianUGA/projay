package saboteur.model;

import saboteur.model.Card.Card;

public class OperationTrash extends Operation {


	private static final long serialVersionUID = -5255213409983264528L;

	public OperationTrash(Player sourcePlayer, Card card) {
		super(sourcePlayer, card);
	}

	@Override
	public void exec(Game game) {
		this.getSourcePlayer().removeHandCard(this.getCard());
		game.getTrash().add(this.getCard());
	}
	
	@Override
	public void execReverse(Game game) {
		this.getSourcePlayer().addHandCard(this.getCard());
		game.getTrash().remove(this.getCard());
	}
	
	@Override
	public boolean isOperationTrash(){
		return true;
	}
	
	@Override
	public void displayOperationInformation(){
		System.out.print("OperationTrash : Card = " + this.getCard().getClassName());
	}

}
