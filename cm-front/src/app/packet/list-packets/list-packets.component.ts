
import { ConfirmationService, SelectItemGroup, PrimeIcons, MenuItem } from 'primeng/api';
import { MessageService } from 'primeng/api';
import { Packet } from '../../../shared/models/Packet';
import { OfferService } from '../../../shared/services/offer.service';
import { PacketService } from '../../../shared/services/packet.service';
import { DatePipe } from '@angular/common';
import { Table, TableRowSelectEvent } from 'primeng/table';
import { CityService } from '../../../shared/services/city.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from '../../../shared/services/fb-page.service';
import { catchError, EMPTY, Observable, Subject,takeUntil} from 'rxjs';
import { StorageService } from 'src/shared/services/strorage.service';
import { DateUtils } from 'src/shared/utils/date-utils';
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
import { City } from 'src/shared/models/City';
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { DashboardCard } from 'src/shared/models/DashboardCard';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ContextMenu } from 'primeng/contextmenu';
import { ProductService } from 'src/shared/services/product.service';
import { ClientReason, ClientReasonDetails } from 'src/shared/enums/client-reason';
import { Note } from 'src/shared/models/Note';
import { StringUtils } from 'src/shared/utils/string-utils';
import { CustomSelectItem } from 'src/shared/models/CustomSelectItem';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe]
})
export class ListPacketsComponent implements OnInit, OnDestroy {
  statusItemsLabel: string;

  onRowSelect($event: TableRowSelectEvent) {
    console.log($event);

  }

  x: number;

