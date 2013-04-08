package pitch_detection;

public class Pair<L,R> {
	public L left;
	public R right;
	
	public Pair(L _left, R _right){
		left = _left;
		right = _right;
	}
	@Override
	public boolean equals(Object that){
		if(that == null || !(that instanceof Pair)){
			return false;
		}
		Pair pThat = (Pair) that;
		return this.left.equals(pThat.left) && this.right.equals(pThat.right);
	}
}
