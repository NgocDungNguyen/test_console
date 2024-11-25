package com.rentalsystem.ui;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

/**
 * A class to display progress bars in the console.
 */
public class ProgressDisplay {
    private final Terminal terminal;

    /**
     * Constructs a ProgressDisplay with the given terminal.
     * @param terminal The terminal to use for output
     */
    public ProgressDisplay(Terminal terminal) {
        this.terminal = terminal;
    }

    /**
     * Displays a progress bar with a message.
     * @param message The message to display alongside the progress bar
     * @param current The current progress value
     * @param total The total progress value
     */
    public void showProgress(String message, int current, int total) {
        int width = 50; // Width of the progress bar
        int progress = (int) ((double) current / total * width);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            if (i < progress) {
                bar.append("=");
            } else if (i == progress) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("]");

        String progressString = String.format("%s %s %d/%d", message, bar.toString(), current, total);

        // Print the progress bar
        terminal.writer().print("\r" + new AttributedString(progressString, AttributedStyle.DEFAULT).toAnsi(terminal));
        terminal.writer().flush();

        // Print a newline when progress is complete
        if (current == total) {
            terminal.writer().println();
        }
    }
}