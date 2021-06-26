/*** Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,* and your worldly gain shall come to naught and those who share shall gain eventually above you.* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)*/package org.ninja.component;
import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.compiere.util.CLogger;import org.ninja.model.MRO_ModelHeader;
import org.osgi.service.event.Event;

public class RO_ModelHeaderDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(RO_ModelHeaderDocEvent.class);
		private PO po = null;

	@Override 
	protected void initialize() { 
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MRO_ModelHeader.Table_Name);
		log.info("RO_ModelHeader<PLUGIN> .. IS NOW INITIALIZED");
		}

	@Override 
	protected void doHandleEvent(Event event){
		String type = event.getTopic();
		if (type.equals(IEventTopics.AFTER_LOGIN)) {
	}
 		else {
			setPo(getPO(event));
	log.info(" topic="+event.getTopic()+" po="+po);
		if (po instanceof MRO_ModelHeader){
			if (IEventTopics.PO_AFTER_CHANGE == type){
				MRO_ModelHeader modelpo = (MRO_ModelHeader)po;
	log.fine("MRO_ModelHeader changed: "+modelpo.get_ID());
	// **DO SOMETHING** ;
			}
		}
	  }
 }

	private void setPo(PO eventPO) {
		 po = eventPO;
	}

}
