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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/RunFSGAction.java $
 * $Author: Christopher Ho $
 * $Date: 3/29/17 6:46a $
 * $Revision: 4 $
******************************************************************************/

package symbolthree.oracle.fsg;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.flower.Answer;

public class RunFSGAction extends MyFSGActionBase {

	private String currDir = System.getProperty("user.dir");
	private String fileSep = File.separator;
	private String pathSep = File.pathSeparator;
    private ArrayList<String> cmdArgs  = new ArrayList<String>();
    
	private Hashtable<String, String> systemEnv = new Hashtable<String, String>();
	private File fsgXMLFile = null;   
	private File fsgXMLLog  = null;
	private int exitVal = 0;
	private String actionType = null;
	private String appMode = null;
	
    static final Logger logger = LogManager.getLogger(RunFSGAction.class.getName());
    public static final String RCS_ID = "$Header: /TOOL/myMSG/src/symbolthree/oracle/fsg/RunFSGAction.java 4     3/29/17 6:46a Christopher Ho $";	
    
	public RunFSGAction() {
		appMode = Answer.getInstance().getB(APP_MODE);				
		actionType = Answer.getInstance().getA("ActionType");
	}
    
	@Override
    public void execute() {
		if (appMode.equals(CLIENT_MODE)) {
		  this.setSystemParameters();
		}
		
		if (appMode.equals(SERVER_MODE)) {
			String rgTop = System.getenv("RG_TOP");
			setExecutable(rgTop);
		}
		
        ArrayList<String> al = RGRARG.init().createCommand();
        Iterator<String> cmds = al.iterator();
        String cmd = null;
        int pos = 0;
        String quote = isWindows()?"\"":"";
        while (cmds.hasNext()) {
        	//add quote for all arguments except for the executable in Windows environment 
        	cmd = cmds.next();
        	if (pos > 0) {
        	  cmd = quote + cmd + quote;        	
        	}
      	    cmdArgs.add(cmd);
        	pos++;
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmdArgs);
        
        logger.debug(pb.command().toString());
        
        if (appMode.equals(CLIENT_MODE)) {
        
	        Map<String, String> envVars = pb.environment();        
	        envVars.clear();
	        envVars.putAll(systemEnv);
	        
	        logger.debug(">>> Environment Variables start <<<");
	        // show all systemEnv variables for debug
	        Set<String> envSet = systemEnv.keySet();
	        Iterator<String> itr    = envSet.iterator();        
	        while (itr.hasNext()) {
	            String envKey = itr.next();
	            logger.debug(envKey + "=" + systemEnv.get(envKey));
	        }        
	        
	        logger.debug(">>> Environment Variables end <<<");
        }
        
        File logDir = new File(Instances.getInstance().getXMLDataDirectory());

        // exec.setWorkingDirectory(logDir);
        pb.directory(logDir);
        logger.debug("log directory=" + logDir.getAbsolutePath());
        pb.redirectErrorStream(true);

        // Helper.log(Helper.LOG_DEBUG, "Executable=" + cl.getExecutable());
        // Helper.log(Helper.LOG_DEBUG, "Arguments=" + maskPassword(Arrays.toString(cl.getArguments())));
        logger.debug("RGRARG start...");

        try {
	        Process        shell  = pb.start();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getInputStream()));
	        String         line;
	
	        while ((line = reader.readLine()) != null) {
	        	line = line.trim();
	            logger.info("Output >> " + line);
	            if (line.startsWith("Report filename")) {
	            	String f = line.split(":")[1].trim();
	            	fsgXMLFile = new File(logDir, f);
	            } 
	            if (line.startsWith("Log filename")) {
	            	String f = line.split(":")[1].trim();
	            	fsgXMLLog = new File(logDir, f);
	            } 
	        }
	
	        exitVal = shell.waitFor();
	        reader.close();
	        shell.destroy();
	        
	        logger.debug("Process exit code=" + exitVal);
	        
	        if (exitVal < 0) {
	        	throw new MyFSGException("FSG program error, exit code=" + exitVal);
	        }
	        
	        String fsgXMLFilePath = fsgXMLFile.getAbsolutePath();
	        String fsgXMLLogPath  = fsgXMLLog.getAbsolutePath();
	        
	        logger.debug("fsgXMLFile=" + fsgXMLFilePath);
	        
