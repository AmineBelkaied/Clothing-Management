import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { Status } from 'src/shared/models/status';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';

import { CANCELED, OOS, NOT_SERIOUS, PROBLEME,
  DELETED, statesList, statusList,
  IN_PROGRESS_1,
  IN_PROGRESS_2,
  IN_PROGRESS_3,
  CONFIRMED,
  TO_VERIFY,
  RETURN,
  RETURN_RECEIVED,
  PAID,
  NOT_CONFIRMED,
  DELIVERED,
  UNREACHABLE,
  IN_PROGRESS} from 'src/shared/utils/status-list';

@Component({
  selector: 'app-status-container',
  templateUrl: './status-container.component.html',
  styleUrl: './status-container.component.css'
})
export class StatusContainerComponent {
  @Input() params!: any;
  @Input() activeIndex: number;
  selectedStatus: FormControl = new FormControl();
  oldActiveIndex: number = 2;
  $unsubscribe: Subject<void> = new Subject();
  @Output() statusChange = new EventEmitter<Status>();
  //status:  Status;

  statusItems: any[] = [
    {
      label: "Erreur Chargement",
      title: "Tous",
      badge: 0,
      badgeByDate: 0,
      command: () => {
      },
      disabled: true
    },
    {
      label: OOS,
      title: OOS,
      badge: 0,
      badgeByDate: 0,
      command: () => {
      }
    },
    {
      label: NOT_CONFIRMED,
      title: NOT_CONFIRMED,
      badge: 0,
      badgeByDate: 0,
      command: () => {
      }
    },
    {
      label: UNREACHABLE,
      title: UNREACHABLE,
      icon: 'pi-power-off',
      badge: 0,
      badgeByDate: 0,
      command: () => {
      }
    },
    {
      label: CONFIRMED,
      title: CONFIRMED,
      badge: 0,
      badgeByDate: 0
    },
    {
      label: IN_PROGRESS,
      title: IN_PROGRESS,
      badge: 0,
      badgeByDate: 0
    },
    {
      label: RETURN,
      title: RETURN,
      badge: 0,
      badgeByDate: 0
    },
    {
      label: CANCELED,
      title: CANCELED,
      badge: 0,
      badgeByDate: 0
    },
    {
      label: 'Terminé',
      title: 'Terminé',
      badge: 0,
      badgeByDate: 0
    }
  ];

  inProgressOptionsValue !:any;
  inProgressOptions: any[] = [
    { name: '1' , value:IN_PROGRESS_1},
    { name: '2' , value:IN_PROGRESS_2},
    { name: '3' , value:IN_PROGRESS_3},
    { name: TO_VERIFY , value:TO_VERIFY}
  ];

  canceledOptionsValue !:any;
  canceledOptions: any[] = [
    { name: CANCELED , value:CANCELED},
    { name: DELETED , value:DELETED}
  ];

  nonConfirmedOptionsValue !:any;
  nonConfirmedOptions: any[] = [
      { name: NOT_CONFIRMED , value:NOT_CONFIRMED},
      { name: UNREACHABLE , value:UNREACHABLE},
  ];

  endedOptionsValue !:any;
  endedOptions: any[] = [
      { name: DELIVERED , value:DELIVERED},
      { name: PAID , value:PAID},
      { name: RETURN_RECEIVED , value:RETURN_RECEIVED}
  ];

  isAdmin: boolean;
  statusLabel: string;
  noClose: boolean = false;

