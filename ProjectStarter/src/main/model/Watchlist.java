// Json convertion logic referenced from this stack overflow post
//https://stackoverflow.com/questions/12155800/how-to-convert-hashmap-to-json-object-in-java

package model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;


// Class representing a user's watchlist. 
// The watchlist will contain a list of shows and series
// wont need a constructor because initial state will be empty until filled by the user. 
public class Watchlist implements Writable {
    private ArrayList<Show> shows = new ArrayList<Show>();
    private HashMap<Series, Boolean> series = new HashMap<>();
    // private int currShow;
    // private Show upNext;

    // // REQUIRES: Shows list or series must not be empty
    // // MODIFIES: this, Show, Series
    // // EFFECTS: queues up the next episode in a series
    // public void getNextEpisode() {
    //     //stub
    // }

    // REQUIRES: Shows list must not be empty
    // EFFECTS: reccommends a random show on the watchlist to continue watching.  
    public Show recommendRandomShow() {
        int randVal = (int)(Math.random() * shows.size());
        return shows.get(randVal);
    }

    // REQUIRES: Series list must not be empty
    // EFFECTS: reccommends a random series on the watchlist to continue watching.  
    public Series recommendRandomSeries() {
        int randVal = (int)(Math.random() * series.size());
        ArrayList<Series> seriesKeys = new ArrayList<Series>(series.keySet());
        return seriesKeys.get(randVal);
    }
    

    // REQUIRES: show should not be a part of a series
    // MODIFIES: this
    // EFFECTS: adds a show to the list of shows
    public void addShow(Show show) {
        ArrayList<Show> showList = getShows();
        showList.add(show);
        EventLog.getInstance().logEvent(new Event("Show " + show.getName() + " added to watchlist"));
    }
    
    // REQUIRES: show be an individual show in the watchlist and not an episode of a series.
    // MODIFIES: this
    // EFFECTS: removes the show from the watchlist
    public void removeShow(Show show) {
        ArrayList<Show> showList = getShows();
        showList.remove(show);
        EventLog.getInstance().logEvent(new Event("Show " + show.getName() + " removed from watchlist"));
    }

    // REQURES: If the series is meant to be viewed in order isSeriealized must be True
    // otherwise it will be seen at random
    // MODIFIES: this
    // EFFECTS: adds the given series to our watchlist and records whether it should be seen in series or at random.
    public void addSeries(Series series, boolean isSerialized) {
        HashMap<Series, Boolean> seriesList = getSeries();
        seriesList.put(series, isSerialized);
        EventLog.getInstance().logEvent(new Event("Show " + series.getName() + " added to watchlist"));
    }

    // REQUIRES: series should exist in the watchlist already
    // MODIFIES: this
    // EFFECTS: removes the series from the watchlist
    public void removeSeries(Series series) {
        HashMap<Series, Boolean> seriesList = getSeries();
        seriesList.remove(series);
        EventLog.getInstance().logEvent(new Event("Show " + series.getName() + " removed from watchlist"));
    }

    public HashMap<Series, Boolean> getSeries() {
        return series;
    }

    public ArrayList<Show> getShows() {
        return shows;
    }

    @Override
    public JSONObject toJson() {   
        JSONObject json = new JSONObject();
        json.put("shows", showsToJson());
        // json.put("series", series);
        json.put("series", seriesToJson());
        return json;
    }

    // EFFECTS: returns shows in watchlist as jsonArray
    private JSONArray showsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Show m : shows) {
            jsonArray.put(m.toJson());
        }
        return jsonArray;
    }
    
    // EFFECTS: returns series in watchlist as jsonArray
    private JSONArray seriesToJson() {
        JSONArray map = new JSONArray();
        for (Series m : series.keySet()) {
            JSONObject seriesEntry = new JSONObject();
            seriesEntry.put("series", m.toJson());
            seriesEntry.put("isSerialized", series.get(m));
            map.put(seriesEntry);
        }
        return map;
    }



}
