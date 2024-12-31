package ui;

import java.io.IOException;
import java.util.*;

import model.Series;
import model.Show;
import model.Watchlist;
import persistence.JsonReader;
import persistence.JsonWriter;

// Credit to whoever made the teller app seen in class, I looked at the ui code while designing my ui.
// Class responsible for taking and responding to user input, acting as a standin for a Netflix-like app.
public class ConsoleInterface {

    private ArrayList<Show> moviesCatalogue = new ArrayList<Show>();
    private ArrayList<Series> seriesCatalogue = new ArrayList<Series>();
    private Watchlist userWatchlist = new Watchlist();
    private Scanner input;

    // EFFECTS: generates the entertainment catalogue the user will interact with and then starts the user input loop. 
    public ConsoleInterface() {
        generateCatalogue();
        System.out.println("Welcome, Type: help to see a list of commands ");
        input = new Scanner(System.in);

        runApplication();
    }

    // MODIFIES: this
    // EFFECTS: processes the user's input
    private void runApplication() {
        String command = null;

        while (true) {
            command = input.nextLine();
            command = command.toLowerCase();   
            if (command.equals("quit")) {
                break;
            } else {
                processCommand(command);
            } 
        }
    }

    // MODIFIES: this
    // EFFECTS: processes the user's command
    private void processCommand(String command) {
        if (command.equals("help")) {
            displayMenu();
        } else if (command.equals("c") || command.equals("catalogue")) {
            displayCatalogue();
        } else if (command.equals("v") || command.equals("watchlist")) {
            displayUsersWatchlist();
        } else if (command.equals("a") || command.equals("add")) {
            addToUsersWatchlist();
        } else if (command.equals("r") || command.equals("remove")) {
            removeFromUsersWatchlist();
        } else if (command.equals("w") || command.equals("watch")) {
            watchShowDialogue();
        } else if (command.equals("s") || command.equals("save")) {
            saveWatchlistDialogue();
        } else if (command.equals("l") || command.equals("load")) {
            loadWatchlistDialogue();
        } 
    }
    
    // EFFECTS: displays menu of options to the user
    private void displayMenu() {
        System.out.println("*******Comands********");
        System.out.println("help : displays the menu of commands");
        System.out.println("v or watchlist : view your current watchlist");
        System.out.println("c or catalogue : shows the available media catalogue");
        System.out.println(
                "a or add : brings up the diaglogue to add media from the available media catalogue to your watchlist");
        System.out.println("r or remove : brings up the dialogue to remove media from your watchlist");
        System.out.println("w or watch : brings up the dialogue to watch TV");
        System.out.println("s or save : saves the current watchlist to file");
        System.out.println("l or load : loads a watchlist from file");

        System.out.println("quit : quit the application");
    }
    
    // EFFECTS: displays the full catalogue of media the user can watch and their watch status
    private void displayCatalogue() {
        System.out.println("===== Movies =====");
        for (Show m : moviesCatalogue) {
            System.out.println(m.getName() + "     Seen: " + m.getWatched());
        }
        System.out.println("===== TV shows =====");
        for (Series s : seriesCatalogue) {
            boolean seen = false;
            for (Show ep : s.getEpisodeOrder()) {
                if (ep.getWatched() && !seen) {
                    seen = true;
                }
            }
            System.out.println(s.getName() + "      Seen: " + seen);
        }
    }

