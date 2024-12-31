// Referenced from the JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

package persistence;

import model.Series;
import model.Show;
import model.Watchlist;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

import org.json.*;


public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads watchlist from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Watchlist read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);

        return parseWatchlist(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses worklist from JSON object and returns it
    private Watchlist parseWatchlist(JSONObject jsonObject) {
        Watchlist watchlist = new Watchlist();
        addShows(watchlist, jsonObject);
        addSeriesPlural(watchlist, jsonObject);
        return watchlist;
    }

    // MODIFIES: watchlist
    // EFFECTS: parses shows from JSON object and adds them to watchlist's shows list
    private void addShows(Watchlist watchlist, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("shows");
        for (Object json : jsonArray) {
            JSONObject nextShow = (JSONObject) json;
            
            Show show = parseShowFromJson(nextShow);

            watchlist.addShow(show);            
        }
    }

    // EFFECTS: parses JSON Object representing a show object and returns it as a Show object
    private Show parseShowFromJson(JSONObject nextShow) {
        JSONArray genreList = nextShow.getJSONArray("genres");
        boolean hasWatched = nextShow.getBoolean("hasWatched");
        String name = nextShow.getString("name");
        String description = nextShow.optString("description", "No description");
        int runtime = nextShow.getInt("runtime");

        Show show = new Show(name, runtime);
        show.setDescription(description);
        
        show.setWatched(hasWatched);
        for (int i = 0; i < genreList.length(); i++) {
            show.addGenre(genreList.getString(i));
        }
        return show;
    }

    // MODIFIES: watchlist
    // EFFECTS: parses the JSON object representing the hashmap of series objects and adds them to the watchlist.
    private void addSeriesPlural(Watchlist watchlist, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("series");
        for (Object json : jsonArray) {
            JSONObject nextSeriesPair = (JSONObject) json;
            JSONObject nextSeries = nextSeriesPair.getJSONObject("series");
            
            int lastWatched = nextSeries.getInt("lastWatched");
            String name = nextSeries.getString("name");
            String description = nextSeries.optString("description", "No description");

            ArrayList<Show> episodeOrder = new ArrayList<Show>();
            JSONArray episodeListJson = nextSeries.getJSONArray("episodeOrder");

            for (Object jsonEpisode : episodeListJson) {
                JSONObject nextShow = (JSONObject) jsonEpisode;
                
                Show show = parseShowFromJson(nextShow);
    
                episodeOrder.add(show);            
            }

            Series series = new Series(source, episodeOrder);
            series.setlastWatched(lastWatched);
            series.setName(name);
            series.setDescription(description);
            
            boolean isSerialized = nextSeriesPair.getBoolean("isSerialized");
            
            watchlist.addSeries(series, isSerialized);
        } 

    }
}