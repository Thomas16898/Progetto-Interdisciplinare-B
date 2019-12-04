package GUI;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import Game.RemoteMatch;
import Server.Server;
import Services.Client;
import Services.Controller;
import Services.CryptPassword;
import Services.MatchData;
import Services.Notification;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.opencsv.exceptions.CsvValidationException;

import javax.swing.JPasswordField;
import javax.swing.JList;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

public class TabPaneController {

	private static JFrame frame;
    private Observable gameObservableList = new Observable();
    private static Client client;
	private static Server server;
	private static RemoteMatch match;
	private static MatchData matchData;
	private boolean isAdmin;
	public static boolean creator = true;
	
	private JPasswordField passwordField;
	private JLabel lblVictoryManchesValue;
	private JLabel lblVictoryGamesValue;
	private JLabel lblGamesPlayedValue;
	private JLabel lblManchesPlayedValue;
	private JLabel lblGamesOsservedValue;
	private JLabel lblAverageShiftsGamesValue;
	private JLabel lblManchesOsservedValue;
	private JLabel lblAverageShiftsManchesValue;
	private JLabel lblAverageLostEverythingGamesValue;
	private JLabel lblAverageLostEverythingManchesValue;
	private JLabel lblTopGameValue;
	private JLabel lblAveragePointMancheValue;
	private JLabel lblAverageMovesSolutionValue;
	private JLabel lblTopMancheValue;
	private JLabel lblMoreManchesValue;
	private JLabel lblGivedShiftValue;
	private JLabel lblLostEverythingValue;
	private JLabel lblPointWonValue;
	private JLabel lblBestCalledConsonant1;
	private JLabel lblBestCalledConsonant2;
	private JLabel lblBestCalledConsonant3;
	private JTextField textFieldAddPhrase;
	int posX = 0, posY = 0;
	private JList gameList;

