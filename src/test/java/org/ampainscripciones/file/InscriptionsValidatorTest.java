package org.ampainscripciones.file;

import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class InscriptionsValidatorTest {

    private static final String INSCRIPTIONS_TEST_FILE = "./src/test/resources/inscriptions_test.xlsx";

    private static final String PAYMENTS_TEST_FILE = "./src/test/resources/Movimientos_cuenta_0281573.xls";

    @Spy
    @InjectMocks
    private InscriptionsValidator inscriptionsValidator;

    @Test
    public void validateAndCreateValidatedFile() throws IOException {
        doReturn(new File(INSCRIPTIONS_TEST_FILE)).when(this.inscriptionsValidator).getInscriptionFile();
        doReturn(new File(PAYMENTS_TEST_FILE)).when(this.inscriptionsValidator).getPaymentsFile();
        doReturn("./src/test/resources/").when(this.inscriptionsValidator).getResultFilePath();

        File validatedFile = this.inscriptionsValidator.validateAndCreateValidatedFile();

        assertTrue(validatedFile.exists());
        try (Workbook wb = WorkbookFactory.create(validatedFile)) {
            Sheet sheet = wb.getSheetAt(0);
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(2).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, sheet.getRow(2).getCell(1).getCellStyle().getFillPattern());
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), sheet.getRow(3).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, sheet.getRow(3).getCell(1).getCellStyle().getFillPattern());
            assertEquals(IndexedColors.RED1.getIndex(), sheet.getRow(6).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, sheet.getRow(6).getCell(1).getCellStyle().getFillPattern());
        }
    }

    @Test
    public void extractEmailData() throws IOException {
        File file = new File(INSCRIPTIONS_TEST_FILE);

        Map<Integer, String> data = this.inscriptionsValidator.extractEmailData(file);

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

        assertThrows(IOException.class, () -> this.inscriptionsValidator.extractEmailData(file));
    }

    @Test
    public void extractPaymentsData() throws IOException, InvalidFormatException {
        File file = new File(PAYMENTS_TEST_FILE);

        List<String> data = this.inscriptionsValidator.extractPaymentsData(file);

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

        assertThrows(IOException.class, () -> this.inscriptionsValidator.extractPaymentsData(file));
    }

    @Test
    public void returnRowsWithDoubts() {
        List<String> paymentData = buildPaymentData();
        Map<Integer, String> inscriptionData = buildInscriptionData();

        Map<Integer, String> result = this.inscriptionsValidator.returnRowsWithDoubts(inscriptionData, paymentData);

        assertEquals(4, result.size());
        assertTrue(result.containsKey(2));
        assertEquals(result.get(2), "El email de inscripción 'pepitopalotes@gmail.com' está repetido");
        assertTrue(result.containsKey(3));
        assertEquals(result.get(3), "El email de inscripción 'pepitopalotes@gmail.com' está repetido");
        assertTrue(result.containsKey(5));
        assertEquals(result.get(5), "No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes35@gmail.com' y el del pago es 'pepitopalotes35@gml.com'");
        assertTrue(result.containsKey(6));
        assertEquals(result.get(6), "No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes36@gmail.com' y el del pago es 'pepitopalotes36@hotmail.com'");
    }

    @Test
    public void returnPayedRows() {
        List<String> paymentData = buildPaymentData();
        Map<Integer, String> inscriptionData = buildInscriptionData();

        List<Integer> result = this.inscriptionsValidator.returnPayedRows(inscriptionData, paymentData);

        assertEquals(4, result.size());
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
        assertTrue(result.contains(4));
        assertTrue(result.contains(10));
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
        payementData.add("pepitopalotes35@gml.com");
        payementData.add("XXXXXXXX-lamarequeva@gmail.com");
        payementData.add("XXXXXXXX-pepitopalotes36@hotmail.com");
        return payementData;
    }

    private Map<Integer, String> buildInscriptionData() {
        Map<Integer, String> inscriptionData = new HashMap<>();
        inscriptionData.put(2, "pepitopalotes@gmail.com");
        inscriptionData.put(3, "pepitopalotes@gmail.com");
        inscriptionData.put(4, "pepitopalotes34@gmail.com");
        inscriptionData.put(5, "pepitopalotes35@gmail.com");
        inscriptionData.put(6, "pepitopalotes36@gmail.com");
        inscriptionData.put(7, "pepitopalotes37@gmail.com");
        inscriptionData.put(8, "pepitopalotes38@gmail.com");
        inscriptionData.put(9, "lafigatatia@gmail.com");
        inscriptionData.put(10, "lamarequeva@gmail.com");
        inscriptionData.put(11, "latiatamare@gmail.com");
        return inscriptionData;
    }

}