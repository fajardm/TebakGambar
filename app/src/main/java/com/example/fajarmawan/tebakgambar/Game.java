package com.example.fajarmawan.tebakgambar;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by fajarmawan on 1/11/16.
 */
public class Game {
    public static final int SCORE_ = 5;
    public static String id;
    public static int score = 0;
    public static int karakterKe = 0;
    public static int nyawa = 3;

    public static void load() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size() ; i++) {
                        Game.id = objects.get(i).getObjectId();
                        Game.score = objects.get(i).getInt("score");
                        Game.karakterKe = objects.get(i).getInt("karakterKe");
                        Game.nyawa = objects.get(i).getInt("nyawa");
                    }
                    System.out.println("IDOBJECT: "+Game.id);
                } else {
                    System.out.println("ERROR: "+e.getMessage());
                }
            }
        });
    }

    public static void update(final int scores, final int karakters, final int nyawas) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.fromLocalDatastore();
        query.getInBackground(Game.id, new GetCallback<ParseObject>() {
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    game.put("score", scores);
                    game.put("karakterKe", karakters);
                    game.put("nyawa", nyawas);
                    game.pinInBackground();
                    Game.load();
                }
            }
        });
    }

//    public static void kurangiNyawa() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//        query.fromLocalDatastore();
//        query.getInBackground(Game.id, new GetCallback<ParseObject>() {
//            public void done(ParseObject game, ParseException e) {
//                if (e == null) {
//                    if (Game.nyawa > 0) {
//                        System.out.println("kurangi" + Game.id);
//                        game.put("nyawa", Game.nyawa - 1);
//                        game.pinInBackground();
//                        Game.load();
//                    }
//                } else {
//                    System.out.println("Error: " + e.getMessage());
//                }
//            }
//        });
//    }
//
//    public static void reset() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
//        query.fromLocalDatastore();
//        query.getInBackground(Game.id, new GetCallback<ParseObject>() {
//            public void done(ParseObject game, ParseException e) {
//                if (e == null) {
//                    game.put("karakterKe", 0);
//                    game.put("nyawa", 3);
//                    game.pinInBackground();
//                    Game.load();
//                }
//            }
//        });
//    }
}
