import { Pipe, PipeTransform } from '@angular/core';
import { ColorService } from '../services/color.service';
import { SizeService } from '../services/size.service';
import { FbPageService } from '../services/fb-page.service';

@Pipe({
  name: 'displayList',
})
export class DisplayListPipe implements PipeTransform {
  constructor(
    private colorService: ColorService, // Inject ColorService
    private sizeService: SizeService, // Inject SizeService (use camelCase)
    private fbPageService: FbPageService) {}


  transform(
    list: any[],
    field: string,
    separator: string,
    entity?: String
  ): string {
    if (!list || list.length === 0) return '-';
    else if (!entity)
      return list
        .map((list) => list[field])
        .filter((item) => item != '')
        .join(separator + ' ');
    else if (entity === 'Color')
      return list
        .map((item) => {
          const color = this.colorService.getColorById(item);
          return color ? color.name : '';
        })
        .join(separator + ' ');
    else if (entity === 'Size')
      return list
        .map((item) => {
          const size = this.sizeService.getSizesById(item);
          return size ? size.reference : '';
        })
        .join(separator + ' ');
    else if (entity === 'FbPages')
      return list
        .map((item) => {
          const fbPage = this.fbPageService.getFbPageById(item);
          return fbPage ? fbPage.name : '';
        })
        .join(separator + ' ');
    return '-';
  }
}