  constructor(
    private packetService:PacketService,
    public storageService: StorageService,
    public messageService:MessageService,

    ) {
  }


  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isAdmin = this.storageService.hasRoleAdmin();
    });
    console.log("ngOnInit-status");
    this.createNotification();
  }

  emitSelectedStatus(id:number,statusLabel:string) {
    let status: Status = Object.assign({});
    status.id = id;
    status.label = statusLabel
    status.statusList = this.selectedStatus.value;
    status.noClose = this.noClose;
    this.statusChange.emit(status);
  }



  onActiveIndexChange(event: any) {
    console.log(event);

    const statusLabel = this.statusItems[event]?.title;
    if (statusLabel) {
      switch (statusLabel) {
        case IN_PROGRESS:
          this.handleInProgressStatus();
          break;
        case NOT_CONFIRMED:
          this.handleNotConfirmedStatus();
          break;
        case CANCELED:
          this.handleCanceledStatus();
          break;
        case 'Terminé':
          this.handleEndedStatus();
          break;
        default:{
          this.selectedStatus.patchValue([statusLabel]);
          this.clearAllSelectedStatus();
        }
      }
      this.emitSelectedStatus(event,statusLabel);
    }
  }
  handleEndedStatus() {
    this.nonConfirmedOptionsValue=[];
        this.canceledOptionsValue = [];
        this.inProgressOptionsValue = [];
        if(this.endedOptionsValue != undefined && this.endedOptionsValue.length > 0){
          this.selectedStatus.patchValue(this.endedOptionsValue);
          this.noClose=true;
        }
        else{
          this.selectedStatus.patchValue( [ DELIVERED ,PAID ,RETURN_RECEIVED ]);
          }
  }
  handleCanceledStatus() {
    this.nonConfirmedOptionsValue=[];
        this.endedOptionsValue=[];
        this.inProgressOptionsValue = [];
        if(this.canceledOptionsValue != undefined && this.canceledOptionsValue.length > 0){
          this.noClose=true;
          this.selectedStatus.patchValue(this.canceledOptionsValue);
        }
        else
            this.selectedStatus.patchValue([ CANCELED, DELETED ]);
  }
  handleNotConfirmedStatus() {
    this.endedOptionsValue=[];
        this.canceledOptionsValue = [];
        this.inProgressOptionsValue = [];
        if(this.nonConfirmedOptionsValue != undefined && this.nonConfirmedOptionsValue.length > 0){
          this.noClose=true;
          this.selectedStatus.patchValue(this.nonConfirmedOptionsValue);
        }
        else
            this.selectedStatus.patchValue([ NOT_CONFIRMED, UNREACHABLE ]);
  }
  handleInProgressStatus() {
    this.nonConfirmedOptionsValue=[];
    this.endedOptionsValue=[];
    this.canceledOptionsValue = [];
    if(this.inProgressOptionsValue != undefined && this.inProgressOptionsValue.length > 0){
      this.noClose=true;
      this.selectedStatus.patchValue(this.inProgressOptionsValue);
    }
    else
      this.selectedStatus.patchValue([ TO_VERIFY, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3 ]);
  }

  loadNotification(){
    this.statusItems= [
      {
        label: "Tous",
        title: "Tous",
        icon: 'pi pi-align-justify',
        color: 'green',
        badge:this.statusItems[0].badge,
        badgeByDate:this.statusItems[0].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:"All", detail: event.item.label});
          this.onActiveIndexChange(event.index);
        },
        disabled:true
      },
      {
        label: OOS+"("+this.statusItems[1].badge+")",
        title: OOS,
        icon: 'pi pi-times',
        color: 'red',
        badge:this.statusItems[1].badge,
        badgeByDate:this.statusItems[1].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:OOS, detail: event.item.label});
        }
      },
      {
        label: NOT_CONFIRMED+"("+this.statusItems[2].badge+")",
        title: NOT_CONFIRMED,
        icon: 'pi pi-phone',
        color: 'orange',
        badge:this.statusItems[2].badge,
        badgeByDate:this.statusItems[2].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:NOT_CONFIRMED, detail: event.item.label});
        }
      },
      {
        label: CONFIRMED+"("+this.statusItems[3].badge+")",
        title: CONFIRMED,
        icon: 'pi pi-check',
        color: 'green',
        badge:this.statusItems[3].badge,
        badgeByDate:this.statusItems[3].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:NOT_CONFIRMED, detail: event.item.label});
        }
      },
      {
        label: IN_PROGRESS+"("+this.statusItems[4].badge+")",
        title: IN_PROGRESS,
        icon: 'pi pi-truck',
        color: 'purple',
        badge:this.statusItems[4].badge,
        badgeByDate:this.statusItems[4].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:IN_PROGRESS, detail: event.item.label});
        },
      },
      {
        label: RETURN+"("+this.statusItems[5].badge+")",
        title: RETURN,
        icon: 'pi pi-thumbs-down',
        color: 'red',
        badge:this.statusItems[5].badge,
        badgeByDate:this.statusItems[5].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:RETURN, detail: event.item.label});
          }
      },
      {
        label: CANCELED+"("+this.statusItems[6].badge+")",
        title: CANCELED,
        icon: 'pi pi-thumbs-down',
        color: 'red',
        badge:this.statusItems[6].badge,
        badgeByDate:this.statusItems[6].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:RETURN, detail: event.item.label});
          }
      },
      {
        label: 'Terminé',
        title: 'Terminé',
        icon: 'pi pi-flag',
        color: 'red',
        badge:this.statusItems[7].badge,
        badgeByDate:this.statusItems[7].badgeByDate,
        command: (event: any) => {
          this.messageService.add({severity:'info', summary:'Last Step', detail: event.item.label})
        }
      }
  ];
  }

  createNotification(): void {
    console.log("createNotification");
      this.statusItems[1].badge = 0;
      this.statusItems[1].badgeByDate = 0;
      this.statusItems[2].badge = 0;
      this.statusItems[2].badgeByDate = 0;
      this.statusItems[3].badge = 0;
      this.statusItems[3].badgeByDate = 0;
      this.statusItems[4].badge = 0;
      this.statusItems[4].badgeByDate = 0;
      this.statusItems[5].badge = 0;
      this.statusItems[5].badgeByDate = 0;
      this.statusItems[6].badge = 0;
      this.statusItems[6].badgeByDate = 0;
      this.statusItems[7].badge = 0;
      this.statusItems[7].badgeByDate = 0;
      let all = 0;

    this.packetService.syncNotification(this.params.beginDate,this.params.endDate)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          if (response.length > 0) {
            response.forEach((element: any) => {

              switch (element.status) {
                case OOS:
                  this.statusItems[1].badge = element.statusCount;
                  this.statusItems[1].badgeByDate = element.statusByDateCount;
                  break;
                case NOT_CONFIRMED:
                case UNREACHABLE:
                  this.statusItems[2].badge += element.statusCount;
                  this.statusItems[2].badgeByDate += element.statusByDateCount;
                  break;
                case CONFIRMED:
                  this.statusItems[3].badge = element.statusCount;
                  this.statusItems[3].badgeByDate += element.statusByDateCount;
                  break;
                case IN_PROGRESS_1:
                case IN_PROGRESS_2:
                case IN_PROGRESS_3:
                case TO_VERIFY:
                  this.statusItems[4].badge += element.statusCount;
                  this.statusItems[4].badgeByDate += element.statusByDateCount;
                  break;
                case RETURN:
                  this.statusItems[5].badge = element.statusCount;
                  this.statusItems[5].badgeByDate = element.statusByDateCount;
                  break;
                case CANCELED:
                case DELETED:
                  this.statusItems[6].badge += element.statusCount;
                  this.statusItems[6].badgeByDate += element.statusByDateCount;
                  break;
                case DELIVERED:
                case PAID:
                case RETURN_RECEIVED:
                  this.statusItems[7].badge += element.statusCount;
                  this.statusItems[7].badgeByDate += element.statusByDateCount;
                  break;

              }
              all += element.statusCount;
            });
          }
          this.statusItems[0].badge = all;
          this.loadNotification();
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
  }

  clearAllSelectedStatus(){
    this.nonConfirmedOptionsValue=[];
    this.endedOptionsValue=[];
    this.canceledOptionsValue = [];
    this.inProgressOptionsValue = [];
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
