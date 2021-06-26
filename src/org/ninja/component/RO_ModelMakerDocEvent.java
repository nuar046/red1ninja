/**
import java.util.List;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MColumn;
import org.compiere.model.Query;
import org.compiere.util.Env;

public class RO_ModelMakerDocEvent extends AbstractEventHandler {
 	private static CLogger log = CLogger.getCLogger(RO_ModelMakerDocEvent.class);
		private String trxName = "";
		private PO po = null;

	@Override 
	protected void initialize() { 
		//registerTableEvent(IEventTopics.PO_AFTER_NEW, MColumn.Table_Name);
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
		
			
					createColumn(column);
				log.info(type+" MColumn: "+column.get_ID());
				
				}else if ((IEventTopics.PO_AFTER_CHANGE == type)){
		}
	  }
 }

	private void setPo(PO eventPO) {
		 po = eventPO;
	}

	private void setTrxName(String get_TrxName) {
 	trxName = get_TrxName;
		}
}