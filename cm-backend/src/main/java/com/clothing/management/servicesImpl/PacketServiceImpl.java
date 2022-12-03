package com.clothing.management.servicesImpl;

import com.clothing.management.dto.OfferUpdateDTO;
import com.clothing.management.dto.PacketDTO;
import com.clothing.management.dto.SelectedProductsDTO;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class PacketServiceImpl implements PacketService {

    @Autowired
    IPacketRepository packetRepository;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IProductsPacketRepository productsPacketRepository;

    @Autowired
    IOfferRepository offerRepository;

    @Autowired
    IModelRepository modelRepository;

    @Autowired
    IColorRepository colorRepository;

    @Autowired
    ISizeRepository sizeRepository;

    @Override
    public List<Packet> findAllPackets() {
        List<Packet> sortedPackets = packetRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Packet::getId).reversed())
                .collect(Collectors.toList());
        return sortedPackets;
    }

    @Override
    public List<Packet> findAllTodaysPackets() {
        return packetRepository.findAllTodayPackets();
    }

    @Override
    public List<Packet> findAllPacketsByDate(Date date) {
        return packetRepository.findAllByDate(date);
    }

    @Override
    public Optional<Packet> findPacketById(Long idPacket) {
        return packetRepository.findById(idPacket);
    }

    @Override
    public Packet addPacket(Packet packet) {
        return packetRepository.save(packet);
    }

    @Override
    public Packet updatePacket(Packet packet) { return packetRepository.save(packet); }

    @Override
    public Packet patchPacket(Long idPacket, Map<String, Object> field) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        Packet packet = null;
        if (optionalPacket.isPresent()) {
            packet = optionalPacket.get();
            Optional<String> firstKeyOptional = field.keySet().stream().findFirst();
            if (firstKeyOptional.isPresent()) {
                String firstKey = firstKeyOptional.get();
                System.out.println("firstKey : " + firstKey);
                Field fieldPacket = ReflectionUtils.findField(Packet.class , (String) firstKey);
                fieldPacket.setAccessible(true);
                System.out.println("fieldPacket : " + fieldPacket.toString());
                System.out.println("field.get(firstKey) : " + field.get(firstKey));
                ReflectionUtils.setField(fieldPacket , packet , field.get(firstKey));
                updatePacket(packet);
                if(firstKey.equals("status") && (field.get(firstKey).equals("En cours (1)") || field.get(firstKey).equals("Retour"))) {
                    // iterate through all productsRef
                       String[] relatedProductRefs =  packet.getRelatedProducts().split(" , ");
                       for (String productRef: relatedProductRefs) {
                        Product product = productRepository.findByReference(productRef);
                        if(product != null) {
                            int quantity = product.getQuantity();
                            // reduce product qte
                            if(field.get(firstKey).equals("En cours (1)"))
                                product.setQuantity(quantity - 1);
                            else
                                product.setQuantity(quantity + 1);
                            product.setDate(new Date());
                            productRepository.save(product);
                        }
                        // add the products of the packet inside historic table
                       // productsPacketRepository.save(new ProductsPacket(product, packet, new Date()));
                    }
                }
            }
        }
        return packet;
    }

    @Override
    public void addProductsToPacket(SelectedProductsDTO selectedProductsDTO) {
        // get products references
       List<String> productsRef = selectedProductsDTO.getProductsRef();
       System.out.println("productsRef " + productsRef.toString());
       Optional<Packet> optionalPacket = packetRepository.findById(selectedProductsDTO.getIdPacket());
       if (optionalPacket.isPresent()) {
           Packet packet = optionalPacket.get();
           System.out.println("packet " + packet);
           // set packet price,deliveryPrice and discount
           packet.setPrice(selectedProductsDTO.getTotalPrice());
           packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
           packet.setDiscount(selectedProductsDTO.getDiscount());
           // set related products references
           String relatedProducts = productsRef.stream().collect(Collectors.joining(" , "));
           packet.setRelatedProducts(relatedProducts);
           packet.setPacketReference(selectedProductsDTO.getPacketRef());
           packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
           packetRepository.save(packet);
          // findPacketRelatedProducts(packet.getId());
       }
    }

    public PacketDTO findPacketRelatedProducts(Long idPacket) {
        PacketDTO packetDTO = new PacketDTO();
        List<OfferUpdateDTO> offerUpdateDTOList = new ArrayList<>();
        OfferUpdateDTO offerUpdateDTO = null;
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if(optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            packetDTO.setTotalPrice(packet.getPrice());
            packetDTO.setDeliveryPrice(packet.getDeliveryPrice());
            packetDTO.setDiscount(packet.getDiscount());
            String packetReference = packet.getPacketReference();
            //String packetReference= "TE:PUB.XL,PUN.L,SDBLL-TS:SDN.XL,PUN.M";
            System.out.println("packetReference : "  + packetReference);
            //if(packetReference.contains("-")) {
            if (packetReference != null) {
                String[] offers = packetReference.split("-");
                System.out.println("offers : " + Arrays.toString(offers));
                if(offers.length > 0) {
                    for (int i = 0; i < offers.length; i++) {
                        String[] offerProducts = offers[i].split(":");
                        if (offerProducts.length > 0) {
                            System.out.println("offer id  : " + offerProducts[0]);
                            Optional<Offer> offer = offerRepository.findById(Long.parseLong(offerProducts[0]));
                            if(offer.isPresent()) {
                                offerUpdateDTO = new OfferUpdateDTO(offer.get().getId(), offer.get().getName(), offer.get().getPrice());
                            }
                            String[] productsRef = offerProducts[1].split(",");
                            if (productsRef.length > 0) {
                            List<Product> productList = new ArrayList<>();
                            for (int j = 0; j < productsRef.length; j++) {
                                Product product = productRepository.findByReference(productsRef[j]);
                                if (product == null) {
                                    String modelRef = productsRef[j].substring(0, 2);
                                    //System.out.println("modelRef : " + modelRef);
                                    Model model = modelRepository.findByReference(modelRef);
                                    //System.out.println("model : " + model.getName());
                                    Color color = new Color();
                                    Size size = new Size();
                                    if (productsRef[j].charAt(2) != '?') {
                                        String colorRef = productsRef[j].substring(2, 4);
                                        // System.out.println("colorRef : " + colorRef);
                                        color = colorRepository.findByReference(colorRef);
                                        //System.out.println("color : " + color.getName());
                                        if (productsRef[j].charAt(4) != '?') {
                                            //System.out.println("sizee : " + productsRef[j].substring(4 , productsRef[j].length()));
                                            String sizeRef = productsRef[j].substring(4, productsRef[j].length());
                                            size = sizeRepository.findByReference(sizeRef);
                                        }
                                    } else {
                                        if (productsRef[j].charAt(3) != '?') {
                                            //System.out.println("sizee : " + productsRef[j].substring(3 , productsRef[j].length()));
                                            String sizeRef = productsRef[j].substring(3, productsRef[j].length());
                                            size = sizeRepository.findByReference(sizeRef);
                                        }
                                    }
                                    product = new Product(model != null ? model : null, color, size);
                                }
                                productList.add(product);
                            }
                            offerUpdateDTO.setProducts(productList);
                        }
                        }
                        offerUpdateDTOList.add(offerUpdateDTO);
                    }
                }
            }
            packetDTO.setOfferUpdateDTOList(offerUpdateDTOList);
        }
        return packetDTO;
    }
    @Override
    public void deletePacketById(Long idPacket) {
        packetRepository.deleteById(idPacket);
    }

    /**
     * Delete selected packets by id
     * @param packetsId
     */
    @Override
    public void deleteSelectedPackets(List<Long> packetsId) {
        packetRepository.deleteAllById(packetsId);
    }


}
