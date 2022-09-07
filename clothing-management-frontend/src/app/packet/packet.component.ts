import { Component, OnInit } from '@angular/core';
import { PacketsService } from '../services/packets.service';
import { Packet } from '../models/packet';
import { LazyLoadEvent } from 'primeng/api';
import { SelectItem } from 'primeng/api';
import {MessageService} from 'primeng/api';
@Component({
  selector: 'app-packet-model',
  templateUrl: './packet.component.html',
  styleUrls: ['./packet.component.css']
})
export class PacketComponent implements OnInit {
  packets1: Packet[] = [];
  packets2: Packet[] = [];
  statuses: SelectItem[] = [];

  clonedProducts: { [s: string]: Packet; } = {};
  constructor(private packetService: PacketsService ,private messageService: MessageService) { }

  ngOnInit() {
    this.packetService.getProductsSmall().then(data => this.packets1 = data);
    this.packetService.getProductsSmall().then(data => this.packets2 = data);

    this.statuses = [{label: 'In Stock', value: 'INSTOCK'},{label: 'Low Stock', value: 'LOWSTOCK'},{label: 'Out of Stock', value: 'OUTOFSTOCK'}]
}
onRowEditInit(packet: any) {
  console.log(packet)
  this.clonedProducts[packet.id] = {...packet};
}

onRowEditSave(packet: any) {
  if (packet.price > 0) {
      delete this.clonedProducts[packet.id];
      this.messageService.add({severity:'success', summary: 'Success', detail:'Product is updated'});
  }  
  else {
      this.messageService.add({severity:'error', summary: 'Error', detail:'Invalid Price'});
  }
}

}
