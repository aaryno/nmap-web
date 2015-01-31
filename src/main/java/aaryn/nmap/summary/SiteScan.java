package aaryn.nmap.summary;

import java.util.ArrayList;
import java.util.List;

public class SiteScan {

	private String host;
	
	private String ip;

	private List<String> hostAliases;

	private List<Integer> openPorts;
	private List<Integer> openFilteredPorts;
	private List<Integer> closedPorts;
	private List<Integer> closedFilteredPorts;
	private List<Integer> filteredPorts;
	private List<Integer> unfilteredPorts;
	
	public SiteScan() {
		super();
		openPorts=new ArrayList<>();
		openFilteredPorts=new ArrayList<>();
		closedPorts=new ArrayList<>();
		closedFilteredPorts=new ArrayList<>();
		filteredPorts=new ArrayList<>();
		unfilteredPorts=new ArrayList<>();
		hostAliases=new ArrayList<>();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<String> getHostAliases() {
		return hostAliases;
	}

	public void setHostAliases(List<String> hostAliases) {
		this.hostAliases = hostAliases;
	}

	public List<Integer> getOpenPorts() {
		return openPorts;
	}

	public void setOpenPorts(List<Integer> openPorts) {
		this.openPorts = openPorts;
	}

	public List<Integer> getOpenFilteredPorts() {
		return openFilteredPorts;
	}

	public void setOpenFilteredPorts(List<Integer> openFilteredPorts) {
		this.openFilteredPorts = openFilteredPorts;
	}

	public List<Integer> getClosedPorts() {
		return closedPorts;
	}

	public void setClosedPorts(List<Integer> closedPorts) {
		this.closedPorts = closedPorts;
	}

	public List<Integer> getClosedFilteredPorts() {
		return closedFilteredPorts;
	}

	public void setClosedFilteredPorts(List<Integer> closedFilteredPorts) {
		this.closedFilteredPorts = closedFilteredPorts;
	}

	public List<Integer> getFilteredPorts() {
		return filteredPorts;
	}

	public void setFilteredPorts(List<Integer> filteredPorts) {
		this.filteredPorts = filteredPorts;
	}

	public List<Integer> getUnfilteredPorts() {
		return unfilteredPorts;
	}

	public void setUnfilteredPorts(List<Integer> unfilteredPorts) {
		this.unfilteredPorts = unfilteredPorts;
	}
	
}
