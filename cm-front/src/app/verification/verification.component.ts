import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject, of, takeUntil } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { PacketService } from 'src/shared/services/packet.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { CONFIRMED, RETURN } from 'src/shared/utils/status-list';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.scss']
})
export class VerificationComponent implements OnInit {

  sourcePackets: Packet[] = [];

  targetPackets: Packet[] = [];

  barCode : String;

  $unsubscribe: Subject<void> = new Subject();

  packets: Packet[];
  totalItems: number;
  packet: Packet;
  type : string = CONFIRMED;
  sourceString : string = "Non Validé";
  targetString : string = "Validé";


  params : any={
    page: 0,
    size: 300,
    startDate: null,
    endDate: null,
    mandatoryDate: false,
    status: CONFIRMED,
  };


  constructor(private packetService : PacketService,private dateUtils: DateUtils,private messageService: MessageService,) {

  }

  ngOnInit(): void {
    this.findAllConfirmedPackets();
    this.targetPackets = [];
}

findAllConfirmedPackets(): void {
  if(this.type == CONFIRMED){
    this.sourceString = "Non validé";
    this.targetString = "Validé";
  }
  else {
    this.sourceString = RETURN;
    this.targetString = "Retour Echange";
  }
  this.params.status = this.type;
  this.packetService.findAllPackets(this.params)
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: ResponsePage) => {
        this.packets = response.result;
        //console.log('response',response);
        this.totalItems = response.totalItems;
        if(this.type == CONFIRMED){
          this.sourcePackets = response.result.filter((packet: Packet) => packet.valid == false);
          this.targetPackets = response.result.filter((packet: Packet) => packet.valid);
        }
        else {
          this.sourcePackets = response.result.filter((packet: Packet) => packet.exchangeId == null);
          this.targetPackets = response.result.filter((packet: Packet) => packet.exchangeId);
        }
        console.log('this.sourceProducts',this.sourcePackets.length);
      },
      error: (error: Error) => {
        console.log('Error:', error);
      }
    });
}

Validate(){
  if(this.barCode.length == 13)
  this.barCode = this.barCode.slice(0,12);
  //console.log('validé',this.barCode);
  if (this.type == CONFIRMED){
    if (!(this.sourcePackets.map((packet : Packet) => packet.barcode).indexOf(this.barCode) > -1)){
      if (this.targetPackets.map((packet : Packet) => packet.barcode).indexOf(this.barCode) > -1){
        alert('Error: BarreCode déja validé');
      } else alert("Error: BarreCode n'existe pas");
      return;
    }
    else if (this.targetPackets.map((packet : Packet) => packet.barcode).indexOf(this.barCode) > -1){
      alert('Error: Colie double et déja validé');
      return;
    }
    //let listValidatedPhoneNumber = this.targetPackets.map((packet : Packet) => packet.customerPhoneNb);
    let packetSameBarCode : Packet[] = this.sourcePackets.filter((packet : Packet) => packet.barcode == this.barCode);
    console.log('packetSameBarCode',packetSameBarCode);

    let phoneNumber = packetSameBarCode[0].customerPhoneNb;
    console.log('phoneNumber',phoneNumber);
    console.log('this.sourcePackets',this.sourcePackets);

    let packetSamePhoneNumber : Packet[] = this.sourcePackets.filter((packet : Packet) => packet.customerPhoneNb == phoneNumber);
    console.log('packetSamePhoneNumber',packetSamePhoneNumber);

    if (packetSameBarCode.length>1){
      alert('Error: le code a barre '+this.barCode +' existe plusieur fois');
      return;
    }
    else if (packetSamePhoneNumber.length>1)
    {
      alert('Error: le numero de telephone '+phoneNumber +' existe plusieur fois');
      return;
    }
  }
     this.packetService.validatePacket(this.barCode,this.type).subscribe(response => {
      // Handle the response here
      this.messageService.add({ severity: 'success', summary: 'Success', detail: "code à barre validé: " + this.barCode });
      console.log('response',response);

      this.findAllConfirmedPackets();
      this.barCode = "";
    }, error => {
      // Handle errors here
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la validation du barcode: '+ this.barCode });
      console.error(error);
    });

}
}



