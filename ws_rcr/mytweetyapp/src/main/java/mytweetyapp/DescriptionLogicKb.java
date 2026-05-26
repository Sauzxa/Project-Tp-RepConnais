package mytweetyapp;

/**
 * Encodage canonique de l'ontologie TP5 en syntaxe de logique descriptive (DL).
 * OWL ({@link desclogic}) est la serialisation ; cette classe expose la notation cours RCR.
 */
public final class DescriptionLogicKb {

    private DescriptionLogicKb() {
    }

    /** Axiomes de subsumption et contraintes (T-Box). */
    public static final String[] TBOX_SUBSUMPTIONS = {
            "Vehicle ⊑ TransportEntity",
            "PublicTransport ⊑ Vehicle",
            "ElectricVehicle ⊑ Vehicle",
            "DieselVehicle ⊑ Vehicle",
            "Tram ⊑ ElectricVehicle ⊓ PublicTransport",
            "Bus ⊑ PublicTransport",
            "Station ⊑ TransportEntity",
            "ElectricVehicle ⊓ DieselVehicle ⊑ ⊥"
    };

    /** Roles : domaine, codomaine, symetrie (T-Box). */
    public static final String[] TBOX_ROLES = {
            "stopsAt : Vehicle → Station",
            "connects : Station → Station",
            "connects est symetrique",
            "passengerCapacity : Vehicle → ℕ (attribut fonctionnel)"
    };

    /** Assertions sur les individus (A-Box). */
    public static final String[] ABOX_ASSERTIONS = {
            "Tram(tramA)",
            "stopsAt(tramA, northPark)",
            "passengerCapacity(tramA, 180)",
            "Bus(bus14)",
            "DieselVehicle(bus14)",
            "stopsAt(bus14, centralStation)",
            "passengerCapacity(bus14, 95)",
            "Station(northPark)",
            "Station(centralStation)",
            "connects(northPark, centralStation)"
    };

    /** Requetes de demonstration en DL (instance checking / assertion roles). */
    public static final String[] DEMO_QUERIES = {
            "ElectricVehicle(tramA) ?",
            "Tram(tramA) ?",
            "ElectricVehicle(bus14) ?",
            "DieselVehicle(bus14) ?",
            "stopsAt(tramA, northPark) ?",
            "stopsAt(bus14, centralStation) ?",
            "connects(northPark, centralStation) ?",
            "connects(centralStation, northPark) ?   /* par symetrie */"
    };

    public static void printDlKnowledge() {
        System.out.println("=== Logique descriptive (notation DL) ===");
        System.out.println("Symboles : ⊓ (et), ⊑ (subsomption), ⊥ (incoherent),");
        System.out.println("           C(a) (appartenance), R(a,b) (role), ∃R.C, ∀R.C\n");

        System.out.println("--- T-BOX : subsumptions ---");
        for (String ax : TBOX_SUBSUMPTIONS) {
            System.out.println("  " + ax);
        }

        System.out.println("\n--- T-BOX : roles ---");
        for (String ax : TBOX_ROLES) {
            System.out.println("  " + ax);
        }

        System.out.println("\n--- A-BOX : assertions ---");
        for (String ax : ABOX_ASSERTIONS) {
            System.out.println("  " + ax);
        }

        System.out.println("\n--- Requetes (forme DL) ---");
        for (String q : DEMO_QUERIES) {
            System.out.println("  " + q);
        }
    }

    /** Formule DL pour une requete de type (instance checking). */
    public static String classAssertion(String individual, String concept) {
        return concept + "(" + individual + ")";
    }

    /** Formule DL pour une requete de role binaire. */
    public static String roleAssertion(String individual, String role, String target) {
        return role + "(" + individual + ", " + target + ")";
    }
}
