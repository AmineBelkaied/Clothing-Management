import { Component, ElementRef, EventEmitter, HostListener, Input, Output, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MenuItem, MessageService } from 'primeng/api';
import { TieredMenu } from 'primeng/tieredmenu';
import { Subject, takeUntil } from 'rxjs';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { Status } from 'src/shared/models/status';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';

import {
  CANCELED, OOS, NOT_SERIOUS, PROBLEME,
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
  IN_PROGRESS
} from 'src/shared/utils/status-list';
@Component({
  selector: 'app-status-container',
  templateUrl: './status-container.component.html',
  styleUrl: './status-container.component.css'
})

export class StatusContainerComponent {
  @ViewChildren('menuItem') menuItems!: QueryList<ElementRef>;
  @Input() params!: any;
  @Input() activeIndex: number;
  selectedStatus: FormControl = new FormControl();
  oldActiveIndex: number = 2;
  $unsubscribe: Subject<void> = new Subject();
  @Output() statusChange = new EventEmitter<Status>();
  isAdmin: boolean;
  statusItems: any[];
  selectedIndex: number | null = null;
  borderIndex: number = 2;
  item: Status;
  changed = false;

  constructor(
    private packetService: PacketService,
    public storageService: StorageService,
    public messageService: MessageService,
    private elementRef: ElementRef
  ) {
  }

  ngOnInit(): void {

    this.statusItems = [
      { label: 'Tous', value: "Tous", icon: 'pi pi-bars', color: '#22C55E', count: 0, dayCount: 0, isUserOption: false },
      { label: 'En rupture', value: OOS, icon: 'pi pi-times', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true },
      {
        label: 'Non confirmée', value: NOT_CONFIRMED, icon: 'pi pi-phone', color: '#EAB308', count: 0, dayCount: 0, isUserOption: true,
        options: this.getNonConfirmedOptions(), selectedOptions: []
      },
      { label: 'Confirmée', value: CONFIRMED, icon: 'pi pi-check', color: '#22C55E', count: 0, dayCount: 0, isUserOption: true },
      {
        label: 'À vérifier', value: IN_PROGRESS, icon: 'pi pi-play', color: '#A855F7', count: 0, dayCount: 0, isUserOption: true,
        options: this.getInProgressOptions(), selectedOptions: []
      },
      { label: 'Retour', value: RETURN, icon: 'pi pi-thumbs-down', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true },
      {
        label: 'Annuler', value: CANCELED, icon: 'pi pi-ban', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true,
        options: this.getCanceledOptions(), selectedOptions: []
      },
      {
        label: 'Livrée', value: 'Terminé', icon: 'pi pi-flag', color: '#3B82F6', count: 0, dayCount: 0, isUserOption: false,
        options: this.getEndedOptions(), selectedOptions: []
      }
    ];

    this.storageService.isLoggedIn.subscribe(isLoggedIn => {//Correction
      this.isAdmin = this.storageService.hasRoleAdmin();
    });
    this.createNotification();
  }

  toggleListbox(index: any, item: any) {
    this.borderIndex = index;
    if (this.statusItems[index].options) {
      this.selectedIndex = this.selectedIndex === index ? null : index;
      if (this.selectedIndex == null) {
        console.log("this.selectedIndex", this.borderIndex);
        this.emitSelectedStatus(item);

      }
    } else {
      console.log("no options", this.borderIndex);
      this.emitSelectedStatus(item);
    }
  }


  emitSelectedStatus(item: any) {
    this.changed = false;
    this.item = item;
    this.statusChange.emit(item);
  }

/*   changeBgColor(element: HTMLDivElement) {
    element.style.backgroundColor = '#a8b3c1';
  }

  resetBgColor(element: HTMLDivElement) {
    element.style.backgroundColor = 'black';
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    for(let item of this.menuItems) {
      const clickedInside = item.nativeElement.contains(event.target);
      if (clickedInside) {
        item.nativeElement.classList.add('selected-item');
        item.nativeElement.classList.remove('default-item');
      } else {
        item.nativeElement.classList.add('default-item');
        item.nativeElement.classList.remove('selected-item');
      }
    }
  }
 */

