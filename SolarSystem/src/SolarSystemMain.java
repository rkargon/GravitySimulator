import java.awt.Container;

import javax.swing.JFrame;


public class SolarSystemMain {

	public static void main(String[] args) {
		JFrame win = new JFrame();
		win.setSize(1600, 800);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container con = win.getContentPane();
		SolarSystem s = new SolarSystem();
		con.add(s);
		
		//set up keyboard focus
		s.setFocusable(true);
		s.requestFocusInWindow();
		
		win.setVisible(true);
		s.reset();
	}

}
