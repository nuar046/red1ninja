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

/** Generated Model for RO_CodeMaker
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_RO_CodeMaker extends PO implements I_RO_CodeMaker, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20161106L;

    /** Standard Constructor */
    public X_RO_CodeMaker (Properties ctx, int RO_CodeMaker_ID, String trxName)
    {
      super (ctx, RO_CodeMaker_ID, trxName);
      /** if (RO_CodeMaker_ID == 0)
        {
			setRO_CodeMaker_ID (0);
        } */
    }

    /** Load Constructor */
    public X_RO_CodeMaker (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_RO_CodeMaker[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set GenerateModel.
		@param GenerateModel 
		Generate Model flag if checked will be generated by Module Designer
	  */
	public void setGenerateModel (boolean GenerateModel)
	{
		set_Value (COLUMNNAME_GenerateModel, Boolean.valueOf(GenerateModel));
	}

	/** Get GenerateModel.
		@return Generate Model flag if checked will be generated by Module Designer
	  */
	public boolean isGenerateModel () 
	{
		Object oo = get_Value(COLUMNNAME_GenerateModel);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set GenerateModelFactory.
		@param GenerateModelFactory 
		Flag when checked will generate ModelFactory java in plugin, and model class of table name
	  */
	public void setGenerateModelFactory (boolean GenerateModelFactory)
	{
		set_Value (COLUMNNAME_GenerateModelFactory, Boolean.valueOf(GenerateModelFactory));
	}

	/** Get GenerateModelFactory.
		@return Flag when checked will generate ModelFactory java in plugin, and model class of table name
	  */
	public boolean isGenerateModelFactory () 
	{
		Object oo = get_Value(COLUMNNAME_GenerateModelFactory);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set GenerateProcess.
		@param GenerateProcess 
		Process name will be generated into a new stub for new plugin module
	  */
	public void setGenerateProcess (String GenerateProcess)
	{
		set_Value (COLUMNNAME_GenerateProcess, GenerateProcess);
	}

	/** Get GenerateProcess.
		@return Process name will be generated into a new stub for new plugin module
	  */
	public String getGenerateProcess () 
	{
		return (String)get_Value(COLUMNNAME_GenerateProcess);
	}

	/** Set GenerateTranslation.
		@param GenerateTranslation 
		Get translation from Yandex Online and set in Tab of selected table
	  */
	public void setGenerateTranslation (boolean GenerateTranslation)
	{
		set_Value (COLUMNNAME_GenerateTranslation, Boolean.valueOf(GenerateTranslation));
	}

	/** Get GenerateTranslation.
		@return Get translation from Yandex Online and set in Tab of selected table
	  */
	public boolean isGenerateTranslation () 
	{
		Object oo = get_Value(COLUMNNAME_GenerateTranslation);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set PluginLocation.
		@param PluginLocation PluginLocation	  */
	public void setPluginLocation (String PluginLocation)
	{
		set_Value (COLUMNNAME_PluginLocation, PluginLocation);
	}

	/** Get PluginLocation.
		@return PluginLocation	  */
	public String getPluginLocation () 
	{
		return (String)get_Value(COLUMNNAME_PluginLocation);
	}

	/** Set Process Parameters.
		@param ProcessParameters 
		Comma separated process parameter list
	  */
	public void setProcessParameters (String ProcessParameters)
	{
		set_Value (COLUMNNAME_ProcessParameters, ProcessParameters);
	}

	/** Get Process Parameters.
		@return Comma separated process parameter list
	  */
	public String getProcessParameters () 
	{
		return (String)get_Value(COLUMNNAME_ProcessParameters);
	}

	/** Set CodeMaker.
		@param RO_CodeMaker_ID CodeMaker	  */
	public void setRO_CodeMaker_ID (int RO_CodeMaker_ID)
	{
		if (RO_CodeMaker_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RO_CodeMaker_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RO_CodeMaker_ID, Integer.valueOf(RO_CodeMaker_ID));
	}

	/** Get CodeMaker.
		@return CodeMaker	  */
	public int getRO_CodeMaker_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RO_CodeMaker_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set RO_CodeMaker_UU.
		@param RO_CodeMaker_UU RO_CodeMaker_UU	  */
	public void setRO_CodeMaker_UU (String RO_CodeMaker_UU)
	{
		set_ValueNoCheck (COLUMNNAME_RO_CodeMaker_UU, RO_CodeMaker_UU);
	}

	/** Get RO_CodeMaker_UU.
		@return RO_CodeMaker_UU	  */
	public String getRO_CodeMaker_UU () 
	{
		return (String)get_Value(COLUMNNAME_RO_CodeMaker_UU);
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
}