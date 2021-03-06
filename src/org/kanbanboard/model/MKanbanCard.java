/**********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Universidad Distrital Francisco Jose de Caldas       *
 **********************************************************************/

package org.kanbanboard.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.adempiere.base.event.EventManager;
import org.adempiere.base.event.EventProperty;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.print.MPrintColor;
import org.compiere.process.DocAction;
import org.compiere.process.DocActionEventData;
import org.compiere.process.DocOptions;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.osgi.service.event.Event; 


public class MKanbanCard {


	public static String KDB_ErrorMessage = "KDB_InvalidTransition";

	private int 		  recordId;
	private MKanbanBoard  kanbanBoard;
	private MKanbanStatus belongingStatus;
	private BigDecimal 	  priorityValue;

	private PO			  m_po           = null;
	private String 		  kanbanCardText = null;
	private boolean		  isQueued       = false;
	
	private String        textColor      = null;
	private String        cardColor      = null;

	public BigDecimal getPriorityValue() {
		return priorityValue;
	}

	public PO getM_po() {
		return m_po;
	}

	public void setM_po(PO m_po) {
		this.m_po = m_po;
	}

	public void setKanbanCardText(String kanbanCardText) {
		this.kanbanCardText = kanbanCardText;
	}

	public void setPriorityValue(BigDecimal priorityValue) {
		this.priorityValue = priorityValue;
	}

	public MKanbanStatus getBelongingStatus() {
		return belongingStatus;
	}

	public void setBelongingStatus(MKanbanStatus belongingStatus) {
		this.belongingStatus = belongingStatus;
	}

	public int getRecordID() {
		return recordId;
	}

	public void setRecordId(int name) {
		this.recordId = name;
	}


	public boolean isQueued() {
		return isQueued;
	}

	public void setQueued(boolean isQueued) {
		this.isQueued = isQueued;
	}

	public MKanbanCard(int cardRecord) {
		recordId = cardRecord;
	}

	public MKanbanCard(int cardRecord, MKanbanStatus status) {
		recordId = cardRecord;
		belongingStatus=status;
		kanbanBoard=belongingStatus.getKanbanBoard();
		m_po = kanbanBoard.getTable().getPO(recordId, null);
	}

