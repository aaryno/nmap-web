package aaryn.nmap.summary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import aaryn.nmap.entity.HostAlias;
import aaryn.nmap.entity.InternetHost;
import aaryn.nmap.entity.NmapScan;
import aaryn.nmap.entity.ScanPort;

/**
 * Factory class to generating SiteScan and SiteScanHistory objects from NmapScans
 * @author aaryno1
 *
 */
public class SiteScanFactory {

	public static SiteScan buildSiteScan(NmapScan nmapScan){
		SiteScan siteScan=new SiteScan();
		siteScan.setIp(nmapScan.getInternetHost().getIp());
		try {
		for (ScanPort scanPort : nmapScan.getScanPorts()){
			if ("open".equals(scanPort.getState())){
				siteScan.getOpenPorts().add(scanPort.getPort());
			}
			else if ("open|filtered".equals(scanPort.getState())){
				siteScan.getOpenFilteredPorts().add(scanPort.getPort());
			}
			else if ("closed".equals(scanPort.getState())){
				siteScan.getClosedPorts().add(scanPort.getPort());
			}
			else if ("closed_filtered".equals(scanPort.getState())){
				siteScan.getClosedFilteredPorts().add(scanPort.getPort());
			}
			else if ("filtered".equals(scanPort.getState())){
				siteScan.getFilteredPorts().add(scanPort.getPort());
			}
			else if ("unfiltered".equals(scanPort.getState())){
				siteScan.getUnfilteredPorts().add(scanPort.getPort());
			}
		}
		if (nmapScan.getInternetHost().getHostAliases()!=null){
			Iterator<HostAlias> iterator=nmapScan.getInternetHost().getHostAliases().iterator();
			while (iterator.hasNext()){
				HostAlias hostAlias =iterator.next();
				siteScan.getHostAliases().add(hostAlias.getFqdn());
			}
		}
		} catch (Exception e){
			e.printStackTrace();
		}
		return siteScan;
		
	}
	private static Map<String, Integer> stateValueMap=new HashMap<>();
	static {
		stateValueMap.put("open",0);
		stateValueMap.put("open|filtered",1);
		stateValueMap.put("filtered",2);
		stateValueMap.put("closed",3);
		stateValueMap.put("closed|filtered",4);
	}
	
	public static SiteScanHistory buildSiteScanHistory(List<NmapScan> nmapScans){
		SortedSet<Integer> orderedPorts=new TreeSet<>();
		SortedSet<Integer> orderedOpenedPorts=new TreeSet<>();
		InternetHost internetHost=nmapScans.get(0).getInternetHost();
		List<Date> scanDates=new ArrayList<>();
		List<String> hostnames=new ArrayList<>();
		Map<Integer,String> lastState=new HashMap<>();

		// make a master list of ports in case previous scans used different ports
		// save the scan dates while we pass through the scans
		for (NmapScan nmapScan : nmapScans){
			scanDates.add(new Date(nmapScan.getScanDate().getTime()));
			for (ScanPort scanPort : nmapScan.getScanPorts()){
				orderedPorts.add(scanPort.getPort());
				if (scanPort.getState().startsWith("open")){
					orderedOpenedPorts.add(scanPort.getPort());
				}
			}
		}
		// save off host aliases
		for (HostAlias alias : internetHost.getHostAliases()){
			hostnames.add(alias.getFqdn());
		}
//		// make array and populate it
		String[][] portStateArray=new String[nmapScans.size()][orderedPorts.size()];
		int[][] portStateChanges=new int[nmapScans.size()][orderedPorts.size()];
		
		List<Integer> portsList=new ArrayList<>(orderedPorts);
//		List<String> lastState=new ArrayList<>();
		int scan=0;
		for (NmapScan nmapScan : nmapScans){
			for (ScanPort scanPort : nmapScan.getScanPorts()){
				int port=portsList.indexOf(new Integer(scanPort.getPort()));
				portStateArray[scan][port]=scanPort.getState();
				// flag any state changes
				String lastStateStr=lastState.get(new Integer(port));
				if (lastStateStr!=null && !lastStateStr.equals(scanPort.getState())) {
					portStateChanges[scan][port]=getValueChange(scanPort.getState(),lastStateStr);
				}
				lastState.put(port, scanPort.getState());
			}
			scan++;
		}
		
		SiteScanHistory history=new SiteScanHistory();
		history.setPortStateArray(portStateArray);
		history.setPortChangeArray(portStateChanges);
		history.setHistoricallyOpenPorts(new ArrayList<Integer>(orderedPorts));
		history.setPorts(portsList);;
		history.setScanDates(scanDates);
		history.setIp(internetHost.getIp());
		return history;
	}
	
	public static int getValueChange(String newState, String oldState){
		Integer oldVal=stateValueMap.get(oldState);
		Integer newVal=stateValueMap.get(newState);
		return newVal.intValue()-oldVal.intValue();
	}
	
}
