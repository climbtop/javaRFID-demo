package com.main.test;

import java.util.Observer;

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
import com.util.TimeTool;

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
			
			TimeTool.spend();
		}

		@Override
		public void onLostConnect() {
			 System.out.println("onLostConnect");
			 while(true){
				 try{
					 mReaderHelper.getmConnector().reConnect();
					 break;
				 }catch(Exception e){
					 e.printStackTrace();
				 }
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			 }
		}
		
	};
	public static void main(String[] args) {
		
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
}
