/**
 * Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
 * If you shall try to cheat and find a loophole in this license, then KARMA will exact your share.
 * and your worldly gain shall come to naught and those who share shall gain eventually above you.
 * In compliance with previous GPLv2.0 works of ComPiere USA, Redhuan D. Oon (www.red1.org) and iDempiere contributors 
*/
package org.ninja.utils;


import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_BUTTON;
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_DATE;
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_LIST;
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_NUMBER;
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_STRING;
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_TABLEDIR;
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_YES_NO;
import static org.compiere.model.SystemIDs.REFERENCE_DOCUMENTACTION; 
import static org.compiere.model.SystemIDs.REFERENCE_DATATYPE_ID;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MProcess;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.model.Query;
import org.compiere.model.X_AD_WF_Node;
import org.compiere.model.X_AD_Workflow;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.compiere.util.ValueNamePair;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWorkflow;
import org.ninja.model.MRO_ModelMaker;

/**
 *  @author red1 - Redhuan D. Oon (red1org@gmail.com) adapting from
 *	@author @copyright Nicolas Micoud (TGI)  @copyright TGI, France as submitted to http://idempiere.atlassian.net/browse/IDEMPIERE-1847
 *  @version $Id: 1
 */
public class AddTable extends SvrProcess 
{
	private String	p_tableName = "";
	private String	p_Name = "";
	private String	p_description = "";
	private int 	p_ValueLength = 0;
	private int 	p_NameLength = 0;	
	private boolean p_IsTranslation = false;
	private String	p_WindowType = "M";
	private String	p_EntityType = "U";
	private String	p_AccessLevel = "3";
	private boolean	p_insertWindowInMenu = true;
	private boolean p_IsStdUserWorkflow = false;
	private int		p_tableID = 0;
	private boolean	p_IsSQLStatement = false;
	private boolean p_IsKeyColumn = false;
	private String trxName;
	
	public AddTable(){
		
	}
	
	public AddTable(MTable table) throws Exception{
		trxName = table.get_TrxName();
		p_tableName = table.getTableName();
	}

	public MWindow makeMenuWindowFields(MTable table) throws Exception {
		MWindow window = createWindow(table);
		table.setAD_Window_ID(window.getAD_Window_ID());
		return window;
	}
	
