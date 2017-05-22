package saboteur.ai;

public class Maths {
	public static float positiveOrZero(float i){
		return (i>0 ? i : 0);
	}
	
	public static float ifNegativeZeroElseOne(float i){
		return (i<=0 ? 0 : 1);
	}
}
