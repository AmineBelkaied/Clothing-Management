import { AfterViewChecked, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { ConfirmationService, MessageService, SelectItemGroup} from 'primeng/api';
import { Packet } from '../../../shared/models/Packet';
import { OfferService } from '../../../shared/services/offer.service';
import { PacketService } from '../../../shared/services/packet.service';
import { DatePipe } from '@angular/common';
import { Table } from 'primeng/table';
import { CityService } from '../../../shared/services/city.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from '../../../shared/services/fb-page.service';
import { catchError, identity, Observable, of, Subject,takeUntil} from 'rxjs';
import { Offer } from 'src/shared/models/Offer';
import { PrimeIcons } from 'primeng/api';
import { FormControl } from '@angular/forms';
import { DateUtils } from 'src/shared/utils/date-utils';
import { A_VERIFIER, BUREAU, CONFIRMEE, CORBEIL, EN_COURS, EN_COURS_1, EN_COURS_2, EN_COURS_3, EN_RUPTURE, LIVREE, NON_CONFIRMEE, PAYEE, RETOUR_EXPEDITEUR, RETOUR_RECU, SUPPRIME, TERMINE, statesList, statusList } from 'src/shared/utils/status-list';
import { City } from 'src/shared/models/City';
import { ResponsePage } from 'src/shared/models/ResponsePage';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe],
})
export class ListPacketsComponent implements OnInit, AfterViewChecked, OnDestroy {

  display: boolean = false;
  displayStatus: boolean = false;
  suiviHeader: string = 'Suivi';
  events: any[] = [];
  statusEvents: any[] = [];
  packets: Packet[];
  totalItems: number;
  packet: Packet;
  cols: object[] = [];
  selectedPackets: Packet[] = [];
  rangeDates: Date[] = [];
  startDate: Date = new Date();
  endDate: Date = new Date();
  editMode = false;
  isLoading = false;
  selectedPacket: string = '';
  today_2: Date = new Date(Date.now() - 172800000);

  modelDialog!: boolean;
  submitted!: boolean;

  first = 0;
  rows = 100;
  currentPage = 0;
  oldField: string;
  offersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCity: City;
  filter: string;

  statusList: string[] = [];
  selectedStatusList: string[] = [];
  statesList: string[] = [];
  selectedStatus: FormControl = new FormControl();
  selectedStates: string[] = [];
  $unsubscribe: Subject<void> = new Subject();
  showDeleted : boolean = false;
  pageSize : number = 100;
  params : any;
  loading: boolean = false;

  @ViewChild('dt') dt?: Table;
  private readonly reg: RegExp = /,/gi;
  private readonly FIRST: string = 'FIRST';

  constructor(
    private messageService: MessageService,
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private offerService: OfferService,
    private cityService: CityService,
    private fbPageService: FbPageService,
    private dateUtils: DateUtils,
    private cdRef: ChangeDetectorRef
  ) {
    this.statusList = statusList;
    this.statesList = statesList;
  }

  ngAfterViewChecked() {
    this.cdRef.detectChanges();
  }

  ngOnInit(): void {
    this.params = {
      page: 0,
      size: this.pageSize,
      startDate: this.dateUtils.formatDateToString(new Date()),
      endDate: this.dateUtils.formatDateToString(new Date())
    };
    this.findAllPackets();
    this.createColumns();
    this.findAllOffers();
    this.findAllGroupedCities();
    this.findAllFbPages();
    this.rangeDates[0] = new Date();
    this.selectedStatus.setValue([]);
    this.selectedStatusList = this.statusList;
  }

