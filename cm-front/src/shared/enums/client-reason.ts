export enum ClientReason {
    UNREACHABLE = 'UNREACHABLE',
    WILL_CALL_BACK = 'WILL_CALL_BACK',
    PRODUCT_VERIFICATION = 'PRODUCT_VERIFICATION',
    SIZE_VERIFICATION = 'SIZE_VERIFICATION',
    INCORRECT_NUMBER = 'INCORRECT_NUMBER',
    BUSY_NUMBER = 'BUSY_NUMBER',
    PRODUCT_OOS = 'PRODUCT_OOS',
    UNAVAILABLE_SIZE = 'UNAVAILABLE_SIZE'
}

type ClientReasonDetail = {
    label: string;
    description?: string;
    severity: string;
    text: boolean;
    outlined: boolean;
    status: string;
};

export const ClientReasonDetails: { [key in ClientReason] : ClientReasonDetail} = {
    [ClientReason.UNREACHABLE] : { label: 'Injoignable', description: 'Client injoignable', text: true, outlined: false, severity: 'primary', status : 'UNREACHABLE' },
    [ClientReason.WILL_CALL_BACK] : { label: 'Rappellera', description: 'Il va rappeler', text: true, outlined: false, severity: 'success', status : 'UNREACHABLE' },
    [ClientReason.PRODUCT_VERIFICATION] : { label: 'Vérification Produit', description: 'Vérification du produit', text: true, outlined: false, severity: 'secondary', status : 'UNREACHABLE' },
    [ClientReason.SIZE_VERIFICATION] : { label: 'Vérification Taille', description: 'Vérification du taille', text: true, outlined: false, severity: 'info', status : 'UNREACHABLE' },
    [ClientReason.INCORRECT_NUMBER] : { label: 'Num Incorrect', description: 'Téléphone incorrect', text: true, outlined: false, severity: 'warning', status : 'UNREACHABLE' },
    [ClientReason.BUSY_NUMBER] : { label: 'Num Occupé', text: true, description: 'Téléphone occupé', outlined: false, severity: 'danger', status : 'UNREACHABLE' },

    [ClientReason.PRODUCT_OOS] : { label: 'Produit indisponible', description: 'Produit indisponible', text: true, outlined: false, severity: 'danger', status : 'DELETED' },
    [ClientReason.UNAVAILABLE_SIZE] : { label: 'Taille indisponible', description: 'Taille indisponible', text: true, outlined: false, severity: 'warning', status : 'DELETED' }
};