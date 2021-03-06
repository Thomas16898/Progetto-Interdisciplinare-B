package serverRdF;


import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;

import com.opencsv.exceptions.CsvValidationException;

import database.DBManager;
import database.PhrasesDTO;
import game.MatchManager;
import game.RemoteMatch;
import services.ClientInterface;
import services.EmailManager;
import services.Login;
import services.MatchData;
import services.User;


public class Server extends UnicastRemoteObject implements ServerInterface {
    private DBManager dbManager;
    private EmailManager emailManager;
    private ProfileManager profileManager;
    private PhraseManager phraseManager;
    private MonitoringManager monitoringManager;
    private MatchVisualizer matchVisualizer;
    private MatchManager matchManager;
    private AutenticationManager autenticationManager;
    private RegistrationManager registrationManager;

    public Server(DBManager dbmng, EmailManager emailmang) throws RemoteException {
        dbManager = dbmng;
        emailManager = emailmang;
        profileManager = ProfileManager.createProfileManager(dbManager, emailManager);
        phraseManager = PhraseManager.createPhraseManager(dbManager);
        monitoringManager = MonitoringManager.createMonitoringManager(dbManager);
        matchManager = MatchManager.createMatchManager(dbManager,emailManager);
        matchVisualizer = MatchVisualizer.createMatchVisualizer(matchManager);
        autenticationManager = AutenticationManager.createAutenticationManager(dbManager);
        registrationManager = RegistrationManager.createRegistrationManager(dbManager, emailManager);
    }


    /**
     * Questo metodo permette di inviare una notifica al client nel caso non avvenga una connessione al server o al db 
     * @param c riferimento al client
     */
    public static void serverError(ClientInterface c) {
        if (c == null) {
            System.err.println("Server error");
        } else {
            try {
                c.notifyServerError();
            } catch (RemoteException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public boolean checkEMail(String email) throws RemoteException {
        return registrationManager.checkEmail(email);
    }

    public boolean checkNickname(String nickname) throws RemoteException {
        return registrationManager.checkNickname(nickname);
    }

    public OTPManagerInterface signUp(User form, ClientInterface c, boolean admin) throws RemoteException {
        return registrationManager.signUp(form,c,admin);
    }

    public int signIn(Login form, ClientInterface c, boolean admin) throws RemoteException {
        return autenticationManager.signIn(form,c,admin);
    }

    public ArrayList<MatchData> visualizeMatch(ClientInterface c) throws RemoteException {
        return matchVisualizer.visualizeMatch();
    }

    public RemoteMatch createMatch(ClientInterface c) throws RemoteException {
        return matchManager.createMatch(c);
    }

    public RemoteMatch joinMatch(ClientInterface c, String idMatch) throws RemoteException {
            return matchManager.joinMatch(c,idMatch);
    }

    public RemoteMatch observeMatch(ClientInterface c, String idMatch) throws RemoteException {
        return matchManager.observeMatch(c,idMatch);
    }

    public boolean addPhrases(File file) throws RemoteException, CsvValidationException {
        try {
            return phraseManager.addPhrases(file);
        }catch (IOException e){
            return false;
        }
    }
    
    
    public List<PhrasesDTO> getAllPhrases() throws RemoteException{
    	return phraseManager.getAllPhrases();
    }
    
    
    
    


    public boolean changePassword(String password, ClientInterface c) throws RemoteException {
        String idUser = c.getId();
        return profileManager.changePassword(password, idUser);
    }

    public String checkRecordPlayer() throws RemoteException {
        return monitoringManager.bestStatsUsers();
    }

    public String checkPerPlayer(String nickname) throws RemoteException {
        return monitoringManager.perPlayerStats(nickname);
    }

    public String bestMove() throws RemoteException {
        return monitoringManager.getBestMove();
    }

    
    public int averageManches() throws RemoteException {
        return monitoringManager.averageMovesPerManches();
    }

    public boolean resetPassword(ClientInterface c,String mail) throws RemoteException {
        return profileManager.resetPassword(mail);
    }
    
    public boolean changeName(String name, ClientInterface c) throws RemoteException {
        String idUser = c.getId();
        return profileManager.changeName(name, idUser);
    }

    public boolean changeSurname(String surname, ClientInterface c) throws RemoteException {
        String idUser = c.getId();
        return profileManager.changeSurname(surname, idUser);
    }

    public boolean changeNickname(String nickname, ClientInterface c) throws RemoteException {
        String idUser = c.getId();
        return profileManager.changeNickname(nickname, idUser);
    }
    
    public boolean checkPassword(String nickname,String password, ClientInterface c) throws RemoteException{
        return registrationManager.checkPassword(nickname,password);
    }
    
    
    public boolean uploadPhrase(PhrasesDTO DTO) {
    	return phraseManager.uploadPhrase(DTO);
    }
    
    public boolean deleteAllPhrases() {
    	return phraseManager.deleteAllPhrases();
    }
    
    public boolean deletePhrase(int position) {
    	return phraseManager.deletePhrase(position);
    }
    
    public boolean addPhrase(PhrasesDTO DTO) {
    	return phraseManager.addPhrase(DTO);
    }
     
    
    
    
}