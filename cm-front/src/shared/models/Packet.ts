import { City } from "./City";
import { DeliveryCompany } from "./DeliveryCompany";
import { FbPage } from "./FbPage";
import { Note } from "./Note";
import {Offer} from "./Offer";

export interface Packet {
    id?: number;
    date?: string;
    customerName?: string;
    customerPhoneNb?: string;
    cityId?: number;
    address?: string;
    packetDescription?: string;
    barcode?: string;
    lastDeliveryStatus?: String;
    oldClient?: number;
    offers: Offer[];//??
    fbPageId?: number;
    deliveryCompanyId?: number;
    deliveryPrice: number;
    totalPrice?: number;
    discount: number;
    status?: string;
    lastUpdateDate?: Date;
    printLink?:string;
    valid?: boolean;
    stock?: number;
    notes: Note[];
    lastNote?: Note;
    note?: string;
    productCount: number;
    exchangeId?: number;
    haveExchange?:boolean;
    cityName: string;
    [key: string]: any;//to ignore packet[][] error

}
