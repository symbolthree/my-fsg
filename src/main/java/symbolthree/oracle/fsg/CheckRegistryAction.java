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

import symbolthree.calla.Answer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CheckRegistryAction extends MyFSGActionBase {

	private String  actionType             = null;
    private String  nls_date_format        = null;
    private String  nls_lang               = null;
    private String  nls_numeric_characters = null;
    private String  nls_sort               = null;
    private String  releaseNameInRegistry  = null;
    private int     jvmBit                 = WinRegistry.KEY_WOW64_32KEY;
    private String  windowsBitKey           = "";

    static final Logger logger = LogManager.getLogger(CheckRegistryAction.class.getName());
    
    public CheckRegistryAction() {
    	actionType = Answer.getInstance().getA("ActionType");
    	logger.debug("actionType=" + actionType);
    }
    
    @Override
    public boolean enterAction() {
    	if (actionType.equals(SERVER_MODE) || actionType.equals(XLS_ONLY)) {
    		return false;
    	}
    	
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
        	String bitVal = System.getProperty("sun.arch.data.model");
        	logger.debug("JVM Version=" + bitVal);
        	
        	if (bitVal.equals("32")) jvmBit = WinRegistry.KEY_WOW64_32KEY;
        	if (bitVal.equals("64")) jvmBit = WinRegistry.KEY_WOW64_64KEY;
        	
        	String winBitVal = System.getProperty("os.arch");
        	logger.debug("Windows Version=" + winBitVal);
        	
        	if (winBitVal.equals("amd64")) windowsBitKey = "Wow6432Node\\";
        	
            String releaseName     = Answer.getInstance().getB(RELEASE_NAME);
            
            if (releaseName.startsWith("12.")) {
            	releaseNameInRegistry = RELEASE_12;
            } else if (releaseName.startsWith("11.5")) {
            	releaseNameInRegistry = RELEASE_11i;
            } else {
            	releaseNameInRegistry = "UNKNOWN";
            }            

            nls_lang               = Answer.getInstance().getB(NLS_LANG);
            nls_sort               = Answer.getInstance().getB(NLS_SORT);
            nls_date_format        = Answer.getInstance().getB(NLS_DATE_FORMAT);
            nls_numeric_characters = Answer.getInstance().getB(NLS_NUMERIC_CHARACTERS);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute() {
        try {
            createRegistry();
            setNLSValues();
        } catch (Exception e) {
           logger.error("Registry Error", e);
        }
    }

    private void setNLSValues() throws Exception {
    	String rootKey = "SOFTWARE\\" + windowsBitKey + "ORACLE\\APPLICATIONS\\" + 
                         releaseNameInRegistry + "\\" + APPL_CONFIG_WIN_KEY;
    	
    	WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, rootKey);
    	
    	WinRegistry.writeStringValue(
    			WinRegistry.HKEY_LOCAL_MACHINE, rootKey, "NLS_LANG", nls_lang, jvmBit);

    	WinRegistry.writeStringValue(
    			WinRegistry.HKEY_LOCAL_MACHINE,	rootKey, "NLS_SORT", nls_sort, jvmBit);

    	WinRegistry.writeStringValue(
    			WinRegistry.HKEY_LOCAL_MACHINE,	rootKey, "NLS_NUMERIC_CHARACTERS", nls_numeric_characters, jvmBit);

    	WinRegistry.writeStringValue(
    			WinRegistry.HKEY_LOCAL_MACHINE,	rootKey, "NLS_DATE_FORMAT", nls_date_format, jvmBit);

    	logger.debug("Registry keys saved");
    }

    private boolean createRegistry() {

    	String rootKey = "SOFTWARE\\" + windowsBitKey + "ORACLE\\APPLICATIONS\\" + releaseNameInRegistry;
    	try {
       
    		logger.debug("checking " + rootKey + "\\APPL_CONFIG");
        	
	    	String regValue = WinRegistry.readString(
	    			WinRegistry.HKEY_LOCAL_MACHINE, rootKey, "APPL_CONFIG", jvmBit);
	
	        if (regValue == null || regValue.equals("")) {
	        	WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, rootKey);
	        	WinRegistry.writeStringValue(
	        			WinRegistry.HKEY_LOCAL_MACHINE, rootKey, "APPL_CONFIG", APPL_CONFIG_WIN_KEY, jvmBit);
	        	
	        	logger.debug("Registry keys created");
	            return true;            
	        } else {
	        	logger.debug("Registry keys exist");  
	            return false;	            
	        }
    	} catch (Exception e) {
    		logger.error(e.getMessage());
            return false;    		
    	}
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
