import { City } from "./City";
import { FbPage } from "./FbPage";

export interface Packet {
    id?: string;
    date?: Date;
    customerName?: string;
    customerPhoneNb?: string;
    city?: any;
    fbPage?: any;
    address?: string;
    price?: number;
    relatedProducts?: string;
    packetReference?: string;
    status: any;
    confirmation?: boolean;
}