	/**
	 * Create the application.
	 */
	public TabPaneController() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		MainPane.setArgs(this);
		creator = true;

		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.GRAY);
		frame.setBounds(100, 100, 1050, 683);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);

		JPanel panelMenu = new JPanel();
		panelMenu.setBackground(Color.DARK_GRAY);
		panelMenu.setBounds(0, 0, 284, 683);
		frame.getContentPane().add(panelMenu);
		if (!isAdmin)
			panelMenu.setLayout(new GridLayout(5, 1, 0, 20));
		else
			panelMenu.setLayout(new GridLayout(6, 1, 0, 20));

		JButton btnGames = new JButton("GAMES");
		btnGames.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnGames.setBackground(Color.DARK_GRAY);
		btnGames.setForeground(Color.WHITE);
		panelMenu.add(btnGames);

		JButton btnUserStatistics = new JButton("USER STATISTICS");
		btnUserStatistics.setBackground(Color.DARK_GRAY);
		btnUserStatistics.setForeground(Color.WHITE);
		btnUserStatistics.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panelMenu.add(btnUserStatistics);

		JButton btnGlobalStatistics = new JButton("GLOBAL STATISTICS");
		btnGlobalStatistics.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnGlobalStatistics.setBackground(Color.DARK_GRAY);
		btnGlobalStatistics.setForeground(Color.WHITE);
		panelMenu.add(btnGlobalStatistics);

		JButton btnAddPhrases = new JButton("ADD PHRASES");
		btnAddPhrases.setBackground(Color.DARK_GRAY);
		btnAddPhrases.setForeground(Color.WHITE);
		btnAddPhrases.setFont(new Font("Tahoma", Font.PLAIN, 18));
		if (isAdmin)
			panelMenu.add(btnAddPhrases);
		else
			btnAddPhrases.setVisible(false);

		JButton btnProfile = new JButton("PROFILE");
		btnProfile.setForeground(Color.WHITE);
		btnProfile.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnProfile.setBackground(Color.DARK_GRAY);
		panelMenu.add(btnProfile);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		panelMenu.add(panel_1);

		if (!isAdmin) {
			panel_1.setLayout(new GridLayout(1, 2, 0, 0));
			JButton btnNewMatch = new JButton("NEW \r\nMATCH");
			btnNewMatch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						addMatch();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			btnNewMatch.setBackground(Color.DARK_GRAY);
			btnNewMatch.setFont(new Font("Tahoma", Font.PLAIN, 18));
			btnNewMatch.setForeground(Color.WHITE);
			panel_1.add(btnNewMatch);
		} else {
			panel_1.setLayout(new GridLayout(1, 1, 0, 0));
		}

		JButton btnRefresh = new JButton("REFRESH");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		btnRefresh.setBackground(Color.DARK_GRAY);
		btnRefresh.setForeground(Color.WHITE);
		btnRefresh.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_1.add(btnRefresh);

		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		panel.setBounds(282, 0, 754, 683);
		frame.getContentPane().add(panel);
		panel.setLayout(new CardLayout(0, 0));

		JPanel panelGames = new JPanel();
		panelGames.setBackground(Color.GRAY);
		panel.add(panelGames, "name_861668335796200");

		gameList = new JList();
		panelGames.add(gameList);

		JPanel panelUsersStatistics = new JPanel();
		panelUsersStatistics.setBackground(Color.GRAY);
		panel.add(panelUsersStatistics, "name_861713845535800");
		panelUsersStatistics.setLayout(null);

		JLabel lblVictory = new JLabel("Victory");
		lblVictory.setHorizontalAlignment(SwingConstants.CENTER);
		lblVictory.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblVictory.setForeground(Color.WHITE);
		lblVictory.setBounds(10, 95, 772, 25);
		panelUsersStatistics.add(lblVictory);

		JLabel lblVictoryGames = new JLabel("Games...........................");
		lblVictoryGames.setForeground(Color.WHITE);
		lblVictoryGames.setFont(new Font("Dialog", Font.PLAIN, 20));
		lblVictoryGames.setBounds(195, 130, 270, 22);
		panelUsersStatistics.add(lblVictoryGames);

		lblVictoryGamesValue = new JLabel("0");
		lblVictoryGamesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblVictoryGamesValue.setForeground(Color.WHITE);
		lblVictoryGamesValue.setFont(new Font("Dialog", Font.PLAIN, 20));
		lblVictoryGamesValue.setBounds(411, 130, 180, 25);
		panelUsersStatistics.add(lblVictoryGamesValue);

		JLabel lblVictoryManches = new JLabel("Manches...........................");
		lblVictoryManches.setForeground(Color.WHITE);
		lblVictoryManches.setFont(new Font("Dialog", Font.PLAIN, 20));
		lblVictoryManches.setBounds(195, 168, 270, 25);
		panelUsersStatistics.add(lblVictoryManches);

		lblVictoryManchesValue = new JLabel("0");
		lblVictoryManchesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblVictoryManchesValue.setForeground(Color.WHITE);
		lblVictoryManchesValue.setFont(new Font("Dialog", Font.PLAIN, 20));
		lblVictoryManchesValue.setBounds(411, 174, 180, 19);
		panelUsersStatistics.add(lblVictoryManchesValue);

		JLabel lblGames = new JLabel("Games");
		lblGames.setHorizontalAlignment(SwingConstants.CENTER);
		lblGames.setForeground(Color.WHITE);
		lblGames.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblGames.setBounds(69, 266, 270, 25);
		panelUsersStatistics.add(lblGames);

		JLabel lblGamesPlayed = new JLabel("Played...........................");
		lblGamesPlayed.setForeground(Color.WHITE);
		lblGamesPlayed.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblGamesPlayed.setBounds(79, 301, 187, 13);
		panelUsersStatistics.add(lblGamesPlayed);

		JLabel lblGamesOsserved = new JLabel("Osserved...........................");
		lblGamesOsserved.setForeground(Color.WHITE);
		lblGamesOsserved.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblGamesOsserved.setBounds(79, 324, 187, 13);
		panelUsersStatistics.add(lblGamesOsserved);

		lblGamesPlayedValue = new JLabel("0");
		lblGamesPlayedValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblGamesPlayedValue.setForeground(Color.WHITE);
		lblGamesPlayedValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblGamesPlayedValue.setBounds(276, 301, 63, 13);
		panelUsersStatistics.add(lblGamesPlayedValue);

		lblGamesOsservedValue = new JLabel("0");
		lblGamesOsservedValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblGamesOsservedValue.setForeground(Color.WHITE);
		lblGamesOsservedValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblGamesOsservedValue.setBounds(276, 324, 63, 13);
		panelUsersStatistics.add(lblGamesOsservedValue);

		JLabel lblManches = new JLabel("Manches");
		lblManches.setHorizontalAlignment(SwingConstants.CENTER);
		lblManches.setForeground(Color.WHITE);
		lblManches.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblManches.setBounds(442, 266, 270, 25);
		panelUsersStatistics.add(lblManches);

		JLabel lblManchesPlayed = new JLabel("Played...........................");
		lblManchesPlayed.setForeground(Color.WHITE);
		lblManchesPlayed.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblManchesPlayed.setBounds(452, 301, 187, 13);
		panelUsersStatistics.add(lblManchesPlayed);

		JLabel lblManchesOsserved = new JLabel("Osserved...........................");
		lblManchesOsserved.setForeground(Color.WHITE);
		lblManchesOsserved.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblManchesOsserved.setBounds(452, 324, 187, 13);
		panelUsersStatistics.add(lblManchesOsserved);

		lblManchesPlayedValue = new JLabel("0");
		lblManchesPlayedValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblManchesPlayedValue.setForeground(Color.WHITE);
		lblManchesPlayedValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblManchesPlayedValue.setBounds(649, 301, 63, 13);
		panelUsersStatistics.add(lblManchesPlayedValue);

		lblManchesOsservedValue = new JLabel("0");
		lblManchesOsservedValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblManchesOsservedValue.setForeground(Color.WHITE);
		lblManchesOsservedValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblManchesOsservedValue.setBounds(649, 324, 63, 13);
		panelUsersStatistics.add(lblManchesOsservedValue);

		JLabel lblAverageSoldShifts = new JLabel("Average sold shifts");
		lblAverageSoldShifts.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageSoldShifts.setForeground(Color.WHITE);
		lblAverageSoldShifts.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblAverageSoldShifts.setBounds(69, 395, 270, 25);
		panelUsersStatistics.add(lblAverageSoldShifts);

		JLabel lblAverageShiftsGames = new JLabel("Games...........................");
		lblAverageShiftsGames.setForeground(Color.WHITE);
		lblAverageShiftsGames.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAverageShiftsGames.setBounds(79, 430, 187, 13);
		panelUsersStatistics.add(lblAverageShiftsGames);

		JLabel lblAverageShiftsManches = new JLabel("Manches...........................");
		lblAverageShiftsManches.setForeground(Color.WHITE);
		lblAverageShiftsManches.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAverageShiftsManches.setBounds(79, 453, 187, 13);
		panelUsersStatistics.add(lblAverageShiftsManches);

		lblAverageShiftsGamesValue = new JLabel("0");
		lblAverageShiftsGamesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageShiftsGamesValue.setForeground(Color.WHITE);
		lblAverageShiftsGamesValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAverageShiftsGamesValue.setBounds(276, 430, 63, 13);
		panelUsersStatistics.add(lblAverageShiftsGamesValue);

		lblAverageShiftsManchesValue = new JLabel("0");
		lblAverageShiftsManchesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageShiftsManchesValue.setForeground(Color.WHITE);
		lblAverageShiftsManchesValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAverageShiftsManchesValue.setBounds(276, 453, 63, 13);
		panelUsersStatistics.add(lblAverageShiftsManchesValue);

		JLabel lblAverageLostEverything = new JLabel("Average lost everything");
		lblAverageLostEverything.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageLostEverything.setForeground(Color.WHITE);
		lblAverageLostEverything.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblAverageLostEverything.setBounds(442, 395, 270, 25);
		panelUsersStatistics.add(lblAverageLostEverything);

		JLabel lblAverageLostEverythingGames = new JLabel("Games...........................");
		lblAverageLostEverythingGames.setForeground(Color.WHITE);
		lblAverageLostEverythingGames.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAverageLostEverythingGames.setBounds(452, 430, 187, 13);
		panelUsersStatistics.add(lblAverageLostEverythingGames);

		JLabel lblAverageLostEverythingManches = new JLabel("Manches...........................");
		lblAverageLostEverythingManches.setForeground(Color.WHITE);
		lblAverageLostEverythingManches.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAverageLostEverythingManches.setBounds(452, 453, 187, 13);
		panelUsersStatistics.add(lblAverageLostEverythingManches);

		lblAverageLostEverythingGamesValue = new JLabel("0");
		lblAverageLostEverythingGamesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageLostEverythingGamesValue.setForeground(Color.WHITE);
		lblAverageLostEverythingGamesValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAverageLostEverythingGamesValue.setBounds(649, 430, 63, 13);
		panelUsersStatistics.add(lblAverageLostEverythingGamesValue);

		lblAverageLostEverythingManchesValue = new JLabel("0");
		lblAverageLostEverythingManchesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageLostEverythingManchesValue.setForeground(Color.WHITE);
		lblAverageLostEverythingManchesValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAverageLostEverythingManchesValue.setBounds(649, 453, 63, 13);
		panelUsersStatistics.add(lblAverageLostEverythingManchesValue);

		JLabel lblAveragePointsWon = new JLabel("Average points won");
		lblAveragePointsWon.setHorizontalAlignment(SwingConstants.CENTER);
		lblAveragePointsWon.setForeground(Color.WHITE);
		lblAveragePointsWon.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblAveragePointsWon.setBounds(10, 523, 772, 25);
		panelUsersStatistics.add(lblAveragePointsWon);

		lblPointWonValue = new JLabel("0");
		lblPointWonValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblPointWonValue.setForeground(Color.WHITE);
		lblPointWonValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblPointWonValue.setBounds(370, 558, 63, 13);
		panelUsersStatistics.add(lblPointWonValue);

		JPanel panelGlobalStatistics = new JPanel();
		panel.add(panelGlobalStatistics, "name_863833183078700");
		panelGlobalStatistics.setBackground(Color.GRAY);
		panelGlobalStatistics.setLayout(null);

		JLabel lblTopScore = new JLabel("TOP SCORE");
		lblTopScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblTopScore.setForeground(Color.WHITE);
		lblTopScore.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblTopScore.setBounds(240, 33, 270, 25);
		panelGlobalStatistics.add(lblTopScore);

		JLabel lblTopGame = new JLabel("Game...........................");
		lblTopGame.setForeground(Color.WHITE);
		lblTopGame.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTopGame.setBounds(250, 68, 187, 13);
		panelGlobalStatistics.add(lblTopGame);

		JLabel lblTopManche = new JLabel("Manche...........................");
		lblTopManche.setForeground(Color.WHITE);
		lblTopManche.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTopManche.setBounds(250, 91, 187, 13);
		panelGlobalStatistics.add(lblTopManche);

		lblTopGameValue = new JLabel("0");
		lblTopGameValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblTopGameValue.setForeground(Color.WHITE);
		lblTopGameValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTopGameValue.setBounds(447, 68, 63, 13);
		panelGlobalStatistics.add(lblTopGameValue);

		lblTopMancheValue = new JLabel("0");
		lblTopMancheValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblTopMancheValue.setForeground(Color.WHITE);
		lblTopMancheValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTopMancheValue.setBounds(447, 91, 63, 13);
		panelGlobalStatistics.add(lblTopMancheValue);

		JLabel lblPlayedMoreManches = new JLabel("Played more manches");
		lblPlayedMoreManches.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayedMoreManches.setForeground(Color.WHITE);
		lblPlayedMoreManches.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblPlayedMoreManches.setBounds(103, 174, 270, 25);
		panelGlobalStatistics.add(lblPlayedMoreManches);

		lblMoreManchesValue = new JLabel("name");
		lblMoreManchesValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblMoreManchesValue.setForeground(Color.WHITE);
		lblMoreManchesValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblMoreManchesValue.setBounds(205, 209, 63, 13);
		panelGlobalStatistics.add(lblMoreManchesValue);

		JLabel lblAveragePointsManche = new JLabel("Average points for manche");
		lblAveragePointsManche.setHorizontalAlignment(SwingConstants.CENTER);
		lblAveragePointsManche.setForeground(Color.WHITE);
		lblAveragePointsManche.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblAveragePointsManche.setBounds(383, 174, 270, 25);
		panelGlobalStatistics.add(lblAveragePointsManche);

		lblAveragePointMancheValue = new JLabel("name");
		lblAveragePointMancheValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblAveragePointMancheValue.setForeground(Color.WHITE);
		lblAveragePointMancheValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAveragePointMancheValue.setBounds(485, 209, 63, 13);
		panelGlobalStatistics.add(lblAveragePointMancheValue);

		JLabel lblGiveUpTheTurn = new JLabel("More times gived up the shift");
		lblGiveUpTheTurn.setHorizontalAlignment(SwingConstants.CENTER);
		lblGiveUpTheTurn.setForeground(Color.WHITE);
		lblGiveUpTheTurn.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblGiveUpTheTurn.setBounds(103, 291, 270, 25);
		panelGlobalStatistics.add(lblGiveUpTheTurn);

		lblGivedShiftValue = new JLabel("name");
		lblGivedShiftValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblGivedShiftValue.setForeground(Color.WHITE);
		lblGivedShiftValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblGivedShiftValue.setBounds(205, 326, 63, 13);
		panelGlobalStatistics.add(lblGivedShiftValue);

		JLabel lblLostEverything = new JLabel("Lost Everything");
		lblLostEverything.setHorizontalAlignment(SwingConstants.CENTER);
		lblLostEverything.setForeground(Color.WHITE);
		lblLostEverything.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblLostEverything.setBounds(383, 291, 270, 25);
		panelGlobalStatistics.add(lblLostEverything);

		lblLostEverythingValue = new JLabel("name");
		lblLostEverythingValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblLostEverythingValue.setForeground(Color.WHITE);
		lblLostEverythingValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLostEverythingValue.setBounds(485, 326, 63, 13);
		panelGlobalStatistics.add(lblLostEverythingValue);

		JLabel lblBestCalledConsonant = new JLabel("Best called consonant");
		lblBestCalledConsonant.setHorizontalAlignment(SwingConstants.CENTER);
		lblBestCalledConsonant.setForeground(Color.WHITE);
		lblBestCalledConsonant.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblBestCalledConsonant.setBounds(240, 393, 270, 25);
		panelGlobalStatistics.add(lblBestCalledConsonant);

		lblBestCalledConsonant1 = new JLabel("name");
		lblBestCalledConsonant1.setHorizontalAlignment(SwingConstants.CENTER);
		lblBestCalledConsonant1.setForeground(Color.WHITE);
		lblBestCalledConsonant1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblBestCalledConsonant1.setBounds(342, 428, 63, 13);
		panelGlobalStatistics.add(lblBestCalledConsonant1);

		lblBestCalledConsonant2 = new JLabel("name");
		lblBestCalledConsonant2.setHorizontalAlignment(SwingConstants.CENTER);
		lblBestCalledConsonant2.setForeground(Color.WHITE);
		lblBestCalledConsonant2.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblBestCalledConsonant2.setBounds(342, 452, 63, 13);
		panelGlobalStatistics.add(lblBestCalledConsonant2);

		lblBestCalledConsonant3 = new JLabel("name");
		lblBestCalledConsonant3.setHorizontalAlignment(SwingConstants.CENTER);
		lblBestCalledConsonant3.setForeground(Color.WHITE);
		lblBestCalledConsonant3.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblBestCalledConsonant3.setBounds(342, 475, 63, 13);
		panelGlobalStatistics.add(lblBestCalledConsonant3);

		JLabel lblAverageMovesSolution = new JLabel("Average moves for solution");
		lblAverageMovesSolution.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageMovesSolution.setForeground(Color.WHITE);
		lblAverageMovesSolution.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblAverageMovesSolution.setBounds(240, 529, 270, 25);
		panelGlobalStatistics.add(lblAverageMovesSolution);

		lblAverageMovesSolutionValue = new JLabel("name");
		lblAverageMovesSolutionValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblAverageMovesSolutionValue.setForeground(Color.WHITE);
		lblAverageMovesSolutionValue.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAverageMovesSolutionValue.setBounds(342, 564, 63, 13);
		panelGlobalStatistics.add(lblAverageMovesSolutionValue);

		JPanel panelAddPhrases = new JPanel();
		panelAddPhrases.setBackground(Color.GRAY);
		panel.add(panelAddPhrases, "name_861757544902700");
		panelAddPhrases.setLayout(null);

		textFieldAddPhrase = new JTextField();
		textFieldAddPhrase.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textFieldAddPhrase.setBounds(93, 272, 430, 19);
		panelAddPhrases.add(textFieldAddPhrase);
		textFieldAddPhrase.setColumns(10);

		JLabel lblPhrase = new JLabel("Phrases file .csv");
		lblPhrase.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPhrase.setForeground(Color.WHITE);
		lblPhrase.setBounds(93, 249, 311, 13);
		panelAddPhrases.add(lblPhrase);

		JButton btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					enterFilePhrase();
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSend.setBackground(Color.GREEN);
		btnSend.setForeground(Color.WHITE);
		btnSend.setBounds(556, 338, 85, 21);
		panelAddPhrases.add(btnSend);

		JPanel panelProfile = new JPanel();
		panelProfile.setBackground(Color.GRAY);
		panel.add(panelProfile, "name_861780154478200");
		panelProfile.setLayout(null);

		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.LEFT);
		lblName.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblName.setForeground(Color.WHITE);
		lblName.setBounds(307, 105, 159, 18);
		panelProfile.add(lblName);

		JButton button = new JButton("");
		button.setIcon(new ImageIcon(TabPaneController.class.getResource("/img/icons8-user-96.png")));
		button.setBackground(Color.GRAY);
		button.setBounds(61, 80, 217, 203);
		panelProfile.add(button);

		JLabel lblSurname = new JLabel("Surname");
		lblSurname.setHorizontalAlignment(SwingConstants.LEFT);
		lblSurname.setForeground(Color.WHITE);
		lblSurname.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblSurname.setBounds(476, 105, 159, 18);
		panelProfile.add(lblSurname);

		JLabel lblNickname = new JLabel("Nickname");
		lblNickname.setHorizontalAlignment(SwingConstants.LEFT);
		lblNickname.setForeground(Color.WHITE);
		lblNickname.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNickname.setBounds(307, 151, 159, 18);
		panelProfile.add(lblNickname);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setHorizontalAlignment(SwingConstants.LEFT);
		lblEmail.setForeground(Color.WHITE);
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblEmail.setBounds(307, 197, 437, 25);
		panelProfile.add(lblEmail);

		JLabel lblChangePassword = new JLabel("Change Password");
		lblChangePassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblChangePassword.setForeground(Color.RED);
		lblChangePassword.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblChangePassword.setBounds(80, 369, 217, 22);
		panelProfile.add(lblChangePassword);

		passwordField = new JPasswordField();
		passwordField.setToolTipText("Insert new password here");
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		passwordField.setBounds(324, 369, 259, 22);
		panelProfile.add(passwordField);

		JButton btnReset = new JButton("RESET");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changePassword();	
			}
		});
		btnReset.setBackground(Color.RED);
		btnReset.setForeground(Color.WHITE);
		btnReset.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnReset.setBounds(614, 369, 107, 21);
		panelProfile.add(btnReset);
		frame.setVisible(true);

		btnGames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAddPhrases.setVisible(false);
				panelGames.setVisible(true);
				panelGlobalStatistics.setVisible(false);
				panelProfile.setVisible(false);
				panelUsersStatistics.setVisible(false);
			}
		});

		btnAddPhrases.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAddPhrases.setVisible(true);
				panelGames.setVisible(false);
				panelGlobalStatistics.setVisible(false);
				panelProfile.setVisible(false);
				panelUsersStatistics.setVisible(false);
			}
		});

		btnUserStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAddPhrases.setVisible(false);
				panelGames.setVisible(false);
				panelGlobalStatistics.setVisible(false);
				panelProfile.setVisible(false);
				panelUsersStatistics.setVisible(true);
			}
		});

		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAddPhrases.setVisible(false);
				panelGames.setVisible(false);
				panelGlobalStatistics.setVisible(false);
				panelProfile.setVisible(true);
				panelUsersStatistics.setVisible(false);
			}
		});

		btnGlobalStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAddPhrases.setVisible(false);
				panelGames.setVisible(false);
				panelGlobalStatistics.setVisible(true);
				panelProfile.setVisible(false);
				panelUsersStatistics.setVisible(false);
			}
		});

		try {
			setUserStat();
			setGlobalStats();
		} catch (RemoteException e) {
			Notification.notify("Errore", "Statistiche non caricate", true);
		}

		try {
			lblNickname.setText(client.getNickname());
			lblName.setText(client.getName());
			lblSurname.setText(client.getSurname());
			lblEmail.setText(client.getEmail());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});

		frame.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				// sets frame position when mouse dragged
				frame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});
	}


	public void addMatch() throws RemoteException {
		try {
			match = server.createMatch(client);
			// ON CLOSE LASCIARE LA PARTITA NELLA FINESTRA CREATA
			GameWheel gw = new GameWheel(match, client);
			frame.setVisible(false);
		} catch (RemoteException e) {
			client.notifyServerError();
		}
	}

	public void initializer() {

        gameList.setItems(gameObservableList);
        ArrayList<MatchData> list = new ArrayList<>();
        try {
            list = server.visualizeMatch(client);
            gameObservableList.addAll(list);
            gameList.setItems(gameObservableList);
        } catch (RemoteException e) {
            Notification.notify("Errore", "Server offline", true);
        }
        for (MatchData matchData : list) {
            gameList.setCellFactory(e -> new GameViewController(server, client, matchData));
        }
        disableTab();
        try {
            setUserStat();
            setGlobalStats();
        } catch (RemoteException e) {
            Notification.notify("Errore", "Statistiche non caricate", true);
        }

        try {
            nicknameLabel.setText(client.getNickname());
            nameLabel.setText(client.getName());
            surnameLabel.setText(client.getSurname());
            emailLabel.setText(client.getEmail());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

	}

	public void setUserStat() throws RemoteException {
		String userStat = server.checkPerPlayer(client.getNickname());
		if (!(userStat == null)) {
			StringTokenizer stasts = new StringTokenizer(userStat, " ");
			lblManchesPlayedValue.setText(stasts.nextToken());
			lblGamesPlayedValue.setText(stasts.nextToken());
			String numMancheObserved = stasts.nextToken();
			lblManchesOsservedValue.setText(numMancheObserved);
			// numberManchesObservedLabel1.setText(numMancheObserved); SECONDA SCHERMATA
			// STATISTICHE UTENTE
			String numMatchObserved = stasts.nextToken();
			lblGamesOsservedValue.setText(numMatchObserved);
			// numberMatchesObservedLabel1.setText(numMatchObserved);SECONDA SCHERMATA
			// STATISTICHE UTENTE
			lblVictoryManchesValue.setText(stasts.nextToken());
			lblVictoryGamesValue.setText(stasts.nextToken());
			lblPointWonValue.setText(stasts.nextToken());
			lblAverageShiftsManchesValue.setText(stasts.nextToken());
			lblAverageShiftsGamesValue.setText(stasts.nextToken());
			lblAverageLostEverythingManchesValue.setText(stasts.nextToken());
			lblAverageLostEverythingGamesValue.setText(stasts.nextToken());
		}

	}

	public void setGlobalStats() throws RemoteException {
		try {
			String recordStatStr = server.checkRecordPlayer();
			int avSolManches = server.averageManches();
			String strBestMove = server.bestMove();
			StringTokenizer bestMove = new StringTokenizer(strBestMove, " ");
			StringTokenizer recordStat = new StringTokenizer(recordStatStr, " ");

			lblTopMancheValue.setText(recordStat.nextToken());
			lblTopGameValue.setText(recordStat.nextToken());
			lblMoreManchesValue.setText(recordStat.nextToken());
			lblAveragePointMancheValue.setText(recordStat.nextToken());
			lblGivedShiftValue.setText(recordStat.nextToken());
			lblLostEverythingValue.setText(recordStat.nextToken());

			lblBestCalledConsonant1.setText(bestMove.nextToken());
			lblBestCalledConsonant3.setText(bestMove.nextToken());
			String phrase = "";
			while (bestMove.hasMoreElements()) {
				phrase += bestMove.nextToken() + " ";
			}
			lblBestCalledConsonant2.setText(phrase);
			lblAverageMovesSolutionValue.setText(String.valueOf(avSolManches));
		} catch (RemoteException e) {
			Notification.notify("Errore", "Server offline", true);
		}
	}

	/**
	 * Notifica che la partita alla quale si ha provato a partecipare come giocatore
	 * e' piena
	 */
	public void notifyTooManyPlayers() {

		Thread t = new Thread() {
			public void run() {
				Notification.notify("Giocatori", "Troppi giocatori", true);
			}
		};
		t.start();
	}

	/**
	 * Ricarica la lista delle partite disponibili aggiornata
	 */
	public void refresh() {
		gameObservableList = FXCollections.observableArrayList();
		gameList.setItems(gameObservableList);
		ArrayList<MatchData> list = new ArrayList<>();
		try {
			list = server.visualizeMatch(client);
			gameObservableList.addAll(list);
			gameList.setItems(gameObservableList);
		} catch (RemoteException e) {
			Notification.notify("Errore", "Non � stato possibile aggiornare la lista dei match\n Riprova", true);
		}
		for (MatchData matchData : list) {
			gameList.setCellFactory(e -> new GameViewController(server, client, matchData));
		}
	}

	public void enterFilePhrase() throws CsvValidationException {
		String phrases = textFieldAddPhrase.getText();
		if(phrases.equals(""))
			Notification.notify("ERROR", "INSERT A VALID CSV", false);
		else {
			String phrasesTrim = phrases.trim();
			File filePhrases = new File(phrasesTrim);
			try {
				boolean bool = server.addPhrases(filePhrases);
				if (bool) {
					Thread t = new Thread() {
						public void run() {
							Notification.notify("Successo", "Le frasi sono state aggiunte con successo", false);
						}
					}; t.start();
				} else {
					Thread t = new Thread() {
						public void run() {
							Notification.notify("ERROR", "Non � stato possibile aggiungere le nuove frasi\\n Riprova",
									false);
						}
					};t.start();
				}
			} catch (RemoteException e) {
				Notification.notify("Errore", "Server offline", true);
			}
		}
			

	}

	public void changePassword() {
		String password = passwordField.getText();
		if (!password.equals("")) {
			password = CryptPassword.encrypt(password);
			boolean bool = false;
			try {
				bool = server.changePassword(password, client);
			} catch (RemoteException e) {
				Notification.notify("Errore", "Server offline", true);
			}
			if (bool) {
				Notification.notify("Successo", "La password � stata modificata con successo", false);
			} else {
				Notification.notify("Errore", "Non � stato possibile modificare la password", true);
			}
		}
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	/**
	 * Metodo utilizzato per passare le informazioni del client a
	 * {@link GamePlayerController}
	 *
	 * @param gpc il riferimento al controller {@link GamePlayerController}
	 */
	public static void setGameControlle(GamePlayerController gpc) {
		gpc.setClient(client);
		gpc.setMatch(match);
		gpc.setObserver(false);
	}

	/**
	 * Notifica che una partita e' stata annullata a causa di un problema
	 *
	 * @param reason il motivo per cui la partita e' stata annullata (ad esempio
	 *               perche' non e' stato possibile scegliere le cinque frasi)
	 */
	public static void notifyMatchAbort(String reason) {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("Notifica di partita", reason, false);
			}
		};
		t.start();
	}

	/**
	 * Notifica che la partita si e' conclusa
	 *
	 * @param message un messaggio contenente il vincitore della partita, o nessuno
	 *                in caso in cui la partita si sia interrotta a meta'
	 */
	public static void notifyMatchEnd(String message) {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("Notifica di partita", message, false);
			}
		};
		t.start();
	}

	/**
	 * Notifica che il client corrente e' il vincitore della partita
	 */
	public static void notifyMatchWin() {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("Notifica di partita", "YOU WON!", false);
			}
		};
		t.start();
	}

	/**
	 * Notifica che un utente ha abbandonato la partita
	 *
	 * @param message il messaggio contenente il nickname di chi ha abbandonato
	 */
	public static void notifyLeaver(String message) {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("Notifica di partita", message, false);
			}
		};
		t.start();
	}
	
	public static void setVisible() {
		frame.setVisible(true);
	}
}
