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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/OutputFileQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 2/21/17 4:36a $
 * $Revision: 2 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.flower.Answer;
import symbolthree.flower.Choice;
import symbolthree.flower.ConsoleDirectoryBrowser;
import symbolthree.flower.ConsoleFileBrowser;
import symbolthree.flower.Message;

public class OutputFileQuestion extends MyFSGQuestion {

	private String errMsg = null;
    private String actionType = null;  
    static final Logger logger = LogManager.getLogger(OutputFileQuestion.class.getName());    	
	public static final String RCS_ID = "$Header: /TOOL/myMSG/src/symbolthree/oracle/fsg/OutputFileQuestion.java 2     2/21/17 4:36a Christopher Ho $";
	
    public OutputFileQuestion() {
    	actionType = Answer.getInstance().getA("ActionType");
	}

    @Override
    public boolean enterQuestion() {
    	String fileName = Answer.getInstance().getB(OUTPUT_FILENAME);
    	
    	if (fileName == null || fileName.equals("")) {
            String fileExt = (actionType.equals(FSGXLS))?"xlsx":"xml";
    		fileName = RGRARG.init().generateDefaultFileName() + "." + fileExt;
    		Answer.getInstance().putB(OUTPUT_FILENAME, fileName);
    	}
		return true;
	}
    
    
    @Override
    public boolean isMultipleChoices() {
        if (Answer.getInstance().getB(OUTPUT_ACTION) == null) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean needAnswer() {
        if ((Answer.getInstance().getA(this) != null)
                && (Answer.getInstance().getA(this).equals(CHANGE_FILE)
                    || Answer.getInstance().getA(this).equals(CHANGE_DIRECTORY)
            )) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isDirectoryInput() {
        if ((Answer.getInstance().getB(OUTPUT_ACTION) != null)
                && Answer.getInstance().getB(OUTPUT_ACTION).equals(CHANGE_DIRECTORY)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public String getQuestion() {
    	String str = "";
    	if (actionType.equals(FSG_ONLY)) {
    		str = "** ONLY FSG Data will be generated **\n";
    	}
    	str = str + "Output directory and file name:\n";
    	return str;
    }
    
    @Override
    public String getExplanation() {
    	String dir = null;
    	if (actionType.equals(FSGXLS)) {
    	  dir = Instances.getInstance().getFileDirectory();
    	} else if (actionType.equals(FSG_ONLY)) {
    	  dir = Instances.getInstance().getXMLDataDirectory();	
    	}
    	
    	String filename = Answer.getInstance().getB(OUTPUT_FILENAME);
    	
    	String str = "Directory : " + dir + "\n" + 
    	             "File name : " + filename + "\n";
    	
        String ans = Answer.getInstance().getA(this);

        if ((ans != null) && ans.equals(CHANGE_FILE)) {
            str = str + "\n\n" + "Please enter a new filename";
        }

        if ((ans != null) && ans.equals(CHANGE_DIRECTORY)) {
            str = str + "\n\n" + "Please select a directory";
        }

        return str;
    	
    }
    
    @Override
    public ArrayList<Choice> choices() {
        ArrayList<Choice> al = new ArrayList<Choice>();

        if (actionType.equals(FSGXLS)) {
          al.add(new Choice("N", "Next -> Select template"));
        }

        if (actionType.equals(FSG_ONLY)) {
          al.add(new Choice("N", "Next -> start generate"));
        }
        
        al.add(new Choice(CHANGE_DIRECTORY, "Change directory"));
        al.add(new Choice(CHANGE_FILE, "Change filename"));
        return al;
    }

    @Override
    public boolean leaveQuestion() {
        String ans = Answer.getInstance().getA(this);

        if ((ans != null) &&!ans.equals("")) {
	        if ((Answer.getInstance().getB(OUTPUT_ACTION) != null)
	                && Answer.getInstance().getB(OUTPUT_ACTION).equals(CHANGE_DIRECTORY)) {
	
	            // validation
	            File f = new File(ans);
	
	            if (!f.exists() || !f.isDirectory()) {
	                errMsg = "Invalid directory value.";
	                Answer.getInstance().putA(this, CHANGE_DIRECTORY);
	                return false;
	            }
	
	            Answer.getInstance().putB(OUTPUT_DIR, ans);
	            Answer.getInstance().putB(OUTPUT_ACTION, null);
	            Instances.getInstance().saveDirectory();
	            
	        } else if ((Answer.getInstance().getB(OUTPUT_ACTION) != null)
	                   && Answer.getInstance().getB(OUTPUT_ACTION).equals(CHANGE_FILE)) {
                if (!ans.toUpperCase().endsWith(".XLSX") && actionType.equals(FSGXLS)) {
                    ans = ans + ".xlsx";
                }

                if (!ans.toUpperCase().endsWith(".XML") && actionType.equals(FSG_ONLY)) {
                    ans = ans + ".xml";
                }
                
                Answer.getInstance().putB(OUTPUT_FILENAME, ans);
                Answer.getInstance().putB(OUTPUT_ACTION, null);
            }
        }
        return true;
    }    

    @Override
    public String nextAction() {
    	
    	logger.debug("OUTPUT_DIR     =" + Answer.getInstance().getB(OUTPUT_DIR));
    	logger.debug("OUTPUT_FILENAME=" + Answer.getInstance().getB(OUTPUT_FILENAME));    	
    	
        String ans = Answer.getInstance().getA(this);

        if (ans == null) {
            Answer.getInstance().putB(OUTPUT_ACTION, null);
            return "OutputFile";
        }

        if (ans.equals(CHANGE_DIRECTORY)) {
            System.setProperty(ConsoleDirectoryBrowser.CURR_DIR, Instances.getInstance().getFileDirectory());
            Answer.getInstance().putB(OUTPUT_ACTION, CHANGE_DIRECTORY);
            return "OutputFile";
            
        } else if (ans.equals(CHANGE_FILE)) {
            System.setProperty(ConsoleFileBrowser.CURR_DIR, Instances.getInstance().getFileDirectory());
            Answer.getInstance().putB(OUTPUT_ACTION, CHANGE_FILE);
            return "OutputFile";
            
        } else if (ans.equals("N") && actionType.equals(FSGXLS)) {
        	return "SelectTemplate";

        } else if (ans.equals("N") && actionType.equals(FSG_ONLY)) {
        	return "SaveInstance";
        } else {
            Answer.getInstance().putB(OUTPUT_ACTION, null);

            return "OutputFile";
        }
    }    
    
    @Override
    public Message leaveQuestionMsg(boolean flag) {
        if (!flag) {
            return new Message(Message.ERROR, errMsg);
        } else {
            return null;
        }
    }
    
    @Override
    public String lastAction() {
        return "ReportParameters";
    }           
	
    @Override
    public boolean lineWrap() {
        return false;
    }

}
