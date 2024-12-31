package model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShowTest {

    private Show s1;
    private Show s2;

    @BeforeEach
    void runBefore() {
        s1 = new Show("Deadpool & Wolverine", "02:07:00");
        s2 = new Show("S1E3 - Lord Snow", "00:58:00");
    }
    
    @Test
    void testConstructor() {
        assertEquals("Deadpool & Wolverine", this.s1.getName());
        assertFalse(this.s1.getWatched());
        assertEquals(7620, s1.getRuntime());
    }

    @Test
    void testWatchShow() {
        s1.watchShow();
        assertEquals(true, s1.getWatched());
        s1.watchShow();
        assertEquals(true, s1.getWatched());
        s1.setWatched(false);
        assertEquals(false, s1.getWatched());
    }

    @Test
    void testaddGenre() {
        HashSet<String> genres = new HashSet<>();
        genres.add("action");
        s2.addGenre("action");
        assertEquals(genres, s2.getGenres());
    }

    @Test
    void testSetDescription() {
        String description = "Deadpool recruits Wolverine for some multiverse hopping adventures.";
        s1.setDescription(description);
        assertEquals(description, s1.getDescription());
    }

    // @Test
    // void testToJson() {
    //     System.out.println(s1.toJson().toString());

    // }
}
