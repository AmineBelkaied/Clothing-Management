import { Governorate } from "./Governorate";

export interface City {
    id? : any;
    name?: string;
    postalCode?: string;
    governorate?: Governorate;
}
