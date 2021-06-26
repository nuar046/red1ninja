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
package org.ninja.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for RO_CodeMaker
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_RO_CodeMaker 
{

    /** TableName=RO_CodeMaker */
    public static final String Table_Name = "RO_CodeMaker";

    /** AD_Table_ID=1000004 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 6 - System - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(6);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name GenerateModel */
    public static final String COLUMNNAME_GenerateModel = "GenerateModel";

	/** Set GenerateModel.
	  * Generate Model flag if checked will be generated by Module Designer
	  */
	public void setGenerateModel (boolean GenerateModel);

	/** Get GenerateModel.
	  * Generate Model flag if checked will be generated by Module Designer
	  */
	public boolean isGenerateModel();

    /** Column name GenerateModelFactory */
    public static final String COLUMNNAME_GenerateModelFactory = "GenerateModelFactory";

	/** Set GenerateModelFactory.
	  * Flag when checked will generate ModelFactory java in plugin, and model class of table name
	  */
	public void setGenerateModelFactory (boolean GenerateModelFactory);

	/** Get GenerateModelFactory.
	  * Flag when checked will generate ModelFactory java in plugin, and model class of table name
	  */
	public boolean isGenerateModelFactory();

    /** Column name GenerateProcess */
    public static final String COLUMNNAME_GenerateProcess = "GenerateProcess";

	/** Set GenerateProcess.
	  * Process name will be generated into a new stub for new plugin module
	  */
	public void setGenerateProcess (String GenerateProcess);

	/** Get GenerateProcess.
	  * Process name will be generated into a new stub for new plugin module
	  */
	public String getGenerateProcess();

    /** Column name GenerateTranslation */
    public static final String COLUMNNAME_GenerateTranslation = "GenerateTranslation";

	/** Set GenerateTranslation.
	  * Get translation from Yandex Online and set in Tab of selected table
	  */
	public void setGenerateTranslation (boolean GenerateTranslation);

	/** Get GenerateTranslation.
	  * Get translation from Yandex Online and set in Tab of selected table
	  */
	public boolean isGenerateTranslation();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name PluginLocation */
    public static final String COLUMNNAME_PluginLocation = "PluginLocation";

	/** Set PluginLocation	  */
	public void setPluginLocation (String PluginLocation);

	/** Get PluginLocation	  */
	public String getPluginLocation();

    /** Column name ProcessParameters */
    public static final String COLUMNNAME_ProcessParameters = "ProcessParameters";

	/** Set Process Parameters.
	  * Comma separated process parameter list
	  */
	public void setProcessParameters (String ProcessParameters);

	/** Get Process Parameters.
	  * Comma separated process parameter list
	  */
	public String getProcessParameters();

    /** Column name RO_CodeMaker_ID */
    public static final String COLUMNNAME_RO_CodeMaker_ID = "RO_CodeMaker_ID";

	/** Set CodeMaker	  */
	public void setRO_CodeMaker_ID (int RO_CodeMaker_ID);

	/** Get CodeMaker	  */
	public int getRO_CodeMaker_ID();

    /** Column name RO_CodeMaker_UU */
    public static final String COLUMNNAME_RO_CodeMaker_UU = "RO_CodeMaker_UU";

	/** Set RO_CodeMaker_UU	  */
	public void setRO_CodeMaker_UU (String RO_CodeMaker_UU);

	/** Get RO_CodeMaker_UU	  */
	public String getRO_CodeMaker_UU();

    /** Column name RO_ModelHeader_ID */
    public static final String COLUMNNAME_RO_ModelHeader_ID = "RO_ModelHeader_ID";

	/** Set ModelHeader	  */
	public void setRO_ModelHeader_ID (int RO_ModelHeader_ID);

	/** Get ModelHeader	  */
	public int getRO_ModelHeader_ID();

	public I_RO_ModelHeader getRO_ModelHeader() throws RuntimeException;

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo (int SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public int getSeqNo();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}