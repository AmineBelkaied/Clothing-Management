import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from 'src/shared/services/strorage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'clothing-management-frontend';
  isLoggedIn: Observable<boolean>;

  constructor(public storageService: StorageService) {

    
  }
}
