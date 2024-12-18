import { Component, EventEmitter, Input, Output } from '@angular/core';
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

export class StatusContainerComponent {
  @Input() params!: any;
  @Input() activeIndex: number;
  selectedStatus: FormControl = new FormControl();
  oldActiveIndex: number = 2;
  $unsubscribe: Subject<void> = new Subject();
  @Output() statusChange = new EventEmitter<StatusItem>();
  isAdmin: boolean;
  statusItems: StatusItem[];
  selectedIndex: number | null = null;
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

    this.statusItems = [
      { label: 'Tous', value: "Tous", icon: 'pi pi-bars', color: '#22C55E', count: 0, dayCount: 0, isUserOption: false },
      { label: 'En rupture', value: Status.OOS, icon: 'pi pi-times', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true },
      {
        label: 'Non confirmée', value: Status.NOT_CONFIRMED, icon: 'pi pi-phone', color: '#EAB308', count: 0, dayCount: 0, isUserOption: true,
        options: this.getNonConfirmedOptions(), selectedOptions: []
      },
      { label: 'Confirmée', value: Status.CONFIRMED, icon: 'pi pi-check', color: '#22C55E', count: 0, dayCount: 0, isUserOption: true },
      {
        label: 'À vérifier', value: Status.IN_PROGRESS, icon: 'pi pi-play', color: '#A855F7', count: 0, dayCount: 0, isUserOption: true,
        options: this.getInProgressOptions(), selectedOptions: []
      },
      { label: 'Retour', value: Status.RETURN, icon: 'pi pi-thumbs-down', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true },
      {
        label: 'Annuler', value: Status.CANCELED, icon: 'pi pi-ban', color: '#EF4444', count: 0, dayCount: 0, isUserOption: true,
        options: this.getCanceledOptions(), selectedOptions: []
      },
      {
        label: 'Livrée', value: Status.TERMINE, icon: 'pi pi-flag', color: '#3B82F6', count: 0, dayCount: 0, isUserOption: false,
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
      if (index != null && this.changed==false) {
        this.selectedIndex = this.selectedIndex === index ? null : index;
        this.emitSelectedStatus(item);
      }
    } else {
      this.emitSelectedStatus(item);
    }
    this.changed = false;
  }


  emitSelectedStatus(item: any) {
      this.item = item;
      this.statusChange.emit(item);
  }


  onOptionSelect(item: any) {
    this.changed = true
    this.item = item;
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
      { label: Status.IN_PROGRESS_3, value: Status.IN_PROGRESS_3 },
      { label: Status.TO_VERIFY, value: Status.TO_VERIFY }
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
    let all = 0;

    this.packetService.syncNotification(this.params.beginDate, this.params.endDate)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          if (response.length > 0) {
            response.forEach((element: any) => {                
              switch (Status.findByKey(element.status)) {
                case Status.OOS:
                  this.statusItems[1].count = element.statusCount;
                  this.statusItems[1].dayCount = element.statusByDateCount;
                  break;
                case Status.NOT_CONFIRMED:
                case Status.UNREACHABLE:
                  this.statusItems[2].count += element.statusCount;
                  this.statusItems[2].dayCount += element.statusByDateCount;
                  break;
                case Status.CONFIRMED:
                  this.statusItems[3].count = element.statusCount;
                  this.statusItems[3].dayCount += element.statusByDateCount;
                  break;
                case Status.IN_PROGRESS_1:
                case Status.IN_PROGRESS_2:
                case Status.IN_PROGRESS_3:
                case Status.TO_VERIFY:
                  this.statusItems[4].count += element.statusCount;
                  this.statusItems[4].dayCount += element.statusByDateCount;
                  break;
                case Status.RETURN:
                  this.statusItems[5].count = element.statusCount;
                  this.statusItems[5].dayCount = element.statusByDateCount;
                  break;
                case Status.CANCELED:
                case Status.DELETED:
                  this.statusItems[6].count += element.statusCount;
                  this.statusItems[6].dayCount += element.statusByDateCount;
                  break;
                case Status.DELIVERED:
                case Status.PAID:
                case Status.RETURN_RECEIVED:
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

}
