import { Pipe, PipeTransform } from '@angular/core';
import { SizeService } from '../services/size.service';

@Pipe({
  name: 'displaySize',
})
export class DisplaySizePipe implements PipeTransform {
  constructor(
    private sizeService: SizeService) {}

  transform(
    input: number[]
  ): string[] {
      return input
        .map((item) => {
          const size = this.sizeService.getSizesById(item);
          return size ? size.reference : '';
        });
  }
}
