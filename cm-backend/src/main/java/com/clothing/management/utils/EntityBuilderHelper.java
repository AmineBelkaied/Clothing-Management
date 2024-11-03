package com.clothing.management.utils;

import com.clothing.management.entities.*;
import com.clothing.management.enums.ClientReason;
import com.clothing.management.enums.SystemStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class EntityBuilderHelper {

    public Model.ModelBuilder createModelBuilder(String name, List<Product> products, String description, List<Color> colors,
                                              List<Size> sizes, Set<OfferModel> modelOffers, List<ProductHistory> productHistories,
                                              float purchasePrice, double earningCoefficient, boolean deleted) {
        return Model.builder()
                .name(name)
                .products(products)
                .description(description)
                .colors(colors)
                .sizes(sizes)
                .modelOffers(modelOffers)
                .productHistories(productHistories)
                .purchasePrice(purchasePrice)
                .earningCoefficient(earningCoefficient)
                .isDeleted(deleted);
    }

    public Offer.OfferBuilder createOfferBuilder(String name, Set<FbPage> fbPages, Double price, boolean enabled, boolean deleted) {
        return Offer.builder()
                .name(name)
                .fbPages(fbPages)
                .price(price)
                .isEnabled(enabled)
                .isDeleted(deleted);
    }

    public Product.ProductBuilder createProductBuilder(Size size, Color color, long quantity, Date date, Model model) {
        return Product.builder()
                .size(size)
                .color(color)
                .quantity(quantity)
                .date(date)
                .model(model);
    }

    public FbPage.FbPageBuilder createFbPageBuilder(Long id, String name, String link, boolean enabled) {
        return FbPage.builder()
                .id(id)
                .name(name)
                .link(link)
                .enabled(enabled);
    }

    public ProductHistory.ProductHistoryBuilder createProductHistoryBuilder(Product product, Long quantity, Date lastModificationDate, Model model, User user, String comment) {
        return ProductHistory.builder()
                .product(product)
                .quantity(quantity)
                .lastModificationDate(lastModificationDate)
                .model(model)
                .user(user)
                .comment(comment);
    }

    public ProductsPacket.ProductsPacketBuilder createProductsPacketBuilder(Product product, Packet packet, Offer offer, Long packetOfferId, double profits) {
        return ProductsPacket.builder()
                .product(product)
                .packet(packet)
                .offer(offer)
                .packetOfferId(packetOfferId)
                .profits(profits);
    }

    public PacketStatus.PacketStatusBuilder createPacketStatusBuilder(String status, Packet packet, User user) {
        return PacketStatus.builder()
                .status(status)
                .packet(packet)
                .user(user);
    }

    public DeliveryCompany.DeliveryCompanyBuilder createDeliveryCompanyBuilder(String name, String token, String apiName) {
        return DeliveryCompany.builder()
                .name(name)
                .token(token)
                .apiName(apiName);
    }

    public Note.NoteBuilder createNoteBuilder(Long id, Date date, ClientReason clientReason, SystemStatus status, String explanation, Packet packet, User user) {
        return Note.builder()
                .id(id)
                .date(date)
                .clientReason(clientReason)
                .status(status)
                .explanation(explanation)
                .packet(packet)
                .user(user);
    }

}
