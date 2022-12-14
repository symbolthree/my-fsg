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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportFilterQuestion extends MyFSGQuestion {

    static final Logger logger = LogManager.getLogger(ReportFilterQuestion.class.getName());
   
	public ReportFilterQuestion() {
	}
	
    @Override
    public String getQuestion() {
    	return "Enter part of report name to search";
    }

    
    @Override
    public String getExplanation() {
    	return "(Wildcard (%) is allowed. Case insensitive).";
    }

    
    @Override
    public boolean isMultipleChoices() {
        return false;
    }
    
    @Override
    public String nextAction() {
        return "SelectReport";
    }    
    
    @Override
    public String lastAction() {
        return "SelectReport";
    }    
}