  onOptionSelect(item: any) {
    console.log("item changed", item);
    this.changed = true
    //console.log(`Selected options for ${item.label}:`, item.selectedOptions);
    this.item = item;
    // Implement your logic here
  }

  getNonConfirmedOptions() {
    return [
      { label: NOT_CONFIRMED, value: NOT_CONFIRMED },
      { label: UNREACHABLE, value: UNREACHABLE }
    ];
  }

  getInProgressOptions() {
    return [
      { label: IN_PROGRESS_1, value: IN_PROGRESS_1 },
      { label: IN_PROGRESS_2, value: IN_PROGRESS_2 },
      { label: IN_PROGRESS_3, value: IN_PROGRESS_3 },
      { label: TO_VERIFY, value: TO_VERIFY }
    ];
  }

  getCanceledOptions() {
    return [
      { label: CANCELED, value: CANCELED },
      { label: DELETED, value: DELETED }
    ];
  }

  getEndedOptions() {
    return [
      { label: DELIVERED, value: DELIVERED },
      { label: PAID, value: PAID },
      { label: RETURN_RECEIVED, value: RETURN_RECEIVED }
    ];
  }


  createNotification(): void {
    console.log("createNotification");
    let all = 0;

    this.packetService.syncNotification(this.params.beginDate, this.params.endDate)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          if (response.length > 0) {
            response.forEach((element: any) => {

              switch (element.status) {
                case OOS:
                  this.statusItems[1].count = element.statusCount;
                  this.statusItems[1].dayCount = element.statusByDateCount;
                  break;
                case NOT_CONFIRMED:
                case UNREACHABLE:
                  this.statusItems[2].count += element.statusCount;
                  this.statusItems[2].dayCount += element.statusByDateCount;
                  break;
                case CONFIRMED:
                  this.statusItems[3].count = element.statusCount;
                  this.statusItems[3].dayCount += element.statusByDateCount;
                  break;
                case IN_PROGRESS_1:
                case IN_PROGRESS_2:
                case IN_PROGRESS_3:
                case TO_VERIFY:
                  this.statusItems[4].count += element.statusCount;
                  this.statusItems[4].dayCount += element.statusByDateCount;
                  break;
                case RETURN:
                  this.statusItems[5].count = element.statusCount;
                  this.statusItems[5].dayCount = element.statusByDateCount;
                  break;
                case CANCELED:
                case DELETED:
                  this.statusItems[6].count += element.statusCount;
                  this.statusItems[6].dayCount += element.statusByDateCount;
                  break;
                case DELIVERED:
                case PAID:
                case RETURN_RECEIVED:
                  this.statusItems[7].count += element.statusCount;
                  this.statusItems[7].dayCount += element.statusByDateCount;
                  break;

              }
              all += element.statusCount;
            });
          }
          this.statusItems[0].count = all;
          //this.loadNotification();
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
  }

  ngOnDestroy(): void {
    if (this.changed)
      if (this.item.options) {
        this.emitSelectedStatus(this.item);
        console.log("destroy", this.item);
      }
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

  /*   positionListbox(index: number) {
    const menuItem = this.menuItems.toArray()[index].nativeElement;
    const rect = menuItem.getBoundingClientRect();
    const windowWidth = window.innerWidth;
    const listboxWidth = 200; // Width of the listbox

    if (rect.right + listboxWidth > windowWidth) {
      // Position to the left if there's not enough space on the right
      this.listboxStyle = {
        ...this.listboxStyle,
        left: 'auto',
        right: '100%',
        marginLeft: '0',
        marginRight: '0.5rem'
      };
    } else {
      // Position to the right
      this.listboxStyle = {
        ...this.listboxStyle,
        left: '100%',
        right: 'auto',
        marginLeft: '0.5rem',
        marginRight: '0'
      };
    }
  } */

  /*
    clearAllSelectedStatus(){
      this.nonConfirmedOptionsValue=[];
      this.endedOptionsValue=[];
      this.canceledOptionsValue = [];
      this.inProgressOptionsValue = [];
    }*/

  /*
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
*/

}
