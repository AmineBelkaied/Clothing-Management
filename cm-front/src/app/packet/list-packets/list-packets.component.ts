
import { SelectItemGroup, PrimeIcons, MenuItem } from 'primeng/api';
import { MessageService } from 'primeng/api';
import { Packet } from '../../../shared/models/Packet';
import { OfferService } from '../../../shared/services/offer.service';
import { PacketService } from '../../../shared/services/packet.service';
import { DatePipe } from '@angular/common';
import { Table } from 'primeng/table';
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
import { ResponsePage } from 'src/shared/models/ResponsePage';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { ContextMenu } from 'primeng/contextmenu';
import { ClientReason, ClientReasonDetails } from 'src/shared/enums/client-reason';
import { Note } from 'src/shared/models/Note';
import { StringUtils } from 'src/shared/utils/string-utils';
import { FormControl } from '@angular/forms';
import { StatusContainerComponent } from 'src/app/status-container/status-container.component';
import { Status } from 'src/shared/models/status';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe]
})
export class ListPacketsComponent implements OnInit, OnDestroy {
  oldStatusLabel: string;

  activeIndex: number = 2;
  oldActiveIndex: number;
  //@Output() confirmEvent: EventEmitter<string> = new EventEmitter<string>();
  @ViewChild(StatusContainerComponent) statusContainerComponent!: StatusContainerComponent;
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

  showStatus: boolean = false;


  @ViewChild('dt') dt: Table;
  //private readonly reg: RegExp = /,/gi;
  regBS = /\n/gi;
  private readonly FIRST: string = 'FIRST';
  countCanceled: number = 0;
  realTotalItems: number;
  selectedPhoneNumber: string = '';

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
  selectedStatus: string[] = [NOT_CONFIRMED];
  statusItemsLabel: string;

