import { Pipe, PipeTransform } from '@angular/core';
import { ColorService } from '../services/color.service';

@Pipe({
  name: 'displayColor',
})
export class DisplayColorPipe implements PipeTransform {
  constructor(
    private colorService: ColorService) {
    }


  transform(
    input: number[]
  ): string[] {
      return input
        .map((item) => {
          const color = this.colorService.getColorById(item);
          return color ? color.name : '';
        });
  }
}
