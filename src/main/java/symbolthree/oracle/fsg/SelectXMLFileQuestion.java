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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/SelectXMLFileQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 2/21/17 4:36a $
 * $Revision: 3 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;
import symbolthree.calla.ConsoleDirectoryBrowser;
import symbolthree.calla.Helper;

public class SelectXMLFileQuestion extends MyFSGQuestion {

	private String dataDir     = "";
	private String dataFile    = "";
	private String outputDir   = "";
	private String outputFile  = "";
	
	private String outputAction = ""; 
    static final Logger logger = LogManager.getLogger(SelectXMLFileQuestion.class.getName());	
	
	public SelectXMLFileQuestion() {
    	dataDir = Instances.getInstance().getXMLDataDirectory();
    	dataFile = Helper.nullStr(Answer.getInstance().getB(DATA_FILENAME));
    	
    	outputDir = Instances.getInstance().getFileDirectory();
    	outputFile = Helper.nullStr(Answer.getInstance().getB(OUTPUT_FILENAME));
    	
    	outputAction = Helper.nullStr(Answer.getInstance().getB(OUTPUT_ACTION));    	
 	}

	@Override
	public boolean enterQuestion() {
		
		return true;
	}
	
    @Override
    public String getQuestion() {
   		String str = "Please select a FSG Data file and set the output Excel file\n";
   		return str;
    }

    @Override
    public String getExplanation() {
    	
    	if (outputAction.equals(CHANGE_FILE)) {
    		outputFile = "** Enter file name **";
    	}
    	
    	String str = "FSG Data File\n" +
                    "--------------\n" +
                     "  Directory : " + dataDir + "\n" +
    			     "  File name : " + dataFile + "\n\n" +
                     "Output Excel File\n" +
                     "-----------------\n" +    			     
    			     "  Directory : " + outputDir + "\n" +
                     "  File name : " + outputFile + "\n";
    	
    	if (!outputFile.equals("") && ! outputAction.equals(CHANGE_FILE)) {
    		File f = new File(outputDir, outputFile);
    		if (f.exists()) {
    			str = str + 
    				 "  **This Excel file exists**";
    		}
    	}
    	return str;
    }
    
    @Override
    public boolean isMultipleChoices() {
        if(outputAction.equals(SELECT_FILE) || 
           outputAction.equals(CHANGE_DIRECTORY) ||
           outputAction.equals(CHANGE_FILE)) {  
        	return false;
        }
    	return true;
    }
    
    @Override	
    public boolean isFileInput() {
        if(outputAction.equals(SELECT_FILE)) {   
        	return true;
        }
    	return false;
    }

    @Override	
    public boolean isDirectoryInput() {
        if(outputAction.equals(CHANGE_DIRECTORY)) {   
        	return true;
        }
    	return false;
    }    
    
    @Override
    public ArrayList<Choice> choices() {
        ArrayList<Choice> al = new ArrayList<Choice>();
        if (!dataFile.equals("") && !outputFile.equals("")) {
            al.add(new Choice(NEXT_QUESTION, "Next -> Select template"));	
           }
        al.add(new Choice(SELECT_FILE, "Select FSG Data File"));
        al.add(new Choice(CHANGE_DIRECTORY, "Select output directory"));
        al.add(new Choice(CHANGE_FILE, "Set output file name"));
        
        return al;
    }
    
    @Override
    public String fileExtension() {
    	return "XML";
    }
    
    @Override
    public boolean leaveQuestion() {
    	String ans = Answer.getInstance().getA(this);
    	String action = Helper.nullStr(Answer.getInstance().getB(OUTPUT_ACTION));
    	
    	if (action.equals(SELECT_FILE)) {
    		File file = new File(ans);
    		Answer.getInstance().putB(DATA_DIR, file.getParent());
    		Answer.getInstance().putB(DATA_FILENAME, file.getName());
    		Answer.getInstance().putB(FSG_XML_FILE, file.getAbsolutePath());	

    		//if (outputFile.equals("")) {
    			outputFile = file.getName().substring(0, file.getName().length()-4) + ".xlsx";
    			Answer.getInstance().putB(OUTPUT_FILENAME, outputFile);
    		//}
    	}
    	
    	if (action.equals(CHANGE_DIRECTORY)) {
    		Answer.getInstance().putB(OUTPUT_DIR, ans);
    	}
    	
    	if (action.equals(CHANGE_FILE)) {
    		if (!ans.endsWith(".xlsx") || !ans.endsWith(".XLSX")) {
    			ans = ans + ".xlsx";
    		}
    		Answer.getInstance().putB(OUTPUT_FILENAME, ans);
    	}
    
    	return true;
    }
    
    @Override    
    public String nextAction() {
    	Answer.getInstance().putB(OUTPUT_ACTION, null);    	
    	String ans = Answer.getInstance().getA(this);
    	
        if (ans == null) {
            return "SelectXMLFile";
        }
        
        if (ans.equals(SELECT_FILE)) {
            System.setProperty(ConsoleDirectoryBrowser.CURR_DIR, dataDir);
            Answer.getInstance().putB(FILE_BROWSER_FILE, dataDir); 
            Answer.getInstance().putB(OUTPUT_ACTION, SELECT_FILE);
            return "SelectXMLFile";        	
        }

        if (ans.equals(CHANGE_DIRECTORY)) {
            System.setProperty(ConsoleDirectoryBrowser.CURR_DIR, outputDir);
            Answer.getInstance().putB(FILE_BROWSER_FILE, outputDir);
            Answer.getInstance().putB(OUTPUT_ACTION, CHANGE_DIRECTORY);
            return "SelectXMLFile";        	
        }

        if (ans.equals(CHANGE_FILE)) {
            Answer.getInstance().putB(OUTPUT_ACTION, CHANGE_FILE);
            return "SelectXMLFile";        	
        }
        
        if (ans.equals(NEXT_QUESTION)) {
        	return "SelectTemplate";
        }
        
        return "SelectXMLFile";        
    }
    
    @Override
    public boolean lineWrap() {
        return false;
    }
    
    @Override
    public String lastAction() {
        return "ActionType";
    }          
}
