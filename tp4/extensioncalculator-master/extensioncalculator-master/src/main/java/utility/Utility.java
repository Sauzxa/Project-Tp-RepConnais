package utility;

import controller.MainController;
import javafx.scene.control.Alert;
import model.dlogic.Default;
import model.dlogic.DefaultTheory;
import model.plogic.Formula;
import parser.FParser;
import parser.FormulaParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public static DefaultTheory theory;
    public static MainController mainController;

    private Utility(){}

    public static void invalidFormulaAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid formula");
        alert.setHeaderText(null);
        alert.setContentText("The formula you entered is invalid");

        alert.showAndWait();
    }

    public static void loadTheory(File file) {
        try {
            try (Scanner s = new Scanner(file)) {

                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    String regex = "(?<default>([a-z&|~()]+)?:[a-z&|~(),]+/[a-z&|~()]+).|(?<formula>[a-z&|~()]+).";

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.matches() && matcher.group("formula") != null) {
                        theory.add(FParser.parse(matcher.group("formula")));
                    } else if (matcher.matches() && matcher.group("default") != null) {
                        theory.add(new Default(matcher.group("default")));
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}