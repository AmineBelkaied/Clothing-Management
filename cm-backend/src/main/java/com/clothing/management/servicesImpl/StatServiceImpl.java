package com.clothing.management.servicesImpl;

import com.clothing.management.dto.PacketsStatCountDTO;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.dto.StatTableDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.ModelStockHistory;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.Size;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.repository.IProductsPacketRepository;
import com.clothing.management.services.StatService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatServiceImpl implements StatService {
    private final IProductsPacketRepository productsPacketRepository;
    private final IModelStockHistoryRepository modelStockHistoryRepository;

    public StatServiceImpl(IProductsPacketRepository productsPacketRepository, IModelStockHistoryRepository modelStockHistoryRepository) {
        this.productsPacketRepository = productsPacketRepository;
        this.modelStockHistoryRepository = modelStockHistoryRepository;
    }

    @Override
    public Map<String , List<?>> statAllModelsChart(String beginDate, String endDate){
        //initialisation des lists
        List<Integer> countModelsList;
        ArrayList<Integer> countTotalList = new ArrayList<>();

        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statAllModels(beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingProductsPacket));
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
                for (ProductsDayCountDTO product : existingProductsPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getModelName().equals(uniqueModel))
                        {
                            count+=product.getCountPayed();
                            countRetour+=product.getCountReturn();
                        }
                }
                modelRecap.setSum(count+modelRecap.getSum());
                modelRecap.setRetour(countRetour+modelRecap.getRetour());
                countModelsList.add(count);
            }
            listModelsCount.add(countModelsList);
            modelRecap.setMin(Collections.min(countModelsList));
            modelRecap.setMax(Collections.max(countModelsList));
            modelRecap.setAvg(modelRecap.getSum()/uniqueDates.size());
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
        Map<String, List<?>> uniqueValues = getUnique((existingOffersPacket));
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Offer> uniqueOffers = (List<Offer>) uniqueValues.get("uniqueOffers");

        //stat models chart
        List<List<Integer>> listOffersCount= new ArrayList<>() ;
        List<StatTableDTO> offersRecapCount= new ArrayList<>() ;
        StatTableDTO offerRecap =null;
        for (Offer uniqueoffer : uniqueOffers) {
            countOffersList = new ArrayList<>();
            offerRecap= new StatTableDTO(uniqueoffer.getName());

            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                int countRetour = 0;
                for (ProductsDayCountDTO product : existingOffersPacket) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getOffer().getId()==uniqueoffer.getId())
                    {
                        count+=product.getCountPayed();
                        countRetour+=product.getCountReturn();
                    }
                }
                //System.out.println(uniqueDate+"count:"+count);
                offerRecap.setSum(count+offerRecap.getSum());
                offerRecap.setSum(countRetour+offerRecap.getRetour());
                countOffersList.add(count);
            }
            listOffersCount.add(countOffersList);
            offerRecap.setMin(Collections.min(countOffersList));
            offerRecap.setMax(Collections.max(countOffersList));
            offerRecap.setAvg(offerRecap.getSum()/uniqueDates.size());
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
        StatTableDTO offerTotalRecap = createTableRecap(offersRecapCount);
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
            totalRecap.setSum(totalRecap.getSum()+uniqueRecapCount.getSum());
        }
        return totalRecap;
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
       Map<String, List<?>> uniqueValues = getUnique((existingProductsPacketColor));
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
                for (ProductsDayCountDTO product : existingProductsPacketColor) {
                    if (product.getPacketDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId()))
                        count += product.getCountPayed();
                }if(count==0)colorRecap.setMin(0L);
                colorRecap.setSum(colorRecap.getSum()+count);
                uniqueColorCountList.add(count);
            }
            countColorsLists.add(uniqueColorCountList);
            colorRecap.setMin(Collections.min(uniqueColorCountList));
            colorRecap.setMax(Collections.max(uniqueColorCountList));
            colorRecap.setAvg(colorRecap.getSum()/uniqueDates.size());
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
    public Map<String , List<?>> statAllPacketsChart(String beginDate, String endDate){

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
        List<Long> countInProgress = new ArrayList<>();

        StatTableDTO exchangeRecap= new StatTableDTO(SystemStatus.EXCHANGE.getStatus());
        StatTableDTO returnRecap= new StatTableDTO(SystemStatus.RETURN.getStatus());
        StatTableDTO payedRecap= new StatTableDTO(SystemStatus.PAID.getStatus());
        StatTableDTO oosRecap= new StatTableDTO(SystemStatus.OOS.getStatus());
        StatTableDTO outRecap= new StatTableDTO("Sortie");
        StatTableDTO allRecap= new StatTableDTO("All");
        StatTableDTO inProgressRecap= new StatTableDTO("En Cours");
                for (PacketsStatCountDTO dayStat: existingPackets) {
                    countAll.add(dayStat.getCountAll());
                    countExchange.add(dayStat.getCountExchange());
                    countOut.add(dayStat.getCountOut());
                    countPayed.add(dayStat.getCountPayed());
                    countReturn.add(dayStat.getCountReturn());
                    countOos.add(dayStat.getCountOos());

                    Long inProgress = dayStat.getCountOut()-dayStat.getCountReturn()-dayStat.getCountPayed();
                    countInProgress.add(inProgress);

                    exchangeRecap.setMin(dayStat.getCountExchange()<exchangeRecap.getMin()?dayStat.getCountExchange():exchangeRecap.getMin());
                    exchangeRecap.setMax(dayStat.getCountExchange()>exchangeRecap.getMax()?dayStat.getCountExchange():exchangeRecap.getMax());
                    exchangeRecap.setSum(dayStat.getCountExchange()+exchangeRecap.getSum());

                    outRecap.setMin(dayStat.getCountOut()<outRecap.getMin()?dayStat.getCountOut():outRecap.getMin());
                    outRecap.setMax(dayStat.getCountOut()>outRecap.getMax()?dayStat.getCountOut():outRecap.getMin());
                    outRecap.setSum(dayStat.getCountOut()+outRecap.getSum());

                    allRecap.setMin(dayStat.getCountAll()<allRecap.getMin()?dayStat.getCountAll():allRecap.getMin());
                    allRecap.setMax(dayStat.getCountAll()>allRecap.getMax()?dayStat.getCountAll():allRecap.getMax());
                    allRecap.setSum(dayStat.getCountAll()+allRecap.getSum());

                    payedRecap.setMin(dayStat.getCountPayed()<payedRecap.getMin()?dayStat.getCountPayed():payedRecap.getMin());
                    payedRecap.setMax(dayStat.getCountPayed()>payedRecap.getMax()?dayStat.getCountPayed():payedRecap.getMax());
                    payedRecap.setSum(dayStat.getCountPayed()+payedRecap.getSum());

                    returnRecap.setMin(dayStat.getCountReturn()<returnRecap.getMin()?dayStat.getCountReturn():returnRecap.getMin());
                    returnRecap.setMax(dayStat.getCountReturn()>returnRecap.getMax()?dayStat.getCountReturn():returnRecap.getMax());
                    returnRecap.setSum(dayStat.getCountReturn()+returnRecap.getSum());

                    oosRecap.setMin(dayStat.getCountOos()<oosRecap.getMin()?dayStat.getCountOos():oosRecap.getMin());
                    oosRecap.setMax(dayStat.getCountOos()>oosRecap.getMax()?dayStat.getCountOos():oosRecap.getMax());
                    oosRecap.setSum(dayStat.getCountOos()+oosRecap.getSum());

                    inProgressRecap.setMin(inProgress<inProgressRecap.getMin()?inProgress:inProgressRecap.getMin());
                    inProgressRecap.setMax(inProgress>inProgressRecap.getMax()?inProgress:inProgressRecap.getMax());
                    inProgressRecap.setSum(inProgress+inProgressRecap.getSum());
                    }
                int uniqueDatesSize = uniqueDates.size();
                if( uniqueDatesSize == 0 )
                    uniqueDatesSize = 1;
        allRecap.setAvg(allRecap.getSum()/uniqueDatesSize);
        allRecap.setPer(100L);
        double payedSum = payedRecap.getSum();
        double percentage= 0;
        if (payedSum != 0) {
            percentage = exchangeRecap.getSum()*100/payedSum;
            percentage = Math.round(percentage*10);
            exchangeRecap.setPer(percentage/10);
        }
        double allSum = allRecap.getSum();
        if (allSum != 0) {
            percentage = outRecap.getSum() * 100 / allSum;
            percentage = Math.round(percentage*10);
            outRecap.setPer(percentage/10);
            percentage = oosRecap.getSum()*100/ allSum;
            percentage = Math.round(percentage*10);
            oosRecap.setPer(percentage/10);
        }
        double outSum = outRecap.getSum();
        if (allSum != 0) {
            percentage = payedRecap.getSum() * 100 / outSum;
            percentage = Math.round(percentage*10);
            payedRecap.setPer(percentage/10);
            percentage = returnRecap.getSum()*100/ outSum;
            percentage = Math.round(percentage*10);
            returnRecap.setPer(percentage/10);
        }

        exchangeRecap.setAvg(exchangeRecap.getSum()/uniqueDatesSize);
        outRecap.setAvg(outRecap.getSum()/uniqueDatesSize);
        payedRecap.setAvg(payedRecap.getSum()/uniqueDatesSize);
        returnRecap.setAvg(returnRecap.getSum()/uniqueDatesSize);
        oosRecap.setAvg(oosRecap.getSum()/uniqueDatesSize);
        inProgressRecap.setAvg(inProgressRecap.getSum()/uniqueDatesSize);

        statusRecapCount.add(exchangeRecap);
        statusRecapCount.add(returnRecap);
        statusRecapCount.add(payedRecap);
        statusRecapCount.add(outRecap);
        statusRecapCount.add(inProgressRecap);
        statusRecapCount.add(oosRecap);
        statusRecapCount.add(allRecap);

        statusCountLists.add(countExchange);
        statusCountLists.add(countReturn);
        statusCountLists.add(countPayed);
        statusCountLists.add(countOut);
        statusCountLists.add(countInProgress);
        statusCountLists.add(countOos);
        statusCountLists.add(countAll);

        Map <String , List<?>> data =new HashMap<>();
        data.put("dates",uniqueDates);
        data.put("statusCountLists",statusCountLists);
        data.put("statusRecapCount",statusRecapCount);
        //System.out.println("data"+data);
        return data;
    }

    public static Map<String, List<?>> getUnique(List<ProductsDayCountDTO> productsList) {
        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List< Date > uniqueDates = new ArrayList<>();
        List< Color > uniqueColors = new ArrayList<>();
        List< Size > uniqueSizes = new ArrayList<>();
        List< String > uniqueProductRefs = new ArrayList<>();
        List< String > uniqueModelNames = new ArrayList<>();
        List< Long > uniqueModelIds = new ArrayList<>();
        List< Offer > uniqueOffers = new ArrayList<>();
        List< Long > uniqueOffersIds = new ArrayList<>();

        for (ProductsDayCountDTO product : productsList) {
            Date packetDate = product.getPacketDate();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
            }

            Color color = product.getColor();
            if (!uniqueColors.contains(color)) {
                uniqueColors.add(color);
            }

            String modelName = product.getModelName();
            if (!uniqueModelNames.contains(modelName)) {
                uniqueModelNames.add(modelName);
                uniqueModelIds.add(product.getModelId());
            }
            Offer offer = product.getOffer();
            //System.out.println("looking for offer");
            if (!uniqueOffersIds.contains(offer.getId())) {
                uniqueOffers.add(offer);
                uniqueOffersIds.add(offer.getId());
                //System.out.println("new offer"+offer);
            }
            if(product.getSize()!=null){
                Size size = product.getSize();
                if (!uniqueSizes.contains(size)) {
                    uniqueSizes.add(size);
                }
            }
            if(product.getColor()!=null){
                String productRef = product.getColor().getName()+" "+product.getSize().getReference() ;
                if (!uniqueProductRefs.contains(productRef)) {
                    uniqueProductRefs.add(productRef);
                }
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

        return uniqueAttributes;
    }

    @Override//used in stock(stock par model)
    public Map <String , List<?>> statModelSoldChart(Long modelId,String beginDate,String endDate){
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(modelId,beginDate,endDate);
        Map<String, List<?>> uniqueValues = getUnique((existingProductsPacket));
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Color> uniqueColors = (List<Color>) uniqueValues.get("uniqueColors");
        List<Size> uniqueSizes = (List<Size>) uniqueValues.get("uniqueSizes");
        List<String> uniqueProductRefs = (List<String>) uniqueValues.get("uniqueProductRefs");

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
                    count+=productDto.getCount();
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
    }

    @Override
    public List<ProductsDayCountDTO> productsCountByDate(Long modelId,String beginDate,String endDate){
        return productsPacketRepository.productsCountByDate(modelId, beginDate,endDate);
    }

}
