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
        InscriptionDTO inscriptionDTO = data.get(10);
        assertEquals("latiatamare@gmail.com", inscriptionDTO.getEmail());
        assertEquals("Pepito Palotes Pérez", inscriptionDTO.getParent1Name());
        assertEquals("Second3 Parent Name", inscriptionDTO.getParent2Name());
        assertEquals("Lucas Fernández Pérez", inscriptionDTO.getChild1Name());
        assertEquals("Ágata Tururú López", inscriptionDTO.getChild2Name());
        assertEquals("María Fernández Pérez", inscriptionDTO.getAusiasChild1Name());
        assertEquals("Other Pérez Surname", inscriptionDTO.getAusiasChild2Name());
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

        assertEquals(14, data.size());
        assertEquals("pepitopalotes@gmail.com", data.get(0));
        assertEquals("PAULA APELLIDO APELLIDO2", data.get(1));
        assertEquals("CCCXXXXXXXXXXXXXXXXXXXX", data.get(2));
        assertEquals("LINA APELLIDO APELLIDO", data.get(3));
        assertEquals("LAURA LAURA LAURA", data.get(4));
        assertEquals("INMACULADA INMA INMA", data.get(5));
        assertEquals("PEPITOPALOTES34@GMAIL.COM", data.get(6));
        assertEquals("PEPITOPALOTES35@GIL.CO", data.get(7));
        assertEquals("lamarequeva@gmail.com", data.get(8));
        assertEquals("PEPITOPALOTES36ARROBAHOTMAIL.COM", data.get(9));
        assertEquals("RAMON GARCIA PEREZ", data.get(10));
        assertEquals("PEPITOPALOTES38ARROBAGMAIL.COM", data.get(11));
        assertEquals("AGATA TURURU", data.get(12));
        assertEquals("Pepito7", data.get(13));
    }

    @Test
    public void extractPaymentsDataThrowsIOExceptionWhenFileDoesNotExist() {
        File file = new File("A non existing file");

        assertThrows(IOException.class, () -> this.inscriptionsValidator.extractPaymentsData(file));
    }

    @Test
    public void returnRowsWithDoubts() throws IOException {
        final List<String> paymentData = buildPaymentData();
        final File file = new File(TEST_RESOURCES_DIRECTORY + "/validate_doubts_test.xlsx");
        final Map<Integer, InscriptionDTO> inscriptionData = this.inscriptionsValidator.extractInscriptionsData(file);

        Map<Integer, String> result = this.inscriptionsValidator.returnRowsWithDoubts(inscriptionData, paymentData);

        assertEquals(22, result.size());
        assertEquals("El email de inscripción 'pepitopalotes@gmail.com' está repetido", result.get(1));
        assertEquals("El email de inscripción 'pepitopalotes@gmail.com' está repetido", result.get(2));
        assertEquals("El nombre del padre/madre 1 'Pepito2 Palotes Pérez' está repetido", result.get(3));
        assertEquals("El nombre del padre/madre 1 'Pepito2 Palotes Pérez' está repetido", result.get(4));
        assertEquals("El nombre del padre/madre 1 'Pepito3 Palotes Pérez' está repetido", result.get(5));
        assertEquals("El nombre del padre/madre 2 'Pepito3 Palotes Pérez' está repetido", result.get(6));
        assertEquals("El nombre del padre/madre 2 'Pepito6 Palotes Pérez' está repetido", result.get(7));
        assertEquals("El nombre del padre/madre 2 'Pepito6 Palotes Pérez' está repetido", result.get(8));
        assertEquals("El nombre del/la niño/a 2 'Altea8 Palotes Sánchez' está repetido", result.get(9));
        assertEquals("El nombre del/la niño/a 2 'Altea8 Palotes Sánchez' está repetido", result.get(10));
        assertEquals("El nombre del/la niño/a 2 'Altea9 Palotes Sánchez' está repetido", result.get(11));
        assertEquals("El nombre del/la niño/a 1 'Altea9 Palotes Sánchez' está repetido", result.get(12));
        assertEquals("El nombre del/la niño/a 1 'Altea13 Palotes Sánchez' está repetido", result.get(13));
        assertEquals("El nombre del/la niño/a 1 'Altea13 Palotes Sánchez' está repetido", result.get(14));
        assertEquals("El nombre del/la niño/a de Ausiás 2 'Pau Palotes Sánchez' está repetido", result.get(15));
        assertEquals("El nombre del/la niño/a de Ausiás 2 'Pau Palotes Sánchez' está repetido", result.get(16));
        assertEquals("El nombre del/la niño/a de Ausiás 1 'Pau2 Palotes Sánchez' está repetido", result.get(17));
        assertEquals("El nombre del/la niño/a de Ausiás 2 'Pau2 Palotes Sánchez' está repetido", result.get(18));
        assertEquals("El nombre del/la niño/a de Ausiás 1 'Pau5 Palotes Sánchez' está repetido", result.get(19));
        assertEquals("El nombre del/la niño/a de Ausiás 1 'Pau5 Palotes Sánchez' está repetido", result.get(20));
        assertEquals("No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes35@gmail.com' y el del pago es 'pepitopalotes35@gml.com'",
                result.get(21));
        assertEquals("No hay coincidencia exacta en el email: el de inscripción es 'pepitopalotes38@hotmail.com' y el del pago es 'pepitopalotes38@gmail.com'",
                result.get(23));
    }

    @Test
    public void returnPayedRows() throws IOException {
        List<String> paymentData = buildPaymentData();
        Map<Integer, InscriptionDTO> inscriptionData = buildInscriptionData();

        List<Integer> result = this.inscriptionsValidator.returnPayedRows(inscriptionData, paymentData);

        assertEquals(4, result.size());
        assertTrue(result.contains(3));
        assertTrue(result.contains(7));
        assertTrue(result.contains(9));
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

    private List<String> buildPaymentData() throws IOException {
        File file = new File(TEST_RESOURCES_DIRECTORY + "/Movimientos_cuenta_0281573.xls");
        return this.inscriptionsValidator.extractPaymentsData(file);
    }

    private Map<Integer, InscriptionDTO> buildInscriptionData() throws IOException {
        File file = new File(TEST_RESOURCES_DIRECTORY + "/inscriptions_test.xlsx");
        return this.inscriptionsValidator.extractInscriptionsData(file);
    }

}