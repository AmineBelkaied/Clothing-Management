import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Status } from 'src/shared/enums/status';
import { PacketService } from 'src/shared/services/packet.service';

@Component({
  selector: 'app-paid-return',
  templateUrl: './paid-return.component.html',
  styleUrls: ['./paid-return.component.scss']
})
export class PaidReturnComponent implements OnInit {
  text: string = ''; // Initialize with the provided text
  extractedBarcodes: string[] = [];
  type = Status.PAID;
  errorMessage: string ="";

  constructor(private packetService: PacketService,private messageService: MessageService) {

  }

  ngOnInit(): void {
  }

  extractBarcodes() {
    const barcodeRegex = /\d{12}/g;
    this.extractedBarcodes = this.text.match(barcodeRegex) || [];
    console.log(this.extractedBarcodes);
    console.log(this.type);
    if(this.type==null)
    this.messageService.add({
      severity: 'error',
      summary: 'Erreur',
      detail: "Veiller selectionnée le type",
      life: 3000
    })
    else
    this.packetService.updateStatus(this.extractedBarcodes,this.type)
      .subscribe((result: any) => {
        if(result.length > 0) {
        this.messageService.add({
          severity: 'error',
          summary: 'Erreur',
          detail: this.createErrorMessage(result),
          life: 3000
        },);
        } else {
          this.messageService.add({
            severity: 'success',
            summary: 'Succés',
            detail: "Le status des commandes a été modifié avec succés",
          });
        }
      });
  }

  createErrorMessage(result: string[]) {
    this.errorMessage += "Les code à barres suivants sont introuvables : ";
    result.forEach((element, index) => {
      this.errorMessage += element;
        if(index < result.length - 1)
          this.errorMessage += " , ";
    });
    return this.errorMessage;
  }
}
