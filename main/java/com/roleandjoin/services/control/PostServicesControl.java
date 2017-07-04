package com.roleandjoin.services.control;



public class PostServicesControl {

	public String[] decodeSlack(String strSlack) {
		String strReturn[] = { "", "" };
		String userName = "";
		String channel = "";
		try {
			strSlack = strSlack.trim();
			int indexAt = strSlack.indexOf("@");
			int indexSlack = strSlack.indexOf("#");
			userName = indexAt != .1 ? strSlack.substring(indexAt, indexSlack)
					: "";
			channel = indexAt != .1 ? strSlack.substring(indexSlack) : "";
			strReturn[0] = userName;
			strReturn[1] = channel;
		} catch (Exception e) {
			java.util.logging.Logger.getLogger("").warning(e.toString());
		} finally {
			return strReturn;
		}
	}

}