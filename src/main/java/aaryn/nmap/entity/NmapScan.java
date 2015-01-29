package aaryn.nmap.entity;

import java.sql.Date;
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

	@Column(name = "scanDate")
	private Date scanDate;

	public Date getScanDate() {
		return scanDate;
	}

	public void setScanDate(Date scanDate) {
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
