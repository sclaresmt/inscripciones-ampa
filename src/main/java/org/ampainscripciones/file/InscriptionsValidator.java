package org.ampainscripciones.file;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class InscriptionsValidator {

    protected Map<Integer, String> extractEmailData(File file) throws IOException {

        if (!file.exists()) {
            throw new IOException(String.format("File %s does not exist!", file.getAbsolutePath()));
        }

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
                .dropWhile(rowsWithDoubts::containsKey).toList();

        try (Workbook wb = WorkbookFactory.create(resultFile)) {
            final Sheet sheet = wb.getSheetAt(0);
            final short paymentInfoCellNumber = sheet.getRow(0).getLastCellNum();
            final Cell payedHeadCell = sheet.getRow(0).createCell(paymentInfoCellNumber);
            payedHeadCell.setCellValue("¿Pagado?");
            CellStyle greenStyle = createCellStyle(wb, IndexedColors.GREEN.getIndex());
            CellStyle redStyle = createCellStyle(wb, IndexedColors.RED.getIndex());
            CellStyle blueStyle = createCellStyle(wb, IndexedColors.BLUE.getIndex());
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                final Cell cell = row.createCell(paymentInfoCellNumber);
                if (payedRows.contains(row.getRowNum())) {
                    cell.setCellValue(Payed.SÍ.name());
                    cell.setCellStyle(greenStyle);
                } else if (rowsWithDoubts.containsKey(row.getRowNum())) {
                    cell.setCellValue(Payed.DUDA.name());
                    cell.setCellStyle(blueStyle);
                } else {
                    cell.setCellValue(Payed.NO.name());
                    cell.setCellStyle(redStyle);
                }
            }

//            final SheetConditionalFormatting sheetConditionalFormatting = sheet.getSheetConditionalFormatting();
////            sheetConditionalFormatting.createConditionalFormattingRule("=$T2=\"" + Payed.SÍ + "\"")
////                    .createPatternFormatting().setFillBackgroundColor(IndexedColors.GREEN.getIndex());
////            sheetConditionalFormatting.createConditionalFormattingRule("=$T2=\"" + Payed.NO + "\"")
////                    .createPatternFormatting().setFillBackgroundColor(IndexedColors.RED.getIndex());
////            sheetConditionalFormatting.createConditionalFormattingRule("=$T2=\"" + Payed.DUDA + "\"")
////                    .createPatternFormatting().setFillBackgroundColor(IndexedColors.BLUE.getIndex());
//            CellRangeAddress[] ranges = new CellRangeAddress[]{new CellRangeAddress(1, sheet.getLastRowNum(),
//                    0, paymentInfoCellNumber)};
//            sheetConditionalFormatting.addConditionalFormatting(ranges, createFormattingRuleForFormula("=$T2=\""
//                    + Payed.SÍ + "\"", IndexedColors.GREEN.getIndex(), sheetConditionalFormatting));
//            sheetConditionalFormatting.addConditionalFormatting(ranges, createFormattingRuleForFormula("=$T2=\""
//                    + Payed.NO + "\"", IndexedColors.RED.getIndex(), sheetConditionalFormatting));
//            sheetConditionalFormatting.addConditionalFormatting(ranges, createFormattingRuleForFormula("=$T2=\""
//                    + Payed.DUDA + "\"", IndexedColors.BLUE.getIndex(), sheetConditionalFormatting));

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

    private CellStyle createCellStyle(Workbook wb, short colorIndex) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(colorIndex);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private ConditionalFormattingRule createFormattingRuleForFormula(final String formula, final short colourIndex, SheetConditionalFormatting sheetConditionalFormatting) {
        ConditionalFormattingRule rule = sheetConditionalFormatting.createConditionalFormattingRule(formula);
        PatternFormatting patternFormatting = rule.createPatternFormatting();

//        patternFormatting.setFillBackgroundColor(colourIndex);
//        patternFormatting.setFillPattern(FillPatternType.SOLID_FOREGROUND.getCode());

        patternFormatting.setFillBackgroundColor(colourIndex);
        patternFormatting.setFillPattern(FillPatternType.BRICKS.getCode());

//        patternFormatting.setFillForegroundColor(colourIndex);
//        patternFormatting.setFillPattern(FillPatternType.SOLID_FOREGROUND.getCode());

//        patternFormatting.setFillBackgroundColor(IndexedColors.BLACK.index);
//        patternFormatting.setFillPattern(FillPatternType.BIG_SPOTS.getCode());
//        patternFormatting.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());

        return rule;
    }

    protected File getInscriptionFile() throws IOException {
        return new File("./inscripciones");
    }

    protected File getPaymentsFile() {
        return new File("./pagos");
    }

    protected String getResultFilePath() {
        return "./";
    }
}
