import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { PacketService } from 'src/shared/services/packet.service';
import { DateUtils } from 'src/shared/utils/date-utils';
import { CONFIRMEE, RETOUR } from 'src/shared/utils/status-list';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.scss']
})
export class VerificationComponent implements OnInit {

  sourceProducts!: Packet[];

  targetProducts!: Packet[];

  barCode : String;

  $unsubscribe: Subject<void> = new Subject();

  packets: Packet[];
  totalItems: number;
  packet: Packet;
  type : string = CONFIRMEE;
  sourceString : string;
  targetString : string;


  params : any={
    page: 0,
    size: 300,
    startDate: null,
    endDate: null,
    mandatoryDate: true,
    status: CONFIRMEE,
  };


  constructor(private packetService : PacketService,private dateUtils: DateUtils) {

  }

  ngOnInit(): void {
    this.findAllConfirmedPackets();
    this.targetProducts = [];
}

findAllConfirmedPackets(): void {
  this.params.status = this.type;
  this.packetService.findAllPackets(this.params)
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe({
      next: (response: ResponsePage) => {
        this.packets = response.result;
        console.log('response',response);
        this.totalItems = response.totalItems;
        if(this.type == CONFIRMEE){
          this.sourceProducts = response.result.filter((packet: Packet) => packet.valid == false);
          this.targetProducts = response.result.filter((packet: Packet) => packet.valid);
          this.sourceString = "Non validé";
          this.targetString = "Validé";
        }
        else {
          this.sourceProducts = response.result.filter((packet: Packet) => packet.exchange);
          this.targetProducts = response.result.filter((packet: Packet) => packet.exchange == false);
          this.sourceString = RETOUR;
          this.targetString = "Retour Echange";
        }
        console.log('this.sourceProducts',this.sourceProducts.length);
      },
      error: (error: Error) => {
        console.log('Error:', error);
      }
    });
}

Validate(){
  console.log('validé',this.barCode);

  this.packetService.validatePacket(this.barCode,this.type).subscribe(response => {
    // Handle the response here
    this.findAllConfirmedPackets();
    this.barCode = "";
  }, error => {
    // Handle errors here
    console.error(error);
  });
}
}



