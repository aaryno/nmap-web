package aaryn.nmap.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aaryn.nmap.dao.NmapScannerDao;
import aaryn.nmap.entity.NmapScan;
import aaryn.nmap.scanner.NmapScanner;
import aaryn.nmap.summary.SiteScan;
import aaryn.nmap.summary.SiteScanFactory;
import aaryn.nmap.summary.SiteScanHistory;

/**
 * Web service implementations for single site scan and historical site scan
 * path to single site scan is /nmap/scan/{host} path to historical site scan is
 * /nmap/history/{host}
 * 
 * Results are returned in JSON according to the default schema derived by
 * Jackson ObjectMapper from aaryn.nmap.summary.SiteScan and
 * aaryn.nmap.summary.SiteScanHistory.
 * 
 * @author aaryno1
 *
 */

@Path("/nmap")
public class Scan {

	private static String ports = "0-1000";

	@GET
	@Path("/scan/{host}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SiteScan> scanHost(@PathParam("host") String host) {
		int cc = 0;
		System.out.println("Scan " + cc++);
		System.out.println("Scanning host: " + host);
		if (host == null || host.length() == 0) {
			NmapScan nmapScan = new NmapScan();
			nmapScan.setError("Please enter a host");
		}
		String[] hosts = host.split(",");
		List<SiteScan> scans = new ArrayList<>();
		for (int i = 0; i < hosts.length; i++) {
			String hostActual = hosts[i];
			NmapScan nmapScan = null;
			System.out.println("Scan *" + cc++);
			try {
				nmapScan = NmapScanner.scan(hostActual, ports);

				if (!nmapScan.isHostFound()) {
					System.out.println("Host not found: " + host);
					return null;
				}
				System.out.println("Scan &" + cc++);

				if (nmapScan.isHostFound()) {
					NmapScannerDao.getInstance().saveNmapScan(nmapScan);
				}
				System.out.println("Scan ^" + cc++);
				scans.add(SiteScanFactory.buildSiteScan(nmapScan));
				i++;
				System.out.println("Scan %" + cc++);
			} catch (IOException e) {
				if (nmapScan == null) {
					nmapScan = new NmapScan();
				}
				e.printStackTrace();
				nmapScan.setError("Error occurred: " + e.getMessage());
				return null;
			}
		}
		System.out.println("Scan $" + cc++);
		// List<NmapScan>
		// nmapScans=NmapScannerDao.getInstance().retrieveNmapScansByHost(host);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			System.out.println("Scan @" + cc++);
			String json = objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(scans);
			System.out.println("Scan " + cc++);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return scans;
	}

	@GET
	@Path("/history/{host}")
	@Produces(MediaType.APPLICATION_JSON)
	public SiteScanHistory getHistory(@PathParam("host") String host) {
		List<aaryn.nmap.entity.NmapScan> nmapScans = NmapScannerDao
				.getInstance().retrieveNmapScansByHost(host);
		SiteScanHistory history = SiteScanFactory
				.buildSiteScanHistory(nmapScans);
		return history;
	}
}
