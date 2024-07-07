package com.clothing.management.servicesImpl;

import com.clothing.management.dto.PacketsStatCountDTO;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.dto.StatOfferTableDTO;
import com.clothing.management.dto.StatTableDTO;
import com.clothing.management.entities.*;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.repository.IProductsPacketRepository;
import com.clothing.management.services.PacketService;
import com.clothing.management.services.StatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.sql.SQLOutput;
import java.util.*;

@Service
public class StatServiceImpl implements StatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketService.class);
    private final IProductsPacketRepository productsPacketRepository;
    private final IModelStockHistoryRepository modelStockHistoryRepository;

    public StatServiceImpl(IProductsPacketRepository productsPacketRepository, IModelStockHistoryRepository modelStockHistoryRepository) {
        this.productsPacketRepository = productsPacketRepository;
        this.modelStockHistoryRepository = modelStockHistoryRepository;
    }

    @Override
    public Map<String , List<?>> statAllModelsChart(String beginDate, String endDate, Boolean countProgressEnabler){
        //initialisation des lists
        List<Integer> countModelsList;
        ArrayList<Integer> countTotalList = new ArrayList<>();

        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statAllModels(beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingProductsPacket),false);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<String> uniqueModels = (List<String>) uniqueValues.get("uniqueModelNames");

        //stat models chart
        List<List<Integer>> listModelsCount= new ArrayList<>() ;
        List<StatTableDTO> modelsRecapCount= new ArrayList<>() ;
        StatTableDTO modelRecap =null;
        for (String uniqueModel : uniqueModels) {
            countModelsList = new ArrayList<>();
            modelRecap= new StatTableDTO(uniqueModel);
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                int countRetour = 0;
                int countProgress = 0;
                for (ProductsDayCountDTO row : existingProductsPacket) {
                    if (row.getPacketDate().equals(uniqueDate) && row.getModelName().equals(uniqueModel))
                        {
                            //System.out.println("model-"+row.getModelName()+"/payed:"+row.getCountPayed());

                            if(countProgressEnabler)
                                count+=row.getCountPayed()+row.getCountProgress();

                            else count+=row.getCountPayed();

                            countProgress+=row.getCountProgress();
                            countRetour+=row.getCountReturn();
                        }
                }
                modelRecap.setPayed(count+modelRecap.getPayed());
                modelRecap.setRetour(countRetour+modelRecap.getRetour());
                modelRecap.setProgress(countProgress+modelRecap.getProgress());
                countModelsList.add(count);
            }
            listModelsCount.add(countModelsList);
            modelRecap.setMin(Collections.min(countModelsList));
            modelRecap.setMax(Collections.max(countModelsList));
            modelRecap.setAvg(modelRecap.getPayed()/uniqueDates.size());
            modelsRecapCount.add(modelRecap);
        }

        //total models Count
        Integer i = 0;
        for (Date uniqueDate : uniqueDates) {
            Integer sum = 0;
            for (List<Integer> totalPerDay : listModelsCount) {
                sum += totalPerDay.get(i);
            }
            countTotalList.add(sum);
            i++;
        }
        listModelsCount.add(countTotalList);

        //total recap product table: table recap des models
        StatTableDTO modelTotalRecap = createTableRecap(modelsRecapCount);
        modelsRecapCount.add(modelTotalRecap);
        uniqueModels.add("Total");
        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("models",uniqueModels);
        data.put("modelsCount",listModelsCount);
        data.put("modelsRecapCount",modelsRecapCount);
        return data;
    }

    @Override
    public Map<String , List<?>> statAllStockChart(String beginDate, String endDate){
        //stock history
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statStockByDate(beginDate, endDate);
        Map<String, List<?>> uniqueValues = getUniqueStock((statStock));
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Long> uniqueModelsIds = (List<Long>) uniqueValues.get("modelsIds");
        List<String> uniqueModels = (List<String>) uniqueValues.get("uniqueModelsNames");
        List<List<Long>> modelsStockHistory = new ArrayList<>();
        for (Long uniqueModelId : uniqueModelsIds) {
            ArrayList<Long> uniqueModelStockHistory = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                Long quantity = statStock.stream()
                        .filter(statStockRow -> {
                            try {
                                return statStockRow.getDate().equals(uniqueDate) && statStockRow.getModelId() == uniqueModelId;
                            } catch (Exception e) {
                                throw new RuntimeException("Error parsing date", e);
                            }
                        })
                        .map(ModelStockHistory::getQuantity)
                        .findFirst()
                        .orElse(2L);
                uniqueModelStockHistory.add(quantity);
            }
            modelsStockHistory.add(uniqueModelStockHistory);
        }

        //total modelsTotalStockHistory: calculate les totals par jours des models
        List<Long> modelsTotalStockHistory = new ArrayList<>();
        int i =0;
        for (Date uniqueDate : uniqueDates) {
            Long sum = Long.valueOf(0);
            for (List<Long> modelsTSH : modelsStockHistory) {
                sum += modelsTSH.get(i);
            }
            modelsTotalStockHistory.add(sum);
            i++;
        }
        modelsStockHistory.add(modelsTotalStockHistory);
        uniqueModels.add("Total");
        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("models",uniqueModels);
        data.put("statStock",modelsStockHistory);

        return data;
    }

    public static Map<String, List<?>> getUniqueStock(List<ModelStockHistory> modelList) {
        List<Date> uniqueDates = new ArrayList<>();
        List<Long> uniquemodelsIds = new ArrayList<>();
        List<String> uniqueModelNames = new ArrayList<>();
        for (ModelStockHistory model : modelList) {
            Date modelStockDate = model.getDate();
            if (!uniqueDates.contains(modelStockDate)) {
                uniqueDates.add(modelStockDate);
            }

            if (!uniquemodelsIds.contains(model.getModelId())) {
                uniquemodelsIds.add(model.getModelId());
                uniqueModelNames.add(model.getModelName());
            }
        }
        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("modelsIds", uniquemodelsIds);
        uniqueAttributes.put("uniqueModelsNames", uniqueModelNames);

        return uniqueAttributes;
    }

    @Override
    public Map<String , List<?>> statAllOffersChart(String beginDate, String endDate){
        //initialisation des lists
        List<Integer> countOffersList;
        ArrayList<Integer> countTotalList = new ArrayList<>();

        List<ProductsDayCountDTO> existingOffersPacket = productsPacketRepository.offersCountByDate(beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingOffersPacket),true);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Offer> uniqueOffers = (List<Offer>) uniqueValues.get("uniqueOffers");

        //stat models chart
        List<List<Integer>> listOffersCount= new ArrayList<>() ;
        List<StatOfferTableDTO> offersRecapCount= new ArrayList<>() ;
        StatOfferTableDTO offerRecap =null;
        for (Offer uniqueoffer : uniqueOffers) {
            countOffersList = new ArrayList<>();
            offerRecap= new StatOfferTableDTO(uniqueoffer);

            for (Date uniqueDate : uniqueDates) {
                int countPayed = 0;
                int countProgress = 0;
                int countRetour = 0;

                for (ProductsDayCountDTO row : existingOffersPacket) {
                    if (row.getPacketDate().equals(uniqueDate) && row.getOffer().getId()==uniqueoffer.getId())
                    {
                        if (row.getCountPayed()>0)countPayed+=1;
                        if (row.getCountReturn()>0)countRetour+=1;
                        if (row.getCountProgress()>0)countProgress+=1;
                    }
                }
                //System.out.println(uniqueDate+"count:"+count);
                offerRecap.setPayed(countPayed+offerRecap.getPayed());
                offerRecap.setRetour(countRetour+offerRecap.getRetour());
                offerRecap.setProgress(countProgress+offerRecap.getProgress());
                countOffersList.add(countPayed);
            }
            listOffersCount.add(countOffersList);
            offerRecap.setMin(Collections.min(countOffersList));
            offerRecap.setMax(Collections.max(countOffersList));
            offerRecap.setAvg(offerRecap.getPayed()/uniqueDates.size());
            offerRecap.setSellingPrice(offerRecap.getOffer().getPrice());

            //calculate purshase Price
            Double purshasePrice = 0.0;
            Set<OfferModel> offerModels= uniqueoffer.getOfferModels();
            for (OfferModel uniqueOfferModel : offerModels) {
                purshasePrice +=uniqueOfferModel.getQuantity()*uniqueOfferModel.getModel().getPurchasePrice();
            }
            offerRecap.setPurchasePrice(purshasePrice);


            offersRecapCount.add(offerRecap);
        }



        //total models Count
        //ajout la courbe total des objets calculé precedament
        Integer i = 0;
        for (Date uniqueDate : uniqueDates) {
            Integer sum = 0;
            for (List<Integer> offerPerDay : listOffersCount) {
                sum += offerPerDay.get(i);
            }
            countTotalList.add(sum);
            i++;
        }
        listOffersCount.add(countTotalList);

        //create table
        StatOfferTableDTO offerTotalRecap = createOfferTableRecap(offersRecapCount);
        offersRecapCount.add(offerTotalRecap);
        uniqueOffers.add(new Offer("Total"));

        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("offers",uniqueOffers);
        data.put("countOffersLists",listOffersCount);
        data.put("offersRecapCount",offersRecapCount);
        return data;
    }

    public StatTableDTO createTableRecap(List<StatTableDTO> recapCount){
        StatTableDTO totalRecap= new StatTableDTO("Total");
        totalRecap.setMin(0L);
        for (StatTableDTO uniqueRecapCount : recapCount) {
            totalRecap.setAvg(totalRecap.getAvg()+uniqueRecapCount.getAvg());
            totalRecap.setMax(totalRecap.getMax()+uniqueRecapCount.getMax());
            totalRecap.setMin(totalRecap.getMin()+uniqueRecapCount.getMin());
            totalRecap.setPayed(totalRecap.getPayed()+uniqueRecapCount.getPayed());
            totalRecap.setRetour(totalRecap.getRetour()+uniqueRecapCount.getRetour());
            totalRecap.setProgress(totalRecap.getProgress()+uniqueRecapCount.getProgress());

        }
        return totalRecap;
    }
    public StatOfferTableDTO createOfferTableRecap(List<StatOfferTableDTO> recapCount){
        StatOfferTableDTO totalRecap= new StatOfferTableDTO(new Offer("Total"));
        totalRecap.setMin(0L);
        for (StatOfferTableDTO uniqueRecapCount : recapCount) {
            totalRecap.setAvg(totalRecap.getAvg()+uniqueRecapCount.getAvg());
            totalRecap.setMax(totalRecap.getMax()+uniqueRecapCount.getMax());
            totalRecap.setMin(totalRecap.getMin()+uniqueRecapCount.getMin());
            totalRecap.setPayed(totalRecap.getPayed()+uniqueRecapCount.getPayed());
            totalRecap.setRetour(totalRecap.getRetour()+uniqueRecapCount.getRetour());
            double offerPurshasePrice = uniqueRecapCount.getPurchasePrice()*totalRecap.getPayed();
            LOGGER.info(offerPurshasePrice+"");
            totalRecap.setPurchasePrice(totalRecap.getPurchasePrice()+offerPurshasePrice);
            totalRecap.setSellingPrice(totalRecap.getSellingPrice()+uniqueRecapCount.getSellingPrice()*totalRecap.getPayed());
        }
        return totalRecap;
    }

    private Double calculateOfferPurshasePrice(Offer offer) {
        return 6.0;
    }

    @Override
    public Map<String , List<?>> statAllColorsChart(String beginDate, String endDate,List<Long> lookForModelIds){
        System.out.println(beginDate);
        List<ProductsDayCountDTO> existingProductsPacketColor;
        if(lookForModelIds.isEmpty()){
            existingProductsPacketColor = productsPacketRepository.statAllModelsByColor(beginDate,endDate);
        }else{
            existingProductsPacketColor = productsPacketRepository.statByColorAndModels(beginDate,endDate,lookForModelIds);
        }
       Map<String, List<?>> uniqueValues = getUnique((existingProductsPacketColor),false);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Color> uniqueColors = (List<Color>) uniqueValues.get("uniqueColors");

        List<List<Integer>> countColorsLists = new ArrayList<>();
        List<StatTableDTO> colorsRecapCount= new ArrayList<>() ;
        StatTableDTO colorRecap;
        ArrayList<Integer> uniqueColorCountList;
        for (Color uniqueColor : uniqueColors) {
            uniqueColorCountList = new ArrayList<>();
            colorRecap= new StatTableDTO(uniqueColor.getName());
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                int countRetour = 0;
                int countProgress = 0;
                for (ProductsDayCountDTO product : existingProductsPacketColor) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId()))
                    {
                        count+=product.getCountPayed();
                        countRetour+=product.getCountReturn();
                        countProgress+=product.getCountProgress();
                    }
                }if(count==0)colorRecap.setMin(0L);
                colorRecap.setPayed(colorRecap.getPayed()+count);
                colorRecap.setRetour(countRetour+colorRecap.getRetour());
                colorRecap.setProgress(countProgress+colorRecap.getProgress());
                uniqueColorCountList.add(count);
            }
            countColorsLists.add(uniqueColorCountList);
            colorRecap.setMin(Collections.min(uniqueColorCountList));
            colorRecap.setMax(Collections.max(uniqueColorCountList));
            colorRecap.setAvg(colorRecap.getPayed()/uniqueDates.size());
            colorsRecapCount.add(colorRecap);
        }

        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("colors",uniqueColors);
        data.put("countColorsLists",countColorsLists);
        data.put("colorsRecapCount",colorsRecapCount);
        //System.out.println("data"+data);
        return data;
    }

    public static List<Date> getUniqueDates(List<PacketsStatCountDTO> packetList) {
        List<Date> uniqueDates = new ArrayList<>();
        for (PacketsStatCountDTO packet : packetList) {
            Date packetDate = packet.getDate();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
            }
        }
        return uniqueDates;
    }

    @Override
    public Map<String, List<?>> statAllPacketsChart(String beginDate, String endDate) {
        List<PacketsStatCountDTO> existingPackets = productsPacketRepository.statAllPackets(beginDate, endDate);
        List<Date> uniqueDates = getUniqueDates(existingPackets);

        List<List<Long>> statusCountLists = createStatusCountLists(existingPackets);
        List<StatTableDTO> statusRecapCount = createStatusRecapCount(existingPackets, uniqueDates.size());

        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("statusCountLists", statusCountLists);
        data.put("statusRecapCount", statusRecapCount);

        return data;
    }



    //for packets chart
    private List<List<Long>> createStatusCountLists(List<PacketsStatCountDTO> existingPackets) {
        List<Long> countAll = new ArrayList<>();
        List<Long> countExchange = new ArrayList<>();
        List<Long> countOut = new ArrayList<>();
        List<Long> countPayed = new ArrayList<>();
        List<Long> countReturn = new ArrayList<>();
        List<Long> countOos = new ArrayList<>();
        List<Long> countProgress = new ArrayList<>();

        for (PacketsStatCountDTO dayStat : existingPackets) {
            countAll.add(dayStat.getCountAll());
            countExchange.add(dayStat.getCountExchange());
            countOut.add(dayStat.getCountOut());
            countPayed.add(dayStat.getCountPayed());
            countReturn.add(dayStat.getCountReturn());
            countOos.add(dayStat.getCountOos());
            countProgress.add(dayStat.getCountProgress());
        }

        return Arrays.asList(countExchange, countReturn, countPayed, countOut, countProgress, countOos, countAll);
    }

    //for packets chart
    private List<StatTableDTO> createStatusRecapCount(List<PacketsStatCountDTO> existingPackets, int uniqueDatesSize) {
        StatTableDTO exchangeRecap = new StatTableDTO(SystemStatus.EXCHANGE.getStatus());
        StatTableDTO returnRecap = new StatTableDTO(SystemStatus.RETURN.getStatus());
        StatTableDTO payedRecap = new StatTableDTO(SystemStatus.PAID.getStatus());
        StatTableDTO oosRecap = new StatTableDTO(SystemStatus.OOS.getStatus());
        StatTableDTO outRecap = new StatTableDTO("Sortie");
        StatTableDTO allRecap = new StatTableDTO("All");
        StatTableDTO progressRecap = new StatTableDTO("En Cours");

        for (PacketsStatCountDTO dayStat : existingPackets) {
            updateRecapStats(exchangeRecap, dayStat.getCountExchange());
            updateRecapStats(outRecap, dayStat.getCountOut());
            updateRecapStats(allRecap, dayStat.getCountAll());
            updateRecapStats(payedRecap, dayStat.getCountPayed());
            updateRecapStats(returnRecap, dayStat.getCountReturn());
            updateRecapStats(oosRecap, dayStat.getCountOos());
            updateRecapStats(progressRecap, dayStat.getCountProgress());
        }

        finalizeRecapStats(exchangeRecap, uniqueDatesSize, payedRecap.getPayed());
        finalizeRecapStats(outRecap, uniqueDatesSize, allRecap.getPayed());
        finalizeRecapStats(allRecap, uniqueDatesSize, allRecap.getPayed());
        finalizeRecapStats(payedRecap, uniqueDatesSize, outRecap.getPayed());
        finalizeRecapStats(returnRecap, uniqueDatesSize, outRecap.getPayed());
        finalizeRecapStats(oosRecap, uniqueDatesSize, allRecap.getPayed());
        finalizeRecapStats(progressRecap, uniqueDatesSize, allRecap.getPayed()); // No percentage for progressRecap

        return Arrays.asList(exchangeRecap, returnRecap, payedRecap, outRecap, progressRecap, oosRecap, allRecap);
    }

    //for packets chart
    private void updateRecapStats(StatTableDTO recap, long count) {
        recap.setMin(Math.min(count, recap.getMin()));
        recap.setMax(Math.max(count, recap.getMax()));
        recap.setPayed(count + recap.getPayed());
    }

    //for packets chart
    private void finalizeRecapStats(StatTableDTO recap, int uniqueDatesSize, double total) {
        if (uniqueDatesSize == 0) uniqueDatesSize = 1;
        recap.setAvg(recap.getPayed() / uniqueDatesSize);
        recap.setPer(total == 0 ? 0 : Math.round(recap.getPayed() * 1000.0 / total) / 10.0);
    }


    public static Map<String, List<?>> getUnique(List<ProductsDayCountDTO> productsList,Boolean offerDto
    ) {
        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List< Date > uniqueDates = new ArrayList<>();
        List< Color > uniqueColors = new ArrayList<>();
        List< Size > uniqueSizes = new ArrayList<>();
        List< String > uniqueProductRefs = new ArrayList<>();
        List< String > uniqueModelNames = new ArrayList<>();
        List< Long > uniqueModelIds = new ArrayList<>();
        List< Offer > uniqueOffers = new ArrayList<>();
        List< Long > uniqueOffersIds = new ArrayList<>();
        List< Long > uniqueProductsIds = new ArrayList<>();

        for (ProductsDayCountDTO product : productsList) {
            Date packetDate = product.getPacketDate();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
            }
            if(!offerDto){
                Color color = product.getColor();
                if (!uniqueColors.contains(color)) {
                    uniqueColors.add(color);
                }
                String modelName = product.getModelName();
                if (!uniqueModelNames.contains(modelName)) {
                    uniqueModelNames.add(modelName);
                    uniqueModelIds.add(product.getModelId());
                }
                if(product.getSize()!=null){
                    Size size = product.getSize();
                    if (!uniqueSizes.contains(size)) {
                        uniqueSizes.add(size);
                    }
                }
                if(product.getProductId()!=null){
                    long id = product.getProductId();
                    if (!uniqueProductsIds.contains(id)) {
                        uniqueProductsIds.add(id);
                    }
                }
                if(product.getColor()!=null){
                    String productRef = product.getColor().getName()+" "+product.getSize().getReference() ;
                    if (!uniqueProductRefs.contains(productRef)) {
                        uniqueProductRefs.add(productRef);
                    }
                }
            }



            Offer offer = product.getOffer();
            //System.out.println("looking for offer");
            if (!uniqueOffersIds.contains(offer.getId())) {
                uniqueOffers.add(offer);
                uniqueOffersIds.add(offer.getId());
                //System.out.println("new offer"+offer);
            }


        }

        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniqueColors", uniqueColors);
        uniqueAttributes.put("uniqueModelNames", uniqueModelNames);
        uniqueAttributes.put("uniqueModelIds", uniqueModelIds);
        uniqueAttributes.put("uniqueOffers", uniqueOffers);
        uniqueAttributes.put("uniqueOffersIds", uniqueOffersIds);
        uniqueAttributes.put("uniqueSizes", uniqueSizes);
        uniqueAttributes.put("uniqueProductRefs", uniqueProductRefs);
        uniqueAttributes.put("uniqueProductIds", uniqueProductsIds);

        return uniqueAttributes;
    }
    private Map<String, List<?>> getUniqueValues(List<ProductsDayCountDTO> existingProductsPacket) {
        Map<String, List<?>> uniqueValues = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Color> uniqueColors = new ArrayList<>();
        List<Size> uniqueSizes = new ArrayList<>();
        List<String> uniqueProductRefs = new ArrayList<>();
        List<Long> uniqueProductIds = new ArrayList<>();

        for (ProductsDayCountDTO product : existingProductsPacket) {
            if (!uniqueDates.contains(product.getPacketDate())) uniqueDates.add(product.getPacketDate());
            if (!uniqueColors.contains(product.getColor())) uniqueColors.add(product.getColor());
            if (!uniqueSizes.contains(product.getSize())) uniqueSizes.add(product.getSize());
            String colorAndSize = product.getColor().getName() + " " + product.getSize().getReference();
            if (!uniqueProductRefs.contains(colorAndSize)) uniqueProductRefs.add(colorAndSize);
            if (!uniqueProductIds.contains(product.getProductId())) uniqueProductIds.add(product.getProductId());
        }

        uniqueValues.put("uniqueDates", uniqueDates);
        uniqueValues.put("uniqueColors", uniqueColors);
        uniqueValues.put("uniqueSizes", uniqueSizes);
        uniqueValues.put("uniqueProductRefs", uniqueProductRefs);
        uniqueValues.put("uniqueProductIds", uniqueProductIds);

        return uniqueValues;
    }


    @Override//used in stock(stock par model)
    public Map<String, List<?>> statModelSoldChart(Long modelId, String beginDate, String endDate) {
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(modelId, beginDate, endDate);
        Map<String, List<?>> uniqueValues = getUniqueValues(existingProductsPacket);

        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Color> uniqueColors = (List<Color>) uniqueValues.get("uniqueColors");
        List<Size> uniqueSizes = (List<Size>) uniqueValues.get("uniqueSizes");
        List<String> uniqueProductRefs = (List<String>) uniqueValues.get("uniqueProductRefs");

        List<List<Integer>> listProductsCount = getProductCounts(existingProductsPacket, uniqueProductRefs, uniqueDates);
        List<List<Integer>> listColorsCount = getColorCounts(existingProductsPacket, uniqueColors, uniqueDates);
        List<List<Integer>> listSizesCount = getSizeCounts(existingProductsPacket, uniqueSizes, uniqueDates);

        Map<String, List<?>> dataHashMap = new HashMap<>();
        dataHashMap.put("sizes", uniqueSizes);
        dataHashMap.put("colors", uniqueColors);
        dataHashMap.put("productRefs", uniqueProductRefs);
        dataHashMap.put("dates", uniqueDates);
        dataHashMap.put("productsCount", listProductsCount);
        dataHashMap.put("sizesCount", listSizesCount);
        dataHashMap.put("colorsCount", listColorsCount);

        return dataHashMap;
    }

    private List<List<Integer>> getProductCounts(List<ProductsDayCountDTO> existingProductsPacket, List<String> uniqueProductRefs, List<Date> uniqueDates) {
        List<List<Integer>> listProductsCount = new ArrayList<>();

        for (String uniqueProductRef : uniqueProductRefs) {
            List<Integer> countProductsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO productDto : existingProductsPacket) {
                    String colorAndSize = productDto.getColor().getName() + " " + productDto.getSize().getReference();
                    if (productDto.getPacketDate().equals(uniqueDate) && uniqueProductRef.equals(colorAndSize)) {
                        count += productDto.getCountPayed();
                    }
                }
                countProductsList.add(count);
            }
            listProductsCount.add(countProductsList);
        }

        return listProductsCount;
    }

    private List<List<Integer>> getColorCounts(List<ProductsDayCountDTO> existingProductsPacket, List<Color> uniqueColors, List<Date> uniqueDates) {
        List<List<Integer>> listColorsCount = new ArrayList<>();

        for (Color uniqueColor : uniqueColors) {
            List<Integer> countColorsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId())) {
                        count += product.getCountPayed();
                    }
                }
                countColorsList.add(count);
            }
            listColorsCount.add(countColorsList);
        }

        return listColorsCount;
    }

    private List<List<Integer>> getSizeCounts(List<ProductsDayCountDTO> existingProductsPacket, List<Size> uniqueSizes, List<Date> uniqueDates) {
        List<List<Integer>> listSizesCount = new ArrayList<>();

        for (Size uniqueSize : uniqueSizes) {
            List<Integer> countSizesList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getSize().getId().equals(uniqueSize.getId())) {
                        count += product.getCountPayed();
                    }
                }
                countSizesList.add(count);
            }
            listSizesCount.add(countSizesList);
        }

        return listSizesCount;
    }

    @Override
    public List<ProductsDayCountDTO> productsCountByDate(Long modelId,String beginDate,String endDate){
        return productsPacketRepository.productsCountByDate(modelId, beginDate,endDate);
    }
}
    /*public Map<String , List<?>> statAllPacketsChart(String beginDate, String endDate){

        List<PacketsStatCountDTO> existingPackets = productsPacketRepository.statAllPackets(beginDate,endDate);
        List<Date> uniqueDates = getUniqueDates((existingPackets));

        List<List<Long>> statusCountLists = new ArrayList<>();
        List<StatTableDTO> statusRecapCount= new ArrayList<>() ;

        List<Long> countAll = new ArrayList<>();
        List<Long> countExchange = new ArrayList<>();
        List<Long> countOut = new ArrayList<>();
        List<Long> countPayed = new ArrayList<>();
        List<Long> countReturn = new ArrayList<>();
        List<Long> countOos = new ArrayList<>();
        List<Long> countProgress = new ArrayList<>();

        StatTableDTO exchangeRecap= new StatTableDTO(SystemStatus.EXCHANGE.getStatus());
        StatTableDTO returnRecap= new StatTableDTO(SystemStatus.RETURN.getStatus());
        StatTableDTO payedRecap= new StatTableDTO(SystemStatus.PAID.getStatus());
        StatTableDTO oosRecap= new StatTableDTO(SystemStatus.OOS.getStatus());
        StatTableDTO outRecap= new StatTableDTO("Sortie");
        StatTableDTO allRecap= new StatTableDTO("All");
        StatTableDTO progressRecap= new StatTableDTO("En Cours");
                for (PacketsStatCountDTO dayStat: existingPackets) {
                    countAll.add(dayStat.getCountAll());
                    countExchange.add(dayStat.getCountExchange());
                    countOut.add(dayStat.getCountOut());
                    countPayed.add(dayStat.getCountPayed());
                    countReturn.add(dayStat.getCountReturn());
                    countOos.add(dayStat.getCountOos());
                    countProgress.add(dayStat.getCountProgress());

                    exchangeRecap.setMin(dayStat.getCountExchange()<exchangeRecap.getMin()?dayStat.getCountExchange():exchangeRecap.getMin());
                    exchangeRecap.setMax(dayStat.getCountExchange()>exchangeRecap.getMax()?dayStat.getCountExchange():exchangeRecap.getMax());
                    exchangeRecap.setPayed(dayStat.getCountExchange()+exchangeRecap.getPayed());

                    outRecap.setMin(dayStat.getCountOut()<outRecap.getMin()?dayStat.getCountOut():outRecap.getMin());
                    outRecap.setMax(dayStat.getCountOut()>outRecap.getMax()?dayStat.getCountOut():outRecap.getMin());
                    outRecap.setPayed(dayStat.getCountOut()+outRecap.getPayed());

                    allRecap.setMin(dayStat.getCountAll()<allRecap.getMin()?dayStat.getCountAll():allRecap.getMin());
                    allRecap.setMax(dayStat.getCountAll()>allRecap.getMax()?dayStat.getCountAll():allRecap.getMax());
                    allRecap.setPayed(dayStat.getCountAll()+allRecap.getPayed());

                    payedRecap.setMin(dayStat.getCountPayed()<payedRecap.getMin()?dayStat.getCountPayed():payedRecap.getMin());
                    payedRecap.setMax(dayStat.getCountPayed()>payedRecap.getMax()?dayStat.getCountPayed():payedRecap.getMax());
                    payedRecap.setPayed(dayStat.getCountPayed()+payedRecap.getPayed());

                    returnRecap.setMin(dayStat.getCountReturn()<returnRecap.getMin()?dayStat.getCountReturn():returnRecap.getMin());
                    returnRecap.setMax(dayStat.getCountReturn()>returnRecap.getMax()?dayStat.getCountReturn():returnRecap.getMax());
                    returnRecap.setPayed(dayStat.getCountReturn()+returnRecap.getPayed());

                    oosRecap.setMin(dayStat.getCountOos()<oosRecap.getMin()?dayStat.getCountOos():oosRecap.getMin());
                    oosRecap.setMax(dayStat.getCountOos()>oosRecap.getMax()?dayStat.getCountOos():oosRecap.getMax());
                    oosRecap.setPayed(dayStat.getCountOos()+oosRecap.getPayed());

                    progressRecap.setMin(dayStat.getCountProgress()<progressRecap.getMin()?dayStat.getCountProgress():progressRecap.getMin());
                    progressRecap.setMax(dayStat.getCountProgress()>progressRecap.getMax()?dayStat.getCountProgress():progressRecap.getMax());
                    progressRecap.setPayed(dayStat.getCountProgress()+progressRecap.getPayed());
                    }
                int uniqueDatesSize = uniqueDates.size();
                if( uniqueDatesSize == 0 )
                    uniqueDatesSize = 1;
        allRecap.setAvg(allRecap.getPayed()/uniqueDatesSize);
        allRecap.setPer(100L);
        double payedSum = payedRecap.getPayed();
        double percentage= 0;
        if (payedSum != 0) {
            percentage = exchangeRecap.getPayed()*100/payedSum;
            percentage = Math.round(percentage*10);
            exchangeRecap.setPer(percentage/10);
        }
        double allSum = allRecap.getPayed();
        if (allSum != 0) {
            percentage = outRecap.getPayed() * 100 / allSum;
            percentage = Math.round(percentage*10);
            outRecap.setPer(percentage/10);

            percentage = oosRecap.getPayed()*100/ allSum;
            percentage = Math.round(percentage*10);
            oosRecap.setPer(percentage/10);
        }
        double outSum = outRecap.getPayed();
        if (allSum != 0) {
            percentage = payedRecap.getPayed() * 100 / outSum;
            percentage = Math.round(percentage*10);
            payedRecap.setPer(percentage/10);
            percentage = returnRecap.getPayed()*100/ outSum;
            percentage = Math.round(percentage*10);
            returnRecap.setPer(percentage/10);
        }

        exchangeRecap.setAvg(exchangeRecap.getPayed()/uniqueDatesSize);
        outRecap.setAvg(outRecap.getPayed()/uniqueDatesSize);
        payedRecap.setAvg(payedRecap.getPayed()/uniqueDatesSize);
        returnRecap.setAvg(returnRecap.getPayed()/uniqueDatesSize);
        oosRecap.setAvg(oosRecap.getPayed()/uniqueDatesSize);
        progressRecap.setAvg(progressRecap.getPayed()/uniqueDatesSize);

        statusRecapCount.add(exchangeRecap);
        statusRecapCount.add(returnRecap);
        statusRecapCount.add(payedRecap);
        statusRecapCount.add(outRecap);
        statusRecapCount.add(progressRecap);
        statusRecapCount.add(oosRecap);
        statusRecapCount.add(allRecap);

        statusCountLists.add(countExchange);
        statusCountLists.add(countReturn);
        statusCountLists.add(countPayed);
        statusCountLists.add(countOut);
        statusCountLists.add(countProgress);
        statusCountLists.add(countOos);
        statusCountLists.add(countAll);

        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("statusCountLists",statusCountLists);
        data.put("statusRecapCount",statusRecapCount);
        //System.out.println("data"+data);
        return data;
    }*/
    /*public Map <String , List<?>> statModelSoldChart(Long modelId,String beginDate,String endDate){
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(modelId,beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingProductsPacket),false);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Color> uniqueColors = (List<Color>) uniqueValues.get("uniqueColors");
        List<Size> uniqueSizes = (List<Size>) uniqueValues.get("uniqueSizes");
        List<String> uniqueProductRefs = (List<String>) uniqueValues.get("uniqueProductRefs");
        List<Long> uniqueProductIds = (List<Long>) uniqueValues.get("uniqueProductIds");

        //System.out.println("uniqueValues"+uniqueValues);
        List<List<Integer>> listProductsCount= new ArrayList<>() ;
        List<List<Integer>> listColorsCount= new ArrayList<>() ;
        List<List<Integer>> listSizesCount= new ArrayList<>() ;
        List<Integer> countProductsList = new ArrayList<>();
        List<Integer> countColorsList = new ArrayList<>();
        List<Integer> countSizesList = new ArrayList<>();


        for (String uniqueProductRef : uniqueProductRefs) {
            countProductsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO productDto : existingProductsPacket) {
                    String colorAndSize = productDto.getColor().getName()+" "+productDto.getSize().getReference();
                    if (productDto.getPacketDate().equals(uniqueDate) && uniqueProductRef.equals(colorAndSize))
                    count+=productDto.getCountPayed();
                }
                countProductsList.add(count);
            }
            listProductsCount.add(countProductsList);
        }
        for (Color uniqueColor : uniqueColors) {
            countColorsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId()))
                        count+=product.getCountPayed();
                }
                countColorsList.add(count);
            }
            listColorsCount.add(countColorsList);
        }
        for (Size uniqueSize: uniqueSizes) {
            countSizesList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getSize().getId().equals(uniqueSize.getId()))
                        count+=product.getCountPayed();
                }
                countSizesList.add(count);
            }
            listSizesCount.add( countSizesList);
        }
        Map <String , List<?>> dataHashMap =new HashMap<>();
        dataHashMap.put("sizes",uniqueSizes);
        dataHashMap.put("colors",uniqueColors);
        dataHashMap.put("productRefs",uniqueProductRefs);
        dataHashMap.put("dates",uniqueDates);
        dataHashMap.put("productsCount",listProductsCount);
        dataHashMap.put("sizesCount",listSizesCount);
        dataHashMap.put("colorsCount",listColorsCount);
        return dataHashMap;
    }*/