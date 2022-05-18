package org.ampainscripciones.file;

import org.ampainscripciones.model.InscriptionDTO;
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


    protected Map<Integer, InscriptionDTO> extractInscriptionsData(File file) throws IOException, InvalidFormatException {

        if (!file.exists()) {
            throw new IOException(String.format("File %s does not exist!", file.getAbsolutePath()));
        }

        Map<Integer, InscriptionDTO> sheetValuesByRowIndex = new HashMap<>();
        OPCPackage pkg = OPCPackage.open(file);
        try (XSSFWorkbook wb = new XSSFWorkbook(pkg)) {

            Sheet sheetAt = wb.getSheetAt(0);
            for (Iterator<Row> rowIterator = sheetAt.rowIterator(); rowIterator.hasNext();) {

                List<String> rowValues = new ArrayList<>();
                Row row = rowIterator.next();
                InscriptionDTO inscriptionDTO = new InscriptionDTO();
                inscriptionDTO.setTimestamp(LocalDateTime.from(DateTimeFormatter.ofPattern("M/d/y hh:mm:ss")
                        .parse(row.getCell(0).getStringCellValue())));
                inscriptionDTO.setEmail(row.getCell(1).getStringCellValue());
                inscriptionDTO.setParent1Name(row.getCell(2).getStringCellValue());
                inscriptionDTO.setParent1PhoneNumber(row.getCell(3).getNumericCellValue());
                inscriptionDTO.setParent2Name(row.getCell(4).getStringCellValue());
                inscriptionDTO.setParent2PhoneNumber(row.getCell(5).getNumericCellValue());
                inscriptionDTO.setAusiasChildrenNumber(row.getCell(6).getNumericCellValue());
                inscriptionDTO.setAusiasChild1Name(row.getCell(7).getStringCellValue());
                inscriptionDTO.setAusiasChild1Course(row.getCell(8).getStringCellValue());
                inscriptionDTO.setAusiasChild2Name(row.getCell(9).getStringCellValue());
                inscriptionDTO.setAusiasChild2Course(row.getCell(10).getStringCellValue());
                inscriptionDTO.setLluisChildrenNumber(row.getCell(11).getNumericCellValue());
                inscriptionDTO.setLluisChild1Name(row.getCell(12).getStringCellValue());
                inscriptionDTO.setLluisChild1Course(row.getCell(13).getStringCellValue());
                inscriptionDTO.setLluisChild2Name(row.getCell(14).getStringCellValue());
                inscriptionDTO.setLluisChild2Course(row.getCell(15).getStringCellValue());
                inscriptionDTO.setPaymentFileUrl(row.getCell(16).getStringCellValue());
                inscriptionDTO.setProtectionDataPolicy(row.getCell(17).getStringCellValue());
                inscriptionDTO.setChildrenImageAuthorization(row.getCell(18).getStringCellValue());
                sheetValuesByRowIndex.put(row.getRowNum(), inscriptionDTO);
            }

        }

        return sheetValuesByRowIndex;
    }

}
