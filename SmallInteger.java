import java.util.Arrays;

public class SmallInteger extends Timer implements Comparable<SmallInteger>{
    
    public static enum Method {TRADITIONAL,SHIFT,KEION,SLOW}; // WHICH METHOD TO USE DURING MATH OPERATIONS (KEION&SHIFT WORK BEST)
    
    public boolean verbose = false; // MORE DEV VARIABLES, if VERBOSE IS ON, Visuals of the array operations are displayed in console
    public boolean CREATE_TEMPS = true; // CREATE TEMPS: Experimental & dev variable, when disabled, its not stable in multiplication
    
    public static Method Efficiency = Method.SHIFT; // Algorithm to use [SHIFT] is Default
    
	private byte nums[]; // Most Significant digit starts at index 0, used as digite store
	private boolean POSITIVE = true; // Determines if num is negative; **EXPERIMENTAL NOT STABLE**
	
	final static SmallInteger ONE = new SmallInteger("1"); //Pre-created variables (Final and do not get motified)
	final static SmallInteger ZERO = new SmallInteger("0");
	
	public byte[] getArray() {
		return this.nums; //returns byte array
	}
	//CONSTRUCTOR: Takes a string and converts it to a number, doesnt include letters or symbols.
	public SmallInteger(String num) { 
		this.nums = new byte[num.length()];
		for(int i = 0; i < this.nums.length; i++) {
			nums[i]=(byte)(num.charAt(i)-48);
		}
		//System.gc();
	}
	//CONSTRUCTOR: takes in pre created byte array (must be pre-carried)
	public SmallInteger(byte[] digits) {
		nums=digits;
		//System.gc();
	}
	//returns number of digits
	public int length() {
		return nums.length;
	}
	//EXPERIMENTAL***
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
	//Adds a Number to the Object (Switches between different algorithms)
	public void add(SmallInteger g) {
		switch(Efficiency) {
		case TRADITIONAL:
			this.fastAdd(g);
			break;
		case SHIFT:
			this.shiftAdd(g);
			break;
		case KEION:
			this.keionAdd(g);
			break;
		default:
			verbPrint("In progress! Method not usable. Nothing changed.");
			break;
		}
	}
	//Multiplies a Number to the Object (Switches between different algorithms)
	public void multiply(SmallInteger g) {
		switch(Efficiency) {
		case SHIFT:
			this.shiftMultiply(g);
			break;
		case SLOW:
			if(this.toInteger()!=null) {
				this.slowMultiply(g.toInteger());
			}
			break;
		default:
			verbPrint("In progress! Method not usable. Nothing changed.");
			break;
		}
	}
	//Subtracts a Number to the Object (Switches between different algorithms)
	public void subtract(SmallInteger g) {
		switch(Efficiency) {
		case KEION:
			if(this.compareTo(g)<0) {
				if(CREATE_TEMPS) {
					byte[] temps = Arrays.copyOf(g.nums,g.length());
					g.keionSubtract(this);
					this.nums = g.nums;
					g.nums = temps;
					
				}else {
			
					g.keionSubtract(this);
					this.nums = g.nums;
				}
			}else if(this.compareTo(g)==0){
				this.nums = ZERO.nums;
			}else {
				this.keionSubtract(g);
			}
			break;
		default:
			if(this.compareTo(g)<0) {
				if(CREATE_TEMPS) {
					byte[] temps = Arrays.copyOf(g.nums,g.length());
					g.keionSubtract(this);
					this.nums = g.nums;
					g.nums = temps;
					this.POSITIVE = false;
				}else {
			
					g.keionSubtract(this);
					this.nums = g.nums;
				}
			}else if(this.compareTo(g)==0){
				this.nums = ZERO.nums;
			}else {
				this.keionSubtract(g);
			}
			break;
		}
		
	}
	
	//Summing Algorithms 
	
