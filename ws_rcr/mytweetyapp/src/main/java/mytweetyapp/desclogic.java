package mytweetyapp;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * TP 5 : Logique descriptive (DL) — Transport public ville intelligente.
 * La base est definie en notation DL ({@link DescriptionLogicKb}) et serialisee en OWL
 * pour le raisonnement HermiT. Aligne sur {@link SmartCityFol} (TP1 FOL).
 */
public class desclogic {

    public static final String NS = "http://www.usthb.rcr/smartcity#";

    private static OWLOntology cachedOntology;
    private static OWLReasoner cachedReasoner;

    public static void main(String[] args) {
        System.out.println("=== TP 5: Logique descriptive — Transport public (Smart City) ===\n");

        try {
            DescriptionLogicKb.printDlKnowledge();
            System.out.println();

            OWLOntology ontology = loadOntology();
            System.out.println("--- Serialisation OWL (equivalent DL) ---");
            printKnowledge(ontology);

            OWLReasonerFactory factory = new ReasonerFactory();
            OWLReasoner reasoner = factory.createReasoner(ontology);
            try {
                if (!reasoner.isConsistent()) {
                    System.out.println("ERREUR: ontologie incoherente.");
                    return;
                }
                System.out.println("\n--- Raisonnement HermiT (verifier les requetes DL) ---");
                runBuiltInQueries(reasoner, ontology);
            } finally {
                reasoner.dispose();
            }
        } catch (OWLOntologyCreationException e) {
            System.err.println("Erreur chargement OWL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static OWLOntology loadOntology() throws OWLOntologyCreationException {
        if (cachedOntology != null) {
            return cachedOntology;
        }
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = findOntologyFile();
        if (file != null) {
            cachedOntology = manager.loadOntologyFromOntologyDocument(file);
        } else {
            InputStream in = desclogic.class.getResourceAsStream("/desclogic.owl");
            if (in == null) {
                throw new IllegalStateException("desclogic.owl introuvable (fichier ou classpath)");
            }
            cachedOntology = manager.loadOntologyFromOntologyDocument(in);
        }
        return cachedOntology;
    }

    public static QueryAnswer ask(String subject, String relation, String object) {
        try {
            OWLOntology ontology = loadOntology();
            OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
            OWLReasoner reasoner = getOrCreateReasoner();
            if (!reasoner.isConsistent()) {
                return new QueryAnswer(false, "?", "Ontologie incoherente.");
            }
            return evaluateQuery(reasoner, df, subject, relation, object);
        } catch (Exception e) {
            return new QueryAnswer(false, "?", "Erreur OWL: " + e.getMessage());
        }
    }

    private static synchronized OWLReasoner getOrCreateReasoner() throws OWLOntologyCreationException {
        OWLOntology ontology = loadOntology();
        if (cachedReasoner == null) {
            OWLReasonerFactory factory = new ReasonerFactory();
            cachedReasoner = factory.createReasoner(ontology);
            cachedReasoner.precomputeInferences(
                    InferenceType.CLASS_ASSERTIONS,
                    InferenceType.OBJECT_PROPERTY_ASSERTIONS);
        }
        return cachedReasoner;
    }

    private static QueryAnswer evaluateQuery(
            OWLReasoner reasoner,
            OWLDataFactory df,
            String subject,
            String relation,
            String object) {
        var parsed = NaturalLanguageQueryParser.parseFields(subject, relation, object);
        String sub;
        String rel;
        String obj;
        if (parsed.isPresent()) {
            sub = parsed.get().subject();
            rel = parsed.get().relation() == null ? "" : parsed.get().relation();
            obj = parsed.get().object() == null ? "" : parsed.get().object();
        } else {
            sub = subject == null ? "" : subject.trim();
            rel = relation == null ? "" : relation.trim().toLowerCase(Locale.ROOT);
            obj = object == null ? "" : object.trim();
        }
        rel = rel.toLowerCase(Locale.ROOT);

        if (sub.isBlank()) {
            return help("Indiquez un individu (Subject ou NL).",
                    "Ex: Tram(tramA) | Is tramA a Tram? | tramA + type + Tram");
        }

        String indName = sub;
        String clsName = obj;
        OWLNamedIndividual ind = resolveIndividual(df, sub);
        if (ind == null) {
            OWLClass clsAsSubject = resolveClass(df, sub);
            OWLNamedIndividual indFromObj = obj.isBlank() ? null : resolveIndividual(df, obj);
            if (clsAsSubject != null && indFromObj != null) {
                ind = indFromObj;
                indName = ind.getIRI().getShortForm();
                clsName = clsAsSubject.getIRI().getShortForm();
                rel = "type";
            } else {
                return help("Individu inconnu: " + sub + ". Utilisez: tramA, bus14, northPark, centralStation.",
                        sub + "(?)");
            }
        } else {
            indName = ind.getIRI().getShortForm();
        }

        if (!obj.isBlank() && ("type".equals(rel) || "is-a".equals(rel) || "a".equals(rel))) {
            clsName = obj;
        }

        OWLClass clsFromRel = rel.isBlank() ? null : resolveClass(df, rel);
        if (clsFromRel != null && obj.isBlank()) {
            clsName = clsFromRel.getIRI().getShortForm();
            rel = "type";
        }

        String formula;
        boolean entailed;

        if (rel.isBlank() || "type".equals(rel) || "is-a".equals(rel) || "a".equals(rel)) {
            if (clsName.isBlank()) {
                return help("Pour le type, donnez une classe (Object): Tram, Bus, ElectricVehicle...",
                        indName + "(?)");
            }
            OWLClass cls = resolveClass(df, clsName);
            if (cls == null) {
                return help("Classe inconnue: " + clsName,
                        DescriptionLogicKb.classAssertion(indName, clsName));
            }
            clsName = cls.getIRI().getShortForm();
            formula = DescriptionLogicKb.classAssertion(indName, clsName);
            entailed = reasoner.getTypes(ind, false).containsEntity(cls);
        } else if ("stops-at".equals(rel) || "stopsat".equals(rel) || "on".equals(rel)) {
            if (obj.isBlank()) {
                return help("Object requis: northPark ou centralStation (pas le mot generique 'station').",
                        "stopsAt(" + indName + ", ?)");
            }
            OWLNamedIndividual station = resolveIndividual(df, obj);
            OWLObjectProperty prop = df.getOWLObjectProperty(IRI.create(NS + "stopsAt"));
            if (station == null) {
                return help("Station inconnue: " + obj + " — utilisez northPark ou centralStation.",
                        DescriptionLogicKb.roleAssertion(indName, "stopsAt", obj));
            }
            formula = DescriptionLogicKb.roleAssertion(
                    indName, "stopsAt", station.getIRI().getShortForm());
            entailed = reasoner.getObjectPropertyValues(ind, prop).containsEntity(station);
        } else if ("connects".equals(rel) || "connected".equals(rel) || "connected-to".equals(rel)) {
            if (obj.isBlank()) {
                return help("Object requis: northPark ou centralStation.",
                        "connects(" + indName + ", ?)");
            }
            OWLNamedIndividual other = resolveIndividual(df, obj);
            OWLObjectProperty prop = df.getOWLObjectProperty(IRI.create(NS + "connects"));
            if (other == null) {
                return help("Station inconnue: " + obj + " — utilisez northPark ou centralStation.",
                        DescriptionLogicKb.roleAssertion(indName, "connects", obj));
            }
            formula = DescriptionLogicKb.roleAssertion(
                    indName, "connects", other.getIRI().getShortForm());
            entailed = reasoner.getObjectPropertyValues(ind, prop).containsEntity(other);
        } else {
            return help("Relations: type/is-a, stops-at, connects. NL: Tram(tramA), stopsAt(tramA,northPark).",
                    indName + " + " + rel + " + " + obj);
        }

        String msg = entailed
                ? "YES — ⊢_DL " + formula + "  (HermiT)"
                : "NO — ⊬_DL " + formula;
        return new QueryAnswer(entailed, formula, msg);
    }

    private static QueryAnswer help(String message, String suggestedFormula) {
        return new QueryAnswer(false, suggestedFormula,
                message + " Classes: Tram, Bus, ElectricVehicle, DieselVehicle, Station.");
    }

    private static void runBuiltInQueries(OWLReasoner reasoner, OWLOntology ontology) {
        OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();

        query(reasoner, df, "tramA", "type", "ElectricVehicle");
        query(reasoner, df, "tramA", "type", "Tram");
        query(reasoner, df, "bus14", "type", "ElectricVehicle");
        query(reasoner, df, "bus14", "type", "DieselVehicle");
        query(reasoner, df, "tramA", "stops-at", "northPark");
        query(reasoner, df, "bus14", "stops-at", "centralStation");
        query(reasoner, df, "northPark", "connects", "centralStation");
        query(reasoner, df, "centralStation", "connects", "northPark");
    }

    private static void query(OWLReasoner reasoner, OWLDataFactory df, String s, String r, String o) {
        QueryAnswer ans = evaluateQuery(reasoner, df, s, r, o);
        System.out.println("  " + ans.formula() + "  →  " + (ans.entailed() ? "vrai" : "faux"));
    }

    private static void printKnowledge(OWLOntology ontology) {
        System.out.println("Domaine: Transport public — Ville intelligente");
        System.out.println("(Chaque axiome OWL ci-dessous correspond a un axiome DL ci-dessus)\n");

        System.out.println("--- T-BOX (concepts) ---");
        List<String> classes = new ArrayList<>();
        for (OWLClass c : ontology.getClassesInSignature()) {
            if (!c.isOWLThing()) {
                classes.add(c.getIRI().getShortForm());
            }
        }
        classes.sort(String::compareToIgnoreCase);
        for (String c : classes) {
            System.out.println("  " + c);
        }

        System.out.println("\n--- T-BOX (roles) ---");
        for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature()) {
            System.out.println("  " + p.getIRI().getShortForm()
                    + "  domain=" + formatDomainRange(ontology, p, true)
                    + "  range=" + formatDomainRange(ontology, p, false));
        }
        for (OWLDataProperty p : ontology.getDataPropertiesInSignature()) {
            System.out.println("  " + p.getIRI().getShortForm() + " (datatype)");
        }

        System.out.println("\n--- A-BOX (individus + faits) ---");
        Map<String, List<String>> factsByIndividual = new TreeMap<>();
        for (OWLNamedIndividual ind : ontology.getIndividualsInSignature()) {
            String name = ind.getIRI().getShortForm();
            List<String> facts = new ArrayList<>();
            for (OWLClassAssertionAxiom ax : ontology.getClassAssertionAxioms(ind)) {
                if (ax.getClassExpression().isNamed()) {
                    facts.add("type " + ax.getClassExpression().asOWLClass().getIRI().getShortForm());
                }
            }
            for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(ind)) {
                if (ax.getObject().isNamed() && ax.getProperty().isOWLObjectProperty()) {
                    facts.add(ax.getProperty().asOWLObjectProperty().getIRI().getShortForm()
                            + " -> " + ax.getObject().asOWLNamedIndividual().getIRI().getShortForm());
                }
            }
            for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(ind)) {
                if (ax.getProperty().isOWLDataProperty()) {
                    facts.add(ax.getProperty().asOWLDataProperty().getIRI().getShortForm()
                            + " = " + ax.getObject().toString());
                }
            }
            factsByIndividual.put(name, facts);
        }
        for (Map.Entry<String, List<String>> e : factsByIndividual.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + String.join(", ", e.getValue()));
        }
    }

    private static String formatDomainRange(OWLOntology ont, OWLObjectProperty p, boolean domain) {
        if (domain) {
            for (OWLObjectPropertyDomainAxiom ax : ont.getObjectPropertyDomainAxioms(p)) {
                if (ax.getDomain().isNamed()) {
                    return ax.getDomain().asOWLClass().getIRI().getShortForm();
                }
            }
        } else {
            for (OWLObjectPropertyRangeAxiom ax : ont.getObjectPropertyRangeAxioms(p)) {
                if (ax.getRange().isNamed()) {
                    return ax.getRange().asOWLClass().getIRI().getShortForm();
                }
            }
        }
        return "?";
    }

    private static OWLNamedIndividual resolveIndividual(OWLDataFactory df, String localName) {
        try {
            OWLOntology ont = loadOntology();
            String id = localName.trim();
            if (id.isEmpty()) {
                return null;
            }
            String[] variants = {
                    id,
                    id.substring(0, 1).toLowerCase(Locale.ROOT) + id.substring(1),
                    id.substring(0, 1).toUpperCase(Locale.ROOT) + id.substring(1)
            };
            for (String v : variants) {
                OWLNamedIndividual ind = df.getOWLNamedIndividual(IRI.create(NS + v));
                if (ont.containsIndividualInSignature(ind.getIRI())) {
                    return ind;
                }
            }
        } catch (OWLOntologyCreationException ignored) {
            // fall through
        }
        return null;
    }

    private static OWLClass resolveClass(OWLDataFactory df, String localName) {
        try {
            OWLOntology ont = loadOntology();
            String id = localName.trim();
            String[] variants = {id, capitalize(id)};
            for (String v : variants) {
                OWLClass cls = df.getOWLClass(IRI.create(NS + v));
                if (ont.containsClassInSignature(cls.getIRI())) {
                    return cls;
                }
            }
        } catch (OWLOntologyCreationException ignored) {
            // fall through
        }
        return null;
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    private static File findOntologyFile() {
        Path[] candidates = {
                Paths.get("desclogic.owl"),
                Paths.get("../desclogic.owl"),
                Paths.get("../../desclogic.owl")
        };
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return candidate.toFile();
            }
        }
        return null;
    }
}
