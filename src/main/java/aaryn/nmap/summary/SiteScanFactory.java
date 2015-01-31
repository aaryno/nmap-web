package aaryn.nmap.summary;

import java.util.Iterator;

import aaryn.nmap.entity.HostAlias;
import aaryn.nmap.entity.NmapScan;
import aaryn.nmap.entity.ScanPort;

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
}
