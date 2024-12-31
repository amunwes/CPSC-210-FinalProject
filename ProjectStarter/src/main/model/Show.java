package model;

import java.util.*;

import org.json.JSONObject;

import persistence.Writable;

// represents a piece of playable visual media (ie. movie, recorded play, tv show, etc), 
// generally refered to as a "show", contains metadata about the show (ie. name,
// runtime, description, genre tags)
// will also track whether or not the show has been watched by the user.
public class Show implements Writable {
    private String name;
    private String description;
    private int runtime;
    private HashSet<String> genres = new HashSet<>();
    private boolean hasWatched;
    
    //REQUIRES: Name has a non-zero length, 
    // runtime is non-zero and conforms to the HH:MM:SS format 
    // ie. 1:20:30 for a runtime of 1 hour 20 mins and 30 seconds
    //EFFECT: name is set to showName, hasWatched defaults to false, 
    // runtime is parsed as a string and saved as an integer number of seconds.
    public Show(String name, String runtime) {
        this.name = name;
        this.runtime = runtimeStringToInt(runtime);
    }

    //REQUIRES: Name has a non-zero length, 
    // runtime is non-zero and conforms to the HH:MM:SS format 
    // ie. 1:20:30 for a runtime of 1 hour 20 mins and 30 seconds
    //EFFECT: name is set to showName, hasWatched defaults to false, 
    // runtime is saved as an integer number of seconds.
    public Show(String name, int runtime) {
        setName(name);
        setRuntime(runtime);
    }

    //MODIFIES: this
    //EFFECT: changes a shows status to watched.
    public void watchShow() {
        setWatched(true);
    }

    //REQUIRES: genre has non-zero length.
    //MODIFIES: this
    //EFFECT: Adds a genre tag to the show.
    public void addGenre(String genre) {
        genres.add(genre);
    }

    //REQUIRES: a string timestamp of the form hh:mm::ss
    //EFFECT: converts the runtime string into and integer representing the runtime in seconds 
    private int runtimeStringToInt(String time) {
        int timeInt = 0;
        String[] timeSegments = time.split(":");
        timeInt += Integer.parseInt(timeSegments[0]) * 60 * 60;
        timeInt += Integer.parseInt(timeSegments[1]) * 60;
        timeInt += Integer.parseInt(timeSegments[2]); 
        return timeInt;
    }

    public HashSet<String> getGenres() {
        return genres;
    }

    public void setWatched(boolean watchStatus) {
        this.hasWatched = watchStatus;
        EventLog.getInstance().logEvent(new Event("Show " + this.name + " watch status updated to: " + watchStatus));
    }

    public boolean getWatched() {
        return hasWatched;
    }

    public void setName(String newName) {
        this.name = newName;
    }
    
    public String getName() {
        return name;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtimeInt) {
        this.runtime = runtimeInt;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        json.put("runtime", runtime);
        json.put("genres", genres);
        json.put("hasWatched", hasWatched);
        return json;
    }
}
