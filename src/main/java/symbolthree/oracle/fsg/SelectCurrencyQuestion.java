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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/SelectCurrencyQuestion.java $
 * $Author: Christopher Ho $
 * $Date: 3/29/17 6:46a $
 * $Revision: 4 $
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

public class SelectCurrencyQuestion extends MyFSGQuestion {


    private String releaseName = null;
    private ArrayList<Choice> al = new ArrayList<Choice>();
    static final Logger logger = LogManager.getLogger(SelectCurrencyQuestion.class.getName());
    public static final String RCS_ID = "$Header: /TOOL/myMSG/src/symbolthree/oracle/fsg/SelectCurrencyQuestion.java 4     3/29/17 6:46a Christopher Ho $";    
    
	public SelectCurrencyQuestion() {
        releaseName = Answer.getInstance().getB(RELEASE_NAME);			
	}

    public boolean enterQuestion() {
    	try {
    	Connection conn = DBConnection.getInstance().getConnection();
    	String sql = Query.init().getSQL("selectCurrency");
    	PreparedStatement ps = conn.prepareStatement(sql);
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
    	return "Please select the currency for this FSG report\n" + 
                "[Current Currency : " +  RGRARG.init().getValue(RGRARG.CURRENCY)  + "]\n";
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
    	RGRARG.init().setValue(RGRARG.CURRENCY, ans);
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
      	
      	return "SelectCurrency";
    }       
}
