/**


	public class ProductAccts extends SvrProcess {


		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("M_Product_Category_ID")){
					M_Product_Category_ID = p.getParameterAsInt();
			}
		}
	}

		return "Product Category Accounts Inserted";
	}
}