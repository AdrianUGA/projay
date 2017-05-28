package saboteur.model;

//      ////^\\\\
//      | ^   ^ |
//     @ (o) (o) @
//      |   <   |
//      |  ___  |
//       \_____/
//     ____|  |____
//    /    \__/    \
//   /              \
//  /\_/|        |\_/\
// / /  | L3Info |  \ \
//( <   |Grenoble|   > )
// \ \  |        |  / /
//  \ \ |________| / /
//   \ \|<I_D_I__|/ /
//    \ \ / I  \ / /
//     \ /  I   \ /
//     |         |
//     |    |    |
//     |    |    |
//     |    |    |
//     |    |    |
//     | ## | ## |
//     |    |    |
//     |    |    |
//     |____|____|
//     (____(____)
//      _| | _| |
//  cccC__Cccc___)

public class Human extends Player {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5780673417385421937L;

	public Human(Game game, String name) {
		super(game, name);
		// TODO Auto-generated constructor stub
	}

	public boolean isHuman(){
		return true;
	}
}
