package uoa.lavs.controllers.interfaces;

import javafx.scene.control.Control;

public interface ValidateType {
  boolean validateFields();

  boolean validate(Control element, Type type);

  enum Type {
    DATE,
    EMAIL,
    NUMBER,
    WEBSITE,
    PRIMARY
  }
}
