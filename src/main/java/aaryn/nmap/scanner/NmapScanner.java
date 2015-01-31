package aaryn.nmap.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import aaryn.nmap.dao.NmapScannerDao;
import aaryn.nmap.entity.HostAlias;
import aaryn.nmap.entity.InternetHost;
import aaryn.nmap.entity.NmapScan;
import aaryn.nmap.entity.ScanPort;
import aaryn.nmap.util.NmapWebProperties;

public class NmapScanner {
	private static Map<Integer,String> portStateMap =new HashMap<>();
	private static String ports="0-100";

	public static List<NmapScan> getScans(String host){
		return NmapScannerDao.getInstance().retrieveNmapScansByHost( host);
	}
	public static NmapScan scan(String host, String portRange) throws IOException
	{
		List<Integer> ports=getPortsFromPortRange(portRange);
		String outputFile="nmap_out."+UUID.randomUUID().toString();
		try { 
			String nmapPath=NmapWebProperties.getInstance().getProperties().getProperty("nmap_path");
			String[] cmdArray=new String[]{ nmapPath,"-oX",outputFile,"-v",host,"-p",portRange};
			Process p = Runtime.getRuntime().exec(cmdArray);  
			System.out.println("Scanning with: "+nmapPath+", "+host);
			
			int exitStatus=p.waitFor();
			System.out.println(outputFile);
			NmapScan nmapScan=readScanOutput(outputFile,ports);
			
			System.out.println("Scanning: "+exitStatus);
			return nmapScan;
		} catch (Throwable t) {
			System.err.println(t.getMessage());
			NmapScan nmapScan=new NmapScan();
			nmapScan.setScanDate(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
			return nmapScan;
		} finally {
			File f=new File(outputFile);
			if (f.exists()){
				f.delete();
			}
		}
	}
	
	private static List<Integer> getPortsFromPortRange(String portRange) {
		List<Integer> ports=new ArrayList<Integer>();
		String[] tokens=portRange.split(",");
		if (tokens.length>1){
			for (int i=0; i<tokens.length-1; i++){
				ports.addAll(getPortsFromPortRange(tokens[i]));
			}
		} else {
			tokens=portRange.split("-");
			if (tokens.length>1){
				Integer start=Integer.valueOf(tokens[0]);
				Integer end=Integer.valueOf(tokens[1]);
				for (int i=start; i<=end; i++){
					ports.add(Integer.valueOf(i));
				}
			}
		}
		Collections.sort(ports);
		return ports;
	}

	/**
	 * Builds the nmap scan summary from the xml output generated by nmap
	 * @param nmapOutputFile name of output file produced by nmap, notably
	 * the [file] specified by -oX [file]
	 * 
	 * Caveat to the reader: a little hairy with the nested loops as we
	 * traverse the DOM plus  it has DAO dependency built in to consolidate 
	 * object reconciliation (i.e., don't make a new InternetHost if there's 
	 * already open in the database). I prefer to separate these functions.
	 * 
	 * @return NmapScan, which is a hibernate entity
	 * This will be fully populated with the ScanPorts (and their state),
	 * @throws NmapException 
	 */
	public static NmapScan readScanOutput(String nmapOutputFile, List<Integer> ports) throws NmapException{
		NmapScan nmapScan=new NmapScan();
		// map to keep track of which ScanPort is associated with which port 
		// (for lookup when we get the "portid" string from the xml attribute)
		Map<Integer,ScanPort> scanPortMap =new HashMap<>();
		for (Integer port : ports){
			ScanPort scanPort = new ScanPort(port,"closed",nmapScan);
			nmapScan.getScanPorts().add(scanPort);
			scanPortMap.put(port,scanPort);
		}
		  SAXBuilder builder = new SAXBuilder();
		  File xmlFile = new File(nmapOutputFile);
		Document document=null;
		   System.out.println("before sax builder");
		try {
			document = (Document) builder.build(xmlFile);
			   System.out.println("got a doc?");
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			   System.out.println("doc fail");
			e.printStackTrace();
			return nmapScan;
		} finally {
			xmlFile.delete();
		}
		   System.out.println("getting root");
		Element nmaprunElement = document.getRootElement();
		if (nmaprunElement!=null){
		   Element hostElement=nmaprunElement.getChild("host");
		   if (hostElement==null){
			   // no host found
			   System.out.println("host element not found");
			   nmapScan.setHostFound(false);
		   } else {
			   nmapScan.setHostFound(true);
			   System.out.println("host element found");
			   Element addressElement=hostElement.getChild("address");
			   nmapScan.setScanDate(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
			   InternetHost internetHost=null;
			   if (addressElement!=null){
				   String ip=addressElement.getAttributeValue("addr");
				   String ipType=addressElement.getAttributeValue("addrtype");
				   internetHost=NmapScannerDao.getInstance().retrieveHostByIp(ip);
				   if (internetHost==null){
					   System.out.println("UNABLE to find host for ip "+ip);
					   internetHost=new InternetHost();
					   internetHost.setIp(ip);
					   internetHost.setIpType(ipType);
				   } else {
					   System.out.println("DUPE hostname found for "+ip);
				   }
				   nmapScan.setInternetHost(internetHost);
				   System.out.println("ip: "+ip+", "+ipType);
			   }
			   if (internetHost==null){
				   throw new NmapException("Parse error, unable to find host in doc");
			   }

			   Element hostnamesElement=hostElement.getChild("hostnames");
			   if (hostnamesElement!=null){
				   List<Element> hostnameList=hostnamesElement.getChildren("hostname");
				   for (Element hostnameElement : hostnameList){
					   String hostname=hostnameElement.getAttributeValue("name");
					   if (hostname!=null){
						   boolean found=false;
						   if (internetHost.getHostAliases()==null){
							   internetHost.setHostAliases(new HashSet<HostAlias>());
						   }
						   for (HostAlias hostAlias : internetHost.getHostAliases()){
							   if (hostAlias.getFqdn()!=null && hostAlias.getFqdn().equals(hostname)){
								   found=true;
							   }
						   }
						   if (!found){
							   HostAlias hostAlias=NmapScannerDao.getInstance().retrieveHostAliasByFqdn(hostname);
							   if (hostAlias==null){
								   hostAlias=new HostAlias();
								   hostAlias.setInternetHost(internetHost);
							   }
							   internetHost.getHostAliases().add(hostAlias);
							   hostAlias.setFqdn(hostname);
							   System.out.println("hostalias fqdn: "+hostAlias.getFqdn());
						   }
						   System.out.println("hostname: "+hostname);
					   }
				   }
			   }
			   Element portsElement=hostElement.getChild("ports");
			   if (portsElement!=null){
				   List<Element> portsList=portsElement.getChildren("port");
				   for (int i=0; i<portsList.size(); i++){
					   Element portElement=portsList.get(i);
					   if (portElement!=null){
						   String portid = portElement.getAttributeValue("portid");
						   Integer port=null;
						   ScanPort scanPort=null;
						   for (Integer port2 : scanPortMap.keySet()){
							   if (port2.intValue()==Integer.valueOf(portid)){
								   port=port2;
								   scanPort=scanPortMap.get(port);
							   }
						   }
						   Element stateElement=portElement.getChild("state");
						   if (stateElement!=null){
							   String state = stateElement.getAttributeValue("state");
							   
							   // Integer port needs t be .equal port in Set...
							   scanPort.setState(state);
							   System.out.println("Port: "+port+": "+state);
						   } // end stateElement
						   else {
							   throw new NmapException("Parse error, unable to find port state in doc");
						   }
					   } // end portElement
					   else {
						   throw new NmapException("Parse error, unable to find port element in doc");
					   }
				   } // end portsList loop
			   } // end portsElement
			   else {
				   throw new NmapException("Parse error, unable to find ports in doc");
			   }
		   }
		}
		return nmapScan;  
	}
}
