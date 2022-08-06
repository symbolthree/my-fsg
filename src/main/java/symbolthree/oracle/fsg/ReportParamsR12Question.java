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

public class ReportParamsR12Question extends MyFSGQuestion {
    
	private ArrayList<Choice> al = new ArrayList<Choice>();
	private int ledgerCount      = 0;
    static final Logger logger = LogManager.getLogger(ReportParamsR12Question.class.getName());
    
	public ReportParamsR12Question() {
	}

    @Override
    public boolean enterQuestion() {
    	// skip query if returned from SelectPeriod, SelectCurrency SelectLedger screen
    	if (isReenter()) return true; 
    	
    	int reportID    = RGRARG.init().getValueAsInt(RGRARG.REPORT_ID);
    	int accessSetID = RGRARG.init().getValueAsInt(RGRARG.GL_ACCESS_SET_ID);
    	
    	try {
    	Connection conn = DBConnection.getInstance().getConnection();
    	// select default values of selected report under selected responsibility
    	String sql = Query.init().getSQL("reportParamsR12");
    	PreparedStatement ps = conn.prepareStatement(sql);
    	ps.setInt(1, accessSetID);
    	ps.setInt(2, reportID);
    	
    	ResultSet rs = ps.executeQuery();
    	while (rs.next()) {
    		RGRARG.init().setMeaning(RGRARG.REPORT_ID,       rs.getString("REPORT_NAME"));
    		
    		RGRARG.init().setValue(RGRARG.COLUMN_SET_ID,     rs.getString(RGRARG.COLUMN_SET_ID));
    		RGRARG.init().setMeaning(RGRARG.COLUMN_SET_ID,   rs.getString("COLUMN_SET"));
    		
    		RGRARG.init().setValue(RGRARG.ROW_SET_ID,        rs.getString(RGRARG.ROW_SET_ID));
    		RGRARG.init().setMeaning(RGRARG.ROW_SET_ID,      rs.getString("ROW_SET"));
    		
    		RGRARG.init().setValue(RGRARG.ROW_ORDER_ID,      rs.getString(RGRARG.ROW_ORDER_ID));
    		RGRARG.init().setMeaning(RGRARG.ROW_ORDER_ID,    rs.getString("ROW_ORDER"));
    		
    		RGRARG.init().setValue(RGRARG.DISPLAY_SET_ID,    rs.getString(RGRARG.DISPLAY_SET_ID));
    		RGRARG.init().setMeaning(RGRARG.DISPLAY_SET_ID,  rs.getString("DISPLAY_SET"));
    		
    		RGRARG.init().setValue(RGRARG.CONTENT_SET_ID,    rs.getString(RGRARG.CONTENT_SET_ID));
    		RGRARG.init().setMeaning(RGRARG.CONTENT_SET_ID,  rs.getString("CONTENT_SET"));
    		
    		RGRARG.init().setValue(RGRARG.SHORT_NAME,        rs.getString(RGRARG.SHORT_NAME));
    		RGRARG.init().setMeaning(RGRARG.SHORT_NAME,      rs.getString("LEDGER_NAME"));    		
    		
    		RGRARG.init().setValue(RGRARG.COA_ID,            rs.getString(RGRARG.COA_ID));
    		
    		RGRARG.init().setValue(RGRARG.ROUNDING_OPTION,   rs.getString(RGRARG.ROUNDING_OPTION));    		
    		RGRARG.init().setMeaning(RGRARG.ROUNDING_OPTION, rs.getString("ROUNDING_OPTION_MEAN"));
    		
    		RGRARG.init().setValue(RGRARG.OVERRIDE_VALUES,   rs.getString(RGRARG.OVERRIDE_VALUES));
    		
    		RGRARG.init().setValue(RGRARG.CURRENCY,          rs.getString(RGRARG.CURRENCY));    		
    		
    		RGRARG.init().setValue(RGRARG.FLEX_CODE_ID,      rs.getString(RGRARG.FLEX_CODE_ID));    		

    		RGRARG.init().setValue(RGRARG.MIN_DISPLAY_LEVEL,   rs.getString(RGRARG.MIN_DISPLAY_LEVEL));    		
    		RGRARG.init().setMeaning(RGRARG.MIN_DISPLAY_LEVEL, rs.getString("MIN_DISPLAY_LEVEL_MEAN"));
    		
    		RGRARG.init().setValue(RGRARG.PARAMETER_SET_ID,  rs.getString(RGRARG.PARAMETER_SET_ID));
    		
    		RGRARG.init().setValue(RGRARG.PERIOD,            rs.getString(RGRARG.PERIOD));
    	}

    	rs.close();
    	ps.close();
    	
    	} catch (Exception e) {
    		logger.catching(e);
    	}
    	return true;
    }
    
    
    @Override
    public ArrayList<Choice> choices() {
    	// ledger count
    	try {
    	String sql = Query.init().getSQL("ledgerCount");
    	Connection conn = DBConnection.getInstance().getConnection();
    	PreparedStatement ps = conn.prepareStatement(sql);
    	int accessSetID = RGRARG.init().getValueAsInt(RGRARG.GL_ACCESS_SET_ID);    	
    	ps.setInt(1, accessSetID);
    	ResultSet rs = ps.executeQuery();
    	rs.next();
    	ledgerCount = rs.getInt(1);
    	rs.close();
    	ps.close();
    	} catch (Exception e) {
    		logger.catching(e);
    	}
    	
    	
    	Choice c0 = new Choice(NEXT_QUESTION, "Next -> Set output file");
    	al.add(c0);
    	
    	logger.debug("ledger count = " + ledgerCount);
    	if (ledgerCount > 1) {
    	  Choice c1 = new Choice("LEDGER", "Change Ledger");
      	  al.add(c1);    	  
    	}  
    	Choice c2 = new Choice("PERIOD", "Change Period");
    	Choice c3 = new Choice("CURRENCY", "Change Currency");
   	
    	al.add(c2);
    	al.add(c3);

    	return al;
    }
    
    @Override
    public String getQuestion() {
    	return "Please review the FSG report parameters:\n";
    }
    

    @Override
    public String getExplanation() {
    	String str = "";
    	str = str + "Report Name : " + RGRARG.init().getMeaning(RGRARG.REPORT_ID) + "\r\n";
    	str = str + "Ledger      : " + RGRARG.init().getMeaning(RGRARG.SHORT_NAME) + "\r\n";
    	str = str + "Currency    : " + RGRARG.init().getValue(RGRARG.CURRENCY) + "\r\n";
    	str = str + "Period      : " + RGRARG.init().getValue(RGRARG.PERIOD) + "\r\n";
    	
    	return str;
    }

    
    @Override
    public boolean isMultipleChoices() {
        return true;
    }

    @Override
    public boolean lineWrap () {
        return false;
    }
    
    
    @Override
    public String nextAction() {
    	String ans = Answer.getInstance().getA(this);
    	if (ans.equals("LEDGER"))    return "SelectLedger";
    	if (ans.equals("CURRENCY"))  return "SelectCurrency";
    	if (ans.equals("PERIOD"))    return "SelectPeriod";    	
    	if (ans.equals(NEXT_QUESTION)) return "OutputFile";
    	
    	return "ReportParameters";
    }            
    
    @Override
    public String lastAction() {
        return "SelectReport";
    }       
    
    
    private boolean isReenter() {
    	String s = Answer.getInstance().getB(REENTER);
    	if (s != null && s.equals("Y")) {
    		return true;
    	} else {
    		return false;
    	}
    }
        
}
