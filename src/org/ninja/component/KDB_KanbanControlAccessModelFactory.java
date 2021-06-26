/**
import java.sql.ResultSet;
import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

public class KDB_KanbanControlAccessModelFactory implements IModelFactory {
	@Override 	public Class<?> getClass(String tableName) {
		 if (tableName.equals(MKDB_KanbanControlAccess.Table_Name)){
			 return MKDB_KanbanControlAccess.class;
		 }
  		return null;
	}
	@Override	public PO getPO(String tableName, int Record_ID, String trxName) {
		 if (tableName.equals(MKDB_KanbanControlAccess.Table_Name)) {
		     return new MKDB_KanbanControlAccess(Env.getCtx(), Record_ID, trxName);
		 }
  		return null;
	}
	@Override	public PO getPO(String tableName, ResultSet rs, String trxName) {
		 if (tableName.equals(MKDB_KanbanControlAccess.Table_Name)) {
		     return new MKDB_KanbanControlAccess(Env.getCtx(), rs, trxName);
		   }
 		 return null;
	}
}