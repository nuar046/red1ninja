/**


	/**
	public class NinjaExporter extends SvrProcess { 
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("File_Directory")){
					File_Directory = (String)p.getParameter();
			}
				else if(name.equals("AD_Menu_ID")){
					AD_Menu_ID = p.getParameterAsInt();
			}
				else if(name.equals("AD_Window_ID")){
					AD_Window_ID = p.getParameterAsInt();
			}
				else if(name.equals("AD_Table_ID")){
					AD_Table_ID = p.getParameterAsInt();
			}
				else if(name.equals("IsActive")){
					IsActive = "Y".equals(p.getParameter());
			}
				else if(name.equals("Processed")){
					Processed = "Y".equals(p.getParameter());
			}
		}
	}

		return "Columns written to Excel : "+colcnt;
	}
}