package com.thermometry.boardTypes;

import gov.nasa.gsfc.protobuf.ThermometryPacketProtobuf.*;

public class Master {

	byte[] fPacketArraybytes;
	int UBCFrameCount = 0; 
	char fUBCFrameCountTemp []= new char[8]; 

	int PICFrameCount = 0;
	char fPicFrameCountTemp [] = new char [8];
	ThermometryPacket.Builder thermometryPacket = ThermometryPacket.newBuilder();
	MasterMessage.Builder masterMessage = MasterMessage.newBuilder();

		
	public Master (){}

	public void readMaster(byte[] packetArraybytes){
	

		fPacketArraybytes=packetArraybytes;

		//Address
		char addressChar []= new char[2];
		for(int i=0;i<2;i++){
			addressChar[i] = (char)fPacketArraybytes[1+i];
		}		
		
		int Address = Integer.parseInt(String.valueOf(addressChar), 16) ; 
		thermometryPacket.setMs(masterMessage.setAddress(Address));


		//BoardType
		char BoardType = (char) packetArraybytes[3];
		thermometryPacket.setMs(masterMessage.setBoardType(String.valueOf(BoardType)));


		//FrameCounter
		char frameCounterChar []= new char[2];
		for(int i=0;i<2;i++){
			frameCounterChar[i] = (char)fPacketArraybytes[4+i];
		}
		int FrameCounter = Integer.parseInt(String.valueOf(frameCounterChar), 16) ; 
		thermometryPacket.setMs(masterMessage.setFrameCounter(FrameCounter));

		

		//Status
		char status =  (char)fPacketArraybytes[7];
		int Status = Integer.parseInt(String.valueOf(status), 16) ;
		thermometryPacket.setMs(masterMessage.setStatus(Status));


		//UBC Frame Count
		for(int i=0;i<8;i++){
			fUBCFrameCountTemp[i] = (char)fPacketArraybytes[8+i];
		}
		UBCFrameCount=Integer.parseInt(String.valueOf(fUBCFrameCountTemp), 16);
		thermometryPacket.setMs(masterMessage.setUBCFrameCount(UBCFrameCount));


		//PIC Frame Count
		for(int i=0;i<8;i++){
			fPicFrameCountTemp[i] = (char)fPacketArraybytes [16+i];
		}
		PICFrameCount = Integer.parseInt(String.valueOf(fPicFrameCountTemp), 16);
		thermometryPacket.setMs(masterMessage.setPICFrameCount(PICFrameCount));

		thermometryPacket.build();
		
		System.out.println("\n**************************************************************************************************\n"
							+ "***************** Master VALUES ******************************************************************\n ");
	
		System.out.println("Address = "+thermometryPacket.getMs().getAddress());
		System.out.println("BoardType = "+thermometryPacket.getMs().getBoardType());
		System.out.println("FrameCounter = "+thermometryPacket.getMs().getFrameCounter());
		System.out.println("Status = "+thermometryPacket.getMs().getStatus()+"\n\tHEX Character bits:\n\t0 (LSB)	COM Mode (0 = RS232 or 1 = Fiber)\n\t1		UBC Frame Count present	(1 = present)\n\t2		CLK Source (0 = External or 1 = Internal)\n\t3 (MSB)	Unused ");
		System.out.println("UBCFrameCount = " + thermometryPacket.getMs().getUBCFrameCount());
		System.out.println("PICFrameCount = " + thermometryPacket.getMs().getPICFrameCount());

	}
}

/*
Pos Len	Meaning
0	1   '*'
1	2	Address
3	1	Card type ('M')
4	2	Frame counter
6	1	Unused
7	1	Status
		HEX Character bits
		0 (LSB)	COM Mode (0 = RS232 or 1 = Fiber)
		1		UBC Frame Count present	(1 = present)
		2		CLK Source (0 = External or 1 = Internal)
		3 (MSB)	Unused

8   8   UBC Frame Count
16  8   PIC Frame Count
24  1	'\r'
25  1	'\n'
26  1 	'\0'
 */
