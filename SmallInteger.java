import java.util.Arrays;

public class SmallInteger implements Comparable<SmallInteger>{
	private byte nums[]; // Most Significant digit starts at index 0, used as digite store
	private boolean POSITIVE = true; // Determines if num is negative; **EXPERIMENTAL NOT STABLE**
	
	final static SmallInteger ONE = new SmallInteger("1"); //Pre-created variables (Final and do not get motified)
	final static SmallInteger ZERO = new SmallInteger("0");
	
	//CONSTRUCTOR: Takes a string and converts it to a number, doesnt include letters or symbols.
	public SmallInteger(String num) { 
		this.nums = new byte[num.length()];
		for(int i = 0; i < this.nums.length; i++) {
			nums[i]=(byte)(num.charAt(i)-48);
		}
	}
	//CONSTRUCTOR: takes in pre created byte array (must be pre-carried)
	public SmallInteger(byte[] digits) {
		nums=digits;
	}
	//returns number of digits
	public int length() {
		return nums.length;
	}
	public boolean isPositive() {
		return POSITIVE;
	}
	//Returns Scientific Notation starting at most significant digit.
	public String SciNotation() {
    	String coefficient = "";
    	
    	int digitPlace = 0;
    	for(int i = 0; i < nums.length; i++) {
    		if(i>=10) break;
    		coefficient+=nums[i];
    		digitPlace = i;
    	}
    	coefficient = coefficient.substring(0, 1) + "." + coefficient.substring(1);
    	
    	return coefficient + " x 10^" + (nums.length-1);
    }
	//returns number as string (DANGEROUS FOR NUMBERS WITH LENGTH > 1,000)
	public String toString() {
		String text = "";
		if(!POSITIVE) text+="-";
		for(byte b : nums) text += b + "";
		return text;
	}
	//Returns number as an integer, if the length is >10 it will not store as an integer and return 0;
	public Integer toInteger() {
		int temp = 0;
		if(length()>=10 || length()==10 && nums[0]>=2) return null;
		for(int i = 0; i < length(); i++) {
			int placeVal = (int)Math.pow(10, length()-1-i)*nums[i];
			temp += placeVal;
		}
		return temp;
	}

	public byte[] getArray() {
		return this.nums; //returns byte array
	}

	//Subtracts a Number to the Object (Switches between different algorithms)
	public void subtract(SmallInteger g) {
		if(this.compareTo(g)<0) {
			byte[] temps = Arrays.copyOf(g.nums,g.length());
			g.keionSubtract(this);
			this.nums = g.nums;
			g.nums = temps;
			POSITIVE=false;
		}else if(this.compareTo(g)==0){
			this.nums = ZERO.nums;
		}else {
			this.keionSubtract(g);

		}
		
	}
	public int compareTo(SmallInteger o) {
		if(((SmallInteger)o).length()>this.length()) return -1; //this is smaller
		if(((SmallInteger)o).length()<this.length()) return 1;  //this is bigger
		if(((SmallInteger)o).length()==this.length()) {
			int idx = this.length()-1;
			while(idx>=0) {
				if(((SmallInteger)o).nums[idx]>this.nums[idx]) return -1; // this is smaller
				if(((SmallInteger)o).nums[idx]<this.nums[idx]) return 1; // this is bigger
				idx--;
			}
		}
		return 0;
	}
	//BEST ALGORITHM
	//Strategy: Shift, Add now, Carry & Shrink later.
	//NO TEMPORARY ARRAYS ARE USED AT ALL
	public void add(SmallInteger g) {
		// STEPS:
		// 1. FIND GREATER LENGTH AND SHIFTS ARRAY RIGHT FOR ROOM FOR THE CARRY OVER
		// 2. ADD DIGITS TOGETHER STARTING FROM THE END IDX
		// 3. CARRY ALGORITHM MOVES DIGITS IN 10s PLACE TO NEXT IDX

		if(g.length()>this.length()) {
			this.shiftLeft((g.length()-this.length()));
			//skip over size iteration
		}

		int idx = 1;
		if(POSITIVE)
			while(idx <= g.length() ) {
				this.nums[this.length()-idx] += g.nums[g.length()-idx];
				idx++;
			}
		else keionSubtract(g);
		carry(this.length()-1, this.length()-g.length()-2);
		
	}
	