	public boolean changeStatus(String statusColumn, String newStatusValue) {

		if (m_po == null)
			return false;
		boolean success=true;

		if (statusColumn.equals(MKanbanBoard.STATUSCOLUMN_DocStatus)) {
			if (m_po instanceof DocAction && m_po.get_ColumnIndex("DocAction") >= 0) {
				try {
					String p_docAction = kanbanBoard.getDocAction(newStatusValue);
					//No valid action
					if (p_docAction == null)
						throw new IllegalStateException();

					m_po.set_ValueOfColumn("DocAction", p_docAction);
					KDB_ErrorMessage = "KDB_InvalidTransition";
					
					//TODO trigger EventHandler but do we need to go thru its valid actions? 
					DocActionEventData eventData = new DocActionEventData(newStatusValue, null, "", "", m_po.get_Table_ID(),new ArrayList<String>(Arrays.asList(p_docAction)), new ArrayList<String>(Arrays.asList(new String[14])),new AtomicInteger(0), m_po);
					Event event = EventManager.newEvent(IEventTopics.DOCACTION,new EventProperty(EventManager.EVENT_DATA, eventData),new EventProperty("tableName", m_po.get_TableName()));
					EventManager.getInstance().sendEvent(event);
					if ((m_po.get_Value("DocStatus")).equals(newStatusValue)) { 
						m_po.saveEx();
					} else {
						KDB_ErrorMessage = eventData.orderType;//you can pass a customized exception message from the plugin event handler
						throw new IllegalStateException();
					}
				} catch (IllegalStateException e) {
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					KDB_ErrorMessage = e.getLocalizedMessage();
					return false;
				}
			}			
		} else {
			if (m_po.get_ColumnIndex("DocAction") >= 0) {
				if (((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Completed)||
						((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Voided)||
						((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Reversed)||
						((DocAction) m_po).getDocStatus().equals(DocAction.STATUS_Closed)) {
					KDB_ErrorMessage = "KDB_CompletedCard";
					return false;
				}
			}
			success = m_po.set_ValueOfColumnReturningBoolean(statusColumn, newStatusValue);
			m_po.saveEx();
		}
		return success;
	}

	public void getPriorityColor() {

		if (kanbanBoard.hasPriorityOrder() && kanbanBoard.getPriorityRules().size() > 0) {
			for (MKanbanPriority priorityRule : kanbanBoard.getPriorityRules()) {
				BigDecimal minValue = new BigDecimal(priorityRule.getMinValue());
				BigDecimal maxValue = new BigDecimal(priorityRule.getMaxValue());

				if (priorityValue.compareTo(minValue) >= 0 && priorityValue.compareTo(maxValue) <= 0) {
					MPrintColor priorityColor = new MPrintColor(Env.getCtx(), priorityRule.getKDB_PriorityColor_ID(), null);
					MPrintColor PriorityTextColor = new MPrintColor(Env.getCtx(), priorityRule.getKDB_PriorityTextColor_ID(), null);
					cardColor = priorityColor.getName();
					textColor = PriorityTextColor.getName();
					break;
				}
			} 
		}
	}
	
	public String getTextColor() {
		if (textColor == null)
			getPriorityColor();
		return textColor;
	}
	
	public String getCardColor() {
		if (cardColor == null)
			getPriorityColor();
		return cardColor;
	}

	public String getKanbanCardText() {
		if (kanbanCardText == null)
			translate();

		String parsedText = parse(kanbanCardText);

		if (kanbanBoard.get_ValueAsBoolean("IsHtml"))
			parsedText = parseHTML(parsedText);

		return parsedText;	
	}

	/**
	 * 	Translate to BPartner Language
	 */
	private void translate() {
		//	Default if no Translation
		if(kanbanBoard.getKDB_KanbanCard() != null)
			kanbanCardText=kanbanBoard.get_Translation(MKanbanBoard.COLUMNNAME_KDB_KanbanCard);
		else
			kanbanCardText=Integer.toString(recordId);
	}	//	translate

	private String parse(String text) {
		if (text.indexOf('@') == -1)
			return text;
		//	Parse PO
		text = parse (text, m_po);
		return text;
	}	//	parse
	
	private String parseHTML(String text) {

		if (text == null)
			return "";

		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<body>\n<div class=\"help-content\">\n");
		sb.append(text);
		sb.append("</div>\n</body>\n</html>");	

		return sb.toString();
	} //parseHTML

	/**
	 * 	Parse text
	 *	@param text text
	 *	@param po object
	 *	@return parsed text
	 */
	private String parse (String text, PO po) {
		if (po == null || text.indexOf('@') == -1)
			return text;

		String inStr = text;
		String token;
		StringBuilder outStr = new StringBuilder();

		int i = inStr.indexOf('@');
		while (i != -1) {
			outStr.append(inStr.substring(0, i));			// up to @
			inStr = inStr.substring(i+1, inStr.length());	// from first @

			int j = inStr.indexOf('@');						// next @
			if (j < 0) {									// no second tag
				inStr = "@" + inStr;
				break;
			}

			token = inStr.substring(0, j);

			//format string
			String format = "";
			int f = token.indexOf('<');
			if (f > 0 && token.endsWith(">")) {
				format = token.substring(f+1, token.length()-1);
				token = token.substring(0, f);
			}

			outStr.append(parseVariable(token, format, po));		// replace context

			inStr = inStr.substring(j+1, inStr.length());	// from second @
			i = inStr.indexOf('@');

			//if not HTML behave as usual - break line per field
			if (!kanbanBoard.get_ValueAsBoolean("IsHtml"))
				outStr.append(System.getProperty("line.separator"));
		}

		outStr.append(inStr);				//	add remainder
		return outStr.toString();
	}	//	parse

	/**
	 * 	Parse Variable
	 *	@param variable variable
	 *	@param po po
	 *	@return translated variable or if not found the original tag
	 */
	private String parseVariable (String variable, String format,PO po) {
		int index = po.get_ColumnIndex(variable);
		if (index == -1) {
			int i = variable.indexOf('.');
			if (i != -1) {
				StringBuilder outStr = new StringBuilder();
				outStr.append(variable.substring(0, i));
				variable = variable.substring(i+1, variable.length());
				outStr.append("_ID"); //Foreign Key column

				index = po.get_ColumnIndex(outStr.toString());

				Integer subRecordId;

				if (index != -1) {
					MColumn column = MColumn.get(Env.getCtx(), po.get_TableName(), po.get_ColumnName(index));
					MTable table = MTable.get(Env.getCtx(),column.getReferenceTableName());

					subRecordId = (Integer)po.get_Value(outStr.toString());
					if(subRecordId==null)
						return "";
					PO subPo = table.getPO(subRecordId, null);						
					return parseVariable(variable,format,subPo);
				}
			}

			StringBuilder msgreturn = new StringBuilder("@").append(variable).append("@");
			return msgreturn.toString();	//	keep for next
		}	
		//
		MColumn col = MColumn.get(Env.getCtx(), po.get_TableName(), variable);
		Object value = null;
		if (col != null && col.isSecure()) {
			value = "********";
		} else if (col.getAD_Reference_ID() == DisplayType.Date || col.getAD_Reference_ID() == DisplayType.DateTime || col.getAD_Reference_ID() == DisplayType.Time) {
			SimpleDateFormat sdf;
			if (format != null && format.length() > 0) {
				sdf = new SimpleDateFormat(format, Env.getLanguage(Env.getCtx()).getLocale());
			} else {
				sdf = DisplayType.getDateFormat(col.getAD_Reference_ID());
			}
			if (po.get_Value(index) != null)
				value = sdf.format (po.get_Value(index));	
		} else if (col.getAD_Reference_ID() == DisplayType.YesNo) {
			if (po.get_ValueAsBoolean(variable))
				value = Msg.getMsg(Env.getCtx(), "Yes");
			else
				value = Msg.getMsg(Env.getCtx(), "No");
		} else if (col.getAD_Reference_ID() == DisplayType.Number || col.getAD_Reference_ID() == DisplayType.Amount) {
			DecimalFormat df;
			if (format != null && format.length() > 0) {
				df =  DisplayType.getNumberFormat(col.getAD_Reference_ID(),null,format);
			} else {
				df = DisplayType.getNumberFormat(col.getAD_Reference_ID());
			}

			if(po.get_Value(index)!=null)
				value = df.format (po.get_Value(index));	
		} else {
			value = po.get_Value(index);
		}

		if (value == null)
			return "";
		return value.toString();
	}	//	parseVariable
}
