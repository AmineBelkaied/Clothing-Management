import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  activeClass = false;
  activeRoute = false;

  constructor() { }

  ngOnInit(): void {
    this.activeClass = true;
  }

  changeClass() {
    this.activeClass = !this.activeClass;
  }

}
