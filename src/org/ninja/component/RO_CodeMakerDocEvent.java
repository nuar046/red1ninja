/**
import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.osgi.service.event.Event;

public class RO_CodeMakerDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(RO_CodeMakerDocEvent.class);
		private PO po = null;

	@Override 
	protected void initialize() { 
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MRO_CodeMaker.Table_Name);
		log.info("RO_CodeMaker<PLUGIN> .. IS NOW INITIALIZED");
		}

	@Override 
	protected void doHandleEvent(Event event){
		String type = event.getTopic();
		if (type.equals(IEventTopics.AFTER_LOGIN)) {
	}
 		else {
			setPo(getPO(event));
	log.info(" topic="+event.getTopic()+" po="+po);
		if (po instanceof MRO_CodeMaker){
			if (IEventTopics.PO_AFTER_CHANGE == type){
				MRO_CodeMaker modelpo = (MRO_CodeMaker)po;
	log.fine("MRO_CodeMaker changed: "+modelpo.get_ID());
	// **DO SOMETHING** ;
			}
		}
	  }
 }

	private void setPo(PO eventPO) {
		 po = eventPO;
	}

}