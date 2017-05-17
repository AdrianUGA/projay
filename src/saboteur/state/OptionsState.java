package saboteur.state;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import saboteur.App;
import saboteur.GameStateMachine;
import saboteur.model.Game;
import saboteur.tools.Resources;

public class OptionsState implements State {

    private GameStateMachine gsm;
    private Game game;
    private Stage primaryStage;

    @FXML private Slider sliderMusic;
    @FXML private SVGPath volumeOff;
    @FXML private SVGPath volumeOn;
    @FXML private SVGPath volumeDown;

    public OptionsState(GameStateMachine gsm, Game game, Stage primaryStage){
        this.gsm = gsm;
        this.game = game;
        this.primaryStage = primaryStage;
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
            Pane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            this.primaryStage.setScene(scene);
            this.primaryStage.setFullScreen(true);
            this.primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            sliderMusic.setValue(Resources.volume*100);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onExit() {

    }
    
    @FXML
    private void cancelOptionButtonAction() {
    	this.gsm.change("mainMenu");
    }

    @FXML
    private void okOptionButtonAction() { this.gsm.change("mainMenu"); }

    @FXML
    private void music(){
        if(sliderMusic.getValue() <= 5) //volume fermé/très faible
        {
           volumeOff.setVisible(true);
           volumeOn.setVisible(false);
           volumeDown.setVisible(false);

           Resources.loadMusic().setMute(true);

        }
        else if(sliderMusic.getValue() >50) //volume fort
        {
            volumeOff.setVisible(false);
            volumeOn.setVisible(true);
            volumeDown.setVisible(false);
            Resources.loadMusic().setMute(false);
            Resources.loadMusic().setVolume(calculVolume(sliderMusic.getValue()));
            Resources.volume = calculVolume(sliderMusic.getValue());
        }
        else //volume pas très fort
        {
            volumeOff.setVisible(false);
            volumeOn.setVisible(false);
            volumeDown.setVisible(true);
            Resources.loadMusic().setMute(false);
            Resources.loadMusic().setVolume(calculVolume(sliderMusic.getValue()));
            Resources.volume = calculVolume(sliderMusic.getValue());
        }
    }

    private double calculVolume(double value) {
        return value/100.0;
    }
}
