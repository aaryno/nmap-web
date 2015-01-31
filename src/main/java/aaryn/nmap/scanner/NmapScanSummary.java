package aaryn.nmap.scanner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aaryn.nmap.PortState;

/**
 * Contains detailed information about an individual nmap scan.
 * 
 * @author aaryno1
 *
 */
public class NmapScanSummary {

	private Map<Integer,PortState> portStates;
	private String scanOutputStdout;
	private String scanOutputStderr;
	private String host;
	private Date startScanTime;
	private Date endScanTime;
	private int exitStatus;
	private boolean interrupted;
	private boolean unknownHost;
	
	public NmapScanSummary(){
		this.portStates=new HashMap<>();
		this.unknownHost=false;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(int exitStatus) {
		this.exitStatus = exitStatus;
	}

	public String getScanOutputStdout() {
		return scanOutputStdout;
	}

	public void setScanOutputStdout(String scanOutput) {
		this.scanOutputStdout = scanOutput;
	}

	public String getScanOutputStderr() {
		return scanOutputStderr;
	}

	public void setScanOutputStderr(String scanOutput) {
		this.scanOutputStderr = scanOutput;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Date getStartScanTime() {
		return startScanTime;
	}

	public void setStartScanTime(Date startScanTime) {
		this.startScanTime = startScanTime;
	}

	public Date getEndScanTime() {
		return endScanTime;
	}

	public void setEndScanTime(Date endScanTime) {
		this.endScanTime = endScanTime;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setPortState(Integer port, PortState portState) {
		this.portStates.put(port,portState);
	}
	
	public Map<Integer,PortState> getPortStates(){
		return this.portStates;
	}

	public boolean isUnknownHost() {
		return unknownHost;
	}

	public void setUnknownHost(boolean unknownHost) {
		this.unknownHost = unknownHost;
	}
	

	
}