  findAllPackets(): void {
    this.loading = true;
    this.packetService.findAllPackets(this.params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: ResponsePage) => {
          this.packets = response.result;
          this.totalItems = response.totalItems;
          console.log(this.packets[0]);
          this.loading = false;
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
  }

  createColumns(): void {
    this.cols = [
      { field: 'date', header: 'Date' },
      { field: 'fbPage.name', header: 'PageFB' },
      { field: 'customerName', header: 'Client', customExportHeader: 'Product Code'},
      { field: 'customerPhoneNb', header: 'Téléphone' },
      { field: 'city', header: 'Ville' },
      { field: 'address', header: 'Adresse' },
      { field: 'relatedProducts', header: 'Articles' },
      { field: 'price', header: 'Prix' },
      { field: 'status', header: 'Statut' },
      { field: 'Help', header: 'Help' },
      { field: 'barcode', header: 'Barcode' },
    ];
  }

  findAllOffers(): void {
    this.offerService.findAllOffers().subscribe((offers: any) => {
      this.offersList = offers.filter((offer: Offer) => offer.enabled);
    });
  }

  findAllGroupedCities(): void {
    this.cityService.findAllGroupedCities().subscribe((groupedCities: any) => {
      this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
      this.groupedCities = [...new Set(this.groupedCities)];
    });
  }

  findAllFbPages(): void {
    this.fbPageService.findAllFbPages().subscribe((result: any) => {
      this.fbPages = result;
    });
  }

  onEditInit(packet: any): void {
    this.oldField = packet.data[packet.field];
  }

  onEditComplete(packet: any): void {
    if (this.oldField !== packet.data[packet.field]) {
      if (packet.field == 'city' || packet.field == 'fbPage') {
        this.updatePacket(packet.data);
      } else {
        let updatedField = { [packet.field]: packet.data[packet.field] };
        let msg = 'Le champ a été modifié avec succés';
        if ( packet.field == 'status' && (packet.data[packet.field] == CONFIRMEE)) {
          if (!this.checkPacketValidity(packet.data)) {
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir tous les champs' });
            packet.data[packet.field] = this.oldField;
            return;
          }
          this.selectedPacket = packet['data'].id;
          this.isLoading = true;
        }
        this.packetService
          .patchPacket(packet['data'].id, updatedField)
          .pipe(
            catchError((err: any, caught: Observable<any>): Observable<any> => {
              this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la mise à jour ' + err.error.message });
              packet.data[packet.field] = this.oldField;
              this.isLoading = false;
              return of();
            })
          )
          .subscribe((responsePacket: Packet) => {
            if (packet.field === 'status' && ((packet.data[packet.field] === 'Confirmée') || (packet.data[packet.field] === 'Retour Echange'))) {
              this.isLoading = false;
              if (responsePacket.barcode != null) {
                let pos = this.packets.map((packet: Packet) => packet.id).indexOf(responsePacket.id);
                this.packets.splice(pos, 1, responsePacket);
                msg = 'Le barcode a été crée avec succés';
                if (packet.data[packet.field] === 'Retour Echange') {
                 this.oldField === PAYEE ? packet.data[packet.field] = PAYEE : packet.data[packet.field] = LIVREE;
                }
              } else {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la creation du barcode' });
              }
            }
            this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
          });
      }
    }
  }

  checkPacketValidity(packet: Packet): boolean {
    return (this.isValid(packet.fbPage) && this.isValid(packet.address) && this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) && this.isValid(packet.city) && this.isValid(packet.packetDescription));
  }

  newPacket(): Packet {
    return {
      date: this.dateUtils.getDate(new Date()),
      barcode: '',
      lastDeliveryStatus: '',
      customerName: '',
      customerPhoneNb: '',
      address: '',
      relatedProducts: '',
      packetReference: '',
      packetDescription: '',
      price: 0,
      status: 'Non confirmée',
      exchange: false,
    };
  }

