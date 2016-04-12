package com.thermometry;

import java.lang.Byte;
import java.util.logging.Level;
import java.util.logging.Logger;

import calibration.Calibrator;

import com.thermometry.boardTypes.AnalogIn;
import com.thermometry.boardTypes.AnalogOut;
import com.thermometry.boardTypes.DSPID;
import com.thermometry.boardTypes.Master;
import com.thermometry.boardTypes.TRead_Diode;
import com.thermometry.boardTypes.TRead_HighRes;
import com.thermometry.boardTypes.TRead_LowRes;
import com.thermometry.boardTypes.TRead_Standard;

public class BoardTypeHandler  {
	private static final Logger sLogger = Logger.getLogger(BoardTypeHandler.class.getName());
	Byte[] packetArrayBytes;
	TRead_Diode trD = new TRead_Diode();
	TRead_Standard trS = new TRead_Standard();
	TRead_LowRes trL = new TRead_LowRes();
	TRead_HighRes trH =  new TRead_HighRes();
	Master ms = new Master();
	DSPID ds = new DSPID();;
	AnalogIn ai = new AnalogIn();
	AnalogOut ao = new AnalogOut();

	public BoardTypeHandler(){}


	public void detectHeader(Byte[] packetArrayBytes, Calibrator calibrators[]) {
		
		this.packetArrayBytes = packetArrayBytes;

		/*
		 *  Transform Byte array to byte array
		 *  */

		byte[] packetArraybytes = new byte[this.packetArrayBytes.length];

		for (int i = 0; i < packetArraybytes.length; i++) {
			packetArraybytes[i] = this.packetArrayBytes[i].byteValue();
		}

		/***
		 * Read the board type, switch cases, create thread and class for each type
		 * */

		/* If first element is not a '*' error !*/
		if(packetArraybytes[0]!='*'){
			sLogger.log(Level.WARNING, "Sync bit '*' not found !" );
			System.out.println("Sync bit '*' not found !");
		}

		else{
			//	sLogger.log(Level.INFO,"Sync bit '*' found ! =) ");

		}

		byte boardType = packetArraybytes[3];

		switch(boardType){

		case 'T':  /*Parse to TRead */
			//Status
			int statusByteInteger;
			String status =Character.toString ((char) packetArraybytes[7]);
			statusByteInteger = Integer.parseUnsignedInt(String.valueOf(status), 10);

			if(statusByteInteger == 0){
				//readMode = TRead_Standard;
				System.out.println("TRead type match !, readMode = "+statusByteInteger);

				trS.read(packetArraybytes, calibrators);
			}	
			else if(statusByteInteger == 1){
				//readMode = TRead_HR;
				trH.read(packetArraybytes, calibrators);
			}
			else if(statusByteInteger == 2){
				//readMode = TRead_LR;
				trL.read(packetArraybytes, calibrators);
			}
			else if(statusByteInteger == 3){
				//readMode = TRead_Diode;
				trD.read(packetArraybytes);
			}
			else{
				sLogger.log(Level.SEVERE, "readMode unknown = " +statusByteInteger );			
			}
			
			break;

		case 'D':
			System.out.println("Board type 'D' = DSIP, starting parser ...");
			ds.readDSIP(packetArraybytes);
			break;

		case 'I':
			System.out.println("Board type 'I' = Analog IN, starting parser ...");
			ai.readAnalogIn(packetArraybytes);

			break;

		case 'O':
			System.out.println("Board type 'I' = Analog IN, starting parser ...");
			ao.readAnalogOut(packetArraybytes);			
			break;

		case 'M':
			System.out.println("Board type 'M' = Master, starting parser ...");
			ms = new Master();
			ms.readMaster(packetArraybytes);			
			break;

		default: System.out.println("No board type match, found = "+boardType);

		break;
		}


	}
}
