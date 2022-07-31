package symbolthree.oracle.fsg;

import symbolthree.calla.Answer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CheckRegistryAction extends MyFSGActionBase {
	
    private String  nls_date_format        = null;
    private String  nls_lang               = null;
    private String  nls_numeric_characters = null;
    private String  nls_sort               = null;
    private String  releaseName            = null;
    private int     jvmBit                 = WinRegistry.KEY_WOW64_32KEY;
    private String  windowsBitKey           = "";

    static final Logger logger = LogManager.getLogger(CheckRegistryAction.class.getName());
    
    @Override
    public boolean enterAction() {
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0) {
        	String bitVal = System.getProperty("sun.arch.data.model");
        	logger.debug("JVM Version=" + bitVal);
        	
        	if (bitVal.equals("32")) jvmBit = WinRegistry.KEY_WOW64_32KEY;
        	if (bitVal.equals("64")) jvmBit = WinRegistry.KEY_WOW64_64KEY;
        	
        	String winBitVal = System.getProperty("os.arch");
        	logger.debug("Windows Version=" + winBitVal);
        	
        	if (winBitVal.equals("amd64")) windowsBitKey = "Wow6432Node\\";
        	
            releaseName            = Answer.getInstance().getB(RELEASE_NAME);
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
           logger.error(e);
        }
    }

    private void setNLSValues() throws Exception {
    	String rootKey = "SOFTWARE\\" + windowsBitKey + "ORACLE\\APPLICATIONS\\" + releaseName + "\\SYMBOLTHREE";
    	
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

    	String rootKey = "SOFTWARE\\" + windowsBitKey + "ORACLE\\APPLICATIONS\\" + releaseName;
    	try {
	    	String regValue = WinRegistry.readString(
	    			WinRegistry.HKEY_LOCAL_MACHINE, rootKey, "APPL_CONFIG", jvmBit);
	
	        if (regValue == null || regValue.equals("")) {
	        	WinRegistry.createKey(WinRegistry.HKEY_LOCAL_MACHINE, rootKey);
	        	WinRegistry.writeStringValue(
	        			WinRegistry.HKEY_LOCAL_MACHINE, rootKey, "APPL_CONFIG", "SYMBOLTHREE", jvmBit);
	        	
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
        return "SaveConfig";
    }
}
