package com.thermometry.boardTypes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalogOut {

	byte[] fPacketArraybytes;
	private static final Logger sLogger = Logger.getLogger(AnalogOut.class.getName());


	public AnalogOut (){	}

	public void readAnalogOut(byte[] packetArraybytes){

		fPacketArraybytes = packetArraybytes;


		//Address
		char addressChar []= new char[2];
		for(int i=0;i<2;i++){
			addressChar[i] = (char)fPacketArraybytes[1+i];
		}		
		int addressInt = Integer.parseInt(String.valueOf(addressChar), 16) ; 
		System.out.println("Address of AnalogOut = "+addressInt);

		//BoardType
		char boardType = (char)packetArraybytes[3];
		System.out.println("Board type AnalogOut = "+boardType);

		//FrameCounter
		char frameCounterChar []= new char[2];
		for(int i=0;i<2;i++){
			frameCounterChar[i] = (char)fPacketArraybytes[4+i];
		}

		int frameCounterInt = Integer.parseInt(String.valueOf(frameCounterChar), 16) ; 
		System.out.println("Frame Counter Analog Out  = "+frameCounterInt);


		//Status
		String status =Character.toString ((char) fPacketArraybytes[7]);
		int statusByteInteger = Integer.parseUnsignedInt(String.valueOf(status), 10);		
		System.out.println("Status Byte = "+statusByteInteger);



		int [] AnalogOut = new int[32]; //32 = Num Channels usually,requires verification
		char AnalogOutTemp []= new char[3];

		for (int j = 0; j < AnalogOut.length; j++) {
			for(int i=0;i<AnalogOutTemp.length;i++){
				AnalogOutTemp[i] = (char)fPacketArraybytes[8+3*j+i];
			}
			AnalogOut[j] = Integer.parseInt(String.valueOf(AnalogOutTemp), 16) ; 
			System.out.println("Analog Out["+j+"] = "+AnalogOut[j]+" = "+obtainVolts(AnalogOut[j])+" volts");
		}

	}

	float obtainVolts(int analogInValue){

		//Max value if 12 bits  = 1111 1111 1111
		int maxValue = (int) Math.pow(2, 12) - 1;
		if(analogInValue >  maxValue){
			sLogger.log(Level.SEVERE, "max Value unknown = " +analogInValue );		
		}

		//Max value 10 V
		float result = 10 * (analogInValue/maxValue);
		return result;		
	}
	/*
	Pos Len	Meaning
	0	1   '*'
	1	2	Address
	3	1	Card type
	4	2	Frame counter
	6	1	Unused
	7	1	Status
	8	3	Analog Out[0]
	11	3	Analog Out[1]
	14	3	Analog Out[2]
	17	3	Analog Out[3]
	20	3	Analog Out[4]
	23	3	Analog Out[5]
	26	3	Analog Out[6]
	29	3	Analog Out[7]
	32	3	Analog Out[8]
	35	3	Analog Out[9]
	38	3	Analog Out[10]
	41	3	Analog Out[11]
	44	3	Analog Out[12]
	47	3	Analog Out[13]
	50	3	Analog Out[14]
	53	3	Analog Out[15]
	56	3	Analog Out[16]
	59	3	Analog Out[17]
	62	3	Analog Out[18]
	65	3	Analog Out[19]
	68	3	Analog Out[20]
	71	3	Analog Out[21]
	74	3	Analog Out[22]
	77	3	Analog Out[23]
	80	3	Analog Out[24]
	83	3	Analog Out[25]
	86	3	Analog Out[26]
	89	3	Analog Out[27]
	92	3	Analog Out[28]
	95	3	Analog Out[29]
	98	3	Analog Out[30]
	101	3	Analog Out[31]
	104	1	'\r'
	105	1	'\n'
	106	1	'\0'	- not transmited, but makes frame a C string
	 */



}
