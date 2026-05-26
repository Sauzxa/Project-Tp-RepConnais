package mytweetyapp;

/**
 * Base de connaissances TP2 — logique propositionnelle appliquee au traitement
 * d'une commande e-commerce (chaque proposition = un fait booleen du systeme).
 */
public final class ECommercePlKb {

    public enum Scenario {
        /** Commande valide : paiement, stock, anti-fraude, adresse OK. */
        SUCCESS,
        /** Paiement et stock OK mais echec anti-fraude : commande bloquee. */
        FRAUD_BLOCKED,
        /** Stock indisponible : declenchement remboursement. */
        OUT_OF_STOCK
    }

    public static final String DOMAIN =
            "Plateforme e-commerce — pipeline de validation et expedition d'une commande";

    /** Variables propositionnelles (atomes PL). */
    public static final String[] PROPOSITIONS = {
            "PaymentReceived      — le paiement est confirme",
            "ItemInStock          — l'article est en stock",
            "FraudCheckPassed     — le controle anti-fraude est valide",
            "ShippingAddressValid — l'adresse de livraison est valide",
            "OrderConfirmed       — la commande est validee (agregation des preconditions)",
            "OrderShipped         — la commande est expediee",
            "CustomerNotified     — le client a recu une notification",
            "RefundIssued         — un remboursement est emis"
    };

    /** Regles metier encodees en implications PL (=>). */
    public static final String[] RULES = {
            "(PaymentReceived && ItemInStock && FraudCheckPassed && ShippingAddressValid) => OrderConfirmed",
            "OrderConfirmed => OrderShipped",
            "OrderShipped => CustomerNotified",
            "(!PaymentReceived || !ItemInStock) => RefundIssued",
            "OrderShipped => !RefundIssued"
    };

    public static final String[] FACTS_SUCCESS = {
            "PaymentReceived",
            "ItemInStock",
            "FraudCheckPassed",
            "ShippingAddressValid"
    };

    public static final String[] FACTS_FRAUD_BLOCKED = {
            "PaymentReceived",
            "ItemInStock",
            "ShippingAddressValid",
            "!FraudCheckPassed"
    };

    public static final String[] FACTS_OUT_OF_STOCK = {
            "PaymentReceived",
            "!ItemInStock",
            "FraudCheckPassed",
            "ShippingAddressValid"
    };

    public static final String[] DEMO_QUERIES_SUCCESS = {
            "OrderConfirmed ?",
            "OrderShipped ?",
            "CustomerNotified ?",
            "RefundIssued ?",
            "!PaymentReceived ?"
    };

    public static final String[] DEMO_QUERIES_FRAUD = {
            "OrderConfirmed ?  (attendu: faux)",
            "OrderShipped ?    (attendu: faux)",
            "RefundIssued ?    (attendu: faux — paiement OK)"
    };

    public static final String[] DEMO_QUERIES_STOCK = {
            "OrderShipped ?    (attendu: faux)",
            "RefundIssued ?    (attendu: vrai — stock manquant)"
    };

    private ECommercePlKb() {
    }

    public static String[] factsFor(Scenario scenario) {
        return switch (scenario) {
            case SUCCESS -> FACTS_SUCCESS;
            case FRAUD_BLOCKED -> FACTS_FRAUD_BLOCKED;
            case OUT_OF_STOCK -> FACTS_OUT_OF_STOCK;
        };
    }

    public static String scenarioLabel(Scenario scenario) {
        return switch (scenario) {
            case SUCCESS -> "Commande acceptee (toutes preconditions remplies)";
            case FRAUD_BLOCKED -> "Commande bloquee (echec anti-fraude)";
            case OUT_OF_STOCK -> "Commande annulee (rupture de stock, remboursement)";
        };
    }

    public static void printKnowledge(Scenario scenario) {
        System.out.println("=== Logique propositionnelle — E-commerce ===");
        System.out.println(DOMAIN);
        System.out.println("Scenario : " + scenarioLabel(scenario) + "\n");

        System.out.println("--- Propositions (atomes) ---");
        for (String p : PROPOSITIONS) {
            System.out.println("  " + p);
        }

        System.out.println("\n--- Regles (implications) ---");
        for (String r : RULES) {
            System.out.println("  " + r);
        }

        System.out.println("\n--- Faits du scenario (assertions) ---");
        for (String f : factsFor(scenario)) {
            System.out.println("  " + f);
        }
    }

    /** Nom Tweety de l'atome pour une relation Ask. */
    public static String atomForRelation(String relation) {
        if (relation == null) {
            return null;
        }
        return switch (relation.trim().toLowerCase()) {
            case "order-shipped", "ordershipped" -> "OrderShipped";
            case "customer-notified", "customernotified" -> "CustomerNotified";
            case "payment-received", "paymentreceived" -> "PaymentReceived";
            case "item-in-stock", "iteminstock" -> "ItemInStock";
            case "order-confirmed", "orderconfirmed" -> "OrderConfirmed";
            case "fraud-check-passed", "fraudcheckpassed" -> "FraudCheckPassed";
            case "shipping-address-valid", "shippingaddressvalid" -> "ShippingAddressValid";
            case "refund-issued", "refundissued" -> "RefundIssued";
            default -> null;
        };
    }
}
