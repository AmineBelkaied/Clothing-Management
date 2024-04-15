import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { AsyncAction } from 'rxjs/internal/scheduler/AsyncAction';
import { APPNAME } from 'src/assets/constants';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
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
  isSuperAdmin: boolean;
  globalConf!: GlobalConf;
  constructor(private packetService: PacketService,private messageService: MessageService,
              private globalConfService: GlobalConfService, private router: Router,  public storageService: StorageService) {
   }

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      this.storageService.getTenantName() === "diggie" ? this.appName = DIGGIE : this.appName = LYFT;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
      this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
    });

    this.activeClass = true;
    this.globalConfService.globalConf$.subscribe((globalConf: GlobalConf) => this.globalConf = Object.assign({}, globalConf));
  }

  changeClass() {
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
      this.isSuperAdmin ? this.router.navigate(["/auth/login/"]) : this.router.navigate(["/auth/login/" + this.storageService.getTenantName()]);
      this.storageService.removeUser();
    }

}
