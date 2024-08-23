package uoa.lavs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

@Controller
public class CustomerController {
  @FXML
  public void initialize() {
    System.out.println("CustomerController initialized");
  }

  @FXML
  private void onClickAddAddress(ActionEvent event) {
    System.out.println("Add Address clicked");
  }

  @FXML
  private void onClickAddEmail(ActionEvent event) {
    System.out.println("Add Email clicked");
  }

  @FXML
  private void onClickAddPhone(ActionEvent event) {
    System.out.println("Add Phone clicked");
  }

  @FXML
  private void onClickAddEmployment(ActionEvent event) {
    System.out.println("Add Employment clicked");
  }

  @FXML
  private void onClickAddLoan(ActionEvent event) {
    Main.setScene(AppScene.ADD_LOAN);
  }

  @FXML
  private void onClickCancel(ActionEvent event) {
    System.out.println("Cancel clicked");
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    System.out.println("Save clicked");
  }

  @FXML
  private void onClickHome(ActionEvent event) {
    Main.setScene(AppScene.START);
  }
}
