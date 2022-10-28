package com.artyommameev.psfsv1unpacker.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.NonNull;
import lombok.val;

/**
 * The simple utility class to create JavaFX Alert dialogs.
 *
 * @author Artyom Mameev
 */
public class SimpleAlertCreator {

    /**
     * Creates a simple JavaFX error alert dialog.
     *
     * @param title      the alert title.
     * @param headerText the alert header text.
     * @return the created alert dialog with the given title and header text.
     * @throws NullPointerException if the title or the header text is null.
     */
    public static Alert createErrorAlert(@NonNull String title,
                                         @NonNull String headerText) {
        val alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText("");

        return alert;
    }

    /**
     * Creates a simple JavaFX info alert dialog.
     *
     * @param title      the alert title.
     * @param headerText the alert header text.
     * @return the created alert dialog with the given title and header text.
     * @throws NullPointerException if the title or the header text is null.
     */
    public static Alert createInfoAlert(@NonNull String title,
                                        @NonNull String headerText) {
        val alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText("");

        return alert;
    }

    /**
     * Creates and returns a simple JavaFX alert dialog with "Yes" and "No"
     * buttons and "Are You Sure?" text.
     *
     * @param title the alert title.
     * @return the created alert dialog with the given title.
     * @throws NullPointerException if the title is null.
     */
    public static Alert createAreYouSureDialog(@NonNull String title) {
        val alert = new Alert(Alert.AlertType.CONFIRMATION, "",
                ButtonType.YES, ButtonType.NO);

        alert.setHeaderText("Are You Sure?");
        alert.setTitle(title);

        return alert;
    }
}
