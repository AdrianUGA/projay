package saboteur;

import saboteur.state.State;

import java.util.HashMap;
import java.util.Stack;

public class GameStateMachine {

    private HashMap<String, State> states;
    private Stack<State> statesStack;

    public GameStateMachine(){
        this.states = new HashMap<>();
        this.statesStack = new Stack<>();
    }

    public void add(String name, State state){
        this.states.put(name, state);
    }

    public void push(String name){
        this.statesStack.push(this.states.get(name));
        this.statesStack.peek().onEnter(null);
    }

    public void push(String name, Object param){
        this.statesStack.push(this.states.get(name));
        this.statesStack.peek().onEnter(param);
    }

    public void pop(){
        this.statesStack.pop().onExit();
    }

    public void change(String name){
        if (!this.statesStack.empty()){
            this.pop();
        }
        this.statesStack.clear();
        this.push(name);

    }

    public void render(){
        this.statesStack.peek().render();
    }

    public void update(){
        this.statesStack.peek().update();
    }
}
