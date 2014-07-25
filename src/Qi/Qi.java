package Qi;

import javax.swing.JFrame;

public class Qi {
	public static void main(String args[])
	{
		JFrame window = new JFrame("Qi");
		window.setContentPane(new QiPanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
}
