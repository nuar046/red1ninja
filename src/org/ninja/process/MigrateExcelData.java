/**
 


	public class MigrateExcelData extends SvrProcess {

	private Timestamp Today = new Timestamp(System.currentTimeMillis());
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("IsActive")){
					IsActive = "Y".equals(p.getParameter());
			}
				else if(name.equals("Processed")){
					Processed = "Y".equals(p.getParameter());
			}	else if(name.equals("File_Directory")){
		}
	}

		return message;
	}
}