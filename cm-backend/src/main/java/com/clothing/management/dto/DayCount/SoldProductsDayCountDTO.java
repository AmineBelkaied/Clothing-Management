package com.clothing.management.dto.DayCount;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoldProductsDayCountDTO {

    private Long id;           // Product ID
    private Long colorId;        // Color ID
    private Long sizeId;         // Size ID
    private long qte;          // Quantity of the product
    private long countPaid;    // Count of "Livrée" and "Payée" statuses
    private long countProgress; // Count of "Confirmée" and progress statuses
    private long countOos;     // Count of "En rupture" status
    private long countExchange; // Count of "Retour" and "Retour reçu" statuses where no exchange ID exists
}
