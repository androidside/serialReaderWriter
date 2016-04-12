package com.thermometry.boardTypes;

import com.sun.org.apache.bcel.internal.generic.DDIV;

import gov.nasa.gsfc.protobuf.ThermometryPacketProtobuf.*;

public class DSPID {

	byte[] fPacketArraybytes;

	int numberOfChannels = 8;

	int DemodRaw []= new int[numberOfChannels];
	float Demod[] = new float[numberOfChannels];


	int CoilIsenseRaw []= new int[numberOfChannels];
	float[] CoilIsense =  new float[numberOfChannels];

	int CoilDACRaw []= new int[numberOfChannels];
	float CoilDAC []= new float[numberOfChannels];


	float ADac;
	int GDac; 
	float CoilVMon, AnalogIn,	Vsupply,Gnd,BoardTemp,ExternalTemp;
	float [] AnalogOut =  new float [4];
	float PidSetPoint;
	int PidError,PidAccumulator,PidP,PidI; //PidP,PidI 8 bits

	int ADacRaw, CoilVMonRaw, AnalogInRaw,	VsupplyRaw,GndRaw,BoardTempRaw,ExternalTempRaw;
	int [] AnalogOutRaw =  new int [4];
	int PidSetPointRaw;

	char fDemodTemp []= new char[8];
	char CoilIsenseRawTemp []= new char[4];
	char fCoilDacTemp []= new char[4];
	
	
	ThermometryPacket.Builder thermometryPacket = ThermometryPacket.newBuilder();
	
	DSPIDMessage.Builder dSPIDMessage = DSPIDMessage.newBuilder();

	public DSPID (){	}

