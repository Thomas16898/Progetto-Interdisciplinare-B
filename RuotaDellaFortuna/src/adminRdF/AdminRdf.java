package adminRdF;

import gui.HostNameConnection;

import services.AdminChecker;

public class AdminRdf {

	/**
	 * Launch the application. * 
	 * @param args 
	 * 
	 */
	public static void main(String[] args) {
		AdminChecker.setIsAdmin(true);
		@SuppressWarnings("unused")
		HostNameConnection wp = new HostNameConnection();
	}
}
