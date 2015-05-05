package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.sourceforge.javydreamercsw.msm.server.EntityListener;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@EntityListeners(value = EntityListener.class)
@Table(name = "service_instance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceInstance.findAll", query = "SELECT s FROM ServiceInstance s"),
    @NamedQuery(name = "ServiceInstance.findById", query = "SELECT s FROM ServiceInstance s WHERE s.id = :id")})
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @TableGenerator(name = "SERVICE_INSTANCE_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "SERVICE_INSTANCE",
            allocationSize = 1,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SERVICE_INSTANCE")
    @Column(name = "id")
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance")
    private List<InstanceField> instanceFieldList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceInstance")
    private List<PersonHasService> personHasServiceList;
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Service serviceId;

    public ServiceInstance() {
    }

    public ServiceInstance(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public List<InstanceField> getInstanceFieldList() {
        return instanceFieldList;
    }

    public void setInstanceFieldList(List<InstanceField> instanceFieldList) {
        this.instanceFieldList = instanceFieldList;
    }

    @XmlTransient
    public List<PersonHasService> getPersonHasServiceList() {
        return personHasServiceList;
    }

    public void setPersonHasServiceList(List<PersonHasService> personHasServiceList) {
        this.personHasServiceList = personHasServiceList;
    }

    public Service getServiceId() {
        return serviceId;
    }

    public void setServiceId(Service serviceId) {
        this.serviceId = serviceId;
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
        if (!(object instanceof ServiceInstance)) {
            return false;
        }
        ServiceInstance other = (ServiceInstance) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.ServiceInstance[ id=" + id + " ]";
    }

}
