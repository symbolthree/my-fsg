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

public class SelectRespQuestion extends MyFSGQuestion {

    ArrayList<Choice> al = new ArrayList<Choice>();
    static final Logger logger = LogManager.getLogger(SelectRespQuestion.class.getName());
    
	public SelectRespQuestion() {
	}

    @Override
    public boolean enterQuestion() {
    	try {
    	Connection conn = DBConnection.getInstance().getConnection();
    	String sql = Query.init().getSQL("resp");
        
    	PreparedStatement ps = conn.prepareStatement(sql);
    	String sid = Answer.getInstance().getA("SelectInstance");
    	String respKey = Instances.getInstance().getRespKey(sid);
    	ps.setString(1, respKey);
    	ResultSet rs = ps.executeQuery();
    	while (rs.next()) {
    		Choice c = new Choice(rs.getString("RESP_KEY"), rs.getString("RESP_NAME"));
    		al.add(c);
    	}
    	rs.close();
    	ps.close();
    	
    	} catch (Exception e) {
    		logger.catching(e);
    	}
    	return true;
    }	
	
    
    @Override
    public String getQuestion() {
    	return "Please select a GL responsibility";
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
    public boolean leaveQuestion() {
    	String respKey = Answer.getInstance().getA(this);
    	
    	logger.debug("RespKey=" + respKey);
    	
    	// ACCESS_SET_ID exists for R12
        String releaseName = Answer.getInstance().getB(RELEASE_NAME);
        if (releaseName.equals(RELEASE_121) || releaseName.equals(RELEASE_122)) {
	    	int accessSetID = -1;
	    	// set gl ledger name
	    	try {
	    	Connection conn = DBConnection.getInstance().getConnection();
	    	String sql =  Query.init().getSQL("accessSetID");
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	ps.setString(1, respKey);
	    	ResultSet rs = ps.executeQuery();
	    	while (rs.next()) {
	    		accessSetID = rs.getInt(3);
	    	}
	
	    	rs.close();
	    	ps.close();
	    	
	    	} catch (Exception e) {
	    		logger.catching(e);
	    	}
	    	
	    	logger.debug("accessSetID=" + accessSetID);
	    	RGRARG.init().setValue(RGRARG.GL_ACCESS_SET_ID, String.valueOf(accessSetID));
        }
        
    	return true;
    }
    
    @Override
    public String nextAction() {
        return "SelectReport";
    }    
    
    @Override
    public String lastAction() {
        return "SelectInstance";
    }        

}
