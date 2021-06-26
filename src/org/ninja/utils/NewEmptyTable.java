/**
 * Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
 * If you shall try to cheat and find a loophole in this license, then KARMA will exact your share.
 * and your worldly gain shall come to naught and those who share shall gain eventually above you.
 * In compliance with previous GPLv2.0 works of ComPiere USA, Redhuan D. Oon (www.red1.org) and contributors 
*/
package org.ninja.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.adempiere.exceptions.DBException;
import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.model.MTable;
import org.compiere.model.M_Element;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Reference;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.ninja.process.GenerateModule;

/**
 * @author Redhuan D. Oon (red1org@gmail.com) adapted original simple class by
 * @author Mohamed Dernoun
 * @copyright TGI, France as submitted to http://idempiere.atlassian.net/browse/IDEMPIERE-1847
 * Modified to fit ModuleCreator event
 * Process used to create iDempiere Mandatory Fields with rich datatype handling
 */
public class NewEmptyTable
{
	private static CLogger log = CLogger.getCLogger (NewEmptyTable.class);
	private int tableID;
	private MTable mt;
	private String p_tableName;
	private String p_EntityType="XX"; //temporary to trigger Column New Event see RO_ModelMakerDocEvent
	
	public NewEmptyTable(MTable table){
		this.mt = table;
		this.p_tableName = table.getTableName();
		tableID = table.getAD_Table_ID();
	}

	/**
	 * set 7 main cols to create columns
	 * @return
	 * @throws Exception
	 */
	public MTable createNewTable() throws Exception
	{
		String enttype = mt.getEntityType();
		String trxName = mt.get_TrxName();
		// Create AD_Element "Table_Name" + _ID
		//obtain if exists
		M_Element id = M_Element.get (Env.getCtx(),p_tableName + "_ID",trxName);
		if (id==null) {
			id = new M_Element(Env.getCtx(), 0, trxName);
			id.setColumnName(p_tableName + "_ID");
			id.setPrintName(mt.getName());
			id.setName(mt.getName());
			//id.setDescription(mt.getDescription());
			//id.setHelp(mt.getHelp());
			id.setEntityType(enttype);
 		
			try {
				id.saveEx(trxName);
			} catch (Throwable t) {
				log.severe("AD_Element "+p_tableName + "_ID" + " already exists ?");
			}
		}
		//UUID column
		M_Element elementUU = M_Element.get (Env.getCtx(), p_tableName + "_UU");
		if (elementUU == null){
			elementUU = new M_Element (Env.getCtx(), p_tableName + "_UU", "U", trxName);
			elementUU.saveEx(trxName);
		}
		
		//ID Column
		MColumn columnID = new MColumn(Env.getCtx(), 0, trxName);
		columnID.setColumnName(p_tableName+"_ID");
		columnID.setName(mt.getName());
		columnID.setAD_Element_ID(id.get_ID());
		columnID.setAD_Reference_ID(13); //"ID"
		columnID.setFieldLength(22);
		columnID.setIsMandatory(true);
		columnID.setIsKey(true);
		columnID.setAD_Table_ID(tableID);
		columnID.setEntityType(p_EntityType);
		columnID.saveEx(trxName);
		
		String copylist = "'AD_Client_ID','AD_Org_ID','Created','CreatedBy','UpdatedBy','Updated','IsActive'";

		return createColumns(enttype, trxName, copylist);
	}
	/**
	 * Split string by commas into array and addColumn
	 * Force String reference with length = 22
	 * If exists, will pull from Element, datatype reference
	 * Allow datatype reference scripting i.e.  String, Q#Quantity, Y#Yes/No, D#DateTime, A#Amount, TableDirect_ID, L#List 
	 * @param columnlist
	 * @param trxName
	 */
	public void createColumns(String columnlist, String trxName){
		if (columnlist.length()==0)
			return;
		String[] colnames = columnlist.split(",");
		if (colnames.length==0)
			return;

		if (colnames.length==1 && columnlist.split("#").length>2)
			colnames = columnlist.split(" ");; //a single long string with more #elements? Means forgot to put in commas and used spaces instead.
		for (String colname:colnames){
			String[] split = colname.split("#");
			String type="";
			if (split.length==2){
				type = split[0].trim();
				colname = split[1];
			}
			colname = colname.trim();
			colname = colname.replace(" ", "");
			addColumn(type,colname,trxName);
		}
	}

