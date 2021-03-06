package game;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import database.DBManager;
import database.ManchesDTO;
import database.MatchesDTO;
import database.PhrasesDTO;
import database.UsersDTO;
import serverRdF.Server;
import services.ClientInterface;
import services.EmailManager;
import services.MatchData;


public class Match extends UnicastRemoteObject implements RemoteMatch {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Player> players;
    private List<ClientInterface> observers;
    private String id;
    private boolean onGoing;
    private Manche manche;
    private int turn;
    private boolean firstTurn;
    private LocalDateTime creationTime;
    private DBManager dbManager;
    private EmailManager emailmng;
    private boolean[] phraseStatus;
    private MoveTimer timer = null;
    private boolean spinnedWheel = false;
    private boolean noConsonantLeft = false;
    private boolean matchEnded = false;
    private int time = 5000; 


    public Match() throws RemoteException {

    }


    public Match(String id, LocalDateTime localDateTime, DBManager db, EmailManager email) throws RemoteException {
        this.id = id;
        onGoing = false;
        dbManager = db;
        emailmng = email;
        manche = new Manche(dbManager, id, localDateTime);
        players = new ArrayList<Player>();
        observers = new ArrayList<ClientInterface>();
        turn = -1;
        firstTurn = true;
        creationTime = localDateTime;
    }

    /**
     * Metodo per determinare il risultato del giro di ruota
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     * @return ritorna un valore numerico corrispondente al risultato uscito sulla ruota o 0 se il valore non � numerico (Passa, Perdi tutto, Jolly)
     */
    public int wheelSpin() throws RemoteException {
        Player activePlayer = players.get(turn);
        if (timer.isThisForJolly() || timer.isThisForSolution() || timer.isThisForVocal() || noConsonantLeft || spinnedWheel) {
            timer.interrupt();
            errorInTurn(false, false);
            return 0;
        } else {
            firstTurn = false;
            timer.interrupt();
            wheelResult("GIRA...");
            spinnedWheel = true;
            Random rnd = new Random();
            int result = rnd.nextInt(24);
            switch (result) {
                case 0:
                    wheelResult("PASSA");
                    manche.getTurns().addMove(activePlayer.getIdPlayer(), "passa", 0);
                    errorInTurn(true, true);
                    return 0;
                case 1:
                    wheelResult("JOLLY");
                    manche.getTurns().addMove(activePlayer.getIdPlayer(), "jolly", -1);
                    activePlayer.addJolly();
                    for (ClientInterface c : observers) {
                        try {
                                c.notifyPlayerStats(turn, activePlayer.getNickname(), activePlayer.getPartialPoints(), activePlayer.getPoints(), activePlayer.getNumJolly());

                        } catch (RemoteException e) {
                            leaveMatchAsObserver(c);
                        }
                    }
                    for (Player p : players) {
                        try {
                                p.getClient().notifyPlayerStats(turn, activePlayer.getNickname(), activePlayer.getPartialPoints(), activePlayer.getPoints(), activePlayer.getNumJolly());

                        } catch (RemoteException e) {
                            leaveMatchAsPlayer(p);
                        }
                    }
                    startTurn(turn);
                    return 0;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    wheelResult("400");
                    startTimer(time, false, false, false);
                    return 400;
                case 7:
                case 8:
                    wheelResult("PERDE");
                    manche.getTurns().addMove(activePlayer.getIdPlayer(), "perde", 0);
                    for (ClientInterface c : observers) {
                        try {
                            c.notifyPlayerStats(turn, activePlayer.getNickname(), 0, activePlayer.getPoints(), activePlayer.getNumJolly());
                        } catch (RemoteException e) {
                            leaveMatchAsObserver(c);
                        }
                    }
                    for (Player p : players) {
                        try {
                            p.getClient().notifyPlayerStats(turn, activePlayer.getNickname(), 0, activePlayer.getPoints(), activePlayer.getNumJolly());
                        } catch (RemoteException e) {
                            leaveMatchAsPlayer(p);
                        }
                    }
                    nextTurn();
                    return 0;
                case 9:
                case 10:
                case 11:
                case 12:
                    wheelResult("500");
                    startTimer(time, false, false, false);
                    return 500;
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                    wheelResult("300");
                    startTimer(time, false, false, false);
                    return 300;
                case 19:
                case 20:
                case 21:
                    wheelResult("600");
                    startTimer(time, false, false, false);
                    return 600;
                case 22:
                    wheelResult("1000");
                    startTimer(time, false, false, false);
                    return 1000;
                case 23:
                    wheelResult("700");
                    startTimer(time, false, false, false);
                    return 700;
                default:
                    return 0;
            }
        }
    }

