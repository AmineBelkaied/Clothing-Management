import { Color } from "./Color";
import { Product } from "./Product";
import { Size } from "./Size";

export interface Model {
    id?: number;
    name: string;
    description?: string;
    colors: number[];
    sizes: number[];
    products: any[],
    purchasePrice: number;
    earningCoefficient:number;
    deleted: boolean;
    enabled: boolean;
    defaultId?: number;

    selectedColor?:number;
    selectedSize?:number;
    selectedSizeReel?:number;
    selectedProduct?:Product;
}
