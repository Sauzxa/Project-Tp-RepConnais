package mytweetyapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TpRunnerFx extends Application {

    private record ExampleDef(String tpName, String logicType, String label, Class<?> entryClass) {
        @Override
        public String toString() {
            return label;
        }
    }

    private static final List<ExampleDef> EXAMPLES = List.of(
            // New Custom Projects
            new ExampleDef("Projet E-Commerce (TP 2)", "Propositional Logic", "ECommercePl", ECommercePl.class),
            new ExampleDef("Projet Smart City (Extra FOL)", "First-Order Logic", "SmartCityFol", SmartCityFol.class),
            new ExampleDef("Projet Abonnements (TP 3)", "Default Logic", "SubscriptionDefL", SubscriptionDefL.class),
            new ExampleDef("Projet Hopital (TP 4)", "Semantic Networks", "HospitalSemanticNet", HospitalSemanticNet.class),
            new ExampleDef("Projet OWL (TP 5)", "Description Logic", "desclogic", desclogic.class),
            
            // Classic TPs & Examples
            new ExampleDef("TP1 (FOL)", "First-Order Logic", "FolExample", FolExample.class),
            new ExampleDef("TP3 (Modal/Def)", "Modal Logic", "Modal", Modal.class),
            new ExampleDef("TP3 (Modal/Def)", "Modal Logic", "MlExample2", MlExample2.class),
            new ExampleDef("TP3 (Modal/Def)", "Modal Logic", "MlExample", MlExample.class),
            new ExampleDef("TP4 (Semantic)", "Semantic Networks", "UniversitySemanticNet", UniversitySemanticNet.class),
            new ExampleDef("TP4 (Semantic)", "Semantic Networks", "BirdExceptionSemanticNet", BirdExceptionSemanticNet.class)
    );

    @Override
    public void start(Stage stage) {
        Map<String, List<ExampleDef>> byTp = groupByTp(EXAMPLES);

        ComboBox<String> tpSelector = new ComboBox<>();
        tpSelector.getItems().addAll(byTp.keySet());
        tpSelector.getSelectionModel().selectFirst();

        ComboBox<ExampleDef> exampleSelector = new ComboBox<>();
        Label logicTypeLabel = new Label("Logic Type: -");
        Button runButton = new Button("Run");
        CheckBox showRawOutput = new CheckBox("Show full output");
        CheckBox showKb = new CheckBox("Show extracted KB");
        showKb.setSelected(true);
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(360);
        outputArea.setMinHeight(300);
        Pane semanticGraph = new Pane();
        semanticGraph.setPrefHeight(320);
        semanticGraph.setMinHeight(320);
        semanticGraph.setStyle("-fx-background-color: #fbfbfb; -fx-border-color: #c7c7c7;");
        StackPane semanticContainer = new StackPane(semanticGraph);
        semanticContainer.setVisible(false);
        semanticContainer.setManaged(false);

        updateExamples(tpSelector.getValue(), byTp, exampleSelector, logicTypeLabel);
        tpSelector.valueProperty().addListener((obs, oldValue, newValue) ->
                updateExamples(newValue, byTp, exampleSelector, logicTypeLabel));
        exampleSelector.valueProperty().addListener((obs, oldValue, newValue) -> {
            logicTypeLabel.setText("Logic Type: " + (newValue == null ? "-" : newValue.logicType()));
            boolean isSemantic = newValue != null && "Semantic Networks".equals(newValue.logicType());
            semanticContainer.setVisible(isSemantic);
            semanticContainer.setManaged(isSemantic);
            if (isSemantic) {
                renderSemanticGraph(semanticGraph, newValue);
            } else {
                semanticGraph.getChildren().clear();
            }
        });

        runButton.setOnAction(
                evt -> runExample(
                        exampleSelector.getValue(),
                        showRawOutput.isSelected(),
                        showKb.isSelected(),
                        outputArea,
                        runButton,
                        semanticGraph));

        HBox topRow = new HBox(15, new Label("TP:"), tpSelector, new Label("Example:"), exampleSelector, runButton);
        topRow.setStyle("-fx-alignment: center-left;");
        
        HBox optionsRow = new HBox(15, logicTypeLabel, showRawOutput, showKb);
        optionsRow.setStyle("-fx-alignment: center-left; -fx-padding: 0 0 5 0;");
        
        VBox root = new VBox(15, topRow, optionsRow, outputArea, semanticContainer);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Segoe UI', Helvetica, sans-serif; -fx-font-size: 14px;");
        
        runButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        tpSelector.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 4; -fx-padding: 3; -fx-font-size: 13px;");
        exampleSelector.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 4; -fx-padding: 3; -fx-font-size: 13px;");
        logicTypeLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold;");
        outputArea.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #10b981; -fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 14px; -fx-border-color: #d1d5db; -fx-border-radius: 6; -fx-background-radius: 6;");
        
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        VBox.setVgrow(semanticContainer, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f0f2f5;");

        Scene scene = new Scene(scrollPane, 1040, 860);
        stage.setScene(scene);
        stage.setTitle("Tweety TP Runner - RCR Project");
        stage.setMinWidth(980);
        stage.setMinHeight(760);
        stage.show();
    }

    private static void runExample(
            ExampleDef example,
            boolean showRawOutput,
            boolean showKb,
            TextArea outputArea,
            Button runButton,
            Pane semanticGraph) {
        if (example == null) {
            outputArea.setText("No example selected.");
            return;
        }
        runButton.setDisable(true);
        outputArea.setText("Running " + example.label() + " ...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                String raw = invokeMainAndCapture(example.entryClass());
                String results = showRawOutput ? raw : toResultsOnly(raw);
                if (!showKb) {
                    return results;
                }
                String kb = extractKbFromSource(example.entryClass());
                return "=== Extracted KB (from code) ===\n"
                        + kb
                        + "\n\n=== Results ===\n"
                        + results;
            }
        };

        task.setOnSucceeded(e -> {
            outputArea.setText(task.getValue());
            if ("Semantic Networks".equals(example.logicType())) {
                renderSemanticGraph(semanticGraph, example);
            }
            runButton.setDisable(false);
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            outputArea.setText("Execution error:\n" + (ex == null ? "Unknown error" : ex.getMessage()));
            runButton.setDisable(false);
        });

        Thread worker = new Thread(task, "tp-runner-worker");
        worker.setDaemon(true);
        worker.start();
    }

    private static String invokeMainAndCapture(Class<?> clazz) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        try {
            System.setOut(capture);
            System.setErr(capture);
            Method mainMethod = clazz.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]);
        } catch (Exception e) {
            capture.println("Execution failed for " + clazz.getSimpleName() + ": " + e.getMessage());
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            capture.flush();
            capture.close();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String toResultsOnly(String rawOutput) {
        if (rawOutput == null || rawOutput.isBlank()) {
            return "No output produced.";
        }
        String[] lines = rawOutput.split("\\R");
        List<String> picked = new ArrayList<>();
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) {
                continue;
            }
            String lower = t.toLowerCase();
            if (t.startsWith("Q") || t.startsWith("ANSWER")
                    || t.contains("->") || lower.startsWith("query")
                    || lower.contains("answer")
                    || lower.contains("knowledge base")
                    || lower.contains("base de connaissances")) {
                picked.add(t);
            }
        }
        if (picked.isEmpty()) {
            return rawOutput;
        }
        return String.join(System.lineSeparator(), picked);
    }

    private static String extractKbFromSource(Class<?> clazz) {
        Path sourcePath = resolveSourcePath(clazz);
        if (sourcePath == null) {
            return "Could not locate source file.";
        }
        try {
            String source = Files.readString(sourcePath, StandardCharsets.UTF_8);
            List<String> lines = new ArrayList<>();
            lines.addAll(extractAssignedKbStrings(source));
            lines.addAll(extractParseFormulaEntries(source));
            lines.addAll(extractSemanticNetworkRelations(source));
            lines.addAll(extractOntologyHints(source));
            if (lines.isEmpty()) {
                return "No KB patterns found in source.";
            }
            return String.join(System.lineSeparator(), lines);
        } catch (IOException e) {
            return "Failed to read source: " + e.getMessage();
        }
    }

    private static Path resolveSourcePath(Class<?> clazz) {
        Path moduleRoot = Paths.get(System.getProperty("user.dir"));
        Path[] candidates = {
            moduleRoot.resolve("src/main/java/mytweetyapp/" + clazz.getSimpleName() + ".java"),
            moduleRoot.resolve("mytweetyapp/src/main/java/mytweetyapp/" + clazz.getSimpleName() + ".java")
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static List<String> extractAssignedKbStrings(String source) {
        List<String> result = new ArrayList<>();
        Pattern blockPattern = Pattern.compile("(?:kbString|bsp)\\s*=\\s*(.*?);", Pattern.DOTALL);
        Matcher blockMatcher = blockPattern.matcher(source);
        while (blockMatcher.find()) {
            String expr = blockMatcher.group(1);
            List<String> fragments = extractQuotedFragments(expr);
            if (!fragments.isEmpty()) {
                result.add("KB string:");
                for (String fragment : fragments) {
                    String[] split = fragment.split("\\\\n");
                    for (String part : split) {
                        String cleaned = part.replace("\\r", "").trim();
                        if (!cleaned.isEmpty()) {
                            result.add("  - " + cleaned);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static List<String> extractParseFormulaEntries(String source) {
        Pattern pattern = Pattern.compile("parseFormula\\(\"(.*?)\"\\)");
        Matcher matcher = pattern.matcher(source);
        List<String> formulas = new ArrayList<>();
        while (matcher.find()) {
            formulas.add(matcher.group(1).trim());
        }
        if (formulas.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        lines.add("Parsed formulas:");
        for (String formula : formulas) {
            lines.add("  - " + formula);
        }
        return lines;
    }

    private static List<String> extractSemanticNetworkRelations(String source) {
        Pattern pattern = Pattern.compile("(\\w+)\\.addRelation\\(\"([^\"]+)\",\\s*([\\w\\(\\)\"\\.]+)\\)");
        Matcher matcher = pattern.matcher(source);
        List<String> rels = new ArrayList<>();
        while (matcher.find()) {
            rels.add(matcher.group(1) + " --" + matcher.group(2) + "--> " + matcher.group(3));
        }
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        lines.add("Semantic relations:");
        for (String rel : rels) {
            lines.add("  - " + rel);
        }
        return lines;
    }

    private static List<String> extractOntologyHints(String source) {
        Pattern filePattern = Pattern.compile("String\\s+FILE\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = filePattern.matcher(source);
        if (!matcher.find()) {
            return Collections.emptyList();
        }
        List<String> lines = new ArrayList<>();
        lines.add("Ontology source:");
        lines.add("  - " + matcher.group(1));
        lines.add("  - Concepts/Roles/Individuals are loaded from this ontology file at runtime.");
        return lines;
    }

    private static List<String> extractQuotedFragments(String text) {
        Pattern quotePattern = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = quotePattern.matcher(text);
        List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            parts.add(matcher.group(1));
        }
        return parts;
    }

    private static Map<String, List<ExampleDef>> groupByTp(List<ExampleDef> source) {
        Map<String, List<ExampleDef>> byTp = new LinkedHashMap<>();
        for (ExampleDef def : source) {
            byTp.computeIfAbsent(def.tpName(), k -> new ArrayList<>()).add(def);
        }
        return byTp;
    }

    private static void updateExamples(
            String tpName,
            Map<String, List<ExampleDef>> byTp,
            ComboBox<ExampleDef> exampleSelector,
            Label logicTypeLabel) {
        exampleSelector.getItems().clear();
        List<ExampleDef> defs = byTp.get(tpName);
        if (defs == null || defs.isEmpty()) {
            logicTypeLabel.setText("Logic Type: -");
            return;
        }
        exampleSelector.getItems().addAll(defs);
        exampleSelector.getSelectionModel().selectFirst();
        ExampleDef selected = exampleSelector.getValue();
        logicTypeLabel.setText("Logic Type: " + (selected == null ? "-" : selected.logicType()));
    }

    private static void renderSemanticGraph(Pane pane, ExampleDef example) {
        if (example == null) {
            pane.getChildren().clear();
            return;
        }
        if ("UniversitySemanticNet".equals(example.label())) {
            renderUniversitySemanticGraph(pane);
            return;
        }
        if ("BirdExceptionSemanticNet".equals(example.label())) {
            renderBirdExceptionGraph(pane);
            return;
        }
        renderHospitalSemanticGraph(pane);
    }

    private static void renderHospitalSemanticGraph(Pane pane) {
        pane.getChildren().clear();

        addRectNode(pane, 300, 78, 140, 48, "Doctor");
        addRectNode(pane, 610, 78, 200, 48, "Healthcare_Professional");
        addEllipseNode(pane, 300, 206, 78, 32, "Surgeon");
        addEllipseNode(pane, 110, 206, 95, 34, "Dr_House");
        addEllipseNode(pane, 720, 244, 78, 32, "Hospital");
        addEllipseNode(pane, 860, 206, 78, 32, "Surgery");

        drawArrow(pane, 300, 174, 300, 106, "is-a", -20);
        drawArrow(pane, 180, 184, 272, 106, "is-a", -10);
        drawArrow(pane, 370, 78, 510, 78, "is-a", -10);
        drawArrow(pane, 610, 102, 682, 216, "works-in", 10);
        drawArrow(pane, 378, 206, 782, 206, "performs", -10);
    }

    private static void renderUniversitySemanticGraph(Pane pane) {
        pane.getChildren().clear();

        addRectNode(pane, 290, 78, 140, 48, "Professor");
        addRectNode(pane, 570, 78, 120, 48, "Person");
        addEllipseNode(pane, 290, 206, 92, 34, "AI_Professor");
        addEllipseNode(pane, 100, 206, 90, 34, "Dr_Smith");
        addEllipseNode(pane, 570, 244, 82, 32, "University");
        addEllipseNode(pane, 860, 206, 110, 34, "Machine_Learning");

        drawArrow(pane, 290, 172, 290, 106, "is-a", -18);
        drawArrow(pane, 170, 184, 266, 106, "is-a", -8);
        drawArrow(pane, 360, 78, 510, 78, "is-a", -8);
        drawArrow(pane, 570, 102, 570, 212, "works-in", 22);
        drawArrow(pane, 382, 206, 748, 206, "teaches", -10);
    }

    private static void renderBirdExceptionGraph(Pane pane) {
        pane.getChildren().clear();

        addRectNode(pane, 260, 86, 140, 48, "Oiseaux");
        addEllipseNode(pane, 120, 214, 86, 34, "Moineaux");
        addEllipseNode(pane, 360, 214, 86, 34, "Autruche");
        addEllipseNode(pane, 560, 86, 78, 34, "Voler");
        addRectNode(pane, 560, 214, 150, 48, "Locomotion");

        drawArrow(pane, 330, 86, 482, 86, "relation generale", -10);
        drawArrow(pane, 120, 182, 235, 110, "is-a", -8);
        drawArrow(pane, 360, 182, 286, 110, "is-a", -8);
        drawArrow(pane, 560, 120, 560, 188, "is-a", 20);
        drawExceptionArrow(pane, 410, 190, 514, 108, "exception / blocage");
    }

    private static void addRectNode(Pane pane, double x, double y, double w, double h, String label) {
        Rectangle r = new Rectangle(x - w / 2, y - h / 2, w, h);
        r.setFill(Color.WHITE);
        r.setStroke(Color.BLACK);
        Text t = new Text(x - label.length() * 3.1, y + 4, label);
        pane.getChildren().addAll(r, t);
    }

    private static void addEllipseNode(Pane pane, double x, double y, double rx, double ry, String label) {
        Ellipse e = new Ellipse(x, y, rx, ry);
        e.setFill(Color.WHITE);
        e.setStroke(Color.BLACK);
        Text t = new Text(x - label.length() * 3.1, y + 4, label);
        pane.getChildren().addAll(e, t);
    }

    private static void drawArrow(Pane pane, double x1, double y1, double x2, double y2, String label, double labelYOffset) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(1.3);
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double len = 10;
        double a1 = angle - Math.toRadians(20);
        double a2 = angle + Math.toRadians(20);
        Polygon arrow = new Polygon(
                x2, y2,
                x2 - len * Math.cos(a1), y2 - len * Math.sin(a1),
                x2 - len * Math.cos(a2), y2 - len * Math.sin(a2));
        arrow.setFill(Color.BLACK);
        Text lbl = new Text((x1 + x2) / 2 + 6, (y1 + y2) / 2 + labelYOffset, label);
        pane.getChildren().addAll(line, arrow, lbl);
    }

    private static void drawExceptionArrow(Pane pane, double x1, double y1, double x2, double y2, String label) {
        Line line = new Line(x1, y1, x2, y2);
        line.getStrokeDashArray().addAll(8.0, 6.0);
        line.setStrokeWidth(2.2);
        line.setStroke(Color.FIREBRICK);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double len = 11;
        double a1 = angle - Math.toRadians(22);
        double a2 = angle + Math.toRadians(22);
        Polygon arrow = new Polygon(
                x2, y2,
                x2 - len * Math.cos(a1), y2 - len * Math.sin(a1),
                x2 - len * Math.cos(a2), y2 - len * Math.sin(a2));
        arrow.setFill(Color.FIREBRICK);
        Text lbl = new Text((x1 + x2) / 2 + 8, (y1 + y2) / 2 - 8, label);
        lbl.setFill(Color.FIREBRICK);
        pane.getChildren().addAll(line, arrow, lbl);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
