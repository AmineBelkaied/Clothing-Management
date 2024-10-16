export interface ProductCountDTO {
    id:number;//
    packetDate: Date;
    offerId: number;
    modelId: number;
    modelName: string;
    color: string;//
    size: string;//
    countExchange : number;//
    countOos : number;//
    countProgress: number;//
    countPayed: number;//
    count: number;
    qte:number;//
}

export interface SoldProduct {
  countProgress: number;
  countPayed: number;
  id: number;
  countExchange: number;
  countOos: number;
/*   color: number;
  size: number; */
  qte: number;  // Quantity in stock
}

export interface ProductCountByColor {
  [size: number]: SoldProduct;  // Mapping of size to SoldProduct
}

export interface ProductsCount {
  [color: number]: ProductCountByColor;  // Mapping of color to ProductCountByColor
}

