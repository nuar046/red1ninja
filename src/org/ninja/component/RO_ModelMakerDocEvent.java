/*** Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,* and your worldly gain shall come to naught and those who share shall gain eventually above you.* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)*/package org.ninja.component;
import java.util.List;import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MColumn;import org.compiere.model.MField;import org.compiere.model.MTab;import org.compiere.model.MEntityType;import org.compiere.model.PO;
import org.compiere.model.Query;import org.compiere.util.CLogger;
import org.compiere.util.Env;import org.ninja.model.MRO_ModelMaker;import org.osgi.service.event.Event;

public class RO_ModelMakerDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(RO_ModelMakerDocEvent.class);
		private String trxName = "";
		private PO po = null;		private int seqNo;

	@Override 
	protected void initialize() { 
		//registerTableEvent(IEventTopics.PO_AFTER_NEW, MColumn.Table_Name);		//registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MColumn.Table_Name);
		log.info("RO_ModelMaker<PLUGIN> .. IS NOW INITIALIZED");
		}

	@Override 
	protected void doHandleEvent(Event event){
		String type = event.getTopic();
		if (type.equals(IEventTopics.AFTER_LOGIN)) {
	}
 		else {
			setPo(getPO(event));
			setTrxName(po.get_TrxName());
			log.info(" topic="+event.getTopic()+" po="+po);
					if (po instanceof MColumn){				MColumn column = (MColumn)po;
							if (IEventTopics.PO_AFTER_NEW == type){
					createColumn(column);			
				log.info(type+" MColumn: "+column.get_ID());
				
				}else if ((IEventTopics.PO_AFTER_CHANGE == type)){					changedColumn(column);				}
		}
	  }
 }	private void createColumn(MColumn column) {				//avoid not Ninja custom made columns			if (!column.getEntityType().equals("XX")) //Only Ninja 'XX' allowed.  				return; 			column.setEntityType("U");			column.saveEx(trxName);			//New column will create at its usual Window Tab as new field			int table_id = column.getAD_Table_ID();			if (table_id<1)				return;			//locate associated Table and Tab			MTab tab = new Query(Env.getCtx(),MTab.Table_Name,MTab.COLUMNNAME_AD_Table_ID+"=? AND "+MTab.COLUMNNAME_AD_Window_ID+"=?",trxName)			.setParameters(table_id,column.getAD_Table().getAD_Window_ID())			.first();			if (tab==null)				return;						//insert new column into field of parent tab via table. Only for first time.			MField field = new MField (tab);			field.setColumn(column);			boolean doneb4 = new Query(Env.getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Column_ID+"=?",trxName)			.setParameters(column.get_ID()).count()>0;			log.info(doneb4?"DONE BEFORE YYYYYYYYYYYYY":"NOT DONE BEFORE XXXXXXXXXXX");			if (doneb4)				return;			if (column.isKey()){				field.setIsDisplayed(false);				field.setIsDisplayedGrid(false);			}			//find last sequenceNo			MField last = new Query(Env.getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Tab_ID+"=?",trxName)			.setParameters(tab.getAD_Tab_ID())			.setOrderBy("SeqNo DESC")			.first();			if (last.getSeqNo()>0)				seqNo = last.getSeqNo();			seqNo += 10;			//set spaces if leading caps			if (column.getName().endsWith("_ID") && column.getAD_Reference_ID()==19){				String nosuffix = column.getName(); //TABLE DIR suffix _ID not needed				column.setName(nosuffix.substring(0, nosuffix.length()-3));			}			String fieldname = column.getName();									String[] r = fieldname.split("(?=\\p{Lu})");			StringBuilder composed = new StringBuilder();			for (String s:r){				if (s.isEmpty()) 					continue;				composed.append(s.length()==1?s:s+" ");			}			fieldname = composed.toString().trim().replaceAll("\\s{2,}", " ");			field.setIsCentrallyMaintained(false);			field.setName(fieldname);			field.setSeqNo(seqNo);			field.saveEx(trxName);			}
	/**	 * Only for Window Field > Alternative Name	 * @param column	 */	private void changedColumn(MColumn column){		String changename = column.getDescription();		if (changename!=null && changename.startsWith("**")){			String fieldname = changename.substring(2); 					fieldname = MRO_ModelMaker.leadingCapsSpacing(fieldname); 			List<MField> fieldtabs = new Query(Env.getCtx(),MField.Table_Name,MField.COLUMNNAME_AD_Column_ID+"=?",trxName)			.setParameters(column.get_ID()).list();			if (fieldtabs!=null){				for (MField field:fieldtabs){					field.setName(fieldname);					field.saveEx(trxName);				}			}		}	}
	private void setPo(PO eventPO) {
		 po = eventPO;
	}

	private void setTrxName(String get_TrxName) {
 	trxName = get_TrxName;
		}
}
