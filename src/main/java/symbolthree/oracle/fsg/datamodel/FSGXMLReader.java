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

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.InputSource;

public class FSGXMLReader {
    private Document document  = null;
    private Namespace ns = Namespace.getNamespace("fsg","http://www.oracle.com/fsg/2002-03-20/");
    private FSGReport fsgReport = new FSGReport();
    
    private XPathFactory             xpfac    = XPathFactory.instance();
    private XPathExpression<Element> xpath    = null;
    
    static final Logger logger = LogManager.getLogger(FSGXMLReader.class.getName());
    
	public FSGXMLReader(String _xmlFile) {
		
        SAXBuilder builder = new SAXBuilder();
		try {
			
			//document = builder.build(new FileInputStream(_xmlFile));
			
			// try to overcome the "Invalid byte 1 of 1-byte UTF-8 sequence" error for non-Latin characters
			FileInputStream fis = new FileInputStream(_xmlFile);
			InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
			InputSource source = new InputSource(reader);
			source.setEncoding("UTF-8");
			document = builder.build(source);
			
		} catch (Exception e) {
			logger.catching(e);
		}
	}
	
	public void read() {
		
		xpath = xpfac.compile("/MasterReport/*[not(*)]", Filters.element(), null, ns);
		List<Element> children = xpath.evaluate(document);
		
		Iterator<Element> itr = children.iterator();
		while (itr.hasNext()) {
			Element ele = itr.next();
			logger.debug("Header - " + ele.getName() + "=" + ele.getValue().trim());
			getFsgReport().addHeader(ele.getName(), ele.getValue().trim());
		}
		
        setColumnInfo();
        
        xpath = xpfac.compile("/MasterReport/fsg:RptDef", Filters.element(), null, ns);
        
        List<Element>  worksheet   = xpath.evaluate(document);
        
        Iterator<Element> worksheetItr = worksheet.iterator();
        
        int sheetNo = 0;
        while (worksheetItr.hasNext()) {
        	// set headerInfo for the first sheet (or the only sheet). All sheets are the same.
        	Element rptDefEle = worksheetItr.next();
        	
        	if (sheetNo==0) setRowInfo(rptDefEle);
        	
        	String sheetName = rptDefEle.getAttributeValue("RptDetName").trim();
        	
        	FSGSheetData sheetData = new FSGSheetData(sheetName);
        	logger.debug("sheetname="+ sheetName);
        	
        	// fill out cell values
        	logger.debug("sheet data dimension = " + getFsgReport().getColSize() + " x " + getFsgReport().getRowSize());
        	String[][] cells = new String[getFsgReport().getRowSize()][getFsgReport().getColSize()];
        	
        	int cellPosY = 0;
        	List<Element> rptLines = rptDefEle.getChildren("RptLine", ns);
        	Iterator<Element> rptLinesItr = rptLines.iterator();
        	
        	while (rptLinesItr.hasNext()) {
        		//System.out.println("in rpt LINE Itr...");
        		Element rptLine = rptLinesItr.next();
        		List<Element> rptCells = rptLine.getChildren("RptCell", ns);
        		Iterator<Element> rptCellsItr = rptCells.iterator();
        		
        		int cellPosX     = 0;
        		String cellValue = null;
        		
        		while (rptCellsItr.hasNext()) {
        			//System.out.println("in rpt CELL sItr...");
      			    Element rptCell = rptCellsItr.next();
      			    // first col is the account names, so data is starting from second col
        			if (cellPosX > 0) {
        				cellValue = rptCell.getAttributeValue("RealNum");
        				
        				if (cellValue == null || cellValue.equals("")) {
        					cellValue = rptCell.getValue();
        					cellValue = cellValue.trim();
        				} else {
        				  if (rptCell.getValue().endsWith("%")) {
        					  cellValue = cellValue + "%";
        				  }
        				}
        				cells[cellPosY][cellPosX-1] = cellValue;
        				logger.debug("[" + cellPosY + "," + cellPosX + "] - " + cellValue);        				
        			}
    			    cellPosX++;
        		}
        		cellPosY++;
        	}
            sheetData.setSheetData(cells);
            getFsgReport().addSheetData(sheetNo, sheetData);
        	sheetNo++;
        }

	}
	
	private void setColumnInfo() {
		xpath = xpfac.compile("/MasterReport/fsg:ColContext", Filters.element(), null, ns);
		List<Element> colCxts = xpath.evaluate(document);
		Iterator<Element> colCxtsItr = colCxts.iterator();
		int pos = 0;
		while (colCxtsItr.hasNext()) {
			String[] colHeaderLines = new String[9]; 
			Element colCxt = colCxtsItr.next();
			for (int i=0;i<9;i++) {
				colHeaderLines[i] = colCxt.getChild("ColHeadLine" + (i+1), ns).getValue().trim();
				logger.debug("Col Info [" + pos + "," + i + "] - " + colHeaderLines[i]);
			}
			getFsgReport().addColLabel(pos, colHeaderLines);
			pos++;
		}
	}
	
	private void setRowInfo(Element _ele) {
		List<Element> rptLines = _ele.getChildren("RptLine", ns);
		Iterator<Element> rptLinesItr = rptLines.iterator();
		
		int pos = 0;
		while (rptLinesItr.hasNext()) {
			Element rptLine = rptLinesItr.next();
			// get the first child -- which is the account label
			// R12 is RealDesc attribute, 11i is the node value 
			String lbl = rptLine.getChild("RptCell", ns).getAttributeValue("RealDesc");
			if (lbl==null) {
				lbl = rptLine.getChild("RptCell", ns).getText();
			}
			lbl = rtrim(lbl);
			logger.debug("Row Info [" + pos + "] - " + lbl);
			getFsgReport().addRowLabel(pos, lbl);
			pos++;
		}
	}
	
	public String getHeaderValue(String name) {
		  String rtnVal = fsgReport.getHeaderValue(name);
		  if (rtnVal==null) rtnVal = name;
		  return rtnVal;
	}

	public int getNoOfSheet() {
		// R12 use TabCount value, 11i uses XPath count
		int rtnVal = -1;
		String str = getHeaderValue("TabCount");
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		
		xpath = xpfac.compile("/MasterReport/fsg:RptDef", Filters.element(), null, ns);
		List<Element> noOfReport = xpath.evaluate(document);
		rtnVal = noOfReport.size();
		
		return rtnVal;
		
	}
	
	public FSGReport getFsgReport() {
		return fsgReport;
	}

	public void setFsgReport(FSGReport fsgReport) {
		this.fsgReport = fsgReport;
	}
	
	
	public static String ltrim(String s) {
	    int i = 0;
	    while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
	        i++;
	    }
	    return s.substring(i);
	}

	public static String rtrim(String s) {
	    int i = s.length()-1;
	    while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
	        i--;
	    }
	    return s.substring(0,i+1);
	}
	
/*	
	public static void main(String[] args) {
		String inputFile = "C:\\Users\\Administrator\\symbolthree\\myFSG\\log\\single.xml";
		//String inputFile = "C:\\Users\\Administrator\\symbolthree\\myFSG\\log\\multiple.xml";
		FSGXMLReader reader = new FSGXMLReader(inputFile);
		reader.read();
	}
*/	
}
