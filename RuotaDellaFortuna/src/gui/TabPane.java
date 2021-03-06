package gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.List;

import java.util.StringTokenizer;

import javax.swing.JFrame;

import game.RemoteMatch;

import serverRdF.ServerInterface;
import services.ClientInterface;
import services.GameBeingPlayed2;
import services.GameBeingPlayed2Render;
import services.MatchData;
import services.Notification;

import javax.swing.DefaultListModel;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.opencsv.exceptions.CsvValidationException;

import database.PhrasesDTO;

import javax.swing.JPasswordField;
import javax.swing.JList;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.ImageIcon;


public class TabPane {

	private static JFrame frame;
	private static ClientInterface client;
	private static ServerInterface server;
	private static RemoteMatch match;
	private static MatchData matchData;
	private boolean isAdmin;
	public static boolean creator = true;

	private JPasswordField passwordFieldNuova;
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
	private JLabel lblNickname;
	int posX = 0, posY = 0;
	private JPanel panelGames;
	ArrayList<MatchData> list2;
	private DefaultListModel<GameBeingPlayed2> listModel;
	JList<GameBeingPlayed2> gamePlayedList;
	private JTextField textFieldNickname;
	private JTextField textFieldChangeName;
	private JTextField textFieldChangeSurname;
	private JPasswordField passwordFieldVecchia;
	private JLabel lblSurname;
	private JLabel lblName;
	private JList listPhrases;
	private JList listThemes;
	private DefaultListModel<String> modelThemes;
	private DefaultListModel<String> modelPhrases;
	private DefaultListModel<Integer> modelId;
	private JTextField textFieldPhrase;
	private JTextField textFieldTheme;
	private int selectedListItem;
	private String[] themesArray;
	private String[] phrasesArray;
	private JList listId;
	private JButton btnAdd;

	/**
	 * Create the application.
	 */
	public TabPane() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		MainPane.setArgs(this);
		creator = true;

		try {
			frame = new JFrame("Rdf: " + client.getNickname());
		} catch (HeadlessException | RemoteException e2) {
			e2.printStackTrace();
		}
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
			panelMenu.setLayout(new GridLayout(5, 1, 0, 20));

		JButton btnGames = new JButton("GAMES");
		btnGames.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnGames.setBackground(Color.DARK_GRAY);
		btnGames.setForeground(Color.WHITE);
		panelMenu.add(btnGames);

		JButton btnUserStatistics = new JButton("USER STATISTICS");
		btnUserStatistics.setBackground(Color.DARK_GRAY);
		btnUserStatistics.setForeground(Color.WHITE);
		btnUserStatistics.setFont(new Font("Tahoma", Font.PLAIN, 18));
		if (isAdmin)
			btnUserStatistics.setVisible(false);
		else
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
						frame.setVisible(false);
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
		panel.setBounds(282, 0, 758, 641);
		frame.getContentPane().add(panel);
		panel.setLayout(new CardLayout(0, 0));

		panelGames = new JPanel();
		panelGames.setAutoscrolls(true);
		panelGames.setBackground(Color.GRAY);
		panel.add(panelGames, "name_861668335796200");

