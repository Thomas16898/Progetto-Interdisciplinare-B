package database;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface PhrasesDAO {
	String PhraseTable = "phrases";
	String PhrasePhraseAttribute = "phrase";
	String PhraseThemeAttribute = "theme";
	String PhraseIdAttribute = "id";

	List<PhrasesDTO> get5Phrases(String idPlayer1, String idPlayer2, String idPlayer3) throws SQLException;

	boolean addPhrases(ArrayList<PhrasesDTO> phrases) throws SQLException;

	List<PhrasesDTO> getAllPhrases() throws SQLException;
	
	boolean deleteAllPhrases() throws SQLException;
	
	boolean deletePhrase(int position) throws SQLException;
}