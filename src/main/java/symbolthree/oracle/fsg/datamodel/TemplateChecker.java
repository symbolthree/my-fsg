/******************************************************************************
 *
 * ≡≡ myFSG ≡≡
 * Copyright (C) 2016 Christopher Ho
 * All Rights Reserved, symbolthree.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: christopher.ho@symbolthree.com
 *
 * ================================================
 *
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/datamodel/TemplateChecker.java $
 * $Author: Christopher Ho $
 * $Date: 3/29/17 6:46a $
 * $Revision: 1 $
******************************************************************************/

package symbolthree.oracle.fsg.datamodel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TemplateChecker {

	private File templateFile = null;
	private String message = null;
	private static int TEMPLATE_MAJOR_VERSION = 1;
	private static int TEMPLATE_MINOR_VERSION = 0;
	
    static final Logger logger = LogManager.getLogger(TemplateChecker.class.getName());	
    public static final String RCS_ID = "$Header: /TOOL/myMSG/src/symbolthree/oracle/fsg/datamodel/TemplateChecker.java 1     3/29/17 6:46a Christopher Ho $";
    
	public TemplateChecker(String _file) {
		templateFile = new File(_file);
	}
	
	public boolean check() {
		if (!templateFile.exists()) {
			message = templateFile.getName()  + " does not exists";
			return false;
		}
		
		if (!templateFile.getName().endsWith("xlsx") &&
			! templateFile.getName().endsWith("XLSX")) {
			message = templateFile.getName()  + "is not an Excel file";
			return false;
		}
		
		XSSFWorkbook workbook = null;
		boolean isValid = true;
		try {
		  workbook = new XSSFWorkbook(new FileInputStream(templateFile));
		
		if (workbook.getSheetIndex("STYLE") < 0) {
			message = templateFile.getName() + "does not have STYLE worksheet";
			isValid = false;
		}
		if (workbook.getSheetIndex("TEMPLATE") < 0) {
			message = templateFile.getName() + " does not have TEMPLATE worksheet";
			isValid = false;			
		}
		
		XSSFSheet sheet = workbook.getSheet("STYLE");
		XSSFCell cell = sheet.getRow(0).getCell(1);
		String version = cell.getStringCellValue();
		
		logger.info("Template version = " + version);
		
		try {
			
			int majorVer = Integer.parseInt(version.split("\\.")[0]);
			int minorVer = Integer.parseInt(version.split("\\.")[1]);
			
			logger.info("Major " + majorVer + " / Minor " + minorVer);
			
			if (majorVer < TEMPLATE_MAJOR_VERSION || minorVer < TEMPLATE_MINOR_VERSION) {
				message = "Invalid template version";
				isValid = false;
			}
			
		} catch (Exception e) {
			message = "Unknown template version";
			isValid = false;
		}

		if (! isValid) {
			workbook.close();
		}
		
		} catch (Exception e) {
			message = e.getMessage();
			isValid = false;
		}
		
		logger.info("Message = " + message);
		if (!isValid) {
			return false;
		}
		
		return true;
	}
	
	public String getMessage() {
		return message;
	}
}
