package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "instance_field")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InstanceField.findAll",
            query = "SELECT i FROM InstanceField i"),
    @NamedQuery(name = "InstanceField.findById",
            query = "SELECT i FROM InstanceField i WHERE i.instanceFieldPK.id = :id"),
    @NamedQuery(name = "InstanceField.findByServiceInstanceId",
            query = "SELECT i FROM InstanceField i WHERE i.instanceFieldPK.serviceInstanceId = :serviceInstanceId"),
    @NamedQuery(name = "InstanceField.findByIntVal",
            query = "SELECT i FROM InstanceField i WHERE i.intVal = :intVal"),
    @NamedQuery(name = "InstanceField.findByFloatVal",
            query = "SELECT i FROM InstanceField i WHERE i.floatVal = :floatVal"),
    @NamedQuery(name = "InstanceField.findByBoolVal",
            query = "SELECT i FROM InstanceField i WHERE i.boolVal = :boolVal")})
public class InstanceField implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected InstanceFieldPK instanceFieldPK;
    @Column(name = "int_val")
    private Integer intVal;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "float_val")
    private Float floatVal;
    @Lob
    @Column(name = "string_val")
    private byte[] stringVal;
    @Column(name = "bool_val")
    private Boolean boolVal;
    @JoinColumn(name = "service_instance_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ServiceInstance serviceInstance;

    public InstanceField() {
    }

    public InstanceField(InstanceFieldPK instanceFieldPK) {
        this.instanceFieldPK = instanceFieldPK;
    }

    public InstanceField(int serviceInstanceId) {
        this.instanceFieldPK = new InstanceFieldPK(serviceInstanceId);
    }

    public InstanceFieldPK getInstanceFieldPK() {
        return instanceFieldPK;
    }

    public void setInstanceFieldPK(InstanceFieldPK instanceFieldPK) {
        this.instanceFieldPK = instanceFieldPK;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public Float getFloatVal() {
        return floatVal;
    }

    public void setFloatVal(Float floatVal) {
        this.floatVal = floatVal;
    }

    public byte[] getStringVal() {
        return stringVal;
    }

    public void setStringVal(byte[] stringVal) {
        this.stringVal = stringVal;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(Boolean boolVal) {
        this.boolVal = boolVal;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (instanceFieldPK != null ? instanceFieldPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InstanceField)) {
            return false;
        }
        InstanceField other = (InstanceField) object;
        if ((this.instanceFieldPK == null && other.instanceFieldPK != null) || (this.instanceFieldPK != null && !this.instanceFieldPK.equals(other.instanceFieldPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.InstanceField[ instanceFieldPK=" + instanceFieldPK + " ]";
    }

}
