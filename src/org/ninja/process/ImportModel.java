/**


	public class ImportModel extends SvrProcess {


		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("Name")){
					Name = (String)p.getParameter();
			}
		}
	}

	}
}