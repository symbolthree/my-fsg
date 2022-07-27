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
 * $Archive: /TOOL/myMSG/src/symbolthree/oracle/fsg/datamodel/FSGSheetData.java $
 * $Author: Christopher Ho $
 * $Date: 1/09/17 11:33p $
 * $Revision: 2 $
******************************************************************************/

package symbolthree.oracle.fsg.datamodel;

public class FSGSheetData {

	private String sheetName;
	private String[][] sheetData;
    public static final String RCS_ID = "$Header: /TOOL/myMSG/src/symbolthree/oracle/fsg/datamodel/FSGSheetData.java 2     1/09/17 11:33p Christopher Ho $";
	
	public FSGSheetData(String _sheetName) {
		this.sheetName = _sheetName;
	}
	
	public FSGSheetData() {
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
	public void setSheetData(String[][] data) {
		this.sheetData = data;
	}

	public String[][] getSheetData() {
		return sheetData;
	}

}
