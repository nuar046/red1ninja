/**
import java.sql.ResultSet;
import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

public class RO_ModelHeaderModelFactory implements IModelFactory {
	@Override 	public Class<?> getClass(String tableName) {
		 if (tableName.equals(MRO_ModelHeader.Table_Name)){
			 return MRO_ModelHeader.class;
		 }
  		return null;
	}
	@Override	public PO getPO(String tableName, int Record_ID, String trxName) {
		 if (tableName.equals(MRO_ModelHeader.Table_Name)) {
		     return new MRO_ModelHeader(Env.getCtx(), Record_ID, trxName);
		 }
  		return null;
	}
	@Override	public PO getPO(String tableName, ResultSet rs, String trxName) {
		 if (tableName.equals(MRO_ModelHeader.Table_Name)) {
		     return new MRO_ModelHeader(Env.getCtx(), rs, trxName);
		   }
 		 return null;
	}
}