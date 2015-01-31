package aaryn.nmap.dao;

import java.util.ArrayList;
import java.util.Iterator;
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
		session.saveOrUpdate(nmapScan);
		tx.commit(); 
		session.close();
	}

	public void saveInternetHost(InternetHost internetHost){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.saveOrUpdate(internetHost);
		tx.commit(); 
		session.close();
	}
	public void deleteInternetHost(InternetHost internetHost){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.delete(internetHost);
		tx.commit(); 
		session.close();
	}

	public void saveHostAlias(HostAlias hostAlias){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.saveOrUpdate(hostAlias);
		tx.commit(); 
		session.close();
	}
	public void deleteHostAlias(HostAlias hostAlias){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.delete(hostAlias);
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
	
	
	public void saveNmapScan(NmapScan nmapScan){
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		session.saveOrUpdate(nmapScan.getInternetHost());
		tx.commit(); 
		tx=session.beginTransaction();
		session.save(nmapScan);
		tx.commit(); 
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
		tx.commit();
		session.close();
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
	public List<NmapScan> retrieveNmapScansByHost(String host) {
		InternetHost internetHost=retrieveHost(host);
		if (internetHost==null){
			return null;
		}
		List<NmapScan> nmapScanList=new ArrayList<>();
		Session session = getSessionFactory().openSession();
		Transaction tx=session.beginTransaction();
		Query query=session.createQuery("from NmapScan as scan where scan.internetHost.ip=:ip");
		query.setParameter("ip",internetHost.getIp());
		List<NmapScan> list=query.list();
		Iterator<NmapScan> iterator=list.iterator();
		while (iterator.hasNext()){
			nmapScanList.add(iterator.next());
		}
		tx.commit();
		session.close();
		return nmapScanList;
	}
	
}
