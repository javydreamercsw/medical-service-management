package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "service")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Service.findAll", 
            query = "SELECT s FROM Service s"),
    @NamedQuery(name = "Service.findById", 
            query = "SELECT s FROM Service s WHERE s.id = :id"),
    @NamedQuery(name = "Service.findByName", 
            query = "SELECT s FROM Service s WHERE s.name = :name")})
public class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "SERVICE_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "SERVICE",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SERVICE_GEN")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "service")
    private List<ServiceHasField> serviceHasFieldList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceId")
    private List<ServiceInstance> serviceInstanceList;

    public Service() {
    }

    public Service(Integer id) {
        this.id = id;
    }

    public Service(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServiceHasField> getServiceHasFieldList() {
        return serviceHasFieldList;
    }

    public void setServiceHasFieldList(List<ServiceHasField> serviceHasField) {
        this.serviceHasFieldList = serviceHasField;
    }

    @XmlTransient
    public List<ServiceInstance> getServiceInstanceList() {
        return serviceInstanceList;
    }

    public void setServiceInstanceList(List<ServiceInstance> serviceInstanceList) {
        this.serviceInstanceList = serviceInstanceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.Service[ id=" + id + " ]";
    }
    
}
