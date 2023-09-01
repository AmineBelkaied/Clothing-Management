import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { PacketService } from 'src/shared/services/packet.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {


  activeClass = false;
  activeRoute = false;

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
        .subscribe((response: any) => {
          console.log(response);

          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'La liste est syncronisé',
            life: 1000,
          });
        });
  }
}
