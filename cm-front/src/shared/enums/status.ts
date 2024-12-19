export enum Status {
    CREATION = 'Création',
    NOT_CONFIRMED = 'Non confirmée',
    CONFIRMED = 'Confirmée',
    OOS = 'En rupture',
    IN_PROGRESS_1 = 'En cours (1)',
    IN_PROGRESS_2 = 'En cours (2)',
    IN_PROGRESS_3 = 'En cours (3)',
    DELIVERED = 'Livrée',
    PAID = 'Payée',
    TO_VERIFY = 'A verifier',
    RETURN = 'Retour',
    RETURN_RECEIVED = 'Retour reçu',
    DELETED = 'Supprimé',
    BUREAU = 'Bureau',
    IN_PROGRESS = 'En Cours',
    TERMINE = 'Terminé',
    UNREACHABLE = 'Injoignable',
    PROBLEM = 'Problème',
    NOT_SERIOUS = 'Pas Serieux',
    CANCELED = 'Annuler',
    VALIDATION = 'validation',
    EXCHANGE = 'Echange',
    INCORRECT_BARCODE = 'Code à barre incorrect',
    EMPTY = ''
}

export namespace Status {
    export function findByKey(key: string| undefined): any {
        return Status[key as keyof typeof Status];
    }

    export function findByValue(value: string): string | undefined {
        return Object.entries(Status).find(([_, v]) => v === value)?.[0];
    }

    export function values(): string[] {
        return Object.values(Status) as string[];
    }
}
