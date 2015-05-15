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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "tmfield")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TMField.findAll", query = "SELECT t FROM TMField t"),
    @NamedQuery(name = "TMField.findById", query = "SELECT t FROM TMField t WHERE t.id = :id"),
    @NamedQuery(name = "TMField.findByName", query = "SELECT t FROM TMField t WHERE t.name = :name")})
public class TMField implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @TableGenerator(name = "TMFIELD_GEN",
            table = "SEQUENCES",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_NUMBER",
            pkColumnValue = "TMFIELD",
            allocationSize = 1,
            initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TMFIELD_GEN")
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @JoinColumn(name = "field_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FieldType fieldTypeId;
    @JoinColumn(name = "range_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Range rangeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tmfieldId")
    private List<InstanceField> instanceFieldList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tmfield")
    private List<ServiceHasField> serviceHasFieldList;
    @Lob
    @Column(name = "desc")
    private byte[] desc;
    @ManyToMany(mappedBy = "tMFieldList")
    private List<Range> rangeList;

    public TMField() {
    }

    public TMField(Integer id) {
        this.id = id;
    }

    public TMField(Integer id, String name) {
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

    public FieldType getFieldTypeId() {
        return fieldTypeId;
    }

    public void setFieldTypeId(FieldType fieldTypeId) {
        this.fieldTypeId = fieldTypeId;
    }

    public Range getRangeId() {
        return rangeId;
    }

    public void setRangeId(Range rangeId) {
        this.rangeId = rangeId;
    }

    @XmlTransient
    public List<InstanceField> getInstanceFieldList() {
        return instanceFieldList;
    }

    public void setInstanceFieldList(List<InstanceField> instanceFieldList) {
        this.instanceFieldList = instanceFieldList;
    }

    @XmlTransient
    public List<ServiceHasField> getServiceHasFieldList() {
        return serviceHasFieldList;
    }

    public void setServiceHasFieldList(List<ServiceHasField> serviceHasFieldList) {
        this.serviceHasFieldList = serviceHasFieldList;
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
        if (!(object instanceof TMField)) {
            return false;
        }
        TMField other = (TMField) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.TMField[ id=" + id + " ]";
    }

    public byte[] getDesc() {
        return desc;
    }

    public void setDesc(byte[] desc) {
        this.desc = desc;
    }

    @XmlTransient
    public List<Range> getRangeList() {
        return rangeList;
    }

    public void setRangeList(List<Range> rangeList) {
        this.rangeList = rangeList;
    }
}
