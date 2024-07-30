import { City } from "./City";
import { DeliveryCompany } from "./DeliveryCompany";
import { FbPage } from "./FbPage";
import {Offer} from "./Offer";

export interface Packet {
    id?: number;
    date?: any;
    customerName?: string;
    customerPhoneNb?: string;
    city?: City;
    address?: string;
    packetDescription?: string;
    barcode?: any;
    lastDeliveryStatus?: any;
    oldClient?: number;
    offers: Offer[];
    fbPage?: FbPage;
    deliveryCompany?: DeliveryCompany;
    price: number;
    deliveryPrice: number;
    discount: number;
    status?: any;
    lastUpdateDate?: any;
    printLink?:string;
    valid?: boolean;
    stock?: number;
    attempt?: number;
    note?: string;
    productCount: number;
    exchangeId?: number;
    haveExchange?:boolean;
    //[key: string]: any;

}
