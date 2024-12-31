package model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SeriesTest {
    private Show e1;
    private Show e2;
    private Show e3;
    private ArrayList<Show> episodes;
    private Series s1;

    @BeforeEach
    void runBefore() {
        
        e1 = new Show("S1E1 - ???", "00:27:20");
        e2 = new Show("S1E2 - ????", "00:26:05");
        e3 = new Show("S1E3 - ??", "00:26:55");
        episodes = new ArrayList<>();
        episodes.add(e1);
        episodes.add(e2);
        episodes.add(e3);

        s1 = new Series("test series",episodes);       
    }
    
    @Test
    void testConstructor() {
        assertEquals("test series", s1.getName());
        assertEquals(episodes, s1.getEpisodeOrder());
        episodes.add(e1);
        assertNotEquals(episodes, s1.getEpisodeOrder());
        assertEquals(3, s1.getEpisodeOrder().size());
    }

    @Test
    void testAddMultipleEpisodes() {
        Show e4 = new Show("S1E4 - ??", "00:26:55");
        Show e5 = new Show("S1E5 - ??", "00:26:55");
        s1.addEpisode(e4, 4);
        assertEquals(e4, s1.getEpisode(4));
        s1.addEpisode(e5, 1);
        assertEquals(5, s1.getEpisodeOrder().size());
        assertEquals(e5, s1.getEpisode(1));
    }

    @Test
    void testRemoveMultipleEpisodes() {
        s1.removeEpisode(1);
        assertEquals(2, s1.getEpisodeOrder().size());
        assertEquals(e2, s1.getEpisode(1));
        s1.removeEpisode(2);
        s1.removeEpisode(1);
        assertEquals(0, s1.getEpisodeOrder().size());
    }

    @Test
    void testGetNextEpisode() {
        s1.setlastWatched(1);
        assertEquals(e2, s1.getNextEpisode());
        s1.setlastWatched(3);
        assertEquals(e1, s1.getNextEpisode());
    }

    @Test
    void testGetRandomEpisode() {
        int e1Count = 0;
        int e2Count = 0;
        int e3Count = 0;
        s1.setlastWatched(1);
        for (int i = 0; i < 1000; i++) {
            Show ep = s1.getRandomEpisode();
            if (ep == e1) {
                e1Count++;
            } else if (ep == e2) {
                e2Count++;
            } else if (ep == e3) {
                e3Count++;
            } else {
                break;
            }
        }
        assertTrue(e1Count >= 0 && e2Count >= 400 && e3Count >= 400);
    }

    @Test
    void testSetDescription() {
        String description = "A sample series of episodes used for testing purposes.";
        s1.setDescription(description);
        assertEquals(description, s1.getDescription());
    }

    @Test
    void testGetEpisodeNum() {
        assertEquals(1, s1.getEpisodeNum(e1));
        assertEquals(2, s1.getEpisodeNum(e2));
        assertEquals(3, s1.getEpisodeNum(e3));
    }

    // @Test
    // void testToJson() {
    //     System.out.println(s1.toJson().toString());

    // }
}
