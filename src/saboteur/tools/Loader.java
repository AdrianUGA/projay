package saboteur.tools;


import org.json.JSONArray;
import org.json.JSONObject;
import saboteur.App;
import saboteur.model.Card.ActionCard;
import saboteur.model.Card.ActionCardToPlayer;
import saboteur.model.Card.Card;
import saboteur.model.Card.SabotageCard;

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
                System.out.println(obj);
                for (int i = 0; i < obj.getInt("number"); i++){
                    obj.getString("type");
                    cards.add(new SabotageCard(ActionCardToPlayer.));
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }


}
