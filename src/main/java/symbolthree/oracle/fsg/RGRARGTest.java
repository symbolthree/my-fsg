package symbolthree.oracle.fsg;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RGRARGTest {

	private ArrayList<String> cmdArgs  = new ArrayList<String>();

	public RGRARGTest() {
	}

	public static void main(String[] args) {
		RGRARGTest t = new RGRARGTest();
		t.doServerModeTest();
	}

	private void doServerModeTest() {
        File logDir = new File(Instances.getInstance().getXMLDataDirectory());
//        String quoteChar = "\"";
      String quoteChar = "";        
        //cmdArgs.add(quoteChar + "C:\\Oracle\\apps\\apps_st\\appl\\rg\\12.0.0\\bin\\RGRARG.exe" + quoteChar);
        cmdArgs.add(System.getenv("RG_TOP") + File.separator + "bin" + File.separator + "RGRARG");
        cmdArgs.add(quoteChar + "apps/apps" + quoteChar);
        cmdArgs.add(quoteChar + "0" + quoteChar);
        cmdArgs.add(quoteChar + "Y" + quoteChar);
        cmdArgs.add(quoteChar + "1017" + quoteChar);
        cmdArgs.add(quoteChar + "101" + quoteChar);
        cmdArgs.add(quoteChar + "FSG-ADHOC-" + quoteChar);
        cmdArgs.add(quoteChar + "C" + quoteChar);
        cmdArgs.add(quoteChar + "GLLE" + quoteChar);
        cmdArgs.add(quoteChar + "Vision Operations" + quoteChar);
        cmdArgs.add(quoteChar + "1042" + quoteChar);
        cmdArgs.add(quoteChar + "1000" + quoteChar);
        cmdArgs.add(quoteChar + "1064" + quoteChar);
        cmdArgs.add(quoteChar + "Dec-08" + quoteChar);
        cmdArgs.add(quoteChar + "USD" + quoteChar);
        cmdArgs.add(quoteChar + "C" + quoteChar);
        cmdArgs.add(quoteChar + "-01----" + quoteChar);
        cmdArgs.add(quoteChar + "" + quoteChar);
        cmdArgs.add(quoteChar + "" + quoteChar);
     	cmdArgs.add(quoteChar + "" + quoteChar);
     	cmdArgs.add(quoteChar + "Y" + quoteChar);
     	cmdArgs.add(quoteChar + "N" + quoteChar);
     	cmdArgs.add(quoteChar + "1" + quoteChar);
     	cmdArgs.add(quoteChar + "" + quoteChar);
     	cmdArgs.add(quoteChar + "1042" + quoteChar);
     	cmdArgs.add(quoteChar + "58" + quoteChar);
     	cmdArgs.add(quoteChar + "-998" + quoteChar);
     	cmdArgs.add(quoteChar + "SQLGL" + quoteChar);

        ProcessBuilder pb = new ProcessBuilder(cmdArgs);
        pb.directory(logDir);
        System.out.println("log directory=" + logDir.getAbsolutePath());
        pb.redirectErrorStream(true);
        try {
			Process shell  = pb.start();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getInputStream()));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	        	line = line.trim();
	        	System.out.println(">>>" + line);
	        }
			int exitVal = shell.waitFor();
			System.out.println(exitVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
