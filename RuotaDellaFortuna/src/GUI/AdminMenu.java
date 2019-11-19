package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class AdminMenu extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminMenu frame = new AdminMenu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AdminMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 380, 328);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnWatchAMatch = new JButton("Watch a Match");
		btnWatchAMatch.setBounds(10, 10, 170, 112);
		contentPane.add(btnWatchAMatch);
		
		JButton btnAddMisteryPhrases = new JButton("Add Mistery Phrases");
		btnAddMisteryPhrases.setBounds(190, 10, 170, 112);
		contentPane.add(btnAddMisteryPhrases);
		
		JButton btnStatistics = new JButton("Statistics");
		btnStatistics.setBounds(10, 132, 170, 112);
		contentPane.add(btnStatistics);
		
		JButton btnAccountSettings = new JButton("Account Settings");
		btnAccountSettings.setBounds(190, 132, 170, 112);
		contentPane.add(btnAccountSettings);
		
		JButton btnExit = new JButton("EXIT");
		btnExit.setBounds(10, 254, 350, 21);
		contentPane.add(btnExit);
	}
}
