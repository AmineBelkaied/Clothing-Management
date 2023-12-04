import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { PacketService } from 'src/shared/services/packet.service';
import { PAYEE } from 'src/shared/utils/status-list';

@Component({
  selector: 'app-payed-return',
  templateUrl: './payed-return.component.html',
  styleUrls: ['./payed-return.component.scss']
})
export class PayedReturnComponent implements OnInit {
  text: string = ''; // Initialize with the provided text
  extractedBarcodes: string[] = [];
  type: string= PAYEE
  errorMessage :string ="";

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
