-- Update the 'status' field in the 'packet' table with matching keys from the 'SystemStatus' enum
UPDATE packet
SET status = CASE
                 WHEN status = 'Creation' THEN 'CREATION'
                 WHEN status = 'Livrée' THEN 'DELIVERED'
                 WHEN status = 'Injoignable' THEN 'UNREACHABLE'
                 WHEN status = 'Confirmée' THEN 'CONFIRMED'
                 WHEN status = 'Non confirmée' THEN 'NOT_CONFIRMED'
                 WHEN status = 'En cours (1)' THEN 'IN_PROGRESS_1'
                 WHEN status = 'En cours (2)' THEN 'IN_PROGRESS_2'
                 WHEN status = 'En cours (3)' THEN 'IN_PROGRESS_3'
                 WHEN status = 'A verifier' THEN 'TO_VERIFY'
                 WHEN status = 'Retour' THEN 'RETURN'
                 WHEN status = 'Problème' THEN 'PROBLEM'
                 WHEN status = 'Retour Echange' THEN 'RETOUR_EXCHANGE'
                 WHEN status = 'Retour reçu' THEN 'RETURN_RECEIVED'
                 WHEN status = 'Pas Serieux' THEN 'NOT_SERIOUS'
                 WHEN status = 'Annuler' THEN 'CANCELED'
                 WHEN status = 'Retour Expediteur' THEN 'RETOUR_EXPEDITEUR'
                 WHEN status = 'Payée' THEN 'PAID'
                 WHEN status = 'Echange' THEN 'EXCHANGE'
                 WHEN status = 'Code à barre incorrect' THEN 'INCORRECT_BARCODE'
                 WHEN status = 'Supprimé' THEN 'DELETED'
                 WHEN status = 'En rupture' THEN 'OOS'
    END
WHERE status IN (
                 'Creation', 'Livrée', 'Injoignable', 'Confirmée', 'Non confirmée',
                 'En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier',
                 'Retour', 'Problème', 'Retour Echange', 'Retour reçu', 'Pas Serieux',
                 'Annuler', 'Retour Expediteur', 'Payée', 'Echange',
                 'Code à barre incorrect', 'Supprimé', 'En rupture'
    );

-- Update the 'status' field in the 'packet_status' table with matching keys from the 'SystemStatus' enum
UPDATE packet_status
SET status = CASE
                 WHEN status = 'Creation' THEN 'CREATION'
                 WHEN status = 'Livrée' THEN 'DELIVERED'
                 WHEN status = 'Injoignable' THEN 'UNREACHABLE'
                 WHEN status = 'Confirmée' THEN 'CONFIRMED'
                 WHEN status = 'Non confirmée' THEN 'NOT_CONFIRMED'
                 WHEN status = 'En cours (1)' THEN 'IN_PROGRESS_1'
                 WHEN status = 'En cours (2)' THEN 'IN_PROGRESS_2'
                 WHEN status = 'En cours (3)' THEN 'IN_PROGRESS_3'
                 WHEN status = 'A verifier' THEN 'TO_VERIFY'
                 WHEN status = 'Retour' THEN 'RETURN'
                 WHEN status = 'Problème' THEN 'PROBLEM'
                 WHEN status = 'Retour Echange' THEN 'RETOUR_EXCHANGE'
                 WHEN status = 'Retour reçu' THEN 'RETURN_RECEIVED'
                 WHEN status = 'Pas Serieux' THEN 'NOT_SERIOUS'
                 WHEN status = 'Annuler' THEN 'CANCELED'
                 WHEN status = 'Retour Expediteur' THEN 'RETOUR_EXPEDITEUR'
                 WHEN status = 'Payée' THEN 'PAID'
                 WHEN status = 'Echange' THEN 'EXCHANGE'
                 WHEN status = 'Code à barre incorrect' THEN 'INCORRECT_BARCODE'
                 WHEN status = 'Supprimé' THEN 'DELETED'
                 WHEN status = 'En rupture' THEN 'OOS'
    END
WHERE status IN (
                 'Creation', 'Livrée', 'Injoignable', 'Confirmée', 'Non confirmée',
                 'En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier',
                 'Retour', 'Problème', 'Retour Echange', 'Retour reçu', 'Pas Serieux',
                 'Annuler', 'Retour Expediteur', 'Payée', 'Echange',
                 'Code à barre incorrect', 'Supprimé', 'En rupture'
    );