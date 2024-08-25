package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Detail;

public abstract class PopupController extends Controller {
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

  abstract void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, Boolean isPrimary);

  protected void closePopup() {
    ((Pane) popupPane.getScene().getRoot()).getChildren().remove(popupPane);
  }
}
