import { Component, Input, OnInit } from '@angular/core';
import { Color } from '../models/color';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {

  constructor() { }
  @Input() color: Color = {
    'id': '',
    'name' : '',
    'reference': ''
  }
  ngOnInit(): void {
  }

}
