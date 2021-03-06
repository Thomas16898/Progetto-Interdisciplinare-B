package gui;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import java.awt.Font;
import javax.swing.SwingConstants;
import game.RemoteMatch;
import serverRdF.ServerInterface;
import services.AdminChecker;
import services.ClientInterface;
import services.GameBeingPlayed2Render;
import services.MatchData;
import services.Notification;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.awt.event.ActionEvent;

public class GameBeingPlayed extends JDesktopPane{

	private JFrame frame;
	private JLabel lblPlayer;
	private JLabel lblPlayer_1;
	private JLabel lblPlayer_2;
	private JButton btnJoin;
	private JButton btnObserve;

	private ServerInterface server;
	private static ClientInterface client;
	private MatchData matchData;
	private static RemoteMatch match;
	public static boolean player;
	private JButton btnExit;
	private int posX = 0, posY = 0;

	/**
	 * Create the application.
	 * @param server
	 * @param client
	 * @param matchData
	 */
	public GameBeingPlayed(ServerInterface server, ClientInterface client, MatchData matchData) {
		this.server = server;
		this.client = client;
		this.matchData = matchData;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setUndecorated(true);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setBackground(Color.GRAY);
		frame.setVisible(true);
		frame.setBounds(100, 100, 704, 98);
		frame.getContentPane().setLayout(new GridLayout(0, 6, 0, 0));
		

		lblPlayer = new JLabel("Player1");
		lblPlayer.setForeground(Color.WHITE);
		lblPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer.setFont(new Font("Tahoma", Font.BOLD, 15));
		frame.getContentPane().add(lblPlayer);

		lblPlayer_1 = new JLabel("Player 2");
		lblPlayer_1.setForeground(Color.WHITE);
		lblPlayer_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		frame.getContentPane().add(lblPlayer_1);

		lblPlayer_2 = new JLabel("Player3");
		lblPlayer_2.setForeground(Color.WHITE);
		lblPlayer_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer_2.setFont(new Font("Tahoma", Font.BOLD, 15));
		frame.getContentPane().add(lblPlayer_2);

		btnJoin = new JButton("JOIN");
		btnJoin.setForeground(Color.WHITE);
		btnJoin.setBackground(Color.GREEN);
		btnJoin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		frame.getContentPane().add(btnJoin);

		btnObserve = new JButton("OBSERVE");
		btnObserve.setBackground(Color.CYAN);
		btnObserve.setFont(new Font("Tahoma", Font.PLAIN, 18));
		frame.getContentPane().add(btnObserve);
		
		
		
		if (AdminChecker.isIsAdmin())
			btnJoin.setEnabled(false);

		lblPlayer.setText(matchData.getPlayer1());
		lblPlayer_1.setText(matchData.getPlayer2());
		lblPlayer_2.setText(matchData.getPlayer3());
		
		btnExit = new JButton("CLOSE");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				GameBeingPlayed2Render.setChosen(false);
			}
		});
		btnExit.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnExit.setBackground(Color.RED);
		frame.getContentPane().add(btnExit);
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					player = true;
					match = server.joinMatch(client, matchData.getIdMatch());
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				if (match == null) {
					Notification.notify("ERROR", "GAME DOESN'T EXIST");
				} else {
					TabPane.creator = false;
					Game game = new Game(match, client);
					TabPane.setInvisible();
					frame.dispose();

				}
			}
		});

		btnObserve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player = false;
				try {
					match = server.observeMatch(client, matchData.getIdMatch());
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				if (match == null) {
					Notification.notify("ERROR", "GAME DOESN'T EXIST");
				} else {
					TabPane.creator = false;
					Game game = new Game(match, client);
					TabPane.setInvisible();
					frame.dispose();
				}
			}
		});
		
		
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});

		frame.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				frame.setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});
		frame.setLocationRelativeTo(null);

	}

	 private void setAviableLabel(boolean aviable) {
	        if (aviable) {
	            btnJoin.setEnabled(true);
	        } else {
	            btnJoin.setEnabled(false);
	        }
	    }
	
    public static void setGameControllerObserver(Game gpc) {
        gpc.setClient(client);
        gpc.setMatch(match);
        gpc.setObserver(!GameBeingPlayed.player);
    }
}
