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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.calla.Answer;
import symbolthree.calla.Choice;

public class SelectReportQuestion extends MyFSGQuestion {

	private String filterChoice = "-999";
	private String filterStr     = null;
	private int accessSetID      = 0;
	private ArrayList<Choice> al = new ArrayList<Choice>();
    private String releaseName = null;
    
    static final Logger logger = LogManager.getLogger(SelectReportQuestion.class.getName());
    
	public SelectReportQuestion() {
        releaseName = Answer.getInstance().getB(RELEASE_NAME);		
	}

    @Override
    public boolean enterQuestion() {
    	// add choice for filtering
    	Choice f = new Choice(filterChoice, "## Filter report by name ##");
    	al.add(f);
        
    	try {
	    	Connection conn = DBConnection.getInstance().getConnection();
	    	PreparedStatement ps = null;
	    	
	    	if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
		    	String sql = Query.init().getSQL("reportR12");
		    	
		    	filterStr = Answer.getInstance().getA("ReportFilter");
		    	accessSetID = RGRARG.init().getValueAsInt(RGRARG.GL_ACCESS_SET_ID);
		    	
		    	if (filterStr == null || filterStr.equals("")) {
		    		filterStr = "%";
		    	}
		    	logger.debug("filterStr=" + filterStr + " / accessSetID=" + accessSetID);
		    	ps = conn.prepareStatement(sql);
		    	ps.setInt(1, accessSetID);
		    	ps.setString(2, filterStr.toUpperCase());
	    	}
		    	
	    	if (releaseName.equals(RELEASE_11i)) {
		    	String sql = Query.init().getSQL("report115");
		    	String respKey = Answer.getInstance().getA("SelectResp");
		    	filterStr = Answer.getInstance().getA("ReportFilter");
	
		    	if (filterStr == null || filterStr.equals("")) {
		    		filterStr = "%";
		    	}
		    	
		    	logger.debug("filterStr=" + filterStr);
		    	ps = conn.prepareStatement(sql);
		    	ps.setString(1, respKey);
		    	ps.setString(2, filterStr.toUpperCase());
	    	}
	    	
	    	ResultSet rs = ps.executeQuery();
	    	while (rs.next()) {
	    		Choice c = new Choice(rs.getString(1), rs.getString(2));
	    		al.add(c);
	    	}
	    	
    	} catch (Exception e) {
    		logger.catching(e);
    	}
    	return true;
    }
    
    @Override
    public String getQuestion() {
    	return "Please select a FSG report";
    }
    
    @Override
    public String getExplanation() {
    	if (filterStr!=null && !filterStr.equals("")) {
    		return "Filter is [" + filterStr + "]";
    	} else {
    		return null;
    	}
    }
    
    @Override
    public boolean isMultipleChoices() {
        return true;
    }   
    
    @Override
    public ArrayList<Choice> choices() {
        return al;
    }
    
    @Override
    public String nextAction() {
    	String reportID = Answer.getInstance().getA(this);
    	logger.debug("report_id=" + reportID);
    	
    	if (reportID.equals(filterChoice)) {
    		return "ReportFilter";
    	}
    	
    	RGRARG.init().setValue(RGRARG.REPORT_ID, reportID);
    	
    	if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
          return "ReportParamsR12";
    	}
    	
    	if (releaseName.equals(RELEASE_11i)) {
    	  return "ReportParams115";
    	}
    	
    	return "SelectReport";
    }    
    
    @Override
    public String lastAction() {
        return "SelectResp";
    }    
}
    
