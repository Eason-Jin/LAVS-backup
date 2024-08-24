package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Detail;

public abstract class PopupController {
  @FXML protected Pane popupPane;

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
}
