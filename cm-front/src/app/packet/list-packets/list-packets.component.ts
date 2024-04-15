import { AfterViewChecked, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { ConfirmationService, MessageService, SelectItemGroup, PrimeIcons, MenuItem } from 'primeng/api';
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
import { FormControl } from '@angular/forms';
import { DateUtils } from 'src/shared/utils/date-utils';
import { A_VERIFIER, BUREAU, CANCELED, CONFIRMEE, CORBEIL, ENDED, EN_COURS, EN_COURS_1, EN_COURS_2,
  EN_COURS_3, INJOIGNABLE, LIVREE, NON_CONFIRMEE, NOT_SERIOUS, PAYEE, PROBLEME, RETOUR, RETOUR_RECU,
  DELETED, TERMINE, statesList, statusList } from 'src/shared/utils/status-list';
import { City } from 'src/shared/models/City';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { firstUrl } from 'src/assets/constants';
import * as FileSaver from 'file-saver';

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
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);
  editMode = false;
  isLoading = false;
  selectedPacket: string = '';


  modelDialog!: boolean;
  submitted!: boolean;

  first = 0;
  rows = 100;
  currentPage = 0;
  oldField: string;
  offersList: any[] = [];
  allOffersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCity: City | undefined;
  //selectedCity: string | undefined;
  filter: string;

  statusList: string[] = [];
  optionButtons: MenuItem[];
  packetStatusList: string[] = [];
  statesList: string[] = [];
  selectedStatus: FormControl = new FormControl();
  selectedStates: string[] = [];
  $unsubscribe: Subject<void> = new Subject();
  pageSize : number = 100;
  params : any={
    page: 0,
    size: this.pageSize,
    startDate: this.dateUtils.formatDateToString(this.today),
    endDate: this.dateUtils.formatDateToString(this.today),
    mandatoryDate: false
  };
  loading: boolean = false;
  mandatoryDateCheckBox : boolean = false;
  oldDateFilterCheckBox : boolean = false;
  dateOptions : any[] = [{label: 'Off', value: false}, {label: 'On', value: true}];
  value : boolean = this.mandatoryDateCheckBox;
  nbrConfirmed : number= 0;

  notificationList : any [] = [
    {
      class:'pi-check-circle',
      severity:'info',
      status: CONFIRMEE,
      count: this.nbrConfirmed
    },
    {
      class:'pi-question-circle',
      severity:'info',
      status: A_VERIFIER,
      count: '0'
    },
    {
      class:'pi-power-off',
      severity:'warning',
      status: INJOIGNABLE,
      count: '0'
    },
    {
      class:'pi-phone',
      severity:'danger',
      status: NON_CONFIRMEE,
      count: '0'
    }
  ];

  @ViewChild('dt') dt?: Table;
  private readonly reg: RegExp = /,/gi;
  regBS = /\n/gi;
  private readonly FIRST: string = 'FIRST';
  countDeleted: number = 0;
  realTotalItems: number;

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
    this.createNotification();
    this.createColumns();
    this.findAllOffers();
    this.findAllGroupedCities();
    this.findAllFbPages();
    this.rangeDates = [this.today];
    this.selectedStatus.setValue([]);
  }

  createNotification(): void {
    this.packetService.syncNotification()
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: DashboardCard[]) => {
          //console.log('createDashboardResponse',response);
          //this.notificationList = response;

          if (response != null && response.length > 0) {
            response.forEach((element: any) => {
              if(element.status == A_VERIFIER) this.notificationList[1].count = element.statusCount;
              else if(element.status == INJOIGNABLE) this.notificationList[2].count = element.statusCount;
              else if(element.status == NON_CONFIRMEE) this.notificationList[3].count = element.statusCount;
            });
          }
        },
        error: (error: Error) => {
          console.log('Error:', error);
        }
      });
  }

  findAllPackets(): void {
    this.loading = true;
    this.packetService.findAllPackets(this.params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: ResponsePage) => {
          this.packets = response.result;
          this.totalItems = response.totalItems;
          //this.countDeleted =response.result.filter(packet => packet.status === DELETED).length;
          this.packets = response.result.filter((packet: any) => this.checkPacketNotNull(packet));
          //console.log("findAllPackets",response);
          this.realTotalItems = response.totalItems;
          this.totalItems = this.packets.length;
          let countConfirmed =response.result.filter(packet => packet.status === CONFIRMEE).length;

          this.notificationList[0].count =  countConfirmed > 0 ? countConfirmed :this.notificationList[0].count;
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
      { field: 'barcode', header: 'Barcode' },
    ];
  }

  findAllOffers(): void {
    this.offerService.findAllOffers().subscribe((offers: any) => {
      this.allOffersList = offers;
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
      this.fbPages = result.filter((fbPage: any) => fbPage.enabled);
    });
  }

  onEditInit(packet: any): void {
    this.oldField = packet.data[packet.field];
    console.log('onEditInit',packet);
    if ( packet.field == 'status'){
      this.packetStatusList = [];
      if(packet.data.stock === -1){
        this.messageService.add({ severity: 'error',summary: 'Error', detail: "Veiller remplir tous les champs des articles" });
        this.packetStatusList = [INJOIGNABLE]
      }
           else if (this.oldField == NON_CONFIRMEE || this.oldField == NOT_SERIOUS || this.oldField == INJOIGNABLE
        || this.oldField == CANCELED || this.oldField == DELETED|| this.oldField == ENDED ) {

        this.packetStatusList = [ NON_CONFIRMEE, ENDED, CONFIRMEE, NOT_SERIOUS, INJOIGNABLE, CANCELED];
      }
      else if (this.oldField == CONFIRMEE || this.oldField == A_VERIFIER) {
        this.packetStatusList = [ EN_COURS_1, EN_COURS_2 ,EN_COURS_3, CANCELED, A_VERIFIER, LIVREE, RETOUR, PAYEE, RETOUR_RECU, PROBLEME];
      }

      else if (this.oldField == EN_COURS_1 || this.oldField == EN_COURS_2 || this.oldField ==  EN_COURS_3) {
        this.packetStatusList = [ INJOIGNABLE, EN_COURS_1, EN_COURS_2 ,EN_COURS_3, A_VERIFIER, LIVREE, RETOUR, PAYEE,RETOUR_RECU, PROBLEME];
      }
      else if (this.oldField == LIVREE || this.oldField == PAYEE) {
        this.packetStatusList = [ PAYEE, RETOUR ,RETOUR_RECU];
      }
      else if (this.oldField == RETOUR) {
        this.packetStatusList = [ PROBLEME ,RETOUR_RECU];
      }
      else if (this.oldField == PROBLEME) {
        this.packetStatusList = [ RETOUR_RECU ,LIVREE ,PAYEE ,EN_COURS_2 ,CANCELED ];
      }
    }
  }

  onEditComplete(packet: any): void {
    if(packet.data.city == null && this.selectedCity != null && packet.field == 'city'){
      packet.data.city = this.selectedCity;
      this.updatePacket(packet.data);
      this.selectedCity = undefined;
    }

    else if (this.oldField !== packet.data[packet.field] && packet.data[packet.field] != undefined ) {

      if (packet.field == 'city' || packet.field == 'fbPage' || packet.field == 'date') {
        this.selectedCity=undefined;
        if(packet.field == 'date'){
          packet.data[packet.field].setHours(packet.data[packet.field].getHours() + 1);
        }
        this.updatePacket(packet.data);
      } else {
        let updatedField = { [packet.field]: packet.data[packet.field] };
        let msg = 'Le champ a été modifié avec succés';

        if ( packet.field == 'status' && packet.data[packet.field] != null) {

          if(packet.data[packet.field] == DELETED && packet.data.barcode != null && packet.data.barcode != "" ){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez ne pas supprimée les packets sorties' });
            packet.data[packet.field] = this.oldField;
            return;
          }

          if(packet.data[packet.field] == CANCELED && this.oldField != CONFIRMEE && this.oldField != A_VERIFIER && this.oldField != DELETED){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez ne pas annuler que les packets sorties' });
            packet.data[packet.field] = this.oldField;
            return;
          }
          if((packet.data[packet.field] == NON_CONFIRMEE
            || packet.data[packet.field] == ENDED
            || packet.data[packet.field] == NOT_SERIOUS
            ) && (
            this.oldField == EN_COURS_1
            || this.oldField == EN_COURS_2
            || this.oldField == EN_COURS_3
            )){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Ce Colis est déja en cours' });
            packet.data[packet.field] = this.oldField;
            return;
          }

          if((packet.data[packet.field] == EN_COURS_1
            || packet.data[packet.field] == EN_COURS_2
            || packet.data[packet.field] == EN_COURS_3
            || packet.data[packet.field] == CANCELED
            || packet.data[packet.field] == LIVREE
            || packet.data[packet.field] == RETOUR
            || packet.data[packet.field] == RETOUR_RECU
            || packet.data[packet.field] == PAYEE
            ) && (
            packet.data.barcode == null || packet.data.barcode == ""
            )){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: "Ce Colis n'a pas sorti" });
            packet.data[packet.field] = this.oldField;
            return;
          }

          if((packet.data[packet.field] == NON_CONFIRMEE
            || packet.data[packet.field] == ENDED
            || packet.data[packet.field] == NOT_SERIOUS
            || packet.data[packet.field] == NON_CONFIRMEE
            || packet.data[packet.field] == ENDED
            || packet.data[packet.field] == NOT_SERIOUS
            || packet.data[packet.field] == INJOIGNABLE
            ) && (
              this.oldField == LIVREE
            || this.oldField == RETOUR
            || this.oldField == RETOUR_RECU
            || this.oldField == PAYEE
            )){
            this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Ce Colis est déja Terminée' });
            packet.data[packet.field] = this.oldField;
            return;
          }

          if(packet.data[packet.field] == CONFIRMEE){
            if (!this.checkPacketValidity(packet.data)) {
              packet.data[packet.field] = this.oldField;
              return;
            }
            if (!this.checkPacketDescription(packet.data)) {
              this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir la taille de l article' });
              packet.data[packet.field] = this.oldField;
              return;
            }

            this.selectedPacket = packet['data'].id;
            this.isLoading = true;
          }
          if(packet.data[packet.field] == null || packet.data[packet.field] == undefined){
              packet.data[packet.field] = this.oldField;
              this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Champ vide' });
              return;
          }
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
          .subscribe({
              next: (responsePacket: Packet) => {
                this.createNotification();
                console.log(packet.data);
                if(packet.data.stock < 10 && packet.field === 'status'
                 && ((packet.data[packet.field] === CONFIRMEE && responsePacket.barcode != null)
                 || packet.data[packet.field] === CANCELED
                 || packet.data[packet.field] === RETOUR_RECU))
                 {
                   let x =this.getLastStock(packet.data.id)
                   console.log("x",x);

                 }
                //this.findAllPackets();
                console.log("refresh all list");

                if (packet.field === 'status' && packet.data[packet.field] === 'Confirmée') {

                  this.isLoading = false;
                  if (responsePacket.barcode != null) {

                      let pos = this.packets.map((packet: Packet) => packet.id).indexOf(responsePacket.id);
                      this.packets.splice(pos, 1, responsePacket);
                      msg = 'Le barcode a été crée avec succés';
                      this.notificationList[0].count +=1;

                  } else {
                    packet.data[packet.field] = this.oldField;
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur lors de la creation du barcode' });
                  }
                }
                else if(responsePacket.oldClient!= undefined){
                  let pos = this.packets.map((packet: Packet) => packet.id).indexOf(responsePacket.id);
                    this.packets.splice(pos, 1, responsePacket);
                };
                this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
                  },
                  error : (error: Error) => {
                    console.log(error);
                  }
                });

      }
    }
  }

  checkPacketValidity(packet: Packet): boolean {
      if (!(this.isValid(packet.fbPage) && this.isValid(packet.address) && this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) && this.isValid(packet.city) && this.isValid(packet.packetDescription)))
      {
        this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir tous les champs' });
        return false;
      }
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est Valid'});
      return true;
  }

  onSubmit(event:Event) {

      this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Stock 0-Veiller le remplacer par un fake size.',
        icon: 'pi pi-exclamation-circle',
        acceptIcon: 'pi pi-check mr-1',
        rejectIcon: 'pi pi-times mr-1',
        rejectButtonStyleClass: 'p-button-danger p-button-sm',
        acceptButtonStyleClass: 'p-button-outlined p-button-sm',
        accept: () => {
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Veiller remplir fake size', life: 3000 });
        },
        reject: () => {
            //this.submitProductsOffers(productsOffers,false);
            this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 });
        }
      });
  }

  checkPacketNotNull(packet: Packet): boolean {
    //console.log('phVal',this.isValid(packet.customerPhoneNb)+" aa "+packet.customerPhoneNb+'bb'+packet.id);

    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || this.isValid(packet.city) || this.isValid(packet.packetDescription));
  }

  checkPacketDescription(packet: Packet): boolean {
    return packet.packetDescription!= undefined && packet.packetDescription.includes('(');
  }

  newPacket(): Packet {
    return {
      date: this.dateUtils.getDate(this.today),
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
      oldClient : 0,
      valid : false,
      stock : -1
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
    if (packet.status != PAYEE && packet.status != RETOUR_RECU && packet.status != LIVREE)
    this.packetService.getLastStatus(packet, this.FIRST)
      .subscribe({
          next: (response: Packet) => {
            this.packets.splice(this.packets.indexOf(packet), 1, response);
            this.createNotification();
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  getLastStock(packetId: number): void {
    console.log("getLasStock-packetId", packetId);
    //if (packet.status != PAYEE && packet.status != RETOUR_RECU && packet.status != LIVREE)
    this.packetService.getLastStock(packetId)
      .subscribe({
          next: (listupdatedStock:any) => {
            console.log("getLasStock-listupdatedStock", listupdatedStock);
            listupdatedStock.forEach((element: any) => {
              console.log("element",element);
              let pos = this.packets.map((packet: Packet) => packet.id).indexOf(element.id);
              this.packets.splice(pos, 1, element);
              //this.packets.splice(this.packets.indexOf(element), 1, element);
            });
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  openLinkGetter(code: any): void {
    window.open(firstUrl+"/recherche.php?code=" + code, '_blank');
  }

  printFirst(link: string): void {
    window.open(link, '_blank');
  }

  showTimeLineDialog(packet: Packet): void {
    try {
      this.packetService.getPacketTimeLine(packet.id).subscribe((response: any) => {
          this.statusEvents = [];
          this.suiviHeader = "Suivi Historique - Commande N° " + packet.id;
          if (response != null && response.length > 0) {
            response.forEach((element: any) => {
              this.statusEvents.push({status: element.status, date: element.date, user: element.user?.fullName, icon: PrimeIcons.ENVELOPE, color: '#9C27B0'});
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
    if(this.loading == false){
      this.loading=true;
      this.packetService
      .addPacket(this.newPacket())
      .subscribe((response: Packet) => {
        this.loading=false;
        this.packets.unshift(response);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés', life: 1000 });
      });
    }
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
    let pos = this.packets.map((packet: Packet) => packet.id).indexOf($event.packet.id);
    this.packets.splice(pos, 1, $event.packet);
    this.editMode ? this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été mis à jour avec succés', life: 1000 }) : this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été ajoutés avec succés', life: 1000 });
  }

  checkValidity(date1: Date, date2: Date, status: String): boolean {
    if (status != PAYEE && status != RETOUR_RECU)
      return this.dateUtils.getDate(date1) < this.dateUtils.getDate(date2);
    return false;
  }

  handleInputChange() {
    const inputValue = this.filter;
    const numbersCount = (inputValue.match(/\d/g) || []).length;
    if (this.filter === '' || numbersCount === 5 || numbersCount === 8 || numbersCount === 12  ) {
      this.filterPackets('global');
    }
  }

  filterPackets($event?: string): void {
    this.createRangeDate();
    //this.setPacketStatusList();
    let page = 0;

    if ($event == 'states') {
      this.onStateChange();
    }
    else if ($event == 'clear') {
      this.selectedStates = [];
      //this.packetStatusList = this.statusList;
      this.selectedStatus.setValue([]);
    }else if($event == 'page')
      page = this.currentPage;
    if (this.selectedStatus.value == null) this.selectedStatus.setValue([]);

    this.params = {
      page: page,
      size: this.pageSize,
      searchText: this.filter != null && this.filter != '' ? this.filter : null,
      startDate: this.dateUtils.formatDateToString(this.startDate),
      endDate: this.dateUtils.formatDateToString(this.endDate),
      status: this.selectedStatus.value.length == 0 ? null : this.selectedStatus.value.join(),
      mandatoryDate: this.mandatoryDateCheckBox
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

  onNotificationClick($event?: string): void{
    //console.log("aaaa",$event);
    this.selectedStatus.setValue([]);
    this.selectedStatus.patchValue([$event]);
    this.filterPackets('status');
  }

  onStateChange(): void {
    this.selectedStatus.setValue([]);
    //this.packetStatusList = [];
    if (this.selectedStates.indexOf(CORBEIL) > -1) {
      this.selectedStatus.patchValue([DELETED]);
    }

    if (this.selectedStates.indexOf(BUREAU) > -1) {
      this.selectedStatus.patchValue([ NON_CONFIRMEE, ENDED, A_VERIFIER, CONFIRMEE ]);
    }
    if (this.selectedStates.indexOf(EN_COURS) > -1) {
      this.selectedStatus.patchValue([ A_VERIFIER, LIVREE, PAYEE, RETOUR, RETOUR_RECU]);
      //this.packetStatusList = this.statusList;
    }
    if (this.selectedStates.indexOf(TERMINE) > -1) {
      this.selectedStatus.patchValue([PAYEE, RETOUR_RECU]);
    }
  }


  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.pageSize = $event.rows;
    this.filterPackets('page');
  }

  resetTable(): void{
    //console.log('reset');

    this.selectedStates = [];
    this.selectedPackets = [];
    this.selectedStatus.setValue([]);
    this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now())];
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
    this.packetStatusList = this.statusList;
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

  checkCodeABarreExist(packet:Packet){
    return packet!= undefined && packet.barcode !="" && packet.barcode!= null
  }

  checkPhoneNbExist(packet:Packet){
    return packet!= undefined && packet.customerPhoneNb !="" && packet.customerPhoneNb!= null
  }

  mandatoryDateChange(){
    if(this.selectedStatus.value != null && this.selectedStatus.value.length > 0)
    this.filterPackets('global')
  }

  oldDateFilter(){
      this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now() - 86400000)];
      //this.createRangeDate();
      this.filterPackets('global');

    //console.log('aaa',this.rangeDates);
  }
  todayDate(){
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.rangeDates[0]=this.startDate;
        this.endDate = this.today;
        this.rangeDates= [this.startDate,this.today];
        //this.createRangeDate();
      }
    else this.rangeDates = [this.today];
    //this.createRangeDate();
    this.filterPackets('global');
  }

  clearDate(){
    this.rangeDates = [];
    //this.createRangeDate();
    this.filterPackets('global');
  }

  openShowOptionMenu(packet:any) {
    console.log("pp",packet);

    this.optionButtons = [
      {
        label: 'Duplicate',
        icon: 'pi pi-refresh',
        disabled:!this.checkCodeABarreExist(packet),
        command: () => {
          this.duplicatePacket(packet)
        }
      },
      {
        label: 'Valider stock',
        icon: 'pi pi-refresh',
        disabled:packet.stock>15 || packet.stock==-1,
        command: () => {
          this.getLastStock(packet.id)
        }
      },
      {
        label: 'Chercher Tel',
        icon: 'pi pi-phone',
        disabled:!this.checkPhoneNbExist(packet),
        command: () => {
          this.filter= packet.customerPhoneNb;
          this.filterPackets('phone');
        }
      },
      {
        label: 'Historique',
        icon: 'pi pi-history',
        disabled:false,
        command: () => {
          this.showTimeLineDialog(packet);
        }
      },
      { separator: true },
      {
        label: 'Actualiser status',
        icon: 'pi pi-sync',
        disabled:!(this.checkCodeABarreExist(packet)),
        command: () => {
          this.getLastStatus(packet);
        }
      },
      {
        label: 'Tel First',
        icon: 'pi pi-search-plus',
        disabled:!(this.checkPhoneNbExist(packet)),
        command: () => {
          this.openLinkGetter(packet.customerPhoneNb);
        }
      },
      {
        label: 'BarreCode First',
        icon: 'pi pi-qrcode',
        disabled:!this.checkCodeABarreExist(packet),
        command: () => {
          this.openLinkGetter(packet.barcode)
        }
      },
      {
        label: 'Print First',
        icon: 'pi pi-print',
        disabled:!(this.checkCodeABarreExist(packet)),
        command: () => {
          this.printFirst(packet.printLink)
        }
      }
  ];
  }

  exportCSV() {
    let packets: any[] = [];
    let selectedPackets = this.selectedPackets.map((p) => p.id);
    packets = this.packets.slice();
    if (this.filter != '' && this.filter != null)
      packets = this.dt!.filteredValue;
    if (this.selectedPackets.length > 0) {
      let filteredPackets = packets.filter(
        (packet) => selectedPackets.indexOf(packet.id) > -1
      );
      if (filteredPackets.length > 0) packets = filteredPackets.slice();
    }

    // map the packets to a customer packetList
    packets = packets?.map(
      (packet: any) =>
      (packet = {
        destinataire_nom: this.getValue(packet.customerName),
        adresse: this.getValue(packet.address).replace(this.regBS, ' '),
        ville: this.getValue(packet.city?.name),
        gouvernorat: this.getValue(packet.city?.governorate.name),
        telephone: this.getPhoneNumber1(packet.customerPhoneNb),
        telephone2: this.getPhoneNumber2(packet.customerPhoneNb),
        nombre_de_colis: 1,
        prix: this.getValue(packet.price-packet.discount+packet.deliveryPrice),
        designation:
          this.getValue(packet.id) +
          ' ' +
          this.getValue(packet.fbPage?.name) +
          ' | ' +
          this.getValue(packet.packetDescription?.replace(this.regBS, ', ')),
        commentaire:
          'Le colis peut etre ouvert lors de la commande du client',
        barcode: this.getValue(packet.barcode),
      })
    );

    // prepare the columns to be exported
    let cols: any[] = [
      { field: 'destinataire_nom', header: 'destinataire_nom' },
      { field: 'adresse', header: 'adresse' },
      { field: 'ville', header: 'ville' },
      { field: 'gouvernorat', header: 'gouvernorat' },
      { field: 'telephone', header: 'telephone' },
      { field: 'telephone2', header: 'telephone2' },
      { field: 'nombre_de_colis', header: 'nombre_de_colis' },
      { field: 'prix', header: 'prix' },
      { field: 'designation', header: 'designation' },
      { field: 'commentaire', header: 'commentaire' },
      { field: 'barcode', header: 'Barcode' },
    ];
    let csv = '';
    let csvSeparator = ';';
    //headers
    for (let i = 0; i < cols.length; i++) {
      if (cols[i].field) {
        csv += cols[i].field;

        if (i < cols.length - 1) {
          csv += csvSeparator;
        }
      }
    }
    //body
    packets?.forEach((record: any, j) => {
      csv += '\n';
      for (let i = 0; i < cols.length; i++) {
        if (cols[i].field) {
          //console.log(record[cols[i].field]);
          // resolveFieldData seems to check if field is nested e.g. data.something --> probably not needed
          csv += record[cols[i].field]; //this.resolveFieldData(record, this.columns[i].field);
          if (i < cols.length - 1) {
            csv += csvSeparator;
          }
        }
      }
    });
    this.download(csv, 'first - ' + this.dateUtils.formatDateToString(new Date()));
  }

  download(text: any, filename: any) {
    let element = document.createElement('a');
    element.setAttribute(
      'href',
      'data:text/csv;charset=utf-8,' + encodeURIComponent(text)
    );
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
  }

  selectCity( packet: Packet) {
    this.selectedCity = packet.city;
}

}

    /*exportExcel() {
    console.log(this.dt?._totalRecords);
    console.log(this.dt?.totalRecords);
    let packets: any[] = [];
    let selectedPackets = this.selectedPackets.map((p) => p.id);
    packets = this.packets.slice();
    if (this.filter != '' && this.filter != null)
      packets = this.dt!.filteredValue;
    if (this.selectedPackets.length > 0) {
      let filteredPackets = packets.filter(
        (packet) => selectedPackets.indexOf(packet.id) > -1
      );
      if (filteredPackets.length > 0) packets = filteredPackets.slice();
    }

    packets = packets?.map(
      (packet: any) =>
      (packet = {
        Id: packet.id,
        Prix: packet.price + packet.deliveryPrice - packet.discount,
        Références: packet.packetDescription,
        PageFB: packet.fbPage?.name,
      })
    );
    import('xlsx').then((xlsx) => {
      const worksheet = xlsx.utils.json_to_sheet(packets);
      const workbook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
      const excelBuffer: any = xlsx.write(workbook, {
        bookType: 'xlsx',
        type: 'array',
      });
      this.saveAsExcelFile(excelBuffer, 'products');
    });
  }

/*   saveAsExcelFile(buffer: any, fileName: string): void {
    let EXCEL_TYPE =
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
    let EXCEL_EXTENSION = '.xlsx';
    const data: Blob = new Blob([buffer], {
      type: EXCEL_TYPE,
    });
    FileSaver.saveAs(
      data,
      fileName + ' - ' + this.dateUtils.formatDateToString(new Date()) + EXCEL_EXTENSION
    );
  } */
