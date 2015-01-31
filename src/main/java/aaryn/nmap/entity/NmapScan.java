package aaryn.nmap.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Hibernate entity class representing the guts of an nmap scan, including the host,
 * the scan time, and all the port states.
 * 
 * @author aaryno1
 *
 */
@Entity
@Table(name = "NmapScan")
public class NmapScan {

	public NmapScan(){
		scanPorts=new ArrayList<>();
	}
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id; // primary key

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="error")
	private String error;
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Column(name = "scanDate")
	private Timestamp scanDate;

	public Timestamp getScanDate() {
		return scanDate;
	}

	public void setScanDate(Timestamp scanDate) {
		this.scanDate = scanDate;
	}

	@Column(name="hostFound")
	private boolean hostFound;
	
	public boolean isHostFound() {
		return hostFound;
	}

	public void setHostFound(boolean hostFound) {
		this.hostFound = hostFound;
	}

	@ManyToOne(fetch=FetchType.EAGER,cascade={ CascadeType.ALL })
	@JoinColumn(name="internetHostId")
	private InternetHost internetHost;

	public InternetHost getInternetHost() {
		return internetHost;
	}

	public void setInternetHost(InternetHost internetHost) {
		this.internetHost = internetHost;
	}

    @OneToMany(mappedBy="nmapScan", fetch=FetchType.EAGER, cascade={ CascadeType.ALL })
	@OrderBy("port")
    private List<ScanPort> scanPorts;
	public List<ScanPort> getScanPorts() {
		return scanPorts;
	}

	public void setScanPorts(List<ScanPort> scanPorts) {
		this.scanPorts = scanPorts;
	}

}
