package com.raphaelkargon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SolarSystem extends JPanel {
	private final int TIMER_DELAY = 16;

	private int nplanets = 1000;
	private int current_planets = nplanets;// current number of planets to
											// display on next refresh;

	private double maxvelocity = 3;

	private Timer timer;
	private ArrayList<Planet> planets;

	// values for new planets added by user
	private double np_mass, np_x, np_y, np_vx, np_vy;
	private boolean is_adding_planet, is_setting_velocity;

	// for displaying planet info when user selects a planet.
	private JLabel description;
	private Planet described_planet;

	private boolean is_walled = false;;

	public SolarSystem() {
		super();

		this.setBackground(Color.BLACK);
		this.addKeyListener(new SolarSystemKeyAdapter());
		this.addMouseListener(new SolarSystemMouseAdapter());
		this.addMouseMotionListener(new SolarSystemMouseMotionAdapter());

		planets = new ArrayList<Planet>(nplanets);

		timer = new Timer(TIMER_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveplanets();
				repaint();
			}
		});

		description = new JLabel();
		description.setForeground(Color.WHITE);
		description.setVisible(true);
		this.add(description);
	}

	public void reset() {
		timer.stop();
		described_planet = null;

		planets = new ArrayList<Planet>(current_planets);
		for (int i = 1; i <= current_planets; i++) {
			double mass = Math.random() * 100 + 1;
			double x = Math.random() * getWidth();
			double y = Math.random() * getHeight();
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

					// when worlds collide... (said George Pal to his bride...)
					if (drsquared < (r1 + r2) * (r1 + r2)) {

						double vx_new, vy_new, x_new, y_new;

						// determine new velocity & location, based on relative
						// masses of planets
						x_new = (p.x * p.mass + p2.x * p2.mass)
								/ (p.mass + p2.mass);
						y_new = (p.y * p.mass + p2.y * p2.mass)
								/ (p.mass + p2.mass);

						vx_new = (p.vx * p.mass + p2.vx * p2.mass)
								/ (p.mass + p2.mass);
						vy_new = (p.vy * p.mass + p2.vy * p2.mass)
								/ (p.mass + p2.mass);

						p.mass += p2.mass;

						p.x = x_new;
						p.y = y_new;
						p.vx = vx_new;
						p.vy = vy_new;
						r1 = p.getRadius();// update radius with new mass
						planets.remove(j); // delete other planet

						// update indices when planet is deleted
						j--;
						if (i > j)
							i--;

						// planet that was removed was selected
						if (described_planet == p2)
							described_planet = p;

						continue;
					}

					p.accelerateTowardsPlanet(planets.get(j), TIMER_DELAY);
				}
			}

			if (is_walled) {
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

		if (is_adding_planet) {
			r = Planet.getRadius(np_mass);
			g.drawOval((int) (np_x - r), (int) (np_y - r), (int) (r * 2),
					(int) (r * 2));
			if (is_setting_velocity)
				g.drawLine((int) np_x, (int) np_y, (int) (np_x + np_vx),
						(int) (np_y + np_vy));
		}
		description.setVisible(false);
		if (described_planet != null) {

			description.setText("Mass: "
					+ String.format("%.2f", described_planet.mass) + "\n"
					+ "Position X: "
					+ String.format("%.2f", described_planet.x) + "\n"
					+ "Position Y: "
					+ String.format("%.2f", described_planet.y) + "\n"
					+ "Velocity X:"
					+ String.format("%.3f", described_planet.vx) + "\n"
					+ "Velocity Y:"
					+ String.format("%.3f", described_planet.vy) + "\n");
			description.setVisible(true);

			g.setColor(Color.CYAN);
			r = described_planet.getRadius();
			g.drawOval((int) (described_planet.x - r - 2),
					(int) (described_planet.y - r - 2), (int) (r * 2) + 4,
					(int) (r * 2) + 4);

			g.setColor(Color.WHITE);

			description.setVisible(true);
		}

		// if planet is being added, override description text with new planet
		// info
		if (is_adding_planet) {

			description.setText("Mass: " + String.format("%.2f", np_mass)
					+ "\n" + "Position X: " + String.format("%.2f", np_x)
					+ "\n" + "Position Y: " + String.format("%.2f", np_y)
					+ "\n" + "Velocity X:" + String.format("%.3f", np_vx)
					+ "\n" + "Velocity Y:" + String.format("%.3f", np_vy)
					+ "\n");

			description.setVisible(true);
		}

		for (int i = 0; i < planets.size(); i++) {
			p = planets.get(i);
			r = p.getRadius();

			if (p.mass > 0)
				g.fillOval((int) (p.x - r), (int) (p.y - r), (int) (r * 2),
						(int) (r * 2));
			else
				g.drawOval((int) (p.x - r), (int) (p.y - r), (int) (r * 2),
						(int) (r * 2));
		}
	}

	class SolarSystemMouseAdapter extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {

			// user ctrl-clicks on a planet to view info
			if (e.isShiftDown()) {
				described_planet = null;
				for (int i = 0; i < planets.size(); i++) {
					Planet p = planets.get(i);
					double r = p.getRadius();
					if (r * r >= (e.getX() - p.x) * (e.getX() - p.x)
							+ (e.getY() - p.y) * (e.getY() - p.y)) {
						described_planet = p;
					}
				}
				repaint();
			}if (is_setting_velocity) {
				is_setting_velocity = is_adding_planet = false;
				planets.add(new Planet(np_mass, 0, 0, np_x, np_y));
			}
			repaint();
		}

		public void mousePressed(MouseEvent e) {
			if (!is_adding_planet) {
				is_adding_planet = true;
				np_mass = 0;
				np_x = e.getX();
				np_y = e.getY();
				np_vx = 0;
				np_vy = 0;
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (is_adding_planet) {
				if (is_setting_velocity) {
					is_setting_velocity = is_adding_planet=false;
					planets.add(new Planet(np_mass, np_vx, np_vy, np_x, np_y));
					repaint();
				} else {
					//automatically create circular orbit to nearest planet (assumes only two planets in system, won't always work)
					if (e.isControlDown() && planets.size()!=0) {

						Planet nearest = null, tmp;
						double min_dist = -1, dist_squared, v_tot;

						// find nearest planet
						for (int i = 0; i < planets.size(); i++) {
							tmp = planets.get(i);
							//calculating r^2 is faster, take sqrt later
							dist_squared = (tmp.x - np_x) * (tmp.x - np_x)
									+ (tmp.y - np_y) * (tmp.y - np_y);
							if (dist_squared < min_dist
									|| min_dist < 0) {
								min_dist = dist_squared;
								nearest = tmp;
							}
						}
						min_dist = Math.sqrt(min_dist);
						
						v_tot = Math.sqrt(Planet.G*(np_mass + nearest.mass)/min_dist);
						v_tot *= 0.1;
						np_vx = v_tot * (np_y-nearest.y)/min_dist + nearest.vx;
						np_vy = -v_tot * (np_x-nearest.x)/min_dist + nearest.vy;
						System.out.println(v_tot);
						is_adding_planet = false;
						planets.add(new Planet(np_mass, np_vx, np_vy, np_x,
								np_y));
					} else
						is_setting_velocity = true;
				}
			}

		}

	}

	class SolarSystemMouseMotionAdapter extends MouseMotionAdapter {

		public void mouseDragged(MouseEvent e) {
			if (is_setting_velocity) {
				np_vx = (e.getX() - np_x);
				np_vy = (e.getY() - np_y);
			} else
				np_mass = (e.getX() - np_x) * (e.getX() - np_x)
						+ (e.getY() - np_y) * (e.getY() - np_y);

			if (e.isShiftDown())
				np_mass = -np_mass; // aw yess, negative mass!

			repaint();
		}
	}

	class SolarSystemKeyAdapter extends KeyAdapter {

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (timer.isRunning()) {
					System.out.println("Simulation stopped.");
					timer.stop();
				} else {
					System.out.println("Simulation started");
					timer.start();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				current_planets = e.isShiftDown() ? 0 : nplanets;
				System.out.println("Simulation reset with " + current_planets
						+ " planets.");
				reset();
			} else if (e.getKeyCode() == KeyEvent.VK_W) {
				is_walled = e.isShiftDown() ? true : !is_walled;
				System.out.println("Walls turned " + (is_walled ? "on" : "off")
						+ ".");
			}

			else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
					|| e.getKeyCode() == KeyEvent.VK_DELETE) {
				if (described_planet != null) {
					planets.remove(described_planet);
					System.out.println("Object " + described_planet.toString()
							+ " removed.");
					described_planet = null;
					repaint();
				}
			}
		}
	}
}