	//KeionAdd (first iteration with automated carry)
	//Strategy: Expand, Add & Carry now, Shrink Later.
	private void keionAdd(SmallInteger numAdded) {
		if(nums.length >= numAdded.nums.length) {
			boolean hasCarry = false;
			for(int i = 0; i < nums.length && numAdded.nums.length-2-i >=0; i++) {
				nums[nums.length-1-i] += numAdded.nums[numAdded.nums.length-1-i];
				if(nums[nums.length-1-i] > 9) hasCarry = true;
				else if(nums.length-1-i != 0) hasCarry = false;
			}
		 
			if(hasCarry && nums[0]>=9) shiftLeft();
			
			//System.gc();
			
			for(int i = 0; i < numAdded.nums.length; i++) {
				//iterate backwards
				if(nums[nums.length-1-i] > 9) {
					nums[nums.length-2-i] += (byte)(nums[nums.length-1-i]/10);
					nums[nums.length-1-i] = (byte)(nums[nums.length-1-i]%10);
					
				}
			}
		}else {//this will take more temp memory as swap is needed TEST THIS
			byte[] tempUGH = numAdded.nums;
			numAdded.nums = nums;
			nums = tempUGH;
			add(numAdded);
		}
	}
	//Experimental and was extremely unstable
	private void fastAdd(SmallInteger g) {
		
		if(nums.length>=g.nums.length) {
			byte carry = 0;
			for(int i = 0; i < nums.length && g.nums.length-1-i >=0; i++) {
				byte digSum = (byte) (carry + nums[nums.length-1-i] + g.nums[g.nums.length-1-i]);
				if(digSum > 9) carry=1;
				else carry = 0;
			}
			if(carry == 1) {
				shiftLeft();
				nums[0]=1;
			}
		}else {//this will take more temp memory as swap is needed TEST THIS
			byte[] tempUGH = g.nums;
			g.nums = nums;
			nums = tempUGH;
			fastAdd(g);
		}
	}
	//BEST ALGORITHM
	//Strategy: Shift, Add now, Carry & Shrink later.
	//NO TEMPORARY ARRAYS ARE USED AT ALL
	private void shiftAdd(SmallInteger g) {
		// STEPS:
		// 1. FIND GREATER LENGTH AND SHIFTS ARRAY RIGHT FOR ROOM FOR THE CARRY OVER
		// 2. ADD DIGITS TOGETHER STARTING FROM THE END IDX
		// 3. CARRY ALGORITHM MOVES DIGITS IN 10s PLACE TO NEXT IDX
		
		/*
		if(g.length() >= this.length()) {
			shiftLeft(g.length() - this.length() + 2);
			verbPrint("Array Shifted. ");
		}
		*/
		int idx = 1;
		
		while(idx <= g.length() ) {
			this.nums[this.length()-idx] += g.nums[g.length()-idx];
			idx++;
			//verbPrint("Adding digit at index " +(this.length()-idx)+ "...");
		}
		
		//carry(this.length()-1, this.length()-g.length()-2);
		
		//verbPrint("Array sum " + (idx-1) + " times");
		
	}
	// Experimental***Stable
	// Rather than affecting object, this (Future) static method will return the sum between two small Integers.
	private SmallInteger shiftAdd(SmallInteger a, SmallInteger b) {
		SmallInteger t = new SmallInteger(Arrays.copyOf(a.nums, numCarry));
		
		//SHIFT
		if(b.length() >= t.length()) 
			this.shiftLeft(b.length() - t.length() + 2);
		int idx = 1;
		//ADD
		while(idx <= b.length() ) {
			t.nums[t.length()-idx] += b.nums[b.length()-idx];
			idx++;
		}
		//CARRY
		t.carry(t.length()-1, t.length()-b.length()-2);
		//RETURN
		return t;
		
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
				verbPrint("["+i + "]: Carry! Digit left as " + nums[this.length()-i-1]);
			}
			else {
				nums[this.length() - i-1] = tempVal;
				verbPrint("["+i + "]: No carry! Digit left as " + tempVal);
			}
		}
		this.autoShrink();
	}
	
	//Multiplication Algorithms
	private void slowMultiply(int i) {
		System.out.println("Multiply");
		SmallInteger temp = new SmallInteger(nums);
		
		while(i > 1) {
			shiftAdd(temp);
			i--;
		}
	}
	
	//Shift Multiply O(n^2)
	//Strategy: Shift, Compare, Carry at Threshold, shrink after.
	//Take Away: algorithm very Inefficient however future iterations will be improved.
	private void shiftMultiply(SmallInteger g) {
		byte[] temp = new byte[g.length()+this.length()+1];
	
		for(int i = 0; i < temp.length; i++) temp[i] = 0;
		
		if(this.length()<g.length()) this.shiftLeft(g.length()-this.length()); // shift array if numbers dont have same length
		
		int move = 0;
		for(int i = this.length()-1; i >= 0; i--) { //start at last element of first num
			for(int s = g.length()-1; s >= 0; s--) { // start at last element of second num
				//verbPrint("Multiplying at index: " + s + "," + i);
				if(temp.length-1-move <= 0) break;
				if(((int)temp[temp.length-1-move] + (int)g.nums[s]*(int)this.nums[i]) > 100 ) carry(temp,temp.length-1-move,0);
				temp[temp.length-1-move] += g.nums[s]*this.nums[i]; // sum downward into temp array
				/*
				if(verbose) { // for debugging
					for(byte b : temp) {
						System.out.print("["+b+"]");
					}
					System.out.println("\n"+ SciNotation());
				}
				*/
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
		//if(q==a) return q; 
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
	private void verbPrint(String text) {
		if(verbose) System.out.println(text);
	}
	public void carry(int idx, int end) {
		//if(verbose) verbPrint("Carrying digit \'"+(nums[idx] / 10)+ "\' from index["+idx+"/"+(length()-1)+"]: "+this.toString());
		super.numCarry++;
		if(idx == 0 || idx == end) return;
		
		if(nums[idx] < 10 && nums[idx] >= 0){
			while(nums[idx] < 10 && nums[idx] >= 0) {
				idx--;
				System.out.println(idx);
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
		super.numCarry++;
		//if(verbose) System.out.println("Carrying digit \'"+(array[idx] / 10)+ "\' from index["+idx+"/"+(array.length-1)+"]: ");
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
	
	private void shiftLeft() {
		byte[] temp = new byte[nums.length+1];
		System.arraycopy(nums, 0, temp, 1, nums.length);
		nums = temp;
		//System.gc();
	}
	private void shiftLeft(int num) {
		verbPrint("Shifting left to right " + num + " times(s)");
		byte[] temp = new byte[nums.length+num];
		System.arraycopy(nums, 0, temp, num, nums.length);
		nums = temp;
		//System.gc();
	}
	private void autoShrink() {
		int i = 0;
		while(i < this.length()-1 && nums[i]==0) {
			i++;
		}
		verbPrint("Shrinking array from left index by " + i + " places");
		byte[] newArray = new byte[nums.length-i];
		System.arraycopy(nums, i, newArray, 0, nums.length-i);
		this.nums=newArray;
		
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
}
