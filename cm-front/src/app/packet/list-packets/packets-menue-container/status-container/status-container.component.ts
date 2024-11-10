import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { Status } from 'src/shared/models/status';
import { PacketService } from 'src/shared/services/packet.service';
import { StorageService } from 'src/shared/services/strorage.service';

import {
  CANCELED, OOS, NOT_SERIOUS, PROBLEME,
  DELETED,
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
  @Input() params!: any;
  @Input() activeIndex: number;
  selectedStatus: FormControl = new FormControl();
  oldActiveIndex: number = 2;
  $unsubscribe: Subject<void> = new Subject();
  @Output() statusChange = new EventEmitter<Status>();
  isAdmin: boolean;
  statusItems: Status[];
  selectedIndex: number | null = null;
  borderIndex: number = 2;
  item: Status;
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

}
