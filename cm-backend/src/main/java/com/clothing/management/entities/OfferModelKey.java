package com.clothing.management.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OfferModelKey implements Serializable {

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "offer_id")
    private Long offerId;

    public OfferModelKey() {
    }

    public OfferModelKey(Long modelId, Long offerId) {
        this.modelId = modelId;
        this.offerId = offerId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OfferModelKey)) return false;
        OfferModelKey that = (OfferModelKey) o;
        return Objects.equals(getModelId(), that.getModelId()) &&
                Objects.equals(getOfferId(), that.getOfferId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModelId(), getOfferId());
    }

    @Override
    public String toString() {
        return "OfferModelKey{" +
                "modelId=" + modelId +
                ", offerId=" + offerId +
                '}';
    }
}
