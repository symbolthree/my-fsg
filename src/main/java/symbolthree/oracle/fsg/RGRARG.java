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
/*
 R11 Arguments are:
    0   RGRARG.exe                
    1   username/password         
    2   0                         
    3   Y                         
    4   Period of Interest        
    5   Effective Date            
    6   Report ID                 
    7   Row Set ID                
    8   Column Set ID             
    9   Unit of Measure ID        
   10   Default Set of Books ID   
   11   Content Set ID            
   12   Parameter Set ID          
   13   Miniumum Display Level    
   14   Override Values           
   15   Maximum Page Length       
   16   Row Order ID              
   17   Exceptions Flag           
   18   Rounding Option           
   19   Report Display Set ID     
   20   Output Option             
   21   Sub-Request Run ID        
   22   Application Shortname      
 
  R12 Arguments are:
    #   Meaning                   Parameter Name     Example value
    0   RGRARG.exe                RGRARG             RGRARG.exe
    1   username/password         USERID             apps/apps
    2   0                         CONC_REQ_ID        0
    3   Y                         IMPLICIT           Y
    4   Data Access Set ID        GL_ACCESS_SET_ID   1001
    5   Chart of Accounts Id      COA_ID             101
    6   ADHOC Prefix              ADHOC_PREFIX       FSG-ADHOC-
    7   Industry                  INDUSTRY           C
    8   Flex Code Id              FLEX_CODE_ID       GLLE
    9   Default Ledger ID         SHORT_NAME         VCT TW2021(TWD)
   10   Report ID                 REPORT_ID          2001
   11   Row Set ID                ROW_SET_ID         3000
   12   Column Set ID             COLUMN_SET_ID      4000
   13   Period of Interest        PERIOD             NOV-16
   14   Unit of Measure ID        CURRENCY           TWD
   15   Rounding Option           RUNNING_OPTION     C
   16   Override Values           OVERRIDE_VALUES 
   17   Content Set ID            CONTENT_SET_ID  
   18   Row Order ID              ROW_ORDER_ID       2000
   19   Report Display Set ID     DISPLAT_SET_ID 
   20   Output Option             OUTPUT_OPTION      Y
   21   Exceptions Flag           EXCEPTION_FLAG     N
   22   Minimum Display Level     MIN_DISPLAY_LEVEL  
   23   Effective Date            EFFECTIVE_DATE    
   24   Parameter Set ID          PARAMETER_SET_ID   2001
   25   Maximum Page Length       MAX_PAGE_LENGTH    58
   26   Sub-Request Run ID        SUBREQUEST_RUN_ID  -998
   27   Application Shortname     APP_SHORT_NAME     SQLGL
  */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import symbolthree.calla.Answer;

public class RGRARG implements Constants {

   private static String   ATTRIBUTE_NODE = "A";
   private static String   ELEMENT_NODE   = "E";
   private static Document document  = null;
   private static String   parameterFile = "/symbolthree/oracle/fsg/parameters.xml";
   private static RGRARG   rgrarg = null;
   private XPathFactory    xpfac    = XPathFactory.instance();
   
   static final Logger logger = LogManager.getLogger(RGRARG.class.getName());
   
   public static String RGRARG                   = "RGRARG";
   public static String USERID                   = "USERID";
   public static String CONC_REQ_ID              = "CONC_REQ_ID";
   public static String IMPLICIT                 = "IMPLICIT";
   public static String GL_ACCESS_SET_ID         = "GL_ACCESS_SET_ID";
   public static String SET_OF_BOOKS_ID          = "SET_OF_BOOKS_ID"; 
   public static String COA_ID                   = "COA_ID";
   public static String ADHOC_PREFIX             = "ADHOC_PREFIX";
   public static String INDUSTRY                 = "INDUSTRY";
   public static String FLEX_CODE_ID             = "FLEX_CODE_ID";
   public static String SHORT_NAME               = "SHORT_NAME";
   public static String REPORT_ID                = "REPORT_ID";
   public static String ROW_SET_ID               = "ROW_SET_ID";
   public static String COLUMN_SET_ID            = "COLUMN_SET_ID";
   public static String PERIOD                   = "PERIOD";
   public static String CURRENCY                 = "CURRENCY";
   public static String ROUNDING_OPTION          = "ROUNDING_OPTION";
   public static String OVERRIDE_VALUES          = "OVERRIDE_VALUES";
   public static String CONTENT_SET_ID           = "CONTENT_SET_ID";
   public static String ROW_ORDER_ID             = "ROW_ORDER_ID";
   public static String DISPLAY_SET_ID           = "DISPLAY_SET_ID";
   public static String OUTPUT_OPTION            = "OUTPUT_OPTION";
   public static String EXCEPTION_FLAG           = "EXCEPTION_FLAG";
   public static String MIN_DISPLAY_LEVEL        = "MIN_DISPLAY_LEVEL";
   public static String EFFECTIVE_DATE           = "EFFECTIVE_DATE";
   public static String PARAMETER_SET_ID         = "PARAMETER_SET_ID";
   public static String SUBREQUEST_RUN_ID        = "SUBREQUEST_RUN_ID";
   public static String MAX_PAGE_LENGTH          = "MAX_PAGE_LENGTH";
   public static String APP_SHORT_NAME           = "APP_SHORT_NAME";   
   
