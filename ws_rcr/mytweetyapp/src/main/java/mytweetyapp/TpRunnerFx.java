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
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import javafx.scene.text.Font;
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
            new ExampleDef("Projet Smart City OWL (TP 5)", "Description Logic", "desclogic", desclogic.class),
            
            // Classic TPs & Examples
            new ExampleDef("TP1 (FOL)", "First-Order Logic", "FolExample", FolExample.class),
            new ExampleDef("Projet Tramway Modal (TP 3)", "Modal Logic", "SmartCityModal", SmartCityModal.class),
            new ExampleDef("TP3 (Modal classique)", "Modal Logic", "MlExample2", MlExample2.class),
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
        semanticGraph.setPrefHeight(380);
        semanticGraph.setMinHeight(380);
        semanticGraph.setStyle("-fx-background-color: #fbfbfb; -fx-border-color: #c7c7c7;");
        StackPane graphContainer = new StackPane(semanticGraph);
        graphContainer.setVisible(false);
        graphContainer.setManaged(false);

        updateExamples(tpSelector.getValue(), byTp, exampleSelector, logicTypeLabel);
        tpSelector.valueProperty().addListener((obs, oldValue, newValue) ->
                updateExamples(newValue, byTp, exampleSelector, logicTypeLabel));
        TextField nlQuestionField = new TextField();
        nlQuestionField.setPromptText("e.g. Is tramA on northPark? / Does Dr_House work-in Hospital?");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject (tramA, alice, Dr_House...)");
        ComboBox<String> relationCombo = new ComboBox<>();
        TextField objectField = new TextField();
        objectField.setPromptText("Object (northPark, Hospital...)");
        Button askButton = new Button("Ask");
        Label queryHintLabel = new Label();
        queryHintLabel.setWrapText(true);
        VBox queryPanel = buildQueryPanel(
                nlQuestionField, subjectField, relationCombo, objectField, askButton, queryHintLabel);

        exampleSelector.valueProperty().addListener((obs, oldValue, newValue) -> {
            logicTypeLabel.setText("Logic Type: " + (newValue == null ? "-" : newValue.logicType()));
            boolean showGraph = newValue != null && hasKnowledgeGraph(newValue);
            graphContainer.setVisible(showGraph);
            graphContainer.setManaged(showGraph);
            if (showGraph) {
                renderKnowledgeGraph(semanticGraph, newValue);
            } else {
                semanticGraph.getChildren().clear();
            }
            updateQueryPanel(newValue, queryPanel, relationCombo, queryHintLabel);
        });

        askButton.setOnAction(evt -> runQuery(
                exampleSelector.getValue(),
                nlQuestionField,
                subjectField,
                relationCombo,
                objectField,
                outputArea,
                askButton));

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
        
        updateQueryPanel(exampleSelector.getValue(), queryPanel, relationCombo, queryHintLabel);

        VBox root = new VBox(15, topRow, optionsRow, queryPanel, new Separator(), outputArea, graphContainer);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Segoe UI', Helvetica, sans-serif; -fx-font-size: 14px;");
        
        runButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        askButton.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        nlQuestionField.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 4; -fx-padding: 6;");
        subjectField.setStyle(nlQuestionField.getStyle());
        objectField.setStyle(nlQuestionField.getStyle());
        relationCombo.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 4; -fx-padding: 3;");
        queryHintLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
        tpSelector.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 4; -fx-padding: 3; -fx-font-size: 13px;");
        exampleSelector.setStyle("-fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 4; -fx-padding: 3; -fx-font-size: 13px;");
        logicTypeLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-weight: bold;");
        outputArea.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #10b981; -fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 14px; -fx-border-color: #d1d5db; -fx-border-radius: 6; -fx-background-radius: 6;");
        
        VBox.setVgrow(outputArea, Priority.ALWAYS);
        VBox.setVgrow(graphContainer, Priority.ALWAYS);

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

    private static VBox buildQueryPanel(
            TextField nlQuestionField,
            TextField subjectField,
            ComboBox<String> relationCombo,
            TextField objectField,
            Button askButton,
            Label queryHintLabel) {
        Label queryTitle = new Label("Ask a question");
        queryTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        HBox nlRow = new HBox(10, new Label("Natural language:"), nlQuestionField, askButton);
        nlQuestionField.setPrefWidth(420);
        HBox.setHgrow(nlQuestionField, Priority.ALWAYS);
        HBox fieldsRow = new HBox(10,
                new Label("Subject:"), subjectField,
                new Label("Relation:"), relationCombo,
                new Label("Object:"), objectField);
        subjectField.setPrefWidth(140);
        relationCombo.setPrefWidth(160);
        objectField.setPrefWidth(140);
        HBox.setHgrow(subjectField, Priority.SOMETIMES);
        HBox.setHgrow(objectField, Priority.SOMETIMES);
        VBox panel = new VBox(8, queryTitle, nlRow, fieldsRow, queryHintLabel);
        panel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");
        return panel;
    }

    private static void updateQueryPanel(
            ExampleDef example,
            VBox queryPanel,
            ComboBox<String> relationCombo,
            Label queryHintLabel) {
        boolean supported = example != null && ExampleQueries.supportsInteractiveQuery(example.entryClass());
        queryPanel.setVisible(supported);
        queryPanel.setManaged(supported);
        if (!supported) {
            return;
        }
        relationCombo.getItems().setAll(ExampleQueries.relationsFor(example.entryClass()));
        if (!relationCombo.getItems().isEmpty()) {
            relationCombo.getSelectionModel().selectFirst();
        }
        String hint = ExampleQueries.exampleHints().get(example.entryClass().getSimpleName());
        queryHintLabel.setText(hint == null ? "" : "Examples: " + hint);
    }

    private static void runQuery(
            ExampleDef example,
            TextField nlQuestionField,
            TextField subjectField,
            ComboBox<String> relationCombo,
            TextField objectField,
            TextArea outputArea,
            Button askButton) {
        if (example == null || !ExampleQueries.supportsInteractiveQuery(example.entryClass())) {
            outputArea.setText("Select an example that supports interactive queries.");
            return;
        }

        String subject = subjectField.getText();
        String relation = relationCombo.getValue() == null ? "" : relationCombo.getValue();
        String object = objectField.getText();

        var resolved = QueryInputResolver.resolve(
                nlQuestionField.getText(), subject, relation, object,
                example.entryClass().getSimpleName());
        if (resolved.isPresent()) {
            subject = resolved.get().subject();
            relation = resolved.get().relation();
            object = resolved.get().object();
            subjectField.setText(subject);
            if (relationCombo.getItems().contains(relation)) {
                relationCombo.setValue(relation);
            } else if (!relation.isBlank()) {
                relationCombo.getItems().add(relation);
                relationCombo.setValue(relation);
            }
            objectField.setText(object);
        }

        askButton.setDisable(true);
        try {
            QueryAnswer answer = ExampleQueries.ask(example.entryClass(), subject, relation, object);
            String line = "QUERY: " + answer.formula() + System.lineSeparator()
                    + "ANSWER: " + (answer.entailed() ? "YES" : "NO") + System.lineSeparator()
                    + answer.explanation();
            String existing = outputArea.getText();
            if (existing == null || existing.isBlank() || existing.startsWith("Running ") || existing.equals("No example selected.")) {
                outputArea.setText("=== Query result ===" + System.lineSeparator() + line);
            } else {
                outputArea.appendText(System.lineSeparator() + System.lineSeparator() + "=== Query result ===" + System.lineSeparator() + line);
            }
        } catch (Exception e) {
            outputArea.appendText(System.lineSeparator() + "Query error: " + e.getMessage());
        } finally {
            askButton.setDisable(false);
        }
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
            if (hasKnowledgeGraph(example)) {
                renderKnowledgeGraph(semanticGraph, example);
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
        if (desclogic.class.equals(clazz)) {
            return formatDescriptionLogicKb();
        }
        if (SmartCityModal.class.equals(clazz)) {
            return formatModalLogicKb();
        }
        if (ECommercePl.class.equals(clazz)) {
            return formatECommercePlKb();
        }
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

    private static String formatECommercePlKb() {
        List<String> lines = new ArrayList<>();
        lines.add(ECommercePlKb.DOMAIN);
        lines.add("Scenario (GUI): " + ECommercePlKb.scenarioLabel(ECommercePlKb.Scenario.SUCCESS));
        lines.add("Propositions:");
        for (String p : ECommercePlKb.PROPOSITIONS) {
            lines.add("  - " + p);
        }
        lines.add("Regles:");
        for (String r : ECommercePlKb.RULES) {
            lines.add("  - " + r);
        }
        lines.add("Faits:");
        for (String f : ECommercePlKb.FACTS_SUCCESS) {
            lines.add("  - " + f);
        }
        return String.join(System.lineSeparator(), lines);
    }

    private static String formatModalLogicKb() {
        List<String> lines = new ArrayList<>();
        lines.add(ModalLogicKb.DOMAIN);
        lines.add("Axiomes (□/◇):");
        for (int i = 0; i < ModalLogicKb.MODAL_AXIOMS.length; i++) {
            lines.add("  - " + ModalLogicKb.MODAL_AXIOMS[i]);
            lines.add("    " + ModalLogicKb.TWEETY_AXIOMS[i]);
        }
        lines.add("Faits:");
        for (int i = 0; i < ModalLogicKb.FACTS.length; i++) {
            lines.add("  - " + ModalLogicKb.FACTS[i]);
            lines.add("    " + ModalLogicKb.TWEETY_FACTS[i]);
        }
        return String.join(System.lineSeparator(), lines);
    }

    private static String formatDescriptionLogicKb() {
        List<String> lines = new ArrayList<>();
        lines.add("Logique descriptive (T-Box / A-Box):");
        lines.add("T-Box (subsumptions):");
        for (String ax : DescriptionLogicKb.TBOX_SUBSUMPTIONS) {
            lines.add("  - " + ax);
        }
        lines.add("T-Box (roles):");
        for (String ax : DescriptionLogicKb.TBOX_ROLES) {
            lines.add("  - " + ax);
        }
        lines.add("A-Box:");
        for (String ax : DescriptionLogicKb.ABOX_ASSERTIONS) {
            lines.add("  - " + ax);
        }
        return String.join(System.lineSeparator(), lines);
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

    private static boolean hasKnowledgeGraph(ExampleDef example) {
        if (example == null) {
            return false;
        }
        return "Semantic Networks".equals(example.logicType())
                || SmartCityModal.class.equals(example.entryClass());
    }

    private static void renderKnowledgeGraph(Pane pane, ExampleDef example) {
        if (example == null) {
            pane.getChildren().clear();
            return;
        }
        if (SmartCityModal.class.equals(example.entryClass())) {
            renderModalKripkeGraph(pane);
            return;
        }
        renderSemanticGraph(pane, example);
    }

    private static void renderModalKripkeGraph(Pane pane) {
        pane.getChildren().clear();

        ModalEvaluator eval = SmartCityModal.evaluator();
        addLegendBox(pane, 16, 12,
                "Modele de Kripke — Logique modale",
                "□ φ : vrai dans tous les mondes accessibles",
                "◇ φ : vrai dans au moins un monde accessible",
                eval.accessibilitySummary());

        double w0x = 400;
        double w0y = 210;
        double w1x = 400;
        double w1y = 48;
        double w2x = 130;
        double w2y = 210;
        double w3x = 130;
        double w3y = 48;
        double w4x = 670;
        double w4y = 48;
        double w5x = 670;
        double w5y = 210;

        for (ModalEvaluator.WorldSnapshot snap : eval.snapshots()) {
            double x;
            double y;
            Color fill;
            boolean accessible = snap.accessible();
            switch (snap.id()) {
                case "w1" -> {
                    x = w1x;
                    y = w1y;
                    fill = Color.web("#dbeafe");
                }
                case "w2" -> {
                    x = w2x;
                    y = w2y;
                    fill = Color.web("#fee2e2");
                }
                case "w3" -> {
                    x = w3x;
                    y = w3y;
                    fill = Color.web("#fef9c3");
                }
                case "w4" -> {
                    x = w4x;
                    y = w4y;
                    fill = Color.web("#ede9fe");
                }
                case "w5" -> {
                    x = w5x;
                    y = w5y;
                    fill = Color.web("#fef2f2", 0.75);
                }
                default -> {
                    x = w0x;
                    y = w0y;
                    fill = Color.web("#dcfce7");
                }
            }
            addWorldNode(pane, x, y, snap.caption(), ModalEvaluator.formatAtomLines(snap.atoms()), fill, accessible);
        }

        drawArrow(pane, w0x, w0y - 52, w1x, w1y + 52, "", 0);
        drawArrow(pane, w0x - 88, w0y, w2x + 88, w2y, "", 0);
        drawArrow(pane, w0x - 62, w0y - 42, w3x + 62, w3y + 52, "", 0);
        drawArrow(pane, w0x + 62, w0y - 42, w4x - 62, w4y + 52, "", 0);

        drawBlockedWorldHint(pane, w0x + 95, w0y, w5x - 95, w5y);

        Text axiomText = new Text(40, 358,
                "Axiomes : □(DoorOpen→¬Moving)  □¬◇(DoorOpen∧Moving)  ◇Moving  ◇PowerFailure  ◇DoorOpen  ◇Emergency");
        axiomText.setFont(Font.font("Segoe UI", 11));
        axiomText.setFill(Color.web("#475569"));
        pane.getChildren().add(axiomText);
    }

    private static void drawBlockedWorldHint(Pane pane, double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.web("#b91c1c"));
        line.getStrokeDashArray().addAll(6.0, 6.0);
        line.setStrokeWidth(1.2);
        pane.getChildren().add(line);
        Text blocked = new Text((x1 + x2) / 2 - 42, (y1 + y2) / 2 - 6, "⊘ hors R(w₀)");
        blocked.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 10));
        blocked.setFill(Color.web("#b91c1c"));
        pane.getChildren().add(blocked);
    }

    private static void addWorldNode(
            Pane pane, double x, double y, String title, List<String> atomLines, Color fill, boolean accessible) {
        double w = 175;
        double h = 28 + atomLines.size() * 14;
        Rectangle box = new Rectangle(x - w / 2, y - h / 2, w, h);
        box.setArcWidth(14);
        box.setArcHeight(14);
        box.setFill(fill);
        if (accessible) {
            box.setStroke(Color.web("#334155"));
            box.setStrokeWidth(1.5);
        } else {
            box.setStroke(Color.web("#b91c1c"));
            box.setStrokeWidth(1.5);
            box.getStrokeDashArray().addAll(8.0, 6.0);
        }

        Text titleText = new Text(x - w / 2 + 10, y - h / 2 + 16, title);
        titleText.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 12));
        titleText.setFill(Color.web("#0f172a"));
        pane.getChildren().addAll(box, titleText);

        double lineY = y - h / 2 + 32;
        for (String line : atomLines) {
            Text atomText = new Text(x - w / 2 + 10, lineY, line);
            atomText.setFont(Font.font("Consolas", 11));
            atomText.setFill(line.startsWith("✓") ? Color.web("#166534") : Color.web("#9ca3af"));
            pane.getChildren().add(atomText);
            lineY += 14;
        }
    }

    private static void addLegendBox(Pane pane, double x, double y, String... lines) {
        double h = 18 + lines.length * 14;
        double w = 520;
        Rectangle box = new Rectangle(x, y, w, h);
        box.setFill(Color.web("#f8fafc", 0.92));
        box.setStroke(Color.web("#cbd5e1"));
        box.setArcWidth(8);
        box.setArcHeight(8);
        pane.getChildren().add(box);
        double lineY = y + 16;
        for (int i = 0; i < lines.length; i++) {
            Text t = new Text(x + 10, lineY, lines[i]);
            t.setFont(Font.font("Segoe UI", i == 0 ? javafx.scene.text.FontWeight.BOLD : javafx.scene.text.FontWeight.NORMAL, i == 0 ? 12 : 11));
            t.setFill(Color.web("#334155"));
            pane.getChildren().add(t);
            lineY += 14;
        }
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
