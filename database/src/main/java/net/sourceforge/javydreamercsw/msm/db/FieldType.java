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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
@Table(name = "field_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FieldType.findAll", query = "SELECT f FROM FieldType f"),
    @NamedQuery(name = "FieldType.findById", query = "SELECT f FROM FieldType f WHERE f.id = :id"),
    @NamedQuery(name = "FieldType.findByName", query = "SELECT f FROM FieldType f WHERE f.name = :name")})
public class FieldType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "FIELD_TYPE_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "FIELD_TYPE",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FIELD_TYPE_GEN")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fieldTypeId")
    private List<Field> tmfieldList;

    public FieldType() {
    }

    public FieldType(Integer id) {
        this.id = id;
    }

    public FieldType(Integer id, String name) {
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

    @XmlTransient
    public List<Field> getTmfieldList() {
        return tmfieldList;
    }

    public void setTmfieldList(List<Field> tmfieldList) {
        this.tmfieldList = tmfieldList;
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
        if (!(object instanceof FieldType)) {
            return false;
        }
        FieldType other = (FieldType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.FieldType[ id=" + id + " ]";
    }

}
