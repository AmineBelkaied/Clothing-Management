
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
import { PacketFilterParams } from 'src/shared/models/PacketFilterParams';

@Component({
  selector: 'app-list-packets',
  templateUrl: './list-packets.component.html',
  styleUrls: ['./list-packets.component.css'],
  providers: [DatePipe]
})
export class ListPacketsComponent implements OnInit, OnDestroy {
  nbrSelectedPackets: number;
  oldStatusLabel: string;


  //@Output() confirmEvent: EventEmitter<string> = new EventEmitter<string>();

  @ViewChild('contextMenu', { static: false }) contextMenu: ContextMenu;
  @ViewChild('dt') dt: Table;
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

  editMode = false;
  isLoading = false;
  selectedPacket: Packet;


  modelDialog!: boolean;


  oldFieldValue: string = "";
  offersIdsListByFbPage: any[] = [];
  allOffersList: any[] = [];
  groupedCities: SelectItemGroup[] = [];
  fbPages: FbPage[] = [];
  selectedCityId: number | undefined;
  today: Date = new Date();
  today_2: Date = new Date(Date.now() - 172800000);
  //selectedCity: string | undefined;


  enCoursStatus: string[] = [];


  optionButtons: MenuItem[];
  packetStatusList: string[] = [];

  selectedStates: string[] = [];
  $unsubscribe: Subject<void> = new Subject();


  nbrConfirmed: number = 0;
  countUNREACHABLE: string = "0";
  loading: boolean = false;




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
  //note
  selectedPacketNotes: Note[] = [];
  @ViewChild("expRef") explanationElement: ElementRef;
  explanationTitle: string;
  noteActionStatus: string;

  filter: string;
  params: PacketFilterParams;
  pageSize: number = 100;//correction


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
    this.findAllFbPages();
    this.offerService.getOffersSubscriber();
    this.createColumns();
    this.findAllGroupedCities();

  }

  findAllFbPages(): void {
    //this.fbPages = this.fbPageService.fbPages;
    //console.log(this.fbPages);

    this.fbPageService.getFbPagesSubscriber().subscribe((result: any) => {
      this.fbPages = result.filter((fbPage: any) => fbPage.enabled);
    });
  }

  onFilterPacketsChange(params: PacketFilterParams) {
    this.params = params;
    this.loadAllPackets(params);
  }
  onButtonMenuClick(buttonType: String) {
    switch (buttonType) {
      case 'add':
        this.addNewPacket();
        break;
      case 'delete':
        this.deleteSelectedPackets();
        break;
      case 'delete':
        this.selectedPackets = [];
        break;
    }
  }

  loadAllPackets(params: PacketFilterParams): void {

    this.loading = true;
    this.packetService.findAllPackets(params)
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (response: ResponsePage) => {
          this.selectedPackets = [];
          this.packets = response.result.filter((packet: any) => this.checkPacketNotNull(packet));
          this.realTotalItems = response.totalItems;
          this.totalItems = this.packets.length;
          //let countConfirmed =response.result.filter(packet => packet.status === CONFIRMED).length;
          //this.statusItems[3].badge = countConfirmed > 0 ? countConfirmed:0;
          this.loading = false;

        },
        error: (error: Error) => {
          console.log('Error:', error);
          this.loading = false;
        }
      });
  }

  addNewPacket(){
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
  checkPacketNotNull(packet: Packet): boolean {
    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || packet.cityId! >0 || this.isValid(packet.packetDescription));
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
    return phoneNumber.replace('/', ' ');
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


  duplicatePacket(packet: Packet): void {
    this.packetService
      .duplicatePacket(packet.id!)
      .subscribe((response: any) => {// correction------------------------
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'La commande est dupliqué avec succés', life: 1000});
        this.packets.unshift(response);
      });
  }

  loadOfferListAndOpenOffersDialog(packet: Packet,editMode:boolean): void {
    if (packet.fbPageId) {
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



  onPageChange($event: any): void {
    this.params.page = $event.page;
    this.params.size = $event.rows;
    this.loadAllPackets(this.params);
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



  checkCodeABarreExist(packet:Packet){
    return packet.barcode != "" && packet.barcode!= null
  }

  checkPhoneNbExist(packet:Packet){
    return packet.customerPhoneNb !="" && packet.customerPhoneNb!= null
  }


  selectCity( packet: Packet) {
    this.selectedCityId = packet.cityId;
  }
  selectPhoneNumber( packet: any) {
    this.selectedPhoneNumber = packet.customerPhoneNb;
  }
  selectedPacketChange($event: Event) {
    this.nbrSelectedPackets = this.selectedPackets.length;
    console.log(this.nbrSelectedPackets);

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
            //this.filterPackets('phone');
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

  deleteSelectedPackets(): void {
    this.addAttempt('DELETED');
  }
}
