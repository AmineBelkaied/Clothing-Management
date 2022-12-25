import { City } from "./City";
import { FbPage } from "./FbPage";

export interface Packet {
    id?: string;
    date?: any;
    customerName?: string;
    customerPhoneNb?: string;
    city?: any;
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
    confirmationDate?: any;
}