   private static SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");

   protected RGRARG() {
       SAXBuilder builder = new SAXBuilder();
       try {
		  document = builder.build(this.getClass().getResourceAsStream(parameterFile));
	   } catch (Exception e) {
		  logger.catching(e);
	   }
   }
   
   public static RGRARG init() {
       if (rgrarg == null) {
    	   rgrarg = new RGRARG();
       }
       return rgrarg;
   }
   
   private String prefix() {
	   return "/RGRARG/parameters[@release='" + Answer.getInstance().getB(RELEASE_NAME) + "']";
   }
   
   public void setValue(String _key, String _val) {
	   String xpath = prefix() + "/parameter[@name='" + _key + "']";
	   logger.debug("set value for xpath " + xpath);
	   XPathExpression<Element> xp = xpfac.compile(xpath, Filters.element());
	   Element ele = xp.evaluateFirst(document);
	   if (_val == null) {
		 ele.setAttribute("value", "");		   
	   } else {
         ele.setAttribute("value", _val);		   
	   }
   }

   public void setMeaning(String _key, String _meaning) {
	   String xpath = prefix() + "/parameter[@name='" + _key + "']";
	   XPathExpression<Element> xp = xpfac.compile(xpath, Filters.element());
	   Element ele = xp.evaluateFirst(document);
	   if (_meaning == null) {
         ele.setAttribute("meaning", "");
	   } else {
	     ele.setAttribute("meaning", _meaning);
	   }
	   
   }   
   
   public String getValue(String _key) {
	   String xpath = prefix() + "/parameter[@name='" + _key + "']/@value"; 
	   String val = getSingleXPathValue(xpath, ATTRIBUTE_NODE);
	   if (val==null || val.equals("")) {
		   val = getDefault(_key);
	   }
	   return val;
   }

   public String getMeaning(String _key) {
	   String xpath = prefix() + "/parameter[@name='" + _key + "']/@meaning"; 
	   String val = getSingleXPathValue(xpath, ATTRIBUTE_NODE);
	   return val;
   }
   
   
   public int getValueAsInt(String _key) {
	   String str = getValue(_key);
	   int rtVal = Integer.MAX_VALUE;
	   try {
		   rtVal = Integer.parseInt(str);
	   } catch (Exception e) {
		   logger.warn("Unable to change value to int : " + str);
	   }
	   return rtVal;
   }
   
   public String getDefault(String _key) {
	   String xpath = prefix() + "/parameter[@name='" + _key + "']/@default"; 
	   return getSingleXPathValue(xpath, ATTRIBUTE_NODE);
   }   
   
   private String getSingleXPathValue(String xpath, String nodeType) {
       String rtnVal = null;

       if (nodeType.equals(ATTRIBUTE_NODE)) {
           XPathExpression<Attribute> xp   = xpfac.compile(xpath, Filters.attribute());
           Attribute                  attr = xp.evaluateFirst(document);

           if (attr != null) {
               rtnVal = attr.getValue();
           }
       }

       if (nodeType.equals(ELEMENT_NODE)) {
           XPathExpression<Element> xp  = xpfac.compile(xpath, Filters.element());
           Element                  ele = xp.evaluateFirst(document);

           if (ele != null) {
               rtnVal = ele.getValue();
           }
       }

       return rtnVal;
   }
   
   public void clearAll() {
	   if (rgrarg != null) rgrarg = null;
   }
   
   public ArrayList<String> createCommand() {
	   String xpath = prefix() + "/@noOfParameter";
	   int noOfParameter = Integer.parseInt(getSingleXPathValue(xpath, ATTRIBUTE_NODE));
	   logger.debug("noOfParameter=" + noOfParameter);

	   ArrayList<String> al = new ArrayList<String>();
	   
	   for (int i=0; i<=noOfParameter; i++) {
		   xpath = prefix() + "/parameter[@position='" + i + "']/@value";
		   String a = getSingleXPathValue(xpath, ATTRIBUTE_NODE);
		   
		   if (a == null || a.equals("")) {
			   xpath = prefix() + "/parameter[@position='" + i + "']/@default";
			   a = getSingleXPathValue(xpath, ATTRIBUTE_NODE);
		   }
		   
		   logger.debug("position " + i + "=" + a);
		   al.add(a);
	   }
	   return al;
   }
   
   public String generateDefaultFileName() {
	   int maxLength=20;
	   String str = null;
	   
       String timestamp = sdf.format(Calendar.getInstance().getTime());
       str = Answer.getInstance().getA("SelectInstance") + "_[" + 
             getMeaning(REPORT_ID).substring(0, Math.min(getMeaning(REPORT_ID).length(), maxLength)) + "]_[" +
             getValue(PERIOD) + "]_" 
             + timestamp;
       return str;
   }
   
}
