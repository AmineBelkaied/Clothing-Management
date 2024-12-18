import { ChartIDName } from "./ChartIDName";

export interface ChartDTO {
    uniqueDates :string[];
    uniqueItems: ChartIDName[];
    itemsCount: number[][];
}
