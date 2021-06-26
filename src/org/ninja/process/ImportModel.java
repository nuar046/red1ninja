/*** Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,* and your worldly gain shall come to naught and those who share shall gain eventually above you.* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)*/package org.ninja.process;
import org.compiere.model.Query;import org.compiere.process.ProcessInfoParameter;import org.compiere.process.SvrProcess;import org.compiere.util.Env;import org.ninja.model.MRO_ModelMaker;import org.ninja.utils.HandleCSVImport;

	public class ImportModel extends SvrProcess {
	private String Name = "";
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)					;
				else if(name.equals("Name")){
					Name = (String)p.getParameter();
			}
		}
	}
	protected String doIt() {		String result = ""; 		MRO_ModelMaker  model = new Query(Env.getCtx(),MRO_ModelMaker.Table_Name,MRO_ModelMaker.COLUMNNAME_RO_ModelMaker_ID+"=?",get_TrxName())				.setParameters(getRecord_ID()).first(); 		HandleCSVImport attachcsv = new HandleCSVImport(model);		try {			result = attachcsv.getCSVandProcess();			log.fine(result); 		} catch (Exception e) { 					e.printStackTrace(); 		}    		return result;
	}
}
