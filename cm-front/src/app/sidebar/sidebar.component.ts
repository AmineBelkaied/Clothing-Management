import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { AsyncAction } from 'rxjs/internal/scheduler/AsyncAction';
import { APPNAME } from 'src/assets/constants';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { PacketService } from 'src/shared/services/packet.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  $unsubscribe: Subject<void> = new Subject();
  activeClass = false;
  activeRoute = false;
  appname : String = APPNAME;
  globalConf!: GlobalConf;

  constructor(private packetService: PacketService, private globalConfService: GlobalConfService,private messageService: MessageService) {
   }

  ngOnInit(): void {
    this.activeClass = true;

    //this.globalConfService.globalConf$.subscribe((globalConf: GlobalConf) => this.globalConf = Object.assign({}, globalConf));
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


}
