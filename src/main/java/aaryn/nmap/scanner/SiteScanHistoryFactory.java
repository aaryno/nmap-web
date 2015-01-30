package aaryn.nmap.scanner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aaryn.nmap.dao.NmapScannerDao;
import aaryn.nmap.entity.HostAlias;
import aaryn.nmap.entity.InternetHost;
import aaryn.nmap.entity.NmapScan;
import aaryn.nmap.entity.ScanPort;

public class SiteScanHistoryFactory {

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
		InternetHost internetHost=nmapScans.get(0).getInternetHost();
		List<Date> scanDates=new ArrayList<>();
		List<String> hostnames=new ArrayList<>();
		Map<Integer,String> lastState=new HashMap<>();

		// make a master list of ports in case previous scans used different ports
		// save the scan dates while we pass through the scans
		for (NmapScan nmapScan : nmapScans){
			for (ScanPort scanPort : nmapScan.getScanPorts()){
				orderedPorts.add(scanPort.getPort());
				scanDates.add(new Date(nmapScan.getScanDate().getTime()));
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
	
	public static void main(String[] arg) throws JsonProcessingException{
    	String host="yahoo.com";

		InternetHost internetHost=NmapScannerDao.getInstance().retrieveHost(host);
        List<NmapScan> nmapScanList=NmapScannerDao.getInstance().
        		retrieveNmapScansByHost(internetHost.getIp());
        
        SiteScanHistory siteScanHistory=SiteScanHistoryFactory.buildSiteScanHistory(nmapScanList);
        ObjectMapper objectMapper = new ObjectMapper();
        String json=objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(siteScanHistory);
        System.out.println(json);
	}
}
