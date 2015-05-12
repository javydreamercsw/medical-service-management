package net.sourceforge.javydreamercsw.msm.db;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.sourceforge.javydreamercsw.msm.server.EntityListener;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@EntityListeners(value = EntityListener.class)
@Table(name = "person", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findById", query = "SELECT p FROM Person p WHERE p.id = :id"),
    @NamedQuery(name = "Person.findByName", query = "SELECT p FROM Person p WHERE p.name = :name"),
    @NamedQuery(name = "Person.findByLastname", query = "SELECT p FROM Person p WHERE p.lastname = :lastname"),
    @NamedQuery(name = "Person.findBySsn", query = "SELECT p FROM Person p WHERE p.ssn = :ssn"),
    @NamedQuery(name = "Person.findByUserName", query = "SELECT p FROM Person p WHERE p.username = :un")})
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "PERSON_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "PERSON",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSON_GEN")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "lastname")
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 5, max = 45)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 6, max = 45)
    @Column(name = "password")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 11)
    @Column(name = "ssn")
    private String ssn = "XXX-XX-XXXX";
    @JoinColumn(name = "access_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Access accessId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private List<PersonHasService> personHasServiceList;
    @Basic(optional = false)
    @Column(name = "login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date login;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attempts")
    private int attempts;
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Address addressId;

    public Person() {
    }

    public Person(Integer id) {
        this.id = id;
    }

    public Person(Integer id, String name, String lastname, String ssn) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.ssn = ssn;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String un) {
        this.username = un;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pw) {
        this.password = pw;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Access getAccessId() {
        return accessId;
    }

    public void setAccessId(Access accessId) {
        this.accessId = accessId;
    }

    public Date getLogin() {
        return login;
    }

    public void setLogin(Date ll) {
        this.login = ll;
    }

    @XmlTransient
    public List<PersonHasService> getPersonHasServiceList() {
        return personHasServiceList;
    }

    public void setPersonHasServiceList(List<PersonHasService> personHasServiceList) {
        this.personHasServiceList = personHasServiceList;
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
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.Person[ id=" + id + " ]";
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Address getAddressId() {
        return addressId;
    }

    public void setAddressId(Address addressId) {
        this.addressId = addressId;
    }
}
