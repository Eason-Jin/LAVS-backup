package uoa.lavs.controllers;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public abstract class Controller {
  protected Alert alert = new Alert(Alert.AlertType.ERROR);
  protected StringBuilder errorMessage = new StringBuilder();

  protected String fieldNormalBorder = "-fx-border-color: #d0d7de;";
  protected String fieldRedBorder = "-fx-border-color: red;";
  protected String tableNormalBorder = "-fx-border-color: #d0d7de";
  protected String tableRedBorder = "-fx-border-color: red";

  protected boolean validateEmailFormat(String email) {
    // Email should be in the format of a@b.c
    return email.matches("^.+@.+\\..+$");
  }

  protected boolean validateNumberFormat(String number) {
    try {
      Integer.parseInt(number);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  protected boolean validateWebsiteFormat(String website) {
    // Website should be in the format of a.b
    return website.matches("^.+\\..+$");
  }

  protected boolean validateDateFormat(LocalDate date, boolean beforeToday) {
    if (beforeToday) {
      return date.isBefore(LocalDate.now());
    } else {
      return date.isAfter(LocalDate.now());
    }
  }

  protected boolean isEmpty(Control ui) {
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      try {
        tf.getText();
        if (tf.getText() == null) return true;
      } catch (Exception e) {
        return true;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        return true;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        return true;
      }
    }
    return false;
  }

  protected boolean isTooLong(String str, int length) {
    return str.length() > length;
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
