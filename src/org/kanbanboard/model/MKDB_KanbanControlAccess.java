/**

import java.sql.ResultSet;
import java.util.Properties;

public class MKDB_KanbanControlAccess extends X_KDB_KanbanControlAccess{

	private static final long serialVersionUID = -1L;

	public MKDB_KanbanControlAccess(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MKDB_KanbanControlAccess(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}