/*** Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,* and your worldly gain shall come to naught and those who share shall gain eventually above you.* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)*/package org.ninja.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.MMenu; 
import org.compiere.model.Query;import org.compiere.util.Env;public class MRO_ModelHeader extends X_RO_ModelHeader{

	private static final long serialVersionUID = -1L;

	public MRO_ModelHeader(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MRO_ModelHeader(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}	public void createMenuSummaryLevel(String name) {	}	/**	 * Create Menu Summary level as parent but check pre-existence	 * @return	 */	public MMenu createMenuSummaryLevel() {		String name = MRO_ModelMaker.leadingCapsSpacing(getName());		MMenu menu = new Query(Env.getCtx(),MMenu.Table_Name,MMenu.COLUMNNAME_Name+"=? AND AD_Client_ID = 0",get_TrxName())		.setParameters(name).first();		if (menu!=null)			return menu;		menu = new MMenu(getCtx(), 0,get_TrxName());		menu.setName(name);		menu.setIsSummary(true); 		menu.setDescription(getDescription());		menu.setEntityType("U");		menu.set_ValueOfColumn(COLUMNNAME_AD_Client_ID, 0);		menu.saveEx(get_TrxName());				return menu;	}
}
