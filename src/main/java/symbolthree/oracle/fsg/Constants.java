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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/Constants.java $
 * $Author: Christopher Ho $
 * $Date: 2/21/17 4:36a $
 * $Revision: 4 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.io.File;

public interface Constants {
	
    public static final String PROGRAM_VERSION           = "1.0";
    // internal properties
    
    public static final String APP_MODE                  = "APP_MODE";
    
    // action type
    public static final String FSGXLS                    = "FSGXLS";
    public static final String FSG_ONLY                  = "FSG_ONLY";
    public static final String XLS_ONLY                  = "XLS_ONLY";
    
    
    public static final String CHANGE_DIRECTORY          = "CHANGE_DIRECTORY";
    public static final String CHANGE_FILE               = "CHANGE_FILE";
    public static final String CHANGE_TEMPLATE           = "CHANGE_TEMPLATE";
    public static final String NEXT_QUESTION             = "NEXT_QUESTION";
    public static final String LAST_CHOICE               = "LAST_CHOICE";
    
    public static final String CLIENT_MODE               = "Standalone Version";

    public static final String MYFSG_APPLICATION_DIR     = System.getProperty("user.dir") + File.separator + "config";
    
    public static final String CONFIG_FILENAME           = "myFSG.xml";
    public static final int    ENCRYPTED_PASSWORD_LENGTH = 100;
    public static final String PASSWORD_ATTEMPT          = "PASSWORD_ATTEMPT";
    public static final String PASSWORD_SEED             = "SYMBOLTHREE";
    public static final String ASK_PASSWORD              = "ASK_PASSWORD";
    
    public static final String REENTER                   = "REENTER";
    
    public static final String APPL_CONFIG_WIN_KEY       = "SYMBOLTHREE";
    
    public static final String INSTANCES_FILENAME        = "INSTANCES.xml";
    
    public static final String OUTPUT_ACTION             = "OUTPUT_ACTION";
    
    public static final String DATA_DIR                  = "DATA_DIR";    
    public static final String DATA_FILENAME             = "DATA_FILENAME";
    
    public static final String OUTPUT_DEFAULT_DIR        = System.getProperty("user.dir") + File.separator + "output";
    public static final String OUTPUT_DIR                = "OUTPUT_DIR";
    public static final String OUTPUT_FILENAME           = "OUTPUT_FILENAME";
    
    public static final String TEMPLATE_DIR              = "TEMPLATE_DIR";
    public static final String TEMPLATE_FILENAME         = "TEMPLATE_FILENAME";
    
    		
    public static final String FSG_XML_FILE              = "FSG_XML_FILE";
    public static final String FSG_XML_LOG               = "FSG_XML_LOG";
    
    public static final String LOG_DIR                   = "LOG_DIR";
    
    public static final int    MAX_PASSWORD_ATTEMPT      = 3;
    
    public static final String DOWNLOAD_CLIENT_URL       = "http://symbolthree.com/home/products/myfsg/";
    
    // internal values
    public static final String RELEASE_11i         = "11.5.0";
    public static final String RELEASE_12          = "12.0.0";    
    public static final String RELEASE_121         = "12.1.0";
    public static final String RELEASE_122         = "12.2.0";
    public static final String RELEASE_NAME        = "RELEASE_NAME";
    public static final String SELECT_FILE         = "SELECT_FILE";
    public static final String SERVER_MODE         = "Server Addon Version";
    public static final String TNSNAMES            = "tnsnames.ora";
    public static final String TNS_INSTANCE_PARAMS = "TNS_INSTANCE_PARAMS";
    
    // NLS parameters
    public static final String NLS_DATE_FORMAT           = "NLS_DATE_FORMAT";
    public static final String NLS_INSTALLED             = "NLS_INSTALLED";
    public static final String NLS_LANG                  = "NLS_LANG";
    public static final String NLS_NUMERIC_CHARACTERS    = "NLS_NUMERIC_CHARACTERS";
    public static final String NLS_SESSION_LANG          = "NLS_SESSION_LANG";
    public static final String NLS_SORT                  = "NLS_SORT";
    public static final String NLS_TRANSLATION           = "NLS_TRANSLATION";
    
    // FSG2XLSX settings
    public static final String DEFAULT_TEMPLATE    = System.getProperty("user.dir") + File.separator + 
    		                                         "template" + File.separator + "default.xlsx";
    
}


