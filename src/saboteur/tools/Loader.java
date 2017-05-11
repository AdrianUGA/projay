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
import java.util.HashMap;

public class Loader {

    public HashMap<String, ArrayList<Card>> loadCard(){
        HashMap<String, ArrayList<Card>> cards = new HashMap<>();
        try{
            Path pathBddFile = Paths.get(App.class.getResource("/resources/bdd.json").toURI());
            String text = new String(Files.readAllBytes(pathBddFile), StandardCharsets.UTF_8);
            JSONObject rootObj = new JSONObject(text);
            JSONObject cardsObj = rootObj.getJSONObject("cards");

            //load goldCards
            cards.put("path", new ArrayList<>());
            cards.put("startPath", new ArrayList<>());
            cards.put("goalPath", new ArrayList<>());
            JSONArray pathCardsObj = cardsObj.getJSONArray("pathCards");
            pathCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                JSONArray cardinalObj = obj.getJSONArray("cardinal");
                String[] cardinal = new String[cardinalObj.length()];
                for (int i = 0; i < cardinalObj.length(); i++){
                    cardinal[i] = cardinalObj.getString(i);
                }
                PathCard pathCard = new PathCard(cardinal, obj.getBoolean("isCulDeSac"), obj.getBoolean("isStart"), obj.getBoolean("isGoal"), obj.getBoolean("hasGold"));
                pathCard.setFrontImage(obj.getString("frontImage"));
                pathCard.setBackImage(obj.getString("backImage"));
                if(pathCard.isStart()){
                    cards.get("startPath").add(pathCard);
                } else if (pathCard.isGoal()){
                    cards.get("goalPath").add(pathCard);
                } else{
                    cards.get("path").add(pathCard);
                }
            });

            //load goldCards
            cards.put("gold", new ArrayList<>());
            JSONArray goldCardsObj = cardsObj.getJSONArray("goldCards");
            goldCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    GoldCard goldCard = new GoldCard(obj.getInt("value"));
                    goldCard.setFrontImage(obj.getString("frontImage"));
                    cards.get("gold").add(goldCard);
                }
            });

            //load sabotageCards
            cards.put("action", new ArrayList<>());
            JSONArray sabotageCardsObj = cardsObj.getJSONArray("sabotageCards");
            sabotageCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    SabotageCard sabotageCard = new SabotageCard(Tool.valueOf(obj.getString("type")));
                    sabotageCard.setFrontImage(obj.getString("frontImage"));
                    sabotageCard.setBackImage(obj.getString("backImage"));
                    cards.get("action").add(sabotageCard);
                }
            });

            //load rescueCards
            JSONArray rescueCardsObj = cardsObj.getJSONArray("rescueCards");
            rescueCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    RescueCard rescueCard = new RescueCard(Tool.valueOf(obj.getString("type")));
                    rescueCard.setFrontImage(obj.getString("frontImage"));
                    rescueCard.setBackImage(obj.getString("backImage"));
                    cards.get("action").add(rescueCard);
                }
            });

            //load doubleRescueCards
            JSONArray doubleRescueCardsObj = cardsObj.getJSONArray("doubleRescueCards");
            doubleRescueCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    DoubleRescueCard doubleRescueCard = new DoubleRescueCard(Tool.valueOf(obj.getString("type1")), Tool.valueOf(obj.getString("type2")));
                    doubleRescueCard.setFrontImage(obj.getString("frontImage"));
                    doubleRescueCard.setBackImage(obj.getString("backImage"));
                    cards.get("action").add(doubleRescueCard);
                }
            });

            //load planCard
            JSONObject planCardObj = cardsObj.getJSONObject("planCard");
            for (int i = 0; i < planCardObj.getInt("number"); i++){
                PlanCard planCard = new PlanCard();
                planCard.setFrontImage(planCardObj.getString("frontImage"));
                planCard.setBackImage(planCardObj.getString("backImage"));
                cards.get("action").add(planCard);
            }

            //load collapseCard
            JSONObject collapseCardObj = cardsObj.getJSONObject("collapseCard");
            for (int i = 0; i < collapseCardObj.getInt("number"); i++){
                CollapseCard collapseCard = new CollapseCard();
                collapseCard.setFrontImage(planCardObj.getString("frontImage"));
                collapseCard.setBackImage(planCardObj.getString("backImage"));
                cards.get("action").add(collapseCard);
            }
            return cards;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}