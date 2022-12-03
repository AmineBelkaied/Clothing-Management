import { Governorate } from "./Governorate";

export interface City {
    id? : any;
    name: any;
    postalCode?: any;
    governorate?: Governorate;
}