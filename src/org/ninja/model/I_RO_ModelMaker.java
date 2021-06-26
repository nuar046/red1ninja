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

/** Generated Interface for RO_ModelMaker
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_RO_ModelMaker 
{

    /** TableName=RO_ModelMaker */
    public static final String Table_Name = "RO_ModelMaker";

    /** AD_Table_ID=1000000 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

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

    /** Column name Callout */
    public static final String COLUMNNAME_Callout = "Callout";

	/** Set Callout.
	  * Fully qualified class names and method - separated by semicolons
	  */
	public void setCallout (String Callout);

	/** Get Callout.
	  * Fully qualified class names and method - separated by semicolons
	  */
	public String getCallout();

    /** Column name ColumnSet */
    public static final String COLUMNNAME_ColumnSet = "ColumnSet";

	/** Set ColumnSet	  */
	public void setColumnSet (String ColumnSet);

	/** Get ColumnSet	  */
	public String getColumnSet();

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

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

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

    /** Column name KanbanBoard */
    public static final String COLUMNNAME_KanbanBoard = "KanbanBoard";

	/** Set KanbanBoard	  */
	public void setKanbanBoard (boolean KanbanBoard);

	/** Get KanbanBoard	  */
	public boolean isKanbanBoard();

    /** Column name Master */
    public static final String COLUMNNAME_Master = "Master";

	/** Set Master	  */
	public void setMaster (String Master);

	/** Get Master	  */
	public String getMaster();

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

    /** Column name PrintFormat */
    public static final String COLUMNNAME_PrintFormat = "PrintFormat";

	/** Set PrintFormat	  */
	public void setPrintFormat (boolean PrintFormat);

	/** Get PrintFormat	  */
	public boolean isPrintFormat();

    /** Column name RO_ModelHeader_ID */
    public static final String COLUMNNAME_RO_ModelHeader_ID = "RO_ModelHeader_ID";

	/** Set ModelHeader	  */
	public void setRO_ModelHeader_ID (int RO_ModelHeader_ID);

	/** Get ModelHeader	  */
	public int getRO_ModelHeader_ID();

	public I_RO_ModelHeader getRO_ModelHeader() throws RuntimeException;

    /** Column name RO_ModelMaker_ID */
    public static final String COLUMNNAME_RO_ModelMaker_ID = "RO_ModelMaker_ID";

	/** Set ModelMaker	  */
	public void setRO_ModelMaker_ID (int RO_ModelMaker_ID);

	/** Get ModelMaker	  */
	public int getRO_ModelMaker_ID();

    /** Column name RO_ModelMaker_UU */
    public static final String COLUMNNAME_RO_ModelMaker_UU = "RO_ModelMaker_UU";

	/** Set RO_ModelMaker_UU	  */
	public void setRO_ModelMaker_UU (String RO_ModelMaker_UU);

	/** Get RO_ModelMaker_UU	  */
	public String getRO_ModelMaker_UU();

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

    /** Column name WorkflowModel */
    public static final String COLUMNNAME_WorkflowModel = "WorkflowModel";

	/** Set WorkflowModel	  */
	public void setWorkflowModel (String WorkflowModel);

	/** Get WorkflowModel	  */
	public String getWorkflowModel();

    /** Column name WorkflowStructure */
    public static final String COLUMNNAME_WorkflowStructure = "WorkflowStructure";

	/** Set WorkflowStructure	  */
	public void setWorkflowStructure (boolean WorkflowStructure);

	/** Get WorkflowStructure	  */
	public boolean isWorkflowStructure();
}
