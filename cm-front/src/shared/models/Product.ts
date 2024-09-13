import { Color } from "./Color";
import { Model } from "./Model";
import { Size } from "./Size";

export interface Product {
    id : number;
    name: string;
    deleted:boolean;
    qte:number;
    color:Color;
    size:Size;
    modelId:number;
    model?:Model;
}
