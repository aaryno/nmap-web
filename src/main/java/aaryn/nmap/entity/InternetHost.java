package aaryn.nmap.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "InternetHost")
public class InternetHost 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
    private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ip",unique=true)
    private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Column(name = "ipType")
    private String ipType;

	public String getIpType() {
		return ipType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}

    @OneToMany(mappedBy="internetHost", fetch=FetchType.EAGER, cascade={CascadeType.ALL})
    private Set<HostAlias> hostAliases;

	public Set<HostAlias> getHostAliases() {
		return hostAliases;
	}

	public void setHostAliases(Set<HostAlias> hostAliases) {
		this.hostAliases = hostAliases;
	}
}
