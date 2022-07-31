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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/PostProcessQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 3/29/17 6:46a $
 * $Revision: 5 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;

public class PostProcessQuestion extends MyFSGQuestion {

	private ArrayList<Choice> al = new ArrayList<Choice>();
	private String actionType = null;    
    File excelFile = null;
    static final Logger logger = LogManager.getLogger(PostProcessQuestion.class.getName());
    
	public PostProcessQuestion() {
		actionType = Answer.getInstance().getA("ActionType");		
	}
	
    @Override
    public boolean enterQuestion() {
    	return true;
    }
	
    @Override
    public String getQuestion() {
    	String outputFile = null;
    	
    	if (actionType.equals(FSGXLS) || actionType.equals(XLS_ONLY)) {
          String dir  = Answer.getInstance().getB(OUTPUT_DIR);
   		  outputFile = Answer.getInstance().getB(OUTPUT_FILENAME);
    	  excelFile = new File(dir, outputFile);
    	  if (excelFile.exists() && excelFile.length() > 0) {
        	al.add(new Choice("OPEN", "Open Excel file"));
        	return "Process completed. Output file is \n" + excelFile.getAbsolutePath();
    	  }
    	}
    	
    	if (actionType.equals(FSG_ONLY)) {
    	  outputFile = Answer.getInstance().getB(FSG_XML_FILE);
    	  File file = new File(outputFile);
    	  if (file.exists()) {
    		  return "Process completed. Output file is " + outputFile;
    	  }
    	} 
    	
    	return "Prcocess failed. Please review log file for details.";
    }
    

	public boolean lineWrap() {
		return false;
	}
    
    @Override
    public boolean isMultipleChoices() {
       return true;
    }
    
    @Override
    public ArrayList<Choice> choices() {
    	al.add(new Choice("RESTART", "Start another process"));
        return al;
    }
    
    @Override
    public String nextAction() {
    	String ans = Answer.getInstance().getA(this);
    	if (ans.equals("OPEN")) {
    		try {
				Desktop.getDesktop().open(excelFile);
			} catch (IOException e) {
				logger.catching(e);
			}  	
    	}
    	
    	if (ans.equals("RESTART")) {
    		resetParameters();
    		return "ServerClientMode";
    	}
    	
  	    return "PostProcess";    	
    }
    
    //TODO
    private void resetParameters() {
    	RGRARG.init().clearAll();
    	Answer.getInstance().clearAll();
    }

}
