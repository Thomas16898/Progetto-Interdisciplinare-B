package serverRdF;

import java.rmi.RemoteException;
import java.util.Random;

import database.DBManager;
import database.UsersDTO;
import services.Client;
import services.CryptPassword;
import services.EmailAddressDoesNotExistException;
import services.EmailManager;
import services.User;

public class RegistrationManager {
	private DBManager dbManager;
	private EmailManager emailManager;
	private static RegistrationManager registrationManager = null;

	private RegistrationManager(DBManager dbManager, EmailManager emailManager) {
		this.dbManager = dbManager;
		this.emailManager = emailManager;
	}

	public static RegistrationManager createRegistrationManager(DBManager dbManager, EmailManager emailManager) {
		if (registrationManager == null) {
			registrationManager = new RegistrationManager(dbManager, emailManager);
			return registrationManager;
		} else
			return registrationManager;
	}

	public OTPHelper signUp(User form, Client c, boolean admin) throws RemoteException {
		String otp = generateOTP();
		String sub = "Confirm registration RDF";
		String text = "Insert" + otp + " in the app";
		try {
			emailManager.sendEmail(form.getEmail(), sub, text);
		} catch (EmailAddressDoesNotExistException e) {
			throw new EmailAddressDoesNotExistException();
		}
		WaitingThread thread = new WaitingThread(c, dbManager, form, admin);
		String cryptedOTP = CryptPassword.crypt(otp);
		OTPHelperImplementation otpHelper = new OTPHelperImplementation(thread, cryptedOTP);
		thread.start();
		return otpHelper;
	}

	private String generateOTP() {
		Random rd = new Random();
		String res = "";
		for (int i = 0; i < 6; i++) {
			res += rd.nextInt(10);
		}
		return res;
	}

	public boolean checkEmail(String email) {
		UsersDTO user = dbManager.getUserByEmail(email);
		if (user != null) {
			return false;
		} else
			return true;
	}

	public boolean checkNickname(String nickname) {
		UsersDTO user = dbManager.getUserByNickname(nickname);
		if (user != null) {
			return false;
		} else
			return true;
	}

	public boolean checkPassword(String nickname, String password) {
		UsersDTO user = dbManager.getUserByNickname(nickname);
		if (user.getPassword().equals(password)) {
			return true;
		} else {
			return false;
		}

	}
}