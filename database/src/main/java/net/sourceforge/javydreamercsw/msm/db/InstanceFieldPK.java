package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class InstanceFieldPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @TableGenerator(name = "INSTANCE_FIELD_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "INSTANCE_FIELD",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "INSTANCE_FIELD")
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_instance_id")
    private int serviceInstanceId;

    public InstanceFieldPK() {
    }

    public InstanceFieldPK(int serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(int serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) serviceInstanceId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InstanceFieldPK)) {
            return false;
        }
        InstanceFieldPK other = (InstanceFieldPK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.serviceInstanceId == other.serviceInstanceId;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.InstanceFieldPK[ id=" 
                + id + ", serviceInstanceId=" + serviceInstanceId + " ]";
    }
}
