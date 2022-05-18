package org.ampainscripciones.file;

import org.ampainscripciones.model.InscriptionDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidadorFicheroTest {

    private static String INSCRIPTIONS_TEST_FILE = "./src/test/resources/inscriptions_test.xlsx";

    @Spy
    @InjectMocks
    private ValidadorFichero validadorFichero;

    @Test
    public void extractInscriptionsData() throws IOException, InvalidFormatException {
        File file = new File(INSCRIPTIONS_TEST_FILE);

        Map<Integer, InscriptionDTO> data = this.validadorFichero.extractInscriptionsData(file);

        assertEquals(9, data.size());
        assertEquals("pepitopalotes33@gmail.com", data.get(0).getEmail());
        assertEquals("pepitopalotes34@gmail.com", data.get(1).getEmail());
        assertEquals("pepitopalotes35@gmail.com", data.get(2).getEmail());
        assertEquals("pepitopalotes36@gmail.com", data.get(3).getEmail());
        assertEquals("pepitopalotes37@gmail.com", data.get(4).getEmail());
        assertEquals("pepitopalotes38@gmail.com", data.get(5).getEmail());
        assertEquals("lafigatatia@gmail.com", data.get(6).getEmail());
        assertEquals("lamarequeva@gmail.com", data.get(7).getEmail());
        assertEquals("latiatamare@gmail.com", data.get(8).getEmail());
    }

}