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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import symbolthree.oracle.fsg.Constants;
import symbolthree.oracle.fsg.MyFSGException;
import symbolthree.oracle.fsg.datamodel.Style.DATA_STYLE;

public class FSG2XLSX implements Constants {
    private File             inputXMLFile         = null;
    private File             outputExcelFile      = null;
    private File             templateFile         = null;
    private XSSFWorkbook     workbook             = null;
    private FSGXMLReader     fsgReader            = null;
    private Style            allStyles            = null;
    private int              subReportLocRow      = -1;
    private int              subReportLocCol      = -1;
    private int              reportStartRow       = 0;
    private int              homeStartRow         = 0;
    private int              HEADER_ROW           = 4;
    
    private static final String TEMPLATE_WKS_NAME = "TEMPLATE";
    private static final String HOME_WKS_NAME     = "HOME";
    private static final String START_KEYWORD     = "START";
    
    private static Pattern pattern = Pattern.compile("&\\s*(\\w+)");
    
    static final Logger logger = LogManager.getLogger(FSG2XLSX.class.getName());
    
	public FSG2XLSX(String _xml) {    
        inputXMLFile    = new File(_xml);
        outputExcelFile = new File(inputXMLFile.getParent(), inputXMLFile.getName() + ".xlsx");
        templateFile    = new File (DEFAULT_TEMPLATE);        
	}    
    
	public FSG2XLSX(String _xml, String _excel) {    
        inputXMLFile    = new File(_xml);
        outputExcelFile = new File(_excel);
        templateFile    = new File (DEFAULT_TEMPLATE);        
	}
	
	public FSG2XLSX(String _xml, String _excel, String _template) {
        inputXMLFile    = new File(_xml);
        outputExcelFile = new File(_excel);
        templateFile    = new File (_template);        
	}

	private void checkFiles() throws MyFSGException {
		String errMsg = null;
		
        if (!inputXMLFile.exists()) {
        	errMsg = "Cannot find FSG XML file " + inputXMLFile.getAbsolutePath();
            logger.error(errMsg);
            throw new MyFSGException(errMsg);
        }	
        
        if (!templateFile.exists()) {
        	errMsg = "Cannot find template file " + templateFile.getAbsolutePath();
            logger.error(errMsg);
            throw new MyFSGException(errMsg);
        }
        
        if (outputExcelFile.exists()) { 
        	outputExcelFile.delete();
        }
        
        if (outputExcelFile.exists()) {
         throw new MyFSGException("Unable to delete " + outputExcelFile.getAbsolutePath());
        }
	}
	
	public static void main(String[] args) {
		FSG2XLSX f = null;
        if (args.length == 3) {
        	 f = new FSG2XLSX(args[0], args[1], args[2]);        	
        } else if (args.length == 2) {
        	f = new FSG2XLSX(args[0], args[1]);
        } else if (args.length == 1) {
        	f = new FSG2XLSX(args[0]);
        } else {
            System.out.println("FSG2XLSX [FSG XML file]");
            System.out.println("FSG2XLSX [FSG XML file] [Output Excel file]");
            System.out.println("FSG2XLSX [FSG XML file] [Output Excel file] [Template file]");
            System.exit(1);
        }
        f.doConvert();
	}
	
