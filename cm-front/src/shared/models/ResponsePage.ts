import { Packet } from "./Packet";

export interface ResponsePage {

    result: Packet[];
    currentPage: number;
    totalItems: number;
    totalPages: number;
}