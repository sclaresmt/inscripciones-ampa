package org.ampainscripciones.file;

import org.ampainscripciones.model.InscriptionDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class InscriptionsValidator {

    protected Map<Integer, InscriptionDTO> extractInscriptionsData(final File file) throws IOException {
        final Map<Integer, InscriptionDTO> inscriptionDataByRowIndex = new HashMap<>();
        try (Workbook wb = WorkbookFactory.create(file)) {

            final Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                if (StringUtils.isBlank(getStringValueWithCheck(row.getCell(0)))) {
                    break;
                }
                final InscriptionDTO inscriptionDTO = new InscriptionDTO();
                inscriptionDTO.setEmail(getStringValueWithCheck(row.getCell(1)));
                inscriptionDTO.setParent1Name(getStringValueWithCheck(row.getCell(2)));
                inscriptionDTO.setParent2Name(getStringValueWithCheck(row.getCell(4)));
                inscriptionDTO.setChild1Name(getStringValueWithCheck(row.getCell(7)));
                inscriptionDTO.setChild2Name(getStringValueWithCheck(row.getCell(9)));
                inscriptionDTO.setAusiasChild1Name(getStringValueWithCheck(row.getCell(12)));
                inscriptionDTO.setAusiasChild2Name(getStringValueWithCheck(row.getCell(14)));
                inscriptionDataByRowIndex.put(i, inscriptionDTO);
            }

        }

        return inscriptionDataByRowIndex;
    }

    protected List<String> extractPaymentsData(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException(String.format("File %s does not exist!", file.getAbsolutePath()));
        }

        List<String> paymentDescription = new ArrayList<>();
        try (Workbook wb = new HSSFWorkbook(Files.newInputStream(file.toPath()))) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                double numericCellValue = row.getCell(4).getNumericCellValue();
                if (numericCellValue == 15.00D) {
                    String stringCellValue = row.getCell(3).getStringCellValue();
                    paymentDescription.add(stringCellValue.contains("-") ? StringUtils
                            .substringAfterLast(stringCellValue, "-") : stringCellValue);
                }
            }
        }
        return paymentDescription;
    }

    public List<Integer> returnPayedRows(Map<Integer, InscriptionDTO> inscriptionData, List<String> paymentData) {
        List<Integer> payedRows = new ArrayList<>();
        paymentData.forEach(payment -> {
            for (Map.Entry<Integer, InscriptionDTO> entry : inscriptionData.entrySet()) {
                if (emailMatch(payment, entry.getValue()) || normalizedStringMatchPayment(payment, entry.getValue().getParent1Name())
                    || normalizedStringMatchPayment(payment, entry.getValue().getParent2Name())
                    || normalizedStringMatchPayment(payment, entry.getValue().getChild1Name())
                    || normalizedStringMatchPayment(payment, entry.getValue().getChild2Name())
                    || normalizedStringMatchPayment(payment, entry.getValue().getAusiasChild1Name())
                    || normalizedStringMatchPayment(payment, entry.getValue().getAusiasChild2Name())) {
                        payedRows.add(entry.getKey());
                        break;
                }
            }
        });
        return payedRows;
    }

    protected boolean normalizedStringMatchPayment(final String paymentConcept, final String inscriptionName) {
        return inscriptionName != null
                && Normalizer.normalize(inscriptionName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").toUpperCase().contains(paymentConcept)
                && paymentConcept.split(" ").length >= inscriptionName.split(" ").length - 1
                && inscriptionName.split(" ").length >= 2 && paymentConcept.split(" ").length >=2;
    }

    private boolean emailMatch(String paymentConcept, InscriptionDTO inscriptionDTO) {
        return paymentConcept.replace("ARROBA", "@").replace("ARROVA", "@")
                .equals(StringUtils.upperCase(inscriptionDTO.getEmail()));
    }

    public Map<Integer, String> returnRowsWithDoubts(Map<Integer, InscriptionDTO> inscriptionData, List<String> paymentData) {
        Map<Integer, String> rowsWithDoubts =  new HashMap<>();
        for (Map.Entry<Integer, InscriptionDTO> inscription : inscriptionData.entrySet()) {
            final Integer key = inscription.getKey();
            final InscriptionDTO value = inscription.getValue();
            boolean isRepeated = false;
            for (Map.Entry<Integer, InscriptionDTO> entry : inscriptionData.entrySet()) {

                if (areValuesRepeated(key, value.getEmail(), entry.getKey(), entry.getValue().getEmail(), null)) {
                    rowsWithDoubts.put(key, String.format("El email de inscripción '%s' está repetido",
                            value.getEmail()));
                    isRepeated = true;
                    break;
                }

                if (areValuesRepeated(key, value.getParent1Name(), entry.getKey(), entry.getValue().getParent1Name(),
                        entry.getValue().getParent2Name())) {
                    rowsWithDoubts.put(key, String.format("El nombre del padre/madre 1 '%s' está repetido",
                            value.getParent1Name()));
                    isRepeated = true;
                    break;
                }

                if (areValuesRepeated(key, value.getParent2Name(), entry.getKey(), entry.getValue().getParent2Name(),
                        entry.getValue().getParent1Name())) {
                    rowsWithDoubts.put(key, String.format("El nombre del padre/madre 2 '%s' está repetido",
                            value.getParent2Name()));
                    isRepeated = true;
                    break;
                }

                if (areValuesRepeated(key, value.getChild1Name(), entry.getKey(), entry.getValue().getChild1Name(),
                        entry.getValue().getChild2Name())) {
                    rowsWithDoubts.put(key, String.format("El nombre del/la niño/a 1 '%s' está repetido",
                            value.getChild1Name()));
                    isRepeated = true;
                    break;
                }

                if (areValuesRepeated(key, value.getChild2Name(), entry.getKey(), entry.getValue().getChild2Name(),
                        entry.getValue().getChild1Name())) {
                    rowsWithDoubts.put(key, String.format("El nombre del/la niño/a 2 '%s' está repetido",
                            value.getChild2Name()));
                    isRepeated = true;
                    break;
                }

                if (areValuesRepeated(key, value.getAusiasChild1Name(), entry.getKey(), entry.getValue().getAusiasChild1Name(),
                        entry.getValue().getAusiasChild2Name())) {
                    rowsWithDoubts.put(key, String.format("El nombre del/la niño/a de Ausiás 1 '%s' está repetido",
                            value.getAusiasChild1Name()));
                    isRepeated = true;
                    break;
                }

                if (areValuesRepeated(key, value.getAusiasChild2Name(), entry.getKey(), entry.getValue().getAusiasChild2Name(),
                        entry.getValue().getAusiasChild1Name())) {
                    rowsWithDoubts.put(key, String.format("El nombre del/la niño/a de Ausiás 2 '%s' está repetido",
                            value.getAusiasChild2Name()));
                    isRepeated = true;
                    break;
                }

            }

            if (isRepeated) {
                continue;
            }
            checkDoubtInEmail(paymentData, rowsWithDoubts, key, value);
        }
        return rowsWithDoubts;
    }

    private void checkDoubtInEmail(List<String> paymentData, Map<Integer, String> rowsWithDoubts, Integer key, InscriptionDTO value) {
        for (String concept : paymentData) {
            String emailSeparator = "";
            if (concept.contains("@")) {
                emailSeparator = "@";
            }
            if (concept.contains("ARROBA")) {
                emailSeparator = "ARROBA";
            }
            if (concept.contains("ARROVA")) {
                emailSeparator = "ARROVA";
            }
            final String emailWithoutDominion = StringUtils.substringBeforeLast(concept, emailSeparator);
            final String inscriptionEmail = StringUtils.upperCase(value.getEmail());
            if (StringUtils.isNotBlank(emailWithoutDominion) &&
                    StringUtils.substringBefore(inscriptionEmail, "@").equals(emailWithoutDominion)
                    && !inscriptionEmail.equals(concept.replace(emailSeparator, "@"))) {
                rowsWithDoubts.put(key, String.format("No hay coincidencia exacta en el email: el de " +
                        "inscripción es '%s' y el del pago es '%s'", value.getEmail(),
                        concept.replace(emailSeparator, "@").toLowerCase()));
                break;
            }
        }
    }

    public File validateAndCreateValidatedFile() throws IOException {
        final File resultFile = new File(this.getSourcesFilesFolderPath() + File.separator + "result-file.xlsx");
        if (resultFile.exists()) {
            resultFile.delete();
        }
        Files.copy(this.getInscriptionFile().toPath(), resultFile.toPath());
        final File paymentsFile = this.getPaymentsFile();
        final Map<Integer, InscriptionDTO> inscriptionData = this.extractInscriptionsData(resultFile);
        final List<String> paymentsData = this.extractPaymentsData(paymentsFile);
        final Map<Integer, String> rowsWithDoubts = this.returnRowsWithDoubts(inscriptionData, paymentsData);
        final List<Integer> payedRows = this.returnPayedRows(inscriptionData, paymentsData).stream()
                .filter(payedRow -> !rowsWithDoubts.containsKey(payedRow)).collect(Collectors.toList());
        try (Workbook wb = WorkbookFactory.create(resultFile)) {
            final Sheet sheet = wb.getSheetAt(0);
            final short paymentInfoCellNumber = sheet.getRow(0).getLastCellNum();
            final Cell payedHeadCell = sheet.getRow(0).createCell(paymentInfoCellNumber);
            payedHeadCell.setCellValue("¿Pagado?");
            CellStyle currentCellStyle = payedHeadCell.getCellStyle();
            CellStyle greenStyle = createCellStyle(currentCellStyle, wb, IndexedColors.LIGHT_GREEN.getIndex());
            CellStyle redStyle = createCellStyle(currentCellStyle, wb, IndexedColors.RED1.getIndex());
            // This actually shows as orange
            CellStyle blueStyle = createCellStyle(currentCellStyle, wb, IndexedColors.PALE_BLUE.getIndex());

            iterateAndValidateEachRow(rowsWithDoubts, payedRows, sheet, paymentInfoCellNumber, greenStyle, redStyle, blueStyle);

            // Dummy path to avoid bug: https://stackoverflow.com/a/52389913
            final String dummyPath = resultFile + ".new";
            try (FileOutputStream fileOut = new FileOutputStream(dummyPath)) {
                wb.write(fileOut);
            }
            Files.delete(resultFile.toPath());
            Files.move(Paths.get(dummyPath), resultFile.toPath());
        }
        System.out.println("Inscripciones validadas!");
        return resultFile;
    }

    private void iterateAndValidateEachRow(Map<Integer, String> rowsWithDoubts, List<Integer> payedRows, Sheet sheet, short paymentInfoCellNumber, CellStyle greenStyle, CellStyle redStyle, CellStyle blueStyle) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            final Row row = sheet.getRow(i);
            final Cell cell = row.createCell(paymentInfoCellNumber);
            if (payedRows.contains(row.getRowNum())) {
                cell.setCellValue(Payed.SÍ.name());
                row.setRowStyle(greenStyle);
                modifyRowStyleCellByCell(greenStyle, row);
            } else if (rowsWithDoubts.containsKey(row.getRowNum())) {
                cell.setCellValue(Payed.DUDA.name());
                row.setRowStyle(blueStyle);
                modifyRowStyleCellByCell(blueStyle, row);
            } else {
                cell.setCellValue(Payed.NO.name());
                row.setRowStyle(redStyle);
                modifyRowStyleCellByCell(redStyle, row);
            }
        }
    }

    private void modifyRowStyleCellByCell(CellStyle newStyle, Row row) {
        for(Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();) {
            Cell nextCell = cellIterator.next();
            if (nextCell.getColumnIndex() == 0) {
                continue;
            }
            nextCell.setCellStyle(newStyle);
        }
    }

    private CellStyle createCellStyle(CellStyle currentCellStyle, Workbook wb, short colorIndex) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.cloneStyleFrom(currentCellStyle);
        cellStyle.setFillForegroundColor(colorIndex);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    protected File getInscriptionFile() throws IOException {
        File dir = new File(this.getSourcesFilesFolderPath());
        return Arrays.stream(Objects.requireNonNull(dir.listFiles((dir1, name) -> name.endsWith(".xlsx"))))
                .findFirst().orElseThrow(() -> new IOException("No file found with extension '.xlsx' to check inscriptions"));
    }

    protected File getPaymentsFile() throws IOException {
        File dir = new File(this.getSourcesFilesFolderPath());
        return Arrays.stream(Objects.requireNonNull(dir.listFiles((dir1, name) -> name.endsWith(".xls"))))
                .findFirst().orElseThrow(() -> new IOException("No file found with extension '.xls' to check payments"));
    }

    protected String getSourcesFilesFolderPath() {
        return ".";
    }

    private String getStringValueWithCheck(final Cell cell) {
        if (cell != null && CellType.STRING.equals(cell.getCellType())) {
            return cell.getStringCellValue();
        }
        return null;
    }

    private boolean areValuesRepeated(final Integer currentKey, final String currentValue, final Integer keyToCheck,
                                          final String valueToCheck, final String otherValueToCheck) {
        return currentValue != null && !currentKey.equals(keyToCheck)
                && (currentValue.equals(valueToCheck) || currentValue.equals(otherValueToCheck));
    }
}