    public void doConvert() {
    	try {
    	checkFiles();
    	
    	logger.info("Input XML   = " + inputXMLFile.getAbsolutePath());
    	logger.info("Template    = " + templateFile.getAbsolutePath());
    	logger.info("Output XLSX = " + outputExcelFile.getAbsolutePath());

    	// load FSG XML data
    	fsgReader = new FSGXMLReader(inputXMLFile.getAbsolutePath());
    	fsgReader.read();
    	
    	workbook = new XSSFWorkbook(new FileInputStream(templateFile));

    	// load styles
    	allStyles = new Style(workbook);
    	allStyles.loadStyle();

    	XSSFRow row = null;
  	    XSSFCell cell = null;
    	
    	// put data to template sheet 
    	XSSFSheet sheet    = workbook.getSheet(TEMPLATE_WKS_NAME);
    	replaceKeywordInSheet(sheet);
    	logger.debug("Keyword replacement done");
    	
    	// row expansion 
    	if (allStyles.getTemplateStyle() == Style.TEMPLATE_STYLE.EXPAND) {
    		int lastCol = fsgReader.getFsgReport().getColSize();
    		for (int i=0;i<reportStartRow;i++) {
    		  logger.debug("row expansion to column " + lastCol);
    		  CellRangeAddress mergeCells = new CellRangeAddress(i,i,1,lastCol);  // first row, last row, first col, last col
    		  sheet.addMergedRegion(mergeCells);
    		}
    	}
    	
  	    int noOfSheet = fsgReader.getNoOfSheet();
  	    logger.debug("No. of sheet required: " + noOfSheet);
  	    
  	    int sheetNo = 0;
  	    // clone sheets and set subReportName
  	    /*
  	     * TEMPLATE sheet name is changed to SHEET0
  	     * If more than 1 sheet, next sheet name is SHEET1 to SHEETn
  	     */
    	for (sheetNo=0; sheetNo<noOfSheet; sheetNo++) {
    	  String tempSheetName = "SHEET" + sheetNo;	

    	  if (sheetNo == 0) {
    	    workbook.setSheetName(workbook.getSheetIndex(TEMPLATE_WKS_NAME), tempSheetName);
    	  } else {
		   	int templateSheetNo = workbook.getSheetIndex("SHEET0");
 	  	   	sheet = workbook.cloneSheet(templateSheetNo);
 	  	    workbook.setSheetName(workbook.getSheetIndex(sheet.getSheetName()), tempSheetName);
 	  	    logger.debug("clone worksheet to " + tempSheetName);
    	  }
    	  
    	  // set subReportName    	  
   		  if (subReportLocRow > -1 && subReportLocCol > -1) {
  	    	  sheet = workbook.getSheet(tempSheetName);
  	    	  cell = sheet.getRow(subReportLocRow).getCell(subReportLocCol);
  	    	  String name = fsgReader.getFsgReport().getSheetData(sheetNo).getSheetName();
  	    	  cell.setCellValue(getSubReportName(name));
   		  }
  	    }
    	
  	    // fill column info
      	for (sheetNo=0; sheetNo<noOfSheet; sheetNo++) {
  	      logger.info("fill column info for sheet " + sheetNo);
  	      ArrayList<String[]> colLabels = fsgReader.getFsgReport().getColLabel();
  	      sheet = workbook.getSheet("SHEET" + sheetNo);
  	      for (int i=0;i<HEADER_ROW;i++) {
  	        row = sheet.getRow(reportStartRow+i);
  	        if (row==null) row=sheet.createRow(reportStartRow+i);
  	          for (int j=0;j<colLabels.size();j++) {
  	            cell = row.getCell(j);
  	            String val = colLabels.get(j)[i];
  	            if (cell==null) cell=row.createCell(j);
  	              cell.setCellValue(val);
  	            if (j==0) {  // first column
  	              if (isUnderline(val)) {
  	            	String styleType = allStyles.getHeaderUnderlineStyleType(Style.HEADER_UNDERLINE_STYLE_TYPE.HEADER);  
  	            	cell.setCellStyle(allStyles.getHeaderStyle(Style.HEADER_STYLE.UNDERLINE));
  	            	if (styleType.equals("BLANK")) cell.setCellValue("");
  	              } else {
  	                cell.setCellStyle(allStyles.getHeaderStyle(Style.HEADER_STYLE.STYLE));
  	              }
  	            } else {
  	              if (isUnderline(val)) {
  	            	String styleType = allStyles.getHeaderUnderlineStyleType(Style.HEADER_UNDERLINE_STYLE_TYPE.COLUMN_HEADER);  
  	            	cell.setCellStyle(allStyles.getColHeaderStyle(Style.COL_HEADER_STYLE.UNDERLINE)); 
  	            	if (styleType.equals("BLANK")) cell.setCellValue("");
  	              } else {
  	                cell.setCellStyle(allStyles.getColHeaderStyle(Style.COL_HEADER_STYLE.STYLE));
  	              }
  	            }
  	          } // column header width for loop
  	        } // column header height for loop
  	     } // sheet for loop

  	    // fill row info
      	for (sheetNo=0; sheetNo<noOfSheet; sheetNo++) {      	
  	      logger.debug("fill row info...");
  	      sheet = workbook.getSheet("SHEET" + sheetNo);  	      
  	      ArrayList<String> rowLabels = fsgReader.getFsgReport().getRowLabel();
  	      for (int i=0;i<rowLabels.size();i++) {
  	    	int rowPos = reportStartRow + HEADER_ROW + i;
  	    	row = sheet.getRow(rowPos);
  	    	logger.debug("row=" + rowPos);
  	    	if (row==null) row=sheet.createRow(rowPos);
  	    	cell = row.getCell(0);
  	    	if (cell==null) cell=row.createCell(0);
  	    	cell.setCellValue(rowLabels.get(i));
  	    	cell.setCellStyle(allStyles.getRowHeaderStyle(Style.ROW_HEADER_STYLE.STYLE));
  	      }
      	}

  	    // fill data      	
      	for (sheetNo=0; sheetNo<noOfSheet; sheetNo++) {      	
	  	    logger.info("fill data for sheet " + sheetNo);
	  	    sheet = workbook.getSheet("SHEET" + sheetNo);
	  	    
	  	    FSGSheetData sheetData = fsgReader.getFsgReport().getSheetData(sheetNo);
	  	    String sheetName = sheetData.getSheetName();
	  	    String[][] data = sheetData.getSheetData();
	  	    logger.debug("sheetName=" + sheetName);
	  	    logger.debug("data.length =" + data.length);  // no of row
	  	    logger.debug("data.width  =" + data[0].length);  // no of column
	  	    
	  	    for (int i=0;i<data.length;i++) {
		        row = sheet.getRow(reportStartRow + HEADER_ROW + i);
	  	       	for (int j=0;j<data[0].length;j++) {
	  	       		cell = row.getCell(j+1);
	  	       		if (cell==null) cell=row.createCell(j+1);
	  	       		String val = data[i][j];
	  	       		if (isNumber(val)) {
	  	       		  //cell.setCellValue(Double.parseDouble(val));
	  	       		  cell.setCellValue(toDouble(val));
	  	       		} else if (isPrecentage(val)) {
	  	       		  //cell.setCellValue(Double.parseDouble(val.substring(0, val.length()-1)));  // remove last %
		  	       	  cell.setCellValue(toDouble(val.substring(0, val.length()-1)));  // remove last %	  	       			
	  	       		}  else if (isUnderline(val)) {
	  	       			cell.setCellValue(getUnderlineCellStr(val));
	  	       		} else {
	  	       		  cell.setCellValue(val);
	  	       		}
	  	       		cell.setCellStyle(getDataStyle(val));
	  	       		
	  	       		logger.debug("write cell " + cell.getRowIndex()  +  "," + cell.getColumnIndex() + " with value " + data[i][j]);
	  	       	}
	  	    }
	  	    
	  	    
	  	    // autoresize columns and freeze pane
	        for (int i = 0; i < data.length+1; i++) {
	          sheet.autoSizeColumn(i, false);
	        }
	        
            int colSize = fsgReader.getFsgReport().getColSize();
            int maxColWidth = 0;
            for (int j = 1; j < colSize; j++) {
               int colWidth = sheet.getColumnWidth(j);
               if (colWidth > maxColWidth) maxColWidth = colWidth;
            }
            
            logger.debug("maxColWidth=" + maxColWidth);
            
            maxColWidth = (int)((double)maxColWidth * 1.1d);
            
            logger.debug("New ColWidth=" + maxColWidth);
            
            for (int j = 1; j < colSize; j++) {
            	sheet.setColumnWidth(j, maxColWidth);
            }
	        
            sheet.createFreezePane(1, reportStartRow + 4);
      	} // end loop of all data sheets

    	// create HOME worksheet if one has defined
    	sheet = workbook.getSheet(HOME_WKS_NAME);
    	if (sheet != null) {
    		replaceKeywordInSheet(sheet);
    		fillHomeSheetData(sheet);
    	}
    	
        /*
         * set worksheet name  
         * Single sheet, use report name if not defined
         * Multiple sheet, use sheetName
         */
   
    	for (sheetNo=0; sheetNo<noOfSheet; sheetNo++) {
      	  String sheetName = fsgReader.getFsgReport().getSheetData(sheetNo).getSheetName();
          sheetName = getWorksheetName(sheetName, true);
      	  String tempSheetName = "SHEET" + sheetNo;        
          workbook.setSheetName(workbook.getSheetIndex(tempSheetName), sheetName);
    	}
        
  	    // delete STYLE worksheet
        workbook.removeSheetAt(workbook.getSheetIndex("STYLE"));
        
        //set first sheet for template with HOME page
    	int idx = workbook.getSheetIndex(HOME_WKS_NAME);
    	if (idx >= 0) {
    	  workbook.setActiveSheet(idx);
    	  workbook.getSheetAt(idx).setActiveCell(new CellAddress(0, 0));
    	}
        
    	FileOutputStream fos = new FileOutputStream(outputExcelFile);
    	workbook.write(fos);
    	
    	} catch (Exception e) {
    		logger.catching(e);
    	}
    }
    
