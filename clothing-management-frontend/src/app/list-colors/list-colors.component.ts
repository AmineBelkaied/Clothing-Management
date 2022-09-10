import { Component, Input, OnInit } from '@angular/core';
import { Color } from '../models/color';
import { ColorService } from '../services/color.service';

@Component({
  selector: 'app-list-colors',
  templateUrl: './list-colors.component.html',
  styleUrls: ['./list-colors.component.css']
})
export class ListColorsComponent implements OnInit {

  colors: Color[] = [];

  constructor(private colorService: ColorService) { }

  ngOnInit(): void {
    this.colorService.colorsSubscriber
    .subscribe((colorList: any) => {
      this.colors = colorList;
    });
  }

  editColor(color: any){
    this.colorService.editColor(color);
    this.colorService.editMode = true;
  }

  deleteColor(color: any)  {

  }
}