	public void readDSIP(byte[] packetArraybytes){

		this.fPacketArraybytes = packetArraybytes;

		//Address
		char addressChar []= new char[2];
		for(int i=0;i<2;i++){
			addressChar[i] = (char)fPacketArraybytes[1+i];
		}		
		int Address = Integer.parseInt(String.valueOf(addressChar), 16) ; 
		thermometryPacket.setDs(dSPIDMessage.setAddress(Address));


		//BoardType
		char BoardType = (char)packetArraybytes[3];
		thermometryPacket.setDs(dSPIDMessage.setBoardType(String.valueOf(BoardType)));

		char frameCounterChar []= new char[2];
		for(int i=0;i<2;i++){
			frameCounterChar[i] = (char)fPacketArraybytes[4+i];		}		
		int FrameCounter = Integer.parseInt(String.valueOf(frameCounterChar), 16) ; 
		thermometryPacket.setDs(dSPIDMessage.setFrameCounter(FrameCounter));
		


		//Tmux
		char TMux = (char)packetArraybytes[6];
		thermometryPacket.setDs(dSPIDMessage.setTMux(String.valueOf(TMux)));


		//Status
		String statusString =Character.toString ((char) fPacketArraybytes[7]);
		int Status = Integer.parseUnsignedInt(String.valueOf(statusString), 10);
		thermometryPacket.setDs(dSPIDMessage.setStatus(Status));



		for (int j = 0; j < numberOfChannels; j++) {
			// First 8 HEX characters are asigned to Demod[j]
			for(int i=0;i<8;i++){
				fDemodTemp[i] = (char)fPacketArraybytes[8+16*j+i];
			}

			DemodRaw[j] = (int)Long.parseLong(String.valueOf(fDemodTemp), 16) ; 
			thermometryPacket.setDs(dSPIDMessage.addDemodRaw(DemodRaw[j]));

			//Following 4 HEX characters are asigned to ADACTemp[j]
			for(int i=0;i<4;i++){
				CoilIsenseRawTemp[i] = (char)fPacketArraybytes[16+16*j+i];
			}
			CoilIsenseRaw[j] = Integer.parseInt(String.valueOf(CoilIsenseRawTemp), 16) ; 
			CoilIsense[j] = (float) (CoilIsenseRaw[j] * 0.001/3.2) ; 
			
			thermometryPacket.setDs(dSPIDMessage.addCoilIsenseRaw(CoilIsenseRaw[j]));
			thermometryPacket.setDs(dSPIDMessage.addCoilIsense(CoilIsense[j]));


			//Following 4 HEX characters are asigned to GDACTemp[j]
			for(int i=0;i<4;i++){
				fCoilDacTemp[i] = (char)fPacketArraybytes[20+16*j+i];
			}
			CoilDACRaw[j] = Integer.parseInt(String.valueOf(fCoilDacTemp), 16) ;
			CoilDAC[j] = (float) (CoilDACRaw[j] * (0.001 / 16));
			
			thermometryPacket.setDs(dSPIDMessage.addCoilDACRaw(CoilDACRaw[j]));
			thermometryPacket.setDs(dSPIDMessage.addCoilDAC(CoilDAC[j]));

		}

		char eightBitsRegTemp []= new char[2];
		char sixeteenBitsRegTemp []= new char[4];
		char thirtyTwoBitsRegTemp []= new char[8];

		int indexRegisterCounter = 0;

		//ADac Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		ADacRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		ADac = (float) (ADacRaw * 0.00857780) ;
		indexRegisterCounter++; //1
		thermometryPacket.setDs(dSPIDMessage.setADacRaw(ADacRaw));
		thermometryPacket.setDs(dSPIDMessage.setADac(ADac));

		//GDac Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		GDac = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		indexRegisterCounter++;//2
		thermometryPacket.setDs(dSPIDMessage.setGDac(GDac));


		//CoilVMonRaw Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		CoilVMonRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		indexRegisterCounter++;//3
		CoilVMon = (float) (CoilVMonRaw * (0.001/3.2/5));
		
		thermometryPacket.setDs(dSPIDMessage.setCoilVMonRaw(CoilVMonRaw));
		thermometryPacket.setDs(dSPIDMessage.setCoilVMon(CoilVMon));

		//AnalogInRaw Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		AnalogInRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		AnalogIn = (float) (AnalogInRaw * 0.001/3.2);
		indexRegisterCounter++;//4
		thermometryPacket.setDs(dSPIDMessage.setAnalogInRaw(AnalogInRaw));
		thermometryPacket.setDs(dSPIDMessage.setAnalogIn(AnalogIn));
		
		//ExternalTempRaw Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		ExternalTempRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		ExternalTemp = (float) (ExternalTempRaw * 1/3.2/49.9 + 205.2) ;
		indexRegisterCounter++;//5
		thermometryPacket.setDs(dSPIDMessage.setExternalTempRaw(ExternalTempRaw));
		thermometryPacket.setDs(dSPIDMessage.setExternalTemp(ExternalTemp));


		//BoardTempRaw Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		BoardTempRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		BoardTemp = (float) (BoardTempRaw * 1/3.2/49.9 + 205.2) ;
		indexRegisterCounter++; //6
		thermometryPacket.setDs(dSPIDMessage.setBoardTempRaw(BoardTempRaw));
		thermometryPacket.setDs(dSPIDMessage.setBoardTemp(BoardTemp));

		//VsupplyRaw Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		VsupplyRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		Vsupply =  (float) (VsupplyRaw * (0.001/3.2*3)); // Gain 1/3
		indexRegisterCounter++;//7
		
		thermometryPacket.setDs(dSPIDMessage.setVsupplyRaw(VsupplyRaw));
		thermometryPacket.setDs(dSPIDMessage.setVsupply(Vsupply));

		//GndRaw Register parsing
		for(int i=0;i<4;i++){
			sixeteenBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		GndRaw = Integer.parseInt(String.valueOf(sixeteenBitsRegTemp), 16) ;
		Gnd =  (float) (GndRaw * (0.001/3.2));
		indexRegisterCounter++;//8
		
		thermometryPacket.setDs(dSPIDMessage.setGndRaw(GndRaw));
		thermometryPacket.setDs(dSPIDMessage.setGnd(Gnd));

		//regAODac Registers Parsing
		char regAODacTemp []= new char[4];

		for (int k = 0; k < AnalogOutRaw.length; k++) {

			for (int l = 0; l < 4; l++) {

				regAODacTemp[l] = (char)fPacketArraybytes[136+indexRegisterCounter*4+l];
			}
			AnalogOutRaw[k] = Integer.parseInt(String.valueOf(regAODacTemp), 16) ; 
			AnalogOut[k] = (float) (AnalogOutRaw[k] * 0.005) ;
			
			thermometryPacket.setDs(dSPIDMessage.addAnalogOutRaw(AnalogOutRaw[k]));
			thermometryPacket.setDs(dSPIDMessage.addAnalogOut(AnalogOut[k]));
		}
		//PidSetPoint Register parsing, 32 bits long (4*8 = 32)
		for(int i=0;i<8;i++){
			thirtyTwoBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		PidSetPointRaw = Integer.parseInt(String.valueOf(thirtyTwoBitsRegTemp), 16) ;
		thermometryPacket.setDs(dSPIDMessage.setPidSetPointRaw(PidSetPointRaw));

		// 2 sets of 4 bytes this time
		indexRegisterCounter=indexRegisterCounter+2;

		//PidError Register parsing, 32 bits long
		for(int i=0;i<8;i++){
			thirtyTwoBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		PidError = Integer.parseInt(String.valueOf(thirtyTwoBitsRegTemp), 16) ;
		// 2 sets of 4 bytes this time
		indexRegisterCounter=indexRegisterCounter+2;		
		thermometryPacket.setDs(dSPIDMessage.setPidError(PidError));


		//PidAccumulator Register parsing, 32 bits long
		for(int i=0;i<8;i++){
			thirtyTwoBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		PidAccumulator = Integer.parseInt(String.valueOf(thirtyTwoBitsRegTemp), 16) ;
		// 2 sets of 4 bytes this time
		indexRegisterCounter=indexRegisterCounter+2;		
		thermometryPacket.setDs(dSPIDMessage.setPidAccumulator(PidAccumulator));

		//PidP Register parsing, Not incrementing indexRegisterCounter 
		for(int i=0;i<2;i++){
			eightBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+i];
		}		
		PidP = Integer.parseInt(String.valueOf(eightBitsRegTemp), 8) ;
		thermometryPacket.setDs(dSPIDMessage.setPidP(PidP));
		//PidI Register parsing +2 on the index !!!!
		for(int i=0;i<2;i++){
			eightBitsRegTemp[i] = (char)fPacketArraybytes[136+indexRegisterCounter*4+2+i];
		}		
		PidI = Integer.parseInt(String.valueOf(eightBitsRegTemp), 8) ;
		thermometryPacket.setDs(dSPIDMessage.setPidI(PidI));

		CalcReducedData(ADacRaw,GDac);
		
		thermometryPacket.build();

		//printValues

		System.out.println(" ----------------- DSPID VALUES -----------------  ");

		System.out.println("Address = "+thermometryPacket.getDs().getAddress());
		System.out.println("BoardType = "+thermometryPacket.getDs().getBoardType());
		System.out.println("FrameCounter = "+thermometryPacket.getDs().getFrameCounter());
		System.out.println("TMUX = "+thermometryPacket.getDs().getTMux());
		System.out.println("Status = "+thermometryPacket.getDs().getStatus());
		for (int i = 0; i < Demod.length; i++) {
			System.out.println("Demod["+i+"] = "+thermometryPacket.getDs().getDemod(i)+" Ohms (Resistance Computed)");			
		}
		System.out.println("ADac = "+thermometryPacket.getDs().getADac()+" nanoAmps");
		System.out.println("GDac = "+thermometryPacket.getDs().getGDac());
		for (int i = 0; i < CoilDAC.length; i++) {
			System.out.println("CoilDAC["+i+"] = "+thermometryPacket.getDs().getCoilDAC(i)+" Volts");
		}
		System.out.println("CoilVMon = "+thermometryPacket.getDs().getCoilVMon()+" Volts");
		for (int i = 0; i < CoilIsense.length; i++) {
			System.out.println("CoilIsense["+i+"]  = "+thermometryPacket.getDs().getCoilIsense(i)+" Amps");
		}
		System.out.println("AnalogIn = "+thermometryPacket.getDs().getAnalogIn()+" Volts");
		System.out.println("VSupply = "+thermometryPacket.getDs().getVsupply()+" Volts");
		System.out.println("Gnd = "+thermometryPacket.getDs().getGnd()+" Volts");
		System.out.println("BoardTemp = "+thermometryPacket.getDs().getBoardTemp()+" Kelvin");
		System.out.println("ExternalTemp = "+thermometryPacket.getDs().getExternalTemp()+" Kelvin");
		for (int i = 0; i < AnalogOut.length; i++) {
			System.out.println("AnalogOut["+i+"]  = "+thermometryPacket.getDs().getAnalogOut(i)+" Volts");			
		}	
		System.out.println("PidSetPoint = "+thermometryPacket.getDs().getPidSetPoint()+" Ohms");
		System.out.println("PidError = "+ thermometryPacket.getDs().getPidError());
		System.out.println("PidAccumulator = "+thermometryPacket.getDs().getPidAccumulator());
		System.out.println("PidP = "+thermometryPacket.getDs().getPidP());
		System.out.println("PidI = "+thermometryPacket.getDs().getPidI());

		System.out.println(" ----------------- DSPID RAW VALUES -----------------  ");

		//printValuesRaw
		for (int i = 0; i < DemodRaw.length; i++) {
			System.out.println("DemodRaw["+i+"] = "+thermometryPacket.getDs().getDemodRaw(i));			
		}
		System.out.println("ADacRaw = "+ thermometryPacket.getDs().getADacRaw());
		for (int i = 0; i < CoilDACRaw.length; i++) {
			System.out.println("CoilDACRaw["+i+"] = "+thermometryPacket.getDs().getCoilDACRaw(i));
		}
		System.out.println("CoilVMonRaw = "+thermometryPacket.getDs().getCoilVMonRaw());
		for (int i = 0; i < CoilIsenseRaw.length; i++) {
			System.out.println("CoilIsenseRaw["+i+"]  = "+thermometryPacket.getDs().getCoilIsenseRaw(i));
		}
		System.out.println("AnalogInRaw = "+thermometryPacket.getDs().getAnalogInRaw());
		System.out.println("VSupplyRaw = "+thermometryPacket.getDs().getVsupplyRaw());
		System.out.println("GndRaw = "+thermometryPacket.getDs().getGndRaw());
		System.out.println("BoardTempRaw = "+thermometryPacket.getDs().getBoardTempRaw());
		System.out.println("ExternalTempRaw = "+thermometryPacket.getDs().getExternalTempRaw());
		for (int i = 0; i < AnalogOutRaw.length; i++) {
			System.out.println("AnalogOutRaw["+i+"]  = "+thermometryPacket.getDs().getAnalogOutRaw(i));			
		}	
		System.out.println("PidSetPointRaw = "+thermometryPacket.getDs().getPidSetPointRaw());

	}


	private void CalcReducedData(int ADacRaw, int GDac ) {

		int i;
		double adac = ADacRaw;
		double gdac = GDac;
		// Ensure GDAC != 0.0, in which case we can't compute a resistance
		if (gdac == 0.0) {
			for (i = 0; i < numberOfChannels; ++i)
				thermometryPacket.setDs(dSPIDMessage.setDemod(i, -1));  // Set to an invalid value if gdac==0

			return;
		}

		int num_avg = 200;    // Number of averages 
		double Rbridge = 201100.0;    // Total bridge resistance in series with thermometer
		// includes 2x100k + protection resistors + Rmux
		double PreampGain = 100.0;          // Fixed gain of preamplifier
		double R42_DivideFactor = 72.5;
		double X = PreampGain * 65536.0 / 4.99 * (adac/gdac) * 4 / R42_DivideFactor;

		// Calculate the resistance of RuOx thermometer using Demod, ADAC, GDAC values
		for (i = 0; i < numberOfChannels; ++i) {
			double d =  DemodRaw[i];
			double delta = d / num_avg;
			double res = Rbridge * delta / (X - delta);
			Demod[i]= (float) res;
			thermometryPacket.setDs(dSPIDMessage.addDemod(Demod[i]));
		}

		// Calculate the PID setpoint in resistance units
		double setPointDemod = PidSetPointRaw;
		double setPointDelta = setPointDemod / num_avg;
		double setPointRes = Rbridge * setPointDelta / (X - setPointDelta);

		PidSetPoint = (float)setPointRes;
		thermometryPacket.setDs(dSPIDMessage.setPidSetPoint(PidSetPoint));
	}


}

/*
Pos Len	Meaning
0	1   '*'
1	2	Address
3	1	Card type
4	2	Frame counter
6	1	TMux
7	1	Status
8	8	Demod[]0
16	4	Coil Current[0]
20	4	Coil DAC[0]
24	8	Demod[1]
32	4	Coil Current[1]
36	4	Coil DAC[1]
40	8	Demod[2]
48	4	Coil Current[2]
52	4	Coil DAC[2]
56	8	Demod[3]
64	4	Coil Current[3]
68	4	Coil DAC[3]
72	8	Demod[4]
80	4	Coil Current[4]
84	4	Coil DAC[4]
88	8	Demod[5]
96	4	Coil Current[5]
100	4	Coil DAC[5]
104	8	Demod[6]
112	4	Coil Current[6]
116	4	Coil DAC[6]
120	8	Demod[7]
	128	4	Coil Current[7]
132	4	Coil DAC[7]
	136	4	ADAC	- Thermometer excitation amplitude dac
	140	4	GDAC	- Thermometer readout gain dac
144	4	VMon	- coil voltage monitor
148 4	Analog IN
152 4	External temp
156 4	Board temp
160 4	Vsupply
164	4	Gnd
168	4	AOUT[0]
172	4	AOUT[1]
176	4	AOUT[2]
180	4	AOUT[3]
184 8	PID setpoint (demod units)	
192	8	PID error
200	8	PID accumulator
208	2	PID P coefficient
210	2	PID I coefficient
    212     2       N_SUM  // How many samples per half-period
214	1	'\r'
215	1	'\n'
216 1	'\0'	- not transmited, but makes frame a C string
 */






