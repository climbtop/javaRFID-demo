package com.main.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Observer;
import java.util.Random;

import com.module.interaction.RXTXListener;
import com.module.interaction.ReaderHelper;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderConnector;
import com.rfid.bean.MessageTran;
import com.rfid.config.CMD;
import com.rfid.config.ERROR;
import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.util.StringTool;

public class RfidTcpTest {
	static ReaderHelper mReaderHelper;
	
	static RXTXListener mListener = new RXTXListener() {
		@Override
		public void reciveData(byte[] btAryReceiveData) {
			// TODO Auto-generated method stub
			System.out.println("reciveData: " + StringTool.byteArrayToString(btAryReceiveData, 0, btAryReceiveData.length));
		}

		@Override
		public void sendData(byte[] btArySendData) {
			// TODO Auto-generated method stub
			System.out.println("sendData: " + StringTool.byteArrayToString(btArySendData, 0, btArySendData.length));
		}

		@Override
		public void onLostConnect() {
			// TODO Auto-generated method stub
		}
		
	};
	public static void main(String[] args) {
		
		try {
			new Thread(new ControlServer()).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}; 
		
		//findPrime(10000000,0);
		//findPrime(10000000,1);
		//findPrime(10000000,2);
		//System.out.println("Is prime: " + isPrime(0xc6a4a7935bd1e995L));
		//testArrayAccessTime();
		final ReaderConnector mConnector = new ReaderConnector();
		//mReaderHelper = mConnector.connectCom("COM7", 115200);
		mReaderHelper = mConnector.connectNet("192.168.1.220", 4001);
		if(mReaderHelper != null) {
			System.out.println("Connect success!");
			try {
				mReaderHelper.registerObserver(mObserver);
				mReaderHelper.setRXTXListener(mListener);
				//((RFIDReaderHelper) mReaderHelper).getTagMask((byte) 0xff);
				
				((RFIDReaderHelper) mReaderHelper).realTimeInventory((byte) 0xff,(byte)0x01);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Connect faild!");
			mConnector.disConnect();
		} 
	}
	
	//######################################################################
	
	static Observer mObserver = new RXObserver() {
		@Override
		protected void onExeCMDStatus(byte cmd,byte status) {
			System.out.format("CDM:%s  Execute status:%S, {%s; %s}\r\n", 
					String.format("%02X",cmd),String.format("%02x", status),
			CMD.format(cmd), ERROR.format(status));
		}
		
		@Override
		protected void onInventoryTag(RXInventoryTag tag) {
			System.out.println("EPC data:" + tag.strEPC);
		}
		
		@Override
		protected void onInventoryTagEnd(RXInventoryTag.RXInventoryTagEnd endTag) {
			System.out.println("inventory end:" + endTag.mTotalRead);
			((RFIDReaderHelper) mReaderHelper).realTimeInventory((byte) 0xff,(byte)0x01);
		}
		
		@Override
	    protected void onOperationTag(RXOperationTag tag) {
			System.out.println("onOperationTag:" + tag.strEPC);
	    }

		@Override
	    protected void onOperationTagEnd(int operationTagCount) {
			System.out.println("operationTagCount:" + operationTagCount);
	    }
		
		//-------------------------------------------------------------
		
		@Override
	    protected void refreshSetting(ReaderSetting readerSetting) {
			System.out.println("Setting:" + readerSetting);
	    }

		@Override
	    protected void onFastSwitchAntInventoryTagEnd(RXInventoryTag.RXFastSwitchAntInventoryTagEnd tagEnd) {
			System.out.println("SwitchAnt:" + tagEnd.toString());
	    }

		@Override
	    protected void onInventory6BTag(byte nAntID, String strUID) {
			System.out.println("nAntID:" + nAntID+", strUID:"+strUID);
	    }

		@Override
	    protected void onInventory6BTagEnd(int nTagCount) {
			System.out.println("nTagCount:" + nTagCount);
	    }

		@Override
	    protected void onRead6BTag(byte antID, String strData) {
			System.out.println("antID:" + antID+", strData:"+strData);
	    }

		@Override
	    protected void onWrite6BTag(byte nAntID, byte nWriteLen) {
			System.out.println("nAntID:" + nAntID+", nWriteLen:"+nWriteLen);
	    }

		@Override
	    protected void onLock6BTag(byte nAntID, byte nStatus) {
			System.out.println("nAntID:" + nAntID+", nStatus:"+nStatus);
	    }

		@Override
	    protected void onLockQuery6BTag(byte nAntID, byte nStatus) {
			System.out.println("nAntID:" + nAntID+", nStatus:"+nStatus);
	    }

		@Override
	    protected void onGetInventoryBufferTagCount(int nTagCount) {
			System.out.println("nTagCount:" + nTagCount);
	    }

		@Override
	    protected void onConfigTagMask(MessageTran msgTran) {
			//System.out.println("msgTran:"+msgTran);
	    }	
		
	};
	
	//######################################################################
	
	
	static void testArrayAccessTime() {
		int arraySize = 32768;
		int data[] = new int[arraySize];
		
		Random random = new Random(0);
		for (int i = 0; i < arraySize; i++) {
			data[i] = random.nextInt() % 256;
		}
		
		Arrays.sort(data);
		
		long start = System.nanoTime();
		long sum = 0;
		
		for (int j = 0; j < 100000; j++) {
			
			for (int k = 0; k < arraySize; k++) {
				int t = (data[k] - 128) >> 31;
			    sum += ~t & data[k];
				/*if (data[k] >= 128) {
					sum += data[k];
				}*/
			}
		}
		
		System.out.println((System.nanoTime() - start) / 1000000000.0);
		System.out.println("sum = " + sum);
	}
	
	
	/** hash al
	 * @param buf
	 * @param seed
	 * @return
	 */
	public static long hash64A(ByteBuffer buf, int seed) {
		ByteOrder byteOrder = buf.order();
		buf.order(ByteOrder.LITTLE_ENDIAN);

		long m = 0xc6a4a7935bd1e995L;
		int r = 47;

		long h = seed ^ (buf.remaining() * m);

		long k;
		while (buf.remaining() >= 8) {
			k = buf.getLong();

			k *= m;
			k ^= k >>> r;
			k *= m;

			h ^= k;
			h *= m;
		}

		if (buf.remaining() > 0) {
			ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
			// for big-endian version, do this first:
			// finish.position(8-buf.remaining());
			finish.put(buf).rewind();
			h ^= finish.getLong();
			h *= m;
		}

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		buf.order(byteOrder);
		return h;
	}
	
	
	static long[] findPrime(long number,int mode) {
		long startNum = 0;
		long start = System.currentTimeMillis();
		switch (mode) {
			case 0:
			{
				while(startNum < number) {
					isPrime(startNum);
					startNum++;
				}
				System.out.println("Mode0 consumption time: " + (System.currentTimeMillis() - start));
			}
				break;
			case 1:
			{
				while(startNum < number) {
					isPrime(startNum);
					startNum += 2;
				}
				System.out.println("Mode1 consumption time: " + (System.currentTimeMillis() - start));
			}
				break;
			case 2:
			{
				while(startNum < number) {
					isPrime(startNum + 1);
					isPrime(startNum + 5);
					startNum += 6;
				}
				System.out.println("Mode2 consumption time: " + (System.currentTimeMillis() - start));
			}
				break;
			default:
				break;
		}
		return null;
	}
	
	
	
	static boolean isPrime(long number) {
		if (number < 0) {
			number = -number;
		}
		long tmp = (long) Math.sqrt(number);
		
		while (tmp > 0) {
			if ((number % tmp) == 0)
				return false;
			tmp--;
		}
		return true;
	}
	
	void eular(int n){
		int primelist[] = new int[n]; 
		int primecount = 0;
		boolean isprime[] = new boolean[n];
	    int i, j;
	    for (i = 0; i <= n; i++){
	        isprime[i] = true;
	    }
	    isprime[0] = isprime[1] = false;
	    for (i = 2; i <= n; i++){
	        if (isprime[i]){
	            primelist[primecount++] = i;
	        }
	        for (j = 0; j < primecount; j++){
	            if (i*primelist[j] > n){
	                break;
	            }
	            isprime[i*primelist[j]] = false;
	            if (i%primelist[j]==0){
	                break;
	            }
	        }
	    }
	}
}
