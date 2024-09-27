import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { SideBarService } from 'src/shared/services/sidebar.service';
import { StorageService } from 'src/shared/services/strorage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'clothing-management-frontend';
  isLoggedIn: boolean;
  isSidebarExpanded = false;
  constructor(public sideBarService : SideBarService) {
    this.sideBarService.idExpandedSubscriber().subscribe(isSidebarExpanded => {
      this.isSidebarExpanded = isSidebarExpanded;
    });
  }
}