    /**
     * Metodo per comunicare il risultato del giro di ruota a tutti gli osservatori
     * 
     * @param result contiene il risultato del giro di ruota
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void wheelResult(String result) throws RemoteException {
        for (ClientInterface c : observers) {
            try {
                c.notifyWheelResult(result);
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                p.getClient().notifyWheelResult(result);
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per la notifica di errori
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void notifyError() throws RemoteException {
        Player activePlayer = players.get(turn);
        for (ClientInterface c : observers) {
            try {
                c.notifyPlayerError(activePlayer.getNickname());
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                p.getClient().notifyPlayerError(activePlayer.getNickname());
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per l'acquisto di una vocale
     * 
     * @param letter contiene la lettera desiderata
     * @param amount contiene il valore che si dovr� spendere per acquistare la lettera
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void giveConsonant(String letter, int amount) throws RemoteException {
        Player activePlayer = players.get(turn);
        char vocal;
        try {
            vocal = letter.charAt(0);
        } catch (IndexOutOfBoundsException e) {
            errorInTurn(true, false);
            return;
        }
        boolean isVocal = (vocal == 'A' || vocal == 'E' || vocal == 'I' || vocal == 'O' || vocal == 'U');
        if (firstTurn || !spinnedWheel || amount == 0 || timer.isThisForJolly() || timer.isThisForSolution() || letter.length() > 1 || isVocal || timer.isThisForVocal() || !manche.checkConsonant(letter)) {
            timer.interrupt();
            errorInTurn(true, false);
            return;
        }
        timer.interrupt();
        notifyLetterCall(letter);
        boolean bool = manche.addConsonant(letter);
        if (bool) {
            notifyNoMoreConsonants();
            noConsonantLeft = true;
        }
        for (ClientInterface c : observers) {
            try {
                c.updatePhrase(letter);
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                p.getClient().updatePhrase(letter);
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
        
        String phrase = manche.getCurrentPhrase().getPhrase().toUpperCase();
        StringTokenizer st = new StringTokenizer(phrase, " ',!?.:;\"/()\\^<>-+*0123456789");
        int counter = 0;
        int j = 0;
        while (st.hasMoreTokens()) {
            String ss = st.nextToken();
            for (int i = 0; i < ss.length(); i++) {
                if (ss.charAt(i) == vocal) {
                    if (phraseStatus[j] == false) {
                        phraseStatus[j] = true;
                        counter++;
                    }
                    j++;
                } else {
                    j++;
                }
            }
        }
        if (counter > 0) {
            int result = counter * amount;
            manche.getTurns().addMove(activePlayer.getIdPlayer(), "" + letter, result);
            players.get(turn).updatePartialPoints(result);
            for (ClientInterface c : observers) {
                try {
                    c.notifyPlayerStats(turn, activePlayer.getNickname(), activePlayer.getPartialPoints(), activePlayer.getPoints(), activePlayer.getNumJolly());
                } catch (RemoteException e) {
                    leaveMatchAsObserver(c);
                }
            }
            for (Player p : players) {
                try {
                    p.getClient().notifyPlayerStats(turn, activePlayer.getNickname(), activePlayer.getPartialPoints(), activePlayer.getPoints(), activePlayer.getNumJolly());
                } catch (RemoteException e) {
                    leaveMatchAsPlayer(p);
                }
            }
            startTurn(turn);
        } else {
            manche.getTurns().addMove(activePlayer.getIdPlayer(), "" + letter, 0);
            errorInTurn(true, true);
        }
    }

    /**
     * Metodo per comunicare le chiamate di lettera dei giocatori agli osservatori
     * 
     * @param letter contiene la lettera chiamata
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void notifyLetterCall(String letter) throws RemoteException {
        Player activePlayer = players.get(turn);
        for (ClientInterface c : observers) {
            try {
                c.notifyLetterCall(activePlayer.getNickname(), letter);
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                if (p.equals(activePlayer)) {
                } else {
                    p.getClient().notifyLetterCall(activePlayer.getNickname(), letter);
                }
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per comunicare l'assenza di consonanti chiamabili presenti nella frase
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void notifyNoMoreConsonants() throws RemoteException {
        for (ClientInterface c : observers) {
            try {
                c.notifyNoMoreConsonant();
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                p.getClient().notifyNoMoreConsonant();
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per chiedere ad un giocatore se desidera acquistare una vocale
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void askForVocal() throws RemoteException {
        Player activePlayer = players.get(turn);
        if (firstTurn || timer.isThisForJolly() || activePlayer.getPartialPoints() < 1000 || timer.isThisForSolution() || timer.isThisForVocal()) {
            timer.interrupt();
            errorInTurn(true, false);
            return;
        }
        timer.interrupt();
        notifyVocalAsk();
        activePlayer.updatePartialPoints(-1000);
        for (ClientInterface c : observers) {
            try {
                c.notifyPlayerStats(turn, activePlayer.getNickname(), activePlayer.getPartialPoints(), activePlayer.getPoints(), activePlayer.getNumJolly());
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                p.getClient().notifyPlayerStats(turn, activePlayer.getNickname(), activePlayer.getPartialPoints(), activePlayer.getPoints(), activePlayer.getNumJolly());
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
        startTimer(10000, false, false, true);
    }

    /**
     * Metodo per comunicare a tutti gli osservatori la richiesta di acquisto di una vocale
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void notifyVocalAsk() throws RemoteException {
        Player activePlayer = players.get(turn);
        for (ClientInterface c : observers) {
            try {
                c.notifyTryVocal(activePlayer.getNickname());
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                if (p.equals(activePlayer)) {
                } else {
                    p.getClient().notifyTryVocal(activePlayer.getNickname());
                }
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per confermare l'acquisto della vocale e verificare se � presente nella frase misteriosa
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void giveVocal(String letter) throws RemoteException {
        Player activePlayer = players.get(turn);
        char vocal;
        try {
            vocal = letter.charAt(0);
        } catch (IndexOutOfBoundsException e) {
            errorInTurn(true, false);
            return;
        }
        boolean isVocal = (vocal == 'A' || vocal == 'E' || vocal == 'I' || vocal == 'O' || vocal == 'U');
        if (firstTurn || timer.isThisForJolly() || timer.isThisForSolution() || letter.length() > 1 || !isVocal || !timer.isThisForVocal()) {
            timer.interrupt();
            errorInTurn(true, false);
            return;
        }
        timer.interrupt();
        notifyLetterCall(letter);
        for (ClientInterface c : observers) {
            try {
                c.updatePhrase(letter);
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                p.getClient().updatePhrase(letter);
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
        String phrase = manche.getCurrentPhrase().getPhrase().toUpperCase();
        StringTokenizer st = new StringTokenizer(phrase, " ',!?.:;\"/()\\^<>-+*");
        int j = 0;
        int counter = 0;
        while (st.hasMoreTokens()) {
            String ss = st.nextToken();
            for (int i = 0; i < ss.length(); i++) {
                if (ss.charAt(i) == letter.charAt(0)) {
                    if (phraseStatus[j] == false) {
                        phraseStatus[j] = true;
                        counter++;
                    }
                    j++;
                } else {
                    j++;
                }
            }
        }
        if (counter > 0) {
            manche.getTurns().addMove(activePlayer.getIdPlayer(), "" + letter, -1);
            startTurn(turn);
        } else {
            manche.getTurns().addMove(activePlayer.getIdPlayer(), "" + letter, 0);
            errorInTurn(true, true);
        }

    }

    /**
     * Metodo per l'utilizzo dei jolly
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void jolly() throws RemoteException {
        Player activePlayer = players.get(turn);
        if (!timer.isThisForJolly() || timer.isThisForSolution() || timer.isThisForVocal()) {
            timer.interrupt();
            errorInTurn(true, false);
            return;
        } else {
            timer.interrupt();
            manche.getTurns().getLastMove().setOutCome(-1);
            activePlayer.removeJolly();
            notifyJollyUsed();
            startTurn(turn);
        }
    }

    /**
     * Metodo per comunicare a tutti gli osservatori dell'avvenuto utilizzo di un jolly
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void notifyJollyUsed() throws RemoteException {
        Player activePlayer = players.get(turn);
        for (ClientInterface c : observers) {
            try {
                c.notifyJollyUsed(activePlayer.getNickname());
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                if (p.equals(activePlayer)) {
                } else {
                    p.getClient().notifyJollyUsed(activePlayer.getNickname());
                }
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per la rilevazione di errori durante il turno (ES. passa turno)
     * 
     * @param canJollyBeUsed contiene true se il giocatore ha un jolly disponibile ed � possibile utilizzarlo
     * @param moveDone contiene la mossa compiuta dal giocatore
     */
    void errorInTurn(boolean canJollyBeUsed, boolean moveDone) {
        Player activePlayer = players.get(turn);
        try {
            if (canJollyBeUsed) {
                notifyError();
                if (players.get(turn).getNumJolly() > 0) {
                    players.get(turn).getClient().askForJolly();
                    startTimer(time, true, false, false);
                } else {
                    if (moveDone)
                        manche.getTurns().getLastMove().setOutCome(0);
                    else
                        manche.getTurns().addMove(activePlayer.getIdPlayer(), "errore", 0);
                    nextTurn();
                }
            } else {
                if (moveDone)
                    manche.getTurns().getLastMove().setOutCome(0);
                else
                    manche.getTurns().addMove(activePlayer.getIdPlayer(), "errore", 0);
                nextTurn();
            }
        } catch (RemoteException e) {
            Server.serverError(null);
        }
    }