  updatePacket(packet: any): void {
    this.packetService.updatePacket(packet)
      .pipe(
        catchError((err: any, caught: Observable<any>): Observable<any> => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la mise à jour ' + err.error.message });
          packet.data[packet.field] = this.oldField;
          return of();
        })
      )
      .subscribe((response: any) => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés'});
      });
  }

  getLastStatus(packet: Packet): void {
    if (packet.status != PAYEE && packet.status != RETOUR_RECU && packet.status != RETOUR_EXPEDITEUR && packet.status != LIVREE)
    this.packetService.getLastStatus(packet, this.FIRST)
      .subscribe({
          next: (response: Packet) => {
            this.packets.splice(this.packets.indexOf(packet), 1, response);
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  openLinkGetter(code: number): void {
    window.open("https://www.firstdeliverygroup.com/fournisseur/recherche.php?code=" + code, '_blank');
  }

  printFirst(link: string): void {
    window.open(link, '_blank');
  }

  showDialogStatus(packet: Packet): void {
    try {
      this.packetService.getPacketAllStatus(packet.id).subscribe((response: any) => {
          this.statusEvents = [];
          this.suiviHeader = 'Suivi Status de packet num :' + packet.id;
          if (response != null && response.length > 0) {
            response.forEach((element: any) => {
              this.statusEvents.push({status: element.status, date: element.date, icon: PrimeIcons.ENVELOPE, color: '#9C27B0'});
            });
          }
          this.cdRef.detectChanges();
          this.displayStatus = true;
        }
      );
    } catch (error) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur dans le status'});
    }
  }

  addNewRow(): void {
    this.packetService
      .addPacket(this.newPacket())
      .subscribe((response: Packet) => {
        this.packets.unshift(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés', life: 1000 });
      });
  }

  duplicatePacket(packet: Packet): void {
    this.packetService
      .duplicatePacket(packet.id)
      .subscribe((response: Packet) => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est dupliqué avec succés', life: 1000});
        this.packets.unshift(response);
      });
  }

  deleteSelectedPackets(): void {
    let selectedPacketsById = this.selectedPackets.map((selectedPacket: Packet) => selectedPacket.id);
    this.confirmationService.confirm({
      message:'Êtes-vous sûr de vouloir supprimer les commandes séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.packetService
          .deleteSelectedPackets(selectedPacketsById)
          .subscribe(() => {
            this.packets = this.packets.filter((packet: Packet) => selectedPacketsById.indexOf(packet.id) == -1);
            this.selectedPackets = [];
            this.messageService.add({severity: 'success',summary: 'Succés', detail: 'Les commandes séléctionnées ont été supprimé avec succés', life: 1000});
          });
      }
    });
  }

  openNew(packet: Packet): void {
    this.packet = Object.assign({}, packet);
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = false;
  }

  editProducts(packet: Packet): void {
    this.packet = Object.assign({}, packet);
    this.submitted = false;
    this.modelDialog = true;
    this.editMode = true;
  }

  hideDialog(): void {
    this.modelDialog = false;
    this.submitted = false;
  }

  OnSubmit($event: any): void {
    this.modelDialog = $event.modelDialog;
    let packet = this.packets.filter((p: any) => p.id == $event.packet.idPacket)[0];
    packet.packetDescription = $event.packet.packetDescription?.replace(this.reg, '\n');
    packet.price = $event.packet.totalPrice
    packet.deliveryPrice = $event.packet.deliveryPrice
    packet.discount = $event.packet.discount;
    this.editMode ? this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été mis à jour avec succés', life: 1000 }) : this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été ajoutés avec succés', life: 1000 });
  }

  checkValidity(date1: Date, date2: Date, status: String): boolean {
    if (status != PAYEE && status != RETOUR_RECU)
      return this.dateUtils.getDate(date1) < this.dateUtils.getDate(date2);
    return false;
  }

  filterPackets($event?: string): void {
    this.createRangeDate();
    if ($event == 'states') {
      this.onStateChange();
    }
    if ($event == 'clear') {
      this.selectedStates = [];
      this.selectedStatusList = this.statusList;
      this.selectedStatus.setValue([]);
    }
    if (this.selectedStatus.value == null) this.selectedStatus.setValue([]);
    this.params = {
      page: this.currentPage,
      size: this.pageSize,
      searchText: this.filter != null && this.filter != '' ? this.filter : null,
      startDate: this.rangeDates !== null && this.rangeDates.length > 0 ? this.dateUtils.formatDateToString(this.startDate) : null,
      endDate: this.rangeDates !== null && this.rangeDates.length > 0 ? this.dateUtils.formatDateToString(this.endDate) : null,
      status: this.selectedStatus.value.length == 0 ? null : this.selectedStatus.value.join()
    };
    this.findAllPackets();
  }

  createRangeDate(): void {
    if (this.rangeDates !== null && this.rangeDates !== undefined) {
      this.startDate = this.rangeDates[0];
      if (this.rangeDates[1]) {
        this.endDate = this.rangeDates[1];
      } else {
        this.endDate = this.startDate;
      }
    }
  }

  onStateChange(): void {
    this.showDeleted = false;
    this.selectedStatus.setValue([]);
    this.selectedStatusList = [];
    if (this.selectedStates.indexOf(CORBEIL) > -1) {
      this.showDeleted = true;
      this.selectedStatus.patchValue([SUPPRIME]);
    }

    if (this.selectedStates.indexOf(BUREAU) > -1) {
      this.selectedStatus.patchValue([ NON_CONFIRMEE, EN_RUPTURE, A_VERIFIER, CONFIRMEE ]);
      this.selectedStatusList = [ NON_CONFIRMEE, EN_RUPTURE, CONFIRMEE];
    }
    if (this.selectedStates.indexOf(EN_COURS) > -1) {
      this.selectedStatus.patchValue([ EN_COURS_1, EN_COURS_2, EN_COURS_3 ]);
      this.selectedStatusList = this.statusList;
    }
    if (this.selectedStates.indexOf(TERMINE) > -1) {
      this.selectedStatus.patchValue([PAYEE, RETOUR_RECU]);
    }
  }

  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.pageSize = $event.rows;
    this.filterPackets('global');
  }

  resetTable(): void{
    this.rangeDates = [];
    this.selectedStates = [];
    this.selectedPackets = [];
    this.selectedStatus.setValue([]);
    this.filterPackets('global');
  }

  changeColor(this: any): void {
    this.style.color = 'red';
  }

  calculatePrice(packet: Packet): number {
    return packet.price! + packet.deliveryPrice! - packet.discount!;
  }

  getValue(fieldName: any): string {
    return fieldName != null && fieldName != undefined ? fieldName : '';
  }

  isValid(field: any) {
    return field != null && field != undefined && field != '';
  }

  trackByFunction = (index: any, item: { id: any }) => {
    return item.id;
  };

  getPhoneNumber1(phoneNumber1: string): string {
    if (this.getValue(phoneNumber1) != '' && phoneNumber1.includes('/')) {
      return phoneNumber1.substring(0, 8);
    }
    return this.getValue(phoneNumber1);
  }

  getPhoneNumber2(phoneNumber: string): string {
    if (this.getValue(phoneNumber) != '' && phoneNumber.includes('/')) {
      return phoneNumber.substring(9, phoneNumber.length);
    }
    return '';
  }

  clearStatus(): void {
  this.selectedStates = [];
    this.selectedStatusList = this.statusList;
    this.selectedStatus.setValue([]);
    if (this.filter != '' && this.filter != null) {
      this.dt!.reset();
      this.dt!.filterGlobal(this.filter, 'contains');
    }
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
