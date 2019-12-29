package database;

import java.sql.SQLException;

public interface MatchesDAO {
	String MatchTable = "matches";
	String MatchIdAttribute = "id";
	String MatchDateAttribute = "date";
	String MatchTimeAttribute = "time";

	/**
	 * Metodo che permette l'inserimento di un match nel db
	 * 
	 * @param matchesDTO riferimento al Data Trasfer Object relativo al match
	 * @return <code>true</code> se l'inserimento va a buon fine, altrimenti <code>false</code>
	 * @throws SQLException gestione errori di collegamento al db
	 */
	boolean addMatch(MatchesDTO matchesDTO) throws SQLException;

	/**
	 * Metodo per l'eliminazione di un match dal db
	 * @param idMatch identificativo match che si vuole eliminare
	 * @return <code>true<code> se il match viene eliminato correttamente, altrimenti <code>false</code>
	 * @throws SQLException gestione errori di collegamento al db
	 */
	boolean deleteMatch(String idMatch) throws SQLException;
}