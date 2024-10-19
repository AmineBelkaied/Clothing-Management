import { Pipe, PipeTransform } from '@angular/core';
import { ColorService } from '../services/color.service';
import { SizeService } from '../services/size.service';
import { FbPageService } from '../services/fb-page.service';

@Pipe({
  name: 'displayFbPage',
})
export class DisplayFbPagePipe implements PipeTransform {
  constructor(
    private fbPageService: FbPageService) {}

  transform(
    input: number[]
  ): string[] {
      return input
        .map((item) => {
          const fbPage = this.fbPageService.getFbPageById(item);
          return fbPage ? fbPage.name : '';
        });
  }
}