    private double toDouble(String str) {
    	try {
    	  Number num = NumberFormat.getNumberInstance(Locale.US).parse(str);
    	  return num.doubleValue();
    	} catch (Exception e) {
    	}
    	return 0;
    }
    
    private boolean isNumber(String str) {
    	if (str==null || str.length()==0) return false;
    	boolean isNumeric = false;
    	try {
			NumberFormat.getNumberInstance(Locale.US).parse(str);
			isNumeric = true;
		} catch (ParseException e) {
		}    	
    	return isNumeric;
/*    	
    	try {
		Double num = Double.parseDouble(str);
		} catch (Exception e) {
			return false;
		}
    	return true;
*/    	
    }
    
    private void replaceKeywordInSheet(XSSFSheet _sheet) {
    	logger.info("Replace keyword for sheet " + _sheet.getSheetName());
    	XSSFRow row = null;
  	    XSSFCell cell = null;
  	    
    	int lastRow = _sheet.getLastRowNum();
    	logger.info("lastRow  = " + lastRow);

    	outerloop:
    	for (int rowIdx=0;rowIdx<=lastRow;rowIdx++) {
    	  row = _sheet.getRow(rowIdx);
    	  if (row==null) row=_sheet.createRow(rowIdx);

    	  short lastCell = row.getLastCellNum();
    	  for (int colIdx=0;colIdx<lastCell;colIdx++) {
    		  logger.debug("reading cell " + CellReference.convertNumToColString(colIdx)+(rowIdx+1));
    		  cell = row.getCell(colIdx);
    		  if (cell !=null && cell.getCellType()==CellType.STRING) {
    		    String cellStr = cell.getStringCellValue();
    		    
    		    if (cellStr.equals(START_KEYWORD) && _sheet.getSheetName().equals(TEMPLATE_WKS_NAME)) {
    		    	reportStartRow = rowIdx; 
    		    	logger.info("TEMPLATE reportStartRow = " + reportStartRow);
    		    	break outerloop;
    		    }
    		    //homeStartRow
    		    if (cellStr.equals(START_KEYWORD) && _sheet.getSheetName().equals(HOME_WKS_NAME)) {
    		    	homeStartRow = rowIdx; 
    		    	logger.info("Home StartRow = " + homeStartRow);
    		    	break outerloop;
    		    }
    		    
    		    if (cellStr.indexOf("&") >= 0) {
    			    String newVal = null;
    			    if (cellStr.equals("&RptDetName")) {
    			      subReportLocRow = cell.getRowIndex(); 
	    			  subReportLocCol = cell.getColumnIndex();
    			    } else {
    			      //newVal = fsgReader.getHeaderValue(cellStr);
    			      newVal = replaceTokens(cellStr);
    			    }
    			    cell.setCellValue(newVal);
    			    logger.debug("replace " + cellStr + " to " + newVal);
    		    } // found a token
    		  } // a valid cell
    	    } // end loop for column scanning
    	} // end loop for row scanning
    }
    
