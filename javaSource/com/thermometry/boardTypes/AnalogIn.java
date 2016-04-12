package com.thermometry.boardTypes;

import gov.nasa.gsfc.protobuf.ThermometryPacketProtobuf.AnalogINMessage;
import gov.nasa.gsfc.protobuf.ThermometryPacketProtobuf.ThermometryPacket;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalogIn {

	byte[] fPacketArraybytes;
	private static final Logger sLogger = Logger.getLogger(AnalogIn.class.getName());

	ThermometryPacket.Builder thermometryPacket = ThermometryPacket.newBuilder();
	AnalogINMessage.Builder analogINMessage= AnalogINMessage.newBuilder();

	public AnalogIn (){	}

	public void readAnalogIn(byte[] packetArraybytes){	

		fPacketArraybytes=packetArraybytes;
		//Address
		char addressChar []= new char[2];
		for(int i=0;i<2;i++){
			addressChar[i] = (char)fPacketArraybytes[1+i];
		}		
		int Address = Integer.parseInt(String.valueOf(addressChar), 16) ; 
		thermometryPacket.setAi(analogINMessage.setAddress(Address));


		//BoardType
		char BoardType = (char)packetArraybytes[3];
		thermometryPacket.setAi(analogINMessage.setBoardType(String.valueOf(BoardType)));


		//FrameCounter
		char frameCounterChar []= new char[2];
		for(int i=0;i<2;i++){
			frameCounterChar[i] = (char)fPacketArraybytes[4+i];
		}		
		int FrameCounter = Integer.parseInt(String.valueOf(frameCounterChar), 16) ;
		thermometryPacket.setAi(analogINMessage.setFrameCounter(FrameCounter));

		//Gain
		String gain =Character.toString ((char) fPacketArraybytes[6]);
		int Gain= Integer.parseUnsignedInt(String.valueOf(gain), 10);	
		thermometryPacket.setAi(analogINMessage.setGain(Gain));


		//Status
		String status =Character.toString ((char) fPacketArraybytes[7]);
		int Status = Integer.parseUnsignedInt(String.valueOf(status), 10);		
		thermometryPacket.setAi(analogINMessage.setStatus(Status));

		//Sample per Channel per frame
		char sampPerChannelPerFrame []= new char[2];
		for(int i=0;i<2;i++){
			sampPerChannelPerFrame[i] = (char)fPacketArraybytes[8+i];
		}
		int SamplesPerChannel = Integer.parseInt(String.valueOf(sampPerChannelPerFrame), 16) ;
		thermometryPacket.setAi(analogINMessage.setSamplesPerChannel(SamplesPerChannel));

		//NumChannels
		char numChannelsChar []= new char[2];
		for(int i=0;i<2;i++){
			numChannelsChar[i] = (char)fPacketArraybytes[10+i];
		}
		int NumberOfChannels = Integer.parseInt(String.valueOf(numChannelsChar), 16) ;
		thermometryPacket.setAi(analogINMessage.setNumberOfChannels(NumberOfChannels));


		int [] ADCRaw = new int[32]; //32 = Num Channels usually, requires verification
		float [] ADC = new float[32];
		char AnalogInTemp []= new char[4];

		for (int j = 0; j < ADCRaw.length; j++) {
			for(int i=0;i<AnalogInTemp.length;i++){
				AnalogInTemp[i] = (char)fPacketArraybytes[12+4*j+i];
			}
			ADCRaw[j] = Integer.parseInt(String.valueOf(AnalogInTemp), 16) ; 

			switch(Gain){
			case 0: //if 0, Gain = 1
				ADC[j] = (float) ((ADCRaw[j] / 3200) - (32768/3200));
				break;
			case 1:  //if 1, Gain = 10

				ADC[j] = (float) ((ADCRaw[j] / 32000) - (32768/32000));	
				break;
			case 2: //if 100, Gain = 100
				ADC[j] = (float) ((ADCRaw[j] / 320000) - (32768/320000));	
				break;
			case 'T': //if 'T', AD590, celsius
				ADC[j] = (float) ((ADCRaw[j] / 320000) - (32768/320000));	

			default: 
				if((char) fPacketArraybytes[6] == 'T'){
					//if 'T', AD590, celsius
					ADC[j] = (float) ((ADCRaw[j] / 320000) - (32768/320000));	
				}
				else{
					sLogger.log(Level.SEVERE, "Gain number = " +Gain +" Gain Char"+ (char) fPacketArraybytes[6]);
				}
				break;
			}

			thermometryPacket.setAi(analogINMessage.addADCRaw(ADCRaw[j]));	
			thermometryPacket.setAi(analogINMessage.addADC(ADC[j]));	
		}

		thermometryPacket.build();

		System.out.println(" ----------------- AnalogIn VALUES -----------------  ");		
		System.out.println("Address = "+thermometryPacket.getAi().getAddress());
		System.out.println("BoardType = "+thermometryPacket.getAi().getBoardType());
		System.out.println("FrameCounter = "+thermometryPacket.getAi().getFrameCounter());
		System.out.println("Gain = "+thermometryPacket.getAi().getGain()+", 0=gain of 1, 1=gain of 10, 2=gain of 100, T=AD590 temperature mode");
		System.out.println("Status = "+thermometryPacket.getAi().getStatus());
		System.out.println("SamplesPerChannel = "+thermometryPacket.getAi().getSamplesPerChannel());
		System.out.println("NumberOfChannels = "+thermometryPacket.getAi().getNumberOfChannels());
		for (int i = 0; i < ADC.length; i++) {
			System.out.println("ADC["+i+"] = "+thermometryPacket.getAi().getADC(i));
		}

		System.out.println(" ----------------- AnalogIn RAW VALUES -----------------  ");		
		for (int i = 0; i < ADCRaw.length; i++) {
			System.out.println("ADCRaw["+i+"] = "+thermometryPacket.getAi().getADCRaw(i));
		}
	}

	/*
	Pos Len	Meaning
	0	1   '*'
	1	2	Address
	3	1	Card type
	4	2	Frame counter
	6	1	Gain (0=gain of 1, 1=gain of 10, 2=gain of 100, T=AD590 temperature mode)
	7	1	Status
	8	2	Samples per channel per frame (defaults to 1)
	10	2	Num channels (1, 2, 4, 8, 16, 32, default to 32)

	12	4	Analog In[0]
	16	4	Analog In[1]
	20	4	Analog In[2]
	24	4	Analog In[3]
	28	4	Analog In[4]
	32	4	Analog In[5]
	36	4	Analog In[6]
	40	4	Analog In[7]
	44	4	Analog In[8]
	48	4	Analog In[9]
	52	4	Analog In[10]
	56	4	Analog In[11]
	60	4	Analog In[12]
	64	4	Analog In[13]
	68	4	Analog In[14]
	72	4	Analog In[15]
	76	4	Analog In[16]
	80	4	Analog In[17]
	84	4	Analog In[18]
	88	4	Analog In[19]
	92	4	Analog In[20]
	96	4	Analog In[21]
	100	4	Analog In[22]
	104	4	Analog In[23]
	108	4	Analog In[24]
	112	4	Analog In[25]
	116	4	Analog In[26]
	120	4	Analog In[27]
	124	4	Analog In[28]
	128	4	Analog In[29]
	132	4	Analog In[30]
	136	4	Analog In[31]
	140	1	'\r'
	141	1	'\n'
	142	1	'\0'	- not transmited, but makes frame a C string
	 */



}
