import { Pipe, PipeTransform } from '@angular/core';
import { DeliveryCompanyService } from '../services/delivery-company.service';

@Pipe({
  name: 'displayDeliveryCompany',
})
export class DisplayDeliveryCompanyPipe implements PipeTransform {
  constructor(
    private deliveryCompanyService: DeliveryCompanyService) {}

  transform(
    input: number
  ): string {
          const deliveryCompany = this.deliveryCompanyService.getDeliveryCompanyById(input);
          return deliveryCompany ? deliveryCompany.color : '';
  }
}
