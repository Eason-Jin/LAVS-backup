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

  protected String fieldNormalBorder = "-fx-border-color: #d0d7de;";
  protected String fieldRedBorder = "-fx-border-color: red;";
  protected String tableNormalBorder = "-fx-border-color: #d0d7de";
  protected String tableRedBorder = "-fx-border-color: red";

  protected boolean validateEmailFormat(TextField email) {
    email.setStyle(fieldNormalBorder);
    // Email should be in the format of a@b.c
    if (email.getText().matches("^.+@.+\\..+$")) {
      return true;
    } else {
      email.setStyle(fieldRedBorder);
      return false;
    }
  }

  protected boolean validateNumberFormat(TextField number, boolean isDouble) {
    number.setStyle(fieldNormalBorder);
    try {
      if (isDouble) {
        Double.parseDouble(number.getText());
      } else {
        Long.parseLong(number.getText());
      }
    } catch (Exception e) {
      number.setStyle(fieldRedBorder);
      return false;
    }
    return true;
  }

  protected boolean validateWebsiteFormat(TextField website) {
    website.setStyle(fieldNormalBorder);
    // Website should be in the format of a.b
    if (website.getText().matches("^.+\\..+$")) {
      return true;
    } else {
      website.setStyle(fieldRedBorder);
      return false;
    }
  }

  protected boolean validateDateFormat(DatePicker dp, boolean beforeToday) {
    dp.setStyle(fieldNormalBorder);
    try {
      LocalDate date = dp.getValue();
      if (beforeToday && date.isBefore(LocalDate.now())) {
        return true;
      } else if (!beforeToday && date.isAfter(LocalDate.now())) {
        return true;
      }
      dp.setStyle(fieldRedBorder);
      return false;
    } catch (Exception e) {
      dp.setStyle(fieldRedBorder);
      return false;
    }
  }

  protected boolean isEmpty(Control ui) {
    ui.setStyle(fieldNormalBorder);
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      try {
        tf.getText();
        if (tf.getText() == null || tf.getText().isEmpty()) {
          tf.setStyle(fieldRedBorder);
          return true;
        }
      } catch (Exception e) {
        tf.setStyle(fieldRedBorder);
        return true;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        cb.setStyle(fieldRedBorder);
        return true;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        dp.setStyle(fieldRedBorder);
        return true;
      }
    }
    return false;
  }

  protected boolean isTooLong(Control str, int length) {
    str.setStyle(fieldNormalBorder);
    if (str instanceof TextField) {
      TextField tf = (TextField) str;
      if (tf.getText() == null || tf.getText().length() > length) {
        tf.setStyle(fieldRedBorder);
        return true;
      }
    } else if (str instanceof TextArea) {
      TextArea ta = (TextArea) str;
      if (ta.getText() == null || ta.getText().length() > length) {
        ta.setStyle(fieldRedBorder);
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
