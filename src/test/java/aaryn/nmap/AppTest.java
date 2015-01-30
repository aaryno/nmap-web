package aaryn.nmap;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import aaryn.nmap.dao.NmapScannerDao;
import aaryn.nmap.entity.HostAlias;
import aaryn.nmap.entity.InternetHost;
import aaryn.nmap.entity.NmapScan;
import aaryn.nmap.entity.ScanPort;
import aaryn.nmap.scanner.NmapScanner;
import aaryn.nmap.util.NmapWebProperties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /** 
     * PROPERTIES 
     * 
     */
    public void testLoadProperties(){
    	String nmapPath=null;
    	try {
    		Properties properties=NmapWebProperties.getInstance().getProperties();
     
    		nmapPath=properties.getProperty("nmap_path");
     
    	} catch (IOException ex) {
    		System.err.println("Unable to load resource file from classpath");
    		ex.printStackTrace();
    	}
    	// if we loaded successfully, then nmapPath is not null
        assertTrue(nmapPath!=null);
        assertTrue(nmapPath.toLowerCase().contains("nmap"));
    }
    
    /**
     *  ************** NMAP TESTS **************
     */
    
    /**
     * 
     * When a user types in an invalid ip or hostname
     * Then an error message should appear asking the user to re-submit (web side)
     * 
     * This function tests that the invalid ip or host is detected and reported
     * back to calling function
     */
    public void testNmapHostError() {
    	String host="255.256.257.258";
    	String ports="0-100";
    	try {
			NmapScan nmapScan=NmapScanner.scan(host,ports);
			assertFalse(nmapScan.isHostFound());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Unexpected error in NmapScanner: "+e.getMessage());
		}
    }
    
    public void testNmapHostSuccess() {
    	String host="google.com";
    	String ports="70-90";
    	try {
    		NmapScan nmapScan=NmapScanner.scan(host,ports);
			assertTrue(nmapScan.isHostFound());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Unexpected error in NmapScanner: "+e.getMessage());
		}
    }
    
    public void testNmapMultiHosts() {
    	String hosts="google.com,yahoo.com";
    	String ports="70-90";
    	String[] hostArray=hosts.split(",");
    	for (String host : hostArray){
    		NmapScan nmapScan;
			try {
				nmapScan = NmapScanner.scan(host,ports);
				assertTrue(nmapScan.isHostFound());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	// test error on none, one, or both
    }

    /**
     *  ************** DATABASE TESTS **************
     */

    public void testDatabaseInsert() {

    	try {
	        InternetHost host = new InternetHost();
	 
	        host.setIp("1.1.1.1");
    		System.out.println("Saving object");
	        NmapScannerDao.getInstance().saveInternetHost(host);

	        assert(true);
    	} catch (Exception e){
    		e.printStackTrace();
    		fail("Unable to insert: "+e.getMessage());
    	}
    }

    public void testDatabaseQuery() {
    	try {

	        String ip="google.com";
    		InternetHost internetHost=NmapScannerDao.getInstance().retrieveHostByIp(ip);
	 
	        assert(internetHost!=null);
	        assert(ip.equals(internetHost.getIp()));
    	} catch (Exception e){
    		e.printStackTrace();
    		fail("Unable to fetch: "+e.getMessage());
    	}
    }
    // todo: More unit tests for various permutations of complete and incomplete data

    public void testInternetHostAliases(){

    	InternetHost internetHost=new InternetHost();
    	internetHost.setIp("1.2.3.4");
    	NmapScannerDao.getInstance().saveInternetHost(internetHost);

    	HostAlias hostAlias=new HostAlias();
    	hostAlias.setFqdn("a.b.c.d");
    	hostAlias.setInternetHost(internetHost);
    	NmapScannerDao.getInstance().saveHostAlias(hostAlias);
    	
        hostAlias=new HostAlias();
    	hostAlias.setFqdn("a.b.c.Z");
    	hostAlias.setInternetHost(internetHost);
    	NmapScannerDao.getInstance().saveHostAlias(hostAlias);

    	InternetHost host=NmapScannerDao.getInstance().retrieveHostByFqdn("a.b.c.d");
    	assertTrue(host!=null);
    	host=NmapScannerDao.getInstance().retrieveHostByIp("1.2.3.4");
    	assertTrue(host!=null);
    	host=NmapScannerDao.getInstance().retrieveHostByIp("1.2.3.4");
    	assertTrue(host!=null);
    	host=NmapScannerDao.getInstance().retrieveHostByFqdn("a.b.c.d");
    	assertTrue(host!=null);
    	host=NmapScannerDao.getInstance().retrieveHostByFqdn("a.b.c.Z");
    	assertTrue(host!=null);
    	host=NmapScannerDao.getInstance().retrieveHost("a.b.c.Z");
    	assertTrue(host!=null);
    	host=NmapScannerDao.getInstance().retrieveHost("1.2.3.4");
    	assertTrue(host!=null);
    	
    	System.out.println("inte : "+internetHost.getId()+","+internetHost.getIp());
    	assertTrue(host.getId().intValue()==internetHost.getId().intValue());
    	InternetHost host2=NmapScannerDao.getInstance().retrieveHost("a.b.c.Z");
    	assertTrue(host2.getId().intValue()==internetHost.getId().intValue());
    	InternetHost host3=NmapScannerDao.getInstance().retrieveHost("1.2.3.4");
    	assertTrue(host3.getId().intValue()==internetHost.getId().intValue());
    }

    /**
     *  ************** SCAN+DATABASE TESTS **************
     */

    public void testDatabasePopulateFullScan() {
    	try {
        	String host="yahoo.com";
        	String ports="0-100";
        	try {
        		NmapScan nmapScan=NmapScanner.scan(host,ports);
    			assertTrue(nmapScan.isHostFound());
    			
        		InternetHost internetHost=NmapScannerDao.getInstance().retrieveHost(host);
    	        assert(internetHost==null);
    	        
    	        NmapScannerDao.getInstance().saveNmapScan(nmapScan);
    	        
    	        
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			fail("Unexpected error in NmapScanner: "+e.getMessage());
    		}
    	} catch (Exception e){
    		e.printStackTrace();
    		fail("Unable to fetch: "+e.getMessage());
    	}
    }

    public void testRetrieveScanHistory() {
    	try {
        	String host="yahoo.com";
        	String ports="70-90";
        	try {
        		NmapScan nmapScan1=NmapScanner.scan(host,ports);
    			assertTrue(nmapScan1.isHostFound());
        		InternetHost internetHost=NmapScannerDao.getInstance().retrieveHost(host);
    	        assert(internetHost==null);
    	        NmapScannerDao.getInstance().saveNmapScan(nmapScan1);

        		NmapScan nmapScan2=NmapScanner.scan(host,ports);
        		nmapScan2.getScanPorts().get(10).setState("filtered");
        		nmapScan2.getScanPorts().get(11).setState("open");
    	        NmapScannerDao.getInstance().saveNmapScan(nmapScan2);

        		NmapScan nmapScan3=NmapScanner.scan(host,ports);
        		nmapScan3.getScanPorts().get(12).setState("filtered");
    	        NmapScannerDao.getInstance().saveNmapScan(nmapScan3);
    	        
    	        List<NmapScan> nmapScanList=NmapScannerDao.getInstance().
    	        		retrieveNmapScansByHost(internetHost.getIp());
    	        System.out.println("NMAP SIZE: "+nmapScanList.size());
    	        assertTrue(nmapScanList.size()>=3);
    	        for (NmapScan nmapScan : nmapScanList){
    	        	System.out.println("nmapscan: "+nmapScan.getId()+" - "+
			    	        nmapScan.getInternetHost().getIp()+","+nmapScan.getInternetHost().
			    	        getHostAliases().iterator().next().getFqdn());
    	        	for (ScanPort scanPort : nmapScan.getScanPorts()){
//        	        	System.out.println("port: "+scanPort.getPort()+": "+scanPort.getState());
    	        	}
    	        }
    	        
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			fail("Unexpected error in NmapScanner: "+e.getMessage());
    		}
    	} catch (Exception e){
    		e.printStackTrace();
    		fail("Unable to fetch: "+e.getMessage());
    	}
    }
    
    /**
     *  ************** WEB SERVICE TESTS **************
     */
//
//	private InternetHost createNewInternetHostFromNmapScan(NmapScan nmapScan) {
//		InternetHost internetHost=new InternetHost();
//		internetHost.setFqdn(nmapScan.get);
//		return internetHost;
//	}
//	private InternetHost mergeInternetHostFromNmapScan(InternetHost internetHost, NmapScan nmapScan) {
//		return null;
//	}

}
