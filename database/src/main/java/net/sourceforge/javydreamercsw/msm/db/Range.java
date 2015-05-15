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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "range")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Range.findAll", query = "SELECT r FROM Range r"),
    @NamedQuery(name = "Range.findById", query = "SELECT r FROM Range r WHERE r.id = :id"),
    @NamedQuery(name = "Range.findByMin", query = "SELECT r FROM Range r WHERE r.min = :min"),
    @NamedQuery(name = "Range.findByMax", query = "SELECT r FROM Range r WHERE r.max = :max")})
public class Range implements Serializable {
    @JoinTable(name = "tmfield_has_range", joinColumns = {
        @JoinColumn(name = "range_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "tmfield_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Field> tMFieldList;

    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "RANGE_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "RANGE",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RANGE_GEN")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "min")
    private float min;
    @Basic(optional = false)
    @NotNull
    @Column(name = "max")
    private float max;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rangeId")
    private List<Field> tmfieldList;
    @JoinColumn(name = "range_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RangeType rangeTypeId;

    public Range() {
    }

    public Range(Integer id) {
        this.id = id;
    }

    public Range(Integer id, float min, float max) {
        this.id = id;
        this.min = min;
        this.max = max;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    @XmlTransient
    public List<Field> getTmfieldList() {
        return tmfieldList;
    }

    public void setTmfieldList(List<Field> tmfieldList) {
        this.tmfieldList = tmfieldList;
    }

    public RangeType getRangeTypeId() {
        return rangeTypeId;
    }

    public void setRangeTypeId(RangeType rangeTypeId) {
        this.rangeTypeId = rangeTypeId;
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
        if (!(object instanceof Range)) {
            return false;
        }
        Range other = (Range) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.Range[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Field> getTMFieldList() {
        return tMFieldList;
    }

    public void setTMFieldList(List<Field> tMFieldList) {
        this.tMFieldList = tMFieldList;
    }

}
