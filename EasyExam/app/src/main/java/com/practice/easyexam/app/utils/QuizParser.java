package com.practice.easyexam.app.utils;

import android.util.Log;

import com.practice.easyexam.app.model.Question;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class QuizParser {
    public static List<Question> parseQuiz(String filePath) {
        List<Question> questions = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            Row firstRow = rowIterator.next();
            int numColumns = getNumberOfNonEmptyCells(firstRow);
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }
//                Row firstRow = rowIterator.next();

                Log.d("numColumns",numColumns+"");
                String questionText = getCellValueAsString(row.getCell(0));
                ArrayList<String> options = new ArrayList<>();

                // Handle 5 columns for options
                for (int i = 1; i < numColumns - 2; i++) {
                    Cell cell = row.getCell(i);
                    String cellValue = getCellValueAsString(cell);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        options.add(cellValue);
                    }
                }

                int correctNum = getCellValueAsInt(row.getCell(numColumns -2)); // Assuming correctNum is numeric
                String image = getCellValueAsString(row.getCell(numColumns -1));
                Question question = new Question(questionText, options, correctNum - 1);
                question.setImage(image);

                questions.add(question);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        return questions;
    }
    private static String getCellValueAsString(Cell cell) {
        return (cell != null) ? cell.toString() : null;
    }

    private static int getNumberOfNonEmptyCells(Row row) {
        return (int) IntStream.range(0, row.getPhysicalNumberOfCells())
                .mapToObj(row::getCell)
                .filter(cell -> cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                .count();
    }

    private static int getCellValueAsInt(Cell cell) {
        return (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) ? (int) cell.getNumericCellValue() : 0;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }
}
