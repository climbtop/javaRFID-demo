package com.rfid;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.module.interaction.ModuleConnector;
import com.module.interaction.ReaderHelper;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

/**
 * The implementation class of ModuleConnector.
 *
 */
public class ReaderConnector implements ModuleConnector{
	private  final String HOSTNAME_REGEXP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	
	private RFIDReaderHelper mRFIDReaderHelper;
	
	private boolean isNetType = false;
	private InetSocketAddress mRemoteAddr;
	private Socket mSocket;
	
	private boolean isComType = false;
	private String comPort;
	private int    comBaud;
	private SerialPort mSeialPort;
	private final String USER = "USER";
	
	@Override
	public ReaderHelper connectCom(final String port, final int baud) {
		try {
			 this.comPort = port;
			 this.comBaud = baud;
			 this.isComType = true;
			 CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);  
			 mSeialPort = (SerialPort) portIdentifier.open(USER,5000);   
			 if (mSeialPort == null) {
					return null;
			 }
			 mSeialPort.setSerialPortParams(baud,  
	                    SerialPort.DATABITS_8,          
	                    SerialPort.STOPBITS_1,          
	                    SerialPort.PARITY_NONE);        
			
			return connect(mSeialPort.getInputStream(),mSeialPort.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void reConnect() throws Exception{
		if(isComType){
			reConnectCom();
		}
		if(isNetType){
			reConnectNet();
		}
	}
	
	public void reConnectCom() throws Exception{
		try {
			if (mSeialPort != null) {
				mSeialPort.close();
				mSeialPort = null;
			}
		} catch (Exception e) {
			throw e;
		}
		try {
			 CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(comPort);  
			 mSeialPort = (SerialPort) portIdentifier.open(USER,5000);   
			 if(mSeialPort == null) return;
			 mSeialPort.setSerialPortParams(comBaud,  
	                    SerialPort.DATABITS_8,          
	                    SerialPort.STOPBITS_1,          
	                    SerialPort.PARITY_NONE);        
		} catch (Exception e) {
			throw e;
		}
		try {
			mRFIDReaderHelper.setReader(mSeialPort.getInputStream(),mSeialPort.getOutputStream());
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ReaderHelper connectNet(final String host, final int port) {
		this.isNetType = true;
		
		if (host.matches(HOSTNAME_REGEXP)){
		}
		else {
			return null;
		}
		
		try {
			mRemoteAddr = new InetSocketAddress(host, port);
			mSocket = new Socket();
		} catch (Exception e1) {
			return null;
		}

		try {
			mSocket.connect(mRemoteAddr, 4000);
			return connect(mSocket.getInputStream(),mSocket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void reConnectNet() throws Exception{
		try {
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
		} catch (Exception e) {
			throw e;
		}
		try {
			mSocket = new Socket();
		} catch (Exception e) {
			throw e;
		}
		try {
			mSocket.connect(mRemoteAddr, 4000);
			mRFIDReaderHelper.setReader(mSocket.getInputStream(),mSocket.getOutputStream());
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ReaderHelper connect(InputStream in,OutputStream out) {
		mRFIDReaderHelper = new RFIDReaderHelper();
		mRFIDReaderHelper.setmConnector(this);
		try {
			mRFIDReaderHelper.setReader(in,out,new ReaderDataPackageParser(),new ReaderDataPackageProcess());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
		return mRFIDReaderHelper;
	}

	@Override
	public boolean isConnected() {
		if(isComType){
			return (mSeialPort!=null  ? (mSeialPort.isRTS()||mSeialPort.isCTS()) : false);
		}
		if(isNetType){
			return (mSocket != null ? mSocket.isConnected() : false);
		}
		return false;
	}

	@Override
	public void disConnect() {
		if (mRFIDReaderHelper != null) {
			mRFIDReaderHelper.signOut();
			mRFIDReaderHelper.setmConnector(null);
			mRFIDReaderHelper = null;
		}
		try {
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
			if (mSeialPort != null) {
				mSeialPort.close();
				mSeialPort = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mRemoteAddr = null;
	}

	public RFIDReaderHelper getmRFIDReaderHelper() {
		return mRFIDReaderHelper;
	}

	public Socket getmSocket() {
		return mSocket;
	}

	public InetSocketAddress getmRemoteAddr() {
		return mRemoteAddr;
	}

	public SerialPort getmSeialPort() {
		return mSeialPort;
	}
	
}
