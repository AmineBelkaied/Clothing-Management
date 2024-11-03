import { Pipe, PipeTransform } from '@angular/core';
import { ColorService } from '../services/color.service';

@Pipe({
  name: 'displayColorHex',
})
export class DisplayColorHexPipe implements PipeTransform {
  constructor(
    private colorService: ColorService) {}

  transform(
    input: number
  ): string {
          const color = this.colorService.getColorById(input);
          return color ? color.hex : '';

  }
}
