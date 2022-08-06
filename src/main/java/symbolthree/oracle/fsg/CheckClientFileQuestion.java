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

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;

public class CheckClientFileQuestion extends MyFSGQuestion {

	private String releaseName = null;
	private boolean dirExist   = false;
	private String appMode     = null;
	
    static final Logger logger = LogManager.getLogger(CheckClientFileQuestion.class.getName());
    
    
	public CheckClientFileQuestion() {
		appMode = Answer.getInstance().getB(APP_MODE);		
	}

    @Override
    public boolean enterQuestion() {
        try {
	        String sid = Answer.getInstance().getA("SelectInstance");
			releaseName = Instances.getInstance().getReleaseName(sid);
			logger.debug("Release Name=" + releaseName);
			// set releaseName Answer
			Answer.getInstance().putB(RELEASE_NAME, releaseName);
			
			// skip checking if Server Mode
	    	if (appMode.equals(SERVER_MODE)) {
	    		return false;
	    	}
	    	
			File clientDir = new File(System.getProperty("user.dir") + File.separator + "CLIENT" + File.separator + releaseName);
			if (clientDir.isDirectory() && clientDir.exists()) {
				logger.debug("Client directory exists");
				dirExist = true;
			} else {
				logger.debug("Client directory does NOT exist");
			}
			
			//Get NLS Info
			logger.debug("checking NLS info...");
			Instances.getInstance().getNLSInfo();
			
		} catch (Exception e) {
			logger.catching(e);
		}
    	return ! dirExist;
    }	
    
    
    @Override
    public String getQuestion() {
    	return "MyFSG Client file for EBS release " + releaseName + " does not exist.\n";
    }
    
    @Override
    public boolean isMultipleChoices() {
        return true;
    }   
    
    @Override
    public String getExplanation() {
    	return "Please download file " + releaseName + ".zip from \r" +
    			DOWNLOAD_CLIENT_URL + "\n\n" +
    			"Unzip this file to " + System.getProperty("user.dir") + File.separator + "CLIENT \n" +
    			"then restart the program.";
    }
    
    @Override
    public String nextAction() {
    	if (! dirExist && appMode.equals(CLIENT_MODE)) {
	    	if (Answer.getInstance().getA(this).equals("A")) {
	    		try {
					Desktop.getDesktop().browse(new URI(DOWNLOAD_CLIENT_URL));
				} catch (Exception e) {
					logger.catching(e);
				}
	    	}
	    	return "CheckClientFile";
    	}    	
        // store userid
        String userID = "apps/" + 
                        Answer.getInstance().getA("DatabaseCredential") + 
                        "@" + 
                        Answer.getInstance().getA("SelectInstance");
        
        logger.debug("userID=" + maskPassword(userID));
        RGRARG.init().setValue(RGRARG.USERID, userID);    	

    	return "SelectResp";
    }   
    
    @Override
    public ArrayList<Choice> choices() {
        ArrayList<Choice> al = new ArrayList<Choice>();
        al.add(new Choice("A", "Open web site for download"));

        return al;
    }
    
    private String maskPassword(String line) {
        String          output = "";
        StringTokenizer st     = new StringTokenizer(line, " ");

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (token.indexOf("apps/") >= 0) {
                String sid = token.split("@")[1];

                output = output + " apps/*******@" + sid;
            } else {
                output = output + " " + token;
            }
        }

        return output;
    }    
}
