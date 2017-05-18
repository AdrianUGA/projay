package saboteur.state;


import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import saboteur.GameStateMachine;
import saboteur.model.Game;

public abstract class State {

    protected GameStateMachine gsm;
    protected Game game;
    protected Stage primaryStage;

    public State(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
    }

    public void update(){

    }

    public void render(){

    }

    public void onEnter(Object param){

    }

    public void onExit(){

    }

    protected void changeLayout(Pane newPane){
        ((StackPane)primaryStage.getScene().getRoot()).getChildren().clear();
        ((StackPane)primaryStage.getScene().getRoot()).getChildren().addAll(newPane);
    }
}
