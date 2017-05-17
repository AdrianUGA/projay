package saboteur.tools;


import org.json.JSONArray;
import org.json.JSONObject;
import saboteur.App;
import saboteur.model.Card.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

public class Loader {

    public final static String savedFolder = ".saves/";
    public final static String configFolder = ".config/";
    
    //To load config :
    public final static int indexPlayerType = 0;
    public final static int indexPlayerName = 1;
    public final static int beginIndexPlayerHand = 3;
    public final static int indexIdCardToPlay = 0;
    public final static int indexPositionX = 2;
    public final static int indexPositionY = 3;
    public final static int indexReverseOrNot = 4;

    public ArrayList<String> loadSavedFile(){
        ArrayList<String> savedFiles = new ArrayList<>();
        Path savesPath = Paths.get(savedFolder);
        try{
            Files.walk(savesPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> savedFiles.add(path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedFiles;
    }

    public Deck loadCard(){
        try{
            Path pathBddFile = Paths.get(App.class.getResource("/resources/bdd.json").toURI());
            String text = new String(Files.readAllBytes(pathBddFile), StandardCharsets.UTF_8);
            JSONObject rootObj = new JSONObject(text);
            JSONObject cardsObj = rootObj.getJSONObject("cards");

            //load pathCards in otherCards|startPathCards|goalPathCards List
            LinkedList<Card> otherCards = new LinkedList<>();
            ArrayList<PathCard> startPathCard = new ArrayList<>();
            ArrayList<PathCard> goalPathCards = new ArrayList<>();
            JSONArray pathCardsObj = cardsObj.getJSONArray("pathCards");
            pathCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                JSONArray cardinalObj = obj.getJSONArray("cardinal");
                String[] cardinal = new String[cardinalObj.length()];
                for (int i = 0; i < cardinalObj.length(); i++){
                    cardinal[i] = cardinalObj.getString(i);
                }
                PathCard pathCard = new PathCard(cardinal, obj.getBoolean("isCulDeSac"), obj.getBoolean("isStart"), obj.getBoolean("isGoal"), obj.getBoolean("hasGold"));
                pathCard.setId(obj.getInt("id"));
                pathCard.setFrontImage(obj.getString("frontImage"));
                pathCard.setBackImage(obj.getString("backImage"));
                if(pathCard.isStart()){
                    startPathCard.add(pathCard);
                } else if (pathCard.isGoal()){
                    pathCard.setVisible(false);
                    goalPathCards.add(pathCard);
                } else{
                    otherCards.add(pathCard);
                }
            });

            //load goldCards in goldCards List
            LinkedList<GoldCard> goldCards = new LinkedList<>();
            JSONArray goldCardsObj = cardsObj.getJSONArray("goldCards");
            goldCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    GoldCard goldCard = new GoldCard(obj.getInt("value"));
                    goldCard.setId(obj.getInt("id"));
                    goldCard.setFrontImage(obj.getString("frontImage"));
                    goldCards.add(goldCard);
                }
            });

            //load sabotageCards in otherCards List
            JSONArray sabotageCardsObj = cardsObj.getJSONArray("sabotageCards");
            sabotageCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    SabotageCard sabotageCard = new SabotageCard(Tool.valueOf(obj.getString("type")));
                    sabotageCard.setId(obj.getInt("id"));
                    sabotageCard.setFrontImage(obj.getString("frontImage"));
                    sabotageCard.setBackImage(obj.getString("backImage"));
                    otherCards.add(sabotageCard);
                }
            });

            //load rescueCards in otherCards List
            JSONArray rescueCardsObj = cardsObj.getJSONArray("rescueCards");
            rescueCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    RescueCard rescueCard = new RescueCard(Tool.valueOf(obj.getString("type")));
                    rescueCard.setId(obj.getInt("id"));
                    rescueCard.setFrontImage(obj.getString("frontImage"));
                    rescueCard.setBackImage(obj.getString("backImage"));
                    otherCards.add(rescueCard);
                }
            });

            //load doubleRescueCards in otherCards List
            JSONArray doubleRescueCardsObj = cardsObj.getJSONArray("doubleRescueCards");
            doubleRescueCardsObj.forEach(item -> {
                JSONObject obj = (JSONObject) item;
                for (int i = 0; i < obj.getInt("number"); i++){
                    DoubleRescueCard doubleRescueCard = new DoubleRescueCard(Tool.valueOf(obj.getString("type1")), Tool.valueOf(obj.getString("type2")));
                    doubleRescueCard.setId(obj.getInt("id"));
                    doubleRescueCard.setFrontImage(obj.getString("frontImage"));
                    doubleRescueCard.setBackImage(obj.getString("backImage"));
                    otherCards.add(doubleRescueCard);
                }
            });

            //load planCard in otherCards List
            JSONObject planCardObj = cardsObj.getJSONObject("planCard");
            for (int i = 0; i < planCardObj.getInt("number"); i++){
                PlanCard planCard = new PlanCard();
                planCard.setId(planCardObj.getInt("id"));
                planCard.setFrontImage(planCardObj.getString("frontImage"));
                planCard.setBackImage(planCardObj.getString("backImage"));
                otherCards.add(planCard);
            }

            //load collapseCard in otherCards List
            JSONObject collapseCardObj = cardsObj.getJSONObject("collapseCard");
            for (int i = 0; i < collapseCardObj.getInt("number"); i++){
                CollapseCard collapseCard = new CollapseCard();
                collapseCard.setId(planCardObj.getInt("id"));
                collapseCard.setFrontImage(planCardObj.getString("frontImage"));
                collapseCard.setBackImage(planCardObj.getString("backImage"));
                otherCards.add(collapseCard);
            }

            Deck deck = new Deck();
            deck.setGoalPathCards(goalPathCards);
            deck.setStartPathCard(startPathCard);
            deck.setOtherCards(otherCards);
            deck.setGoldCards(goldCards);
            return deck;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}