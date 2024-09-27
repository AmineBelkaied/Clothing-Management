import { Pipe, PipeTransform } from '@angular/core';
import { OfferModelsDTO } from '../models/OfferModelsDTO';

@Pipe({
  name: 'displayOfferModels'
})
export class DisplayOfferModelsPipe implements PipeTransform {

  transform(modelQuantities: OfferModelsDTO[]): string {
    if (!modelQuantities || modelQuantities.length === 0) return 'Pas de modèles disponible';

    return modelQuantities
      .map(modelQuantity => `${modelQuantity.quantity} ${modelQuantity.model.name}`)
      .join(', ');
  }

}
