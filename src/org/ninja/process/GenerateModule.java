/**

		
	int models = 0;
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("IsAllNodes")){
					IsAllNodes = "Y".equals(p.getParameter());
			}
		}
	}

		return "COMPLETED - HEADERS:"+cnt+" MODELS:"+models+"(COLUMNS:"+columnlistlength+") CODES:"+codes;
	}
}