    private void fillHomeSheetData(XSSFSheet _sheet) {
    	XSSFRow row = null;
  	    XSSFCell cell = null;
  	    row = _sheet.getRow(homeStartRow);
  	    cell = row.getCell(0);  // title cell
  	    cell.setCellValue(getHomeTitle());
  	    row = _sheet.getRow(homeStartRow+1);
  	    cell = row.getCell(0);
  	    XSSFCellStyle linkStyle = cell.getCellStyle();
  	    
  	    for (int i=0;i<fsgReader.getNoOfSheet();i++) {
  	    	row = _sheet.getRow(homeStartRow + i + 1);
  	    	if (row==null) row = _sheet.createRow(homeStartRow + i);
  	    	cell = row.getCell(0);
  	    	if (cell==null) cell = row.createCell(0);
  	    	String cellVal = fsgReader.getFsgReport().getSheetData(i).getSheetName();
  	    	String sheetName = getWorksheetName(cellVal, true);
  	    	cell.setCellValue(getWorksheetName(getSubReportName(cellVal), false));
  	    	XSSFHyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
  	    	link.setAddress("'" + sheetName + "'!A1");
  	    	cell.setHyperlink(link);
  	    	cell.setCellStyle(linkStyle);
  	    }
    }
    
    private XSSFCellStyle getDataStyle(String dataStr) {
    	// default
    	XSSFCellStyle rtnStyle = allStyles.getDataStyle(DATA_STYLE.STYLE);
    	
    	if (dataStr==null || dataStr.equals("")) {
    		return rtnStyle;
    	}
    	
    	if (dataStr.equals("n/m")) return allStyles.getDataStyle(DATA_STYLE.NO_MEANING);
    	
    	if (dataStr.endsWith("%")) return allStyles.getDataStyle(DATA_STYLE.PRECENTAGE);

    	//11i does not have RealNum attribute in RptCell node
    	// strip comma, dot, larger and smaller than sign
    	String fixedDataStr = dataStr;
    	fixedDataStr = fixedDataStr.replace(".", "");
    	fixedDataStr = fixedDataStr.replace(",", "");
    	fixedDataStr = fixedDataStr.replace("<", "");
    	fixedDataStr = fixedDataStr.replace(">", "");
    	
    	if (fixedDataStr.length()==0) return rtnStyle;
    	
    	try {
    		//TODO
    	  Double num = Double.parseDouble(fixedDataStr);
    	  return allStyles.getDataStyle(DATA_STYLE.NUMERIC);
    	
    	} catch (Exception e) {}
    	
    	String underlineChar = dataStr.substring(0,1);
    	if (isUnderline(dataStr)) {
    		logger.debug("apply underline style for " + underlineChar);
    		return allStyles.getUnderlineStyle(underlineChar); 
    	}
    	
    	return rtnStyle;
    	
    }

