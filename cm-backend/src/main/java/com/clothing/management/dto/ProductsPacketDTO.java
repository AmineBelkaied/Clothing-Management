package com.clothing.management.dto;

import com.clothing.management.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductsPacketDTO {// correction ----------------------a supprimé
    long id;
    long packetOfferId;
    List<ProductDTO> products;



    public ProductsPacketDTO() {
    }
    public ProductsPacketDTO(ProductsPacket productsPacket) {
        this.id = productsPacket.getOffer().getId();
        this.packetOfferId = productsPacket.getPacketOfferId();
        this.products = null;
    }
    public ProductsPacketDTO(long offerId,long packetOfferId,List<ProductDTO> offerProducts) {
        this.id = offerId;
        this.packetOfferId = packetOfferId;
        this.products = offerProducts;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPacketOfferId() {
        return packetOfferId;
    }

    public void setPacketOfferId(long packetOfferId) {
        this.packetOfferId = packetOfferId;
    }

}
