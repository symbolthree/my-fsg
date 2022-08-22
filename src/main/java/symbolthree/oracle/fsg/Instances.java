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
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Map;

import oracle.net.nl.NLException;
import oracle.net.nl.NLParamParser;

import org.apache.commons.io.FileUtils;
//import org.apache.commons.exec.environment.EnvironmentUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import symbolthree.calla.Answer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Instances implements Constants {
  private static Document    document    = null;
  private static Instances   myInstances = null;
  private File               instancesFile;
 
  private XPathFactory  xpfac       = XPathFactory.instance();
  private static String ATTRIBUTE_NODE = "A";
  private static String ELEMENT_NODE   = "E";  
  private String tnsnamesFile          = null;
  
  static final Logger logger = LogManager.getLogger(Instances.class.getName());
  
  protected Instances() {
  	System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
  	 
      try {
          SAXBuilder builder = new SAXBuilder();

          instancesFile = new File(MYFSG_APPLICATION_DIR, INSTANCES_FILENAME);
          
          if (!instancesFile.exists()) {
            // create a new INSTANCES.XML
            Element root = new Element("myFSG");
            document = new Document(root);
            
            Element tnsAdmin = new Element("TNS_ADMIN");
            //tnsAdmin.setText(System.getProperty("user.dir"));
            root.addContent(tnsAdmin);

            Element dataDir = new Element("DATA_DIR");
            root.addContent(dataDir);
            
            Element tempDir = new Element("TEMPLATE_DIR");
            root.addContent(tempDir);            

            Element outDir = new Element("OUTPUT_DIR");
            root.addContent(outDir);
            
          } else {
            document = builder.build(instancesFile);
          }
      } catch (JDOMException je) {
          logger.catching(je);
          
      } catch (IOException ioe) {
          logger.catching(ioe);;
      }
  }

  public static Instances getInstance() {
      if (myInstances == null) {
          myInstances = new Instances();
      }

      return myInstances;
  }

  public void saveTNSAdminDirectory() {
      String dir = System.getProperty("oracle.net.tns_admin");
      XPathExpression<Element> xp = xpfac.compile("/myFSG/TNS_ADMIN", Filters.element());
      Element ele = xp.evaluateFirst(document);
      ele.setText(dir);
  }
  
  public void saveInstancesFile() {
      try {
          XMLOutputter outputter = new XMLOutputter();

          outputter.setFormat(Format.getPrettyFormat());
          outputter.output(document, new FileOutputStream(instancesFile));
      } catch (IOException e) {
          logger.error("Unable to save instances file " + instancesFile.getAbsolutePath());
      }
  }

  public String getXMLDataDirectory() {
		String dir = Answer.getInstance().getB(DATA_DIR);
	    if (dir != null && ! dir.equals("")) return dir;

		try {
		    boolean checked = false;
		
		    dir   = this.getSingleXPathValue("/myFSG/DATA_DIR", ELEMENT_NODE);
		    
		    if (dir == null || dir.equals("")) {
			    logger.info("data dir has not set yet, set to default");
			    dir = OUTPUT_DEFAULT_DIR;
		    }
		    
		    File test = new File(dir);
		
		    if (test.exists() && test.isDirectory()) {
		        dir     = test.getAbsolutePath();
			    logger.debug("Use directory " + dir);
		        checked = true;
		    }
		
		    if (!checked) {
			  logger.debug("Create directory " + test.getAbsolutePath());
		      FileUtils.forceMkdir(test);
		    }
		
		     Answer.getInstance().putB(DATA_DIR, dir);
		     return dir;
		
		} catch (Exception e) {
			dir = System.getProperty("user.dir");
			Answer.getInstance().putB(TEMPLATE_DIR, dir);
		}
		
		return dir;
  }
  
  public String getTemplateDirectory() {
	String dir = Answer.getInstance().getB(TEMPLATE_DIR);
    if (dir != null && ! dir.equals("")) return dir;

	try {
	    boolean checked = false;
	
	    dir   = this.getSingleXPathValue("/myFSG/TEMPLATE_DIR", ELEMENT_NODE);
	    
	    if (dir == null || dir.equals("")) {
		    logger.info("Template dir has not set yet, set to default");
		    dir = System.getProperty("user.dir") + File.separator + "template";
	    }
	    
	    File test = new File(dir);
	
	    if (test.exists() && test.isDirectory()) {
	        dir     = test.getAbsolutePath();
		    logger.debug("Use directory " + dir);
	        checked = true;
	    }
	
	    if (!checked) {
		  logger.debug("Create directory " + test.getAbsolutePath());
	      FileUtils.forceMkdir(test);
	    }
	
	     Answer.getInstance().putB(TEMPLATE_DIR, dir);
	     return dir;
	
	} catch (Exception e) {
		dir = System.getProperty("user.dir");
		Answer.getInstance().putB(TEMPLATE_DIR, dir);
	}
	
	return dir;
	  
  }
  
  public String getFileDirectory() {
	String dir = Answer.getInstance().getB(OUTPUT_DIR);

    if (dir != null && ! dir.equals("")) {
    	return dir;
    }
	
	try {
	    boolean checked = false;
	
	    dir   = this.getSingleXPathValue("/myFSG/OUTPUT_DIR", ELEMENT_NODE);
	    
	    if (dir == null || dir.equals("")) {
		    logger.debug("Output dir has not set yet, set to default");
		    dir = OUTPUT_DEFAULT_DIR;
	    }
	    
	    File test = new File(dir);
	
	    if (test.exists() && test.isDirectory()) {
	        dir     = test.getAbsolutePath();
		    logger.debug("Use directory " + dir);
	        checked = true;
	    }
	
	    if (!checked) {
		  logger.debug("Create directory " + test.getAbsolutePath());
	      FileUtils.forceMkdir(test);
	    }
	
	     Answer.getInstance().putB(OUTPUT_DIR, dir);
	     return dir;
	} catch (Exception e) {
		dir = System.getProperty("user.dir");
		Answer.getInstance().putB(OUTPUT_DIR, dir);
	}
	return dir;
  }  
  
  public void saveDirectory() {
	  String dir = null;
	  XPathExpression<Element> xp = null;
	  Element ele = null;
	  
      dir = Answer.getInstance().getB(OUTPUT_DIR);
      xp = xpfac.compile("/myFSG/OUTPUT_DIR", Filters.element());
      ele = xp.evaluateFirst(document);
      ele.setText(dir);
      logger.debug("OUTPUT_DIR=" + dir);
      
      dir = Answer.getInstance().getB(DATA_DIR);
      xp = xpfac.compile("/myFSG/DATA_DIR", Filters.element());
      ele = xp.evaluateFirst(document);
      ele.setText(dir);	  
      logger.debug("DATA_DIR=" + dir);      
      
      dir = Answer.getInstance().getB(TEMPLATE_DIR);
      xp = xpfac.compile("/myFSG/TEMPLATE_DIR", Filters.element());
      ele = xp.evaluateFirst(document);
      ele.setText(dir);
      logger.debug("TEMPLATE_DIR=" + dir);      
  }
  
 
  public void saveInstanceInfo() {
      String instance    = Answer.getInstance().getA("SelectInstance");
      String password    = Answer.getInstance().getA("DatabaseCredential");
      String respKey     = Answer.getInstance().getA("SelectResp");
      String releaseName = Answer.getInstance().getB(RELEASE_NAME);

      XPathExpression<Element> xp = xpfac.compile("/myFSG/INSTANCE[@SID='" + instance + "']", Filters.element());

      if (xp.evaluate(document).size() == 0) {
          logger.debug("Instance node not defined for " + instance);

          Element newEle = new Element("INSTANCE");

          newEle.setAttribute("SID", instance);

          Element ele1 = new Element(RELEASE_NAME);

          ele1.setText(releaseName);

          Element ele2 = new Element("PASSWORD");

          ele2.setText(Security.getInstance().encryptPwd(password));

          Element ele3 = new Element("RESP_KEY");

          ele3.setText(respKey);
          
          newEle.addContent(ele1);
          newEle.addContent(ele2);
          newEle.addContent(ele3);          

          xp = xpfac.compile("/myFSG", Filters.element());
          Element root = xp.evaluateFirst(document);
          root.addContent(newEle);
          
      } else {
    	  
          XPathExpression<Element> xp2 = xpfac.compile("/myFSG/INSTANCE[@SID='" + instance + "']/PASSWORD", Filters.element());
          
          Element ele             = xp2.evaluateFirst(document);
          String  oldEncryptedPwd = ele.getValue();

          if (!Security.getInstance().decryptPwd(oldEncryptedPwd).equals(password)) {
              ele.setText(Security.getInstance().encryptPwd(password));
          }
          
          xp2 = xpfac.compile("/myFSG/INSTANCE[@SID='" + instance + "']/RESP_KEY", Filters.element());
          ele = xp2.evaluateFirst(document);
          ele.setText(respKey);
      }
  }

  

  public String getPassword(String sid) {
      XPathExpression<Element> xp = xpfac.compile("/myFSG/INSTANCE[@SID='" + sid + "']/PASSWORD", Filters.element());    	  

      Element ele = xp.evaluateFirst(document);
      String  pwd = null;

      if (ele != null) {
          pwd = ele.getValue();
      } else {
        return null;
      }

      if ((pwd != null) && (pwd.length() == ENCRYPTED_PASSWORD_LENGTH)) {
          return Security.getInstance().decryptPwd(pwd);
      } else {
          return pwd;
      }
  }
  
  
  public Hashtable<String, String> getAllTNSEntries() throws MyFSGException {
	  String appMode = Answer.getInstance().getB(APP_MODE);
	  
	  if (appMode.equals(CLIENT_MODE)) {
		  return getClientModeAllTNSEntries();
	  } else if (appMode.equals(SERVER_MODE)) {
		  return getServerModeAllTNSEntries();
	  }
	  return null;
  }
  
  
  private Hashtable<String, String> getServerModeAllTNSEntries() throws MyFSGException {
	Hashtable<String, String> ht      = new Hashtable<String, String>();	  
    String dirName    = "";
    String tnsNameLoc = "";

    dirName = System.getenv().get("TNS_ADMIN");
    logger.debug("TNS_ADMIN=" + dirName);
    File file;    
    if (dirName != null) {
    	file = new File(dirName, TNSNAMES);
    	if (file.exists()) {
          tnsnamesFile = file.getAbsolutePath();
          tnsNameLoc   = dirName;
          logger.debug(file.getAbsolutePath() + " exists");
        } else {
          throw new MyFSGException(file.getAbsolutePath() + " does not exist");
        }
    	
    } else {
    	throw new MyFSGException("System variable TNS_ADMIN is not defined");
    }

    System.setProperty("oracle.net.tns_admin", tnsNameLoc);
    logger.debug("Reading " + tnsnamesFile + "...");

    NLParamParser parser = null;

    try {
      parser = new NLParamParser(tnsnamesFile);
    } catch (NLException nle) {
        throw new MyFSGException("Error in reading " + tnsnamesFile);
    } catch (IOException ioe) {
        // should not throw in here
    }

    String[] allEntries = parser.getNLPAllElements();
    String[] allNames = parser.getNLPAllNames();

    for (int i = 0; i < allNames.length; i++) {
        ht.put(allNames[i], allEntries[i]);
    }

    return ht;
  }
  
  @SuppressWarnings("rawtypes")
  private Hashtable<String, String> getClientModeAllTNSEntries() throws MyFSGException {
    Hashtable<String, String> ht      = new Hashtable<String, String>();
    Map                       envVars = null;
    String                    sep     = System.getProperty("file.separator");

    try {
        envVars = System.getenv();
    } catch (Exception ioe) {
        throw new MyFSGException("Unable to get environmnent variables.");
    }

    /*
     *  get the directory which has tnsnames.ora from
     * (1) System variable TNS_NAME (in server mode), if not present
     * (2) %ORACLE_HOME%\network\admin\, if not present
     * (2) Config file //TNS_ADMIN, if not present
     * (3) current directory
     */
    tnsnamesFile      = null;
    String dirName    = "";
    String tnsNameLoc = "";

    dirName = (String) envVars.get("TNS_ADMIN");
    logger.debug("TNS_ADMIN=" + dirName);
    
    File file;

    if (dirName != null) {
    	file = new File(dirName, TNSNAMES);
    	if (file.exists()) {
          tnsnamesFile = file.getAbsolutePath();
          tnsNameLoc   = dirName;
          logger.debug(file.getAbsolutePath() + " exists");
        } else {
          logger.debug(file.getAbsolutePath() + " does not exist");        	
      }
    }

	if (tnsnamesFile == null) {
	    String oracleHome = (String) envVars.get("ORACLE_HOME");
	    logger.debug("ORACLE_HOME=" + oracleHome);
	    
	    if (oracleHome != null) {
    	  dirName = oracleHome + sep + "NETWORK" + sep + "ADMIN";
          file = new File(dirName, TNSNAMES);    	
          if (file.exists()) {
              tnsnamesFile = file.getAbsolutePath();
              tnsNameLoc   = dirName;
              logger.debug(file.getAbsolutePath() + " exists");
          } else {
            logger.debug(file.getAbsolutePath() + " does not exist");
          }
	    }
    }
	
	// try lowercase path
	if (tnsnamesFile == null) {
	    String oracleHome = (String) envVars.get("ORACLE_HOME");
	    logger.debug("ORACLE_HOME=" + oracleHome);
	    if (oracleHome != null) {
    	  dirName = oracleHome + sep + "network" + sep + "admin";
          file = new File(dirName, TNSNAMES);    	
          if (file.exists()) {
              tnsnamesFile = file.getAbsolutePath();
              tnsNameLoc   = dirName;
              logger.debug(file.getAbsolutePath() + " exists");
          } else {
            logger.debug(file.getAbsolutePath() + " does not exist");
          }
	    }
    }
	
	
    if (tnsnamesFile == null) {
    	dirName = System.getProperty("user.dir");
        file = new File(dirName, TNSNAMES);    	
        if (file.exists()) {
            tnsnamesFile = file.getAbsolutePath();
            tnsNameLoc   = dirName;            
            logger.debug(file.getAbsolutePath() + " exists");
        } else {
            logger.debug(file.getAbsolutePath() + " does not exist");
        }
    }
	
    if (tnsnamesFile == null) {
        dirName = getSingleXPathValue("//TNS_ADMIN", ELEMENT_NODE);
        logger.debug("//TNS_ADMIN=" + dirName);
        
        if ((dirName != null) &&!dirName.equals("")) {
            file = new File(dirName, TNSNAMES);
            if (file.exists()) {
                tnsnamesFile = file.getAbsolutePath();
                tnsNameLoc   = dirName;                
                logger.debug(file.getAbsolutePath() + " exists");
            } else {
                logger.debug(file.getAbsolutePath() + " does not exist");
            }
        }
    }

    if (tnsnamesFile == null) {
        throw new MyFSGException("Unable to find " + TNSNAMES);
    }

    System.setProperty("oracle.net.tns_admin", tnsNameLoc);
    logger.debug("Reading " + tnsnamesFile + "...");

    NLParamParser parser = null;

    try {
      parser = new NLParamParser(tnsnamesFile);
    } catch (NLException nle) {
        throw new MyFSGException("Error in reading " + tnsnamesFile);
    } catch (IOException ioe) {
        // should not throw in here
    }

    String[] allEntries = parser.getNLPAllElements();
    String[] allNames = parser.getNLPAllNames();

    for (int i = 0; i < allNames.length; i++) {
        ht.put(allNames[i], allEntries[i]);
    }

    return ht;
  }
  
  
  public String getTNSNameFile() {
	  return this.tnsnamesFile;
  }

  private String getSingleXPathValue(String xpath, String nodeType) {
  	String rtnVal = null;
  	
  	if (nodeType.equals(ATTRIBUTE_NODE)) {
        XPathExpression<Attribute> xp = xpfac.compile(xpath, Filters.attribute());
        Attribute attr = xp.evaluateFirst(document);
        if (attr != null) rtnVal = attr.getValue();
  	} 
  	if (nodeType.equals(ELEMENT_NODE)) {
          XPathExpression<Element> xp = xpfac.compile(xpath, Filters.element());
          Element ele = xp.evaluateFirst(document);
          if (ele != null) rtnVal = ele.getValue();
    	}
  	return rtnVal;
  } 
  
  public String getRespKey(String sid) {
	  String respKey = null;
	  respKey = this.getSingleXPathValue("/myFSG/INSTANCE[@SID='" + sid + "']/RESP_KEY", ELEMENT_NODE);
	    logger.debug("respKey (from instance file) = " + respKey);
	    return respKey;
  }
  
  public String getReleaseName(String sid) throws Exception {
  	String releaseName = null;

  	releaseName = this.getSingleXPathValue("/myFSG/INSTANCE[@SID='" + sid + "']/RELEASE_NAME", ELEMENT_NODE);
    
    logger.debug("releaseName (from instance file) = " + releaseName);    	
  	
      if (releaseName == null || releaseName.equals("")) {
      	releaseName = Answer.getInstance().getB(RELEASE_NAME);
      }
      
      if (releaseName == null || releaseName.equals("")) {
      	releaseName = getReleaseNameFromDB();
      }
      
      return releaseName;
  	
  }    
  
  public String getReleaseNameFromDB() throws Exception {
  	String releaseName = null;
  	
      Connection conn = DBConnection.getInstance().getConnection();
  	
      String sql = "SELECT RELEASE_NAME FROM FND_PRODUCT_GROUPS";
      ResultSet rs  = conn.createStatement().executeQuery(sql);

      while (rs.next()) {
          releaseName = rs.getString(1);
      }

      rs.close();
      
      if (releaseName.startsWith("12.1")) {
          releaseName = RELEASE_121;
      } else if (releaseName.startsWith("12.2")) {
      	releaseName = RELEASE_122;
      } else if (releaseName.startsWith("11.5")) {
          releaseName = RELEASE_11i;
      } else {
          releaseName = "UNKNOWN";
      }
      logger.debug("releaseName (from DB) = " + releaseName);
      return releaseName;
  }

  public void getNLSInfo() throws Exception {
      String    nls_lang               = null;
      String    nls_language           = null;
      String    nls_territory          = null;
      String    nls_sort               = null;
      String    nls_characterset       = null;
      String    nls_date_format        = null;
      String    nls_numeric_characters = null;
      
      Connection conn = DBConnection.getInstance().getConnection();
      
      String sql = "SELECT PARAMETER, VALUE FROM NLS_DATABASE_PARAMETERS";
      ResultSet rs  = conn.createStatement().executeQuery(sql);

      while (rs.next()) {
          if (rs.getString(1).equals("NLS_LANGUAGE")) {
              nls_language = rs.getString(2);
          } else if (rs.getString(1).equals("NLS_TERRITORY")) {
              nls_territory = rs.getString(2);
          } else if (rs.getString(1).equals("NLS_CHARACTERSET")) {
              nls_characterset = rs.getString(2);
          } else if (rs.getString(1).equals(NLS_SORT)) {
              nls_sort = rs.getString(2);
          } else if (rs.getString(1).equals(NLS_DATE_FORMAT)) {
              nls_date_format = rs.getString(2);
          } else if (rs.getString(1).equals(NLS_NUMERIC_CHARACTERS)) {
              nls_numeric_characters = rs.getString(2);
          }
      }

      rs.close();
      nls_lang = nls_language + "_" + nls_territory + "." + nls_characterset;
      Answer.getInstance().putB(NLS_LANG, nls_lang);
      Answer.getInstance().putB(NLS_SORT, nls_sort);
      Answer.getInstance().putB(NLS_DATE_FORMAT, nls_date_format);
      Answer.getInstance().putB(NLS_NUMERIC_CHARACTERS, nls_numeric_characters);
      logger.debug("NLS Parameters: [" + nls_lang + "] [" + nls_sort + "] [" + NLS_DATE_FORMAT + "] ["
                 + NLS_NUMERIC_CHARACTERS + "]");
  }
  
  public static String releasePath(String release) {
  	if (release.equals(RELEASE_11i)) return "11.5.0";
  	if (release.equals(RELEASE_121)) return "12.0.0";
  	if (release.equals(RELEASE_122)) return "12.0.0";    	
  	return release;
  }
  
  /*
   * return 32, 64 or null 
   */
  public static String winArch() {
  	String rtnVal = null;
      String osName = System.getProperty("os.name");
      logger.debug("os info: " + osName);
      if (osName.toUpperCase().startsWith("WIN")) {
      	String arch = System.getenv("PROCESSOR_ARCHITECTURE");
      	String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
      	rtnVal = arch.endsWith("64")
      	                  || wow64Arch != null && wow64Arch.endsWith("64")
      	                      ? "64" : "32";     
      	logger.debug("os arch " + rtnVal);
      }
      return rtnVal;
  }  
  
}
