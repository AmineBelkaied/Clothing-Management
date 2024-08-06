export interface PacketValidationDTO {
    id: number;
    date: Date;
    customerName: string;
    packetDescription: string;
    customerPhoneNb: string;
    barcode: any;
    fbPage: string;
    deliveryCompanyName: string;
    price: number;
    valid: boolean;
    exchangeId: number;
    haveExchange:boolean;
}
