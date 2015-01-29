package aaryn.nmap.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
//import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import aaryn.nmap.entity.HostAlias;
import aaryn.nmap.entity.InternetHost;
import aaryn.nmap.entity.NmapScan;

/**
 * Dao class for interacting with nmap scanner database
 * 
 * Some shortcomings with this approach: 
 * 
 * @author aaryno1
 *
 */
public class NmapScannerDao {
	 
	private SessionFactory sessionFactory;
	private static NmapScannerDao nmapScannerDao;
	
	private  NmapScannerDao(){
		System.out.println("Getting session factory");
		sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
		System.out.println("Got session factory");
	}
	public static synchronized NmapScannerDao getInstance(){
		if (nmapScannerDao==null){
			nmapScannerDao=new NmapScannerDao();
		}
		return nmapScannerDao;
	}
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	 
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void insertNmapScan(NmapScan nmapScan) {
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.save(nmapScan);
		tx.commit(); 
		session.close();
	}

	public void saveInternetHost(InternetHost internetHost){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.save(internetHost);
		tx.commit(); 
		session.close();
	}
	
	/**
	 * Merge this internetHost with the copy in the database with the same IP
	 * @param newInternetHost
	 * @return
	 */
	public InternetHost getInternetHostMergedWithSaved(InternetHost newInternetHost){
		InternetHost savedInternetHost=retrieveHostByIp(newInternetHost.getIp());
		if (savedInternetHost!=null){
			newInternetHost.setId(savedInternetHost.getId());
			for (HostAlias savedAlias: savedInternetHost.getHostAliases()){
				boolean found=false;
				for (HostAlias newAlias: newInternetHost.getHostAliases()){
					if (savedAlias.getFqdn()!=null && savedAlias.getFqdn().equals(newAlias.getFqdn())){
						newAlias.setId(savedAlias.getId());
						found=true;
						break;
					} 
				}
				if (!found){
					newInternetHost.getHostAliases().add(savedAlias);
				}
			}
		}
		return newInternetHost;
	}
	
	public void saveHostAlias(HostAlias hostAlias){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.save(hostAlias);
		tx.commit(); 
		session.close();
	}
	public void saveNmapScan(NmapScan nmapScan){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.save(nmapScan.getInternetHost());
		tx.commit(); 
		tx=session.beginTransaction();
		session.save(nmapScan);
		tx.commit(); 
//		session.refresh(nmapScan);
//		for (ScanPort scanPort : nmapScan.getScanPorts()){
//			
//		}
		session.close();
	}
	
	public InternetHost retrieveHostByFqdn(String hostname) {
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		Query query=session.createQuery("from HostAlias as alias inner join alias.internetHost as host where alias.fqdn=:fqdn");
		query.setParameter("fqdn",hostname);
		List<Object[]> list=query.list();
		if (list!=null && !list.isEmpty()){
			Object[] pair = list.get(0);
			HostAlias hostAlias = (HostAlias)pair[0];
			InternetHost host=(InternetHost)pair[1];
			tx.commit();
			session.close();
			return host;
		}
		tx.commit();
		session.close();
		return null;
	}
	public InternetHost retrieveHostByIp(String ip){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		Query query=session.createQuery("from InternetHost as host where host.ip=:ip");
		query.setParameter("ip",ip);
		List<InternetHost> list=query.list();
		if (list!=null && !list.isEmpty()){
			InternetHost host=list.get(0);
			tx.commit();
			session.close();
			return host;
		}
		return null;
	}
	public HostAlias retrieveHostAliasByFqdn(String fqdn){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		Query query=session.createQuery("from HostAlias as alias where alias.fqdn=:fqdn");
		query.setParameter("fqdn",fqdn);
		List<HostAlias> list=query.list();
		if (list!=null && !list.isEmpty()){
			HostAlias alias=list.get(0);
			tx.commit();
			session.close();
			return alias;
		}
		return null;
	}
	
	public InternetHost retrieveHost(String hostname){
		System.out.println("Retrieving by hostname");
		InternetHost host=retrieveHostByIp(hostname);
		if (host==null){
			System.out.println("not found: Retrieving by alias");
			return retrieveHostByFqdn(hostname);
		}
		return host;
	}
	
//	public void insertScanPort(ScanPort scanPort) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		session.save(scanPort);
//		session.getTransaction().commit(); 
//	}
//	 
//	public List<NmapScan> selectScansByHost(String hostname) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		// 1. get appropriate host from hosts table
//		
//		// 2. get all scans for this host
//		Criteria criteria = session.createCriteria(NmapScan.class);
//		criteria.add(session.createCriteria(arg0, arg1));
//		List<NmapScan> nmapScans = (List<NmapScan>) criteria.list();
//		session.getTransaction().commit();
//		return nmapScans;
//	}
}
