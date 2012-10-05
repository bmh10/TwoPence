package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import network.Client;

import utils.Utils;
import utils.Vector2D;



/*
 * Ball class which manages the balls position and motion during the game
 */

public class Coin {
	
	private static final float restitution = 0.8f;
	private static final float powerConst = 0.01f;
	private static final float friction = 0.993f;//0.008f; - for more realistic coin friction (use +/- instead of *)
	private static final float velocityMin = 0.05f;
	private static final Color selColor = Color.RED;


	private Vector2D pos, startPos;
	private Vector2D vel;
	private int xrangemin, xrangemax, yrangemin, yrangemax;
	private int size;
	private Image img;
	public boolean inPlay;
	private Color color, defColor;
	private boolean selected, powerLineVisible;
	private Vector2D mousePos;
	private Game game;
	
	//ip=intersection point
	public Vector2D ip;
	
	BallSmokeParticle b;
	
	/*
	 * Creates a ball at specified position with specified colour
	 */
	public Coin(Game game, Vector2D p, Color c) {
		this.game = game;
		this.pos = p;
		this.startPos = new Vector2D(pos.x, pos.y);
		this.size = 50;
		this.vel = new Vector2D(1, 1);
		this.inPlay = false;
		this.color = this.defColor = c;
		this.selected = this.powerLineVisible = false;
		this.mousePos = new Vector2D(0, 0); 
	}
	
	public Vector2D getPos() {
		return pos;
	}
	
	public void setPos(Vector2D pos) {
		this.pos = pos;
	}
	
	public void setStartPos(Vector2D pos) {
		this.startPos = pos;
	}
	
	public Vector2D getVel() {
		return vel;
	}
	
	public void setVel(Vector2D vel) {
		this.vel = vel;
	}
	
	public int getSize() {
		return size;
	}
	
	private float getRadius()
	{
		return size/2;
	}
	
	private float getMass()
	{
		return 1;
	}
	
