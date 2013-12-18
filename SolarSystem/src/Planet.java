/**
 * 
 * 
 * @author raphaelkargon
 *
 */
public class Planet {
	// G = 6.672E-11, but mass is stored in units of 10E9kg.
	public static final double G = 6.672;
	
	public double mass = 1;
	
	public double vx = 1;
	public double vy = 1;
	public double x = 0;
	public double y = 0;

	public Planet() {
		super();
	}

	public Planet(double mass, double vx, double vy, double x, double y) {
		super();
		this.mass = mass;
		this.vx = vx;
		this.vy = vy;
		this.x = x;
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Planet [mass=" + mass + ", vx=" + vx + ", vy=" + vy + "]";
	}
	
	public double getRadius()
	{
		double r = Math.pow(mass/Math.PI, 1.0/3.0);
		
		//returns at least radius of 0.5, i.e. 1 pixel across
		return (r>=0.5) ? r : 0.5;
	}
	
	public void move(){
		x+=vx;
		y+=vy;
	}
	
	/**
	 * moves object according to inherent velocity for a certain time, given in milliseconds
	 * @param ms
	 */
	public void move(double ms){
		x+=(vx*(ms/1000));
		y+=(vy*(ms/1000));
	}
	
	/**
	 * @param ax
	 * @param ay
	 */
	public void accelerate(double ax, double ay){
		vx+=ax;
		vy+=ay;
	}
	
	/**
	 * Accelerates object at given acceleration, but for a certain time, given in milliseconds
	 * 
	 * @param ax
	 * @param ay
	 * @param ms
	 */
	public void accelerate(double ax, double ay, double ms)
	{
		accelerate(ax*(ms/1000), ay*(ms/1000));
	}
	
	/**
	 * Accelerates towards a planet, for a given number of milliseconds
	 * @param p2
	 */
	public void accelerateTowardsPlanet(Planet p2, double ms){
		double ay, ax;
		double dx = (p2.x-x), dy = (p2.y-y);
		double rsquared = dx*dx + dy*dy;
		double r = Math.sqrt(rsquared);
	
		//if planets coincide, do nothing for the moment
		//TODO: WHAT TO DO WHEN PLANETS COINCIDE?
		if(rsquared==0) return;
		
		//force in direction of planet
		double acceleration = G*p2.mass/rsquared;
		ax = dx*acceleration/r;
		ay = dy*acceleration/r;
		
		accelerate(ax, ay, ms);
	}
}
