package org.ampainscripciones.file;

import org.ampainscripciones.model.InscriptionDTO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
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
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class InscriptionsValidatorTest {

    private static final String TEST_RESOURCES_DIRECTORY = "./src/test/resources";

    @Spy
    @InjectMocks
    private InscriptionsValidator inscriptionsValidator;

    @Test
    public void validateAndCreateValidatedFile() throws IOException {
        doReturn(TEST_RESOURCES_DIRECTORY).when(this.inscriptionsValidator).getSourcesFilesFolderPath();

        File validatedFile = this.inscriptionsValidator.validateAndCreateValidatedFile();

        assertTrue(validatedFile.exists());
        try (Workbook wb = WorkbookFactory.create(validatedFile)) {
            Sheet sheet = wb.getSheetAt(0);

            // Rows with doubts
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(1).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, sheet.getRow(1).getCell(1).getCellStyle().getFillPattern());
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(2).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(4).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(5).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(11).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.PALE_BLUE.getIndex(), sheet.getRow(12).getCell(1).getCellStyle().getFillForegroundColor());

            // Payed rows
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), sheet.getRow(3).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, sheet.getRow(3).getCell(1).getCellStyle().getFillPattern());
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), sheet.getRow(9).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), sheet.getRow(13).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.LIGHT_GREEN.getIndex(), sheet.getRow(14).getCell(1).getCellStyle().getFillForegroundColor());

            // Not payed rows
            assertEquals(IndexedColors.RED1.getIndex(), sheet.getRow(6).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, sheet.getRow(6).getCell(1).getCellStyle().getFillPattern());
            assertEquals(IndexedColors.RED1.getIndex(), sheet.getRow(7).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.RED1.getIndex(), sheet.getRow(8).getCell(1).getCellStyle().getFillForegroundColor());
            assertEquals(IndexedColors.RED1.getIndex(), sheet.getRow(10).getCell(1).getCellStyle().getFillForegroundColor());
        }
    }

    @Test
    public void extractInscriptionData() throws IOException {
        File file = new File(TEST_RESOURCES_DIRECTORY + "/inscriptions_test.xlsx");

        Map<Integer, InscriptionDTO> data = this.inscriptionsValidator.extractInscriptionsData(file);

        assertEquals(15, data.size());
        assertEquals("pepitopalotes@gmail.com", data.get(1).getEmail());
        assertEquals("pepitopalotes@gmail.com", data.get(2).getEmail());
        assertEquals("pepitopalotes34@gmail.com", data.get(3).getEmail());
        assertEquals("pepitopalotes35@gmail.com", data.get(4).getEmail());
        assertEquals("pepitopalotes36@gmail.com", data.get(5).getEmail());
        assertEquals("pepitopalotes37@gmail.com", data.get(6).getEmail());
        assertEquals("pepitopalotes38@gmail.com", data.get(7).getEmail());
        assertEquals("lafigatatia@gmail.com", data.get(8).getEmail());
        assertEquals("lamarequeva@gmail.com", data.get(9).getEmail());
        assertEquals("latiatamare@gmail.com", data.get(10).getEmail());
        assertEquals("testingname@gmail.com", data.get(11).getEmail());
        assertEquals("testingname2@gmail.com", data.get(12).getEmail());
        assertEquals("testingname3@gmail.com", data.get(13).getEmail());
        InscriptionDTO inscriptionDTO = data.get(14);
        assertEquals("testingname5@gmail.com", inscriptionDTO.getEmail());
        assertEquals("Inmaculada Inma Inma", inscriptionDTO.getParent1Name());
        assertEquals("Second3 Parent Name", inscriptionDTO.getParent2Name());
        assertEquals("Lucas Fernández Pérez", inscriptionDTO.getChild1Name());
        assertEquals("Altea13 Palotes Sánchez", inscriptionDTO.getChild2Name());
        assertEquals("María Fernández Pérez", inscriptionDTO.getAusiasChild1Name());
        assertEquals("Other Pérez Surname", inscriptionDTO.getAusiasChild2Name());
        assertEquals("testingname6@gmail.com", data.get(15).getEmail());
    }

    @Test
    public void extractInscriptionsDataThrowsIOExceptionWhenFileDoesNotExist() {
        File file = new File("A non existing file");

        assertThrows(IOException.class, () -> this.inscriptionsValidator.extractInscriptionsData(file));
    }

    @Test
    public void extractPaymentsData() throws IOException, InvalidFormatException {
        File file = new File(TEST_RESOURCES_DIRECTORY + "/Movimientos_cuenta_0281573.xls");

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
    public void returnRowsWithDoubts() throws IOException {
        List<String> paymentData = buildPaymentData();
        Map<Integer, InscriptionDTO> inscriptionData = buildInscriptionData();

        Map<Integer, String> result = this.inscriptionsValidator.returnRowsWithDoubts(inscriptionData, paymentData);

        assertEquals(4, result.size());
        assertTrue(result.containsKey(1));
        assertEquals(result.get(1), "El email de inscripción 'pepitopalotes@gmail.com' está repetido");
        assertTrue(result.containsKey(2));
        assertEquals(result.get(2), "El email de inscripción 'pepitopalotes@gmail.com' está repetido");
        assertTrue(result.containsKey(4));
        assertEquals(result.get(4), "No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes35@gmail.com' y el del pago es 'pepitopalotes35@gml.com'");
        assertTrue(result.containsKey(5));
        assertEquals(result.get(5), "No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes36@gmail.com' y el del pago es 'pepitopalotes36@hotmail.com'");
        assertTrue(result.containsKey(12));
        assertEquals(result.get(12), "El nombre del padre/madre 'Parent Name Surname' está repetido");
        assertTrue(result.containsKey(13));
        assertEquals(result.get(13), "El nombre del padre/madre 'Parent Name Surname' está repetido");
    }

    @Test
    public void returnPayedRows() throws IOException {
        List<String> paymentData = buildPaymentData();
        Map<Integer, InscriptionDTO> inscriptionData = buildInscriptionData();

        List<Integer> result = this.inscriptionsValidator.returnPayedRows(inscriptionData, paymentData);

        assertEquals(4, result.size());
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
        assertTrue(result.contains(4));
        assertTrue(result.contains(10));
    }

    @Test
    public void getInscriptionFile() throws IOException {
        doReturn("src/test/resources").when(this.inscriptionsValidator).getSourcesFilesFolderPath();

        File result = this.inscriptionsValidator.getInscriptionFile();

        assertTrue(result.exists());
        assertEquals("inscriptions_test.xlsx", result.getName());
    }

    @Test
    public void getInscriptionFileThrowsIOExceptionWhenFileNotFoundInPath() throws IOException {
        doReturn("src/test").when(this.inscriptionsValidator).getSourcesFilesFolderPath();

        assertThrows(IOException.class, () -> this.inscriptionsValidator.getInscriptionFile());
    }

    @Test
    public void getPaymentsFile() throws IOException {
        doReturn("src/test/resources").when(this.inscriptionsValidator).getSourcesFilesFolderPath();

        File result = this.inscriptionsValidator.getPaymentsFile();

        assertTrue(result.exists());
        assertEquals("Movimientos_cuenta_0281573.xls", result.getName());
    }

    @Test
    public void getPaymentsFileThrowsIOExceptionWhenFileNotFoundInPath() throws IOException {
        doReturn("src/test").when(this.inscriptionsValidator).getSourcesFilesFolderPath();

        assertThrows(IOException.class, () -> this.inscriptionsValidator.getPaymentsFile());
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

    private Map<Integer, InscriptionDTO> buildInscriptionData() throws IOException {
        File file = new File(TEST_RESOURCES_DIRECTORY + "/inscriptions_test.xlsx");
        return this.inscriptionsValidator.extractInscriptionsData(file);
    }

}