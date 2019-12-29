package game;


import java.rmi.RemoteException;

import serverRdF.Server;
import services.ClientInterface;


public class MoveTimer extends Thread {
    private int time;
    private Match match;
    private boolean isThisForJolly;
    private boolean isThisForSolution;
    private boolean isThisForVocal;

    public MoveTimer(int time, Match match, boolean jolly, boolean solution, boolean vocal){
        this.time = time;
        this.match = match;
        isThisForJolly = jolly;
        isThisForSolution = solution;
        isThisForVocal = vocal;
    }

    /**
     * Metodo per determinare se � un timer per la soluzione
     * 
     * @return true se � per la soluzione, false altrimenti
     */
    public boolean isThisForSolution() {
        return isThisForSolution;
    }

    /**
     * Metodo per determinare se � un timer per il jolly
     * 
     * @return true se � per il jolly, false altrimenti
     */
    public boolean isThisForJolly() {
        return isThisForJolly;
    }

    /**
     * Metodo per determinare se � un timer per la chiamata di vocale
     * 
     * @return true se � per la chiamata di vocale, false altrimenti
     */
    public boolean isThisForVocal() {
        return isThisForVocal;
    }


    /**
     * Metodo di esecuzione del Thread che gestisce il timer
     */
    public void run(){
        try{
            int seconds = time/1000;
            for(int i=seconds; i>=0; i--){
                for(ClientInterface c : match.getObservers()) {
                    try {
                        c.updateTimer(i);
                    } catch (RemoteException e) {
                        try {
                            match.leaveMatchAsObserver(c);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                for(Player p : match.getPlayers()) {
                    try {
                        p.getClient().updateTimer(i);
                    } catch (RemoteException e) {
                        try {
                            match.leaveMatchAsPlayer(p);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                sleep(5000); 
            }
//            sleep(time);
            if(!match.isMatchEnded()) {
                for (ClientInterface c : match.getObservers()) {
                    try {
                        c.notifyTimeOut();
                    } catch (RemoteException e) {
                        try {
                            match.leaveMatchAsObserver(c);
                        } catch (RemoteException ex) {
                            Server.serverError(null);
                        }
                    }
                }
                for (Player p : match.getPlayers()) {
                    try {
                        p.getClient().notifyTimeOut();
                    } catch (RemoteException e) {
                        try {
                            match.leaveMatchAsPlayer(p);
                        } catch (RemoteException ex) {
                            Server.serverError(null);
                        }
                    }
                }
                if (isThisForJolly) {
                    match.errorInTurn(false, true);
                } else {
                    match.errorInTurn(true, false);
                }
            }
        }catch(InterruptedException e){
            return;
        }
    }
}