	        if (fsgXMLFile.exists()) {
		        Answer.getInstance().putB(FSG_XML_FILE, fsgXMLFilePath);
		        Answer.getInstance().putB(FSG_XML_LOG, fsgXMLLogPath);
	        } else {
	        	logger.error("Unable to create FSG XML file " + fsgXMLFilePath);
	        }
        } catch (Exception e) {
        	logger.catching(e);
        }
        
        // keep log file if error
        if (exitVal >= 0) {
          File f1 = new File(logDir, "startcp.log");
          if (f1.exists()) f1.delete();
          File f2 = new File(logDir, "startcp.out");
          if (f2.exists()) f2.delete();
        }
        
	}
	
    @Override
    public String nextAction() {
    	if (exitVal==0) {
          return "FSG2Excel";
    	} else {
    	 return "PostProcess";	
    	}
    } 
    
	private void setSystemParameters() {
        Map<String, String> origSystemEnv = System.getenv();

        // fill the running env. variables with the orig. values, using variable name in uppercase
        Set<String>      envSet = origSystemEnv.keySet();
        Iterator<String> itr    = envSet.iterator();

        while (itr.hasNext()) {
            String envKey = itr.next();
            systemEnv.put(envKey.toUpperCase(), origSystemEnv.get(envKey));
        }
        
        String releaseName = Answer.getInstance().getB(RELEASE_NAME);
        
        // ORACLE_HOME
        String oracleHome = currDir + fileSep + "CLIENT" + fileSep + releaseName + fileSep + "ORACLE_HOME";
        
        systemEnv.put("ORACLE_HOME", oracleHome);
        
        // NLS files
        if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
            systemEnv.put("ORA_NLS10", oracleHome + fileSep + "nls" + fileSep + "data" + fileSep + "9idata");
        }
        
        if (releaseName.equals(RELEASE_11i)) {
            if (isWindows()) {
                systemEnv.put("NLSRTL33", oracleHome + fileSep + "NLSRTL33");
                systemEnv.put("ORA_NLS33", oracleHome + fileSep + "NLSRTL33" + fileSep + "DATA");
            }

            if (isUnix()) {
                systemEnv.put("ORA_NLS",
                              oracleHome + fileSep + "ocommon" + fileSep + "nls" + fileSep + "admin" + fileSep
                              + "data");
            }
        }
        
        systemEnv.put("TNS_ADMIN", System.getProperty("oracle.net.tns_admin"));
        
        systemEnv.put("APPLMSG", "mesg");
        
        String origPath = systemEnv.get("PATH");

        String binPath = releaseName.equals(RELEASE_11i)?"11.5.0":"12.0.0";
        
        String applTop =  currDir + fileSep + "CLIENT" + fileSep + releaseName + fileSep + "APPL_TOP";
        String fndTop  = applTop + fileSep + "fnd" + fileSep + binPath; 
        String glTop   = applTop + fileSep + "gl" + fileSep +  binPath;
        String rgTop   = applTop + fileSep + "rg" + fileSep +  binPath; 

        setExecutable(rgTop);
        
        systemEnv.put("FND_TOP", fndTop);
        systemEnv.put("GL_TOP", glTop);
        systemEnv.put("RG_TOP", rgTop);
        
        // add ORACLE_HOME\bin, GL_TOP\bin, FND_TOP\bin and RG_TOP\bin to PATH
        String path = oracleHome + fileSep + "bin" + pathSep + 
        	          fndTop + fileSep + "bin" + pathSep + 
        	          glTop  + fileSep + "bin" + pathSep + 
        	          //rgTop  + fileSep + "bin" + pathSep +
        	          origPath;
        systemEnv.put("PATH", path);
	}
	
	
	private void setExecutable(String rgTop) {
        String executable = RGRARG.init().getValue(RGRARG.RGRARG);
        if (isWindows()) executable = executable + ".exe";
        
        logger.debug("executable=" + executable);
        RGRARG.init().setValue(RGRARG.RGRARG, rgTop + fileSep + "bin" + fileSep + executable);
	}
	
    private boolean isWindows() {
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnix() {
        if ((System.getProperty("os.name").toUpperCase().indexOf("NIX") >= 0)
                || (System.getProperty("os.name").toUpperCase().indexOf("NUX") >= 0)) {
            return true;
        } else {
            return false;
        }
    }	
}