    // EFFECTS: displays the users watchlist with watch status
    private void displayUsersWatchlist() {
        System.out.println("***** Your Watchlist *****");
        System.out.println("===== Movies =====");
        for (Show m : userWatchlist.getShows()) {
            System.out.println(m.getName() + "     Seen: " + m.getWatched());
        }
        System.out.println("===== TV shows =====");
        for (Series s : userWatchlist.getSeries().keySet()) {
            System.out.println(s.getName());
            for (Show ep : s.getEpisodeOrder()) {
                System.out.println(ep.getName() + "     Seen:  " + ep.getWatched());     
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: Adds the specified media from the catalogue to the users watchlist
    // if the media the user wants isnt in the catalogue let them know
    private void addToUsersWatchlist() {
        while (true) {
            String showName = getUserInput("Enter the name exactly or b to back out: ", false);
            if (showName.equals("b")) {
                break;
            }
            boolean found = false;
            
            Show show = getCatalogueMovie(showName, moviesCatalogue);
            if (show != null) {
                found = true;
                userWatchlist.addShow(show);
            } else {
                Series series = getCatalogueSeries(showName, seriesCatalogue);
                if (series != null) {
                    String response = getUserInput("would you like to view this series in order? (Y/N): ", true);
                    userWatchlist.addSeries(series, response.equals("y"));
                    found = true;
                }
            }
            if (!found) {
                System.out.println("I'm sorry we dont appear to have that title, have you checked our catalogue?");
            }
        }
    }
    
    // MODIFIES: this
    // EFFECTS: removes the specified media from the users watchlist
    private void removeFromUsersWatchlist() {
        while (true) {
            String showName = getUserInput("Enter the name exactly or b to back out: ", false);
            if (showName.equals("b")) {
                break;
            }
            boolean found = false;
            
            Show show = getCatalogueMovie(showName, userWatchlist.getShows());
            if (show != null) {
                userWatchlist.removeShow(show);
                found = true;
            } else {
                Series series = getCatalogueSeries(showName, new ArrayList<Series>(userWatchlist.getSeries().keySet()));
                if (series != null) {
                    userWatchlist.removeSeries(series);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("That title isn't on your watchlist so it can't be removed");
            }

        }
    }
    
    // MODIFIES: this, Show
    // EFFECTS: processes user input to determine what show to watch, then sets the show to watched
    private void watchShowDialogue() {
        String promptOpen = "what would you like to watch? \n\t (b to return, titles are case sensitive) : ";
        String showName = getUserInput(promptOpen,false);
        if (showName.equals("b")) {
            return;
        }
        boolean found = false;
        Show show = getCatalogueMovie(showName, moviesCatalogue);
        if (show != null) {
            found = true;
            watchShow(show);
            return;
        } else {
            Series series = getCatalogueSeries(showName, seriesCatalogue);
            if (series != null) {
                found = true;
                String prompt = "what episode would you like to watch?: \n " + "1 - " + series.getEpisodeOrder().size();
                watchShow(series, Integer.parseInt(getUserInput(prompt, false)));
                return;
            }
        }
        if (!found) {
            System.out.println("I'm sorry we dont appear to have that title, have you checked our catalogue?");
        }
    }

    // EFFECTS: Suggests shows to watch next from the users watchlist
    private void suggestNext() {
        int watchlistSize = (userWatchlist.getShows().size() + userWatchlist.getSeries().size());
        if (watchlistSize == 0) {
            System.out.println("your watchlist is empty! so unfortunately we can't reccomend anything.");    
            return;
        }
        int randVal = (int)(Math.random() * watchlistSize); 
        if (randVal < userWatchlist.getShows().size()) {
            reccomendDialogue(userWatchlist.recommendRandomShow());
        } else { 
            reccomendDialogue(userWatchlist.recommendRandomSeries());
        }        
    }

    // REQUIRES: userWatchlist must not be empty
    // EFFECTS suggests shows to watch next from the users watchlist
    // used when they just watched an episode from a series to give an additional option.
    private void suggestNext(Series tvShow) {
        int watchlistSize = (userWatchlist.getShows().size() + userWatchlist.getSeries().size());
        if (watchlistSize == 0) {
            System.out.println("your watchlist is empty! so unfortunately we can't reccomend anything.");    
            return;
        }
        int randVal = (int)(Math.random() * watchlistSize); 
        if (randVal < userWatchlist.getShows().size()) {
            reccomendDialogue(userWatchlist.recommendRandomShow(), tvShow);
        } else { 
            reccomendDialogue(userWatchlist.recommendRandomSeries(), tvShow);
        }      
    }

    // MODIFIES: this
    // EFFECTS: generates dummy data to fill the catalogue for testing and presentation purposes.
    private void generateCatalogue() {
        for (int i = 1; i < 11; i++) {
            String name = "movie " + i;
            String hours = "0" + ((int)Math.random() * 2);
            String mins = ":" + ((int)Math.random() * 60);
            String secs = ":" + ((int)Math.random() * 60);
            Show movie = new Show(name, hours + mins + secs);
            moviesCatalogue.add(movie);
        }
        for (int j = 1; j < 11; j++) {
            String name = "Series " + j;
            ArrayList<Show> epList = new ArrayList<>(); 
            for (int i = 1; i < 6; i++) {
                String epName = name + " Episode " + i;
                String runtime = "00:" + ((int)Math.random() * 60) + ":" + ((int)Math.random() * 60);
                Show episode = new Show(epName, runtime);
                epList.add(episode);
            }
            Series series = new Series(name, epList);
            seriesCatalogue.add(series);        
        }
    }

    //Helper methods
    
    // EFFECTS: retrieves specific show from the provided list of shows or returns null if it doesnt exist
    private Show getCatalogueMovie(String name, ArrayList<Show> catalogue) {
        for (Show i : catalogue) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    // EFFECTS: retrieves specific series from the provided list of series or returns null if it doesnt exist
    private Series getCatalogueSeries(String name, ArrayList<Series> catalogue) {
        for (Series i : catalogue) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    // MODIFIES: Show
    // EFFECTS: sets target show to watched simulating someone watching an episode then continues the watch tv loop
    private void watchShow(Series s, int epNum) {
        s.getEpisode(epNum).watchShow();
        s.setlastWatched(epNum);
        suggestNext(s);
    }

    // MODIFIES: Show
    // EFFECTS: sets target show to watched simulating someone watching an episode then continues the watch tv loop
    private void watchShow(Show m) {
        m.watchShow();
        suggestNext();
    }
    
    // EFFECTS: asks the user the provided prompt and then returns the users response either as is 
    // or case-shifted to lower case.
    private String getUserInput(String prompt, boolean toLower) {
        System.err.println(prompt);
        String response = input.nextLine();
        if (toLower) {
            response = response.toLowerCase();
        }
        return response;
    }
    
    // EFFECTS: communicates the next show reccomendation to the user and provides branches for the watch loop 
    private void reccomendDialogue(Show reccomendation) {        
        System.out.println("based on your watchlist we reccomend " + reccomendation.getName() + " next !");
        String userChoice = getUserInput(
                "\t (b to stop, r to get a new reccomendation, p to play reccomendation): ", true);
        if (userChoice.equals("b")) {
            return;
        } else if (userChoice.equals("r")) {
            suggestNext();
        } else if (userChoice.equals("p")) {
            watchShow(reccomendation);
        }
    }
    
    // EFFECTS: communicates the next show reccomendation to the user and provides branches for the watch loop
    private void reccomendDialogue(Series reccomendation) {        
        System.out.println("based on your watchlist we reccomend " + reccomendation.getName() + " next !");
        String userChoice = getUserInput(
                "\t (b to stop, r to get a new reccomendation, p to play reccomendation): ", true);
        if (userChoice.equals("b")) {
            return;
        } else if (userChoice.equals("r")) {
            suggestNext();
        } else if (userChoice.equals("p")) {
            if (userWatchlist.getSeries().get(reccomendation)) {
                watchShow(reccomendation, reccomendation.getEpisodeNum(reccomendation.getNextEpisode()));
            } else {
                watchShow(reccomendation, reccomendation.getEpisodeNum(reccomendation.getRandomEpisode()));
            }
        }
    }

    // EFFECTS: communicates the next show reccomendation to the user and provides branches for the watch loop
    // @SuppressWarnings("linelength")
    private void reccomendDialogue(Show reccomendation, Series currShow) {
        System.out.println("based on your watchlist we reccomend " + reccomendation.getName() + " next !");
        String promptTop = "\t (b to stop, r to get a new reccomendation, p to play reccomendation, ";
        String promptBot = "n to play the next episode): ";
        String userChoice = getUserInput(promptTop + promptBot, true);
        if (userChoice.equals("b")) {
            return;
        } else if (userChoice.equals("r")) {
            suggestNext(currShow);
        } else if (userChoice.equals("p")) {
            watchShow(reccomendation);
        } else if (userChoice.equals("n")) {
            watchShow(currShow, currShow.getEpisodeNum(currShow.getNextEpisode()));
        }
    }

    // EFFECTS: communicates the next show reccomendation to the user and provides branches for the watch loop
    private void reccomendDialogue(Series reccomendation, Series currShow) {
        System.out.println("based on your watchlist we reccomend " + reccomendation.getName() + " next !");
        String promptTop = "\t (b to stop, r to get a new reccomendation, p to play reccomendation, ";
        String promptBot = "n to play the next episode): ";
        String userChoice = getUserInput(promptTop + promptBot, true);
        if (userChoice.equals("b")) {
            return;
        } else if (userChoice.equals("r")) {
            suggestNext(currShow);
        } else if (userChoice.equals("p")) {
            if (userWatchlist.getSeries().get(reccomendation)) {
                watchShow(reccomendation, reccomendation.getEpisodeNum(reccomendation.getNextEpisode()));
            } else {
                watchShow(reccomendation, reccomendation.getEpisodeNum(reccomendation.getRandomEpisode()));
            }
        } else if (userChoice.equals("n")) {
            watchShow(currShow, currShow.getEpisodeNum(currShow.getNextEpisode()));
        }
    }

    // EFFECTS: interacts with the user to make sure they want to overwrite the saved watchlist before saving
    private void saveWatchlistDialogue() {
        String prompt1 = "Are youe sure? This will overwrite the previously saved watchlist ";
        String prompt2 = "with your current watchlist\n (Y/N)";
        String userChoice = getUserInput(prompt1 + prompt2, true);
        if (userChoice.equals("n")) {
            return;
        } else {
            saveWatchlist();
        }
    }

    //EFFECTS: interacts with the user to make sure they want to overwrite their 
    // currently loaded watchlist before loading
    private void loadWatchlistDialogue() {
        String prompt = "Are youe sure? This will overwrite your current watchlist with the one saved on file \n (Y/N)";
        String userChoice = getUserInput(prompt, true);
        if (userChoice.equals("n")) {
            return;
        } else {
            loadWatchlist();
        }
    }

    // MODIFIES: userWatchlist.json
    // EFFECTS: saves the user's watchlist to a file
    private void saveWatchlist() {
        JsonWriter writer = new JsonWriter("./data/userWatchlist.json");
        try {
            writer.open();
            writer.write(userWatchlist);
            writer.close();
        } catch (IOException e) {
            System.out.println("ERROR writing to file");
            System.err.println(e.getMessage());
        }    
    }

    // MODIFIES: this
    // EFFECTS: loads a user's watchlist from a file
    private void loadWatchlist() {
        JsonReader reader = new JsonReader("./data/userWatchlist.json");
        try {
            userWatchlist = reader.read();
        } catch (Exception e) {
            System.out.println("ERROR reading from file, are you sure it exists?");
            System.err.println(e.getMessage());
        }
        
    }

}