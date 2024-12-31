package model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// A class representing a collection of episodes of a show, 
// having the name of the show, a description, and episodes of the show in sequential order.
public class Series implements Writable {
    private String name;
    private String description;
    private ArrayList<Show> episodeOrder;
    private int lastWatched;
    
    // REQUIRES: episodes is the correct sequential order of episodes for the show
    // MODIFIES: this
    // EFFECTS: creates a series with the supplied list of episodes as the assumed sequential order
    // of episodes for the series, must make a copy of the list and not reference the parameter list.
    // must also initialize lastWatched to 0
    // @SuppressWarnings("unchecked")
    public Series(String name, ArrayList<Show> episodes) {
        this.name = name;
        this.lastWatched = 0;
        episodeOrder = new ArrayList<Show>(episodes); 
    }

    // REQUIRES: the episode is added with the correct episode number not 0-indexed, ie. episode 2 will be entered as 2
    // MODIFIES: this
    // EFFECTS: the episode of the show is added to the episodeOrder List based on it's episode number.
    // ie. episode #1 will be inserted to the list at array position 0.
    // if episode num is larger than the list size then episode is inserted at the end of the list.
    public void addEpisode(Show episode, int episodeNum) {
        ArrayList<Show> episodeList = getEpisodeOrder();
        if (episodeNum >= episodeList.size()) {
            episodeList.add(episode);
        } else {
            episodeList.add(episodeNum - 1, episode);
        }
    }

    // REQUIRES: the episodeOrder.size() > episodeNum >= 0
    // MODIFIES: this
    // EFFECTS: removes the episode at the corresponding episodeNum from the series
    public void removeEpisode(int episodeNum) {
        getEpisodeOrder().remove(episodeNum - 1);
    }

    // MODIFIES: this
    // EFFECTS: retrieves the next episode in the series, 
    // if the last watched episode was the finale will return the first episode of the series
    public Show getNextEpisode() {
        int nextEp = getLastWatched() + 1;
        if (nextEp > getEpisodeOrder().size()) {
            return getEpisode(1);
        } else {
            return getEpisode(nextEp);
        }
    }

    // EFFECTS: retrieves a random episode of the series
    public Show getRandomEpisode() {
        int lastEp = getLastWatched();
        int randEp;
        do {
            randEp = 1 + (int) (Math.random() * (getEpisodeOrder().size()));
        } while (lastEp == randEp);

        return getEpisode(randEp);
    }

    // REQUIRES: episodeOrder.size() > episodeNum >= 0
    // EFFECTS: retrieves the corresponding episode of the series.
    public Show getEpisode(int episodeNum) {
        ArrayList<Show> episodes = getEpisodeOrder();
        return episodes.get(episodeNum - 1);
    }

    // REQUIRES: episode is in series
    // EFFECTS: retrieves the episode's number
    public int getEpisodeNum(Show episode) {
        return getEpisodeOrder().indexOf(episode) + 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public ArrayList<Show> getEpisodeOrder() {
        return episodeOrder;
    }

    public int getLastWatched() {
        return lastWatched;
    }

    public void setlastWatched(int episodeNum) {
        lastWatched = episodeNum;
        EventLog.getInstance().logEvent(new Event("Series " + this.name + " last watched updated to: " + episodeNum));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descString) {
        description = descString;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        json.put("episodeOrder", episodesToJson());
        json.put("lastWatched", lastWatched);
        return json;
    }

    // EFFECTS: returns episodes in the series as a JSON Array
    private JSONArray episodesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Show ep : episodeOrder) {
            jsonArray.put(ep.toJson());
        }
        return jsonArray;
    }
}
