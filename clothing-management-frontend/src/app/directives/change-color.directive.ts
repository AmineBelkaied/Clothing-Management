import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[changeColor]'
})
export class ChangeColorDirective {

  constructor(private element: ElementRef) {
   }

   @HostListener('mouseenter') onMouseEnter() {
    this.element.nativeElement.style.color = 'red';
   }

   @HostListener('mouseleave') onMouseLeave() {
    this.element.nativeElement.style.color = 'black';
   }

}
