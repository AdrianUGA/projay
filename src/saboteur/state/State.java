package saboteur.state;


public interface State {

    void update();

    void render();

    void onEnter(Object param);

    void onExit();
}
