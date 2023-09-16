import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { APPNAME } from 'src/assets/constants';
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

  constructor(private packetService: PacketService,private messageService: MessageService) { }

  ngOnInit(): void {
    this.activeClass = true;
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


}
