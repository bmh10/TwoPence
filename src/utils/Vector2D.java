package utils;

public class Vector2D{

	public float x;
	public float y;

	public Vector2D()
	{
		this.setX(0);
		this.setY(0);
	}

	public Vector2D(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}
	
	public boolean isZero() {
		return (x==0) && (y==0);
	}
	
	public void setZero() {
		x=0;
		y=0;
	}
	
	public void set(Vector2D v)
	{
		this.setX(v.x);
		this.setY(v.y);
	}

	public void set(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}


	public float dot(Vector2D v2)
	{
		float result = 0.0f;
		result = this.getX() * v2.getX() + this.getY() * v2.getY();
		return result;
	}

	public float getLength()
	{
		return (float)Math.sqrt(getX()*getX() + getY()*getY());
	}

	public float getDistance(Vector2D v2)
	{
		return (float) Math.sqrt((v2.getX() - getX()) * (v2.getX() - getX()) + (v2.getY() - getY()) * (v2.getY() - getY()));
	}


	public Vector2D add(Vector2D v2)
	{
		Vector2D result = new Vector2D();
		result.setX(getX() + v2.getX());
		result.setY(getY() + v2.getY());
		return result;
	}

	public Vector2D subtract(Vector2D v2)
	{
		Vector2D result = new Vector2D();
		result.setX(this.getX() - v2.getX());
		result.setY(this.getY() - v2.getY());
		return result;
	}

	public Vector2D multiply(float scaleFactor)
	{
		Vector2D result = new Vector2D();
		result.setX(this.getX() * scaleFactor);
		result.setY(this.getY() * scaleFactor);
		return result;
	}

	public Vector2D normalize()
	{
		float len = getLength();
		if (len != 0.0f)
		{
			this.setX(this.getX() / len);
			this.setY(this.getY() / len);
		}
		else
		{
			this.setX(0.0f);
			this.setY(0.0f);
		}

		return this;
	}

	public String toString()
	{
		return "X: " + getX() + " Y: " + getY();
	}


}
