import { Model } from "./Model";

export interface Offer {
    id?: any;
    name: string;
    price: number;
    models?: Model[];
    quantity?: number;
    enabled?: boolean;
}
