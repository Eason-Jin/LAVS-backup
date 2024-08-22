package uoa.lavs.controllers.interfaces;

import javafx.scene.control.Control;

public interface CheckLength {
  boolean checkLengths();

  boolean checkLength(Control ui, int length);
}
