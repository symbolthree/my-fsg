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

package symbolthree.oracle.fsg.datamodel;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FSGReport {

	private Hashtable<String, String> header = new Hashtable<String, String>();
	private ArrayList<String> rowLabel = new ArrayList<String>();
	private ArrayList<String[]> colLabel = new ArrayList<String[]>();
	private ArrayList<FSGSheetData> sheetData = new ArrayList<FSGSheetData>();
    
	static final Logger logger = LogManager.getLogger(FSGReport.class.getName());
	
	public FSGReport() {
	}

	public ArrayList<String> getRowLabel() {
		return rowLabel;
	}

	public void addRowLabel(int pos, String lbl) {
		rowLabel.add(pos, lbl);
	}

	public ArrayList<String[]> getColLabel() {
		return colLabel;
	}

	public void addColLabel(int pos, String[] lbls) {
		colLabel.add(pos, lbls);
	}

	public int getColSize() {
		// the first column is the Account column, not the period column
		return colLabel.size()-1;
	}

	public int getRowSize() {
		return rowLabel.size();
	}
	
	public void addSheetData(int no, FSGSheetData data) {
		sheetData.add(no, data);
	}
	
	public FSGSheetData getSheetData(int idx) {
		return sheetData.get(idx);
	}

	public void addHeader(String name, String val) {
		header.put(name, val);
	}
	
	public String getHeaderValue(String name) {
		return header.get(name);
	}

}
