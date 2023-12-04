import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DIGGIE, LYFT } from 'src/assets/constants';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  $unsubscribe: Subject<void> = new Subject();
  activeClass: boolean;
  activeRoute = false;
  appName: string;
  userName: string;
  isLoggedIn: boolean;
  isAdmin: boolean;
  constructor(private packetService: PacketService,private messageService: MessageService,
     private router: Router, public storageService: StorageService) { }

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => { 
      this.isLoggedIn = isLoggedIn;
      this.storageService.getTenantName() === "diggie" ? this.appName = DIGGIE : this.appName = LYFT;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
    });

  }

  changeClass() {
/*     const button = document.getElementById('sidebarCollapse');
    if (button) {
      button.style.left = this.activeClass ? '9rem' : '-2.5rem';
    } */
    this.activeClass = !this.activeClass;
    
  }

  SyncFirst() {
    console.log("syncronising");

    this.packetService
        .syncAllPacketsFirst()
        .pipe(takeUntil(this.$unsubscribe))
        .subscribe({
          next: (response: number) => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: response+" packets synchronised",
              life: 1000,
            });
          },
          error: (error: Error) => {
            console.log('SyncError:', error);
          }
        });
    }

    logout() {
      this.storageService.isLoggedIn.next(false);
      this.router.navigate(["/auth/login/" + this.storageService.getTenantName()]);
      this.storageService.removeUser();
    }

}