	public String get_TrxName(){
		return trxName;
	}
	
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("TableName"))
				p_tableName = (String)para[i].getParameter();		
			else if (name.equals("Name"))
				p_Name = (String)para[i].getParameter();
			else if (name.equals("Description"))
				p_description = (String)para[i].getParameter();
			else if (name.equals("ValueLength"))
				p_ValueLength = para[i].getParameterAsInt();
			else if (name.equals("NameLength"))
				p_NameLength = para[i].getParameterAsInt();			
			else if (name.equals("IsTranslated"))
				p_IsTranslation = "Y".equals(para[i].getParameter());
			else if (name.equals("WindowType"))
				p_WindowType = (String)para[i].getParameter();
			else if (name.equals("EntityType"))
				p_EntityType = (String)para[i].getParameter();
			else if (name.equals("AccessLevel"))
				p_AccessLevel = (String)para[i].getParameter();
			else if (name.equals("insertWindowInMenu"))
				p_insertWindowInMenu = "Y".equals(para[i].getParameter());
			else if (name.equals("IsStdUserWorkflow"))
				p_IsStdUserWorkflow = "Y".equals(para[i].getParameter());
			else if (name.equals("SQLStatement"))
				p_IsSQLStatement = "Y".equals(para[i].getParameter());
			else if (name.equals("KeyColumn"))
				p_IsKeyColumn = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	protected String doIt() throws Exception
	{
		if (p_tableName.length()>25 && p_IsTranslation)
			throw new AdempiereUserError("TableName too long : " + p_tableName.length());

		if (Util.isEmpty(p_Name))
			p_Name = p_tableName;
		
		if (p_WindowType.length() == 0 && p_insertWindowInMenu)
			p_insertWindowInMenu = false;

		// Table
		MTable table = CreateTable(false);
		p_tableID = table.getAD_Table_ID();

		// Cr�ation de l'�l�ment tableName + _ID
		M_Element elementID = M_Element.get (getCtx (), p_tableName + "_ID");
		if (elementID == null && p_IsKeyColumn) {
			elementID = new M_Element (getCtx (), p_tableName + "_ID", p_EntityType, get_TrxName ());
			elementID.set_ValueOfColumn("AD_Client_ID", 0);
			elementID.saveEx(trxName);
		}
		
		// Cr�ation de l'�l�ment tableName + _UU
		M_Element elementUU = M_Element.get (getCtx (), p_tableName + "_UU");
		if (elementUU == null && p_IsKeyColumn){
			elementUU = new M_Element (getCtx (), p_tableName + "_UU", p_EntityType, get_TrxName ());
			elementUU.saveEx(trxName);
		}

		if (p_ValueLength > 0)
			CreateAndSynchronizeColumn(table, 620, false);	// Value
		if (p_NameLength > 0)
			CreateAndSynchronizeColumn(table, 469, false);	// Name

		if (p_IsStdUserWorkflow) {
			CreateAndSynchronizeColumn(table, 193, false);	// C_Currency_ID
			CreateAndSynchronizeColumn(table, 263, false);	// DateAcct
			CreateAndSynchronizeColumn(table, 287, false);	// DocAction
			CreateAndSynchronizeColumn(table, 289, false);	// DocStatus
			CreateAndSynchronizeColumn(table, 290, false);	// DocumentNo
			CreateAndSynchronizeColumn(table, 1047, false);	// Processed
			CreateAndSynchronizeColumn(table, 54128, false);// ProcessedOn
			CreateAndSynchronizeColumn(table, 524, false);	// Processing
		}

		if (p_IsKeyColumn){
			CreateAndSynchronizeColumn(table, elementUU.getAD_Element_ID(), false);
			CreateAndSynchronizeColumn(table, elementID.getAD_Element_ID(), true);	// PK derni�re colonne cr��e pour lancer le ColumnSync
		}

		// Translation table >> voir classe TranslationTable
		if (p_IsTranslation) {
			// Cr�er la table
			MTable table_trl = CreateTable(true);

			CreateAndSynchronizeColumn(table_trl, 109, false);	// AD_Language
			CreateAndSynchronizeColumn(table_trl, 420, false);	// IsTranslated

			if (p_NameLength > 0)
				CreateAndSynchronizeColumn(table_trl, 469, false);	// Name
			
			if (p_IsKeyColumn){
				
				M_Element elementTrlUU = M_Element.get (getCtx (), table_trl.getTableName() + "_UU");
				if (elementTrlUU == null) {
					elementTrlUU = new M_Element (getCtx (), table_trl.getTableName() + "_UU", p_EntityType, get_TrxName ());
					elementTrlUU.saveEx(trxName);
				}
				CreateAndSynchronizeColumn(table_trl, elementTrlUU.getAD_Element_ID(), false);	// tableName_Trl_UU
				
				CreateAndSynchronizeColumn(table_trl, elementID.getAD_Element_ID(), true);	// ID table parent ; derni�re colonne cr��e pour lancer le ColumnSync
			}
		}

		if (p_WindowType.length() > 0) {
			MWindow window = createWindow(table);
			table.setAD_Window_ID(window.getAD_Window_ID());
			table.saveEx(trxName);
		}

		if (!p_IsSQLStatement)	// affichage de la table cr��e (car non fait via m�thode getSQLCreate)
			addLog (table.toString());

		return "@ProcessOK@";
	}	//	doIt

	MTable CreateTable(boolean trl) throws Exception
	{
		MTable table = new MTable(getCtx(), 0, get_TrxName());
		table.setTableName(p_tableName + (trl ? "_Trl" : ""));
		table.setName(p_Name + (trl ? " Trl" : ""));
		table.setDescription(p_description + (trl ? " Trl" : ""));
		table.setEntityType(p_EntityType);
		table.setAccessLevel(p_AccessLevel);
		table.set_ValueOfColumn("AD_Client_ID", 0);
		table.saveEx(trxName);

		// Colonnes obligatoires
		CreateAndSynchronizeColumn(table, 102, false);	// AD_Client_ID
		CreateAndSynchronizeColumn(table, 113, false);	// AD_Org_ID
		CreateAndSynchronizeColumn(table, 245, false);	// Created
		CreateAndSynchronizeColumn(table, 246, false);	// CreatedBy
		CreateAndSynchronizeColumn(table, 348, false);	// IsActive
		CreateAndSynchronizeColumn(table, 607, false);	// Updated
		CreateAndSynchronizeColumn(table, 608, false);	// UpdatedBy

		return table;
	}

	void CreateAndSynchronizeColumn(MTable table, int Element_ID, boolean isSynchro) throws Exception
	{
		// Cr�er la colonne et la sauver

		MColumn column = new MColumn(table);

		M_Element element = new M_Element(getCtx(), Element_ID, get_TrxName());
		column.setColumnName (element.getColumnName ());
		column.setName (element.getName ());
		column.setDescription (element.getDescription ());
		column.setHelp (element.getHelp ());
		column.setAD_Element_ID (element.getAD_Element_ID ());

		if (Element_ID==102) {	// AD_Client_ID
			column.setAD_Reference_ID(19);	// TableDirect
			column.setDefaultValue("@#AD_Client_ID@");
			column.setIsMandatory(true);
			column.setIsUpdateable(false);
			column.setReadOnlyLogic("1=1");
		}
		else if (Element_ID==113) {// AD_Org_ID
			column.setAD_Reference_ID(19);	// TableDirect
			column.setDefaultValue("@AD_Org_ID@");
			column.setIsMandatory(true);
			column.setIsUpdateable(false);
		}
		else if (Element_ID==245 || Element_ID==607) {	// Created & Updated
			column.setAD_Reference_ID(16);	// Date+Time
			column.setIsMandatory(true);
			column.setIsUpdateable(false);
		}
		else if (Element_ID==246 || Element_ID==608) {	// CreatedBy & UpdatedBy
			column.setAD_Reference_ID(18);	// Table
			column.setAD_Reference_Value_ID (110);
			column.setIsMandatory(true);
			column.setIsUpdateable(false);
		}
		else if (Element_ID==348 || Element_ID==420 || Element_ID == 1047 || Element_ID == 524)	{ // cases � cocher
			column.setAD_Reference_ID(REFERENCE_DATATYPE_YES_NO);	// Yes-No
			column.setIsMandatory(true);
			column.setIsUpdateable(true);
			column.setFieldLength(1);
			column.setDefaultValue("N");
			if (Element_ID==348)	// IsActive
				column.setDefaultValue("Y");
		}
		else if (Element_ID==109) { // AD_Language
			column.setAD_Reference_ID(18);	// Table
			column.setAD_Reference_Value_ID(106); // AD_Language
			column.setIsMandatory(true);
			column.setIsUpdateable(false);
			column.setFieldLength(6);
			column.setIsParent(true);
		}
		else if (Element_ID==620 || Element_ID==469 || Element_ID==290)	{ // Value & Name & DocumentNo
			column.setAD_Reference_ID(REFERENCE_DATATYPE_STRING);	// String
			column.setIsMandatory(true);
			column.setIsUpdateable(true);
			column.setIsSelectionColumn(true);

			int length = 0;
			if (Element_ID==620) // Value
				length=p_ValueLength;
			else if (Element_ID==469) {	// Name
				length=p_NameLength;
				column.setIsIdentifier(true);
				if (p_IsTranslation && !table.getTableName().toUpperCase().endsWith("_TRL"))
					column.setIsTranslated(true);
			}
			else if (Element_ID==290)
				length=30;
			column.setFieldLength(length);
		}
		else if (Element_ID==193) {	// C_Currency_ID
			column.setAD_Reference_ID(REFERENCE_DATATYPE_TABLEDIR);
			column.setIsMandatory(true);
			column.setIsUpdateable(true);
			column.setFieldLength(22);
			column.setDefaultValue("@C_Currency_ID@");
		}
		else if (Element_ID==263) {	// DateAcct
			column.setAD_Reference_ID(REFERENCE_DATATYPE_DATE);
			column.setIsMandatory(true);
			column.setIsUpdateable(true);
			column.setFieldLength(7);
			column.setDefaultValue("@#Date@");
		}
		else if (Element_ID==617 || Element_ID==618) {	// ValidFrom & ValidTo
			column.setAD_Reference_ID(REFERENCE_DATATYPE_DATE);
			column.setIsUpdateable(true);
			column.setFieldLength(7);
		}
		else if (Element_ID==287) {	// DocAction
			// Creation du workflow
			MWorkflow wf = createWorkflow();
			
			// Creation du process (doit ref�rencer le wf cr�� pr�c�demment)
			MProcess process = new MProcess(getCtx(), 0, get_TrxName());
			process.setValue(p_tableName + " Process");
			process.setName("Process " + p_Name);
			process.setEntityType(p_EntityType);
			process.setAccessLevel(p_AccessLevel);
			process.setAD_Workflow_ID(wf.getAD_Workflow_ID());
			process.saveEx();
			
			column.setAD_Reference_ID(REFERENCE_DATATYPE_BUTTON); 
			column.setAD_Reference_Value_ID(REFERENCE_DOCUMENTACTION);
			column.setIsMandatory(true);
			column.setIsUpdateable(true);
			column.setFieldLength(2);
			column.setDefaultValue("CO");
			column.setAD_Process_ID(process.getAD_Process_ID());
		}
		else if (Element_ID==289) {	// DocStatus
			column.setAD_Reference_ID(REFERENCE_DATATYPE_LIST);
			column.setIsMandatory(true);
			column.setIsUpdateable(true);
			column.setFieldLength(2);
			column.setAD_Reference_Value_ID(0);//TODO find reference value
			column.setDefaultValue("DR");
		}
		else if (Element_ID==54128) {	// ProcessedOn
			column.setAD_Reference_ID(REFERENCE_DATATYPE_NUMBER);
			column.setFieldLength(20);
		}
		else if (element.getName().equalsIgnoreCase(table.getTableName() + "_ID")) {	// ID de la table
			column.setIsKey(true);
			column.setAD_Reference_ID(REFERENCE_DATATYPE_ID);	// ID
			column.setIsMandatory(true);
			column.setFieldLength(22);			
		}
		else if (element.getName().equalsIgnoreCase((table.getTableName().substring(0, table.getTableName().length()-4)) + "_ID")) { // ID de la table parent
			column.setAD_Reference_ID(30);	// Search
			column.setIsParent(true);
			column.setIsMandatory(true);
		}
		else if (element.getName().equalsIgnoreCase(table.getTableName() + "_UU")) {	// UU de la table
			column.setAD_Reference_ID(REFERENCE_DATATYPE_STRING);	// ID
			column.setFieldLength(36);			
		}

		column.saveEx(trxName);
		
		if (isSynchro && p_IsSQLStatement)	// on ne fait la synchro que sur la derni�re colonne pour g�n�rer correctement les pk
			Synchronize(table, column);				
	}

	public void Synchronize(MTable table, MColumn column) throws Exception
	{
		// SynchronizeColumn (recopie du doIt, avec mise en commentaire de return sql)
		log.info("C_Column_ID=" + column.toString());

		//	Find Column in Database
		Connection conn = null;
		try {
			conn = DB.getConnectionRO();
			DatabaseMetaData md = conn.getMetaData();
			String catalog = DB.getDatabase().getCatalog();
			String schema = DB.getDatabase().getSchema();
			String tableName = table.getTableName();
			if (md.storesUpperCaseIdentifiers())
			{
				tableName = tableName.toUpperCase();
			}
			else if (md.storesLowerCaseIdentifiers())
			{
				tableName = tableName.toLowerCase();
			}
			int noColumns = 0;
			String sql = null;
			//
			ResultSet rs = md.getColumns(catalog, schema, tableName, null);
			while (rs.next()) {
				noColumns++;
				String columnName = rs.getString ("COLUMN_NAME");
				if (!columnName.equalsIgnoreCase(column.getColumnName()))
					continue;

				//	update existing column
				boolean notNull = DatabaseMetaData.columnNoNulls == rs.getInt("NULLABLE");
				sql = column.getSQLModify(table, column.isMandatory() != notNull);
				break;
			}
			rs.close();
			rs = null;

			//	No Table
			if (noColumns == 0)
				sql = getSQLCreate (table);
			//	No existing column
			else if (sql == null)
				sql = column.getSQLAdd(table);

			//Valeur par d�faut pour Created et Updated
			if( DB.isOracle())
					sql += "; ALTER TABLE " + tableName + " MODIFY (CREATED DATE DEFAULT SYSDATE); "
					+ "ALTER TABLE " + tableName + " MODIFY (UPDATED DATE DEFAULT SYSDATE)";
			else if (DB.isPostgreSQL()) ; // TODO
				//ALTER TABLE pa_dashboardcontent_trl ALTER created SET DEFAULT now();
				//ALTER TABLE pa_dashboardcontent_trl ALTER updated SET DEFAULT now();

			// Foreign keys pour tables traduction
			if (p_IsTranslation && tableName.toUpperCase().endsWith("_TRL"))
			{
				// trouver le nom de la table parent (-_Trl)
				String tableParentName = table.getTableName().substring(0, table.getTableName().length()-4); // on enl�ve _Trl
				MTable tableParent = MTable.get(getCtx(), tableParentName);

				String constraintLang = "FK_ADLang_" + table.getAD_Table_ID();
				String constraintParent = "FK_" + tableParent.getAD_Table_ID() + "_" + column.getAD_Column_ID();

				if( DB.isOracle())
					sql += "; ALTER TABLE " + tableName + " ADD (CONSTRAINT " + constraintLang + " FOREIGN KEY (AD_LANGUAGE) REFERENCES AD_LANGUAGE); "
					+ "ALTER TABLE " + tableName + " ADD (CONSTRAINT " + constraintParent + " FOREIGN KEY (" + tableParentName.toUpperCase() + "_ID) REFERENCES " + tableParentName.toUpperCase() + ")";

				//ALTER TABLE PA_DashboardContent_Trl ADD (CONSTRAINT ADLangu_PADashboardContentTrl FOREIGN KEY (AD_Language) REFERENCES AD_Language);
                //ALTER TABLE PA_DashboardContent_Trl ADD (CONSTRAINT PADashboardContent_PADashboard FOREIGN KEY (PA_DashboardContent_ID) REFERENCES PA_DashboardContent);
			}

			int no = 0;
			if (sql.indexOf(DB.SQLSTATEMENT_SEPARATOR) == -1) {
				no = DB.executeUpdate(sql, false, get_TrxName());
				addLog (0, null, new BigDecimal(no), sql);
			}
			else {
				String statements[] = sql.split(DB.SQLSTATEMENT_SEPARATOR);
				for (int i = 0; i < statements.length; i++)
				{
					int count = DB.executeUpdate(statements[i], false, get_TrxName());
					addLog (0, null, new BigDecimal(count), statements[i]);
					no += count;
				}
			} 
			if (no == -1) {
				String msg = "@Error@ ";
				ValueNamePair pp = CLogger.retrieveError();
				if (pp != null)
					msg = pp.getName() + " - ";
				msg += sql;
				throw new AdempiereUserError (msg);
			}
			//return sql;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {}
			}
		} 
	}

	MWindow createWindow(MTable table) throws Exception
	{ 
		//query if window already exists, then bypass	
		MWindow window = new Query(Env.getCtx(),MWindow.Table_Name,MWindow.COLUMNNAME_AD_Window_ID+"=?",trxName)
		.setParameters(table.getAD_Window_ID()).first();
		String windowName = p_tableName;
		if (window==null){
			window = new MWindow(getCtx(), 0, trxName);
			
			//remove prefix and separate leading caps
			String prefix[] = p_tableName.split("_");
			if (prefix.length>1){
				windowName = "";
				for (int i =1; i< prefix.length; i++) {
					windowName = windowName+prefix[i];
				}
			}			
			windowName = MRO_ModelMaker.leadingCapsSpacing(windowName);
			window.setName(windowName);
			window.setWindowType(p_WindowType);
			window.setEntityType(p_EntityType);
			window.setHelp(table.getHelp());
			window.saveEx(trxName);
		} 
		addLog (window.toString());
		// query if Tab already exists, then bypass
		MTab tab = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_AD_Table_ID+"=?",trxName)
		.setParameters(table.get_ID()).first();
		if (tab==null){
			tab = new MTab(window);
			tab.setName(windowName);
			tab.setAD_Table_ID(table.getAD_Table_ID());
			
			//get last tab's sequence no.
			//sub-tabs is set after it and away from other sub-tabs groups to avoid cross subtabbing.
			int wintabs = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_AD_Window_ID+"=?",trxName)
			.setParameters(window.get_ID())
			.count();
			tab.setSeqNo(wintabs*10+10);
			tab.setEntityType("U");
			tab.setTabLevel(0);
			tab.saveEx(trxName);
		}

		createTabFields(tab);
		return window;
	}

	public void createTabFields(MTab tab) throws SQLException {
		int ten = 10;	
		boolean toggle=false;
		// Champs 1er onglet Useless as the CreateFieldFromTab is better
			String sql = "SELECT * FROM AD_Column c "
					+ "WHERE AD_Table_ID=?"	
					+ " AND NOT (Name LIKE 'Created%' OR Name LIKE 'Updated%')"
					+ " AND IsActive='Y' "
					+ "ORDER BY Name";
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, tab.getAD_Table_ID());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				MColumn column = new MColumn (getCtx(), rs, get_TrxName()); 
				String columnName = column.getColumnName();
				//
				MField field = new Query(getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Column_ID+"=? AND "
						+MField.COLUMNNAME_AD_Tab_ID+"=?",get_TrxName())
						.setParameters(column.get_ID(),tab.get_ID())
						.first();
				if (field==null) {
					field = new MField (tab);
					log.info("Tab-Field created: "+tab.getName()+"-"+field.getName() );
				}
				field.setColumn(column);
				if (column.isKey()){
					field.setIsDisplayed(false);
					field.setIsDisplayedGrid(false);
				}
				// S�quence & IsSameLine
				int seqNo = 0;
				if (columnName.equals("AD_Client_ID")){
					seqNo = 3;
					field.setIsDisplayedGrid(false);
				}
					
				else if (columnName.equals("AD_Org_ID"))
				{
					seqNo = 6;
					field.setIsSameLine(true);
					field.setIsDisplayedGrid(false);
					field.setXPosition(3);
				}
				else {
					seqNo = seqNo+ten;	
					//toggling X Position
					if (toggle)
						field.setXPosition(3);
					if (toggle)
						toggle=false;
					else 
						toggle=true;
										
				}
				if (columnName.equals("IsActive") && p_ValueLength > 0) {
					field.setIsSameLine(true);	// on met IsActive � droite de Value
					field.setIsDisplayedGrid(false);
				}	
				
				
				String fieldname = MRO_ModelMaker.leadingCapsSpacing(column.getName());
				
				String changename = column.getDescription();
				if (changename!=null && changename.startsWith("**")){
					 fieldname = MRO_ModelMaker.leadingCapsSpacing(changename.substring(2)); 
				}
				field.setIsCentrallyMaintained(false);
				field.setName(fieldname);	
				field.setSeqNo(seqNo);
				if (field.getAD_Client_ID()!=0)
					field.set_ValueOfColumn("AD_Client_ID", 0);
				field.saveEx(trxName);
				log.fine("Tab-Field updated: "+tab.getName()+"-"+field.getName() );
			}
			/** TODO red1 migrate from DocEvent
			 * 	String changename = column.getDescription();
		if (changename!=null && changename.startsWith("**")){
			String fieldname = changename.substring(2); 
		
			fieldname = MRO_ModelMaker.leadingCapsSpacing(fieldname);
 
			List<MField> fieldtabs = new Query(Env.getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Column_ID+"=?",trxName)
			.setParameters(column.get_ID()).list();
			if (fieldtabs!=null){
				for (MField field:fieldtabs){
					field.setName(fieldname);
					field.saveEx(trxName);
				}
			}
		}
			 */
	}

	int getFieldSeqNo(String columnName, boolean trl)
	{
		if (columnName.equals("Value"))
			return 30;
		else if (columnName.equals("IsActive"))
			return 40;
		else if (columnName.equals("Name"))
			return 50;		
		else
			return 500;
	}
	
	public String getSQLCreate(MTable table)
	{
		String tableName=table.getTableName();
		StringBuffer sb = new StringBuffer("CREATE TABLE ")
			.append(tableName).append(" (");
		//
		boolean hasPK = false;
		boolean hasParents = false;
		StringBuffer constraints = new StringBuffer();
		MColumn[]	m_columns = null;
		m_columns = table.getColumns(true);
		for (int i = 0; i < m_columns.length; i++)
		{
			MColumn column = m_columns[i];
			String colSQL = column.getSQLDDL();
			if ( colSQL != null )
			{
				if (i > 0)
					sb.append(", ");
					sb.append(column.getSQLDDL());
			}
			else // virtual column
				continue;
			//
			if (column.isKey())
				hasPK = true;
			if (column.isParent())
				hasParents = true;
			String constraint = column.getConstraint(tableName);
			if (constraint != null && constraint.length() > 0)
				constraints.append(", ").append(constraint);
		}
		//	Multi Column PK
		if (!hasPK && hasParents)
		{
			StringBuffer cols = new StringBuffer();
			for (int i = 0; i < m_columns.length; i++)
			{
				MColumn column = m_columns[i];
				if (!column.isParent())
					continue;
				if (cols.length() > 0)
					cols.append(", ");
				cols.append(column.getColumnName());
			}
			
			if (tableName.toUpperCase().endsWith("_TRL"))
			{
				String constraintName = "";
				if (tableName.length()>22)
					constraintName=tableName.replace("_", "") + "Key";
				else
					constraintName = tableName + "_Key";
				
				sb.append(", CONSTRAINT ")
					.append(constraintName).append(" PRIMARY KEY (")
					.append(cols).append(")");				
			}
			else //(=standard)
			{
			sb.append(", CONSTRAINT ")
				.append(tableName).append("_Key PRIMARY KEY (")
				.append(cols).append(")");
			}
		}
		
		sb.append(constraints)
			.append(")");
		return sb.toString();
	}	//	getSQLCreate


	
	MWorkflow createWorkflow()
	{
		MWorkflow wf = new MWorkflow(getCtx(), 0, get_TrxName());
		wf.setValue("Process_" + p_tableName);
		wf.setName("Process_" + p_tableName);
		wf.setWorkflowType(X_AD_Workflow.WORKFLOWTYPE_DocumentProcess);
		wf.setAD_Table_ID(p_tableID);
		wf.setEntityType(p_EntityType);
		wf.setAuthor("osef");
		wf.saveEx();
		
		// Noeuds
		MWFNode docAuto = createWorkflowNode(wf, "DocAuto", X_AD_WF_Node.ACTION_DocumentAction, X_AD_WF_Node.DOCACTION_None);
		MWFNode docComplete = createWorkflowNode(wf, "DocComplete", X_AD_WF_Node.ACTION_DocumentAction, X_AD_WF_Node.DOCACTION_Complete);
		MWFNode docPrepare = createWorkflowNode(wf, "DocPrepare", X_AD_WF_Node.ACTION_DocumentAction, X_AD_WF_Node.DOCACTION_Prepare);
		MWFNode start = createWorkflowNode(wf, "Start", X_AD_WF_Node.ACTION_WaitSleep, null);
		
		// Transitions
		createWorkflowNodeNext(start, docPrepare.getAD_WF_Node_ID(), 10, "(Standard Approval)", true);
		createWorkflowNodeNext(start, docAuto.getAD_WF_Node_ID(), 100, "(Standard Transition)", false);
		createWorkflowNodeNext(docPrepare, docComplete.getAD_WF_Node_ID(), 100, "(Standard Transition)", false);
		
		wf.setAD_WF_Node_ID(start.getAD_WF_Node_ID());
		wf.saveEx();
		return wf;
	}
	
	MWFNode createWorkflowNode(MWorkflow wf, String Name, String Action, String DocAction)
	{
		MWFNode wfn = new MWFNode(wf, "(" + Name + ")", "(" + Name + ")");
		wfn.setDescription("(Standard Node)");
		wfn.setEntityType(p_EntityType);
		wfn.setAction(Action);
		wfn.setDocAction(DocAction);
		wfn.saveEx();
		return wfn;
	}
	
	void createWorkflowNodeNext(MWFNode wfn, int nodeNextID, int seq, String description, boolean isStdUserWF)
	{
		MWFNodeNext wfnn = new MWFNodeNext(wfn, nodeNextID);
		wfnn.setSeqNo(seq);
		wfnn.setDescription(description);
		wfnn.setIsStdUserWorkflow(isStdUserWF);
		wfnn.setEntityType(p_EntityType);
		wfnn.saveEx();
	}

}	//	AddTable