  @ViewChild('contextMenu', { static: false }) contextMenu: ContextMenu;
  display: boolean = false;
  displayStatus: boolean = false;
  suiviHeader: string = 'Suivi';
  events: any[] = [];
  statusEvents: any[] = [];
  packets: Packet[] = [];
  totalItems: number;
  packet: Packet;
  cols: object[] = [];
  selectedPackets: Packet[] = [];
  rangeDates: Date[] = [];
  beginDate: Date = new Date();
  endDate: Date | null = new Date();
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);
  editMode = false;
  isLoading = false;
  selectedPacket: Packet;


  modelDialog!: boolean;

  first = 0;
  rows = 100;
  currentPage = 0;
  oldFieldValue: string = "";
  offersIdsListByFbPage: any[] = [];
  allOffersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCityId: number | undefined;
  //selectedCity: string | undefined;
  filter: string;

  enCoursStatus: string[] = [];
  statusList: string[] = [];
  optionButtons: MenuItem[];
  packetStatusList: string[] = [];
  statesList: string[] = [];
  selectedStatus: FormControl = new FormControl();
  selectedStates: string[] = [];
  $unsubscribe: Subject<void> = new Subject();
  pageSize: number = 100;
  params: any = {
    page: 0,
    size: this.pageSize,
    beginDate: this.dateUtils.formatDateToString(this.today),
    endDate: this.dateUtils.formatDateToString(this.today),
    mandatoryDate: false
  };
  loading: boolean = false;
  mandatoryDateCheckBox: boolean = false;
  oldDateFilterCheckBox: boolean = false;
  dateOptions: any[] = [{ label: 'Off', value: false }, { label: 'On', value: true }];
  value: boolean = this.mandatoryDateCheckBox;
  nbrConfirmed: number = 0;
  countUNREACHABLE: string = "0";

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
  showStatus: boolean = false;
  activeIndex: number = 2;
  oldActiveIndex: number = 2;

  @ViewChild('dt') dt: Table;
  //private readonly reg: RegExp = /,/gi;
  regBS = /\n/gi;
  private readonly FIRST: string = 'FIRST';
  countCanceled: number = 0;
  realTotalItems: number;
  selectedPhoneNumber: string = '';
  //deliveryCompanyName?: DeliveryCompany;
  @Output() confirmEvent: EventEmitter<string> = new EventEmitter<string>();
  visibleNote: boolean = false;
  activeClass: boolean;
  userName: string;
  isLoggedIn: boolean;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  meterGroupValue= [
    { label: 'Space used', value: 15, color: '#34d399' }
  ];
  selectedField: keyof Packet;
  explanation: string;
  clientReasons: any[];
  note: Note = {
    date: new Date()
  };
  selectedPacketNotes: Note[] = [];
  @ViewChild("expRef") explanationElement: ElementRef;
  explanationTitle: string;
  noteActionStatus: string;

  constructor(
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private offerService: OfferService,
    private cityService: CityService,
    private fbPageService: FbPageService,
    private dateUtils: DateUtils,
    public storageService: StorageService,
    public messageService:MessageService,
    private cdRef: ChangeDetectorRef,
    private productService : ProductService
    ) {
    this.statusList = statusList;
    this.statesList = statesList;
  }



  ngAfterViewChecked(){
    this.cdRef.detectChanges();
  }

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
      this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
      this.activeClass = true;
    });
    this.rangeDates = [this.today,this.today];
    this.offerService.getOffersSubscriber();
    this.createColumns();
    this.findAllGroupedCities();
    this.findAllFbPages();
    this.onActiveIndexChange(2);
    this.selectedStatus.setValue([]);
  }

  getCityById(cityId: number): string {
    for (const group of this.groupedCities) {
      // Find the city with the matching ID in each group's items
      const foundCity = group.items.find((city: any) => city.value === cityId);

      if (foundCity) {
        // Return the formatted string: governorate - city
        return `${group.label} - ${foundCity.label}`;
      }
    }
    // Return an empty string or a fallback message if the city is not found
    return 'City not found';
  }

  findAllFbPages(): void {
    this.fbPageService.getFbPagesSubscriber().subscribe((result: any) => {
      this.fbPages = result.filter((fbPage: any) => fbPage.enabled);
    });
  }

  findAllPackets(): void {
    this.loading = true;
    this.packetService.findAllPackets(this.params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: ResponsePage) => {

          this.packets = response.result.filter((packet: any) => this.checkPacketNotNull(packet));
          this.realTotalItems = response.totalItems;
          this.totalItems = this.packets.length;
          let countConfirmed =response.result.filter(packet => packet.status === CONFIRMED).length;
          this.statusItems[3].badge = countConfirmed > 0 ? countConfirmed:0;
          this.loading = false;
          this.showStatus = false;
        },
        error: (error: Error) => {
          console.log('Error:', error);
          this.loading = false;
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

  findAllGroupedCities(): void {
    this.cityService.findAllGroupedCities().subscribe((groupedCities: any) => {
      this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
    });
  }

  onEditComplete($event: any): void {
    const packet = $event.data;
    this.loading = true;
    console.log('this.oldFieldValue',this.oldFieldValue);
    console.log('packet[this.selectedField]',packet[this.selectedField]);

    try {
      if (this.oldFieldValue !== packet[this.selectedField] && packet[this.selectedField] !== undefined) {
        switch (this.selectedField) {
          case 'status':
            this.handleStatusField(packet);
            break;
          default:
            this.patchPacketService(packet);
            break;
        }
      } else {
        console.log("No changes");
        this.loading = false;
        return;
      }
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
      this.oldFieldValue = '';
    }
  }

  patchPacketService(packet: Packet) {
    console.log('this.selectedField',this.selectedField);
    console.log('packet',packet);


    let updatedField;
    if (this.selectedField === 'cityId')
      updatedField = { ['city']: packet.cityId };
    else if (this.selectedField === 'fbPage'){
      updatedField = { ['fbPage']: packet.fbPage!.id };
    }
    else if (this.selectedField === 'date')
      updatedField = { [this.selectedField]: this.onDateSelect(packet.date) };
    else
      updatedField = { [this.selectedField]: packet[this.selectedField] };

    let status = packet.status;
    this.packetService.patchPacket(packet.id!, updatedField)
      .pipe(
        catchError((err: any): Observable<any> => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Error updating packet ' + err.error.message });
          const packetIndex = this.packets.findIndex((p: any) => p.id === packet.id);
          if (packetIndex !== -1) {
            this.packets[packetIndex][this.selectedField] = this.oldFieldValue;
          }
          this.loading = false;
          // Return an EMPTY observable to indicate that the observable chain should terminate gracefully
          return EMPTY;
        })
      )
      .subscribe({
        next: (responsePacket: any) => {
          let msg = "Packet updated successfully";
          if (this.selectedField === 'status') {
            this.createNotification();
          }
          if (this.selectedField === 'status' && status === CONFIRMED && responsePacket.barcode != null) {
            this.updatePacketFields(responsePacket);
            msg = 'Barcode created successfully';
          } else if (responsePacket.oldClient !== undefined && this.selectedField === 'customerPhoneNb') {

            const packetIndex = this.packets.findIndex((p: Packet) => p.id === responsePacket.id);
            if (packetIndex !== -1) {
              this.packets[packetIndex].oldClient = responsePacket.oldClient;
              msg = 'Phone number updated successfully';
            }
          }

          this.messageService.add({ severity: 'success', summary: 'Success', detail: msg });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }


  onDateSelect(event: any) {
    // Manually set the hours to 3 AM, minutes, and seconds to zero
    const selectedDate = new Date(event);
    selectedDate.setHours(8, 0, 0, 0);
    return selectedDate;
  }


  checkPacketValidity(packet: Packet): boolean {
      if (!(packet.fbPage!.id && this.isValid(packet.address) && this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) && packet.cityId && this.isValid(packet.packetDescription)))
      {
        this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir tous les champs' });
        return false;
      }
      return true;
  }

  checkPacketNotNull(packet: Packet): boolean {
    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || packet.cityId! >-1 || this.isValid(packet.packetDescription));
  }

  checkPacketDescription(packet: Packet): boolean {
    return packet.packetDescription!= undefined && packet.packetDescription.includes('(');
  }

  getLastStatus(packet: Packet): void {
    if (packet.status != PAID && packet.status != RETURN_RECEIVED && packet.status != DELIVERED)
    this.packetService.getLastStatus(packet.id!)
      .subscribe({
          next: (response: any) => {//correction Packet
            this.updatePacketFields(response);
            this.createNotification();
          },
          error : (error: Error) => {
            console.log(error);
          }
        });
  }

  updatePacketFields(updatedPacket: Packet, action?: string) {

    let listId = this.packets.map((packetX: Packet) => packetX.id);
    let X = listId.indexOf(updatedPacket.id);
    if (X > -1) {
      this.packets = [...this.packets.map(packet => packet.id === updatedPacket.id ? updatedPacket : packet)];
      /*this.packets[X].status = packet.status;
      this.packets[X].lastDeliveryStatus = packet.lastDeliveryStatus;
      this.packets[X].lastUpdateDate = packet.lastUpdateDate;
      if (action === 'ADD_NOTE_ACTION') {
        //this.packets[X].notes.length++;
        this.packets[X].notes = packet.notes;
      }
      this.packets[X].barcode = packet.barcode;
      this.packets[X].packetDescription = packet.packetDescription;
      this.packets[X].oldClient = packet.oldClient;
      this.packets[X].stock = packet.stock;
      this.packets[X].price = packet.price;
      this.packets[X].deliveryPrice = packet.deliveryPrice;
      this.packets[X].discount = packet.discount;*/
    }
  }

  addAttempt(status: string,packet?: Packet): void {
    console.log(this.selectedPackets);

    this.noteActionStatus = status;
    /*if( status == 'DELETED' ){
      let index = this.selectedPackets.findIndex((selectedPacket: Packet) => selectedPacket.barcode == null || selectedPacket.barcode == "");
      console.log(index);

      if (index > -1) {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please do not delete outgoing packets' });
        return;
      }
    }*/

    if(packet)this.selectedPacket = packet;
    this.clientReasons = this.getReasonOptionsByStatus(status);
    this.explanationTitle = '';
    this.explanation = '';
    this.note = {
      date: new Date(),
      packet: packet,
      clientReason: '',
      status: ''
    };
    this.clientReasons.forEach(clientReason => {
      clientReason.text = true
      clientReason.outlined = false
    });
    this.visibleNote = true;
  }

  confirmNote() {
    this.note.date = new Date();
    this.note.explanation = StringUtils.isStringValid(this.explanation) ? this.explanationTitle + ' : ' + this.explanation : this.explanationTitle;
    switch (this.noteActionStatus) {
      case 'DELETED': {
        let selectedPacketsByIds = this.selectedPackets.map((selectedPacket: Packet) => selectedPacket.id!);
        this.packetService.deleteSelectedPackets(selectedPacketsByIds, this.note).subscribe({
          next: () => {
            // Handle successful response
            this.packets = this.packets.filter(packet => !selectedPacketsByIds.includes(packet.id!));
            this.selectedPackets = [];
            this.messageService.add({
              severity: 'success',
              summary: 'Succès',
              detail: 'Les commandes sélectionnées ont été supprimées avec succès',
              life: 1000
            });
          },
          error: (err:any) => {
            // Handle error response
            console.error('Error:', err);
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Une erreur est survenue lors de la suppression des commandes',
              life: 1000
            });
          }
        });
        break;
      }
      case 'UNREACHABLE': {
        this.packetService.addAttempt(this.note, this.selectedPacket.id!)
          .subscribe({
            next: (response: any) => {
              this.updatePacketFields(response, 'ADD_NOTE_ACTION');
              this.messageService.add({ severity: 'info', summary: 'Success', detail: 'La note a été ajoutée avec succés', life: 1200 });
            },
            error: () => {
              this.messageService.add({ severity: 'error', summary: 'Error', detail: "Une erreur est survenue lors de l'ajout de la note", life: 1200 });
            }
          });
          break;
      }
      case 'CANCELED': {
        this.packetService.addAttempt(this.note, this.selectedPacket.id!)
          .subscribe({
            next: (response: any) => {
              this.updatePacketFields(response, 'ADD_NOTE_ACTION');
              this.messageService.add({ severity: 'info', summary: 'Success', detail: 'La note a été ajoutée avec succés', life: 1200 });
            },
            error: () => {
              this.messageService.add({ severity: 'error', summary: 'Error', detail: "Une erreur est survenue lors de l'ajout de la note", life: 1200 });
            }
          });
          break;
      }
      default: {
        console.log("Note Action Status did not match any cases.");
      }
    }
    this.visibleNote = false;
}

  openLinkGetter(code: any, deliveryCompany: DeliveryCompany): void {
    let link = deliveryCompany.barreCodeUrl + code;
    console.log("link", link + "/code:" + code);
    window.open(link, '_blank');
  }

  printFirst(link: string): void {
    window.open(link, '_blank');
  }

  showTimeLineDialog(packet: Packet): void {
    try {
      this.packetService.getPacketTimeLine(packet.id!).subscribe((response: any) => {
        this.statusEvents = [];
        this.suiviHeader = "Suivi Historique - Commande N° " + packet.id;
        if (response != null && response.length > 0) {
          response.forEach((element: any) => {
            this.statusEvents.push({ status: element.status, date: element.date, user: element.user, icon: PrimeIcons.ENVELOPE, color: '#9C27B0' });
          });
        }

        this.displayStatus = true;
      }
      );
    } catch (error) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Erreur dans le status'});
    }
  }

  addNewRow(): void {
    console.log("activeIndex", this.activeIndex);
    if (this.activeIndex != 2){
      console.log("activeIndex != 2", this.activeIndex);
      this.onActiveIndexChange(2);
    }
    else if (!this.loading) {
      //this.activeIndex=2;
      console.log("new pack");

      this.loading = true;
      this.packetService
        .addPacket()
        .subscribe((response: Packet) => {
          console.log("new pack", response);

          this.loading = false;
          //this.packets = [...this.packets.map(packet => packet.id === updatedPacket.id ? updatedPacket : packet)];
          this.packets.unshift(response);
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est ajoutée avec succés', life: 1000 });
        });
    }
  }

  duplicatePacket(packet: Packet): void {
    this.packetService
      .duplicatePacket(packet.id!)
      .subscribe((response: any) => {// correction------------------------
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est dupliqué avec succés', life: 1000});
        this.packets.unshift(response);
      });
  }

  deleteSelectedPackets(): void {
    this.addAttempt('DELETED');
  }

  loadOfferListAndOpenOffersDialog(packet: Packet,editMode:boolean): void {
    if (packet.fbPage) {
      this.productService.loadProducts().subscribe(() => {
        this.packet = Object.assign({}, packet);
        this.modelDialog = true;
        this.editMode = editMode;
      });
    } else {
      this.messageService.add({ severity: 'info', summary: 'Info', detail: 'Pas de page Facebook selectionné', life: 1000 });
    }
  }

  hideDialog(): void {
    this.modelDialog = false;
  }

  OnSubmit($event: any): void {
    this.modelDialog = $event.modelDialog;
    this.updatePacketFields($event.packet);
    this.editMode ? this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été mis à jour avec succés', life: 1000 }) : this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Les articles ont été ajoutés avec succés', life: 1000 });
  }

  checkValidity(date1: Date, date2: Date, status: String): boolean {
    if (status != PAID && status != RETURN_RECEIVED)
      return this.dateUtils.getDate(date1) < this.dateUtils.getDate(date2);
    return false;
  }

  handleInputChange() {
    const inputValue = this.filter;
    const numbersCount = (inputValue.match(/\d/g) || []).length;
    if (numbersCount === 5 || numbersCount === 8 || numbersCount === 12  ) {
      this.oldActiveIndex = this.activeIndex;
      this.filterPackets('global');
      this.activeIndex = 0;
    }
    if (this.filter === '') {
      this.filterPackets('global');
      this.activeIndex = this.oldActiveIndex;
    }
  }

  filterPackets($event?: string): void {
    this.createRangeDate();
    if(this.endDate){
      let page = 0;
      if ($event == 'clear') {
        this.selectedStates = [];
        this.selectedStatus.setValue([]);
      } else if ($event == 'page')
        page = this.currentPage;
      if (this.selectedStatus.value == null) this.selectedStatus.setValue([]);

      if (this.filter !== '' && this.filter)
        {
          this.oldActiveIndex = this.activeIndex;
          this.activeIndex = 0;
        }

      this.params = {
        page: page,
        size: this.pageSize,
        searchText: this.filter != null && this.filter != '' ? this.filter : null,
        beginDate: this.dateUtils.formatDateToString(this.beginDate),
        endDate: this.dateUtils.formatDateToString(this.endDate),
        status: this.selectedStatus.value.length == 0 ? null : this.selectedStatus.value.join(),
        mandatoryDate: this.mandatoryDateCheckBox
      };
      this.findAllPackets();
    }
  }

  createRangeDate(): void {
    if (this.rangeDates !== null) {
      this.beginDate = this.rangeDates[0];
      if (this.rangeDates[1]) {
        this.endDate = this.rangeDates[1];
      } else {
        this.endDate = null;
      }
    } else {
      this.beginDate = this.today;
      this.endDate = this.today;
    }
  }

  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.pageSize = $event.rows;
    this.filterPackets('page');
  }

  resetTable(): void {
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
    return fieldName != null ? fieldName : '';
  }

  isValid(field: any) {
    return field != null && field != '';
  }

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

  checkCodeABarreExist(packet:Packet){
    return packet.barcode != "" && packet.barcode!= null
  }

  checkPhoneNbExist(packet:Packet){
    return packet.customerPhoneNb !="" && packet.customerPhoneNb!= null
  }

  mandatoryDateChange(){
    if(this.selectedStatus.value != null && this.selectedStatus.value.length > 0)
    this.filterPackets('global')
  }

  oldDateFilter(){
      this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now() - 86400000)];
      this.filterPackets('global');
  }
  todayDate(){
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.rangeDates[0]=this.beginDate;
        this.endDate = this.today;
        this.rangeDates= [this.beginDate,this.today];
      }
    else this.rangeDates = [this.today];
    this.filterPackets('global');
  }

  clearDate(){
    this.rangeDates = [];
    this.filterPackets('global');
  }

  selectCity( packet: Packet) {
    this.selectedCityId = packet.cityId;
  }
  selectPhoneNumber( packet: any) {
    this.selectedPhoneNumber = packet.customerPhoneNb;
  }

  showStatusButton() {
    this.showStatus= !this.showStatus;
    this.createNotification();
  }

  getReasonOptionsByStatus(status: string) {
    return (Object.keys(ClientReason) as (keyof typeof ClientReason)[])
      .filter(key => isNaN(Number(key)) && ClientReasonDetails[ClientReason[key]].status === status)  // Filter out any non-number keys
      .map(key => {
        const reasonKey = ClientReason[key];
        const clientReasonDetails = ClientReasonDetails[reasonKey];
        return {
          value: key,
          label: clientReasonDetails.label,
          description: clientReasonDetails.description,
          status: clientReasonDetails.status,
          text: clientReasonDetails.text,
          outlined: clientReasonDetails.outlined,
          severity: clientReasonDetails.severity,
        }
      })
  }

  onSelectReason(clientReason: ClientReason, index: number) {
    this.explanationTitle = this.clientReasons[index]?.description;
    if (this.explanationElement && this.explanationElement.nativeElement) {
      setTimeout(() => {
        this.explanationElement.nativeElement.focus();
      }, 0);
    }
    this.clientReasons.forEach(clientReason => {
      clientReason.text = true
      clientReason.outlined = false
    });
    this.clientReasons[index].text = false;
    this.clientReasons[index].outlined = true;

    this.note.clientReason = clientReason;
    this.note.status = this.clientReasons[index].status;
  }

  onNoteClick(event: MouseEvent, op: any, notes: Note[]): void {
    event.stopPropagation(); // Prevent the click event from propagating
    if (notes.length > 1) {
      op.toggle(event);
      this.selectedPacketNotes = notes;
    }
  }

  clearAllSelectedStatus(){
    this.nonConfirmedOptionsValue=[];
    this.endedOptionsValue=[];
    this.canceledOptionsValue = [];
    this.enCoursOptionsValue = [];
  }

  onEditInit($event: any): void {
    this.selectedField = $event.field;
    this.oldFieldValue = $event.data[this.selectedField];

    if (this.selectedField === 'status') {
      this.packetStatusList = [];

      if ($event.data.stock === -1) {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: "Please fill in all article fields" });
        this.packetStatusList = [UNREACHABLE];
      } else if ([NOT_CONFIRMED].includes(this.oldFieldValue)) {
        this.packetStatusList = [OOS, CONFIRMED, NOT_SERIOUS, UNREACHABLE];
      } else if ([NOT_SERIOUS, UNREACHABLE, CANCELED, DELETED, OOS].includes(this.oldFieldValue)) {
        this.packetStatusList = [NOT_CONFIRMED, OOS, CONFIRMED, NOT_SERIOUS, UNREACHABLE, CANCELED];
      } else if ([CONFIRMED, TO_VERIFY].includes(this.oldFieldValue)) {
        this.packetStatusList = [IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3, CANCELED, TO_VERIFY, DELIVERED, RETURN, PAID, RETURN_RECEIVED, PROBLEME];
      } else if ([IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3].includes(this.oldFieldValue)) {
        this.packetStatusList = [UNREACHABLE, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3, TO_VERIFY, DELIVERED, RETURN, PAID, RETURN_RECEIVED, PROBLEME];
      } else if ([DELIVERED, PAID].includes(this.oldFieldValue)) {
        this.packetStatusList = [PAID, RETURN, RETURN_RECEIVED];
      } else if (this.oldFieldValue === RETURN) {
        this.packetStatusList = [PROBLEME, RETURN_RECEIVED];
      } else if (this.oldFieldValue === PROBLEME) {
        this.packetStatusList = [RETURN_RECEIVED, DELIVERED, PAID, IN_PROGRESS_2, CANCELED];
      }
    }
  }

  onActiveIndexChange(event: any) {
    this.statusItemsLabel = this.statusItems[event].title;
    this.activeIndex = event;
    if(this.statusItemsLabel == IN_PROGRESS)
      {
        this.nonConfirmedOptionsValue=[];
        this.endedOptionsValue=[];
        this.canceledOptionsValue = [];
        if(this.enCoursOptionsValue != undefined && this.enCoursOptionsValue.length > 0)
        this.selectedStatus.patchValue(this.enCoursOptionsValue);
        else
        this.selectedStatus.patchValue([ TO_VERIFY, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3 ]);
      }
    else if(this.statusItemsLabel == NOT_CONFIRMED)
      {
        this.endedOptionsValue=[];
        this.canceledOptionsValue = [];
        this.enCoursOptionsValue = [];
        if(this.nonConfirmedOptionsValue != undefined && this.nonConfirmedOptionsValue.length > 0)
            this.selectedStatus.patchValue(this.nonConfirmedOptionsValue);
        else
            this.selectedStatus.patchValue([ NOT_CONFIRMED, UNREACHABLE ]);
      }
    else if(this.statusItemsLabel == CANCELED)
      {
        this.nonConfirmedOptionsValue=[];
        this.endedOptionsValue=[];
        this.enCoursOptionsValue = [];
        if(this.canceledOptionsValue != undefined && this.canceledOptionsValue.length > 0)
            this.selectedStatus.patchValue(this.canceledOptionsValue);
        else
            this.selectedStatus.patchValue([ CANCELED, DELETED ]);
      }

    else if(this.statusItemsLabel == "Terminé")
      {
        this.nonConfirmedOptionsValue=[];
        this.canceledOptionsValue = [];
        this.enCoursOptionsValue = [];
        if(this.endedOptionsValue != undefined && this.endedOptionsValue.length > 0)
          this.selectedStatus.patchValue(this.endedOptionsValue);
      else{
        this.selectedStatus.patchValue( [ DELIVERED ,PAID ,RETURN_RECEIVED ]);
        }
      }
    else {
      this.clearAllSelectedStatus();
      this.selectedStatus.setValue([]);
      this.selectedStatus.patchValue([this.statusItemsLabel]);
    }
    this.filterPackets('status');
  }

  private handleStatusField(packet: any): void {
    const status = packet[this.selectedField];
    const barcode = packet.barcode;
    let errorMessage: string | undefined = undefined;

    switch (status) {
      case CANCELED:
        if (this.oldFieldValue == NOT_CONFIRMED)
          errorMessage = "Delete don't cancel";
        else if (this.oldFieldValue !== CONFIRMED && this.oldFieldValue !== TO_VERIFY && this.oldFieldValue !== DELETED) {
          errorMessage = 'Please do not cancel outgoing packets';
        }else if(barcode != null && barcode !== "") this.addAttempt('CANCELED',packet);
        break;
      case NOT_CONFIRMED:
      case OOS:
      case NOT_SERIOUS:
        if (this.oldFieldValue === IN_PROGRESS_1 || this.oldFieldValue === IN_PROGRESS_2 || this.oldFieldValue === IN_PROGRESS_3) {
          errorMessage = 'This packet is already in progress';
        }
        break;
      case IN_PROGRESS_1:
      case IN_PROGRESS_2:
      case IN_PROGRESS_3:
      case DELIVERED:
      case RETURN:
      case RETURN_RECEIVED:
      case PAID:
        if (barcode == null || barcode === "") {
          errorMessage = "This packet has not been dispatched yet";
        }
        break;
      case UNREACHABLE:
        {
          if (barcode != null && barcode !== "") {
          errorMessage = 'This packet is already completed';
        }}
        break;
      case CONFIRMED:
        if (!this.checkPacketValidity(packet)) {
          errorMessage = 'Packet validation failed';
        } else if (!this.checkPacketDescription(packet)) {
          errorMessage = 'Please enter the size of the item';
        }
        break;
      default:
        break;
    }

    if (errorMessage) {
      packet[this.selectedField] = this.oldFieldValue;
      this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage });
      this.loading = false;
      return;
    }

    this.patchPacketService(packet);

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
    if(this.showStatus){
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

  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

  //intiator

  openContextMenu(event:any,packet:any, contextMenu:ContextMenu) {
    event.preventDefault();
    this.selectedPacket = packet;
    this.contextMenu.target = event.currentTarget;
    if (contextMenu) {
      this.optionButtons = [
        {
          label: 'Duplicate',
          icon: 'pi pi-refresh',
          disabled:!( packet.status == DELIVERED || packet.status == PAID ),
          command: () => {
            this.duplicatePacket(packet)
          }
        },
        {
          label: 'Ajouter tentative',
          icon: 'pi pi-refresh',
          disabled:packet.status!=UNREACHABLE,
          command: () => {
            this.addAttempt('UNREACHABLE',packet)
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
          label: 'History',
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
          label: 'Tel',
          icon: 'pi pi-search-plus',
          disabled:!this.checkPhoneNbExist(packet),
          command: () => {
            this.openLinkGetter(packet.customerPhoneNb,packet.deliveryCompany);
          }
        },
        {
          label: 'BarreCode',
          icon: 'pi pi-qrcode',
          disabled:!this.checkCodeABarreExist(packet),
          command: () => {
            this.openLinkGetter(packet.barcode,packet.deliveryCompany)
          }
        },
        {
          label: 'Print',
          icon: 'pi pi-print',
          disabled:!(this.checkCodeABarreExist(packet)),
          command: () => {
            this.printFirst(packet.printLink)
          }
        }
      ];
    }
  }

  enCoursOptionsValue !:any;
  enCoursOptions: any[] = [
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

}
