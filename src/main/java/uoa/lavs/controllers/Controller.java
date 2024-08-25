package uoa.lavs.controllers;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public abstract class Controller {
  protected Alert alert = new Alert(Alert.AlertType.ERROR);
  protected StringBuilder errorMessage = new StringBuilder();

  protected String normalBorder = "-fx-border-color: #d0d7de";
  protected String redBorder = "-fx-border-color: red";

  protected boolean validateEmailFormat(TextField email) {
    email.getStyleClass().remove("invalid");
    // Email should be in the format of a@b.c
    if (email.getText().matches("^.+@.+\\..+$")) {
      return true;
    } else {
      email.getStyleClass().add("invalid");
      return false;
    }
  }

  protected boolean validateNumberFormat(TextField number, boolean isDouble) {
    number.getStyleClass().remove("invalid");
    try {
      if (isDouble) {
        Double.parseDouble(number.getText());
      } else {
        Long.parseLong(number.getText());
      }
    } catch (Exception e) {
      number.getStyleClass().add("invalid");
      return false;
    }
    return true;
  }

  protected boolean validateWebsiteFormat(TextField website) {
    website.getStyleClass().remove("invalid");
    // Website should be in the format of a.b
    if (website.getText().matches("^.+\\..+$")) {
      return true;
    } else {
      website.getStyleClass().add("invalid");
      return false;
    }
  }

  protected boolean validateDateFormat(DatePicker dp, boolean beforeToday) {
    dp.getStyleClass().remove("invalid");
    try {
      LocalDate date = dp.getValue();
      if (beforeToday && date.isBefore(LocalDate.now())) {
        return true;
      } else if (!beforeToday && date.isAfter(LocalDate.now())) {
        return true;
      }
      dp.getStyleClass().add("invalid");
      return false;
    } catch (Exception e) {
      dp.getStyleClass().add("invalid");
      return false;
    }
  }

  protected boolean isEmpty(Control ui) {
    ui.getStyleClass().remove("invalid");
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      try {
        tf.getText();
        if (tf.getText() == null || tf.getText().isEmpty()) {
          tf.getStyleClass().add("invalid");
          return true;
        }
      } catch (Exception e) {
        tf.getStyleClass().add("invalid");
        return true;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        cb.getStyleClass().add("invalid");
        return true;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        dp.getStyleClass().add("invalid");
        return true;
      }
    }
    return false;
  }

  protected boolean isTooLong(Control str, int length) {
    str.getStyleClass().remove("invalid");
    if (str instanceof TextField) {
      TextField tf = (TextField) str;
      if (tf.getText() == null || tf.getText().length() > length) {
        tf.getStyleClass().add("invalid");
        return true;
      }
    } else if (str instanceof TextArea) {
      TextArea ta = (TextArea) str;
      if (ta.getText() == null || ta.getText().length() > length) {
        ta.getStyleClass().add("invalid");
        return true;
      }
    }
    return false;
  }

  protected void showAlert() {
    alert.setTitle("Please fix the following errors:");
    alert.setContentText(errorMessage.toString());
    alert.showAndWait();
    errorMessage = new StringBuilder();
  }

  protected void appendErrorMessage(String message) {
    if (errorMessage.indexOf(message) == -1) {
      errorMessage.append(message);
    }
  }
}
