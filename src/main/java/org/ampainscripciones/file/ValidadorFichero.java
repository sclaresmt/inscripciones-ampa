package org.ampainscripciones.file;

import org.ampainscripciones.model.InscriptionDTO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ValidadorFichero {

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
        inscriptionData.entrySet().forEach(entry -> {
            for (Map.Entry<Integer, String> recursiveEntry: inscriptionData.entrySet()) {
                if (recursiveEntry.getValue().equals(entry.getValue()) && !recursiveEntry.getKey().equals(entry.getKey())) {
                    rowsWithDoubts.put(entry.getKey(), String.format("El email de inscripción '%s' está repetido", entry.getValue()));
                    break;
                }
            }
        });
        return rowsWithDoubts;
    }

}
