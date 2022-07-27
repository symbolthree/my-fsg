/******************************************************************************
 *
 * ≡≡ FNDLOADER ≡≡
 * Copyright (C) 2009-2016 Christopher Ho
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
 * $Archive: /TOOL/myFSG/src/symbolthree/oracle/fsg/SaveInstanceAction.java $
 * $Author: Christopher Ho $
 * $Date: 10/20/17 11:48a $
 * $Revision: 4 $
******************************************************************************/


package symbolthree.oracle.fsg;

import symbolthree.flower.Answer;
import symbolthree.oracle.fsg.Instances;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SaveInstanceAction extends MyFSGActionBase {
	private String actionType = null;
    public static final String RCS_ID =
        "$Header: /TOOL/myFSG/src/symbolthree/oracle/fsg/SaveInstanceAction.java 4     10/20/17 11:48a Christopher Ho $";
    static final Logger logger = LogManager.getLogger(SaveInstanceAction.class.getName());
    
    public SaveInstanceAction() {
    	actionType = Answer.getInstance().getA("ActionType");
    }
    
    @Override
    public void execute() {
    	Instances.getInstance().getXMLDataDirectory();  // initialize the DATA_DIR value if not set
    	
        Instances.getInstance().saveDirectory();
        if (! actionType.equals(XLS_ONLY)) {
          Instances.getInstance().saveTNSAdminDirectory();
          Instances.getInstance().saveInstanceInfo();
        }
        Instances.getInstance().saveInstancesFile();        
    }

    @Override
    public String nextAction() {
    	if (! actionType.equals(XLS_ONLY)) {
    	  return "RunFSG";
    	} else {
    	  return "FSG2Excel";
    	}
    }
}

