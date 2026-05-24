package controller;

import model.dlogic.Default;
import model.plogic.Formula;
import parser.FParser;
import utility.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class NewDefaultController {
    @FXML
    private TextField prereqTextField;
    @FXML
    private TextField justTextField;
    @FXML
    private TextField consTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private void add() {
        Formula prereq;

        if (!prereqTextField.getText().isEmpty()) {
            prereq = FParser.parse(prereqTextField.getText());
        } else {
            prereq = null;
        }


        Formula cons = FParser.parse(consTextField.getText());

        Set<Formula> justifications = new HashSet<>();
        String justStrings[] = justTextField.getText().split(",");

        for(String s : justStrings) {
            justifications.add(FParser.parse(s));
        }

        if ((prereq == null || prereq.isValid()) && cons != null && cons.isValid()) {
            for (Formula f : justifications) {
                if (!(f != null && f.isValid())) {
                    Utility.invalidFormulaAlert();
                    return;
                }
            }

            Default d = new Default(prereq,justifications,cons);
            Utility.theory.add(d);
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
