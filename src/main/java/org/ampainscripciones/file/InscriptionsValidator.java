package org.ampainscripciones.file;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        return null;
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

}