    private String getUnderlineCellStr(String _str) {
    	String rtnVal = _str;
    	if (_str != null && _str.length() > 0) {
      	  String underlineChar = _str.substring(0,1);
      	  String type = allStyles.getUnderlineStyleType(underlineChar);
      	  if (type.equals("FILL")) {
      		  rtnVal = underlineChar;
      	  } else if (type.equals("BORDER")) {
      		  rtnVal = null;
      	  } else if (type.equals("BLANK")) {
      		rtnVal = null;
      	  }
    	}
    	return rtnVal;
    }
    
    private boolean isPrecentage(String _str) {
    	if (_str != null && _str.length() > 0) {
    		if (_str.endsWith("%")) return true;
    	}
    	return false;
    }
    
    private boolean isUnderline(String _str) {
    	if (_str != null && _str.length() > 0) {
    	  String underlineChar = _str.substring(0,1);
    	  if (_str.replace(underlineChar, "").equals("")) {
    		return true;
    	  } else {
    		return false;
    	  }
    	}
    	return false;
    }    
    
    
    private String getHomeTitle() {
    	String rtnVal = "";
    	String subReportName = fsgReader.getFsgReport().getSheetData(0).getSheetName();
    	if (subReportName.indexOf("=") > 0) {
    		rtnVal = subReportName.split("=")[0].trim();
    	}
    	return rtnVal;
    }
    
    
    private String getSubReportName(String _name) {
        if ((_name == null) || _name.equals("")) {
            return "Sheet1";
        }
        
        if (_name.startsWith("No specific ") && _name.endsWith(" requested")) {
        	return fsgReader.getHeaderValue("ReportName");
        }
        return _name.trim();
    }
    
    private String getWorksheetName(String _name, boolean normalized) {
        if ((_name == null) || _name.equals("")) {
            return "Sheet1";
        }
        
        String rtnValue = getSubReportName(_name);

        // when sheet name is "Department=130 (Computer Resources)"
        // show "130 (Computer Resources)" only
        try {
            if (rtnValue.indexOf("=") > 0) {
                rtnValue = rtnValue.split("=")[1];
            }
        } catch (Exception e) {}

        if (normalized) {
            // illegal characters :, \, /, ?, *, [ or ]
            if (rtnValue.length() > 30) {
                rtnValue = rtnValue.substring(0, 30);
            }

            rtnValue = rtnValue.replace('/', ' ');
            rtnValue = rtnValue.replace('\\', ' ');
            rtnValue = rtnValue.replace('?', ' ');
            rtnValue = rtnValue.replace('*', ' ');
            rtnValue = rtnValue.replace('[', ' ');
            rtnValue = rtnValue.replace(']', ' ');
        }
        
        rtnValue = rtnValue.trim();

        return rtnValue;
    }
    
    
    private String replaceTokens(String text) {
		Matcher matcher = pattern.matcher(text);
		StringBuffer buffer = new StringBuffer();
		
		while (matcher.find()) {
		  String token = matcher.group(1);
		  //String replacement = replacements.get(matcher.group(1));
		  String replacement = fsgReader.getHeaderValue(token);
		  if (replacement != null) {
		    matcher.appendReplacement(buffer, "");
		    buffer.append(replacement);
		  }
		}
		matcher.appendTail(buffer);
		return buffer.toString();
    }    

}
