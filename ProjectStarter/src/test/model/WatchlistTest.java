package model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WatchlistTest {
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
    void testAddMultipleShows() {
        w1.addShow(m1);
        assertEquals(1, w1.getShows().size());
        w1.addShow(e1);
        w1.addShow(e2);
        assertEquals(3, w1.getShows().size());
    }

    @Test
    void testRemoveMultipleShows() {
        w1.addShow(m1);
        w1.addShow(e1);
        w1.addShow(e2);
        w1.addShow(e3);
        w1.removeShow(e1);
        assertEquals(-1, w1.getShows().indexOf(e1));
        assertEquals(3, w1.getShows().size());
        w1.removeShow(m1);
        w1.removeShow(e2);
        assertEquals(1, w1.getShows().size());
    }

    @Test
    void testAddRemoveMultipleSeries() {
        w1.addSeries(s1, false);
        assertEquals(1, w1.getSeries().size());
        assertEquals(false, w1.getSeries().get(s1));
        w1.addSeries(s2, true);
        assertEquals(2, w1.getSeries().size());
        assertEquals(true, w1.getSeries().get(s2));
        w1.removeSeries(s1);
        assertEquals(1, w1.getSeries().size());
        w1.removeSeries(s2);
        assertEquals(0, w1.getSeries().size());        
    }
    
    @Test
    void testReccomendRandomShow() {
        HashMap<Show, Integer> counter = new HashMap<>();
        counter.put(e1, 0);
        counter.put(e2, 0);
        counter.put(e3, 0);
        counter.put(m1, 0);
        w1.addShow(e1);
        w1.addShow(e2);
        w1.addShow(e3);
        w1.addShow(m1);
        
        for (int i = 0; i < 1000; i++) {
            Show ep = w1.recommendRandomShow();
            counter.put(ep, counter.get(ep) + 1);
        }
        assertTrue(counter.get(e1) > 200);
        assertTrue(counter.get(e2) > 200); 
        assertTrue(counter.get(e3) > 200);
        assertTrue(counter.get(m1) > 200);
    }

    @Test
    void testReccomendRandomSeries() {
        HashMap<Series, Integer> counter = new HashMap<>();
        counter.put(s1, 0);
        counter.put(s2, 0);

        w1.addSeries(s1, false);
        w1.addSeries(s2, true);
        
        for (int i = 0; i < 1000; i++) {
            Series series = w1.recommendRandomSeries();
            counter.put(series, counter.get(series) + 1);
        }

        assertTrue(counter.get(s1) > 400);
        assertTrue(counter.get(s2) > 400);
    }

    // @Test
    // void testToJson() {
    //     // m1.setDescription("some not null description");
    //     // w1.addShow(m1);
    //     // s1.setDescription("some description of the series for test");
    //     // w1.addSeries(s1, false);
    //     w1.addSeries(s2, true);
    //     System.out.println(w1.toJson().toString());

    // }
}
