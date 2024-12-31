// Referenced from the JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

package persistence;

import model.Series;
import model.Show;
import model.Watchlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    private Show e1;
    private Show e2;
    private Show e3;
    private ArrayList<Show> episodes;
    private Series s1;
    private Series s2;
    private Show m1;
    private Watchlist w1;

    @BeforeEach
    void runBefore() {
        e1 = new Show("S1E1 - ???", "00:27:20");
        e2 = new Show("S1E2 - ????", "00:26:05");
        e3 = new Show("S1E3 - ??", "00:26:55");
        episodes = new ArrayList<>();
        episodes.add(e1);
        episodes.add(e2);
        s1 = new Series("test series", episodes); 
        episodes.clear();
        episodes.add(e3);
        s2 = new Series("test series 2", episodes);
        m1 = new Show("Deadpool & Wolverine", "02:07:00");
        w1 = new Watchlist();
    }

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyWatchlist() {
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyWatchlist.json");
            writer.open();
            writer.write(w1);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyWatchlist.json");
            Watchlist wl = reader.read();
            assertEquals(w1.getSeries().size(), wl.getSeries().size());
            assertEquals(w1.getShows().size(), wl.getShows().size());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            setupWriterGeneralTest();
            JsonReader reader = new JsonReader("./data/testWriterGeneralWatchlist.json");
            Watchlist wl = reader.read();
            ArrayList<Show> ogShows = w1.getShows();
            HashMap<Series, Boolean> ogSeries = w1.getSeries();
            ArrayList<Show> shows = wl.getShows();
            HashMap<Series, Boolean> series = wl.getSeries();
            assertEquals(w1.getSeries().size(), series.size());
            assertEquals(w1.getShows().size(), shows.size());
            assertEquals(ogShows.get(0).getName(), shows.get(0).getName());
            assertEquals(ogShows.get(0).getRuntime(), shows.get(0).getRuntime());
            assertEquals(ogShows.get(1).getName(), shows.get(1).getName());
            assertEquals(ogShows.get(1).getRuntime(), shows.get(1).getRuntime());
            Set<Series> ogKeySet = ogSeries.keySet();
            Set<Series> keySet = series.keySet();
            assertEquals(ogKeySet.size(), keySet.size());
         
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    private void setupWriterGeneralTest() throws FileNotFoundException {
        w1.addSeries(s1, false);
        w1.addSeries(s2, true);
        w1.addShow(e3);
        w1.addShow(m1);
        JsonWriter writer = new JsonWriter("./data/testWriterGeneralWatchlist.json");
        writer.open();
        writer.write(w1);
        writer.close();
    }
}