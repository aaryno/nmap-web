package aaryn.nmap.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "HostAlias")
public class HostAlias 
{
	@Id
	@GeneratedValue
	@Column(name = "id")
    private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    @ManyToOne
    @JoinColumn(name="internetHostId")
    private InternetHost internetHost;
    
	public InternetHost getInternetHost() {
		return internetHost;
	}

	public void setInternetHost(InternetHost internetHost) {
		this.internetHost = internetHost;
	}

	@Column(name = "fqdn")
    private String fqdn;

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

}