	//Subtraction Algorithms
	private void keionSubtract(SmallInteger g) { 
		// v1 ONLY WORKS WHEN ANSWER ISNT NEGATIVE
		// v2 reverse the pointers if number being subtracted is larger (justify by length or by iteration)
	
		
		if(g.length()>this.length()) {
			this.shiftLeft((g.length()-this.length()));
			//skip over size iteration
		}
		
		
		for(int i = 0; i < g.length(); i++) {
			byte tempVal = (byte) (nums[this.length()-i-1] - g.nums[g.length()-i-1]);
			
			if(nums[this.length()-i-1] - g.nums[g.length()-i-1] < 0) {
				// means that threes a negative carry
				if(this.length()-i-2 < 0 ) {
					nums[this.length() - i-1] = (byte) (tempVal+(byte)1); //leave negative on last num
					return; // no next digit (no carrys)
				}
				
				//leave the new digit as (10+(first-other))
				nums[this.length() - i-1] = (byte) (10 + (tempVal));
				//next digit += ((first-other)/10)-1
				nums[this.length() - i - 2] += tempVal/10-1;
			}
			else {
				nums[this.length() - i-1] = tempVal;
			}
		}
		this.autoShrink();
	}
	
	//Shift Multiply O(n^2)
	//Strategy: Shift, Compare, Carry at Threshold, shrink after.
	//Take Away: algorithm very Inefficient however future iterations will be improved.
	public void muliply(SmallInteger g) {
		byte[] temp = new byte[g.length()+this.length()+1];
	
		for(int i = 0; i < temp.length; i++) temp[i] = 0;
		
		if(this.length()<g.length()) this.shiftLeft(g.length()-this.length()); // shift array if numbers dont have same length
		
		int move = 0;
		for(int i = this.length()-1; i >= 0; i--) { //start at last element of first num
			for(int s = g.length()-1; s >= 0; s--) { // start at last element of second num
				if(temp.length-1-move <= 0) break;
				if(((int)temp[temp.length-1-move] + (int)g.nums[s]*(int)this.nums[i]) > 100 ) carry(temp,temp.length-1-move,0);
				temp[temp.length-1-move] += g.nums[s]*this.nums[i]; // sum downward into temp array
				
				move++; // index of temp array
			}
			move = this.length()-i; // set move's starting point decrementing from the end every iteration
		}
		carry(temp,temp.length-1,0);
		this.nums = temp;
		this.autoShrink();
	}
	//Division Algorithms
	//Best algorithm Ive created so far, uses the binary search concept to find the remainder of a large number
	//Didnt have enough time to implement this however this is a working and tested proof to get all the digits of a number without its 
	
	public int binaryDivide(int a, int b) {
		int max = a;
		int min = 0;
		int r = 0;
		int q = 1;
		if(a==b) return 1;
		
		q= (min+max)/2;
		
		while(min < max) {
			r=a-(q*b);
			if(r==0) return q;
			else if(r>0) min = q+1;
			else max=q;
			q=(min+max)/2;
		}
		if(q==a) return q; 
		return q-1;
	}
	public int binaryDivideInt(SmallInteger a) {
		int max = a.toInteger();
		int min = 0;
		int r = 0;
		int q = 1;
		if(a.compareTo(this)==0) return 1;
		
		q= (min+max)/2;
		
		while(min < max) {
			r=a.toInteger()-(q*this.toInteger());
			if(r==0) return q;
			else if(r>0) min = q+1;
			else max=q;
			q=(min+max)/2;
		}
		return q-1;
	}
	public int recurDivide(int a, int b) {
		if(a < b) return recurDivide(0,a);
		int q=0;
		int r = a;
		while(r>=b) {
			q++;
			r -= b;
		}
		return q;
		
	}
	//Class Methods
	public void carry(int idx, int end) {
		if(idx == 0 || idx == end) return;
		
		if(nums[idx] < 10 && nums[idx] >= 0){
			while(nums[idx] < 10 && nums[idx] >= 0) {
				idx--;
				if(idx < 0) return;
				if(idx-1 <= 0) return;
			}
		}
		if(nums[idx] >= 10){
			nums[idx-1] += (byte) (nums[idx] / 10);
			nums[idx] = (byte) (nums[idx] % 10);
		}else if(nums[idx] < 0) {
			nums[idx]++; 
		}
			
		carry(idx-1, end);
	}
	private void carry(byte[] array, int idx, int end) {
		if(idx == 0 || idx == end) return;
		if(array[idx] < 10 ) {
			while(array[idx] < 10) {
				idx--;
				if(idx < 0) return;
				if(idx-1 <= 0) return;
			}
		}
		if(array[idx] >= 10){
			array[idx-1] += (byte) (array[idx] / 10);
			array[idx] = (byte) (array[idx] % 10);
		}
		carry(array, idx-1, end);
	}
	private void shiftLeft(int num) {
		byte[] temp = new byte[nums.length+num];
		System.arraycopy(nums, 0, temp, num, nums.length);
		nums = temp;
	}
	private void autoShrink() {
		int i = 0;
		while(i < this.length()-1 && nums[i]==0) {
			i++;
		}
		byte[] newArray = new byte[nums.length-i];
		System.arraycopy(nums, i, newArray, 0, nums.length-i);
		this.nums=newArray;
		
	}
	
	
}
