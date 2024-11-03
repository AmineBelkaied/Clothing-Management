import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { combineLatest, Subject, takeUntil } from 'rxjs';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { ColorService } from 'src/shared/services/color.service';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';
import { FbPageService } from 'src/shared/services/fb-page.service';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { PacketService } from 'src/shared/services/packet.service';
import { SideBarService } from 'src/shared/services/sidebar.service';
import { SizeService } from 'src/shared/services/size.service';
import { StorageService } from 'src/shared/services/strorage.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  @Output() expansionChanged = new EventEmitter<boolean>();
  @Input() expanded: boolean = false;
  isExpanded: boolean = false;
  menuItems: any[];

  $unsubscribe: Subject<void> = new Subject();
  activeClass: boolean;
  activeRoute = true;
  readonly appName: string= 'ABYSOFT';
  userName: string;
  isLoggedIn: boolean = false;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  globalConf: GlobalConf = {
    applicationName: "AbySoft",
    oneSourceApp: true,
    noDefaultSource: false
  };
  showUserMenu: boolean = false;



  readonly clothingManagementLabel: string = 'Clothing Management';

  constructor( private packetService: PacketService,
               private storageService: StorageService,
               private globalConfService: GlobalConfService,
               private messageService: MessageService,
               private sideBarService: SideBarService,
               private colorService : ColorService,
               private sizeService : SizeService,
               private fbPageService : FbPageService,
               private deliveryCompanyService : DeliveryCompanyService,
               private router: Router) {
                this.menuItems = [
                  { label: 'Commandes', icon: 'pi pi-shopping-cart', routerLink: "/packets", isUserOption : true },
                  { label: 'Modèles', icon: 'pi pi-th-large', routerLink: "/models", isUserOption : false },
                  { label: 'Offres', icon: 'pi pi-gift', routerLink: "/offers", isUserOption : false },
                  { label: 'Stock', icon: 'pi pi-box', routerLink: "/stock", isUserOption : true },
                  { label: 'Configuration', icon: 'pi pi-cog', routerLink: "/config", isUserOption : false },
                  { label: 'Statistique', icon: 'pi pi-chart-bar', routerLink: "/statistique", isUserOption : false },
                  { label: 'Suivie packet', icon: 'pi pi-map-marker', routerLink: "/payed-return", isUserOption : false },
                  { label: 'Validation', icon: 'pi pi-check-square', routerLink: "/verification", isUserOption : true },
                ];

                this.storageService.isLoggedIn.subscribe(isLoggedIn => {
                  this.isLoggedIn = isLoggedIn;
                  this.userName = this.storageService.getUserName();
                  this.isAdmin = this.storageService.hasRoleAdmin();
                  this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
                  this.activeClass = true;

                  this.globalConfService.getGlobalConfSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
                    (globalConf: GlobalConf) => {
                      this.globalConf = globalConf;

                      combineLatest([
                        this.sizeService.getSizesSubscriber(),
                        this.colorService.getColorsSubscriber(),
                        this.fbPageService.getFbPagesSubscriber(),
                        this.deliveryCompanyService.getDeliveryCompaniesSubscriber()
                      ]).pipe(takeUntil(this.$unsubscribe))
                        .subscribe();
                    })
                });
   }

  ngOnInit(): void {
  }

  changeClass() {
    this.activeClass = !this.activeClass;
  }

  sync() {
    console.log("syncronising");
    this.packetService
        .syncAllPackets(this.storageService.getTenantName())
        .pipe(takeUntil(this.$unsubscribe))
        .subscribe({
          next: (response: number) => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: response+" packets synchronised",
              life: 1000,
            });
          },
          error: (error: Error) => {
            console.log('SyncError:', error);
          }
        });
    }

    logout() {
      this.storageService.isLoggedIn.next(false);
      this.isSuperAdmin ? this.router.navigate(["/login"]) : this.router.navigate(["/login/" + this.storageService.getTenantName()]);
      this.storageService.removeUser();
      this.showUserMenu = false;
    }


    toggleSidebar() {
      this.isExpanded = !this.isExpanded;
      if(this.isExpanded)
        this.showUserMenu = false;
      this.emitExpansionState();
    }

    toggleUserMenu() {

      if(this.isExpanded)
        {
          this.isExpanded = false
          this.emitExpansionState();
        }
        this.showUserMenu = !this.showUserMenu;
    }

    private emitExpansionState() {
      this.sideBarService.isExpanded.next(this.isExpanded);
      //this.expansionChanged.emit(this.isExpanded);
    }
}
