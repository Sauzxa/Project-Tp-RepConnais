package mytweetyapp;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;

/**
 * Point d'entree TP3 logique modale — delegue a {@link SmartCityModal}.
 */
public class Modal {

    public static void main(String[] args) throws ParserException, IOException {
        SmartCityModal.main(args);
    }
}
