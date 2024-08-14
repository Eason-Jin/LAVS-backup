package uoa.lavs.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.dataoperations.CustomerUpdater;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.models.Customer;
import atlantafx.base.theme.Styles;


import java.io.IOException;

public class AddCustomerController {
    
    @FXML private Button homeButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button infoButton;

    @FXML private TextField titleField;
    @FXML private TextField familyNameField;
    @FXML private TextField givenNameField;
    @FXML private DatePicker dobPicker;

    @FXML private TextField citizenshipField;
    @FXML private TextField visaField;

    @FXML private TextField address1Field;
    @FXML private TextField address2Field;
    @FXML private TextField suburbField;
    @FXML private TextField cityField;
    @FXML private TextField postcodeField;
    @FXML private TextField countryField;
    @FXML private CheckBox isPrimaryAddress;
    @FXML private CheckBox isMailingAddress;

    @FXML private TextField emailField;
    @FXML private CheckBox isPrimaryEmail;
    @FXML private ComboBox<FXCollections> phoneTypeBox;
    @FXML private TextField prefixField;
    @FXML private TextField numberField;
    @FXML private CheckBox isPrimaryNumber;
    @FXML private CheckBox isTextingNumber;

    @FXML private TextField jobField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyAddress1Field;
    @FXML private TextField companyAddress2Field;
    @FXML private TextField companySuburbField;
    @FXML private TextField companyCityField;
    @FXML private TextField companyPostcodeField;
    @FXML private TextField companyCountryField;
    @FXML private TextField employerPhoneField;
    @FXML private TextField employerEmailField;
    @FXML private TextField companyWebsiteField;

    @FXML
    private void onClickHome(ActionEvent event) throws IOException {
        Main.setScene(SceneManager.AppScene.START);
    }

    @FXML
    private void onClickSave(ActionEvent event) {
        // Save the form to the mainframe
        
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        // Clear the form
    }

    @FXML
    private void onClickInfo(ActionEvent event) {
        // Show the help dialog
    }
}