	/**
	 * Default or S# String datatype with length = 22
	 * if ElementName exists, its datatype adopted
	 * Q# = Qty - 11
	 * Y# = Yes/No - 1
	 * A# = Amount - 11
	 * D# = DateTime - 11
	 * T# = Text - 2000 
	 * L# - List Validation /Table/Data
	 * @param colname
	 * @param trxName
	 */
	private void addColumn(String type, String colname, String trxName){
		MTable table = new MTable(Env.getCtx(),tableID,trxName);
		X_AD_Reference ref = null;
		String[] changename = colname.split(">");
		if (changename.length>1){
			colname = changename[0];
		}
		String[] refset=colname.split("=");
		String[] reflist=null;
		if (refset.length>1){
			colname = refset[0];
			reflist = refset[1].split("/");
		}
		int reference = 10; //String
		int length = 22;
		MColumn column = table.getColumn(colname);
		if (column==null){
			column = new MColumn(Env.getCtx(),0,trxName);
			//element existed?
			M_Element element = M_Element.get(Env.getCtx(), colname, trxName);
			//new element
			if (element==null){
				element = new M_Element (Env.getCtx(), colname, "U", trxName);
				element.saveEx(trxName);
			} 
			column.setAD_Element_ID(element.get_ID());
			column.setAD_Reference_ID(reference); 
			column.setColumnName(colname);
			column.setAD_Table_ID(tableID);
			column.setEntityType(p_EntityType); 
			column.setName(colname); 
			column.setFieldLength(length);
			column.setIsMandatory(false);
			column.setIsKey(false);	
		}

		if (type.equals("Q")) {//Quantity
			reference = 29;
			length = 11;
		}
		else if (type.equals("Y")) {//Boolean Yes/No
			reference = 20;		
			length = 1;
		}			
		else if (type.equals("A")) {//Amount
			reference = 12;	
			length = 11;
		}
		else if (type.equals("D")) //Date
			reference = 15;	
		else if (type.equals("d")) //Date
			reference = 16;
		else if (type.equals("T")) {//Text - 2000 chars
			reference = 14;	 
			length = 2000;
		} else if (type.equals("L")){//split = sign for L values
			reference = 17;
			ref = new Query(Env.getCtx(),X_AD_Reference.Table_Name,X_AD_Reference.COLUMNNAME_Name+" Like '"+colname+"%'", trxName)
			.setParameters()
			.first();
			if (ref==null) {//create new reference on the fly
				ref = new X_AD_Reference(Env.getCtx(),0,trxName);
				ref.setValidationType(X_AD_Reference.VALIDATIONTYPE_ListValidation);
				ref.setEntityType("U");
				ref.setName(colname);
				ref.saveEx(trxName);
			} else {
				if (ref.getValidationType().equals("L")) {
					column.setAD_Reference_ID(reference); 
					column.setAD_Reference_Value_ID(ref.get_ID()); 
				}
			}
			if (reflist!=null){
				for (int i=0;i<reflist.length;i++){
					boolean existed = new Query(Env.getCtx(),MRefList.Table_Name,MRefList.COLUMNNAME_Name+"=? AND "+MRefList.COLUMNNAME_AD_Reference_ID+"=?",trxName)
					.setParameters(reflist[i],ref.get_ID())
					.count()>0;
					if (existed) continue;
					MRefList reflistitem = new MRefList(Env.getCtx(),0,trxName); 
					reflistitem.setAD_Reference_ID(ref.get_ID());
					reflistitem.setName(reflist[i]);
					reflistitem.setValue(reflist[i].substring(0,reflist[i].length()<2?1:2));
					reflistitem.saveEx(trxName);
				}
				column.setAD_Reference_Value_ID(ref.get_ID()); 
			}
		} 
		column.setFieldLength(length); 
		column.setAD_Reference_ID(reference); 
		if (column.getName().endsWith("_ID") && reference==10){ 
			column.setAD_Reference_ID(DisplayType.TableDir);
			//remove suffix _ID
			String nosuffix = column.getName();
			column.setName(nosuffix.substring(0, nosuffix.length()-3));
		}
		if (type.isEmpty() || type.equals(""))
			column = CalloutProcessor(column);//if element is common, retrieve its settings
		column.setName(column.getName().trim());
		column.saveEx(trxName);
		
		if (changename.length>1)
			column.setDescription("**"+changename[1]);//pass to during window field creation to swap name
		
		column.setIsUpdateable(true);
		column.saveEx(trxName); //double save to avoid new status over-write at MColumn.java line 332 Sync Terminology
		GenerateModule.columnlistlength++;
		AddTable addTable = new AddTable();
		try {
			addTable.Synchronize(mt, column);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * create columns and synchronize
	 * @param enttype
	 * @param trxName
	 * @param copylist
	 * @return
	 * @throws Exception
	 */
	private MTable createColumns(String enttype, String trxName, String copylist)
			throws Exception {
		// 102 - AD_Reference
		String sql = "SELECT AD_COLUMN_ID from AD_COLUMN where ad_table_ID=102 and columnname IN (" + copylist + ")";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			ps = DB.prepareStatement(sql.toString(), trxName);
			rs = ps.executeQuery();

			while (rs.next())
			{
				MColumn col = new MColumn(Env.getCtx(), rs.getInt(1), trxName);
				MColumn dest = new MColumn(Env.getCtx(), 0, trxName);
				PO.copyValues(col, dest);
				dest.set_ValueNoCheck("AD_Table_ID", new Integer(tableID));
				dest.set_ValueNoCheck("EntityType", enttype);
				dest.setIsMandatory(false);
				dest.setDefaultValue("");
				try {
					dest.saveEx(trxName);
				} catch (Throwable t) {
					log.severe("Duplicate Column ? "+dest.getColumnName() + " - " +mt.getTableName());
				}

				AddTable addTable = new AddTable();
				addTable.Synchronize(mt, dest);
				dest.setIsMandatory(col.isMandatory());
				dest.setDefaultValue(col.getDefaultValue());
				dest.setDescription("");
				dest.setHelp("");
				try {
					dest.saveEx(trxName);
				} catch (Throwable t) {
					log.severe("Duplicate Column ? "+dest.getColumnName() + " - " +mt.getTableName());
				}
			}
		}
		catch (SQLException e)
		{
			throw new DBException(e, sql);
		}
		finally
		{
			DB.close(rs, ps);
			rs = null; ps = null;
		}
		return mt;
	}
	/**
	 * Taken from base plugin > org.compiere.model.Callout_AD_Column
	 * @param column
	 */
	public MColumn CalloutProcessor(MColumn column){
 
			/*MColumn memory = new MColumn(Env.getCtx(),column.get_ID(),null);
			if (memory!=null){
				int ad_reference_id = memory.getAD_Reference_ID();
				if (ad_reference_id == DisplayType.ID)
					ad_reference_id = DisplayType.TableDir;
				column.setAD_Reference_ID(ad_reference_id);
				column.setAD_Val_Rule_ID(memory.getAD_Val_Rule_ID());
				column.setAD_Reference_Value_ID(memory.getAD_Reference_Value_ID());
				column.setFieldLength(memory.getFieldLength());
				column.setDefaultValue(memory.getDefaultValue());
				column.setMandatoryLogic(memory.getMandatoryLogic());
				column.setReadOnlyLogic(memory.getReadOnlyLogic());
				column.setIsIdentifier(memory.isIdentifier());
				column.setIsUpdateable(memory.isUpdateable());
				column.setIsAlwaysUpdateable(memory.isAlwaysUpdateable());
				column.setFKConstraintType(memory.getFKConstraintType());
			}
			
 			
			*/
			// get defaults from most used case
			String sql = ""
					+ "SELECT AD_Reference_ID, "
					+ "       AD_Val_Rule_ID, "
					+ "       AD_Reference_Value_ID, "
					+ "       FieldLength, "
					+ "       DefaultValue, "
					+ "       MandatoryLogic, "
					+ "       ReadOnlyLogic, "
					+ "       IsIdentifier, "
					+ "       IsUpdateable, "
					+ "       IsAlwaysUpdateable, "
					+ "       FKConstraintType,"	
					+ "       COUNT(*) "
					+ "FROM   AD_Column "
					+ "WHERE  ColumnName = ? "
					+ "GROUP  BY AD_Reference_ID, "
					+ "          AD_Val_Rule_ID, "
					+ "          AD_Reference_Value_ID, "
					+ "          FieldLength, "
					+ "          DefaultValue, "
					+ "          MandatoryLogic, "
					+ "          ReadOnlyLogic, "
					+ "          IsIdentifier, "
					+ "          IsUpdateable, "
					+ "          IsAlwaysUpdateable, "
					+ "          FKConstraintType "
					+ "ORDER  BY COUNT(*) DESC ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setString(1, column.getColumnName());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					int ad_reference_id = rs.getInt(1);
					if (ad_reference_id == DisplayType.ID)
						ad_reference_id = DisplayType.TableDir;
					column.setAD_Reference_ID(ad_reference_id);
					column.setAD_Val_Rule_ID(rs.getInt(2));
					column.setAD_Reference_Value_ID(rs.getInt(3));
					column.setFieldLength(rs.getInt(4));
					column.setDefaultValue(rs.getString(5));
					column.setMandatoryLogic(rs.getString(6));
					column.setReadOnlyLogic(rs.getString(7));
					column.setIsIdentifier("Y".equals(rs.getString(8)));
					column.setIsUpdateable("Y".equals(rs.getString(9)));
					column.setIsAlwaysUpdateable("Y".equals(rs.getString(10)));
					column.setFKConstraintType(rs.getString(11));
				}
			}
			catch (SQLException e)
			{
				throw new DBException(e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			return column;
		}
}

