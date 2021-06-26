/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.ninja.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for RO_ModelMaker
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_RO_ModelMaker extends PO implements I_RO_ModelMaker, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20181030L;

    /** Standard Constructor */
    public X_RO_ModelMaker (Properties ctx, int RO_ModelMaker_ID, String trxName)
    {
      super (ctx, RO_ModelMaker_ID, trxName);
      /** if (RO_ModelMaker_ID == 0)
        {
			setRO_ModelMaker_ID (0);
        } */
    }

    /** Load Constructor */
    public X_RO_ModelMaker (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_RO_ModelMaker[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Callout.
		@param Callout 
		Fully qualified class names and method - separated by semicolons
	  */
	public void setCallout (String Callout)
	{
		set_Value (COLUMNNAME_Callout, Callout);
	}

	/** Get Callout.
		@return Fully qualified class names and method - separated by semicolons
	  */
	public String getCallout () 
	{
		return (String)get_Value(COLUMNNAME_Callout);
	}

	/** Set ColumnSet.
		@param ColumnSet ColumnSet	  */
	public void setColumnSet (String ColumnSet)
	{
		set_Value (COLUMNNAME_ColumnSet, ColumnSet);
	}

	/** Get ColumnSet.
		@return ColumnSet	  */
	public String getColumnSet () 
	{
		return (String)get_Value(COLUMNNAME_ColumnSet);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set KanbanBoard.
		@param KanbanBoard KanbanBoard	  */
	public void setKanbanBoard (boolean KanbanBoard)
	{
		set_Value (COLUMNNAME_KanbanBoard, Boolean.valueOf(KanbanBoard));
	}

	/** Get KanbanBoard.
		@return KanbanBoard	  */
	public boolean isKanbanBoard () 
	{
		Object oo = get_Value(COLUMNNAME_KanbanBoard);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Master.
		@param Master Master	  */
	public void setMaster (String Master)
	{
		set_Value (COLUMNNAME_Master, Master);
	}

	/** Get Master.
		@return Master	  */
	public String getMaster () 
	{
		return (String)get_Value(COLUMNNAME_Master);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set PrintFormat.
		@param PrintFormat PrintFormat	  */
	public void setPrintFormat (boolean PrintFormat)
	{
		set_Value (COLUMNNAME_PrintFormat, Boolean.valueOf(PrintFormat));
	}

	/** Get PrintFormat.
		@return PrintFormat	  */
	public boolean isPrintFormat () 
	{
		Object oo = get_Value(COLUMNNAME_PrintFormat);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_RO_ModelHeader getRO_ModelHeader() throws RuntimeException
    {
		return (I_RO_ModelHeader)MTable.get(getCtx(), I_RO_ModelHeader.Table_Name)
			.getPO(getRO_ModelHeader_ID(), get_TrxName());	}

	/** Set ModelHeader.
		@param RO_ModelHeader_ID ModelHeader	  */
	public void setRO_ModelHeader_ID (int RO_ModelHeader_ID)
	{
		if (RO_ModelHeader_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RO_ModelHeader_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RO_ModelHeader_ID, Integer.valueOf(RO_ModelHeader_ID));
	}

	/** Get ModelHeader.
		@return ModelHeader	  */
	public int getRO_ModelHeader_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RO_ModelHeader_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ModelMaker.
		@param RO_ModelMaker_ID ModelMaker	  */
	public void setRO_ModelMaker_ID (int RO_ModelMaker_ID)
	{
		if (RO_ModelMaker_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RO_ModelMaker_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RO_ModelMaker_ID, Integer.valueOf(RO_ModelMaker_ID));
	}

	/** Get ModelMaker.
		@return ModelMaker	  */
	public int getRO_ModelMaker_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RO_ModelMaker_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set RO_ModelMaker_UU.
		@param RO_ModelMaker_UU RO_ModelMaker_UU	  */
	public void setRO_ModelMaker_UU (String RO_ModelMaker_UU)
	{
		set_ValueNoCheck (COLUMNNAME_RO_ModelMaker_UU, RO_ModelMaker_UU);
	}

	/** Get RO_ModelMaker_UU.
		@return RO_ModelMaker_UU	  */
	public String getRO_ModelMaker_UU () 
	{
		return (String)get_Value(COLUMNNAME_RO_ModelMaker_UU);
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set WorkflowModel.
		@param WorkflowModel WorkflowModel	  */
	public void setWorkflowModel (String WorkflowModel)
	{
		set_Value (COLUMNNAME_WorkflowModel, WorkflowModel);
	}

	/** Get WorkflowModel.
		@return WorkflowModel	  */
	public String getWorkflowModel () 
	{
		return (String)get_Value(COLUMNNAME_WorkflowModel);
	}

	/** Set WorkflowStructure.
		@param WorkflowStructure WorkflowStructure	  */
	public void setWorkflowStructure (boolean WorkflowStructure)
	{
		set_Value (COLUMNNAME_WorkflowStructure, Boolean.valueOf(WorkflowStructure));
	}

	/** Get WorkflowStructure.
		@return WorkflowStructure	  */
	public boolean isWorkflowStructure () 
	{
		Object oo = get_Value(COLUMNNAME_WorkflowStructure);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}
}