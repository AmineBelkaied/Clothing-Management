export interface Packet {
    id?: string;
    date?: Date;
    customerName?: string;
    customerPhoneNb?: string;
    governorate?: string;
    address?: string;
    price?: number;
    relatedProducts?: string;
    packetReference?: string;
    confirmation?: boolean;
}