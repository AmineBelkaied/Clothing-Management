import { ModelQuantity } from "./ModelQuantity";

export interface OfferModelDTO {
    offerId?: string;
    name: string;
    price: number;
    enabled?: boolean;
    modelQuantities: ModelQuantity[];
}