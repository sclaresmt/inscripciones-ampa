package org.ampainscripciones;

import org.ampainscripciones.file.InscriptionsValidator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Validando inscripciones...");
        new InscriptionsValidator().validateAndCreateValidatedFile();
    }

}