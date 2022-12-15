import { City } from "./City";
import { FbPage } from "./FbPage";

export interface ExpressItem {
    api_key: string,
    destinataire?: string,
    user_name?: string,
    date_enlevement?: string,
    date_livraison?: string,
    adresse_de_livraison?: string,
    gouvernorat_livraison?: string,
    telephone_de_contact_livraison?: string,
    code_postal_livraison?: string,
    nombre_de_colis: number
}
