/**

import java.io.BufferedReader; 
	/** Logger */
	private static final long serialVersionUID = -1L;
	public MRO_CodeMaker(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MRO_CodeMaker(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}