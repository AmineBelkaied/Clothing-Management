import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { PacketService } from 'src/shared/services/packet.service';

@Component({
  selector: 'app-payed-return',
  templateUrl: './payed-return.component.html',
  styleUrls: ['./payed-return.component.scss']
})
export class PayedReturnComponent implements OnInit {
  text: string = ''; // Initialize with the provided text
  extractedBarcodes: string[] = [];
  type!: string;

  constructor(private packetService: PacketService,private messageService: MessageService) {

  }

  ngOnInit(): void {
  }

  extractBarcodes() {
    const barcodeRegex = /\d{12}/g;
    this.extractedBarcodes = this.text.match(barcodeRegex) || [];
    console.log(this.extractedBarcodes);
    console.log(this.type);
    this.packetService.updateStatus(this.extractedBarcodes,this.type)
      .subscribe((result: any) => {
        console.log('result', result);
        /* this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: result,
        }); */
      });
  }
}
