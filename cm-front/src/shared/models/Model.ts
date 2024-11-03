import { Color } from "./Color";
import { Product } from "./Product";
import { Size } from "./Size";

export interface Model {
    id?: number;
    name: string;
    description?: string;
    colors: any[];
    sizes: any[];
    products: any[],
    purchasePrice: number;
    earningCoefficient:number;
    deleted: boolean;
    enabled: boolean;
    defaultId?: number;

    selectedSizeReel?:number;
    selectedProduct?:Product;
}

export interface ModelDeleteDTO {
    usedOffersCount: number;
    usedOffersNames: string[];
}