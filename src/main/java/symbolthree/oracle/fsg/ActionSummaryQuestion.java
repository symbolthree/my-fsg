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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/ActionSummaryQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 2/21/17 4:36a $
 * $Revision: 1 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;

public class ActionSummaryQuestion extends MyFSGQuestion {

	private String actionType;
    static final Logger logger = LogManager.getLogger(SelectTemplateQuestion.class.getName());	
    
	public ActionSummaryQuestion() {
		actionType = Answer.getInstance().getA("ActionType");
	}

    @Override
    public boolean isMultipleChoices() {
            return true;
    }	

    @Override
    public String getQuestion() {
   		String str = "Action Summary\n";
   		return str;
    }

    @Override
    public String getExplanation() {
    	String str = null;
    	String reportName = RGRARG.init().getMeaning(RGRARG.REPORT_ID);
    	String outputDir  = Answer.getInstance().getB(OUTPUT_DIR);
    	String outputFile = Answer.getInstance().getB(OUTPUT_FILENAME);
    	String dataDir    = Answer.getInstance().getB(DATA_DIR);
    	String dataFile   = Answer.getInstance().getB(DATA_FILENAME);
    	String tempDir    = Answer.getInstance().getB(TEMPLATE_DIR);
    	String tempFile   = Answer.getInstance().getB(TEMPLATE_FILENAME);
    	
    	if (actionType.equals(FSGXLS)) {
    		str = "Create FSG Excel report\n" +
    	          "-----------------------\n" +
    	          "  Report Name : " + reportName + "\n" +
    	          "  Template    : " + tempDir + File.separator + tempFile + "\n" +
			      "  Output Dir  : " + outputDir + "\n" +    		
			      "  Output File : " + outputFile;

    	} else if  (actionType.equals(FSG_ONLY)) {
    		str = "Generate FSG Data only\n" +
      	          "----------------------\n" +
  			      "  Output Dir  : " + dataDir + "\n" +    		
  			      "  Output File : " + dataFile;
    	
    	} else if  (actionType.equals(XLS_ONLY)) {
    		str = "Convert FSG Data to Excel file\n" +
      	          "------------------------------\n" +
	              "  Template    : " + tempDir + File.separator + tempFile + "\n" +    		
			      "  Output Dir  : " + outputDir + "\n" +    		
			      "  Output File : " + outputFile;
    	} 
    	
    	return str;
    }
    
    @Override
    public ArrayList<Choice> choices() {
        ArrayList<Choice> al = new ArrayList<Choice>();
        al.add(new Choice("S", "Start process"));
        return al;
    }
    
	@Override	
    public String nextAction() {
    	String ans = Answer.getInstance().getA(this);
    	
    	if (ans != null && ans.equals("S")) {
    		return "SaveInstance";
    	}
    	
    	return "ActionSummary";
	}

	@Override
	public String lastAction() {
		return "SelectTemplate";
	} 
	
    @Override
    public boolean lineWrap() {
        return false;
    }	
}
