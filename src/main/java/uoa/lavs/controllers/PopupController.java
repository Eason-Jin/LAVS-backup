package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Detail;

public abstract class PopupController {
  @FXML protected Pane popupPane;
  Alert alert = new Alert(Alert.AlertType.ERROR);
  StringBuilder errorMessage = new StringBuilder();

  protected void setPane(Pane pane) {
    this.popupPane = pane;
  }

  @FXML
  public void onClickClosePopup(ActionEvent event) {
    closePopup();
  }

  @FXML
  abstract void onClickSave(ActionEvent event);

  abstract void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, boolean... args);

  protected void closePopup() {
    ((Pane) popupPane.getScene().getRoot()).getChildren().remove(popupPane);
  }

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

  protected boolean isEmpty(Control ui) {
    // true means empty
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      try {
        tf.getText();
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

  protected void showAlert() {
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
