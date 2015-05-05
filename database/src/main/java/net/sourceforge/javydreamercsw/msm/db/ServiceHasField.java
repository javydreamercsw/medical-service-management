package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import net.sourceforge.javydreamercsw.msm.server.EntityListener;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@EntityListeners(value = EntityListener.class)
@Table(name = "service_has_field")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceHasField.findAll",
            query = "SELECT s FROM ServiceHasField s"),
    @NamedQuery(name = "ServiceHasField.findByServiceId",
            query = "SELECT s FROM ServiceHasField s WHERE s.serviceHasFieldPK.serviceId = :serviceId"),
    @NamedQuery(name = "ServiceHasField.findByFieldId",
            query = "SELECT s FROM ServiceHasField s WHERE s.serviceHasFieldPK.fieldId = :fieldId"),
    @NamedQuery(name = "ServiceHasField.findByIndex",
            query = "SELECT s FROM ServiceHasField s WHERE s.index = :index")})
public class ServiceHasField implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ServiceHasFieldPK serviceHasFieldPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "index")
    private int index;
    @JoinColumn(name = "field_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TMField tmfield;
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Service service;

    public ServiceHasField() {
    }

    public ServiceHasField(ServiceHasFieldPK serviceHasFieldPK) {
        this.serviceHasFieldPK = serviceHasFieldPK;
    }

    public ServiceHasField(ServiceHasFieldPK serviceHasFieldPK, int index) {
        this.serviceHasFieldPK = serviceHasFieldPK;
        this.index = index;
    }

    public ServiceHasField(int serviceId, int fieldId) {
        this.serviceHasFieldPK = new ServiceHasFieldPK(serviceId, fieldId);
    }

    public ServiceHasFieldPK getServiceHasFieldPK() {
        return serviceHasFieldPK;
    }

    public void setServiceHasFieldPK(ServiceHasFieldPK serviceHasFieldPK) {
        this.serviceHasFieldPK = serviceHasFieldPK;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TMField getTmfield() {
        return tmfield;
    }

    public void setTmfield(TMField tmfield) {
        this.tmfield = tmfield;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceHasFieldPK != null ? serviceHasFieldPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceHasField)) {
            return false;
        }
        ServiceHasField other = (ServiceHasField) object;
        return !((this.serviceHasFieldPK == null && other.serviceHasFieldPK != null) || (this.serviceHasFieldPK != null && !this.serviceHasFieldPK.equals(other.serviceHasFieldPK)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.ServiceHasField[ serviceHasFieldPK=" + serviceHasFieldPK + " ]";
    }

}
