package ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import model.Event;
import model.EventLog;

// class representing window event listener for the main panel of the app.
public class MainAppWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("Printing logs:");
        for (Event event : EventLog.getInstance()) {
            System.out.println(event.toString());
        }
    }

}
