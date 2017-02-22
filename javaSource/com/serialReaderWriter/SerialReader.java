package com.serialReaderWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.serialReaderWriter.SerialInputStream;
import com.serialReaderWriter.SerialReaderWriterMain;

import java.lang.*;



public class SerialReader implements Runnable
{

	SerialInputStream in;



	public SerialReader(SerialInputStream serialPortInputStream)
	{
		this.in = serialPortInputStream;
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
				System.out.print("Character: " +(char)buffer[0]+" ");
				System.out.print("Character: " +(byte)buffer[0]+" ");
				System.out.print("Integer:"+(int)buffer[0]+"\n");
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