package aaryn.nmap.dao;

import org.hibernate.*;
import org.hibernate.cfg.*;

/**
 * shameless take from
 * https://docs.jboss.org/hibernate/stable/annotations/reference
 * /en/html/ch01.html
 * 
 * @author aaryno1
 *
 */
public class HibernateUtil {

	private static final SessionFactory sessionFactory;
	static {
		try {
			sessionFactory = new AnnotationConfiguration().configure()
					.buildSessionFactory();
		} catch (Throwable ex) {
			// Log exception!
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() throws HibernateException {
		return sessionFactory.openSession();
	}

	public static Session getCurrentSession() throws HibernateException {
		return sessionFactory.getCurrentSession();
	}

	public static SessionFactory getSessionFactory() throws HibernateException {
		return sessionFactory;
	}
}