import { City } from "./City";
import { DeliveryCompany } from "./DeliveryCompany";
import { FbPage } from "./FbPage";
import { Note } from "./Note";

export interface Packet {
    id?: number;
    date?: any;
    customerName?: string;
    customerPhoneNb?: string;
    city?: City;
    fbPage?: any;
    address?: string;
    price?: number;
    deliveryPrice?: number;
    discount?: number;
    relatedProducts?: string;
    packetReference?: string;
    packetDescription?: string;
    status?: any;
    confirmation?: boolean;
    barcode?: any;
    lastDeliveryStatus?: any;
    lastUpdateDate?: any;
    exchangeId?: number;
    printLink?:string;
    oldClient?: number;
    valid?: boolean;
    stock?: number;
    deliveryCompany?: DeliveryCompany;
    notes?: Note[];
    haveExchange?:boolean;
    productCount?:number;
    [key: string]: any;

}
