import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { Status } from 'src/shared/enums/status';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { StatusItem } from 'src/shared/models/status';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';
@Component({
  selector: 'app-status-container',
  templateUrl: './status-container.component.html',
  styleUrl: './status-container.component.css'
})

export class StatusContainerComponent implements OnChanges {
  @Input() params!: any;
  @Input() activeIndex: number;
  selectedStatus: FormControl = new FormControl();
  oldActiveIndex: number = 1;
  $unsubscribe: Subject<void> = new Subject();
  @Output() statusChange = new EventEmitter<StatusItem>();
  isAdmin: boolean;
  statusItems: StatusItem[];
  selectedIndex: number | null;
  selectedItem: StatusItem = {
    label: "", value: "", icon: 'pi pi-phone', color: '#EAB308', count: 0, dayCount: 0, isUserOption: true,
    items: [] , selectedOptions: []
  };
  borderIndex: number = 2;
  item: StatusItem;
  changed = false;

  constructor(
    private packetService: PacketService,
    public storageService: StorageService,
    public messageService: MessageService
  ) {
  }

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {//Correction
      this.isAdmin = this.storageService.hasRoleAdmin();
    });
  }

  initNotification(){
    console.log('this.selectedItem',this.selectedItem);

    this.statusItems = [
      { label: Status.OOS, value: Status.OOS, icon: 'pi pi-times', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true },
      {
        label: Status.NOT_CONFIRMED, value: Status.NOT_CONFIRMED, icon: 'pi pi-phone', color: '#EAB308', count: 0, dayCount: 0, isUserOption: true,
        items: this.getNonConfirmedOptions(), selectedOptions: []
      },
      { label: Status.CONFIRMED, value: Status.CONFIRMED, icon: 'pi pi-check', color: '#22C55E', count: 0, dayCount: 0, isUserOption: true },
      {
        label: Status.IN_PROGRESS, value: Status.IN_PROGRESS, icon: 'pi pi-truck', color: '#A855F7', count: 0, dayCount: 0, isUserOption: true,
        items: this.getInProgressOptions(), selectedOptions: []
      },
      {
        label: Status.TO_VERIFY, value: Status.IN_PROGRESS, icon: 'pi pi-megaphone', color: '#A855F7', count: 0, dayCount: 0, isUserOption: true,
        items: this.getProblem(), selectedOptions: []
      },
      { label: Status.RETURN, value: Status.RETURN, icon: 'pi pi-thumbs-down', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true},
      { label: Status.RETURN_RECEIVED, value: Status.RETURN_RECEIVED, icon: 'pi pi-thumbs-down', color: '#EF4444', count: 0, dayCount: 0, isUserOption: false},
      {
        label: Status.CANCELED, value: Status.CANCELED, icon: 'pi pi-ban', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true,
        items: this.getCanceledOptions(), selectedOptions: []
      },
      {
        label: Status.DELIVERED, value: 'Terminé', icon: 'pi pi-flag', color: '#3B82F6', count: 0, dayCount: 0, isUserOption: false,
        items: this.getEndedOptions(), selectedOptions: []
      }
    ];
  }


  ngOnChanges(changes: SimpleChanges) {

    if (changes['params']) {
      if( changes['params'].firstChange == true)
        {
          this.createNotification();
        }
      if(changes['params'].currentValue.searchText != changes['params'].previousValue.searchText
        || changes['params'].currentValue.mandatoryDate != changes['params'].previousValue.mandatoryDate
        || changes['params'].currentValue.beginDate != changes['params'].previousValue.beginDate
        || changes['params'].currentValue.endDate != changes['params'].previousValue.endDate ){
        this.createNotification();
      }

      if(changes['params'].currentValue.statusFilter == false)
        this.selectedIndex = null;
      if(changes['params'].currentValue.statusFilter == true && this.selectedIndex == null )
        {
          this.selectedItem = this.statusItems[1];
          this.selectedIndex = 1;
        }

    }
  }

  toggleListbox(index: any, item: any) {
    if (this.statusItems[index].items) {
      if (index != null && this.changed==false) {
        this.emitSelectedStatus(item);
      }
    } else {
      this.emitSelectedStatus(item);
    }
    this.selectedIndex = index;
    this.changed = false;
  }


  emitSelectedStatus(item: any) {
      this.selectedItem = item;
      this.statusChange.emit(item);
  }


  onOptionSelect(item: any) {
    this.changed = true
    this.selectedItem = item;
  }

  getNonConfirmedOptions() {
    return [
      { label: Status.NOT_CONFIRMED, value: Status.NOT_CONFIRMED },
      { label: Status.UNREACHABLE, value: Status.UNREACHABLE }
    ];
  }

  getInProgressOptions() {
    return [
      { label: Status.IN_PROGRESS_1, value: Status.IN_PROGRESS_1 },
      { label: Status.IN_PROGRESS_2, value: Status.IN_PROGRESS_2 },
      { label: Status.IN_PROGRESS_3, value: Status.IN_PROGRESS_3 }
    ];
  }
  getProblem() {
    return [
      { label: Status.TO_VERIFY, value: Status.TO_VERIFY },
      { label: Status.PROBLEM, value: Status.PROBLEM }
    ];
  }

  getCanceledOptions() {
    return [
      { label: Status.CANCELED, value: Status.CANCELED },
      { label: Status.DELETED, value: Status.DELETED }
    ];
  }

  getEndedOptions() {
    return [
      { label: Status.DELIVERED, value: Status.DELIVERED },
      { label: Status.PAID, value: Status.PAID },
      { label: Status.RETURN_RECEIVED, value: Status.RETURN_RECEIVED }
    ];
  }

  createNotification(): void {
    this.initNotification();
    let all = 0;

    this.packetService.syncNotification(this.params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          if (response.length > 0) {
            response.forEach((element: any) => {
              switch (Status.findByKey(element.status)) {
                case Status.OOS:
                  this.statusItems[0].count = element.statusCount;
                  this.statusItems[0].dayCount = element.statusByDateCount;
                  break;
                case Status.NOT_CONFIRMED:
                case Status.UNREACHABLE:
                  this.statusItems[1].count += element.statusCount;
                  this.statusItems[1].dayCount += element.statusByDateCount;
                  break;
                case Status.CONFIRMED:
                  this.statusItems[2].count = element.statusCount;
                  this.statusItems[2].dayCount += element.statusByDateCount;
                  break;
                case Status.IN_PROGRESS_1:
                case Status.IN_PROGRESS_2:
                case Status.IN_PROGRESS_3:
                  this.statusItems[3].count += element.statusCount;
                  this.statusItems[3].dayCount += element.statusByDateCount;
                  break;
                case Status.TO_VERIFY:
                case Status.PROBLEM:
                  this.statusItems[4].count += element.statusCount;
                  this.statusItems[4].dayCount += element.statusByDateCount;
                  break;
                case Status.RETURN:
                  this.statusItems[5].count = element.statusCount;
                  this.statusItems[5].dayCount = element.statusByDateCount;
                  break;
                case Status.RETURN_RECEIVED:
                  this.statusItems[6].count = element.statusCount;
                  this.statusItems[6].dayCount = element.statusByDateCount;
                  break;
                case Status.CANCELED:
                case Status.DELETED:
                  this.statusItems[7].count += element.statusCount;
                  this.statusItems[7].dayCount += element.statusByDateCount;
                  break;
                case Status.DELIVERED:
                case Status.PAID:
                  this.statusItems[8].count += element.statusCount;
                  this.statusItems[8].dayCount += element.statusByDateCount;
                  break;

              }
              all += element.statusCount;
            });
          }
          //console.log(all);

          //this.statusItems[0].count = all;
          //this.loadNotification();
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
  }

  ngOnDestroy(): void {
    if (this.changed)
      if (this.selectedItem.items) {
        this.emitSelectedStatus(this.selectedItem);
      }
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

}
