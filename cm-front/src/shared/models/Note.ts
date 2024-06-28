import { Packet } from "./Packet";
import { User } from "./User";

export interface Note {

    id?: number;
    date: Date;
    clientReason?: string;
    explanation?: string;
    status?: string;
    packet?: Packet;
    user?: User;
}