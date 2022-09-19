import { City } from "./City";

export interface Packet {
    id?: string;
    date?: Date;
    customerName?: string;
    customerPhoneNb?: string;
    city?: any;
    address?: string;
    price?: number;
    relatedProducts?: string;
    packetReference?: string;
    confirmation?: boolean;
}