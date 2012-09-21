package utils;

public class Utils {

	public static float power(float f, int p)
	{
		float val = 1;
		while (p > 0)
		{
			val *= f;
			p--;
		}
		
		return val;
	}
	
	public static float square(float f)
	{
		return power(f, 2);
	}
}
