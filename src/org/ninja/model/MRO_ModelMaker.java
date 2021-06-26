/**
* Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.
* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,
* and your worldly gain shall come to naught and those who share shall gain eventually above you.
* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.
* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)
*/

package org.ninja.model;

import java.sql.ResultSet; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.AccessSqlParser;
import org.compiere.model.AccessSqlParser.TableInfo;
import org.compiere.model.I_AD_InfoColumn;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MEntityType;
import org.compiere.model.MField;
import org.compiere.model.MInfoColumn;
import org.compiere.model.MInfoWindow;
import org.compiere.model.MMenu;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.MRole;
import org.compiere.model.MRule;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.model.M_Element;
import org.compiere.model.Query;
import org.compiere.model.X_AD_InfoColumn;
import org.compiere.model.X_AD_Menu;
import org.compiere.model.X_AD_PrintFormatItem;
import org.compiere.model.X_AD_WF_Node;
import org.compiere.model.X_AD_Workflow;
import org.compiere.print.MPrintFormat;
import org.compiere.print.MPrintFormatItem;
import org.compiere.print.MPrintPaper;
import org.compiere.process.ProcessInfo; 
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFNodeNext;
import org.compiere.wf.MWorkflow;
import org.kanbanboard.model.MKDB_KanbanControlAccess;
import org.kanbanboard.model.MKanbanBoard;
import org.kanbanboard.model.MKanbanPriority;
import org.ninja.process.CreateStatusProcess;
import org.ninja.utils.AddTable;
import org.ninja.utils.NewEmptyTable;

/**
 * 
 * @author red1
 *
 */
public class MRO_ModelMaker extends X_RO_ModelMaker{

	private static final long serialVersionUID = -1L;
	private String newTableName;
	
	private String infoWindowTableSet[][]=new String[5][8];
	private int seqNo=0;
	private int AD_Table_ID;
	private String p_EntityType="U"; 
    
