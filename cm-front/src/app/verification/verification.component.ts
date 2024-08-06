import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject, of, takeUntil } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { PacketValidationDTO } from 'src/shared/models/PacketValidationDTO';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { RETURN, VALIDATION } from 'src/shared/utils/status-list';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.scss']
})
export class VerificationComponent implements OnInit {

  sourcePackets: PacketValidationDTO[] = [];

  targetPackets: PacketValidationDTO[] = [];

  barCode : String;

  $unsubscribe: Subject<void> = new Subject();

  packets: PacketValidationDTO[];
  totalItems: number;
  packet: Packet;
  type : string = VALIDATION;
  sourceString : string = "Non Validé";
  targetString : string = "Validé";
  isAdmin: boolean;
  packetSameBarCode : PacketValidationDTO[];

  constructor(private packetService : PacketService,private dateUtils: DateUtils,private messageService: MessageService,public storageService: StorageService) {

  }

  ngOnInit(): void {
    this.isAdmin = this.storageService.hasRoleAdmin();
    this.findAllConfirmedPackets();
    this.targetPackets = [];
}

findAllConfirmedPackets(): void {
  if(this.type == VALIDATION){
    this.sourceString = "Non validé";
    this.targetString = "Validé";
  }
  else {
    this.sourceString = RETURN;
    this.targetString = "Retour Echange";
  }
  this.packetService.findValidationPackets()
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        this.packets = response.result;

        if(this.type == VALIDATION){
          this.sourcePackets = this.packets.filter((packet: PacketValidationDTO) => packet.valid == false);
          this.targetPackets = this.packets.filter((packet: PacketValidationDTO) => packet.valid);
        }
        else {
          this.sourcePackets = this.packets.filter((packet: PacketValidationDTO) => packet.exchangeId == null);
          this.targetPackets = this.packets.filter((packet: PacketValidationDTO) => packet.exchangeId);
        }
      },
      error: (error: Error) => {
        console.log('Error:', error);
      }
    });
}

Validate(){
  let lastNineCharacters = this.barCode.slice(-9);
  let num: number = Number(this.barCode);
  if (this.type == VALIDATION){
    if(this.barCode.length > 8){

      if (!(this.sourcePackets.map((packet : PacketValidationDTO) => packet.barcode.slice(-9)).indexOf(lastNineCharacters) > -1)){
        if (this.targetPackets.map((packet : PacketValidationDTO) => packet.barcode.slice(-9)).indexOf(lastNineCharacters) > -1){
          alert('Error: BarreCode déja validé');
        } else alert("Error: BarreCode n'existe pas");
        return;
      }
      else if (this.targetPackets.map((packet : PacketValidationDTO) => packet.barcode.slice(-9)).indexOf(lastNineCharacters) > -1){
        alert('Error: Colie double et déja validé');
        return;
      }
    }
    if(this.barCode.length < 9){
      if (!(this.sourcePackets.map((packet : PacketValidationDTO) => packet.id).indexOf(num) > -1)){
        if (this.targetPackets.map((packet : PacketValidationDTO) => packet.id).indexOf(num) > -1){
          alert('Error: BarreCode déja validé');
        } else alert("Error: BarreCode n'existe pas");
        return;
      }
      else if (this.targetPackets.map((packet : PacketValidationDTO) => packet.id).indexOf(num) > -1){
        alert('Error: Colie double et déja validé');
        return;
      }
    }
    if(this.barCode.length > 8){
      this.packetSameBarCode = this.sourcePackets.filter((packet : PacketValidationDTO) => packet.barcode.slice(-9) == lastNineCharacters);
      this.barCode = this.packetSameBarCode[0].barcode;
    }
    else {
      this.packetSameBarCode = this.sourcePackets.filter((packet : PacketValidationDTO) => packet.id == num);
      this.barCode = this.packetSameBarCode[0].barcode;
    }

    if (this.packetSameBarCode.length>1){
      alert('Error: le code a barre '+this.barCode +' existe plusieur fois');
      return;
    }
    else {
      let phoneNumber = this.packetSameBarCode[0].customerPhoneNb;
      let packetSamePhoneNumber : PacketValidationDTO[] = this.sourcePackets.filter((packet : PacketValidationDTO) => packet.customerPhoneNb == phoneNumber);
      if (packetSamePhoneNumber.length>1)
    {
      alert('Error: le numero de telephone '+phoneNumber +' existe plusieur fois');
      if(!this.isAdmin)
      return;
    }}
  }
     this.packetService.validatePacket(this.barCode,this.type).subscribe(response => {
      // Handle the response here
      this.messageService.add({ severity: 'success', summary: 'Success', detail: "code à barre validé: " + this.barCode });
      console.log('response',response);

      if(this.type == VALIDATION){
        const packetIndex = this.sourcePackets.findIndex((packet: PacketValidationDTO) => packet.barcode === this.barCode);
        console.log("packetIndex",packetIndex);

        if (packetIndex !== -1) {
          // Remove the packet from sourcePackets
          const [packet] = this.sourcePackets.splice(packetIndex, 1);
          // Add the packet to targetPackets
          this.targetPackets.push(packet);
        }
      }

      //this.findAllConfirmedPackets();

      this.barCode = "";
    }, error => {
      // Handle errors here
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la validation du barcode: '+ this.barCode });
      console.error(error);
    });

}
}



