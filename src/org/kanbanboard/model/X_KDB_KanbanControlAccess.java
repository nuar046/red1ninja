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
package org.kanbanboard.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for KDB_KanbanControlAccess
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_KDB_KanbanControlAccess extends PO implements I_KDB_KanbanControlAccess, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20161207L;

    /** Standard Constructor */
    public X_KDB_KanbanControlAccess (Properties ctx, int KDB_KanbanControlAccess_ID, String trxName)
    {
      super (ctx, KDB_KanbanControlAccess_ID, trxName);
      /** if (KDB_KanbanControlAccess_ID == 0)
        {
			setAD_Role_ID (0);
			setIsReadWrite (false);
			setKDB_KanbanBoard_ID (0);
        } */
    }

    /** Load Constructor */
    public X_KDB_KanbanControlAccess (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client 
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
      StringBuffer sb = new StringBuffer ("X_KDB_KanbanControlAccess[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Role getAD_Role() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Role)MTable.get(getCtx(), org.compiere.model.I_AD_Role.Table_Name)
			.getPO(getAD_Role_ID(), get_TrxName());	}

	/** Set Role.
		@param AD_Role_ID 
		Responsibility Role
	  */
	public void setAD_Role_ID (int AD_Role_ID)
	{
		if (AD_Role_ID < 0) 
			set_ValueNoCheck (COLUMNNAME_AD_Role_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_Role_ID, Integer.valueOf(AD_Role_ID));
	}

	/** Get Role.
		@return Responsibility Role
	  */
	public int getAD_Role_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Role_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Read Write.
		@param IsReadWrite 
		Field is read / write
	  */
	public void setIsReadWrite (boolean IsReadWrite)
	{
		set_Value (COLUMNNAME_IsReadWrite, Boolean.valueOf(IsReadWrite));
	}

	/** Get Read Write.
		@return Field is read / write
	  */
	public boolean isReadWrite () 
	{
		Object oo = get_Value(COLUMNNAME_IsReadWrite);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public org.kanbanboard.model.I_KDB_KanbanBoard getKDB_KanbanBoard() throws RuntimeException
    {
		return (org.kanbanboard.model.I_KDB_KanbanBoard)MTable.get(getCtx(), org.kanbanboard.model.I_KDB_KanbanBoard.Table_Name)
			.getPO(getKDB_KanbanBoard_ID(), get_TrxName());	}

	/** Set Kanban Board.
		@param KDB_KanbanBoard_ID Kanban Board	  */
	public void setKDB_KanbanBoard_ID (int KDB_KanbanBoard_ID)
	{
		if (KDB_KanbanBoard_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanBoard_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_KDB_KanbanBoard_ID, Integer.valueOf(KDB_KanbanBoard_ID));
	}

	/** Get Kanban Board.
		@return Kanban Board	  */
	public int getKDB_KanbanBoard_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_KDB_KanbanBoard_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set KDB_KanbanControlAccess_UU.
		@param KDB_KanbanControlAccess_UU KDB_KanbanControlAccess_UU	  */
	public void setKDB_KanbanControlAccess_UU (String KDB_KanbanControlAccess_UU)
	{
		set_Value (COLUMNNAME_KDB_KanbanControlAccess_UU, KDB_KanbanControlAccess_UU);
	}

	/** Get KDB_KanbanControlAccess_UU.
		@return KDB_KanbanControlAccess_UU	  */
	public String getKDB_KanbanControlAccess_UU () 
	{
		return (String)get_Value(COLUMNNAME_KDB_KanbanControlAccess_UU);
	}
}