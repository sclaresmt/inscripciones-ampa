package org.ampainscripciones.file;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class InscriptionsValidator {

    protected Map<Integer, String> extractEmailData(File file) throws IOException {
        Map<Integer, String> emailValuesByRowIndex = new HashMap<>();
        try (Workbook wb = WorkbookFactory.create(file)) {

            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                emailValuesByRowIndex.put(i, sheet.getRow(i).getCell(1).getStringCellValue());
            }

        }

        return emailValuesByRowIndex;
    }

    protected List<String> extractPaymentsData(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException(String.format("File %s does not exist!", file.getAbsolutePath()));
        }

        List<String> paymentDescription = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(file)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                double numericCellValue = row.getCell(4).getNumericCellValue();
                if (numericCellValue == 15.00D) {
                    paymentDescription.add(row.getCell(3).getStringCellValue());
                }
            }
        }
        return paymentDescription;
    }

    public List<Integer> returnPayedRows(Map<Integer, String> inscriptionData, List<String> paymentData) {
        List<Integer> payedRows = new ArrayList<>();
        inscriptionData.entrySet().forEach(entry -> {
            if (paymentData.stream().anyMatch(data -> data.contains(entry.getValue()))) {
                payedRows.add(entry.getKey());
            }
        });
        return payedRows;
    }

    public Map<Integer, String> returnRowsWithDoubts(Map<Integer, String> inscriptionData, List<String> paymentData) {
        Map<Integer, String> rowsWithDoubts =  new HashMap<>();
        inscriptionData.forEach((key, value) -> {
            boolean isRepeated = false;
            for (Map.Entry<Integer, String> entry : inscriptionData.entrySet()) {
                if (value.equals(entry.getValue()) && !key.equals(entry.getKey())) {
                    rowsWithDoubts.put(entry.getKey(), String.format("El email de inscripción '%s' está repetido",
                            entry.getValue()));
                    isRepeated = true;
                    break;
                }
            }
            if (!isRepeated) {
                for (String paymentConcept : paymentData) {
                    String conceptEmail = paymentConcept;
                    if (paymentConcept.contains("-")) {
                        conceptEmail =StringUtils.substringAfterLast(paymentConcept, "-");
                    }
                    String emailWithoutDominion = conceptEmail;
                    if (conceptEmail.contains("@")) {
                        emailWithoutDominion = StringUtils.substringBeforeLast(conceptEmail, "@");
                    }
                    if (StringUtils.isNotBlank(emailWithoutDominion) && StringUtils.substringBefore(value, "@")
                            .equals(emailWithoutDominion) && !value.equals(conceptEmail)) {
                        rowsWithDoubts.put(key, String.format("No hay coincidencia exacta en el email: el de " +
                                "inscripción es '%s' y el del pago es '%s'", value, conceptEmail));
                        break;
                    }
                }
            }
        });
        return rowsWithDoubts;
    }

    public File validateAndCreateValidatedFile() throws IOException {
        final File resultFile = new File(this.getResultFilePath() + "result-file.xlsx");
        if (resultFile.exists()) {
            resultFile.delete();
        }
        Files.copy(this.getInscriptionFile().toPath(), resultFile.toPath());
        final File paymentsFile = this.getPaymentsFile();
        final Map<Integer, String> inscriptionData = this.extractEmailData(resultFile);
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

            // Dummy path to avoid bug: https://stackoverflow.com/a/52389913
            final String dummyPath = resultFile + ".new";
            try (FileOutputStream fileOut = new FileOutputStream(dummyPath)) {
                wb.write(fileOut);
            }
            Files.delete(resultFile.toPath());
            Files.move(Paths.get(dummyPath), resultFile.toPath());
        }
        return resultFile;
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

    protected String getResultFilePath() {
        return "";
    }

    protected String getSourcesFilesFolderPath() {
        return "";
    }
}
