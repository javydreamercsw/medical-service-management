package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class PersonHasServicePK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "person_id")
    private int personId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_instance_id")
    private int serviceInstanceId;

    public PersonHasServicePK() {
    }

    public PersonHasServicePK(int personId, int serviceInstanceId) {
        this.personId = personId;
        this.serviceInstanceId = serviceInstanceId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
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
        hash += (int) personId;
        hash += (int) serviceInstanceId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersonHasServicePK)) {
            return false;
        }
        PersonHasServicePK other = (PersonHasServicePK) object;
        if (this.personId != other.personId) {
            return false;
        }
        if (this.serviceInstanceId != other.serviceInstanceId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.PersonHasServicePK[ personId=" + personId + ", serviceInstanceId=" + serviceInstanceId + " ]";
    }
    
}
