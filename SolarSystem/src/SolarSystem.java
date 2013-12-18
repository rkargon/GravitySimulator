import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SolarSystem extends JPanel {
	private final int TIMER_DELAY = 20;

	private int nplanets = 900;
	private int maxvelocity = 0;

	private Timer timer;
	private ArrayList<Planet> planets;

	public SolarSystem() {
		super();

		this.setBackground(Color.BLACK);
		planets = new ArrayList<Planet>(nplanets);

		timer = new Timer(TIMER_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveplanets();
				repaint();
			}
		});
	}

	public void reset() {
		for (int i = 1; i <= nplanets; i++) {
			double mass = Math.random() * 100 + 1;
			double r = Math.sqrt(mass);
			double x = Math.random() * (getWidth() - 2 * r) + r;
			double y = Math.random() * (getHeight() - 2 * r) + r;
			double vx = Math.random() * maxvelocity * 2.0 - maxvelocity;
			double vy = Math.random() * maxvelocity * 2.0 - maxvelocity;

			planets.add(new Planet(mass, vx, vy, x, y));
		}

		timer.restart();
	}

	public void moveplanets() {
		Planet p = null, p2 = null, tmp = null;
		double drsquared, dx, dy, r1, r2;

		for (int i = 0; i < planets.size(); i++) {
			p = planets.get(i);
			r1 = p.getRadius();

			for (int j = 0; j < planets.size(); j++) {
				if (i != j) {
					p2 = planets.get(j);
					r2 = p2.getRadius();

					dx = p.x - p2.x;
					dy = p.y - p2.y;
					drsquared = dx * dx + dy * dy;

					// when worlds collide...
					if (drsquared < r1 * r1 + r2 * r2) {
						double vx_new, vy_new;

						// determine new velocity, based on relative masses
						// of
						// planets
						vx_new = (p.vx * p.mass + p2.vx * p2.mass)
								/ (p.mass + p2.mass);
						vy_new = (p.vy * p.mass + p2.vy * p2.mass)
								/ (p.mass + p2.mass);

						p.mass += p2.mass;
						p.vx = vx_new;
						p.vy = vy_new;
						planets.remove(j);
						j--;
						continue;
					}

					p.accelerateTowardsPlanet(planets.get(j), TIMER_DELAY);
				}
			}

			if (p.x - r1 < 0) {
				p.vx = -p.vx;
				p.x = r1 + 1;
			} else if (p.x + r1 > getWidth()) {
				p.vx = -p.vx;
				p.x = getWidth() - r1 - 1;
			}

			if (p.y - r1 < 0) {
				p.vy = -p.vy;
				p.y = r1 + 1;
			} else if (p.y + r1 > getHeight()) {
				p.vy = -p.vy;
				p.y = getHeight() - r1 - 1;
			}

		}
		for (int i = 0; i < planets.size(); i++) {
			planets.get(i).move();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Planet p = null;
		double r;

		g.setColor(Color.WHITE);

		for (int i = 0; i < planets.size(); i++) {
			p = planets.get(i);
			r = p.getRadius();
			g.fillOval((int) (p.x - r), (int) (p.y - r), (int) (r * 2),
					(int) (r * 2));
		}
	}
}
