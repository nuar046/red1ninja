/**
 


	public class ExcelNinja extends SvrProcess {
		 

		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
			}
		}
	}

		return header.getName()+" MODEL SEQNO "+model.getSeqNo();
	}
}