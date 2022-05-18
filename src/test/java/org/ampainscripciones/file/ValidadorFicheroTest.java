package org.ampainscripciones.file;

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

}