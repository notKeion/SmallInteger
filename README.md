# SmallInteger
<h1>EARLY DEVELOPMENT STAGE, NOT STABLE</h1>

A High School-Level recreation of Java's BigInteger Class. With this, the hope was to create a more memory efficient solution as BigInteger stores each digit as an int veriable inside of an array. This array was dynamicly resized corespondent to how many digits were stored. 

With SmallInteger however, this utilizes a dynamicly resized byte array to store each digit, not only storing 1 byte per digit stored apose to the 4 bytes stored per digit (BigInteger), but this also dramaticly decreases the time needed to calculate as less data needs to be shifted, copied, etc.

<b>Features:</b>
 - Reduced space complexity
 - Precise digit by digit calculations
 - Automatic digit carrying to reduce useless place values when shrinking array
 - Fully functional add, subtract and multiply algorithms (division is to be implemented stably in next iteration)
 - Reduced temporary variables to decrease peak memory usage during runtime

<b>Public Methods:</b>
 - .length() returns number of digits.
 - .toByteArray() returns number digit by digit as byte array, most sigificant digit in place at the front.
 - .compareTo() returns -1,0,1 if compared number is smaller, equal or larger.
 - .toInteger() returns number as Integer if number does not exceed INTEGER.MAXIMUM
 - .carry() iterates linearly over array and starts from last index, carries numbers >10 to coresponding neighbour, this is done after adding or during runtime with multiplication.
 - 
  <b>Math Methods:</b>
  These methods are initiated by a call to the method and a switch will switch between different experimental algorithms (will be simplified in later releases)
  - .add()
  - .subtract()
  - .multiply()


  
  ![Screenshot from 2022-06-10 07-20-31](https://user-images.githubusercontent.com/41515697/175853031-f401d50b-3853-4722-93c0-78d6e640b406.png)

