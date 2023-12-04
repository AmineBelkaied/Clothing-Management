import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProductHistoryService } from 'src/shared/services/product-history.service';
import { PacketService } from 'src/shared/services/packet.service';
import { Subject, takeUntil } from 'rxjs';
import { DashboardCard } from 'src/shared/models/DashboardCard';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  $unsubscribe: Subject<void> = new Subject();
  cards : DashboardCard[] = [];

  constructor(private productHistoryService: ProductHistoryService, private packetService: PacketService,) { }

  ngOnInit(): void {
    this.createDashboard();
  }

  createDashboard(): void {
    this.packetService.createDashboard()
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          this.cards = response;
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
  }

  count(index : number){
    return this.cards[index].statusCount;
  }
}
