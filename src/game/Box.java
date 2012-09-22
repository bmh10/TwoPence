package game;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import utils.Vector2D;

public class Box {

	public static final int size = 150;
	public static final int subSize = 50;
	public final int ANIM_SPEED = 2;
	
	private Rectangle box;
	private String text;
	private Color clBox, clText;
	private Vector2D finalPos;
	private int finalW, finalH;
	private boolean visible;
	
	public Box()
	{
		this.box = new Rectangle(0, 0, size, size);
		this.finalPos = new Vector2D(0, 0);
		this.finalW = this.finalH = size;
		this.text = "";
		this.clBox = Color.GRAY;
		this.clText = Color.WHITE;
		this.visible = true;
	}
	
	public Box(Vector2D pos, String text)
	{
		this.box = new Rectangle((int)pos.x, (int)pos.y, size, size);
		this.finalPos = new Vector2D(box.x, box.y);
		this.finalW = this.finalH = size;
		this.text = text;
		this.clBox = Color.GRAY;
		this.clText = Color.WHITE;
		this.visible = true;
	}
	
	public Rectangle getRect()
	{
		return box;
	}
	
	public String getText()
	{
		return text;
	}
	
	public boolean hasNoText()
	{
		return text.equals("");
	}
	
	public Box setText(String t)
	{
		this.text = t;
		return this;
	}
	
	public Box setColor(Color cb, Color ct)
	{
		this.clBox = cb;
		this.clText = ct;
		return this;
	}
	
	public Box setSize(Vector2D s)
	{
		this.box.width  = (int) s.x;
		this.box.height = (int) s.y;
		return this;
	}
	
		
	public Box setVisible(boolean b)
	{
		this.visible = b;
		return this;
	}
	
	public Box setFinalSize(int w, int h)
	{
		this.finalW = w;
		this.finalH = h;
		return this;
	}
	
	public Box setPos(Vector2D v)
	{
		this.box.x = (int) v.x;
		this.box.y = (int) v.y;
		return this;
	}
	
	public Box setFinalPos(Vector2D v)
	{
		this.finalPos = v;
		return this;
	}
	

	
	/*
	 * Moves the box a step toward its target position
	 */
	public boolean animate()
	{
		//Adjust position
		if (box.x < finalPos.x) box.x+=ANIM_SPEED;
		else if (box.x > finalPos.x) box.x-=ANIM_SPEED;
		if (box.y < finalPos.y) box.y+=ANIM_SPEED;
		else if (box.y > finalPos.y) box.y-=ANIM_SPEED;
		
		//Adjust dimensions (when in position)
		if (true) //box.x==finalPos.x && box.y==finalPos.y)
		{
			if (box.width < finalW) box.width = finalW;//+=ANIM_SPEED;
			else if (box.width > finalW) box.width= finalW;//-=ANIM_SPEED;
			if (box.height < finalH) box.height= finalW;//+=ANIM_SPEED;
			else if (box.height > finalH) box.height= finalW;//-=ANIM_SPEED;
			
			//return (box.width==finalW && box.height==finalH);
		}
		return (box.x==finalPos.x && box.y==finalPos.y);
		//return false;
	}
	
	
	public Box draw(Graphics g)
	{
		if (visible)
		{
			g.setColor(clBox);
			g.fill3DRect(box.x, box.y, box.width, box.height, true);
			FontMetrics fm = g.getFontMetrics();
			g.setColor(clText);
			//If animation complete draw text
			if (animate())
			{
				g.drawString(text, box.x+(box.width - fm.stringWidth(text))/2, box.y+(box.height)/2);
			}
		}
		
		return this;
	}
	
}
