package aaryn.nmap.summary;

import java.util.Date;
import java.util.List;

public class SiteScanHistory {

	private String[][] portStateArray;
	private int[][] portChangeArray;
	private List<Integer> historicallyOpenPorts;
	private List<Integer> ports;
	private List<Date> scanDates;
	private String ip;
	private List<String> hostnames;
	
	public String[][] getPortStateArray() {
		return portStateArray;
	}
	public void setPortStateArray(String[][] portStateArray) {
		this.portStateArray = portStateArray;
	}
	
	public int[][] getPortChangeArray() {
		return portChangeArray;
	}
	public void setPortChangeArray(int[][] portChangeArray) {
		this.portChangeArray = portChangeArray;
	}
	public List<Integer> getPorts() {
		return ports;
	}
	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}
	public List<Date> getScanDates() {
		return scanDates;
	}
	public void setScanDates(List<Date> scanDates) {
		this.scanDates = scanDates;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public List<String> getHostnames() {
		return hostnames;
	}
	public void setHostnames(List<String> hostnames) {
		this.hostnames = hostnames;
	}
	public List<Integer> getHistoricallyOpenPorts() {
		return historicallyOpenPorts;
	}
	public void setHistoricallyOpenPorts(List<Integer> historicallyOpenPorts) {
		this.historicallyOpenPorts = historicallyOpenPorts;
	}
	
}
