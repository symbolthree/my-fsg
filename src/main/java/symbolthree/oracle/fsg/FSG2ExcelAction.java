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

package symbolthree.oracle.fsg;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.oracle.fsg.datamodel.FSG2XLSX;

public class FSG2ExcelAction extends MyFSGActionBase {
	
	String actionType = null;
	String releaseName = null;
    static final Logger logger = LogManager.getLogger(FSG2ExcelAction.class.getName());
    
	public FSG2ExcelAction() {
		actionType = Answer.getInstance().getA("ActionType");
	}

	@Override
    public void execute() {
		
		logger.debug("Action Type = " + actionType);
		
		String inputXML     = Answer.getInstance().getB(FSG_XML_FILE);
		String fsglog       = Answer.getInstance().getB(FSG_XML_LOG);
		
		File xfile = new File(inputXML);
		File log = null;
		if (! actionType.equals(XLS_ONLY)) {
		  log = new File(fsglog);
		}

		String outputXlsDir = Answer.getInstance().getB(OUTPUT_DIR); 
		String ouputXls     = Answer.getInstance().getB(OUTPUT_FILENAME);
		String outputExcel  = outputXlsDir + File.separator + ouputXls;
		
		// for FSG_ONLY mode, OUTPUT_FILENAME is the final xml file name 
	    String s = ouputXls.substring(0, ouputXls.lastIndexOf(".")); 

		if (! actionType.equals(XLS_ONLY)) {
		  //File d = xfile.getParentFile();
		  File d = new File(System.getProperty("user.dir") + File.separator + "data"); 	
		  File newXMLFile = new File(d, s + ".xml");
		  File newLogFile = new File(System.getProperty(LOG_DIR), s + ".log");
		  
		  try {
		    FileUtils.moveFile(xfile, newXMLFile);
		    FileUtils.moveFile(log, newLogFile);
		  } catch (Exception e) {
			  logger.catching(e);
		  }
		  
		  Answer.getInstance().putB(FSG_XML_FILE, newXMLFile.getAbsolutePath());  
		
		  logger.debug("XML file renamed to " + newXMLFile.getName());
		}
		
		if (actionType.equals(FSGXLS) || actionType.equals(XLS_ONLY)) {
			
		  logger.debug("output file renames to " + xfile.getAbsolutePath());
		  logger.debug("Output Excel file = " + outputExcel);
		
		  logger.debug("FSG2Excel start...");
		  String template = Answer.getInstance().getB(TEMPLATE_DIR) + 
				            File.separator + 
				            Answer.getInstance().getB(TEMPLATE_FILENAME); 
		  
		  
        //if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
		  FSG2XLSX f2e = new FSG2XLSX(Answer.getInstance().getB(FSG_XML_FILE), outputExcel, template);
		  f2e.doConvert();
        //}
        
		  logger.debug("FSG2Excel done");
		  
		}
	}
	
	@Override
	public String nextAction() {
		return "PostProcess";
	}
}