  constructor(
    private packetService: PacketService,
    private offerService: OfferService,
    private cityService: CityService,
    private fbPageService: FbPageService,
    private dateUtils: DateUtils,
    public storageService: StorageService,
    public messageService:MessageService,
    private cdRef: ChangeDetectorRef
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
    this.statusItemsLabel = this.selectedStatus[0];
    this.rangeDates = [this.today,this.today];
    this.offerService.getOffersSubscriber();
    this.createColumns();
    this.findAllGroupedCities();
    this.findAllFbPages();
    this.filterPackets('global');
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
          //let countConfirmed =response.result.filter(packet => packet.status === CONFIRMED).length;
          //this.statusItems[3].badge = countConfirmed > 0 ? countConfirmed:0;
          this.loading = false;
          //this.showStatus = false;
          console.log("findAllPackets", this.showStatus);
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
      { field: 'customerNamePhone', header: 'Client'},
      { field: 'cityAddress', header: 'Ville & Adresse' },  // Combine city and address here
      { field: 'relatedProducts', header: 'Articles' },
      { field: 'price', header: 'Prix' },
      { field: 'status', header: 'Statut' },
      { field: 'barcode', header: 'Barcode' },
    ];
  }
  formatPhoneNumber(phoneNumber: string): string {
    if (!phoneNumber) {
      return '';
    }
    return phoneNumber.replace('/', '<br />');
  }

  findAllGroupedCities(): void {
    this.cityService.findAllGroupedCities().subscribe((groupedCities: any) => {
      this.groupedCities = this.cityService.adaptListToDropDown(groupedCities);
    });
  }
  onActiveIndexChange(index: number) {
    if (this.statusContainerComponent) {
      this.statusContainerComponent.onActiveIndexChange(index);
    }
  }

  onEditComplete($event: any): void {
    const packet = $event.data;
    this.loading = true;
    console.log('this.oldFieldValue',this.oldFieldValue);
    console.log('packet[this.selectedField]',packet[this.selectedField]);
    try {
      if (packet[this.selectedField] !== undefined
        && this.oldFieldValue !== packet[this.selectedField]) {//handle to field in 1 <td>
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
    let updatedField;
    if (this.selectedField === 'cityId'){
       updatedField = { ['city']: packet.cityId };
    }
    else if (this.selectedField === 'fbPageId'){
      updatedField = { ['fbPage']: packet.fbPageId };
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
          if (this.selectedField === 'status' && status === CONFIRMED && responsePacket.barcode != null) {
            this.updatePacketFields(responsePacket);
            msg = 'Barcode created successfully';
          } else if (this.selectedField === 'cityId') {
            this.updateCityField(responsePacket);
            msg = 'City updated successfully';
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
      if (!(packet.fbPageId && this.isValid(packet.address) && this.isValid(packet.customerName) &&
      this.isValid(packet.customerPhoneNb) && packet.cityId! >-1 && this.isValid(packet.packetDescription)))
      {
        this.messageService.add({ severity: 'error',summary: 'Error', detail: 'Veuillez saisir tous les champs' });
        return false;
      }
      return true;
  }

  checkPacketNotNull(packet: Packet): boolean {
    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || packet.cityId! >0 || this.isValid(packet.packetDescription));
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
      //this.packets = [...this.packets.map(packet => packet.id === updatedPacket.id ? updatedPacket : packet)];
      this.packets[X].status = updatedPacket.status;
      this.packets[X].lastDeliveryStatus = updatedPacket.lastDeliveryStatus;
      this.packets[X].lastUpdateDate = updatedPacket.lastUpdateDate;
      if (action === 'ADD_NOTE_ACTION') {
        //this.packets[X].notes.length++;
        this.packets[X].notes = updatedPacket.notes;
      }
      this.packets[X].barcode = updatedPacket.barcode;
      this.packets[X].packetDescription = updatedPacket.packetDescription;
      this.packets[X].oldClient = updatedPacket.oldClient;
      this.packets[X].stock = updatedPacket.stock;
      this.packets[X].totalPrice = updatedPacket.totalPrice;
      this.packets[X].deliveryPrice = updatedPacket.deliveryPrice;
      this.packets[X].discount = updatedPacket.discount;
    }
  }
  updateCityField(updatedPacket: Packet) {
    let listId = this.packets.map((packetX: Packet) => packetX.id);
    let X = listId.indexOf(updatedPacket.id);
    if (X > -1) {
      this.packets[X].cityName = updatedPacket.cityName;
      this.packets[X].cityId = updatedPacket.cityId;
    }
  }

  addAttempt(status: string,packet?: Packet): void {
    console.log(this.selectedPackets);

    this.noteActionStatus = status;
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
    let link = deliveryCompany.barCodeUrl + code;
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
    if (packet.fbPageId) {
      //this.productService.loadProducts().subscribe(() => {});
        this.packet = Object.assign({}, packet);
        this.modelDialog = true;
        this.editMode = editMode;

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
    this.showStatus = false;
    if(this.endDate){
      let page = 0;
      if ($event == 'clear') {
        this.selectedStates = [];
        this.selectedStatus=[];
      } else if ($event == 'page')
        page = this.currentPage;
      if (this.selectedStatus == null) this.selectedStatus =[];

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
        status: this.selectedStatus.length == 0 ? null : this.selectedStatus,
        mandatoryDate: this.mandatoryDateCheckBox
      };
      this.findAllPackets();
      if ($event == 'noClose') {
        this.showStatus = true
      } else this.showStatus = false
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
    this.selectedStatus=[];
    this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now())];
    this.filterPackets('global');
  }

  changeColor(this: any): void {
    this.style.color = 'red';
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
    this.selectedStatus=[];
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
    if(this.selectedStatus != null && this.selectedStatus.length > 0)
    this.filterPackets('global')
  }
  onStatusChange(status: Status) {
    console.log('Selected statuses in parent:', status);
    this.activeIndex = status.id;
    this.statusItemsLabel = status.label;
    this.selectedStatus = status.statusList;
    if(status.noClose)
      this.filterPackets('noClose')
    else
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


  getFbPageNameById(fbPageId: number) : String {
    return this.fbPageService.getFbPageNameById(fbPageId);
  }

  onEditInit($event: any): void {
    this.selectedField = $event.field;
    console.log($event);

    console.log("this.selectedField",this.selectedField);

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
          label: 'Ajouter note',
          icon: 'pi pi-refresh',
          disabled:packet.status!=UNREACHABLE,
          command: () => {
            this.addAttempt('UNREACHABLE',packet)
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
      ];
      if(this.checkCodeABarreExist(packet)){
        this.optionButtons.push(
          { separator: true },
          {
            label: 'Actualiser status',
            icon: 'pi pi-sync',
            command: () => {
              this.getLastStatus(packet);
            }
          },
          {
            label: 'BarreCode',
            icon: 'pi pi-qrcode',
            command: () => {
              this.openLinkGetter(packet.barcode,packet.deliveryCompany)
            }
          }
        )
      }
      if(this.checkPhoneNbExist(packet))
        this.optionButtons.push(
          { separator: true },
          {
            label: 'Tel',
            icon: 'pi pi-search-plus',
            command: () => {
              this.openLinkGetter(packet.customerPhoneNb,packet.deliveryCompany);
            }
          },{
          label: 'Chercher Tel',
          icon: 'pi pi-phone',
          disabled:!this.checkPhoneNbExist(packet),
          command: () => {
            this.filter= packet.customerPhoneNb;
            this.filterPackets('phone');
          }
        });
      if(packet.deliveryCompany.name != "JAX")
        this.optionButtons.push({
          label: 'Print',
          icon: 'pi pi-print',
          disabled:!(this.checkCodeABarreExist(packet)),
          command: () => {
            this.printFirst(packet.printLink)
          }
        });
    }
  }
}
