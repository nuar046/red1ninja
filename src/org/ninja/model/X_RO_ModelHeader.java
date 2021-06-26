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

/** Generated Model for RO_ModelHeader
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_RO_ModelHeader extends PO implements I_RO_ModelHeader, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20161031L;

    /** Standard Constructor */
    public X_RO_ModelHeader (Properties ctx, int RO_ModelHeader_ID, String trxName)
    {
      super (ctx, RO_ModelHeader_ID, trxName);
      /** if (RO_ModelHeader_ID == 0)
        {
			setRO_ModelHeader_ID (0);
        } */
    }

    /** Load Constructor */
    public X_RO_ModelHeader (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_RO_ModelHeader[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Author.
		@param Author 
		Author/Creator of the Entity
	  */
	public void setAuthor (String Author)
	{
		set_Value (COLUMNNAME_Author, Author);
	}

	/** Get Author.
		@return Author/Creator of the Entity
	  */
	public String getAuthor () 
	{
		return (String)get_Value(COLUMNNAME_Author);
	}

	/** Set Copyright.
		@param Copyright Copyright	  */
	public void setCopyright (String Copyright)
	{
		set_Value (COLUMNNAME_Copyright, Copyright);
	}

	/** Get Copyright.
		@return Copyright	  */
	public String getCopyright () 
	{
		return (String)get_Value(COLUMNNAME_Copyright);
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

	/** Set Version.
		@param Version 
		Version of the table definition
	  */
	public void setVersion (String Version)
	{
		set_Value (COLUMNNAME_Version, Version);
	}

	/** Get Version.
		@return Version of the table definition
	  */
	public String getVersion () 
	{
		return (String)get_Value(COLUMNNAME_Version);
	}
}