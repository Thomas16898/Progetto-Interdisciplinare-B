package serverRdF;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import database.DBManager;
import database.PhrasesDTO;

public class PhraseManager {
	private DBManager dbManager;
	private static PhraseManager phraseManager = null;

	private PhraseManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	/**
	 * Questo metodo crea il singleton di PhraseManager
	 * 
	 * @param dbmng il riferimento a DBManager
	 * @return Il riferimento al singleton di PhraseManager
	 */
	public static PhraseManager createPhraseManager(DBManager dbmng) {
		if (phraseManager == null) {
			phraseManager = new PhraseManager(dbmng);
			return phraseManager;
		} else {
			return phraseManager;
		}
	}

	/**
	 * Questo metodo aggiunge al database le frasi ottenute attraverso un file di
	 * tipo .csv
	 *
	 * @param file il file da leggere
	 * @return <code>true</code> se l'inserimento avviene con successo,
	 *         <code>false</code> altrimenti
	 * @throws IOException in caso di errori nella lettura del file
	 */
	public boolean addPhrases(File file) throws IOException, CsvValidationException {
		CSVReader reader = new CSVReader(new FileReader(file));
		ArrayList<PhrasesDTO> phrases = new ArrayList<>();
		String[] nextLine;
		String theme = "";
		String phrase = "";
		while ((nextLine = reader.readNext()) != null) {
			if (!nextLine[0].equals("")) {
				for (int i = 0; i < nextLine[0].length(); i++) {
					char c = nextLine[0].charAt(i);
					theme += c;
					if (c == '\'') {
						theme += "'";
					}
				}
				nextLine[1] = nextLine[1].trim();
				for (int i = 0; i < nextLine[1].length(); i++) {
					char c = nextLine[1].charAt(i);
					phrase += c;
					if (c == '\'') {
						phrase += "'";
					}
				}
				phrases.add(new PhrasesDTO(theme, phrase));
				theme = "";
				phrase = "";
			}
		}
		return dbManager.addPhrases(phrases);
	}

	/**
	 * Questo metoto permette la visualizzazione di tutte le frasi
	 * @return lista di frasi
	 */
	public List<PhrasesDTO> getAllPhrases() {
		return dbManager.getAllPhrases();
	}

	public boolean uploadPhrase(PhrasesDTO DTO) {
		return dbManager.uploadPhrase(DTO);
	}

	/**
	 * Questo metodo permette di eliminare tutte le frasi
	 * @return <code>true</code> se l'eliminazione � andata a buon fine, altrimenti <code>false</code>
	 */
	public boolean deleteAllPhrases() {
		return dbManager.deleteAllPhrases();
	}

	/**
	 * Questo metodo permette di eliminare una determinata frase
	 * @param position id della frase
	 * @return <code>true</code> se l'eliminazione � andata a buon fine, altrimenti <code>false</code>
	 */
	public boolean deletePhrase(int position) {
		return dbManager.deletePhrase(position);
	}

	/**
	 * Questo metodo permette di aggiungere una frase
	 * @param DTO frase da aggiungere al db
	 * @return <code>true</code> se l'inserimento � andato a buon fine, altrimenti <code>false</code>
	 */
	public boolean addPhrase(PhrasesDTO DTO) {
		return dbManager.addPhrase(DTO);
	}

}
