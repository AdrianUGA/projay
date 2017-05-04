package saboteur;

import saboteur.state.State;

import java.util.HashMap;

public class GameStateMachine {

    private HashMap<String, State> states;
    private State currentState;

    public GameStateMachine(){
        this.states = new HashMap<>();
        this.currentState = null;
    }

    public void add(String name, State state){
        this.states.put(name, state);
    }

    public void change(String name, Object params){
        if (this.currentState != null){
            this.currentState.onExit();
        }
        this.currentState = this.states.get(name);
        this.currentState.onEnter(params);
    }

    public void change(String name){
        if (this.currentState != null){
            this.currentState.onExit();
        }
        this.currentState = this.states.get(name);
        this.currentState.onEnter(null);
    }

    public void render(){
        this.currentState.render();
    }

    public void update(){
        this.currentState.update();
    }
}
