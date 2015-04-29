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
    @Column(name = "Service_id")
    private int serviceid;

    public PersonHasServicePK() {
    }

    public PersonHasServicePK(int personId, int serviceid) {
        this.personId = personId;
        this.serviceid = serviceid;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getServiceid() {
        return serviceid;
    }

    public void setServiceid(int serviceid) {
        this.serviceid = serviceid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) personId;
        hash += (int) serviceid;
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
        return this.serviceid == other.serviceid;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.PersonHasServicePK[ personId=" + personId + ", serviceid=" + serviceid + " ]";
    }
    
}
