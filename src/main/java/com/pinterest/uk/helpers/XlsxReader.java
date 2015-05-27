package com.pinterest.uk.helpers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static org.testng.Assert.assertTrue;


public class XlsxReader {


    public static ArrayList<ArrayList<String>> getFileData(String pathToFile) {
        return getFileData(new File(pathToFile));
    }

    public static ArrayList<ArrayList<String>> getFileData(File file) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        XSSFSheet mySheet = getSheetFromFile(file, 0);

        for (Row row : mySheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            ArrayList<String> cells = new ArrayList<>();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        cells.add(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        cells.add(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        cells.add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    default:
                }
            }
            if (cells.size() > 0) {
                rows.add(cells);
            }
        }


        return rows;
    }

    public static ArrayList<ArrayList<String>> getFileDataAsText(File file) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        XSSFSheet mySheet = getSheetFromFile(file, 0);
        for (Row row : mySheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            ArrayList<String> cells = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cells.add(cell.getStringCellValue());
            }
            if (cells.size() > 0) {
                rows.add(cells);
            }
        }
        return rows;
    }

    public static ArrayList<String> getRowByFirstCell(ArrayList<ArrayList<String>> rows, String firstCellText) {
        for (ArrayList<String> row : rows) {
            if (row.get(0).equals(firstCellText)) {
                return row;
            }
        }
        assertTrue(false, "Row with first cell = <" + firstCellText + "> was not found");
        return null;
    }


    private static XSSFSheet getSheetFromFile(File file, int sheetNumber) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
            return myWorkBook.getSheetAt(sheetNumber);
        } catch (FileNotFoundException e) {
            assertTrue(false, "File " + file.getPath() + " was not found");
            return null;
        } catch (IOException a) {
            a.printStackTrace();
            assertTrue(false, "IOException while parsing xlsx file with next message" + a.getMessage());
            return null;
        }
    }
}