	public Coin setImage(Image i) {
		this.img = i;
		return this;
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean powerLineDrawn() {
		return this.powerLineVisible;
	}
	
	public void setPowerLineDrawn(boolean powerLineVisible, Vector2D mousePos) {
		this.powerLineVisible = powerLineVisible;
		this.mousePos = mousePos;
	}
	
	public void startPlay() {
		// If game already in progress cannot start new game
//		if(inPlay)
//			return;
		
		// Otherwise initialise new game round (keeps old scores)
		inPlay = true;
		
		// Make random start movement
		vel.x = 0; //(Math.random()<0.5) ? -1 : 1; 
		vel.y = 0; //(Math.random()<0.5) ? -1 : 1;
		pos = new Vector2D(startPos.x, startPos.y); //?? this mat not work-> new Point(startPos.x, startPos.y) ??
		this.setSelected(false);
	}
	
	/*
	 * 
	 */
	public boolean intersectsLine(Vector2D p1, Vector2D p2)
	{
		float r = getRadius();
		
		//Get shortest dist from center of circle to line
		double dist = pointToLineDistance(p1, p2, this.pos);
		
		
		//Find equation for perpendicular line
		//Handle case where line gradient could end up being infinate (i.e. vertical line)
		float xdiff = (p2.x-p1.x);
		if (xdiff==0)
		{
			//Equation of line is x=p1.x or x=p2.x
			//Also normal gradient=0 (horizontal line)
			//So we can calculate IP directly
			ip = new Vector2D(p1.x, this.pos.y);
		}
		else
		{
			float lineGradient = (p2.y-p1.y) / xdiff;
			if (lineGradient==0)
			{
				//i.e. we have a horizontal line so normal gradient would be infinity
				//Again we can calculate IP directly
				ip = new Vector2D(this.pos.x, p1.y);
			}
			else
			{
				float normalGradient = -1/lineGradient;
				// y intersection for line
				float c1 = p1.y - lineGradient*p1.x;
				// y intersection for perpendicular line
				float c2 = this.pos.y - normalGradient*this.pos.x;
				//Intersection point
				ip = getIntersectionPoint(lineGradient, c1, normalGradient, c2);
			}
		}
		
		//If circle intersects line and more specifically the line segment between
		//the two coins, return true.
		return (dist <= r && isBetween(p1, p2, ip));
	}
	
	/* 
	 * Returns true of the point c is between points a and b on a line
	 */
	private boolean isBetween(Vector2D a, Vector2D b, Vector2D c)
	{
//	    crossproduct = (c.y - a.y) * (b.x - a.x) - (c.x - a.x) * (b.y - a.y)
//	    if abs(crossproduct) > epsilon : return False   # (or != 0 if using integers)

	    float dotproduct = (c.x - a.x) * (b.x - a.x) + (c.y - a.y)*(b.y - a.y);
	    if (dotproduct < 0)
	    	return false;

	    float squaredlengthba = (b.x - a.x)*(b.x - a.x) + (b.y - a.y)*(b.y - a.y);
	    if (dotproduct > squaredlengthba)
	    	return false;

	    return true;
	}
	
	/*
	 *  Finds distance from point P to the line that passes through A-B
	 */
	private double pointToLineDistance(Vector2D A, Vector2D B, Vector2D P)
	{
	 double normalLength = Math.hypot(B.x - A.x, B.y - A.y);
	 return Math.abs((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x)) / normalLength;
	}
	
	/*
	 * Finds intersection point of two lines (m is gradient, c is y-intersect)
	 */
	private Vector2D getIntersectionPoint(float m1, float c1, float m2, float c2)
	{
		float x = (c2-c1)/(m1-m2);
		float y = m1*x + c1;
		
		//assert(y==m2*x + c2);
		
		return new Vector2D(x, y);
	}
	
	private Vector2D getIntersectionPoint(Vector2D p0, Vector2D p1, Vector2D q0, Vector2D q1)
	{
		float xdiff = (p0.x-p1.x);
		if (xdiff == 0)
		{
			//Line equation is x=p1.x
		}
		float m1 = (p0.y-p1.y)/xdiff;
		float c1 = p0.y - m1*p0.x;
		
		float m2 = (q0.y-q1.y)/(q0.x-q1.x);
		float c2 = q0.y - m1*q0.x;
		
		return getIntersectionPoint(m1, c1, m2, c2);
		
	}
	
	/*
	 * Checks if this coin has been clicked.
	 * Returns true if coin has been clicked.
	 */
	public boolean checkClicked(Vector2D mousePos)
	{
		boolean clicked = false;
		float sqrdist = Utils.square(pos.x-mousePos.x) + Utils.square(pos.y-mousePos.y);
		
		//Check is mouse pos is inside coin when clicked
		clicked = sqrdist <= Utils.square(getRadius());
		
		//this.mousePos = mousePos;
		return clicked;
	}
	
	/*
	 * Updates mouse position as it is being dragged so power line can be drawn,
	 * and direction and velocity of shot can be calculated.
	 */
	public void updatePowerLine(Vector2D mousePos)
	{
			this.mousePos = mousePos;
	}
	
	public void makeShot()
	{
		Vector2D d = new Vector2D(pos.x-mousePos.x, pos.y-mousePos.y);
		this.vel.set(d.multiply(powerConst));
		//selected = false;
		powerLineVisible = false;
		//TODO: If in multiplayer game send vel to server so opponents screen can replicate
		if (game.gameType == Game.ONLINE_MULTI)
		{
			Client.sendVelocity(d);
		}
	}
	
	//TODO: get velocity in db and 
	public void makeOpponentShot()
	{
//		Vector2D d = Client.getVelocity();//  new Vector2D(pos.x-mousePos.x, pos.y-mousePos.y);
//		this.vel.set(d.multiply(powerConst));
	}
	
	public void move(Game game) {
		// If game not being played cannot move
		if(!inPlay)
			return;
		// Move ball
		this.setPos(pos.add(vel));
		
		 //Apply friction
		if (!vel.isZero())
		{
			if (Math.abs(vel.x) < velocityMin)
				vel.x = 0;
			else
				vel.x *= friction;

			
			if (Math.abs(vel.y) < velocityMin)
				vel.y = 0;
			else
				vel.y *= friction;
		}
		
		//If velocity is tiny, stop moving
//		if (vel.getLength() < velocityMin)
//		{
//			vel.setZero();
//		}
		
		// Check that ball is within playing area and adjust, also monitor when points scored, also check orientation of game
		if(pos.y < yrangemin) {
				// Bounce off top wall
				pos.y = yrangemin;
				vel.x *= restitution;
				vel.y = -vel.y * restitution;
		}
		
		if(pos.y > yrangemax) {
				// Bounce off bottom wall
				pos.y = yrangemax;
				vel.x *= restitution;
				vel.y = -vel.y * restitution;
		}
		
		if(pos.x < xrangemin) {
				// Bounce off left wall
				pos.x = xrangemin;
				vel.x = -vel.x * restitution;
				vel.y *= restitution;
		}
		
		if(pos.x > xrangemax) {
				// Bounce off right wall
				pos.x = xrangemax;
				vel.x = -vel.x * restitution;
				vel.y *= restitution;
		}
	}

	/*
	 * Checks if this coin is colliding with another coin
	 */
	public boolean colliding(Coin coin)
	{
	    float xd = (pos.x - coin.getPos().x);
	    float yd = (pos.y - coin.getPos().y);

	    float sumRadius = getRadius() + coin.getRadius();
	    float sqrSumRadius = sumRadius * sumRadius;

	    float distSqr = (xd * xd) + (yd * yd);

	    if (distSqr <= sqrSumRadius)
	    {
	        return true;
	    }

	    return false;
	}

	
	/*
	 * Resolves collision between 2 coins if they are colliding
	 */
	public void resolveCollision(Coin coin)
	{
	    // get the mtd
	    Vector2D delta = (pos.subtract(coin.getPos()));
	    float d = (delta.getLength());
	    // minimum translation distance to push balls apart after intersecting
	    Vector2D mtd = delta.multiply(((getRadius() + coin.getRadius())-d)/d); 


	    // resolve intersection --
	    // inverse mass quantities
	    float im1 = 1 / getMass(); 
	    float im2 = 1 / coin.getMass();
	    
	    // push-pull them apart based off their mass
	    this.setPos(this.pos.add(mtd.multiply(im1 / (im1 + im2))));
	    coin.setPos(coin.getPos().subtract(mtd.multiply(im2 / (im1 + im2))));
	    
	    // impact speed
	    Vector2D v = (this.vel.subtract(coin.vel));
	    float vn = v.dot(mtd.normalize());

	    // sphere intersecting but moving away from each other already
	    if (vn > 0.0f) return;

	    // collision impulse
	    float i = (-(1.0f + restitution) * vn) / (im1 + im2);
	    Vector2D impulse = mtd.multiply(i);

	    // change in momentum
	    this.setVel(this.vel.add(impulse.multiply(im1)));
	    coin.setVel(coin.getVel().subtract(impulse.multiply(im2)));

	}
	
	/*
	 * Checks if this coin is colliding with rectangle
	 */
	public boolean colliding(Rectangle rect)
	{
	return false;
//		return (rect.contains(pos.x-getRadius(), pos.y) ||
//				rect.contains(pos.x+getRadius(), pos.y) ||
//				rect.contains(pos.x, pos.y-getRadius()) ||
//				rect.contains(pos.x, pos.y+getRadius()));
		
//		Vector2D p1, p2;
//		if (rect.x==0)
//		{
////			p1 = new Vector2D(rect.x+rect.width, rect.y);
////			p2 = new Vector2D(rect.x+rect.width, rect.y+rect.height);
//			return (rect.contains(pos.x-getRadius(), pos.y));
//		}
//		else
//		{
////			p1 = new Vector2D(rect.x, rect.y);
////			p2 = new Vector2D(rect.x, rect.y+rect.height);
//			return (rect.contains(pos.x+getRadius(), pos.y));
//		}
		
//		return (pointToLineDistance(p1, p2, this.pos) <= getRadius());
			
		//return rect.contains(pos.x-getRadius(), pos.y);
	}
	/*
	 * Resolves collision between coin and rectangle
	 */
	public void resolveCollision(Rectangle rect)
	{
		if  (rect.contains(pos.x-getRadius(), pos.y))
		{
			pos.x = rect.x+rect.width+getRadius()+1;
			vel.x = -vel.x * restitution;
			vel.y *= restitution;
		}
		else if (rect.contains(pos.x+getRadius(), pos.y))
		{
			pos.x = rect.x-getRadius()-1;
			vel.x = -vel.x * restitution;
			vel.y *= restitution;
		}
		else if (rect.contains(pos.x, pos.y-getRadius()))
		{
			pos.y = rect.y+rect.height+getRadius()+1;
			vel.y = -vel.y * restitution;
			vel.x *= restitution;
		}
		else if (rect.contains(pos.x, pos.y+getRadius()))
		{
			pos.y = rect.y-getRadius()-1;
			vel.y = -vel.y * restitution;
			vel.x *= restitution;
		}
	}
	
	/*
	 * Sets walls with ball is to be contained within
	 */
	public void setRange(int xmin, int xmax, int ymin, int ymax) {
		xrangemin = xmin+size/2;
		xrangemax = xmax-size/2;
		yrangemin = ymin+size/2;
		yrangemax = ymax-size/2;
	}
	
	/*
	 * Sets walls with ball is to be contained within
	 */
	public void setGoalPosts(Vector2D postLT, Vector2D postRT, int goalWidth) {
		
	}
	
	/*
	 * Change ball colour while game is paused then change colour back when game is resumed
	 */
	public void flash() {
		if (this.color == Color.YELLOW)
			this.color = Color.RED;
		else
			this.color = Color.YELLOW;
	}
	
	/*
	 * Draws the ball to screen
	 */
	public void draw(Graphics g) {
		if(!inPlay)
			return;
		
		color = (selected) ? selColor : defColor;
		g.setColor(color);
//		g.fillOval((int)pos.x-size/2, (int)pos.y-size/2, size, size);
		if (img!=null)
		{
			g.drawImage(img, (int)(pos.x-getRadius()), (int)(pos.y-getRadius()), null);
		}
		
		if (selected && powerLineVisible)
		{
			double dist = Math.sqrt(Utils.power(pos.x-mousePos.x, 2) + Utils.power(pos.y-mousePos.y, 2));
			float r,gr,b;
			r = (float)dist/500;
			gr = (float)(1.0f-dist/500);
			b = 0.0f;
			if (gr < 0 || r > 1)
			{
				r  = 1.0f;
				gr = 0.0f;
			}
			Color c = new Color (r, gr, b);
			g.setColor(c);
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g2.drawLine((int)pos.x, (int)pos.y, (int) mousePos.x, (int) mousePos.y);
			
			//drawPathProjection(g2);
		}
			
	}
	
	private void drawPathProjection(Graphics2D g)
	{
		Vector2D tl = new Vector2D(xrangemin, yrangemin);
		Vector2D bl = new Vector2D(xrangemin, yrangemax);
		Vector2D tr = new Vector2D(xrangemax, yrangemin);
		Vector2D br = new Vector2D(xrangemax, yrangemax);
		
		
//		float grad = (pos.y-mousePos.y)/(pos.x-mousePos.x);
//		float c = pos.y - grad*pos.x;

		//Left side
		Vector2D l = getIntersectionPoint(tl, bl, pos, mousePos);
		//Right side
		Vector2D r = getIntersectionPoint(tr, br, pos, mousePos);
		//Top side
		Vector2D t = getIntersectionPoint(tl, tr, pos, mousePos);
		//Bottom side
		Vector2D b = getIntersectionPoint(bl, br, pos, mousePos);
		
		Vector2D a = null;
		if (l.x==xrangemin)
			a = l;
		else if (r.x==xrangemax)
			a = r;
		else if (t.y==yrangemin)
			a = t;
		else if (b.y==yrangemax)
			a = b;
			
		//TODO: move elsewhere
		final float dash1[] = {10.0f};
    	final BasicStroke dashed =
	        new BasicStroke(2.0f,
	                        BasicStroke.CAP_BUTT,
	                        BasicStroke.JOIN_MITER,
	                        10.0f, dash1, 0.0f);
		g.setStroke(dashed);
		
		g.drawLine((int)pos.x, (int)pos.y, (int) a.x, (int) a.y);
	}
}
