/**
import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.osgi.service.event.Event;

public class KDB_KanbanControlAccessDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(KDB_KanbanControlAccessDocEvent.class);
		private PO po = null;

	@Override 
	protected void initialize() { 
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MKDB_KanbanControlAccess.Table_Name);
		log.info("KDB_KanbanControlAccess<PLUGIN> .. IS NOW INITIALIZED");
		}

	@Override 
	protected void doHandleEvent(Event event){
		String type = event.getTopic();
		if (type.equals(IEventTopics.AFTER_LOGIN)) {
	}
 		else {
			setPo(getPO(event));
	log.info(" topic="+event.getTopic()+" po="+po);
		if (po instanceof MKDB_KanbanControlAccess){
			if (IEventTopics.PO_AFTER_CHANGE == type){
				MKDB_KanbanControlAccess modelpo = (MKDB_KanbanControlAccess)po;
	log.fine("MKDB_KanbanControlAccess changed: "+modelpo.get_ID());
	// **DO SOMETHING** ;
			}
		}
	  }
 }

	private void setPo(PO eventPO) {
		 po = eventPO;
	}

}