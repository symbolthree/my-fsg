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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/SelectPeriodQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 3/29/17 6:46a $
 * $Revision: 3 $
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

public class SelectPeriodQuestion extends MyFSGQuestion {

    private ArrayList<Choice> al = new ArrayList<Choice>();
    private String releaseName = null;
    
    static final Logger logger = LogManager.getLogger(SelectPeriodQuestion.class.getName());
    
	public SelectPeriodQuestion() {
        releaseName = Answer.getInstance().getB(RELEASE_NAME);				
	}

    public boolean enterQuestion() {

    	try {
    	Connection conn = DBConnection.getInstance().getConnection();
    	PreparedStatement ps = null;
    	
    	if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
          int accessSetID = RGRARG.init().getValueAsInt(RGRARG.GL_ACCESS_SET_ID);
   	      String sql = Query.init().getSQL("selectPeriodR12");
      	  ps = conn.prepareStatement(sql);
      	  ps.setInt(1, accessSetID);
    	}

    	if (releaseName.equals(RELEASE_11i)) {
   	      String respKey = Answer.getInstance().getA("SelectResp");
    	  String sql = Query.init().getSQL("selectPeriod115");
    	  ps = conn.prepareStatement(sql);
    	  ps.setString(1, respKey);
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
    	return "Please select the Period for this FSG report\n" + 
                "[Current Period : " +  RGRARG.init().getValue(RGRARG.PERIOD)  + "]";
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
    	String ans = Answer.getInstance().getA(this);
    	RGRARG.init().setValue(RGRARG.PERIOD, ans);
    	Answer.getInstance().putB(REENTER, "Y");
    	
    	return getParameterQuestion();
    }    
    
    @Override
    public String lastAction() {
    	return getParameterQuestion();	
    }    	

    private String getParameterQuestion() {
    	if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
            return "ReportParamsR12";
      	}
      	
      	if (releaseName.equals(RELEASE_11i)) {
      	  return "ReportParams115";
      	}
      	
      	return "SelectPeriod";
    }    
}
