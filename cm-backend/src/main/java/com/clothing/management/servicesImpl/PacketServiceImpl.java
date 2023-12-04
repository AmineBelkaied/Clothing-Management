package com.clothing.management.servicesImpl;
import com.clothing.management.dto.*;
import com.clothing.management.enums.DeliveryCompany;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.enums.FirstStatus;
import com.clothing.management.models.DashboardCard;
import com.clothing.management.repository.repositoryImpl.PacketRepositoryImpl;
import com.clothing.management.servicesImpl.api.FirstApiService;
import com.clothing.management.entities.*;
import com.clothing.management.repository.*;
import com.clothing.management.services.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class PacketServiceImpl implements PacketService {

    private final IPacketRepository packetRepository;
    private final IProductRepository productRepository;
    private final IProductsPacketRepository productsPacketRepository;
    private final IOfferRepository offerRepository;
    private final IModelRepository modelRepository;
    private final IColorRepository colorRepository;
    private final ISizeRepository sizeRepository;
    private final IPacketStatusRepository packetStatusRepository;
    private final FirstApiService firstApiService;
    private final PacketRepositoryImpl packetRepositoryImpl;
    private Packet packet;

    @Autowired
    public PacketServiceImpl(
            IPacketRepository packetRepository,
            IProductRepository productRepository,
            IProductsPacketRepository productsPacketRepository,
            IOfferRepository offerRepository,
            IModelRepository modelRepository,
            IColorRepository colorRepository,
            ISizeRepository sizeRepository,
            IPacketStatusRepository packetStatusRepository,
            FirstApiService firstApiService,
            PacketRepositoryImpl packetRepositoryImpl
    ) {
        this.packetRepository = packetRepository;
        this.productRepository = productRepository;
        this.productsPacketRepository = productsPacketRepository;
        this.offerRepository = offerRepository;
        this.modelRepository = modelRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.packetStatusRepository = packetStatusRepository;
        this.firstApiService = firstApiService;
        this.packetRepositoryImpl = packetRepositoryImpl;
    }
    @Override
    public List<Packet> findAllPackets() {
        List<Packet> sortedPackets = packetRepository.findAll().stream()
                .sorted(Comparator.comparing(Packet::getDate).reversed())
                .collect(Collectors.toList());
        return sortedPackets;
    }

    public Page<Packet> findAllPackets(String searchText, String startDate, String endDate, String status, Pageable pageable,boolean mandatoryDate) {
        return packetRepositoryImpl.findAllPackets(searchText, startDate, endDate, status, pageable,mandatoryDate);
    }

    public List<Packet> findAllPacketsByDate(String startDate, String endDate) {
        return packetRepositoryImpl.findAllPacketsByDate(startDate, endDate);
    }
    @Override
    public Page<Packet> findAllTodaysPackets(Pageable pageable) {
        return packetRepository.findAllTodayPackets(pageable);
    }

    @Override
    public List<Packet> findAllDiggiePackets() {
        return packetRepository.findAllDiggiePackets();
    }

    @Override
    public List<Packet> findAllPacketsByDate(Date date) {
        return packetRepository.findAllByDate(date);
    }

    @Override
    public Optional<Packet> findPacketById(Long idPacket) {
        return packetRepository.findById(idPacket);
    }

    @Transactional
    @Override
    public int deleteEmptyPacket() {
        return packetRepository.deleteEmptyPacket();
    }


    @Override
    public Packet addPacket(Packet packet) {
        packetRepository.save(packet);
        savePacketStatusToHistory(packet,SystemStatus.CREATION.getStatus());
        return packet;
    }

    @Override
    public Packet updatePacket(Packet packet) {
        return packetRepository.save(packet);
    }

    @Override
    public Packet updatePacketValid(String barCode,String type) {
        Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
        if (type.equals(SystemStatus.CONFIRMEE.getStatus())){
            optionalPacket.get().setValid(true);
            return packetRepository.save(optionalPacket.get());
        }
        return updatePacketStatus(optionalPacket.get(), SystemStatus.RETOUR_RECU.getStatus());
    }

    @Override
    public Packet patchPacket(Long idPacket, Map<String, Object> field) throws IOException {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        Packet packet = null;
        if (optionalPacket.isPresent()) {
            packet = optionalPacket.get();
            Optional<String> firstKeyOptional = field.keySet().stream().findFirst();
            if (firstKeyOptional.isPresent()) {
                String firstKey = firstKeyOptional.get();
                Field fieldPacket = ReflectionUtils.findField(Packet.class, (String) firstKey);
                fieldPacket.setAccessible(true);
                System.out.println("firstKey:"+firstKey+"/field.get(firstKey):"+field.get(firstKey));

                if (firstKey.equals("status")) {
                        if (field.get(firstKey).equals(SystemStatus.CONFIRMEE.getStatus()))
                            createBarCode(packet, DeliveryCompany.FIRST.toString());
                    updatePacketStatus(packet, String.valueOf(field.get(firstKey)));
                }else {
                    if (firstKey.equals("customerPhoneNb")) {
                        int existCount =0;
                        if(field.get(firstKey) != "" && field.get(firstKey) != null)
                            existCount = checkPhone(field.get(firstKey)+"");
                        packet.setOldClient(existCount);
                        //System.out.println("nombre de reccurance phone: "+existCount);
                    }
                    ReflectionUtils.setField(fieldPacket, packet, field.get(firstKey));
                    updatePacket(packet);
                }
            }
        }
        return packet;
    }

    @Override
    public void addProductsToPacket(SelectedProductsDTO selectedProductsDTO) {
        List<ProductOfferDTO> productsOffers = selectedProductsDTO.getProductsOffers();
        Optional<Packet> optionalPacket = packetRepository.findById(selectedProductsDTO.getIdPacket());
        if (optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            packet.setPrice(selectedProductsDTO.getTotalPrice());
            packet.setDeliveryPrice(selectedProductsDTO.getDeliveryPrice());
            packet.setDiscount(selectedProductsDTO.getDiscount());
            List<ProductsPacket> existingProductsPacket = productsPacketRepository.findByPacketId(packet.getId());
            if(existingProductsPacket.size() > 0)
                productsPacketRepository.deleteAll(existingProductsPacket);
            List<ProductsPacket> newProductsPacket = new ArrayList<>();
            productsOffers.forEach(productOfferDTO -> {
                newProductsPacket.add(new ProductsPacket(new Product(productOfferDTO.getProductId()), packet, new Date(), new Offer(productOfferDTO.getOfferId()), productOfferDTO.getPacketOfferIndex(),0));
            });
            productsPacketRepository.saveAll(newProductsPacket);
            packet.setPacketDescription(selectedProductsDTO.getPacketDescription());
            updatePacket(packet);
        }
    }

    public PacketDTO findPacketRelatedProducts(Long idPacket) {
        PacketDTO packetDTO = new PacketDTO();
        List<OfferUpdateDTO> offerUpdateDTOList = new ArrayList<>();
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if(optionalPacket.isPresent()) {
            packetDTO.setTotalPrice(optionalPacket.get().getPrice());
            packetDTO.setDeliveryPrice(optionalPacket.get().getDeliveryPrice());
            packetDTO.setDiscount(optionalPacket.get().getDiscount());
            List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(optionalPacket.get().getId());
            if(productsPackets.size() > 0) {
                Map<Integer, List<ProductsPacket>> offerListMap = productsPackets.stream()
                        .collect(groupingBy(ProductsPacket::getPacketOfferId));
                offerListMap.forEach((offer, productsPacket) -> {
                    Offer firstOffer = productsPacket.get(0).getOffer();
                    OfferUpdateDTO offerUpdateDTO = new OfferUpdateDTO(firstOffer.getId(), firstOffer.getName(), firstOffer.getPrice(), firstOffer.isEnabled(), productsPacket.stream().map(productPacket -> {
                        try {
                            return mapToProduct(productPacket.getProduct());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList()));
                    offerUpdateDTOList.add(offerUpdateDTO);
                });
            }
            packetDTO.setOfferUpdateDTOList(offerUpdateDTOList);
        }
        return packetDTO;
    }

    private Product mapToProduct(Product product) throws IOException {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setColor(product.getColor());
        newProduct.setSize(product.getSize());
        //newProduct.setReference(product.getReference());
        newProduct.setModel(mapToModel(product.getModel()));
        newProduct.setQuantity(product.getQuantity());
        newProduct.setDate(product.getDate());
        return newProduct;
    }

    private Model mapToModel(Model model) throws IOException {
        Model newModel = new Model();
        newModel.setId(model.getId());
        newModel.setColors(model.getColors());
        newModel.setSizes(model.getSizes());
        newModel.setDescription(model.getDescription());
        newModel.setName(model.getName());
        newModel.setReference(model.getReference());
        if(model.getImage() != null)
            newModel.setBytes(Files.readAllBytes(new File(model.getImage().getImagePath()).toPath()));
        return newModel;
    }

    @Override
    public void deletePacketById(Long idPacket) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if (optionalPacket.isPresent()) {
            updatePacketStatusAndSaveToHistory(optionalPacket.get(), SystemStatus.DELETED.getStatus());
        }
    }

    /**
     * Delete selected packets by id
     *
     * @param packetsId
     */
    @Override
    public void deleteSelectedPackets(List<Long> packetsId) {
        for (Long packetId : packetsId) {

            // Retrieve the packet with the given ID (Assuming you have a method to fetch the packet by ID)
            Optional<Packet> optionalPacket = packetRepository.findById(packetId);
            Packet packet = null;
            if (optionalPacket.isPresent()) {
                packet= optionalPacket.get();
                if(packet.getCustomerPhoneNb() == null || packet.getCustomerPhoneNb().equals(""))
                    packetRepository.deleteById(packetId);
                else {
                    updatePacketStatusAndSaveToHistory(packet, SystemStatus.DELETED.getStatus());
                }
            }
        }
    }

    @Override
    public int checkPhone(String phoneNumber) {
        return packetRepository.findAllPacketsByPhone_number(phoneNumber);
    }

    @Override
    public List<DashboardCard> createDashboard() {
        return packetRepository.createDashboard();
    }

    @Override
    public List<DashboardCard> syncNotification() {
        return packetRepository.createNotification();
    }

    @Override
    public List<ProductsDayCountDTO> productsCountByDate(Long modelId,String beginDate,String endDate){
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.productsCountByDate(modelId, beginDate,endDate);
        return existingProductsPacket;
    }


    //a reduire***************************************************************************************************
    @Override
    public Map <String , List<?>> statModelSoldChart(Long modelId,String beginDate,String endDate){
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(modelId,beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingProductsPacket));
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<String> uniqueColors = (List<String>) uniqueValues.get("uniqueColors");
        List<String> uniqueSizes = (List<String>) uniqueValues.get("uniqueSizes");
        List<Long> uniqueProductRefs = (List<Long>) uniqueValues.get("uniqueProductRefs");

        //System.out.println("uniqueValues"+uniqueValues);
        List<List<Integer>> listProductsCount= new ArrayList<>() ;
        List<List<Integer>> listColorsCount= new ArrayList<>() ;
        List<List<Integer>> listSizesCount= new ArrayList<>() ;
        List<Integer> countProductsList = new ArrayList<>();
        List<Integer> countColorsList = new ArrayList<>();
        List<Integer> countSizesList = new ArrayList<>();


        for (Long uniqueProductRef : uniqueProductRefs) {
            countProductsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getProductId()== uniqueProductRef)
                        count+=product.getCount();
                }
                countProductsList.add(count);
            }
            listProductsCount.add(countProductsList);
        }
        for (String uniqueColor : uniqueColors) {
            countColorsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getColor().equals(uniqueColor))
                        count+=product.getCount();
                }
                countColorsList.add(count);
            }
            listColorsCount.add(countColorsList);
        }
        for (String uniqueSize: uniqueSizes) {
            countSizesList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getSize().equals(uniqueSize))
                        count+=product.getCount();
                }
                countSizesList.add(count);
            }
            listSizesCount.add( countSizesList);
        }
        Map <String , List<?>> aaa =new HashMap<>();
        aaa.put("sizes",uniqueSizes);
        aaa.put("colors",uniqueColors);
        aaa.put("productRefs",uniqueProductRefs);
        aaa.put("dates",uniqueDates);
        aaa.put("productsCount",listProductsCount);
        aaa.put("sizesCount",listSizesCount);
        aaa.put("colorsCount",listColorsCount);
        return aaa;
    }

    public static Map<String, List<?>> getUnique(List<ProductsDayCountDTO> productsList) {
        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List< Date > uniqueDates = new ArrayList<>();
        List< String > uniqueColors = new ArrayList<>();
        List< String > uniqueSizes = new ArrayList<>();
        List< Long > uniqueProductRefs = new ArrayList<>();
        List< String > uniqueModelNames = new ArrayList<>();

        for (ProductsDayCountDTO product : productsList) {
            Date packetDate = product.getPacketDate();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
            }

            String color = product.getColor();
            if (!uniqueColors.contains(color)) {
                uniqueColors.add(color);
            }

            String modelName = product.getModelName();
            if (!uniqueModelNames.contains(modelName)) {
                uniqueModelNames.add(modelName);
            }

            String size = product.getSize();
            if (!uniqueSizes.contains(size)) {
                uniqueSizes.add(size);
            }

            Long productId = product.getProductId();
            if (!uniqueProductRefs.contains(productId)) {
                uniqueProductRefs.add(productId);
            }
        }

        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniqueColors", uniqueColors);
        uniqueAttributes.put("uniqueModelNames", uniqueModelNames);
        uniqueAttributes.put("uniqueSizes", uniqueSizes);
        uniqueAttributes.put("uniqueProductRefs", uniqueProductRefs);

        return uniqueAttributes;
    }

    @Override
    public Map <String , List<?>> statAllModelsChart(String beginDate,String endDate){
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statAllModels(beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingProductsPacket));
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<String> uniqueModels = (List<String>) uniqueValues.get("uniqueModelNames");

        //System.out.println("uniqueValues"+uniqueValues);
        List<List<Integer>> listModelsCount= new ArrayList<>() ;
        List<Integer> countModelsList = new ArrayList<>();
        ArrayList<Integer> countTotalList = new ArrayList<>();
        for (String uniqueModel : uniqueModels) {
            countModelsList = new ArrayList<>();
            Integer i = 0;
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getModelName().equals(uniqueModel))
                        count+=product.getCount();
                }
                countModelsList.add(count);
                i++;
            }
            listModelsCount.add(countModelsList);
        }
        Integer i = 0;
        for (Date uniqueDate : uniqueDates) {
            Integer sum = 0;
            for (List<Integer> totalPerDay : listModelsCount) {
                sum += totalPerDay.get(i);
            }
            countTotalList.add(sum);
            i++;
        }
        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("models",uniqueModels);
        data.put("modelsCount",listModelsCount);
        data.put("countTotalList",countTotalList);
        return data;
    }

    @Override
    public List<PacketStatus> findPacketTimeLineById(Long idPacket) {
        Optional<Packet> optionalPacket = packetRepository.findById(idPacket);
        if (optionalPacket.isPresent()) {
            Packet packet = optionalPacket.get();
            return packet.getPacketStatus();
        }
        return null;
    }

    @Override
    public DeliveryResponseFirst createBarCode(Packet packet, String deliveryCompany) throws IOException {
        if(deliveryCompany.equals(DeliveryCompany.FIRST.toString())) {
            DeliveryResponseFirst deliveryResponse = this.firstApiService.createBarCode(packet);
            if(deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 ) {
                if(!deliveryResponse.isError()){
                    packet.setPrintLink(deliveryResponse.getResult().getLink());
                    packet.setBarcode(deliveryResponse.getResult().getBarCode());
                    packet.setDate(new Date());
                    updatePacket(packet);
                }
                return deliveryResponse;
            }
        }
        return null;
    }

    @Override
    public Packet getLastStatus(Packet packet, String deliveryCompany) throws Exception {
        if(deliveryCompany.equals(DeliveryCompany.FIRST.toString())) {
            try {
                //System.out.println("packet"+packet);
                DeliveryResponseFirst deliveryResponse = this.firstApiService.getLastStatus(packet.getBarcode());
                if (deliveryResponse.getResponseCode() == 200 || deliveryResponse.getResponseCode() == 201 || deliveryResponse.getResponseCode() == 404) {
                    String systemNewStatus = SystemStatus.A_VERIFIER.getStatus();
                    if (deliveryResponse.getStatus()==404 || deliveryResponse.getResult().getState() == null || deliveryResponse.getResult().getState().equals("")) {
                        throw new Exception("Problem API First");
                    }else if (deliveryResponse.getStatus()>199) {
                        //Convert input from first to System Status
                        //System.out.println(deliveryResponse);
                        savePacketStatusToHistory(packet,"First:"+deliveryResponse.getResult().getState());
                        systemNewStatus = mapFirstToSystemStatus(deliveryResponse.getResult().getState());
                        packet.setLastDeliveryStatus(deliveryResponse.getResult().getState());
                        systemNewStatus =
                                (systemNewStatus.equals(SystemStatus.EN_COURS_1.getStatus())//First always return "en cours"
                                || systemNewStatus.equals(SystemStatus.EN_COURS_2.getStatus())//not in First System
                                || systemNewStatus.equals(SystemStatus.EN_COURS_3.getStatus()))
                                && !packet.getStatus().equals(SystemStatus.PROBLEM.getStatus())//not in First System
                                ? upgradeInProgressStatus(packet) : systemNewStatus;

                    }
                    return updatePacketStatus(packet, systemNewStatus);
                }
            }catch (Exception e ){
                packet.setLastDeliveryStatus(SystemStatus.INCORRECT_BARCODE.getStatus());
                return updatePacketStatusAndSaveToHistory(packet, SystemStatus.A_VERIFIER.getStatus());
            }
        }
        return null;
    }


    private String mapFirstToSystemStatus(String status) {
        if (status == null || status.equals(""))
            return SystemStatus.A_VERIFIER.getStatus();
        FirstStatus firstStatus = FirstStatus.fromString(status);
        switch (firstStatus) {
            case LIVREE:
            case EXCHANGE:
                return SystemStatus.LIVREE.getStatus();
            case RETOUR_EXPEDITEUR:
            case RETOUR_DEFINITIF:
            case RETOUR_CLIENT_AGENCE:
                return SystemStatus.RETOUR.getStatus();
            case EN_ATTENTE:
                return SystemStatus.CONFIRMEE.getStatus();
            case A_VERIFIER:
                return SystemStatus.A_VERIFIER.getStatus();
        }
        return SystemStatus.EN_COURS_1.getStatus();
    }

    private String upgradeInProgressStatus(Packet packet) {
        SystemStatus systemStatus = SystemStatus.fromString(packet.getStatus());
        if ((checkSameDateStatus(packet)
                || packet.getStatus().equals(SystemStatus.A_VERIFIER.getStatus()))
                && !packet.getStatus().equals(SystemStatus.CANCELED.getStatus()))
            return packet.getStatus();
        switch (systemStatus) {
            case EN_COURS_1:
                return SystemStatus.EN_COURS_2.getStatus();
            case EN_COURS_2:
                return SystemStatus.EN_COURS_3.getStatus();
            case EN_COURS_3:
                return SystemStatus.A_VERIFIER.getStatus();
            default:
                return SystemStatus.EN_COURS_1.getStatus();
        }
    }

    private boolean checkSameDateStatus(Packet packet) {
        Date date = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(packet.getLastUpdateDate());
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        return (day1 == day2) && (year1 == year2);
    }

    public Packet duplicatePacket(Long idPacket) {
        Packet packet = packetRepository.findById(idPacket).get();
        Packet newPacket = new Packet();
        if(packet != null) {
            newPacket.setCustomerName(packet.getCustomerName() + "   echange id: " + packet.getId());
            newPacket.setCustomerPhoneNb(packet.getCustomerPhoneNb());
            newPacket.setAddress(packet.getAddress());
            newPacket.setRelatedProducts(packet.getRelatedProducts());
            newPacket.setPacketDescription(packet.getPacketDescription());
            newPacket.setPrice(packet.getPrice());
            newPacket.setDate(new Date());
            newPacket.setStatus("Non confirmée");
            newPacket.setFbPage(packet.getFbPage());
            newPacket.setCity(packet.getCity());
            newPacket.setDeliveryPrice(packet.getDeliveryPrice());
            newPacket.setValid(false);
            newPacket.setExchange(true);
        }
        Packet response = packetRepository.save(newPacket);
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(packet.getId());
        if(productsPackets.size()>0) {
            productsPackets.stream().forEach(productsPacket -> {
                ProductsPacket newProductsPacket = new ProductsPacket(productsPacket.getProduct(), response,productsPacket.getPacketDate(), productsPacket.getOffer(), productsPacket.getPacketOfferId());
                productsPacketRepository.save(newProductsPacket);
            });
        }
        savePacketStatusToHistory(newPacket,SystemStatus.CREATION.getStatus());
        return response;
    }


    public List<String> updatePacketsByBarCodes(BarCodeStatusDTO barCodeStatusDTO) {
        List<String> errors = new ArrayList<>();
        //System.out.println(barCodeStatusDTO);
        String newState = barCodeStatusDTO.getStatus();
        barCodeStatusDTO.getBarCodes().forEach(barCode -> {
            try {
                Optional<Packet> optionalPacket = packetRepository.findByBarCode(barCode);
                if(optionalPacket.isPresent()) {
                    if (!optionalPacket.get().getStatus().equals(SystemStatus.RETOUR_RECU.getStatus())) {
                            updatePacketStatus(optionalPacket.get(), newState);
                    } else {
                        errors.add(barCode + " déja récu");
                    }
                }else { errors.add(barCode + " n'existe pas"); }
            } catch(Exception e){
                errors.add(barCode+ " erreur");
                e.printStackTrace();
            }
        });
        return errors;
    }

    private Packet updatePacketStatus(Packet packet,String status){
        if(packet.isExchange()){
            if (status.equals(SystemStatus.PAYEE.getStatus())||status.equals(SystemStatus.LIVREE.getStatus()))
                return updateExchangePacketStatusToPaid(packet,status);
            if (status.equals(SystemStatus.RETOUR_RECU.getStatus())){
                return updateExchangePacketStatusToReturnReceived(packet);
            }
        }
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    private Packet updateExchangePacketStatusToReturnReceived(Packet packet){
        Long id = getExchangeId(packet);
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        if(packet.getStatus().equals(SystemStatus.LIVREE.getStatus())
                ||packet.getStatus().equals(SystemStatus.PAYEE.getStatus())
                ||optionalPacket.get().getStatus().equals(SystemStatus.RETOUR.getStatus()))
        return updatePacketStatusAndSaveToHistory(optionalPacket.get(), SystemStatus.RETOUR_RECU.getStatus());
        else return updatePacketStatusAndSaveToHistory(packet, SystemStatus.RETOUR_RECU.getStatus());
    }
    private Packet updateExchangePacketStatusToPaid(Packet packet,String status){
        Long id = getExchangeId(packet);
        Optional<Packet> optionalPacket = packetRepository.findById(id);
        packet.setPrice(optionalPacket.get().getPrice()+packet.getPrice()-packet.getDiscount());
        if(!optionalPacket.get().getStatus().equals(SystemStatus.RETOUR_RECU.getStatus()))
            updatePacketStatusAndSaveToHistory(optionalPacket.get(), SystemStatus.RETOUR.getStatus());
        return updatePacketStatusAndSaveToHistory(packet, status);
    }

    private Packet updatePacketStatusAndSaveToHistory(Packet packet, String status) {
        if (
            !packet.getStatus().equals(status)&&
            !(
                packet.getStatus().equals(SystemStatus.RETOUR.getStatus())&&
                !(
                    status.equals(SystemStatus.RETOUR_RECU.getStatus())
                    || status.equals(SystemStatus.PROBLEM.getStatus())
                )
            )
        ){
            updateProducts_Status(packet, status);
            updateProducts_Quantity(packet, status);
            savePacketStatusToHistory(packet,status);
            return  savePacketStatus(packet, status);
        }
        return updatePacket(packet);
    }

    private Packet savePacketStatus(Packet packet, String status) {
        packet.setStatus(status);
        packet.setLastUpdateDate(new Date());
        return updatePacket(packet);
    }


    private void savePacketStatusToHistory(Packet packet, String status) {
        PacketStatus packetStatus = new PacketStatus();
        packetStatus.setPacket(packet);
        packetStatus.setStatus(status);
        packetStatus.setDate(new Date());
        packetStatusRepository.save(packetStatus);
    }
    private void updateProducts_Status(Packet packet,String status){
        if(status.equals(SystemStatus.LIVREE.getStatus())
                ||status.equals(SystemStatus.PAYEE.getStatus())
                ||status.equals(SystemStatus.CONFIRMEE.getStatus())
                ||status.equals(SystemStatus.RETOUR_RECU.getStatus())
                ||status.equals(SystemStatus.CANCELED.getStatus()))
            updateProductsPacket_Status_ByPacketId(packet.getId(),status);
    }

    private void updateProducts_Quantity(Packet packet,String status){
        if (status.equals(SystemStatus.RETOUR_RECU.getStatus())
                ||status.equals(SystemStatus.CONFIRMEE.getStatus())
                ||status.equals(SystemStatus.CANCELED.getStatus()))
            updateProductsQuantity(packet.getId(),status);
    }

    public boolean updateProductsPacket_Status_ByPacketId(Long idPacket,String status) {
        int x = -1;
        if (status.equals(SystemStatus.RETOUR_RECU.getStatus()) || status.equals(SystemStatus.CANCELED.getStatus())) x = 0;
        if (status.equals(SystemStatus.CONFIRMEE.getStatus())) x = 1;
        if (status.equals(SystemStatus.LIVREE.getStatus())||status.equals(SystemStatus.PAYEE.getStatus())) x = 2;

        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(idPacket);
        if(productsPackets.size() > 0) {
            for (ProductsPacket product : productsPackets) {
                product.setStatus(x);
                productsPacketRepository.save(product);
            };
        }else return false;
        return true;
    }


    public boolean updateProductsQuantity(Long idPacket,String status) {
        int quantity = 0;
        if (status.equals(SystemStatus.RETOUR_RECU.getStatus()) || status.equals(SystemStatus.CANCELED.getStatus())) quantity = 1;
        if (status.equals(SystemStatus.CONFIRMEE.getStatus())) quantity = -1;
        List<ProductsPacket> productsPackets = productsPacketRepository.findByPacketId(idPacket);
        if(productsPackets.size() > 0) {
            for (ProductsPacket productsPacket : productsPackets) {
                Optional<Product> product = productRepository.findById(productsPacket.getProduct().getId());
                if (product.isPresent()) {
                    updateProductQuantity(product.get(), quantity);
                }
            }
        }else return false;
        return true;
    }

    private void updateProductQuantity(Product product, int quantityChange) {
        product.setQuantity(product.getQuantity() + quantityChange);
        product.setDate(new Date());
        productRepository.save(product);
    }

    public Long getExchangeId(Packet packet){
        Long id = packet.getId();
        int indexStartOfString = packet.getCustomerName().lastIndexOf("id: ");
        if (indexStartOfString != -1) {
            String idSubstring = packet.getCustomerName().substring(indexStartOfString + 4); // +4 to skip "id: "
            id = Long.valueOf(idSubstring.trim());
        }
        return id;
    }


}
