package aaryn.nmap.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ScanPort")
public class ScanPort {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id; 

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="nmapScanId")    
	private NmapScan nmapScan; // foreign key references NmapScan.id
	
	public NmapScan getNmapScan() {
		return nmapScan;
	}

	public void setNmapScan(NmapScan nmapScan) {
		this.nmapScan = nmapScan;
	}

	@Column(name = "port", nullable=false)
	private Integer port;
	
	public Integer getPort() {	
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Column(name = "state", nullable=false)
	private String state;
	
	public String getState() {	
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public ScanPort(Integer port, String state, NmapScan nmapScan) {
		this.port=port;
		this.state=state;
		this.nmapScan=nmapScan;
	}
}
