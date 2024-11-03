import { FbPage } from "./FbPage";
import { OfferModelsDTO } from "./OfferModelsDTO";

export interface Offer {
    id: number;
    name: string;
    price: number;
    enabled: boolean;
    offerModels: any[];
    fbPages: any[]; // Pages in request / number in response
    deleted: boolean;
}