    /**
     * Metodo per effettuare il cambio di turno
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void nextTurn() throws RemoteException {
        if (turn == 2)
            turn = 0;
        else
            ++turn;

        startTurn(turn);
    }


    /**
     * Metodo per iniziare il turno di un giocatore
     * 
     * @param turn indica il giocatore di turno
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    private void startTurn(int turn) throws RemoteException {
        Player activePlayer = null;
        try {
            activePlayer = players.get(turn);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        spinnedWheel = false;
        for (ClientInterface c : observers) {
            try {
                c.notifyNewTurn(activePlayer.getNickname());
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                if (p.equals(activePlayer)) {
                    p.getClient().notifyNewTurn(p.getNickname());
                    p.getClient().notifyYourTurn();
                    startTimer(time, false, false, false);
                } else {
                    p.getClient().notifyNewTurn(activePlayer.getNickname());
                }
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    /**
     * Metodo per la richiesta di soluzione di una frase misteriosa
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void askForSolution() throws RemoteException {
        if (firstTurn || timer.isThisForJolly() || timer.isThisForVocal() || timer.isThisForSolution()) {
            timer.interrupt();
            errorInTurn(true, false);
            return;
        }
        timer.interrupt();
        notifySolutionAsk();
        startTimer(10000, false, true, false);
    }

    private void notifySolutionAsk() throws RemoteException {
        Player activePlayer = players.get(turn);
        for (ClientInterface c : observers) {
            try {
                c.notifyTryForSolution(activePlayer.getNickname());
            } catch (RemoteException e) {
                leaveMatchAsObserver(c);
            }
        }
        for (Player p : players) {
            try {
                if (p.equals(activePlayer)) {
                } else {
                    p.getClient().notifyTryForSolution(activePlayer.getNickname());
                }
            } catch (RemoteException e) {
                leaveMatchAsPlayer(p);
            }
        }
    }

    public void giveSolution(String solution) throws RemoteException {
        Player activePlayer = players.get(turn);
        if (firstTurn || timer.isThisForJolly() || !timer.isThisForSolution() || timer.isThisForVocal()) {
            timer.interrupt();
            errorInTurn(true, false);
            return;
        }
        timer.interrupt();
        String phrase = manche.getCurrentPhrase().getPhrase();
        if (solution.equals(phrase.toUpperCase().trim())) {
            manche.getTurns().addMove(activePlayer.getIdPlayer(), "soluzione", -1);
            endManche(activePlayer);
        } else {
            manche.getTurns().addMove(activePlayer.getIdPlayer(), "soluzione", 0);
            errorInTurn(true, true);
        }
    }


    /**
     * Metodo per iniziare la partita
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void startMatch() throws RemoteException {
        Random rnd = new Random();
        try {
            String idPlayer1 = players.get(0).getIdPlayer();
            String idPlayer2 = players.get(1).getIdPlayer();
            String idPlayer3 = players.get(2).getIdPlayer();

            List<PhrasesDTO> phrases = dbManager.get5Phrases(idPlayer1, idPlayer2, idPlayer3);
            
            if (phrases == null || phrases.size() < 5) {
                try {
                    for (ClientInterface c : observers) {
                        try {
                            c.notifyMatchAbort("GAME ABORTED, there aren't enough phrases");
                        } catch (RemoteException e) {
                            leaveMatchAsObserver(c);
                        }
                    }
                    for (Player p : players) {
                        try {
                            p.getClient().notifyMatchAbort("GAME ABORTED, there aren't enough phrases");
                        } catch (RemoteException er) {
                            players.remove(p);
                        }
                    }
                    MatchManager.deleteMatch(id);
                    boolean bool = dbManager.deleteMatch(id);
                    if (!bool) {
                        throw new SQLException();
                    }
                    List<UsersDTO> admins = dbManager.getAllAdmin();
                    String email = "";
                    String obj = "ATTENTION RdF";
                    String txt = "Add new phrases to the databse";
                    for (UsersDTO admin : admins) {
                        email = admin.getEmail();
                        emailmng.sendEmail(email, obj, txt);
                    }
                } catch (SQLException | RemoteException e) {
                    Server.serverError(null);
                }
            } else {
                matchEnded = false;
                manche.setPhrases(phrases);
                manche.setNumManche(1);
                PhrasesDTO newPhrase = manche.getCurrentPhrase();
                String theme = prepareStringForDB(newPhrase.getTheme());
                String phrase = prepareStringForDB(newPhrase.getPhrase());
                resetPhraseStatus(newPhrase.getPhrase());
                ManchesDTO manches = new ManchesDTO();
                manches.setNumber(manche.getNumManche());
                manches.setMatch(new MatchesDTO(id, creationTime));
                manches.setPhrase(new PhrasesDTO(newPhrase.getId(),theme, phrase));
                dbManager.addManche(manches);
                for (ClientInterface c : observers) {
                    try {
                        c.notifyMatchStart();
                        c.setNewPhrase(newPhrase.getTheme(), newPhrase.getPhrase());
                        dbManager.addMancheJoiner(id, manche.getNumManche(), c.getId(), true);
                    } catch (RemoteException e) {
                        leaveMatchAsObserver(c);
                    }
                }
                for (Player p : players) {
                    try {
                        p.getClient().notifyMatchStart();
                        p.getClient().setNewPhrase(newPhrase.getTheme(), newPhrase.getPhrase());
                        dbManager.addMancheJoiner(id, manche.getNumManche(), p.getIdPlayer(), false);
                    } catch (RemoteException e) {
                        leaveMatchAsPlayer(p);
                    }
                }

                turn = rnd.nextInt(3);
                nextTurn();
            }
        } catch (RemoteException e) {
            try {
                for (ClientInterface c : observers) {
                    try {
                        c.notifyMatchAbort("GAME ABORTED, connection error");
                    } catch (RemoteException d) {
                        leaveMatchAsObserver(c);
                    }
                }
                for (Player p : players) {
                    try {
                        p.getClient().notifyMatchAbort("GAME ABORTED, connection error");
                    } catch (RemoteException ex) {
                        players.remove(p);
                    }
                }
                MatchManager.deleteMatch(id);
                boolean bool = dbManager.deleteMatch(id);
                if (!bool) {
                    throw new SQLException();
                }
            } catch (SQLException | RemoteException ex) {
                Server.serverError(null);
            }
        }
        onGoing = true;
    }


    /**
     * Metodo per la preparazione delle stringhe per l'inserimento nel database
     * 
     * @param s contiene la stringa
     * @return ritorna il risultato modificato
     */
    static String prepareStringForDB(String s) {
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            result += c;
            if (c == '\'') {
                result += "'";
            }
        }
        return result;
    }

    /**
     * Metodo per filtrare caratteri speciali dalle frasi
     * 
     * @param phrase contiene la frase da esaminare
     */
    private void resetPhraseStatus(String phrase) {

        StringTokenizer st = new StringTokenizer(phrase, " ',!?.:;\"/()\\^<>-+*");
        int length = 0;

        while (st.hasMoreTokens()) {
            String s = st.nextToken();

            for (int j = 0; j < s.length(); j++) {
                length++;
            }
        }
        this.phraseStatus = new boolean[length];
        for (int k = 0; k < phraseStatus.length; k++) {
            phraseStatus[k] = false;
        }
    }


    /**
     * Metodo per terminare la manche con un vincitore
     * 
     * @param winner contiene l'oggetto di tipo Player del vincitore
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void endManche(Player winner) throws RemoteException {
        if(!matchEnded)
            manche.endManche(winner);
        firstTurn = true;
        if (winner != null) {
            for (Player p : players) {
                try {
                    if (p.equals(winner)) {
                        p.updatePoints(p.getPartialPoints());
                        p.partialPointsToZero();
                        p.getClient().notifyMancheVictory();
                    } else {
                        p.partialPointsToZero();
                        p.getClient().notifyMancheResult(winner.getNickname());
                    }
                } catch (RemoteException e) {
                    leaveMatchAsPlayer(p);
                }
            }
            if (manche.getNumManche() > 5) {
                endMatch(true);
                return;
            } else {
                PhrasesDTO newPhrase = manche.getCurrentPhrase();
                resetPhraseStatus(newPhrase.getPhrase());
                for (ClientInterface c : observers) {
                    try {
                        c.notifyMancheResult(winner.getNickname());
                        c.notifyNewManche(manche.getNumManche());
                        c.setNewPhrase(newPhrase.getTheme(), newPhrase.getPhrase());
                        Player p = null;
                        for (int i = 0; i < 3; i++) {
                            p = players.get(i);
                            c.notifyPlayerStats(i, p.getNickname(), 0, p.getPoints(), p.getNumJolly());
                        }
                        dbManager.addMancheJoiner(id, manche.getNumManche(), c.getId(), true);
                    } catch (RemoteException e) {
                        leaveMatchAsObserver(c);
                    }
                }
                for (Player p : players) {
                    try {
                        p.getClient().notifyNewManche(manche.getNumManche());
                        p.getClient().setNewPhrase(newPhrase.getTheme(), newPhrase.getPhrase());
                        Player pl = null;
                        for (int i = 0; i < 3; i++) {
                            pl = players.get(i);
                            p.getClient().notifyPlayerStats(i, pl.getNickname(), 0, pl.getPoints(), pl.getNumJolly());
                        }
                        dbManager.addMancheJoiner(id, manche.getNumManche(), p.getIdPlayer(), false);
                    } catch (RemoteException e) {
                        leaveMatchAsPlayer(p);
                    }
                }
                nextTurn();
            }
        }
    }


    /**
     * Metodo per terminare la partita in caso di un vincitore definitivo
     * 
     * @param isThereAWinner diventa true se il vincitore esiste, false altrimenti
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void endMatch(boolean isThereAWinner) throws RemoteException {
        MatchManager.deleteMatch(id);
        if (isThereAWinner) {
            Player winner = null;
            int maxPoint = 0;
            for (Player p : players) {
                if (p.getPoints() > maxPoint) {
                    maxPoint = p.getPoints();
                    winner = p;
                }
            }

            for (ClientInterface c : observers) {
                try {
                    c.notifyEndMatch(winner.getNickname());
                } catch (RemoteException e) {
                    leaveMatchAsObserver(c);
                }
            }

            for (Player p : players) {
                try {
                    if (p.equals(winner)) {
                        p.getClient().notifyMatchWin();
                    } else {
                        p.getClient().notifyEndMatch(winner.getNickname());
                    }
                } catch (RemoteException e) {
                    leaveMatchAsPlayer(p);
                }
            }

            dbManager.addMatchWinner(id, winner.getIdPlayer(), maxPoint);
        } else {
            if (manche.getNumManche() == 0)
                dbManager.deleteMatch(id);
            for (ClientInterface c : observers) {
                try {
                    c.notifyEndMatch("Nessuno");
                } catch (RemoteException e) {
                    leaveMatchAsObserver(c);
                }
            }

            for (Player p : players) {
                try {
                    p.getClient().notifyEndMatch("Nessuno");

                } catch (RemoteException e) {
                    leaveMatchAsPlayer(p);
                }
            }
        }

        UnicastRemoteObject.unexportObject(this, true);
    }

    /**
     * Metodo per l'aggiunta di giocatori alla partita
     * 
     * @param c contiene il client dell'utente che desidera unirsi
     * @return ritorna il valore booleano di full, che indica se la partita � chiusa (true) o aperta (false)
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public synchronized boolean addPlayer(ClientInterface c) throws RemoteException {
        boolean full;
        if (players.size() >= 3)
            full = true;
        else {
            players.add(new Player(c));
            if (players.size() != 1) {
                for (Player p : players) {
                    try {
                        if (!p.getClient().equals(c))
                            for (int i = 0; i < players.size(); i++)
                                p.getClient().notifyPlayerStats(i, players.get(i).getClient().getNickname(), 0, 0, 0);
                    } catch (RemoteException e) {
                        leaveMatchAsPlayer(p);
                    }
                }
                for (ClientInterface client : observers) {
                    try {
                        for (int i = 0; i < players.size(); i++)
                            client.notifyPlayerStats(i, players.get(i).getClient().getNickname(), 0, 0, 0);
                    } catch (RemoteException e) {
                        leaveMatchAsObserver(client);
                    }
                }
            }
            full = false;
        }
        return full;
    }


    /**
     * Metodo per l'aggiunta di un osservatore
     * 
     * @param c contiene il Client dell'osservatore interessato ad unirsi
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public synchronized void addObserver(ClientInterface c) throws RemoteException {
        observers.add(c);
        if (onGoing) {
            dbManager.addMancheJoiner(id, manche.getNumManche(), c.getId(), true);            
        }
    }

    /**
     * Metodo per iniziare il match al raggiungimento della quota massima di concorrenti
     * 
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    @Override
    public void tryForStartMatch() throws RemoteException {
        if (players.size() == 3)
            startMatch();
    }
    

    /**
     * Metodo per abbandonare la partita in quanto client
     * 
     * @param c contiene il Client dell'utente interessato
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public synchronized void leaveMatchAsPlayer(ClientInterface c) throws RemoteException {
        String name = c.getNickname();
        int num = 0;
        for (Player p : players) {
            if (p.getClient().equals(c))
                break;
            else
                num++;
        }
        Player player = null;
        for (Player p : players) {
            try {
                if (p.getClient().equals(c)) {
//                    player = p;
                } else {
                    p.getClient().notifyLeaver(name);
                    p.getClient().notifyPlayerStats(num, "--", 0, 0, 0);
                }
            } catch (RemoteException e) {
                player = p;
            }
        }
        if (player != null)
            players.remove(player);
        else
            players.remove(num);
        for (ClientInterface client : observers) {
            try {
                client.notifyLeaver(name);
                client.notifyPlayerStats(num, "--", 0, 0, 0);
            } catch (RemoteException e) {
                leaveMatchAsObserver(client);
            }
        }
        matchEnded = true;
        if (onGoing) {
            endManche(null);
            endMatch(false);
        } else {
            if (players.isEmpty()) {
                endMatch(false);
            }
        }

    }

    /**
     * Metodo per lasciare la partita da giocatore
     * 
     * @param player contiene l'oggetto di tipo Player che intende lasciare la partita
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    synchronized void leaveMatchAsPlayer(Player player) throws RemoteException {
        if (onGoing) {
            String name = player.getNickname();
            int num = 0;
            for (Player p : players) {
                if (p.equals(player))
                    break;
                else
                    num++;
            }
            Player player1 = null;
            for (Player p : players) {
                try {
                    if (p.getClient().equals(player)) {
                        player1 = p;
                    } else {
                        p.getClient().notifyLeaver(name);
                        p.getClient().notifyPlayerStats(num, "--", 0, 0, 0);
                    }
                } catch (RemoteException e) {
                    player1 = p;
                }
            }
            players.remove(player1);
            for (ClientInterface client : observers) {
                try {
                    client.notifyLeaver(name);
                    client.notifyPlayerStats(num, "--", 0, 0, 0);
                } catch (RemoteException e) {
                    leaveMatchAsObserver(client);
                }
            }
        }
        if (!matchEnded) {
            if (onGoing) {
                endManche(null);
                endMatch(false);
            } else {
                if (players.isEmpty()) {
                    endMatch(false);
                }
            }
        }
    }

    /**
     * Metodo per lasciare la partita in quanto utente di tipo osservatore
     * 
     * @param c contiene il Client dell'osservatore intenzionato a lasciare la partita
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public synchronized void leaveMatchAsObserver(ClientInterface c) throws RemoteException {
        observers.remove(c);
    }

    /**
     * Metodo per richiedere la notifica dei dati della manche corrente riguardanti un giocatore e relativa manche
     * 
     * @param c contiene il Client del giocatore bersaglio
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public void askNotify(ClientInterface c) throws RemoteException {
        if (onGoing && manche.getNumManche() > 0) {
            Player p = null;
            for (int i = 0; i < players.size(); i++) {
                p = players.get(i);
                c.notifyPlayerStats(i, p.getNickname(), p.getPartialPoints(), p.getPoints(), p.getNumJolly());
                c.setNewPhrase(manche.getCurrentPhrase().getTheme(), manche.getCurrentPhrase().getPhrase());
                c.updatePhrase(phraseStatus);
            }
        } else {
            for (int i = 0; i < players.size(); i++) {
                c.notifyPlayerStats(i, players.get(i).getNickname(), 0, 0, 0);
            }
        }
    }

    /**
     * Metodo getter per l'ottenimento dell'ID del match
     * 
     * @return id ritorna il valore dell'ID del match
     * @throws RemoteException per evitare eventuali problemi dovuti alla programmazione distribuita
     */
    public String getMatchId() throws RemoteException {
        return id;
    }


    /**
     * Metodo per la creazione dei dati del match
     * 
     * @return result ritorna i dati riguardanti il match
     */
    public MatchData createMatchData() {
        MatchData result = new MatchData();

        String noName = "--";
        result.setPlayer1(players.get(0).getNickname());
        if (players.size() >= 2) {
            result.setPlayer2(players.get(1).getNickname());
        } else {
            result.setPlayer2(noName);
        }
        if (players.size() == 3) {
            result.setPlayer3(players.get(2).getNickname());
        } else {
            result.setPlayer3(noName);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
        result.setDate(dtf.format(creationTime));
        dtf = DateTimeFormatter.ofPattern("HH:mm");
        result.setTime(dtf.format(creationTime));

        result.setOnGoing(onGoing);
        result.setNumManche(manche.getNumManche());
        result.setIdMatch(id);

        return result;
    }

    /**
     * Metodo getter per l'ottenimento della lista dei giocatori
     * 
     * @return players ritorna la lista dei giocatori del match
     */
    List<Player> getPlayers() {
        return players;
    }

    /**
     * Metodo getter per l'ottenimento della lista degli osservatori
     * 
     * @return observers ritorna la lista degli osservatori del match
     */
    List<ClientInterface> getObservers() {
        return observers;
    }

    /**
     * Metodo getter per l'ottenimento della manche corrente
     * 
     * @return manche ritorna l'oggetto di tipo Manche corrispondente alla manche corrente
     */
    Manche getManche() {
        return manche;
    }

    /**
     * Metodo getter per l'ottenimento del turno corrente
     * 
     * @return turn ritorna l'oggetto di tipo Turn corrispondente al turno del giocatore corrente
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Metodo per l'avvio del timer di risposta
     * 
     * @param time contiene il quantitativo di tempo
     * @param jolly contiene true se il giocatore possiede un jolly, false altrimenti
     * @param solution contiene true se la soluzione � corretta, false altrimenti
     * @param vocal contiene la vocale che si desidera chiamare
     */
    private void startTimer(int time, boolean jolly, boolean solution, boolean vocal) {
        timer = new MoveTimer(time, this, jolly, solution, vocal);
        timer.start();
    }

    /**
     * Metodo che verifica se il match � terminato
     * 
     * @return matchEnded ritorna true se il match � terminato, false altrimenti
     */
    public boolean isMatchEnded() {
        return matchEnded;
    }
}