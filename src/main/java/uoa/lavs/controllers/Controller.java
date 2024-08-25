package uoa.lavs.controllers;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

public abstract class Controller {
  protected enum Setting {
    ADD,
    EDIT,
    VIEW
  }

  protected Alert alert = new Alert(Alert.AlertType.ERROR);
  protected StringBuilder errorMessage = new StringBuilder();

  protected String normalBorder = "-fx-border-color: #d0d7de";
  protected String redBorder = "-fx-border-color: red";

  protected boolean validateEmailFormat(TextField email) {
    email.setStyle(normalBorder);
    // Email should be in the format of a@b.c
    if (email.getText().matches("^.+@.+\\..+$")) {
      return true;
    } else {
      email.setStyle(redBorder);
      return false;
    }
  }

  protected boolean validateNumberFormat(TextField number, boolean isDouble) {
    number.setStyle(normalBorder);
    try {
      if (isDouble) {
        Double.parseDouble(number.getText());
      } else {
        Long.parseLong(number.getText());
      }
    } catch (Exception e) {
      number.setStyle(redBorder);
      return false;
    }
    return true;
  }

  protected boolean validateWebsiteFormat(TextField website) {
    website.setStyle(normalBorder);
    // Website should be in the format of a.b
    if (website.getText().matches("^.+\\..+$")) {
      return true;
    } else {
      website.setStyle(redBorder);
      return false;
    }
  }

  protected boolean validateDateFormat(DatePicker dp, boolean beforeToday) {
    dp.setStyle(normalBorder);
    try {
      LocalDate date = dp.getValue();
      if (beforeToday && date.isBefore(LocalDate.now())) {
        return true;
      } else if (!beforeToday && date.isAfter(LocalDate.now())) {
        return true;
      }
      dp.setStyle(redBorder);
      return false;
    } catch (Exception e) {
      dp.setStyle(redBorder);
      return false;
    }
  }

  protected boolean isEmpty(Control ui) {
    ui.setStyle(normalBorder);
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      try {
        tf.getText();
        if (tf.getText() == null || tf.getText().isEmpty()) {
          tf.setStyle(redBorder);
          return true;
        }
      } catch (Exception e) {
        tf.setStyle(redBorder);
        return true;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        cb.setStyle(redBorder);
        return true;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        dp.setStyle(redBorder);
        return true;
      }
    }
    return false;
  }

  protected boolean isTooLong(Control str, int length) {
    str.setStyle(normalBorder);
    if (str instanceof TextField) {
      TextField tf = (TextField) str;
      if (tf.getText() == null || tf.getText().length() > length) {
        tf.setStyle(redBorder);
        return true;
      }
    } else if (str instanceof TextArea) {
      TextArea ta = (TextArea) str;
      if (ta.getText() == null || ta.getText().length() > length) {
        ta.setStyle(redBorder);
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

  protected boolean handleHomeClick(Setting setting) {
    if (setting != Setting.VIEW) {
      Alert alertHome = new Alert(AlertType.CONFIRMATION);
      alertHome.setTitle("Going home?");
      alertHome.setHeaderText("If you go home, all progress will be lost.");
      alertHome.setContentText("Are you sure you want to go home?");

      if (alertHome.showAndWait().get() != ButtonType.OK) {
        return false;
      }
    }
    return true;
  }
}
