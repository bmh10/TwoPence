package game;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import utils.Vector2D;

public class Box {

	public static final int size = 152;
	public static final int iconSize = 43;
	public static final int subSizeW = 150;
	public static final int subSizeH = 20;
	public final int ANIM_SPEED = 2;
	
	private Rectangle box;
	private String text;
	private Image img, img1;
	private Color clBox, clText;
	private Vector2D finalPos;
	private int finalW, finalH;
	private boolean visible, imgVisible;
	private boolean checked, checkbox;
	boolean onlyShowText;
	
	public Box()
	{
		this.box = new Rectangle(0, 0, size, size);
		this.finalPos = new Vector2D(0, 0);
		this.finalW = this.finalH = size;
		this.text = "";
		this.clBox = Color.GRAY;
		this.clText = Color.WHITE;
		this.visible = this.imgVisible = true;
		this.checked = this.checkbox = false;
		this.onlyShowText = false;
	}
	
	public Box(Vector2D pos, String text)
	{
		this.box = new Rectangle((int)pos.x, (int)pos.y, size, size);
		this.finalPos = new Vector2D(box.x, box.y);
		this.finalW = this.finalH = size;
		this.text = text;
		this.clBox = Color.GRAY;
		this.clText = Color.WHITE;
		this.visible = this.imgVisible = true;
		this.checked = this.checkbox = false;
		this.onlyShowText = false;
	}
	
	public Box reset()
	{
		this.box = new Rectangle(0, 0, size, size);
		this.finalPos = new Vector2D(0, 0);
		this.finalW = this.finalH = size;
		this.text = "";
		this.clBox = Color.GRAY;
		this.clText = Color.WHITE;
		this.visible = this.imgVisible = false;
		this.checked = this.checkbox = false;
		this.onlyShowText = false;
		return this;
	}
	
	public Rectangle getRect()
	{
		return box;
	}
	
	public String getText()
	{
		return text;
	}
	
	public Point getPos()
	{
		return new Point(box.x, box.y);
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
	
	public Box setImage(Image i)
	{
		this.img = i;
		return this;
	}
	
	public Box setImageVisible(boolean b)
	{
		this.imgVisible = b;
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
	
	public Box setSizeBoth(int w, int h)
	{
		this.box.width = finalW = w;
		this.box.height = finalH = h;
		return this;
	}
	
	public Box setPosBoth(Vector2D v)
	{
		this.box.x = (int) v.x;
		this.box.y = (int) v.y;
		this.finalPos = v;;
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
	
	public Vector2D getFinalPos()
	{
		return finalPos;
	}
	
	public Box setToCheckbox(Image i0, Image i1)
	{
		this.checkbox = true;
		img = i0;
		img1 = i1;
		return this;
	}
	
	public Box invertChecked()
	{
		this.checked = !checked;
		return this;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public Box onlyShowText(boolean b)
	{
		this.onlyShowText = b;
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
		if (box.x==finalPos.x && box.y==finalPos.y)
		{
			if (box.width < finalW) box.width +=ANIM_SPEED;
			else if (box.width > finalW) box.width-=ANIM_SPEED;
			if (box.height < finalH) box.height+=ANIM_SPEED;
			else if (box.height > finalH) box.height-=ANIM_SPEED;
			
			return (box.width==finalW && box.height==finalH);
		}
		//return (box.x==finalPos.x && box.y==finalPos.y);
		return false;
	}
	
	
	public Box draw(Graphics g)
	{
		if (visible)
		{
			g.setColor(clBox);
			if (!onlyShowText)
			{
				g.fill3DRect(box.x, box.y, box.width, box.height, true);
			}
			FontMetrics fm = g.getFontMetrics();
			g.setColor(clText);
			//If animation complete draw text
			if (animate())
			{
				g.drawString(text, box.x+(box.width - fm.stringWidth(text))/2, box.y+(box.height)/2+5);
				if (!checkbox)
				{
					if (img!=null && imgVisible)
					{
						g.drawImage(img, box.x+(box.width-img.getWidth(null))/2, box.y+(box.height-img.getHeight(null))/2, null);
					}
				}
				else
				{
					Image i = (checked) ? img1 : img;
					g.drawImage(i, box.x+(box.width-img.getWidth(null))/2, box.y+(box.height-img.getHeight(null))/2, null);
				}
			}
		}
		
		return this;
	}
	
}
