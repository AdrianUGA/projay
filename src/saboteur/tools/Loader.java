package saboteur.tools;


import org.json.JSONArray;
import org.json.JSONObject;
import saboteur.App;
import saboteur.model.Card.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Loader {

    private Path pathBddFile;

    public Loader(){
        try{
            this.pathBddFile = Paths.get(App.class.getResource("/resources/bdd.json").toURI());
        } catch(Exception e){

        }
    }

    public void loadCard(){
        ArrayList<Card> cards = new ArrayList<>();
        try{
            String text = new String(Files.readAllBytes(this.pathBddFile), StandardCharsets.UTF_8);
            JSONObject rootObj = new JSONObject(text);
            JSONObject cardsObj = rootObj.getJSONObject("cards");
            JSONArray sabotageCardsObj = cardsObj.getJSONArray("sabotageCards");
            sabotageCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    SabotageCard sabotageCard = new SabotageCard(Tool.valueOf(obj.getString("type")));
                    sabotageCard.setFrontImage(obj.getString("frontImage"));
                    sabotageCard.setBackImage(obj.getString("backImage"));
                    cards.add(sabotageCard);
                }
            });
            JSONArray rescueCardsObj = cardsObj.getJSONArray("rescueCards");
            rescueCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    RescueCard rescueCard = new RescueCard(Tool.valueOf(obj.getString("type")));
                    rescueCard.setFrontImage(obj.getString("frontImage"));
                    rescueCard.setBackImage(obj.getString("backImage"));
                    cards.add(rescueCard);
                }
            });
            JSONArray doubleRescueCardsObj = cardsObj.getJSONArray("doubleRescueCards");
            doubleRescueCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    RescueCard doubleRescueCard = new RescueCard(Tool.valueOf(obj.getString("type1")), Tool.valueOf(obj.getString("type2")));
                    rescueCard.setFrontImage(obj.getString("frontImage"));
                    rescueCard.setBackImage(obj.getString("backImage"));
                    cards.add(rescueCard);
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }


}
