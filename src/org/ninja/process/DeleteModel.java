/**

import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("IsAllNodes")){
					IsAllNodes = "Y".equals(p.getParameter());
			}
		}
	}

		return "DELETED RECORD ID "+tableid;
	}
}