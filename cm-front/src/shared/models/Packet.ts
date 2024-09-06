import { City } from "./City";
import { DeliveryCompany } from "./DeliveryCompany";
import { FbPage } from "./FbPage";
import { Note } from "./Note";
import {Offer} from "./Offer";

export interface Packet {
    id?: number;
    date?: any;
    customerName?: string;
    customerPhoneNb?: string;
    cityId?: number;
    address?: string;
    packetDescription?: string;
    barcode?: String;
    lastDeliveryStatus?: String;
    oldClient?: number;
    offers: Offer[];//??
    fbPage?: FbPage;
    deliveryCompanyName?: String;
    price: number;
    deliveryPrice: number;
    discount: number;
    status?: String;
    lastUpdateDate?: Date;
    printLink?:String;
    valid?: boolean;
    stock?: number;
    notes: Note[];
    lastNote?: Note;
    note?: String;
    productCount: number;
    exchangeId?: number;
    haveExchange?:boolean;
    //[key: string]: any;

}
