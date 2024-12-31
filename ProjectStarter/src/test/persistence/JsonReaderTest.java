// Referenced from the JsonSerialization Demo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

package persistence;

import org.junit.jupiter.api.Test;

import model.Series;
import model.Show;
import model.Watchlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Watchlist wl = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWatchlist() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyWatchlist.json");
        try {
            Watchlist wl = reader.read();
            assertEquals(0, wl.getSeries().size());
            assertEquals(0, wl.getShows().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralWatchlist.json");
        try {
            Watchlist wl = reader.read();
            
            ArrayList<Show> shows = wl.getShows();
            HashMap<Series, Boolean> series = wl.getSeries();
            
            assertEquals(2, series.size());
            assertEquals(2, shows.size());

            assertEquals("S1E3 - ??", shows.get(0).getName());
            assertEquals(1615, shows.get(0).getRuntime());

            assertEquals("Deadpool & Wolverine", shows.get(1).getName());
            assertEquals(7620, shows.get(1).getRuntime());
         
            Set<Series> keySet = series.keySet();
            
            assertEquals(2, keySet.size());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}