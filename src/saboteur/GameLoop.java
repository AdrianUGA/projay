package saboteur;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {

    private GameStateMachine gsm;

    public GameLoop(GameStateMachine gsm){
        this.gsm = gsm;
    }

    @Override
    public void handle(long now) {
        gsm.update();
        gsm.render();
    }
}
