import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { PacketValidationDTO } from 'src/shared/models/PacketValidationDTO';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';
import { RETURN, VALIDATION } from 'src/shared/utils/status-list';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.scss']
})
export class VerificationComponent implements OnInit, OnDestroy {

  sourcePackets: PacketValidationDTO[] = [];

  targetPackets: PacketValidationDTO[] = [];

  barcode : string;

  $unsubscribe: Subject<void> = new Subject();

  packets: PacketValidationDTO[];
  totalItems: number;
  packet: Packet;
  type : string = VALIDATION;
  sourceString : string = "Non Validé";
  targetString : string = "Validé";
  isAdmin: boolean;
  packetSameBarCode : PacketValidationDTO[];

  constructor(private packetService : PacketService, private messageService: MessageService,public storageService: StorageService) {

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
  this.packetService.getValidationPackets()
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: any) => {
        this.packets = response.result;

        if(this.type == VALIDATION){
          this.sourcePackets = this.packets.filter((packet: PacketValidationDTO) => !packet.valid);
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
  let lastNineCharacters = this.barcode.slice(-9);
  let num: number = Number(this.barcode);
  if (this.type == VALIDATION){
    if(this.barcode.length > 8){

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
    if(this.barcode.length < 9){
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
    if(this.barcode.length > 8){
      this.packetSameBarCode = this.sourcePackets.filter((packet : PacketValidationDTO) => packet.barcode.slice(-9) == lastNineCharacters);
      this.barcode = this.packetSameBarCode[0].barcode;
    }
    else {
      this.packetSameBarCode = this.sourcePackets.filter((packet : PacketValidationDTO) => packet.id == num);
      this.barcode = this.packetSameBarCode[0].barcode;
    }

    if (this.packetSameBarCode.length>1){
      alert('Error: le code a barre '+this.barcode +' existe plusieur fois');
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
  this.packetService.validatePacket(this.barcode, this.type).subscribe({
    next: (response: any) => {
      // Handle the response here
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: `code à barre validé: ${this.barcode}`
      });
      console.log('response', response);

      if (this.type === VALIDATION) {
        this.movePacketToTarget(this.barcode);
      }

      this.barcode = ""; // Reset the barcode after processing
    },
    error: (error: any) => {
      // Handle errors here
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: `Erreur lors de la validation du barcode: ${this.barcode}`
      });
      console.error(error);
    },
    complete: () => {
      // Optional: Add logic for when the observable completes
      console.log('Validation process completed.');
    }
  });
}
  private movePacketToTarget(barcode: string): void {
    const packetIndex = this.sourcePackets.findIndex(
      (packet: PacketValidationDTO) => packet.barcode === barcode
    );
    console.log("packetIndex", packetIndex);

    if (packetIndex !== -1) {
      // Remove the packet from sourcePackets and add it to targetPackets
      const [packet] = this.sourcePackets.splice(packetIndex, 1);
      this.targetPackets.push(packet);
    }
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}



