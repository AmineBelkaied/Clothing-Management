import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayList'
})
export class DisplayListPipe implements PipeTransform {

  transform(list: any[], field: string, seperator: string): string {
    if (!list || list.length === 0) return "-";

    return list
      .map(list => list[field])
      .filter(item => item != '?')
      .join(seperator + ' ');
  }

}
