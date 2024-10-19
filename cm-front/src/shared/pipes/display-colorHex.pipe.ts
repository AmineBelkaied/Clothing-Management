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
    console.log(input);
          const color = this.colorService.getColorById(input);
          console.log(color);

          return color ? color.hex : '';

  }
}
