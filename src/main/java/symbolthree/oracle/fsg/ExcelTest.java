package symbolthree.oracle.fsg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import symbolthree.oracle.fsg.datamodel.Style;

public class ExcelTest {

	public ExcelTest() {
	}
	
	public static void main(String[] args) {
		ExcelTest t = new ExcelTest();
		try {
		//t.test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void test() throws InvalidFormatException, IOException {
		File file = new File("C:\\WORK\\myFSG\\test.xlsx");
		File file2 = new File("C:\\WORK\\myFSG\\test2.xlsx");
		FileOutputStream fos = new FileOutputStream(file2);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(18);
        XSSFCell cell = row.getCell(1);
        System.out.println(cell.getCellStyle().getAlignment());
        
        XSSFRow row2 = sheet.getRow(19);
        XSSFCell cell2 = row2.getCell(1);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.FILL);
        cell2.setCellType(CellType.STRING);
        cell2.setCellValue("-");
        cell2.setCellStyle(style);
        workbook.write(fos);
        workbook.close();
        fos.close();
	}
}
