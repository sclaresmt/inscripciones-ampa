package org.ampainscripciones.file;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidadorFicheroTest {

    private static final String INSCRIPTIONS_TEST_FILE = "./src/test/resources/inscriptions_test.xlsx";

    private static final String PAYMENTS_TEST_FILE = "./src/test/resources/Movimientos_cuenta_0281573.xls";

    @Spy
    @InjectMocks
    private ValidadorFichero validadorFichero;

    @Test
    public void extractEmailData() throws IOException, InvalidFormatException {
        File file = new File(INSCRIPTIONS_TEST_FILE);

        Map<Integer, String> data = this.validadorFichero.extractEmailData(file);

        assertEquals(10, data.size());
        assertEquals("pepitopalotes@gmail.com", data.get(1));
        assertEquals("pepitopalotes@gmail.com", data.get(2));
        assertEquals("pepitopalotes34@gmail.com", data.get(3));
        assertEquals("pepitopalotes35@gmail.com", data.get(4));
        assertEquals("pepitopalotes36@gmail.com", data.get(5));
        assertEquals("pepitopalotes37@gmail.com", data.get(6));
        assertEquals("pepitopalotes38@gmail.com", data.get(7));
        assertEquals("lafigatatia@gmail.com", data.get(8));
        assertEquals("lamarequeva@gmail.com", data.get(9));
        assertEquals("latiatamare@gmail.com", data.get(10));
    }

    @Test
    public void extractEmailDataThrowsIOExceptionWhenFileDoesNotExist() {
        File file = new File("A non existing file");

        assertThrows(IOException.class, () -> this.validadorFichero.extractEmailData(file));
    }

    @Test
    public void extractPaymentsData() throws IOException, InvalidFormatException {
        File file = new File(PAYMENTS_TEST_FILE);

        List<String> data = this.validadorFichero.extractPaymentsData(file);

        assertEquals(10, data.size());
        assertEquals("XXXXXXXX-pepitopalotes@gmail.com", data.get(0));
        assertEquals("XXXXXXXX-PAULA APELLIDO APELLIDO2", data.get(1));
        assertEquals("CCCXXXXXXXXXXXXXXXXXXXX", data.get(2));
        assertEquals("YYYYYYYY-LINA APELLIDO APELLIDO", data.get(3));
        assertEquals("ZZZZZZZZ-LAURA LAURA LAURA", data.get(4));
        assertEquals("AAAAAAAA-INMACULADA INMA INMA", data.get(5));
        assertEquals("XXXXXXXX-pepitopalotes34@gmail.com", data.get(6));
        assertEquals("XXXXXXXX-pepitopalotes35@gml.com", data.get(7));
        assertEquals("XXXXXXXX-lamarequeva@gmail.com", data.get(8));
        assertEquals("XXXXXXXX-pepitopalotes36@hotmail.com", data.get(9));
    }

    @Test
    public void extractPaymentsDataThrowsIOExceptionWhenFileDoesNotExist() {
        File file = new File("A non existing file");

        assertThrows(IOException.class, () -> this.validadorFichero.extractPaymentsData(file));
    }

    @Test
    public void returnRowsWithDoubts() {
        List<String> paymentData = buildPaymentData();
        Map<Integer, String> inscriptionData = buildInscriptionData();

        Map<Integer, String> result = this.validadorFichero.returnRowsWithDoubts(inscriptionData, paymentData);

        assertEquals(4, result.size());
        assertTrue(result.containsKey(1));
        assertEquals(result.get(1), "El email de inscripción 'pepitopalotes@gmail.com' está repetido");
        assertTrue(result.containsKey(2));
        assertEquals(result.get(2), "El email de inscripción 'pepitopalotes@gmail.com' está repetido");
        assertTrue(result.containsKey(4));
        assertEquals(result.get(4), "No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes35@gmail.com' y el del pago es 'pepitopalotes35@gml.com'");
        assertTrue(result.containsKey(5));
        assertEquals(result.get(5), "No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes36@gmail.com' y el de pago es 'pepitopalotes36@hotmail.com'");
    }

    @Test
    public void returnPayedRows() {
        List<String> paymentData = buildPaymentData();
        Map<Integer, String> inscriptionData = buildInscriptionData();

        List<Integer> result = this.validadorFichero.returnPayedRows(inscriptionData, paymentData);

        assertEquals(3, result.size());
        assertTrue(result.contains(6));
        assertTrue(result.contains(5));
        assertTrue(result.contains(9));
    }

    private List<String> buildPaymentData() {
        List<String> payementData = new ArrayList<>();
        payementData.add("XXXXXXXX-pepitopalotes@gmail.com");
        payementData.add("XXXXXXXX-PAULA APELLIDO APELLIDO2");
        payementData.add("CCCXXXXXXXXXXXXXXXXXXXX");
        payementData.add("YYYYYYYY-LINA APELLIDO APELLIDO");
        payementData.add("ZZZZZZZZ-LAURA LAURA LAURA");
        payementData.add("AAAAAAAA-INMACULADA INMA INMA");
        payementData.add("XXXXXXXX-pepitopalotes34@gmail.com");
        payementData.add("XXXXXXXX-pepitopalotes35@gml.com");
        payementData.add("XXXXXXXX-lamarequeva@gmail.com");
        payementData.add("XXXXXXXX-pepitopalotes36@hotmail.com");
        return payementData;
    }

    private Map<Integer, String> buildInscriptionData() {
        Map<Integer, String> inscriptionData = new HashMap<>();
        inscriptionData.put(1, "pepitopalotes@gmail.com");
        inscriptionData.put(2, "pepitopalotes@gmail.com");
        inscriptionData.put(3, "pepitopalotes34@gmail.com");
        inscriptionData.put(4, "pepitopalotes35@gmail.com");
        inscriptionData.put(5, "pepitopalotes36@gmail.com");
        inscriptionData.put(6, "pepitopalotes37@gmail.com");
        inscriptionData.put(7, "pepitopalotes38@gmail.com");
        inscriptionData.put(8, "lafigatatia@gmail.com");
        inscriptionData.put(9, "lamarequeva@gmail.com");
        inscriptionData.put(10, "latiatamare@gmail.com");
        return inscriptionData;
    }

}