/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class ServiceHasFieldPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_id")
    private int serviceId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "field_id")
    private int fieldId;

    public ServiceHasFieldPK() {
    }

    public ServiceHasFieldPK(int serviceId, int fieldId) {
        this.serviceId = serviceId;
        this.fieldId = fieldId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) serviceId;
        hash += (int) fieldId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceHasFieldPK)) {
            return false;
        }
        ServiceHasFieldPK other = (ServiceHasFieldPK) object;
        if (this.serviceId != other.serviceId) {
            return false;
        }
        if (this.fieldId != other.fieldId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.sourceforge.javydreamercsw.msm.db.ServiceHasFieldPK[ serviceId=" + serviceId + ", fieldId=" + fieldId + " ]";
    }
    
}
