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
@Table(name = "range_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RangeType.findAll", query = "SELECT r FROM RangeType r"),
    @NamedQuery(name = "RangeType.findById", query = "SELECT r FROM RangeType r WHERE r.id = :id"),
    @NamedQuery(name = "RangeType.findByName", query = "SELECT r FROM RangeType r WHERE r.name = :name")})
public class RangeType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "RANGE_TYPE_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "RANGE_TYPE",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RANGE_TYPE_GEN")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rangeTypeId")
    private List<Range> rangeList;

    public RangeType() {
    }

    public RangeType(Integer id) {
        this.id = id;
    }

    public RangeType(Integer id, String name) {
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
    public List<Range> getRangeList() {
        return rangeList;
    }

    public void setRangeList(List<Range> rangeList) {
        this.rangeList = rangeList;
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
        if (!(object instanceof RangeType)) {
            return false;
        }
        RangeType other = (RangeType) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.RangeType[ id=" + id + " ]";
    }

}
