// code Referenced from below examples 
// https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
// https://stackoverflow.com/questions/284899/how-do-you-add-an-actionlistener-onto-a-jbutton-in-java
// https://docs.oracle.com/javase/tutorial/uiswing/layout/border.html
// https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
// https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
// https://docs.oracle.com/javase/6/docs/api/javax/swing/JOptionPane.html
// https://stackoverflow.com/questions/16295942/java-swing-adding-action-listener-for-exit-on-close

package ui;

import javax.swing.*;

import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;

import model.EventLog;
import model.Series;
import model.Show;
import model.Watchlist;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.util.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import model.Event;
import model.EventLog;

// EFFECTS: The main gui class used for building and controlling user interaction with the app.
public class MainAppPanel extends JFrame {

    private ArrayList<Show> moviesCatalogue = new ArrayList<Show>();
    private ArrayList<Series> seriesCatalogue = new ArrayList<Series>();
    private Watchlist userWatchlist = new Watchlist();

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel watchlistPanel;
    private JPanel cataloguePanel;

    private static final String MAIN_MENU = "Main Menu";
    private static final String WATCHLIST = "Watchlist";
    private static final String CATALOGUE = "Catalogue";

    public MainAppPanel() {
        generateCatalogue();

        setTitle("TermProjectGui");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        addWindowListener(new MainAppWindowAdapter());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        watchlistPanel = createWatchlistPanel();
        cataloguePanel = createCataloguePanel();

        cardPanel.add(createMainMenuPanel(), MAIN_MENU);
        cardPanel.add(cataloguePanel, CATALOGUE);
        cardPanel.add(watchlistPanel, WATCHLIST);
                
        add(cardPanel);

        cardLayout.show(cardPanel, MAIN_MENU);

    }
    
    //EFFECTS: creates the main menu card
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton watchlistButton = new JButton("Watchlist");
        JButton catalogueButton = new JButton("Catalogue");
        
        Dimension buttonSize = new Dimension(200, 50);
        watchlistButton.setPreferredSize(buttonSize);
        catalogueButton.setPreferredSize(buttonSize);
        
        watchlistButton.setMaximumSize(buttonSize);
        catalogueButton.setMaximumSize(buttonSize);
        
