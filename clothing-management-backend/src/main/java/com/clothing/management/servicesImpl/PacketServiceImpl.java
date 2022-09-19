package com.clothing.management.servicesImpl;

import com.clothing.management.dto.OfferUpdateDTO;
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
        return packetRepository.findAll();
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
           // iterate through all productsRef
           for(String productRef: productsRef) {
               Product product = productRepository.findByReference(productRef);
               if(product != null) {
                   int quantity = product.getQuantity();
                   // reduce product qte
                   product.setQuantity(quantity > 0 ? quantity -1 : 0);
                   productRepository.save(product);
               }
               // add the products of the packet inside historic table
               //productsPacketRepository.save(new ProductsPacket(product, packet, new Date()));
           }
           // set packet price
           packet.setPrice(selectedProductsDTO.getPrice());
           // set related products references
           String relatedProducts = productsRef.stream().collect(Collectors.joining(" , "));
           packet.setRelatedProducts(relatedProducts);
           packet.setPacketReference(selectedProductsDTO.getPacketRef());
           packetRepository.save(packet);
          // findPacketRelatedProducts(packet.getId());
       }
    }

    public List<OfferUpdateDTO> findPacketRelatedProducts(Long idPacket) {
        List<OfferUpdateDTO> offerUpdateDTOList = new ArrayList<>();
        OfferUpdateDTO offerUpdateDTO = null;
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if(optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            String packetReference = packet.getPacketReference();
            //String packetReference= "TE:PUB.XL,PUN.L,SDBLL-TS:SDN.XL,PUN.M";
            System.out.println("packetReference : "  + packetReference);
            //if(packetReference.contains("-")) {
                String[] offers= packetReference.split("-");
            System.out.println("offers : "  + Arrays.toString(offers));
                for(int i=0 ; i < offers.length ; i++) {
                    System.out.println("offersNames : "  + offers[i]);
                    String[] offerProducts =  offers[i].split(":");
                    System.out.println("Arrray");
                    System.out.println(Arrays.toString(offerProducts));
                    Offer offer = offerRepository.findByName(offerProducts[0]);
                    System.out.println("offer : "  + offer.toString());
                    offerUpdateDTO = new OfferUpdateDTO(offer.getId() ,offer.getName() , offer.getPrice());
                    String[] productsRef = offerProducts[1].split(",");
                    List<Product> productList = new ArrayList<>();
                    for(int j=0; j < productsRef.length ; j++) {
                       Product product = productRepository.findByReference(productsRef[j]);
                       if (product == null) {
                           String modelRef = productsRef[j].substring(0,2);
                           System.out.println("modelRef : " + modelRef);
                           Model model = modelRepository.findByReference(modelRef);
                           System.out.println("model : " + model.getName());
                           Color color = new Color();
                           Size size = new Size();
                           if(productsRef[j].charAt(2) != '?') {
                                String colorRef = productsRef[j].substring(2,4);
                               System.out.println("colorRef : " + colorRef);
                                color = colorRepository.findByReference(colorRef);
                               System.out.println("color : " + color.getName());
                               if(productsRef[j].charAt(4) != '?') {
                                   System.out.println("sizee : " + productsRef[j].substring(4 , productsRef[j].length()));
                                   String sizeRef = productsRef[j].substring(4 , productsRef[j].length());
                                   size = sizeRepository.findByReference(sizeRef);
                               }
                           } else {
                               if(productsRef[j].charAt(3) != '?') {
                                   System.out.println("sizee : " + productsRef[j].substring(3 , productsRef[j].length()));
                                   String sizeRef = productsRef[j].substring(3 , productsRef[j].length());
                                   size = sizeRepository.findByReference(sizeRef);
                               }
                           }
                           product = new Product(model, color, size);
                       }
                     /*   List<String> sizes = new ArrayList<>();
                        for(int k=0 ; k <= maxSizeIndex; k++)
                            sizes.add(product.getModel().sizes.get(k));
                        System.out.println("sizes : " + sizes.toString());
                        product.getModel().setSizes(sizes);*/
                       productList.add(product);
                    }
                    offerUpdateDTO.setProducts(productList);
                    offerUpdateDTOList.add(offerUpdateDTO);
                //}
            }
        }
        return offerUpdateDTOList;

        /* String modelRef = models[j].substring(0,1);
        int quantity = StringUtils.countOccurrencesOf(offerModels[1], modelRef);*/
        /*Map<Offer, List<OfferModel>> offerListMap = offerModelRepository.findAll()
                .stream()
                .collect(groupingBy(OfferModel::getOffer));

        List<OfferModelDTO> offerModelListDTO = new ArrayList<>();
        OfferModelDTO offerModelDTO = null;

        for (Offer offer : offerListMap.keySet()) {
            System.out.println("key: " + offer.getName());
            offerModelDTO = new OfferModelDTO(offer.getName() , offer.getPrice());
            List<OfferModel> offerModels= offerListMap.get(offer);
            List<ModelQuantity> modelQuantities = new ArrayList<>();
            for(OfferModel offerModel : offerModels) {
                System.out.println("key: " + offer.getName());
                ModelQuantity modelQuantity = new ModelQuantity(offerModel.getQuantity() , offerModel.getModel());
                modelQuantities.add(modelQuantity);
            }
            offerModelDTO.setModelQuantities(modelQuantities);
            offerModelListDTO.add(offerModelDTO);
        }*/
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
