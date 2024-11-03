import { Color } from "./Color";
import { Model } from "./Model";
import { Size } from "./Size";

export interface Product {
    id : number;
    name: string;
    deleted:boolean;
    qte:number;
    colorId:number;
    sizeId:number;
    modelId:number;
    model?:Model;
}
