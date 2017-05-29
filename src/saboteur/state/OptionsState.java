package saboteur.state;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Icon;
import saboteur.tools.Resources;

public class OptionsState extends State {

    @FXML private Slider sliderMusic;
    @FXML private SVGPath volume;

    public OptionsState(GameStateMachine gsm, Game game, Stage primaryStage){
        super(gsm, game, primaryStage);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEnter(Object param) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/saboteur/view/options.fxml"));
            loader.setController(this);
            Pane pane = loader.load();
            sliderMusic.setValue(Resources.volume*100);
            changeVolume(Resources.volume*100);
            sliderMusic.valueChangingProperty().addListener((observable, oldValue, newValue) -> changeVolume(sliderMusic.getValue()));
            this.changeLayout(pane);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

    }

    @FXML
    private void okOptionButtonAction() {
        this.gsm.change("mainMenu");
    }

    @FXML
    private void music(){
        changeVolume(sliderMusic.getValue());
    }

    private void changeVolume(double newVolume){
        if(newVolume <= 5) //volume fermé/très faible
        {
            volume.setContent(Icon.volumeOff);
            Resources.loadMusic().setMute(true);
            Resources.volume = 0;

        }
        else if(newVolume >50) //volume fort
        {
            volume.setContent(Icon.volumeOn);
            Resources.loadMusic().setMute(false);
            Resources.loadMusic().setVolume(calculVolume(newVolume));
            Resources.volume = calculVolume(newVolume);
        }
        else //volume pas très fort
        {
            volume.setContent(Icon.volumeDown);
            Resources.loadMusic().setMute(false);
            Resources.loadMusic().setVolume(calculVolume(newVolume));
            Resources.volume = calculVolume(newVolume);
        }
    }

    private double calculVolume(double value) {
        return value/100.0;
    }
}