        buttonPanel.add(watchlistButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));  
        buttonPanel.add(catalogueButton);
        
        watchlistButton.addActionListener(e -> rebuildWatchlistPanel());
        catalogueButton.addActionListener(e -> rebuildCataloguePanel());
        
        panel.add(buttonPanel);
        panel.setName(MAIN_MENU);
        return panel;
    }

    // EFFECTS: creates the watchlist panel to be added to my card layout
    private JPanel createWatchlistPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = createWatchlistHeader();
        
        JPanel listPanel = createWatchlistContent();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);  // Increase from default (1)
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        panel.setName(WATCHLIST);
        return panel;
    }

    // EFFECTS: creates and populates the header panel of the watchlist view
    private JPanel createWatchlistHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, MAIN_MENU));
        
        JLabel titleLabel = new JLabel("Watchlist", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton addButton = new JButton("Add to Watchlist");
        addButton.addActionListener(e -> showAddDialogue());

        JButton saveButton = new JButton("Save Watchlist");
        saveButton.addActionListener(e -> saveWatchlistDialogue());
        JButton loadButton = new JButton("Load Watchlist");
        loadButton.addActionListener(e -> loadWatchlistDialogue());
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

        controlPanel.add(addButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        return headerPanel;
    }

    // EFFECTS: creates and populates the content panel for the watchlist card
    private JPanel createWatchlistContent() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        JPanel moviesPanel = getWatchlistMoviesPanel();

        JPanel seriesPanel = getWatchlistSeriesPanel();

        listPanel.add(moviesPanel);
        listPanel.add(seriesPanel);
        return listPanel;
    }

    // EFFECTS: creates and populates a panel with current watchlist movies
    private JPanel getWatchlistMoviesPanel() {
        JPanel moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBorder(BorderFactory.createTitledBorder("Movies"));
        
        for (Show m : userWatchlist.getShows()) {
            JPanel row = new JPanel(new GridLayout(1, 4));
            JLabel nameLabel = new JLabel(m.getName());
            JLabel statusLabel = new JLabel(m.getWatched() ? "✓ Watched" : "Not watched");
            JButton watchButton = new JButton("Watch");
            JButton removeButton = new JButton("Remove");

            row.add(nameLabel);
            row.add(statusLabel);
            row.add(watchButton);
            row.add(removeButton);

            removeButton.addActionListener(e -> {
                removeRowWatchlist(moviesPanel, m, row);
            });

            watchButton.addActionListener(e -> showWatchDialog(m, null, watchlistPanel));

    
            row.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            moviesPanel.add(row);
        }
        return moviesPanel;
    }

    // MODIFIES: this
    // EFFECTS: removes the removedItem from the target panel and from the watchlist
    private void removeRowWatchlist(JPanel targetPanel, Show m, JPanel removedItem) {
        userWatchlist.removeShow(m);
        targetPanel.remove(removedItem);
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // MODIFIES: this
    // EFFECTS: removes the removedItem from the target panel and from the watchlist
    private void removeRowWatchlist(JPanel targetPanel, Series series, JPanel removedItem) {
        userWatchlist.removeSeries(series);
        targetPanel.remove(removedItem);
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // EFFECTS: creates and populates the series panel for the watchlist card
    private JPanel getWatchlistSeriesPanel() {
        JPanel seriesPanel = new JPanel();
        seriesPanel.setLayout(new BoxLayout(seriesPanel, BoxLayout.Y_AXIS));
        seriesPanel.setBorder(BorderFactory.createTitledBorder("TV Shows"));
        
        for (Series s : userWatchlist.getSeries().keySet()) {
            JPanel titlePanel = getWatchlistTitlePanel(seriesPanel, s);
            seriesPanel.add(titlePanel);
        }
        return seriesPanel;
    }

    // EFFECTS: creates and populates a title panel for the particular series in the watchlist
    private JPanel getWatchlistTitlePanel(JPanel seriesPanel, Series s) {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createTitledBorder(s.getName()));

        JPanel seriesButtonPanel = new JPanel(new GridLayout(1, 2));
        JButton toggleButton = new JButton("▼");
        toggleButton.setPreferredSize(new Dimension(20, 20));
        JButton removeButton = new JButton("Remove");

        seriesButtonPanel.add(toggleButton);
        seriesButtonPanel.add(removeButton);  

        JPanel episodesPanel = getWatchlistSeriesEpisodesPanel(s);

        toggleButton.addActionListener(e -> {
            toggleSection(toggleButton, episodesPanel);
        });
      
        removeButton.addActionListener(e -> {
            removeRowWatchlist(seriesPanel, s, titlePanel);
        });
      
        titlePanel.add(seriesButtonPanel);
        titlePanel.add(episodesPanel);
        return titlePanel;
    }

    // EFFECTS: creates and populates a panel with the episodes in a series
    private JPanel getWatchlistSeriesEpisodesPanel(Series s) {
        JPanel episodesPanel = new JPanel();
        episodesPanel.setLayout(new BoxLayout(episodesPanel, BoxLayout.Y_AXIS));

        for (Show ep : s.getEpisodeOrder()) {
            
            JPanel row = getWatchlistSeriesEpisodeRow(s, ep);

            episodesPanel.add(row);

        }
        episodesPanel.setVisible(false);
        return episodesPanel;
    }

    // EFFECTS: creates a row entry for a series in the watchlist card
    private JPanel getWatchlistSeriesEpisodeRow(Series s, Show ep) {
        JPanel row = new JPanel(new GridLayout(1, 3));
        JLabel nameLabel = new JLabel(ep.getName());
        JLabel statusLabel = new JLabel(ep.getWatched() ? "✓ Watched" : "Not watched");
        JButton watchButton = new JButton("Watch");

        row.add(nameLabel);
        row.add(statusLabel);
        row.add(watchButton);
        row.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        watchButton.addActionListener(e -> showWatchDialog(ep, s, watchlistPanel));
        return row;
    }

    
    //EFFECTS: creates the catalogue panel for my card layout
    private JPanel createCataloguePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = createCatalogueHeader();
        
        JPanel listPanel = createCatalogueContent();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);  // Increase from default (1)
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        panel.setName(CATALOGUE);
        return panel;
    }
    
    // EFFECTS: builds the header panel for the catalogue card
    private JPanel createCatalogueHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, MAIN_MENU));
        
        JLabel titleLabel = new JLabel("Catalogue", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    // EFFECTS: creates and populates the content panel for the catalogue card
    private JPanel createCatalogueContent() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel moviesPanel = createCatalogueMoviesPanel();
        
        JPanel seriesPanel = createCatalogueSeriesPanel();

        listPanel.add(moviesPanel);
        listPanel.add(seriesPanel);
        return listPanel;
    }

    // EFFECTS: creates and populates the movies section of the catalogue card
    private JPanel createCatalogueMoviesPanel() {
        JPanel moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));
        moviesPanel.setBorder(BorderFactory.createTitledBorder("Movies"));
        moviesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);  

        for (Show m : moviesCatalogue) {
            JPanel row = new JPanel(new GridLayout(1, 3));

            JLabel nameLabel = new JLabel(m.getName());
            JLabel statusLabel = new JLabel(m.getWatched() ? "✓ Watched" : "Not watched");
            JButton watchButton = new JButton("Watch");
            watchButton.addActionListener(e -> showWatchDialog(m, null, cataloguePanel));

            row.add(nameLabel);
            row.add(statusLabel);
            row.add(watchButton);

            row.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            moviesPanel.add(row);
            
        }
        return moviesPanel;
    }

    // EFFECTS: creates and populates the series section of the catalogue card
    private JPanel createCatalogueSeriesPanel() {
        JPanel seriesPanel = new JPanel();
        seriesPanel.setLayout(new BoxLayout(seriesPanel, BoxLayout.Y_AXIS));
        seriesPanel.setBorder(BorderFactory.createTitledBorder("TV Shows"));
        seriesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Series s : seriesCatalogue) {
            JPanel titlePanel = getCatalogueSeriesTitlePanel(s);
            seriesPanel.add(titlePanel);
        }
        return seriesPanel;
    }

    // EFFECTS: creates and populates the series panel for a specific series in the catalogue
    private JPanel getCatalogueSeriesTitlePanel(Series s) {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createTitledBorder(s.getName()));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton toggleButton = new JButton("▼");
        toggleButton.setPreferredSize(new Dimension(20, 20));
        buttonPanel.add(toggleButton);
        buttonPanel.add(Box.createHorizontalGlue());

        JPanel episodesPanel = new JPanel();
        episodesPanel.setLayout(new BoxLayout(episodesPanel, BoxLayout.Y_AXIS));
        episodesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (Show ep : s.getEpisodeOrder()) {
            JPanel row = getCatalogueSeriesEpisodeRow(s, ep);
            episodesPanel.add(row);
        }

        toggleButton.addActionListener(e -> {
            toggleSection(toggleButton, episodesPanel);
        });

        titlePanel.add(buttonPanel);
        titlePanel.add(episodesPanel);
        return titlePanel;
    }

    // EFFECTS: creates a button to toggle visibility of the targetSection panel
    private void toggleSection(JButton toggleButton, JPanel targetSection) {
        boolean isVisible = targetSection.isVisible();
        targetSection.setVisible(!isVisible);
        toggleButton.setText(isVisible ? "▼" : "▲");
        targetSection.revalidate();
        targetSection.repaint();
    }

    // EFFECTS: creates and populates the individual episodes for a series in the catalogue card
    private JPanel getCatalogueSeriesEpisodeRow(Series s, Show ep) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(ep.getName());
        JLabel statusLabel = new JLabel(ep.getWatched() ? "✓ Watched" : "Not watched");
        JButton watchButton = new JButton("Watch");
        watchButton.addActionListener(e -> showWatchDialog(ep, s, cataloguePanel));
        
        row.add(nameLabel);
        row.add(Box.createHorizontalGlue());
        row.add(statusLabel);
        row.add(Box.createHorizontalGlue());
        row.add(watchButton);
        row.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return row;
    }


    //helper functions 
    //EFFECTS: generates dummy data to populate my catalogue for testing
    private void generateCatalogue() {
        for (int i = 1; i < 11; i++) {
            String name = "movie " + i;
            String hours = "0" + ((int)(Math.random() * 2));
            String mins = ":" + ((int)(Math.random() * 60));
            String secs = ":" + ((int)(Math.random() * 60));
            Show movie = new Show(name, hours + mins + secs);
            moviesCatalogue.add(movie);
        }
        for (int j = 1; j < 11; j++) {
            String name = "Series " + j;
            ArrayList<Show> epList = new ArrayList<>(); 
            for (int i = 1; i < 6; i++) {
                String epName = name + " Episode " + i;
                String runtime = "00:" + ((int)(Math.random() * 60)) + ":" + ((int)(Math.random() * 60));
                Show episode = new Show(epName, runtime);
                epList.add(episode);
            }
            Series series = new Series(name, epList);
            seriesCatalogue.add(series);        
        }
    }

    
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

    //EFFECTS: creates a dialog for adding items to the watchlist
    private void showAddDialogue() {
        JDialog dialog = new JDialog(this, "Add to Watchlist", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0,1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel instructionLabel = new JLabel("Select from available titles:");
        
        JComboBox<String> titleComboBox = getTitleComboBox();
        JCheckBox watchInOrderCheckbox = new JCheckBox("Watch series in order", true);
        
        inputPanel.add(instructionLabel);
        inputPanel.add(titleComboBox);
        inputPanel.add(watchInOrderCheckbox);
        
        JPanel buttonPanel = getAddDialogButtonPanel(dialog, titleComboBox, watchInOrderCheckbox);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    // EFFECTS: creates the functional buttons for the Add dialog
    private JPanel getAddDialogButtonPanel(JDialog dialog, JComboBox<String> titleComboBox, JCheckBox orderCheckbox) {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> {
            getShow(dialog, titleComboBox, orderCheckbox);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    // EFFECTS: generates a combobox filled with the titles of content in the catalogue
    private JComboBox<String> getTitleComboBox() {
        JComboBox<String> titleComboBox = new JComboBox<>();

        for (Show m : moviesCatalogue) {
            titleComboBox.addItem(m.getName());
        }
        for (Series s : seriesCatalogue) {
            titleComboBox.addItem(s.getName());
        }
        return titleComboBox;
    }

    // MODIFIES: this
    // EFFECTS: adds the currently selected media from the comboBox to the userWatchlist and closes the dialog
    private void getShow(JDialog dialog, JComboBox<String> titleComboBox, JCheckBox watchInOrderCheckbox) {
        String selectedTitle = (String) titleComboBox.getSelectedItem();

        Show show = getCatalogueMovie(selectedTitle, moviesCatalogue);
        if (show != null) {
            userWatchlist.addShow(show);
        } else {
            Series series = getCatalogueSeries(selectedTitle, seriesCatalogue);
            if (series != null) {
                userWatchlist.addSeries(series, watchInOrderCheckbox.isSelected());
            }
        }
        dialog.dispose();
        rebuildWatchlistPanel();
    }

    //EFFECTS: rebuilds the watchlist panel to update entries
    private void rebuildWatchlistPanel() {
        cardPanel.remove(watchlistPanel);
        watchlistPanel = createWatchlistPanel();
        cardPanel.add(watchlistPanel, WATCHLIST);
        cardPanel.revalidate();
        cardPanel.repaint();
        cardLayout.show(cardPanel, WATCHLIST);
    }

    //EFFECTS: rebuilds the catalogue panel to update entries
    private void rebuildCataloguePanel() {
        cardPanel.remove(cataloguePanel);
        cataloguePanel = createCataloguePanel();
        cardPanel.add(cataloguePanel, CATALOGUE);
        cardPanel.revalidate();
        cardPanel.repaint();
        cardLayout.show(cardPanel, CATALOGUE);
    }

    // REQUIRES: If show is not part of a series then currentSeries must be null
    // MODIFIES: this
    // EFFECTS: creates a dialog to display an image representing a video and the required interface 
    // to continue or stop watching more shows
    private void showWatchDialog(Show currentShow, Series currentSeries, JPanel callingPanel) {
        // set current show object to watched, process differently if part of series.
        if (currentSeries != null) {
            currentSeries.setlastWatched(currentSeries.getEpisodeNum(currentShow));
        }
        currentShow.watchShow();

        JDialog dialog = new JDialog(this, "Now Watching", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel imagePanel = getWatchImagePanel();
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(currentShow.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = getWatchDialogButtons(callingPanel, dialog);

        dialog.add(imagePanel, BorderLayout.NORTH);
        dialog.add(infoPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // EFFECTS: creates the button panel for the watch dialog
    private JPanel getWatchDialogButtons(JPanel callingPanel, JDialog dialog) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> {
            dialog.dispose();
            if (callingPanel.getName() == WATCHLIST) {
                rebuildWatchlistPanel();
            }
            if (callingPanel.getName() == CATALOGUE) {
                rebuildCataloguePanel();
            }
        });

       
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    // EFFECTS: creates the image panel for the watch dialog
    private JPanel getWatchImagePanel() {
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(400, 300));

        try {
            ImageIcon imageIcon = new ImageIcon("data/VideoStandIn.png");
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            JLabel imageLabel = new JLabel(scaledIcon);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel placeholderLabel = new JLabel("No Image Available");
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(placeholderLabel, BorderLayout.CENTER);
            System.err.println("Error loading image: " + e.getMessage());
        }
        return imagePanel;
    }

    // EFFECTS: communicates to the user that they are about to overwrite their data before doing it
    private void saveWatchlistDialogue() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure? This will overwrite the previously saved watchlist.",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
        if (result == JOptionPane.YES_OPTION) {
            saveWatchlist();
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
            JOptionPane.showMessageDialog(
                    this, 
                    "Watchlist saved successfully!",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error saving watchlist: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }    
    }

    // EFFECTS: informs the user they are about to overwrite their current session before loading the watchlist
    private void loadWatchlistDialogue() {
        int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure? This will overwrite your current watchlist.",
                    "Confirm Load",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
        if (result == JOptionPane.YES_OPTION) {
            loadWatchlist();
        }
    }
    
    // MODIFIES: this
    // EFFECTS: loads a user's watchlist from a file
    private void loadWatchlist() {
        JsonReader reader = new JsonReader("./data/userWatchlist.json");
        try {
            userWatchlist = reader.read();
            JOptionPane.showMessageDialog(
                    this,
                    "Watchlist loaded successfully!",
                    "Load Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
            rebuildWatchlistPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading watchlist: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        
    }
}
