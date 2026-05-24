package controller;

import model.plogic.Formula;
import parser.FParser;
import utility.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewAxiomController {

    @FXML
    private TextField textField;

    @FXML
    private Button cancelButton;

    @FXML
    private void add() {
        String s = textField.getText();
        Formula f = FParser.parse(s);

        if (f != null) {
            Utility.theory.add(f);
            this.close();
        } else {
            Utility.invalidFormulaAlert();
        }
    }

    @FXML
    private void close() {
        Stage s = (Stage) cancelButton.getScene().getWindow();
        s.close();
    }

}
