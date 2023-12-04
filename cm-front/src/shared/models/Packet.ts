import { City } from "./City";
import { FbPage } from "./FbPage";

export interface Packet {
    id?: string;
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
    exchange?: boolean;
    printLink?:string;
    oldClient?: number;
    valid?: boolean;
}
