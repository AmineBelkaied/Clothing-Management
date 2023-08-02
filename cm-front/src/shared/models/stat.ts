export interface DaySales {
  day: number;
  model: Model[];
}
export interface Model {
  modelName: string;
  modelColors: Colors[];
}
export interface Colors {
  colorName: String;
  sizes: Sizes[];
}
export interface Sizes {
  sizeName: String;
  qte: number;
}



