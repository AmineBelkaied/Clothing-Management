import { Color } from "./Color";
import { Product } from "./Product";
import { Size } from "./Size";

export interface Model {
    id?: number;
    name: string;
    description?: string;
    colors: Color[];
    sizes: Size[];
    products: any[],
    purchasePrice: number;
    earningCoefficient:number;
    deleted: boolean;
    enabled: boolean;

    selectedColor?:Color;
    selectedSize?:Size;
    selectedSizeReel?:Size;
    selectedProduct?:Product;
}
