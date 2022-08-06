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
******************************************************************************/

package symbolthree.oracle.fsg.datamodel;

import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Style {

	public enum TEMPLATE_STYLE {NORMAL, EXPAND};
	public enum DATA_STYLE {STYLE, NUMERIC, PRECENTAGE, NO_MEANING, DEFAULT_UNDERLINE_STYLE, DEFAULT_UNDERLINE_TYPE};
	public enum UNDERLINE_STYLE {BLANK, FILL, BORDER};	
	public enum HEADER_STYLE {STYLE, UNDERLINE};
	public enum COL_HEADER_STYLE {STYLE, UNDERLINE};
	public enum ROW_HEADER_STYLE {STYLE, UNDERLINE};
	public enum HEADER_UNDERLINE_STYLE_TYPE {HEADER, ROW_HEADER, COLUMN_HEADER};
	
	private Style.TEMPLATE_STYLE                             templateStyle;
	private Hashtable<Style.DATA_STYLE, XSSFCellStyle>       dataStyles         = new Hashtable<DATA_STYLE, XSSFCellStyle>();
	private Hashtable<Style.HEADER_STYLE, XSSFCellStyle>     headerStyles       = new Hashtable<HEADER_STYLE, XSSFCellStyle>();
	private Hashtable<Style.COL_HEADER_STYLE, XSSFCellStyle> colHeaderStyles    = new Hashtable<COL_HEADER_STYLE, XSSFCellStyle>();
	private Hashtable<HEADER_UNDERLINE_STYLE_TYPE, String>   headerUnderlineStyleType = new Hashtable<HEADER_UNDERLINE_STYLE_TYPE, String>(); 
	private Hashtable<Style.ROW_HEADER_STYLE, XSSFCellStyle> rowHeaderStyles     = new Hashtable<ROW_HEADER_STYLE, XSSFCellStyle>();
	private Hashtable<String, String>                        underlineStyleTypes = new Hashtable<String, String>(); 
	private Hashtable<String, XSSFCellStyle>                 underlineStyles     = new Hashtable<String, XSSFCellStyle>();

	private XSSFWorkbook workbook = null; 

    static final Logger logger = LogManager.getLogger(Style.class.getName());
    
	public Style(XSSFWorkbook _xlsx) {
		workbook = _xlsx;
	}
	
	public void loadStyle() {
		
		XSSFRow row   = null;
		XSSFCell cell = null;
		XSSFCellStyle style = null;
		
		XSSFSheet sheet = workbook.getSheet("STYLE");
		// row 1 -- version  --> skip
		// row 2 -- template style
		row = sheet.getRow(1);
		cell = row.getCell(1);
		String _templateStyle = cell.getStringCellValue();
		if (_templateStyle.equals(Style.TEMPLATE_STYLE.NORMAL.toString())) {
			this.templateStyle = Style.TEMPLATE_STYLE.NORMAL;
		}
		if (_templateStyle.equals(Style.TEMPLATE_STYLE.EXPAND.toString())) {
			this.templateStyle = Style.TEMPLATE_STYLE.EXPAND;
		} 
		
		
		// row 5 -- default styles for all types
		row = sheet.getRow(4);
		cell = row.getCell(1);
		style = cell.getCellStyle();
		dataStyles.put(DATA_STYLE.STYLE, style);
		
		cell = row.getCell(2);
		style = cell.getCellStyle();
		headerStyles.put(HEADER_STYLE.STYLE, style);
		
		cell = row.getCell(3);
		style = cell.getCellStyle();
		colHeaderStyles.put(COL_HEADER_STYLE.STYLE, style);		

		cell = row.getCell(4);
		style = cell.getCellStyle();
		rowHeaderStyles.put(ROW_HEADER_STYLE.STYLE, style);
	
		// row 6 - numeric 
		row = sheet.getRow(5);
		cell = row.getCell(1);
		style = cell.getCellStyle();
		dataStyles.put(DATA_STYLE.NUMERIC, style);

		// row 7 - percentage
		row = sheet.getRow(6);
		cell = row.getCell(1);
		style = cell.getCellStyle();
		dataStyles.put(DATA_STYLE.PRECENTAGE, style);

		// row 8 - no meaning
		row = sheet.getRow(7);
		cell = row.getCell(1);
		style = cell.getCellStyle();
		dataStyles.put(DATA_STYLE.NO_MEANING, style);

		// row 11 - default underline
		row = sheet.getRow(10);
		cell = row.getCell(1);
		style = cell.getCellStyle();
		underlineStyleTypes.put("DEFAULT", cell.getStringCellValue());
		underlineStyles.put("DEFAULT", style);
		
		cell = row.getCell(2);
		style = cell.getCellStyle();
		headerUnderlineStyleType.put(HEADER_UNDERLINE_STYLE_TYPE.HEADER, cell.getStringCellValue());
		headerStyles.put(HEADER_STYLE.UNDERLINE, style);

		cell = row.getCell(3);
		style = cell.getCellStyle();
		headerUnderlineStyleType.put(HEADER_UNDERLINE_STYLE_TYPE.COLUMN_HEADER, cell.getStringCellValue());
		colHeaderStyles.put(COL_HEADER_STYLE.UNDERLINE, style);

		cell = row.getCell(4);
		style = cell.getCellStyle();
		headerUnderlineStyleType.put(HEADER_UNDERLINE_STYLE_TYPE.ROW_HEADER, cell.getStringCellValue());
		rowHeaderStyles.put(ROW_HEADER_STYLE.UNDERLINE, style);
		
		// custom underline styles
		int rowPos = 12;
		boolean isEnd = false;
		while (!isEnd) {
			try {
			row = sheet.getRow(rowPos);
			cell = row.getCell(0);
			if (cell != null &&  
				cell.getStringCellValue() != null && 
				! cell.getStringCellValue().equals("")) {
				String underlineChar = cell.getStringCellValue();
				cell = row.getCell(1);
				style = cell.getCellStyle();
				underlineStyleTypes.put(underlineChar, cell.getStringCellValue());
				underlineStyles.put(underlineChar, style);
				logger.debug("add custom underline style for " + underlineChar);
			} else {
				isEnd = true;
			}
			rowPos = rowPos + 2;
			} catch (NullPointerException npe) {
				isEnd = true;
			}
		}
	}
	
	public XSSFCellStyle getDataStyle(Style.DATA_STYLE _style) {
		return dataStyles.get(_style);
	}
	
	public XSSFCellStyle getHeaderStyle(Style.HEADER_STYLE _style) {
		return headerStyles.get(_style);
	}
	
	public XSSFCellStyle getColHeaderStyle(Style.COL_HEADER_STYLE _style) {
		return colHeaderStyles.get(_style);
	}
	
	public XSSFCellStyle getRowHeaderStyle(Style.ROW_HEADER_STYLE _style) {
		return rowHeaderStyles.get(_style);
	}
	
	public XSSFCellStyle getUnderlineStyle(String underlineChar) {
		XSSFCellStyle style = underlineStyles.get(underlineChar);
		if (style==null) {
			style = underlineStyles.get("DEFAULT");
		}
		return style;
	}

	public String getHeaderUnderlineStyleType(HEADER_UNDERLINE_STYLE_TYPE _type) {
		return headerUnderlineStyleType.get(_type);
	}
	
	public String getUnderlineStyleType(String underlineChar) {
		String styleType = underlineStyleTypes.get(underlineChar);
		if (styleType==null) {
			styleType = underlineStyleTypes.get("DEFAULT");
		}
		return styleType;
	}

	public Style.TEMPLATE_STYLE getTemplateStyle() {
		return templateStyle;
	}

	public void setTemplateStyle(Style.TEMPLATE_STYLE templateStyle) {
		this.templateStyle = templateStyle;
	}
	
}
