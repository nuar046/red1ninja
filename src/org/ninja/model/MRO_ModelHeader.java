/**

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.Query;

	private static final long serialVersionUID = -1L;

	public MRO_ModelHeader(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MRO_ModelHeader(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}