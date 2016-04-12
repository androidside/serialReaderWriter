package com.thermometry;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

import com.thermometry.BoardTypeHandler;
import com.thermometry.SerialInputStream;
import com.thermometry.ThermometryReaderMain;

import calibration.Calibrator;


public class SerialReader implements Runnable
{

	SerialInputStream in;
	private BoardTypeHandler handler = new BoardTypeHandler();
	public Calibrator calibrators[] = new Calibrator[16]; //TRead has 16 channels



	public SerialReader(SerialInputStream serialPortInputStream, Calibrator calibrators[])
	{
		this.in = serialPortInputStream;
		this.calibrators = calibrators;
	}

	public void run()
	{
		byte[] buffer = new byte[1];
		List<Byte> packetListBytes = new ArrayList<Byte>();
		int len = -1;
		int indexOfPacket = 0;
		int c = 0;     
		boolean done = false, starDetected = false;

		System.out.print("Starting Reader ...");
		while (done == false)
		{
			try
			{
				//If avaliable == 0 the method does block

				c = this.in.read(buffer,0,1);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//                System.out.println("C = "+c+" and readBuf = "+readBuf[0]);
			if (c > 0)
			{
				if(ThermometryReaderMain.fDumpData == false){

					if(buffer[0] == '*'){
						starDetected = true;

					}

					if(starDetected == true){
						packetListBytes.add(buffer[0]);
					}

					if(buffer[0] == '\n'){
						starDetected = false;

						/*
						 * Transform List to Array
						 * */ 
						Byte[] packetArrayBytes = new Byte[packetListBytes.size()];
						packetListBytes.toArray(packetArrayBytes);

						handler.detectHeader(packetArrayBytes, calibrators);
						packetListBytes = new ArrayList<Byte>();



						/*
						 * Send packet/
						 */
					}
				}

				System.out.print((char)buffer[0]);
				System.out.flush();
			}
			else if (c < 0)
			{   done = true;
			try
			{
				throw new IOException("Stream closed");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}


		}
	}
}