		panelGames.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});

		panelGames.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				// sets frame position when mouse dragged
				frame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});

		listModel = new DefaultListModel<>();
		// create the list
		list2 = new ArrayList<>();
		gamePlayedList = new JList<GameBeingPlayed2>(listModel);
		gamePlayedList.setForeground(Color.WHITE);
		gamePlayedList.setFont(new Font("Tahoma", Font.BOLD, 18));
		gamePlayedList.setBackground(Color.GRAY);
		uploadGameInProgress();
		panelGames.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		gamePlayedList.setCellRenderer(new GameBeingPlayed2Render());
		// frame.getContentPane().add(new JScrollPane(countryList2));
		panelGames.add(gamePlayedList);

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
		lblMoreManchesValue.setBounds(145, 209, 187, 13);
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
		lblAveragePointMancheValue.setBounds(393, 209, 260, 13);
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
		lblGivedShiftValue.setBounds(103, 326, 270, 13);
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
		lblLostEverythingValue.setBounds(446, 326, 158, 13);
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
		lblBestCalledConsonant1.setBounds(240, 428, 260, 13);
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
		lblBestCalledConsonant3.setBounds(189, 475, 365, 13);
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
		panelAddPhrases.setBorder(null);
		panelAddPhrases.setBackground(Color.GRAY);
		panel.add(panelAddPhrases, "name_861757544902700");
		panelAddPhrases.setLayout(null);

		textFieldAddPhrase = new JTextField();
		textFieldAddPhrase.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textFieldAddPhrase.setBounds(127, 22, 430, 19);
		panelAddPhrases.add(textFieldAddPhrase);
		textFieldAddPhrase.setColumns(10);

		JLabel lblPhrase = new JLabel("Phrases file .csv");
		lblPhrase.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPhrase.setForeground(Color.WHITE);
		lblPhrase.setBounds(10, 25, 311, 13);
		panelAddPhrases.add(lblPhrase);

		JButton btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					enterFilePhrase();
					modelPhrases.clear();
					modelThemes.clear();
					downloadPhrases();
				} catch (CsvValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnSend.setBackground(Color.GREEN);
		btnSend.setForeground(Color.WHITE);
		btnSend.setBounds(663, 20, 85, 21);
		panelAddPhrases.add(btnSend);

		modelPhrases = new DefaultListModel<>();
		listPhrases = new JList(modelPhrases);
		listPhrases.setForeground(Color.WHITE);
		listPhrases.setBackground(Color.DARK_GRAY);
		listPhrases.setBounds(57, 62, 411, 498);
		panelAddPhrases.add(listPhrases);

		listPhrases.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				listThemes.setSelectedIndex(listPhrases.getSelectedIndex());
				listId.setSelectedIndex(listPhrases.getSelectedIndex());
			}
		});

		listPhrases.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getClickCount() == 2) {
					textFieldPhrase.setText((String) listPhrases.getSelectedValue());
					textFieldTheme.setText((String) listThemes.getSelectedValue());
					selectedListItem = listPhrases.getSelectedIndex();
				}
			}
		});

		modelThemes = new DefaultListModel<>();
		listThemes = new JList(modelThemes);
		listThemes.setForeground(Color.WHITE);
		listThemes.setBackground(Color.DARK_GRAY);
		listThemes.setBounds(478, 62, 270, 498);
		panelAddPhrases.add(listThemes);
		listThemes.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				listPhrases.setSelectedIndex(listThemes.getSelectedIndex());
				listThemes.setSelectedIndex(listThemes.getSelectedIndex());
			}
		});

		textFieldPhrase = new JTextField();
		textFieldPhrase.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textFieldPhrase.setBounds(10, 593, 432, 19);
		panelAddPhrases.add(textFieldPhrase);
		textFieldPhrase.setColumns(10);

		textFieldTheme = new JTextField();
		textFieldTheme.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textFieldTheme.setBounds(452, 593, 296, 19);
		panelAddPhrases.add(textFieldTheme);
		textFieldTheme.setColumns(10);

		JLabel lblSelectThePhrase = new JLabel(
				"Select the phrase or the theme (double click on it) and edit it, remeber to upload the changes by clicking on the CONFIRM CHANGES buttom!! ");
		lblSelectThePhrase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSelectThePhrase.setForeground(Color.WHITE);
		lblSelectThePhrase.setBounds(10, 570, 738, 13);
		panelAddPhrases.add(lblSelectThePhrase);

		JButton btnConfirmChanges = new JButton("CONFIRM CHANGES");
		btnConfirmChanges.setBackground(Color.ORANGE);
		btnConfirmChanges.setForeground(Color.WHITE);
		btnConfirmChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (textFieldPhrase.getText().equals("") || textFieldTheme.getText().equals("")) {
					Notification.notify("ATTENTION", "Select the phrase or theme to edit");
				} else {
					modelPhrases.setElementAt(textFieldPhrase.getText(), selectedListItem);
					modelThemes.setElementAt(textFieldTheme.getText(), selectedListItem);
					uploadPhrases(modelId.getElementAt(listId.getSelectedIndex()), textFieldTheme.getText(),
							textFieldPhrase.getText());
					listPhrases.clearSelection();
					listThemes.clearSelection();
					listId.clearSelection();
					textFieldPhrase.setText("");
					textFieldTheme.setText("");
				}
			}
		});
		btnConfirmChanges.setBounds(127, 622, 233, 21);
		panelAddPhrases.add(btnConfirmChanges);
		btnConfirmChanges.setFont(new Font("Tahoma", Font.BOLD, 18));

		modelId = new DefaultListModel<Integer>();
		listId = new JList(modelId);
		listId.setForeground(Color.WHITE);
		listId.setBackground(Color.DARK_GRAY);
		listId.setBounds(10, 62, 37, 498);
		panelAddPhrases.add(listId);
		listId.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				listPhrases.setSelectedIndex(listId.getSelectedIndex());
				listThemes.setSelectedIndex(listId.getSelectedIndex());
			}
		});
		listId.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getClickCount() == 2) {
					textFieldPhrase.setText(modelPhrases.getElementAt(listId.getSelectedIndex()));
					textFieldTheme.setText(modelThemes.getElementAt(listId.getSelectedIndex()));
				}
			}
		});

		JButton btnDelete = new JButton("DELETE");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deletePhrase(modelId.getElementAt(listId.getSelectedIndex()));
				refresh();
				listId.clearSelection();
				listPhrases.clearSelection();
				listThemes.clearSelection();
			}
		});
		btnDelete.setBackground(Color.RED);
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnDelete.setForeground(Color.BLACK);
		btnDelete.setBounds(514, 622, 120, 21);
		panelAddPhrases.add(btnDelete);

		btnAdd = new JButton("ADD");
		btnAdd.setBackground(Color.GREEN);
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setBounds(383, 622, 121, 21);
		panelAddPhrases.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addPhrase();
			}
		});

		listThemes.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				JList list = (JList) e.getSource();
				if (e.getClickCount() == 2) {
					textFieldPhrase.setText((String) listPhrases.getSelectedValue());
					textFieldTheme.setText((String) listThemes.getSelectedValue());
					selectedListItem = listThemes.getSelectedIndex();
				}
			}
		});

		JPanel panelProfile = new JPanel();
		panelProfile.setBackground(Color.GRAY);
		panel.add(panelProfile, "name_861780154478200");
		panelProfile.setLayout(null);

		lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.LEFT);
		lblName.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblName.setForeground(Color.WHITE);
		lblName.setBounds(78, 105, 159, 18);
		panelProfile.add(lblName);


		lblSurname = new JLabel("Surname");
		lblSurname.setHorizontalAlignment(SwingConstants.LEFT);
		lblSurname.setForeground(Color.WHITE);
		lblSurname.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblSurname.setBounds(476, 105, 159, 18);
		panelProfile.add(lblSurname);

		lblNickname = new JLabel("Nickname");
		lblNickname.setHorizontalAlignment(SwingConstants.CENTER);
		lblNickname.setForeground(Color.WHITE);
		lblNickname.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNickname.setBounds(78, 151, 526, 18);
		panelProfile.add(lblNickname);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setForeground(Color.WHITE);
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblEmail.setBounds(78, 197, 526, 25);
		panelProfile.add(lblEmail);

		JLabel lblChangePassword = new JLabel("Change Password");
		lblChangePassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblChangePassword.setForeground(Color.RED);
		lblChangePassword.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblChangePassword.setBounds(61, 446, 217, 22);
		panelProfile.add(lblChangePassword);

		passwordFieldNuova = new JPasswordField();
		passwordFieldNuova.setToolTipText("Insert the new password");
		passwordFieldNuova.setFont(new Font("Tahoma", Font.PLAIN, 15));
		passwordFieldNuova.setBounds(464, 451, 140, 22);
		panelProfile.add(passwordFieldNuova);

		JButton btnReset = new JButton("RESET");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					changePassword();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnReset.setBackground(Color.RED);
		btnReset.setForeground(Color.WHITE);
		btnReset.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnReset.setBounds(614, 451, 107, 21);
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

			Notification.notify("ERROR", "Statistics not uploaded");
		}

		try {
			lblNickname.setText(client.getNickname());
			lblName.setText(client.getName());
			lblSurname.setText(client.getSurname());
			lblEmail.setText(client.getEmail());

			JLabel lblChangeNickname = new JLabel("Change Nickname");
			lblChangeNickname.setHorizontalAlignment(SwingConstants.CENTER);
			lblChangeNickname.setForeground(Color.RED);
			lblChangeNickname.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblChangeNickname.setBounds(61, 306, 217, 25);
			panelProfile.add(lblChangeNickname);

			textFieldNickname = new JTextField();
			textFieldNickname.setBounds(307, 309, 297, 22);
			panelProfile.add(textFieldNickname);
			textFieldNickname.setColumns(10);

			JButton btnChange = new JButton("CHANGE");
			btnChange.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					changeNickname();
				}
			});
			btnChange.setForeground(Color.WHITE);
			btnChange.setFont(new Font("Tahoma", Font.PLAIN, 18));
			btnChange.setBackground(Color.RED);
			btnChange.setBounds(614, 309, 107, 21);
			panelProfile.add(btnChange);

			JLabel lblChangeName = new JLabel("Change Name");
			lblChangeName.setHorizontalAlignment(SwingConstants.CENTER);
			lblChangeName.setForeground(Color.RED);
			lblChangeName.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblChangeName.setBounds(61, 351, 217, 25);
			panelProfile.add(lblChangeName);

			textFieldChangeName = new JTextField();
			textFieldChangeName.setColumns(10);
			textFieldChangeName.setBounds(307, 357, 297, 22);
			panelProfile.add(textFieldChangeName);

			JButton btnChangeName = new JButton("CHANGE");
			btnChangeName.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changeName();
				}
			});
			btnChangeName.setForeground(Color.WHITE);
			btnChangeName.setFont(new Font("Tahoma", Font.PLAIN, 18));
			btnChangeName.setBackground(Color.RED);
			btnChangeName.setBounds(614, 358, 107, 21);
			panelProfile.add(btnChangeName);

			JLabel lblChangeSurname = new JLabel("Change Surname");
			lblChangeSurname.setHorizontalAlignment(SwingConstants.CENTER);
			lblChangeSurname.setForeground(Color.RED);
			lblChangeSurname.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblChangeSurname.setBounds(61, 401, 217, 25);
			panelProfile.add(lblChangeSurname);

			textFieldChangeSurname = new JTextField();
			textFieldChangeSurname.setColumns(10);
			textFieldChangeSurname.setBounds(307, 408, 297, 22);
			panelProfile.add(textFieldChangeSurname);

			JButton btnChangeSurname = new JButton("CHANGE");
			btnChangeSurname.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changeSurname();
				}
			});
			btnChangeSurname.setForeground(Color.WHITE);
			btnChangeSurname.setFont(new Font("Tahoma", Font.PLAIN, 18));
			btnChangeSurname.setBackground(Color.RED);
			btnChangeSurname.setBounds(614, 407, 107, 21);
			panelProfile.add(btnChangeSurname);

			passwordFieldVecchia = new JPasswordField();
			passwordFieldVecchia.setToolTipText("Insert the current password");
			passwordFieldVecchia.setBounds(307, 451, 147, 22);
			panelProfile.add(passwordFieldVecchia);

			JButton btnExit = new JButton("EXIT");
			btnExit.setBackground(Color.RED);
			btnExit.setFont(new Font("Tahoma", Font.PLAIN, 18));
			btnExit.setBounds(943, 651, 85, 21);
			frame.getContentPane().add(btnExit);
			btnExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
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

		gamePlayedList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});

		gamePlayedList.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				// sets frame position when mouse dragged
				frame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});

		downloadPhrases();

	}

	public void uploadGameInProgress() {
		list2.clear();
		GameBeingPlayed2Render.setChosen(false);
		gamePlayedList.removeAll();
		gamePlayedList.clearSelection();
		listModel.clear();
		try {
			list2 = server.visualizeMatch(client);
		} catch (RemoteException e) {
			Notification.notify("ERROR", "The server is offline");
		}
		if (list2.size() > 0) {
			for (MatchData matchData : list2) {
				listModel.addElement(new GameBeingPlayed2(server, client, matchData));
			}
		}

	}

	public void addMatch() throws RemoteException {
		try {
			match = server.createMatch(client);
			// ON CLOSE LASCIARE LA PARTITA NELLA FINESTRA CREATA
			Game g = new Game(match, client);
			// frame.setVisible(false);
		} catch (RemoteException e) {
			client.notifyServerError();
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
			Notification.notify("Errore", "Server offline");
		}
	}

	public static void notifyTooManyPlayers() {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("ERROR", "GAME STARTED YET");
			}
		};
		t.start();
	}

	public void refresh() {
		textFieldPhrase.setText("");
		textFieldTheme.setText("");
		uploadGameInProgress();
		downloadPhrases();
		try {
			setUserStat();
			setGlobalStats();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void enterFilePhrase() throws CsvValidationException {
		String phrases = textFieldAddPhrase.getText();
		if (phrases.equals(""))
			Notification.notify("ERROR", "INSERT A VALID CSV");
		else {
			String phrasesTrim = phrases.trim();
			File filePhrases = new File(phrasesTrim);
			try {
				boolean bool = server.addPhrases(filePhrases);
				if (bool) {
					Thread t = new Thread() {
						public void run() {
							Notification.notify("OK", "Phrases added");
						}
					};
					t.start();
				} else {
					Thread t = new Thread() {
						public void run() {
							Notification.notify("ERROR", "Retry");
						}
					};
					t.start();
				}
			} catch (RemoteException e) {
				Notification.notify("ERROR", "Server offline");
			}
		}

	}

	public void changePassword() throws RemoteException {
		if (passwordFieldNuova.getText().equals("") || passwordFieldVecchia.getText().equals("")) {
			Notification.notify("ERRRO", "Insert the password");
		} else {
			String password = passwordFieldNuova.getText();
			String pswVecchia = passwordFieldVecchia.getText();

			if (server.checkPassword(client.getNickname(), pswVecchia, client)) {
				// password = CryptPassword.crypt(password);
				boolean bool = false;
				try {
					bool = server.changePassword(password, client);
				} catch (RemoteException e) {
					Notification.notify("ERROR", "Server offline");
					passwordFieldNuova.setText("");
					passwordFieldVecchia.setText("");
				}
				if (bool) {
					Notification.notify("OK", "Password changed");
					passwordFieldNuova.setText("");
					passwordFieldVecchia.setText("");

				} else {
					Notification.notify("ERROR", "RETRY");
					passwordFieldNuova.setText("");
					passwordFieldVecchia.setText("");
				}
			} else {
				Notification.notify("ERROR", "Current password error");
				passwordFieldNuova.setText("");
				passwordFieldVecchia.setText("");
			}

		}

	}

	public void setClient(ClientInterface client) {
		this.client = client;
	}

	public void setServer(ServerInterface server) {
		this.server = server;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public static void setGameControlle(Game gpc) {
		gpc.setClient(client);
		gpc.setMatch(match);
		gpc.setObserver(false);
	}

	public static void notifyMatchAbort(String reason) {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("GAME", reason);
			}
		};
		t.start();
	}

	public static void notifyMatchEnd(String message) {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("GAME", message);
			}
		};
		t.start();
	}

	public static void notifyMatchWin() {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("GAME", "YOU WON!");
			}
		};
		t.start();
	}

	public static void notifyLeaver(String message) {
		Thread t = new Thread() {
			public void run() {
				Notification.notify("NOTIFICATION", message);
			}
		};
		t.start();
	}

	public static void setVisible() {
		frame.setVisible(true);
	}

	public static void setInvisible() {
		frame.setVisible(false);

	}

	public void changeNickname() {
		String nickname = textFieldNickname.getText();
		if (!nickname.equals("")) {
			boolean bool = false;
			try {
				bool = server.changeNickname(nickname, client);
			} catch (RemoteException e) {
				Notification.notify("ERROR", "SERVER OFFLINE");
			}
			if (bool) {
				try {
					client.setNickname(nickname);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				lblNickname.setText(nickname);
				Notification.notify("SUCCES", "Nickname changed");
			} else {
				Notification.notify("ERROR", "Nickname already in use");
			}
			textFieldNickname.setText("");
		}
	}

	public void changeSurname() {
		String surname = textFieldChangeSurname.getText();
		if (!surname.equals("")) {
			boolean bool = false;
			try {
				bool = server.changeSurname(surname, client);
			} catch (RemoteException e) {
				Notification.notify("Errore", "Server offline");
			}
			if (bool) {
				lblSurname.setText(surname);
				try {
					client.setSurname(surname);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				Notification.notify("SUCCES", "Surname changed");
			} else {
				Notification.notify("ERROR", "");
			}
			textFieldChangeSurname.setText("");
		}
	}

	public void changeName() {
		String name = textFieldChangeName.getText();
		if (!name.equals("")) {
			boolean bool = false;
			try {
				bool = server.changeName(name, client);
			} catch (RemoteException e) {
				Notification.notify("Errore", "Server offline");
			}
			if (bool) {
				lblName.setText(name);
				try {
					client.setName(name);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				Notification.notify("SUCCES", "Name changed");
			} else {
				Notification.notify("ERROR", "");
			}
			textFieldChangeName.setText("");
		}
	}

	public void downloadPhrases() {
		Thread t = new Thread() {
			public void run() {
				modelPhrases.clear();
				modelThemes.clear();
				modelId.clear();
				List<PhrasesDTO> listPhrasesDTO = null;
				try {
					listPhrasesDTO = server.getAllPhrases();
				} catch (RemoteException e) {
					Notification.notify("ERROR", "");
					System.out.println(e.toString());
				}
				for (int i = 0; i < listPhrasesDTO.size(); i++) {
					modelPhrases.addElement(listPhrasesDTO.get(i).getPhrase());
					modelThemes.addElement(listPhrasesDTO.get(i).getTheme());
					modelId.addElement(listPhrasesDTO.get(i).getId());
				}
			}
		};
		t.start();

	}

	public void uploadPhrases(int id, String theme, String phrase) {
		try {
			server.uploadPhrase(new PhrasesDTO(id, theme, phrase));
			Notification.notify("SUCCESS", "Phrases updated");
		} catch (RemoteException e) {
			Notification.notify("ERROR", "Phrases non uploaded");
		}
	}

	public void createArray() {
		phrasesArray = new String[listPhrases.getModel().getSize()];
		themesArray = new String[listThemes.getModel().getSize()];
		for (int i = 0; i < listPhrases.getModel().getSize(); i++) {
			listPhrases.setSelectedIndex(i);
			listThemes.setSelectedIndex(i);
			phrasesArray[i] = (String) listPhrases.getSelectedValue();
			themesArray[i] = (String) listPhrases.getSelectedValue();
		}
		listPhrases.clearSelection();
		listThemes.clearSelection();
	}

	public void addPhrase() {
		if (btnAdd.getText().equals("ADD")) {
			btnAdd.setText("UPLOAD");
			textFieldPhrase.setText("New Phrase");
			textFieldTheme.setText("New Theme");
			Notification.notify("", "Insert the new phrase with the theme");
		} else if (btnAdd.getText().equals("UPLOAD")) {
			if (textFieldPhrase.getText().equals("New Phrase") || textFieldTheme.getText().equals("New Theme")
					|| textFieldPhrase.getText().equals("") || textFieldTheme.getText().equals("")) {
				Notification.notify("", "You have to change the phrase or the theme");
			} else {
				btnAdd.setText("ADD");
				try {
					server.addPhrase(new PhrasesDTO(textFieldTheme.getText(), textFieldPhrase.getText()));
					refresh();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deletePhrase(int position) {
		try {
			server.deletePhrase(position);
		} catch (RemoteException e) {
			Notification.notify("ERROR", "Phrase not found");
		}
	}
}