	public MRO_ModelMaker(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MRO_ModelMaker(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	/**
	 * Handle new table and window creation. 
	 * WinTabFields handled by RO_ModelMakerDocEvent IEventTopics.PO_AFTER_NEW, MColumn.Table_Name 
	 * @return
	 */
	public MMenu handleModel() {
		if (getName()!=null)
			newTableName = getName().replaceAll("\\s","");
		if (newTableName==null && getWorkflowModel() != null){
			return createWorkflow();
		}
		if (newTableName==null)
			return null;
		
		String multiTableNames[] = newTableName.split(",");
		if (multiTableNames.length>1) {
			MInfoWindow info = createInfoWindow(multiTableNames);
			createInfoColumnSet(getColumnSet(),info);
			return null;
		} else {
			String[] masterdetailcheck = newTableName.split(">");
			if (masterdetailcheck.length>1)
				newTableName=masterdetailcheck[0];
			MMenu menu = null;
			MTable tablepo = new Query(Env.getCtx(), MTable.Table_Name,"UPPER("+MTable.COLUMNNAME_TableName+")=UPPER(?)",get_TrxName())
			.setParameters(newTableName)
			.first();
			if (getMaster()==null && tablepo!=null && !tablepo.getEntityType().equals(MEntityType.ENTITYTYPE_UserMaintained)){
				if (isKanbanBoard() && tablepo.getEntityType().equals(MEntityType.ENTITYTYPE_Dictionary)) {
					AD_Table_ID = tablepo.getAD_Table_ID(); 
					createKanbanBoard();
				}
				return null;
			}
				
			NewEmptyTable nt = null;
			if (tablepo==null) {
				tablepo = new MTable(Env.getCtx(),0,get_TrxName());
				String prefix[] = newTableName.split("_"); //to check for prefix - MY_tableName
				String newName = newTableName;
				if (prefix.length>1)
					newName = prefix[1]; //just take the name without prefix
				tablepo.setName(newName);
				tablepo.setTableName(newTableName);
				tablepo.setHelp(getHelp());
				tablepo.setAccessLevel("3");//Client & Org
				tablepo.setIsDeleteable(true); 
				tablepo.saveEx(get_TrxName());
				nt = new NewEmptyTable(tablepo);
				try {
					//Copy over all columns from AD_New
					//Synch to Database
					tablepo = nt.createNewTable();
				} catch (Exception e) { 
					e.printStackTrace();
				}

			}
			nt = new NewEmptyTable(tablepo);
			AD_Table_ID = tablepo.getAD_Table_ID(); 
			//insert from ColumnList, enacting new MColumn.PO_AFTER_NEW below
			//do it if it is new.			
			String columnlist = getColumnSet();
			if (columnlist!=null && !columnlist.isEmpty()){
				List<MColumn> existing = new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
					.setParameters(tablepo.get_ID()).setOnlyActiveRecords(true).list();
				String[] columnset = columnlist.split(",");	
				createColumns(nt, columnlist);
			}
			
			//Callout creation
			String calloutString = getCallout();
			if (calloutString!=null && !calloutString.isEmpty()){
				createCallout(calloutString,tablepo.get_ID());
			}
			
			if (isWorkflowStructure()){
				nt.createColumns("Name,DocAction,DocStatus,Processed,DocumentNo,IsApproved", get_TrxName());
				MWorkflow wf = createWorkflowStructure();
				MProcess process = new Query(Env.getCtx(),MProcess.Table_Name,MProcess.COLUMNNAME_Value+"=?",get_TrxName())
						.setParameters(newTableName + " Process").first();
				if (process==null){
					process = new MProcess(getCtx(), 0, get_TrxName());
					process.setValue(newTableName + " Process");
					process.setName("Process " + newTableName);
					process.setEntityType(p_EntityType);
					process.setAccessLevel("3");
					process.setAD_Workflow_ID(wf.getAD_Workflow_ID());
					process.saveEx(get_TrxName());
					
					//get back DocAction column
					MColumn dac = new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_ColumnName+"=? AND "+MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
							.setParameters("DocAction",tablepo.get_ID())
							.first();
					if (dac==null)
						throw new AdempiereException("Workflow Structure Creation of DocAction Failed - uncheck to resume");
					dac.setAD_Process_ID(process.get_ID());
					dac.saveEx(get_TrxName());
				}
			}
			
			//create Window, attach Table to main tab
			//create Menu, attach Window
			//if existed, it will resume there
			try {
				AddTable at = new AddTable(tablepo);
				MWindow window = at.makeMenuWindowFields(tablepo); 
				menu = createMenu(window);
				//for setting to menu tree
				
				tablepo.saveEx(get_TrxName());
			} catch (Exception e) { 
				e.printStackTrace();
			}
			
			//Create Master Detail
			if (getMaster()!=null && getMaster().length()>0){
				//create new table with FK = parent ID
				//AD_Tab set to next tab level, link to parent column
				//then user can recall same table in NewWindow to add columns
				createMasterDetail();
			}
			if (getWorkflowModel()!=null)
				createWorkflow();
			
			//do Kanban Board
			if (isKanbanBoard()){
				createKanbanBoard();				
			}

			return menu;
		} 
	}

	private void createKanbanBoard() {
		//get Client that is active - deactivate GardenWorld if you have own client
		MClient client = new Query(Env.getCtx(),MClient.Table_Name,MClient.COLUMNNAME_Name+"!=?",get_TrxName())
				.setParameters("System")
				.setOrderBy(MClient.COLUMNNAME_Updated+" DESC")
				.setOnlyActiveRecords(true).first();
		MKanbanBoard kanban = new Query(Env.getCtx(),MKanbanBoard.Table_Name,MKanbanBoard.COLUMNNAME_Name+"=?",get_TrxName())
				.setParameters(newTableName).first();
		if (kanban==null){
			kanban = new MKanbanBoard(Env.getCtx(),0,get_TrxName());
			kanban.setName(newTableName);
			kanban.set_ValueOfColumn("AD_Client_ID", client.get_ID());
			kanban.setAD_Org_ID(client.getAD_Org_ID());
			;
		} 
		kanban.setAD_Table_ID(AD_Table_ID);
		kanban.setKDB_KanbanCard("@Name@ - @Updated@");
		//get DocumentStatus from newTableName
		MColumn docstatuscolumn = new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_AD_Table_ID+"=? AND "+MColumn.COLUMNNAME_ColumnName+"=?",get_TrxName())
				.setParameters(AD_Table_ID,"DocStatus")
				.first();
		if (docstatuscolumn==null)
			throw new AdempiereException("DocStatus column not found - I give up! Not giving you the Kanban Board");
		kanban.setKDB_ColumnList_ID(docstatuscolumn.get_ID());
 		//kanban.setKDB_PrioritySQL("date_part('sec',current_timestamp-Updated)");
		kanban.saveEx(get_TrxName());
		executeStatusProcess(kanban,client);
		
		//DO ONE TIME ONLY so the first set if exists, then exit.
		//set SystemConfig refresh 5000 milisecs
		MSysConfig config = new Query(Env.getCtx(),MSysConfig.Table_Name,MSysConfig.COLUMNNAME_Name+"=? AND AD_Client_ID=?",get_TrxName())
				.setParameters("KDB_KanbanBoard_RefreshInterval",client.get_ID()).first();
		if (config==null){
			config = new MSysConfig(Env.getCtx(),0,get_TrxName());
			config.set_ValueOfColumn(COLUMNNAME_AD_Client_ID, kanban.getAD_Client_ID());
			config.setName("KDB_KanbanBoard_RefreshInterval");
			config.setValue("15000");
			config.setConfigurationLevel("C");
			config.saveEx(get_TrxName());
		}
		//kanban board control access
		//get Role
		List<MRole> roles = new Query(Env.getCtx(),MRole.Table_Name,MRole.COLUMNNAME_AD_Client_ID+"=?",null)
				.setParameters(client.get_ID()).list();			
		for (MRole role:roles){
			MKDB_KanbanControlAccess controlaccess = new MKDB_KanbanControlAccess(Env.getCtx(),0,get_TrxName());
			controlaccess.setKDB_KanbanBoard_ID(kanban.get_ID());
			controlaccess.set_ValueOfColumn(COLUMNNAME_AD_Client_ID, client.get_ID());
			controlaccess.setAD_Org_ID(client.getAD_Org_ID());
			controlaccess.setAD_Role_ID(role.get_ID());
			controlaccess.setIsReadWrite(true);
			controlaccess.saveEx(get_TrxName());
		} 
		//set Priority Colors
		setKanbanPriorityColors(kanban.get_ID(), client, 0, 10, 113, 100);
		setKanbanPriorityColors(kanban.get_ID(),client, 11, 20, 111, 108);		
		setKanbanPriorityColors(kanban.get_ID(),client, 21, 60, 102, 108); 
	}

	private void setKanbanPriorityColors(int kanbanid, MClient client, int min, int max, int color, int text) {
		MKanbanPriority priority = new MKanbanPriority(Env.getCtx(),0,get_TrxName());
		priority.set_ValueOfColumn(COLUMNNAME_AD_Client_ID, client.get_ID());
		priority.setMinValue(min);
		priority.setMaxValue(max);
		priority.setKDB_PriorityColor_ID(color);
		priority.setKDB_PriorityTextColor_ID(text);
		priority.setKDB_KanbanBoard_ID(kanbanid);
		priority.saveEx(get_TrxName());
	}

	private boolean executeStatusProcess(MKanbanBoard kanban, MClient client) {
 	// Process obtained from the Kanban plugin as dependency
		MProcess pr = new Query(Env.getCtx(), MProcess.Table_Name, MProcess.COLUMNNAME_Classname+"=?", get_TrxName())
		                        .setParameters("org.idempiere.process.CreateStatusProcess")
		                        .first();
		if (pr==null) {
		      log.warning("Create Statuses in Kanban Board Process does not exist. Have you installed and started it?");
		      return false;
		}
		ProcessInfo pi = new ProcessInfo("Kanban Status", pr.get_ID(),kanban.get_Table_ID(),kanban.get_ID());
		pi.setAD_Client_ID(client.get_ID());
		//pi.setParameter(new ProcessInfoParameter[] {pi1,pi2}); 

		// Create an instance of the actual process class.
		CreateStatusProcess process = new CreateStatusProcess();

		// Create process instance (mainly for logging/sync purpose)
		MPInstance mpi = new MPInstance(Env.getCtx(), 0, get_TrxName());
		mpi.set_ValueOfColumn(COLUMNNAME_AD_Client_ID, client.get_ID());
		mpi.setAD_Process_ID(pr.get_ID()); 
		mpi.setRecord_ID(kanban.get_ID());
		mpi.save();

		// Connect the process to the process instance.
		pi.setAD_PInstance_ID(mpi.get_ID());

		log.info("Starting process " + pr.getName());
		boolean result = process.startProcess(Env.getCtx(), pi, Trx.get(get_TrxName(), false));
		return result;
	}

	private void createColumns(NewEmptyTable nt, String columnlist) {
		nt.createColumns(columnlist,get_TrxName());
	}
	
	private MMenu createWorkflow() { 
		if (!getWorkflowModel().contains("="))
			throw new AdempiereException("WorkflowName=Window1,Process1,Window2,etc");
		String[] WFAndNodes = getWorkflowModel().split("=");
		if (WFAndNodes.length==0)
			return null;
		String[] WFNodes = WFAndNodes[1].split(",");

		MWorkflow workflow = new Query(Env.getCtx(),MWorkflow.Table_Name,MWorkflow.COLUMNNAME_Value+"=?",get_TrxName())
		.setParameters(WFAndNodes[0]).first();
		if (workflow==null){
			MWorkflow newWF = new MWorkflow(Env.getCtx(),0,null);
			workflow = newWF;
			workflow.setName(leadingCapsSpacing(WFAndNodes[0]));
			workflow.setValue(WFAndNodes[0]);
			workflow.setAuthor("Ninja");
			workflow.setAccessLevel(X_AD_Workflow.ACCESSLEVEL_ClientPlusOrganization);
			workflow.setWorkflowType(X_AD_Workflow.WORKFLOWTYPE_Wizard);
			workflow.saveEx(get_TrxName());
		}
		//

		MWFNode oldnode = null;
		
		for (int i=0;i<WFNodes.length;i++){
			MWFNode node = new Query(Env.getCtx(),MWFNode.Table_Name,MWFNode.COLUMNNAME_Name+"=? AND "+MWFNode.COLUMNNAME_AD_Workflow_ID+"=?",get_TrxName())
			.setParameters(WFNodes[i],workflow.get_ID()).first();//in addition to Workflow ID..
			if (node==null) {
				node = new MWFNode(Env.getCtx(), 0, get_TrxName());
				node.setName(WFNodes[i]);
				node.setAD_Workflow_ID(workflow.get_ID());
				MWindow window = new Query(Env.getCtx(),MWindow.Table_Name,MWindow.COLUMNNAME_Name+"=?",get_TrxName())
				.setParameters(WFNodes[i]).first();
				MProcess process=null;
				if (window==null) {
					//it maybe a process
					process = new Query(Env.getCtx(),MProcess.Table_Name,MProcess.COLUMNNAME_Name+"=?",get_TrxName())
							.setParameters(WFNodes[i]).first();
					if (process==null)
						throw new AdempiereException("Not creating this window as it does not exist: "+WFNodes[i]);
				}
				if (window!=null){
					node.setAction(MWFNode.ACTION_UserWindow);
					node.setAD_Window_ID(window.get_ID());//window id from the node name associated with a window
				}else if (process!=null){
					node.setAction(MWFNode.ACTION_AppsProcess);
					node.setAD_Window_ID(process.get_ID());
				}
				
				node.setAD_Workflow_ID(workflow.get_ID());
				node.setValue(WFNodes[i]);
				node.saveEx(get_TrxName());
				if (i==0)
					workflow.setAD_WF_Node_ID(node.get_ID());//set to the first node in the script
				//if subsequent loop then do the AD_WF_NodeNext data to link to the next node in the series
				if (oldnode!=null){
					MWFNodeNext next = new MWFNodeNext(oldnode, node.get_ID());
					next.saveEx(get_TrxName());
				} else {
					//start node
					workflow.setAD_WF_Node_ID(node.get_ID());
					workflow.saveEx(get_TrxName());
				}
				oldnode=node;
			}
			
		}
		//attach to menu
		MMenu menu = new MMenu(Env.getCtx(), 0, get_TrxName());
		menu.setName(workflow.getName());
		menu.setAD_Workflow_ID(workflow.getAD_Workflow_ID());
		menu.setAction(X_AD_Menu.ACTION_WorkFlow);
		menu.setEntityType(p_EntityType);
		menu.saveEx(get_TrxName());
		return menu;
	}
	
	MWorkflow createWorkflowStructure()
	{
		MWorkflow wf = new Query(Env.getCtx(),MWorkflow.Table_Name,MWorkflow.COLUMNNAME_Value+"=?",get_TrxName())
				.setParameters("Process_" + newTableName).first();
		if (wf!=null)		
			return wf;
		wf = new MWorkflow(Env.getCtx(),0,get_TrxName());
		wf.setValue("Process_" + newTableName);
		wf.setName("Process_" + newTableName);
		wf.setWorkflowType(X_AD_Workflow.WORKFLOWTYPE_DocumentProcess);
		wf.setAD_Table_ID(AD_Table_ID);
		wf.setEntityType(p_EntityType);
		wf.setAuthor("Ninja");
		wf.saveEx(get_TrxName());
		
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
		wf.saveEx(get_TrxName());
		return wf;
	}
	
	MWFNode createWorkflowNode(MWorkflow wf, String Name, String Action, String DocAction)
	{
		MWFNode wfn = new MWFNode(wf, "(" + Name + ")", "(" + Name + ")");
		wfn.setDescription("(Standard Node)");
		wfn.setEntityType(p_EntityType);
		wfn.setAction(Action);
		wfn.setDocAction(DocAction);
		wfn.saveEx(get_TrxName());
		return wfn;
	}
	
	void createWorkflowNodeNext(MWFNode wfn, int nodeNextID, int seq, String description, boolean isStdUserWF)
	{
		MWFNodeNext wfnn = new MWFNodeNext(wfn, nodeNextID);
		wfnn.setSeqNo(seq);
		wfnn.setDescription(description);
		wfnn.setIsStdUserWorkflow(isStdUserWF);
		wfnn.setEntityType(p_EntityType);
		wfnn.saveEx(get_TrxName());
	}
	
	private MMenu createMenu(MWindow window) { 
			MMenu menu = new Query(getCtx(),MMenu.Table_Name,MMenu.COLUMNNAME_AD_Window_ID+"=?",get_TrxName())
					.setParameters(window.getAD_Window_ID())
					.first();
			if (menu==null) {
				menu = new MMenu(getCtx(), 0, get_TrxName());
				menu.setName(window.getName());
				menu.setAD_Window_ID(window.getAD_Window_ID());
				menu.setAction(X_AD_Menu.ACTION_Window);
				menu.setEntityType(p_EntityType);
				menu.set_ValueOfColumn(COLUMNNAME_AD_Client_ID, 0);
				menu.setAD_Org_ID(0);
				menu.saveEx(get_TrxName()); 
			}				
			return menu; 
	}

	/**
	 * Create InfoWindow from tablenames list i.e. C_OrderLine, C_Order, C_BPartner, M_Product
	 * First table is main FROM table i.e. C_OrderLine a
	 * Others are INNER JOIN tables i.e. INNER JOIN C_Order b ON(a.C_Order_ID=b.C_Order_ID)
	 * Aliases alphabetical i.e. a,b,c, d
	 * Columns automatically picked out
	 * @param multiTableNames
	 */
	private MInfoWindow createInfoWindow(String[] multiTables) {
		setTablesArray(multiTables);
		String firstTable = multiTables[0];
		String infoName = firstTable+" Info-Window";
		MInfoWindow info = new Query(Env.getCtx(),MInfoWindow.Table_Name,MInfoWindow.COLUMNNAME_Name+"=?",get_TrxName())
		.setParameters(infoName)
		.first();
		if (info==null){
			info = new MInfoWindow(Env.getCtx(),0,get_TrxName());
			int tableID = new Query(Env.getCtx(),MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
			.setParameters(firstTable).first().get_ID();
			info.setAD_Table_ID(tableID);  
			
			info.setName(infoName);
			info.setFromClause(firstTable+" a\n");
			for (int i=1; i<multiTables.length;i++){
				info = InfoWindowInnerJoinClause(info,i);
			}
			//create auto columns from table PKs
			info.setEntityType(p_EntityType);
			info.saveEx(get_TrxName());info.getFromClause();
		}
		createInfoKeyColumns(info); 
		return info;
	}
	
	private void createInfoColumnSet(String columnSet,MInfoWindow info) { 
		if (columnSet==null) return;
		if (columnSet.isEmpty()) return;
		String[] infocols = columnSet.split(",");
		for (int i=0;i<infocols.length;i++){
			String colname = infocols[i].trim();
			infoColumnCreation(info,colname,1);
		}
	}
	private void createInfoKeyColumns(MInfoWindow info) {
		// iterate Tableset for each PK and its standard fields if exist 
		String columnname = "";
		for (int i=0;i<8;i++){
			columnname = infoWindowTableSet[1][i]+"_ID";
			infoColumnCreation(info, columnname, i);
		}
	}

	private void infoColumnCreation(MInfoWindow info, String columnname, int i) {
		M_Element element = new Query(Env.getCtx(),M_Element.Table_Name, M_Element.COLUMNNAME_ColumnName+"=?",get_TrxName())
		.setParameters(columnname)
		.first();
		if (element==null) 
			return;	
		MInfoColumn infoColumn = new Query(Env.getCtx(),MInfoColumn.Table_Name,MInfoColumn.COLUMNNAME_ColumnName+"=? AND "
				+MInfoColumn.COLUMNNAME_AD_InfoWindow_ID+"=?",get_TrxName())
				.setParameters(columnname,info.get_ID()).first();
		if (infoColumn!=null)
			return;
		infoColumn = new MInfoColumn(Env.getCtx(),0,get_TrxName());
		if (i==0)
			infoColumn.setIsDisplayed(false);//not for root table as row already display root info.
		infoColumn.setAD_InfoWindow_ID(info.get_ID());
		infoColumn.setIsCentrallyMaintained(true);
		infoColumn.setAD_Element_ID(element.get_ID()); 
		infoColumn.setColumnName(element.getColumnName());
		infoColumn.setDescription(element.getDescription());
		infoColumn.setHelp(element.getHelp());
		infoColumn.setName(element.getName());
		//taken from Callout
		if (infoColumn.getSelectClause() == null || infoColumn.getSelectClause().trim().length() == 0) {
			String fromClause = infoColumn.getAD_InfoWindow().getFromClause();
			AccessSqlParser parser = new AccessSqlParser("SELECT * FROM " + fromClause);
			TableInfo[] tableInfos = parser.getTableInfo(0);
			Map<String, MTable> map = new HashMap<String, MTable>();
			for(TableInfo ti : tableInfos) {
				String alias = ti.getSynonym();
				String tableName = ti.getTableName();
				
				MTable table = map.get(tableName);
				if (table == null) {
					table = MTable.get(Env.getCtx(), tableName);
					if (table == null) { //it maybe new get_TrxName()
						table = new Query(Env.getCtx(),MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
						.setParameters(tableName)
						.first();
					if (table==null)
						continue;//i give up :)
					}
					map.put(tableName, table);
				}
				MColumn col = table.getColumn(element.getColumnName());
				if (col != null) {
					infoColumn.setSelectClause(alias+"."+element.getColumnName());
					infoColumn.setAD_Reference_ID(col.getAD_Reference_ID()==30?19:col.getAD_Reference_ID());
					infoColumn.setAD_Reference_Value_ID(col.getAD_Reference_Value_ID());
					infoColumn.setAD_Val_Rule_ID(col.getAD_Val_Rule_ID());
					//if ((col.isSelectionColumn() || col.isIdentifier()) && !col.isKey()) {		
					if (i>0 && col.getColumnName().endsWith("_ID"))
						infoColumn.setIsQueryCriteria(true);
					infoColumn.setIsIdentifier(col.isIdentifier());
					setQueryOption(infoColumn.getAD_Reference_ID(), infoColumn);
					//} -- red1 -- Allow all PKs to be criteria
					break;   
				}
			}
		}
		if (infoColumn.getSelectClause()==null || infoColumn.getSelectClause().isEmpty())
			throw new AdempiereException("No SelectClause (No Such Column in Models) for :"+element.toString());
		infoColumn.setEntityType(p_EntityType);
		infoColumn.setSeqNo(seqNo++); 
		infoColumn.saveEx(get_TrxName());
	}
	private void setQueryOption(int AD_Reference_ID, I_AD_InfoColumn infoColumn) {
		if (DisplayType.isText(AD_Reference_ID)) {
			infoColumn.setQueryOperator(X_AD_InfoColumn.QUERYOPERATOR_Like);
			infoColumn.setQueryFunction("Upper");
		} else {
			infoColumn.setQueryOperator(X_AD_InfoColumn.QUERYOPERATOR_Eq);
		}
		if (AD_Reference_ID == DisplayType.Date) {
			infoColumn.setQueryFunction("Trunc");
		}
	}
	private void setTablesArray (String[] multiTables){
		char alias = 'a'; 
		infoWindowTableSet=new String[5][8];
		for (int y=0;y<multiTables.length;y++){
			String tablename = multiTables[y];
			if (tablename.isEmpty())
				continue;
			MTable table = new Query(Env.getCtx(),MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
			.setParameters(tablename)
			.first();
			if (table==null) throw new AdempiereException("InfoWindow creation - this MTable: "+tablename+" does not exist");
			infoWindowTableSet[0][y]=String.valueOf(alias);//Alias of Tables in alphabetical order
			infoWindowTableSet[1][y]= tablename; //Name of Tables:i.e.  C_OrderLine, C_Order..
			infoWindowTableSet[2][y]= Integer.toString(table.get_ID());//AD_Table IDs of [1][y]  
			alias++;
			//find FK

		}  
		for (int x=1;x<multiTables.length;x++){ 
			boolean didNotBreak=false;
			for (int y=x-1; y>=0;y--){ 
				//Find in x Foreign Table if it has the PK_ID of y Table
				//i.e. at C_Order, if C_OrderLine.C_Order_ID exists, 
				MColumn column = new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_ColumnName+"=? AND "+MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
				.setParameters(multiTables[x]+"_ID",Integer.parseInt(infoWindowTableSet[2][y]))
				.first(); 
				//if yes, then set alias and its PK as FK
				if (column!=null){ 
					infoWindowTableSet[3][x]=infoWindowTableSet[0][x]+"."+multiTables[x]+"_ID";
					infoWindowTableSet[4][x]=infoWindowTableSet[0][y]+"."+multiTables[x]+"_ID";
					didNotBreak=true;
					break;
				} else {
					log.info("NO Join Key");
					} 
			} 
			if (didNotBreak)
				continue;
			for (int z=0;z<multiTables.length;z++){
				if (x==z)continue;
				MColumn column =  new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_ColumnName+"=? AND "+MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
						.setParameters(multiTables[z]+"_ID",Integer.parseInt(infoWindowTableSet[2][x]))
						.first(); 
				if (column!=null){ 
					infoWindowTableSet[3][x]=infoWindowTableSet[0][x]+"."+multiTables[z]+"_ID";
					infoWindowTableSet[4][x]=infoWindowTableSet[0][z]+"."+multiTables[z]+"_ID";
					break;
				} else {
					log.info("Really NO Join Key");
					}  
			}
		}
	}
	/**
	 * 	alias is alphabetical order of int i
	 *	ON PK=FK is looking for common FK on right side of JOIN
	 * @param multiTableNames
	 * @param i
	 */
	private MInfoWindow InfoWindowInnerJoinClause(MInfoWindow info,int i) {
 		StringBuilder infoClause = new StringBuilder(info.getFromClause());
		infoClause.append("INNER JOIN ");
		infoClause.append(infoWindowTableSet[1][i]+" "+infoWindowTableSet[0][i]); //C_OrderLine b
		infoClause.append(" ON ("+infoWindowTableSet[3][i]+"="+infoWindowTableSet[4][i]+")\n");// ON (b.C_Order_ID=a.C_Order_ID
		info.setFromClause(infoClause.toString());
		return info;
	}
	public void createCallout(String calloutString, int tableID) { 
		//handle model.property anew to avoid confusion in maintaining code.
		String array[][]=new String[8][2];
		String[] firstSplit = calloutString.split(">");
		if (firstSplit.length==1)
			return;
		String mainColumn = firstSplit[0].trim();
		String[] equation = firstSplit[1].split("=");
		if (equation.length==1)
			return;
		array[0][0]= equation[0].trim();	
		array[0][1]="=";
		String fieldsEquation = equation[1];
		//place fields in array.1, place operands in array.2
		//string must have spaces in between each other example var1=var2 * var3 / 100
		String buffer[] = fieldsEquation.split(" ");
		int ctr = 0;
		for (int x=1;x<9;x++){
			array[x][0]=buffer[ctr];
			if (ctr+1==buffer.length) //because its a field to field assignment
				break;
			array[x][1]=buffer[++ctr];
			if (!columnExist(array[x][0]))
				throw new AdempiereException(array[x][0]+" Column does not exist in Callout");		
			if (!isOperand(array[x][1]))
				throw new AdempiereException("illegal operand - not * + - /");

			ctr++;
		}
		setCalloutScript(mainColumn, array, tableID); 
		
	}
	/**
	 * import java.math.BigDecimal;
		BigDecimal a = ((BigDecimal)A_Tab.getValue("First")).multiply((BigDecimal)A_Tab.getValue("Second"));
		if (A_Tab.getValue("First") != null) {
      	A_Tab.setValue("Third", a);
	}
		result = "";
	 */
	private void setCalloutScript(String mainColumn, String[][] array, int tableID) { 
		MColumn column = new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_ColumnName+"=? AND "
				+MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
		.setParameters(mainColumn,tableID).first();
		if (column==null)
			throw new AdempiereException("**mainColumn NULL in setCalloutScript**"); 
		column.setCallout("@script:beanshell:callout"+mainColumn);
		MRule rule = new Query(Env.getCtx(),MRule.Table_Name,MRule.COLUMNNAME_Value+"=?",get_TrxName())
		.setParameters("beanshell:callout"+mainColumn).first();
		if (rule==null)
			rule = new MRule(Env.getCtx(),0,null);
		rule.setValue("beanshell:callout"+mainColumn);
		rule.setName("beanshell:callout"+mainColumn);
		rule.setDescription("beanshell:callout"+mainColumn+" from Aladdin Magic Plugin. Thanks to red1 @ red1.org");
		rule.setEventType(MRule.EVENTTYPE_Callout);
		rule.setRuleType(MRule.RULETYPE_JSR223ScriptingAPIs);
		//
		//calculate script - -
		StringBuilder one = new StringBuilder("import java.math.BigDecimal;\nimport java.math.RoundingMode;\n");
		StringBuilder bd = new StringBuilder();
		for (int x=1;x<9;x++){
			if (array[1][0].contains("."))
				break;
			if ((array[x][0]).equals("100")){
				one.append("import org.compiere.util.Env;\n");
				bd.append("(Env.ONEHUNDRED)");
			} else {
				if (bd.toString().endsWith("divide"))
					bd.append("((BigDecimal)A_Tab.getValue(\""+array[x][0]+"\"),4, RoundingMode.HALF_UP)");
				else
					//expanding script if its ID(model).(get more models).<field> :: import models; set script to get(model object)
					bd.append("((BigDecimal)A_Tab.getValue(\""+array[x][0]+"\"))");
			}
			if (array[x][1]==null)
				break;
			if ((array[x][1]).equals("*")){
				bd.append(".multiply");
			} else if ((array[x][1]).equals("/")){
				bd.append(".divide");
			} else if ((array[x][1]).equals("+")){
				bd.append(".add");			
			} else if ((array[x][1]).equals("-")){
					bd.append(".subtract");
			} else break;
		}
		//setting script
		if (bd.length()>55) { //manual count of length to detect if it is not having any operand and extra field
			rule.setScript(one.append("if (A_Tab.getValue(\""+mainColumn+"\") != null) {\n A_Tab.setValue(\""+array[0][0]+"\",")
					.append(bd).append(");\n}\n result = \"\";").toString());
 
		} else { //script that says, 'Field>Field2=ModelTable.ModelTable.Field  - - Callout: Name1=C_Order.C_BPartner.Email <—JSR223 Query script
			if (array[1][0].contains(".")){
				StringBuilder importlines = new StringBuilder("import org.compiere.util.Env;\n");
				String model = "";
				String prevmodel = "";
				String canon = "";
				String modelname = "";
				 String[] dotvalues = array[1][0].split("\\.");
				 if (dotvalues.length>0)
					 //
					 for (int i = 0; i<dotvalues.length; i++){ 
						 if (i==0){
							 model = dotvalues[i].substring(0, dotvalues[i].length());
							 Class<?> clazz = MTable.getClass(model);
							 canon = clazz.getCanonicalName();
							 importlines.append("import "+canon+";\n"); //add import statement
							 //MBPartner partner = MBPartner.get(Env.getCtx(),(Integer)A_Tab.getValue("C_BPartner_ID"));
							 modelname = canon.substring(canon.lastIndexOf(".")+1);
							 bd.append(modelname+" "+model+" = new "+modelname+"(Env.getCtx(),(Integer)A_Tab.getValue(\""+dotvalues[i]+"_ID\"),null);\n");
							 i++;
							 prevmodel=model;
						 } 
						 if (i==dotvalues.length-1){
							 //last field for param
							 
							if (!columnExist(dotvalues[i])){
								throw new AdempiereException("Callout: MODEL.FIELD not existing = "+dotvalues[i]);
							}
							bd.append("A_Tab.setValue(\""+array[0][0]+"\","+model+".get"+dotvalues[i]+"()").append(");\n result = \"\";");								
							;
							break;
						 }
						 //modelname = get its model to get its next ID model
						//MModel2 model2 = MModel2.get(Env.getCtx(),model1.get_<nextmodel>ID());
						 model = dotvalues[i].substring(0, dotvalues[i].length());
						 Class<?> clazz = MTable.getClass( model);
						 canon = clazz.getCanonicalName();
						 importlines.append("import "+canon+";\n"); //add import statement
						 
						 modelname = canon.substring(canon.lastIndexOf(".")+1);
						 bd.append(modelname+" "+model+" = new "+modelname+"(Env.getCtx(),"+prevmodel+".get"+model+"_ID(),null);\n");
					 }
				 rule.setScript(importlines.append(bd).toString());
			}
			else{
				rule.setScript("if (A_Tab.getValue(\""+mainColumn+"\") != null) {\n"
						+ "A_Tab.setValue(\""+array[0][0]+"\", A_Tab.getValue(\""+array[1][0]+"\"));\n"
						+"}\n"
						+"result = \"\";");
			}
			
		}
		rule.saveEx(get_TrxName());
		column.saveEx(get_TrxName());
	}

	private boolean isOperand(String operand) { 
		if (operand==null)
			return true;
		return operand.matches("[*/+-]{1}"); 
	}

	private boolean columnExist(String column){ 
		if (column.equals("100")) return true;
		//check for getID.getID.field that all IDs and fields exist then return true.
		MColumn col = new Query(Env.getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_ColumnName+"=?",get_TrxName())
		.setParameters(column).first();
		return (col!=null);
	}
	
	private void createMasterDetail() {
		if (AD_Table_ID<1)
			return;
		//get Master Table model
		MTable master = new Query(Env.getCtx(),MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
		.setParameters(getMaster())
		.first();
		if (master==null) 
			throw new AdempiereException("MasterDetail - Master table does not exist (yet) "+getMaster()+" - Update dependencies in ascending order in ModelMaker");
		String subTabName = getName();
		//Split > parentLink
		String parentLinkColumnName = "";
		String splitparent[] = subTabName.split(">");
		if (subTabName.contains(">")){
			subTabName = splitparent[0];
			parentLinkColumnName = splitparent[1];
		}
		MTable tablepo = new Query(Env.getCtx(), MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
		.setParameters(subTabName)
		.first();
		NewEmptyTable nt = null;
		if (tablepo==null) {
			tablepo = new MTable(Env.getCtx(),0,get_TrxName());
			String subprefix[] = subTabName.split("_"); //to check for prefix - MY_tableName
			String subName = subTabName;
			if (subprefix.length>1){
				subName = "";
				for (int i =1; i< subprefix.length; i++) {
					subName = subName+subprefix[i];
				}
			} 
			subName = leadingCapsSpacing(subName); 
			tablepo.setName(subName);
			tablepo.setTableName(subTabName);
			tablepo.setAccessLevel("3");//Client & Org
			tablepo.setIsDeleteable(true); 
			tablepo.saveEx(get_TrxName());
			nt = new NewEmptyTable(tablepo);
			try { 
				//Synch to Database
				tablepo = nt.createNewTable(); 								
			} catch (Exception e) { 
				e.printStackTrace();
			}							
		}
		int parentLinkID = setForeignKey(master.getTableName(), parentLinkColumnName,
				tablepo); 
		
		//create Tab and assign to present parent Window
		//This can also attach any table to another table's parent Window!
		//get window via Table instead of Tab (or C_Order cannot get Sales Order but Sales Opportunity)
		MWindow parentWindow = (MWindow)master.getAD_Window();
		MTab tab = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_AD_Table_ID+"=? AND "+MTab.COLUMNNAME_AD_Window_ID+"=?",get_TrxName())
		.setParameters(master.getAD_Table_ID(),parentWindow.get_ID())
		.first(); 
		if (tab==null) 
			throw new AdempiereException("Table's Window ID not found");
		String tabprefix[] = subTabName.split("_"); //to check for prefix - MY_tableName
		String tabName = subTabName;
		if (tabprefix.length>1)
			tabName = tabprefix[1]; //just take the name without prefix
		tabName = leadingCapsSpacing(tabName); 
		MTab subTab = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_Name+"=? AND "+MTab.COLUMNNAME_AD_Window_ID+"=?",get_TrxName())
		.setParameters(tabName,parentWindow.getAD_Window_ID())
		.first();	
		if (subTab==null){ 
			subTab = new MTab(parentWindow);

			MTable subTable = new Query(Env.getCtx(), MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
			.setParameters(subTabName)
			.first();
			subTab.setAD_Table_ID(subTable.getAD_Table_ID());
			
			//get no of tabs of parent window to increment sub tab seq no properly.
			/*int parenttabs = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_AD_Window_ID+"=?",get_TrxName())
			.setParameters(parentWindow.get_ID())
			.count();		
			subTab.setSeqNo(parenttabs*10+10);*/

			int otherTabs = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_AD_Window_ID+"=?",get_TrxName())
					.setParameters(parentWindow.get_ID()).count();
			subTab.setSeqNo(10+(otherTabs==0?1*10:otherTabs*10));
			
			subTab.setTabLevel(tab.getTabLevel()+1); 
			if (parentLinkID>0){
				subTab.setAD_Column_ID(parentLinkID);
				subTab.setParent_Column_ID(parentLinkID);
			}
			subTab.setName(tabName); 
			subTab.setEntityType(p_EntityType);
			subTab.saveEx(get_TrxName());
			//set its table's Window ID if new i.e. EntityType <> 'D'
			if (!subTable.getEntityType().equals("D")){
				subTable.setAD_Window_ID(parentWindow.getAD_Window_ID());
				subTable.saveEx(get_TrxName());
			}
			//create Tab-Fields for the tab
			AddTable at;
			try {
				at = new AddTable(tablepo); 
				at.createTabFields(subTab);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
		//make PrintFormat for Master Detail Portrait
		if (getMaster()!= null && isPrintFormat()) 
			makePrintFormat(tab,subTab);
			
	}

	private void makePrintFormat(MTab parent,MTab subTab) {
		// TODO Auto-generated method stub 
		MPrintFormat printFormat =null; 
		MProcess process = null;
		if (parent.getAD_Process_ID()<1) {
			process = new MProcess(getCtx(), 0, get_TrxName());
			process.setName("Rpt_"+subTab.getName());
			process.setDescription("Red1 Ninja Generated by www.red1.org");
			process.setHelp("To change, use CSV Attached in PrintFormat at Process of Tab. Then GenerateModule again and new PrintFormat will override.");
			process.setIsReport(true);
			process.saveEx(get_TrxName());
			//save to Tab's Process 
			parent.setAD_Process_ID(process.get_ID());
			parent.saveEx(get_TrxName());
		} else
			process = (MProcess)parent.getAD_Process();

		int printformat_ID = 0;
		if (process.getAD_PrintFormat_ID()>0) {
			printformat_ID = process.getAD_PrintFormat_ID();
			printFormat = (MPrintFormat)process.getAD_PrintFormat();
			MPrintFormatItem[] oldItems = printFormat.getAllItems();
			for (MPrintFormatItem oldItem:oldItems) {
				oldItem.deleteEx(true, get_TrxName());
			}
		}
	
		printFormat = MPrintFormat.createFromTable(getCtx(), parent.getAD_Table_ID(),printformat_ID,get_TrxName());
		printFormat.setName(parent.getName()+" Hdr");
		printFormat.setDescription("Red1 Ninja generated"); 
		printFormat.setIsForm(true);
		printFormat.setIsStandardHeaderFooter(false);
		printFormat.setAD_PrintPaper_ID(101);
		printFormat.setAD_PrintTableFormat_ID(101);
		printFormat.setHeaderMargin(100);
		printFormat.setFooterMargin(0); 
		printFormat.saveEx(get_TrxName());
		//get parent header items
		MPrintFormatItem[] parentItems = printFormat.getAllItems(); 
		int Xpos = 0;
		int Ypos = 0;
		int previousX = 0;
		for (MPrintFormatItem parentItem :parentItems) { 
			if (parentItem.isPrinted()) {
				parentItem.setPrintAreaType(MPrintFormatItem.PRINTAREATYPE_Header);
				parentItem.setIsRelativePosition(false);
				
				//get Tab Display Position and map to Print X/Y Positions
				MField field = new Query(getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Tab_ID+"=? AND "
						+MField.COLUMNNAME_AD_Column_ID+"=?",get_TrxName())
						.setParameters(parent.get_ID(),parentItem.getAD_Column_ID())
						.first();
				if (!field.isDisplayed())
					parentItem.delete(true);
				Xpos = (field.getXPosition()*100)-100;					
				parentItem.setXPosition(Xpos);
				if (previousX>Xpos)
					Ypos = Ypos+20;
				parentItem.setYPosition(Ypos);
				previousX = Xpos+1;
				parentItem.saveEx(get_TrxName()); 
			}			
		}
		process.setAD_PrintFormat_ID(printFormat.get_ID());
		process.saveEx(get_TrxName());
		
		//Sub-Tab PrintItems Look for existing PrintFormat
		MPrintFormat subPrintFormat = new Query(getCtx(),MPrintFormat.Table_Name,MPrintFormat.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
				.setParameters(subTab.getAD_Table_ID()).first();
		if (subPrintFormat==null)
			printformat_ID = 0;
		else {
			printformat_ID = subPrintFormat.get_ID();
			//clear old print items
			MPrintFormatItem[] oldItems = subPrintFormat.getAllItems();
			for (MPrintFormatItem oldItem:oldItems) {
				oldItem.deleteEx(true, get_TrxName());
			}
		}
		
		subPrintFormat = MPrintFormat.createFromTable(getCtx(), subTab.getAD_Table_ID(), printformat_ID, get_TrxName());
		//remove PrintFields that are not Shown In Grid
		MPrintFormatItem[] items = subPrintFormat.getAllItems();
		for (MPrintFormatItem item:items) {
			MField field = new Query(getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Tab_ID+"=? AND "
					+MField.COLUMNNAME_AD_Column_ID+"=?",get_TrxName())
					.setParameters(subTab.get_ID(),item.getAD_Column_ID())
					.first();
			if (field.getSeqNoGrid()>0) {
				item.setSeqNo(field.getSeqNoGrid());
				item.saveEx(get_TrxName());
			}
			else
				item.delete(true);
		}

		//create new Parent Print Item with sub PrintFormat as detail panel
		MPrintFormatItem parentSubItem = new MPrintFormatItem(getCtx(), 0, get_TrxName());
		parentSubItem.setName(subTab.getName()+" detail");
		parentSubItem.setPrintFormatType(X_AD_PrintFormatItem.PRINTFORMATTYPE_PrintFormat);
		parentSubItem.setAD_PrintFormat_ID(printFormat.get_ID());
		parentSubItem.setAD_PrintFormatChild_ID(subPrintFormat.get_ID());
		//get parent id column
		MColumn column = new Query(getCtx(),MColumn.Table_Name,MColumn.COLUMNNAME_Name+"=? AND "+MColumn.COLUMNNAME_AD_Table_ID+"=?",get_TrxName())
				.setParameters(parent.getName().replace(" ", ""),parent.getAD_Table_ID())
				.first();
		parentSubItem.setAD_Column_ID(column.getAD_Column_ID());
		parentSubItem.setPrintAreaType(X_AD_PrintFormatItem.PRINTAREATYPE_Content);
		parentSubItem.setIsRelativePosition(true);
		parentSubItem.setIsNextLine(true); 
		parentSubItem.setSeqNo(88);
		parentSubItem.saveEx(get_TrxName());
	}

	public static String leadingCapsSpacing(String subName) {
		String[] r = subName.split("(?=\\p{Lu})");
		StringBuilder composed = new StringBuilder();
		if (r.length>1){
			for (String s:r){
				s = s.trim();
				if (s.isEmpty()) 
					continue;
				if (s.length()==1)
					composed.append(s);
				else
					composed.append(s+" ");
			}
			return composed.toString().trim(); 
		}
		return subName;
	}

	/**
	 * 	First fetch parent's tab 
		Placing parent ID as ForeignKey
		Link to parent column if > column specified
	 * @param subTabName
	 * @param parentLinkColumnName
	 * @param tablepo
	 * @return
	 */
	private int setForeignKey(String tablename, String parentLinkColumnName, MTable tablepo) {		
		NewEmptyTable nt;
		int parentLinkID = 0;
		String columnname = tablename+"_ID";
		if (!parentLinkColumnName.isEmpty()){ 
			MColumn linkcolumn = tablepo.getColumn(parentLinkColumnName);
			if (linkcolumn!=null){
				columnname = parentLinkColumnName;
				parentLinkID = linkcolumn.get_ID();
			}
		}
		nt = new NewEmptyTable(tablepo);
		MColumn columnexisted = tablepo.getColumn(columnname);
		if (columnexisted==null) {
			createColumns(nt, columnname);			
			//fetch table model again after the new column add
			MTable check = new Query(Env.getCtx(), MTable.Table_Name,MTable.COLUMNNAME_TableName+"=?",get_TrxName())
				.setParameters(tablepo.getTableName())
				.first();
			//check that this parent column exactly exist after all this
			MColumn column = check.getColumn(columnname);
			if (column==null) { 
				log.severe("ERROR in Foreign Key creation");
				return 0;
				}
			column.setIsParent(true); 
			column.saveEx(get_TrxName());
		}else {
			columnexisted.setIsParent(true); 
			columnexisted.saveEx(get_TrxName());
		}

		return parentLinkID;
	}

	public MRO_ModelHeader getHeader() {
		MRO_ModelHeader header = new Query(Env.getCtx(),MRO_ModelHeader.Table_Name,MRO_ModelHeader.COLUMNNAME_RO_ModelHeader_ID+"=?",get_TrxName())
				.setParameters(getRO_ModelHeader_ID()).first();
		return header;
	}
	
	public int getTableID(){
		return AD_Table_ID;
	}
}
