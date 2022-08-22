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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;
import symbolthree.calla.Helper;
import symbolthree.calla.Message;
import symbolthree.oracle.fsg.datamodel.TemplateChecker;

public class SelectTemplateQuestion extends MyFSGQuestion {

	private String templateDir = null;
	private String actionType;
	private String selectedTemplate = null;
	private String errMsg = null;
	private String lastChoice = null;
	
    static final Logger logger = LogManager.getLogger(SelectTemplateQuestion.class.getName());	
    
	public SelectTemplateQuestion() {
		
		actionType  = Answer.getInstance().getA("ActionType");
    	templateDir = Instances.getInstance().getTemplateDirectory();
    	selectedTemplate = Helper.nullStr(Answer.getInstance().getB(TEMPLATE_FILENAME));
    	lastChoice = Helper.nullStr(Answer.getInstance().getB(LAST_CHOICE));
    	
    	logger.debug("template dir=" + templateDir);
    	logger.debug("template    =" + selectedTemplate);
	}

    @Override
    public String getQuestion() {
    	String str = "";
    	String ans = Answer.getInstance().getA(this);
    	
    	if (ans != null && ans.equals(CHANGE_DIRECTORY)) {
    		str = "Select a template directory";
    	} else {
      	  str = "Select a Excel template file\n";
    	}
    	
    	return str;
    }

    @Override
    public String getExplanation() {
    	return  "Directory : " + templateDir + "\n" +
                "Template  : " + selectedTemplate;
    }
    
    @Override
    public boolean isMultipleChoices() {
    	String ans = Answer.getInstance().getA(this);
    	if (ans != null && ans.equals(CHANGE_DIRECTORY)) {
    		return false;
    	} else {
    		return true;
    	}
    }
    
    @Override	
    public boolean isDirectoryInput() {
    	String ans = Answer.getInstance().getA(this);
    	if (ans != null && ans.equals(CHANGE_DIRECTORY)) {    	
    	  return true;
    	} else {
    	  return false;
    	}
    }
    
    @Override
    public ArrayList<Choice> choices() {
        ArrayList<Choice> al = new ArrayList<Choice>();
        
        if (!selectedTemplate.equals("")) {
        	al.add(new Choice(NEXT_QUESTION, "Next -> Action summary"));
        }
        
        al.add(new Choice(CHANGE_DIRECTORY, "Change directory"));
        
        List<File> templateFiles = null;

        templateFiles = (List<File>) FileUtils.listFiles(new File(templateDir), new String[] { "xlsx", "XLSX" }, true);

        Iterator<File> itr = templateFiles.iterator();
        
        while (itr.hasNext()) {
            File   _file     = itr.next();
            String _filePath = _file.getAbsolutePath();

            al.add(new Choice(_filePath, _file.getName()));
        }

        return al;
    }
    
    @Override
    public boolean leaveQuestion() {
    	String ans = Answer.getInstance().getA(this);
    	
    	if (ans.equals(NEXT_QUESTION)) {
    		return true;
    	}
    	
    	if (lastChoice.equals(CHANGE_DIRECTORY) && ans != null && ! ans.equals("")) {
    		File dir = new File(ans);
    		if (!dir.exists()) {
    			errMsg = "Directory " + ans + " does not exist";
    			return false;
    		} else {
    			Answer.getInstance().putB(TEMPLATE_DIR, ans);
    			return true;
    		}
    	}
    	
    	boolean isValid = true;
    	if (! ans.equals(CHANGE_DIRECTORY)) {
    	  logger.debug("start checking template file...");
  	      TemplateChecker checker = new TemplateChecker(ans); 
  	      isValid = checker.check();
  	      if (!isValid) {
  	    	  errMsg = checker.getMessage();
  	    	  logger.debug("errMsg=" + errMsg);
  	    	  Answer.getInstance().putA(this, null);
  	      } else {
  	    	  logger.debug("Template file passed");
  	    	  Answer.getInstance().putB(TEMPLATE_DIR, new File(ans).getParent());
  	    	  Answer.getInstance().putB(TEMPLATE_FILENAME, new File(ans).getName());
  	      }
    	}
  	    return isValid;
    }

    @Override
    public Message leaveQuestionMsg(boolean flag) {
        if (!flag) {
            return new Message(Message.ERROR, errMsg);
        } else {
          return null;
        }
    }    
    
    public String nextAction() {
    	String ans = Answer.getInstance().getA(this);
    	Answer.getInstance().putB(LAST_CHOICE, ans);
    	if (ans.equals(NEXT_QUESTION)) {
    		return "ActionSummary";
    	} else {
    		return "SelectTemplate";
    	}
    }
    
    @Override
    public boolean lineWrap() {
        return false;
    }
    
    @Override
    public String lastAction() {
    	if (actionType.equals(FSGXLS)) {
          return "OutputFile";
    	}
    	
    	if (actionType.equals(XLS_ONLY)) {
            return "SelectXMLFile";
      	}
    	
    	return "SelectTemplate";
    }

}
