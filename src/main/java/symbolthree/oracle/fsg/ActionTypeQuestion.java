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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/ActionTypeQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 1/16/17 8:07a $
 * $Revision: 3 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;

public class ActionTypeQuestion extends MyFSGQuestion {

    ArrayList<Choice> al = new ArrayList<Choice>();
    static final Logger logger = LogManager.getLogger(ActionTypeQuestion.class.getName());
    public static final String RCS_ID = "$Header: /TOOL/myMSG/src/symbolthree/oracle/fsg/ActionTypeQuestion.java 3     1/16/17 8:07a Christopher Ho $";
    
	public ActionTypeQuestion() {
	}
	
	
    @Override
    public String getQuestion() {
    	return "Select which action you want to perform";
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
    	al.add(new Choice(FSGXLS,   "Create FSG Excel report"));
    	al.add(new Choice(FSG_ONLY, "Generate FSG data only"));
    	al.add(new Choice(XLS_ONLY, "Convert FSG Data to Excel file"));
        return al;
    }
    
    @Override
    public String nextAction() {
    	String ans = Answer.getInstance().getA(this);
  	    if (ans.equals(FSGXLS)) return  "SelectInstance";
  	    if (ans.equals(FSG_ONLY)) return "SelectInstance";
  	    if (ans.equals(XLS_ONLY)) return "SelectXMLFile";
  	    return "SelectInstance";
    }	    
    
    @Override
    public String getTitle() {
        InputStream is   = this.getClass().getResourceAsStream("/build.properties");
        Properties  prop = new Properties();
        try {
            prop.load(is);
            is.close();
        } catch (Exception e) {}

        String ver = prop.getProperty("build.version", PROGRAM_VERSION) + " build " + prop.getProperty("build.number");
    	
        return "myFSG " + ver + " [" + Answer.getInstance().getB(APP_MODE) + "]";
    }    

}
