package net.sourceforge.javydreamercsw.tm;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "person_has_service")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonHasService.findAll", query = "SELECT p FROM PersonHasService p"),
    @NamedQuery(name = "PersonHasService.findByPersonId", query = "SELECT p FROM PersonHasService p WHERE p.personHasServicePK.personId = :personId"),
    @NamedQuery(name = "PersonHasService.findByServiceid", query = "SELECT p FROM PersonHasService p WHERE p.personHasServicePK.serviceid = :serviceid"),
    @NamedQuery(name = "PersonHasService.findByDate", query = "SELECT p FROM PersonHasService p WHERE p.date = :date")})
public class PersonHasService implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersonHasServicePK personHasServicePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "person_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Person person;
    @JoinColumn(name = "Service_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Service service;

    public PersonHasService() {
    }

    public PersonHasService(PersonHasServicePK personHasServicePK) {
        this.personHasServicePK = personHasServicePK;
    }

    public PersonHasService(PersonHasServicePK personHasServicePK, Date date) {
        this.personHasServicePK = personHasServicePK;
        this.date = date;
    }

    public PersonHasService(int personId, int serviceid) {
        this.personHasServicePK = new PersonHasServicePK(personId, serviceid);
    }

    public PersonHasServicePK getPersonHasServicePK() {
        return personHasServicePK;
    }

    public void setPersonHasServicePK(PersonHasServicePK personHasServicePK) {
        this.personHasServicePK = personHasServicePK;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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
        hash += (personHasServicePK != null ? personHasServicePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersonHasService)) {
            return false;
        }
        PersonHasService other = (PersonHasService) object;
        return !((this.personHasServicePK == null
                && other.personHasServicePK != null)
                || (this.personHasServicePK != null
                && !this.personHasServicePK.equals(other.personHasServicePK)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.PersonHasService[ personHasServicePK="
                + personHasServicePK + " ]";
    }

}
