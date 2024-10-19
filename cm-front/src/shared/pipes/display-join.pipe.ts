import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayJoin'
})
export class DisplayJoinPipe implements PipeTransform {
  transform(input:Array<string>, sep = ', '): string {
    return input.join(sep);
  }
}
