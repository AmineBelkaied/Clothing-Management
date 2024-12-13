import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { DateUtils } from 'src/shared/utils/date-utils';
import { NOT_CONFIRMED, statusList, UNREACHABLE } from 'src/shared/utils/status-list';
import { Packet } from 'src/shared/models/Packet';
import { StorageService } from 'src/shared/services/strorage.service';
import { Status } from 'src/shared/models/status';
import { PacketFilterParams } from 'src/shared/models/PacketFilterParams';
@Component({
  selector: 'app-packets-menue-container',
  templateUrl: './packets-menue-container.component.html',
  styleUrls: ['./packets-menue-container.component.css']
})
export class PacketsMenueContainerComponent implements OnChanges {

  //@ViewChild(StatusContainerComponent) statusContainerComponent!: StatusContainerComponent;
  packetStatusList: string[] = [];
  rangeDates: Date[] = [];
  beginDate: Date = new Date();
  endDate: Date | null = new Date();
  today: Date = new Date();

  //activeIndex: number = 2;
  //oldActiveIndex: number;
  @Input() filter: string;
  @Input() nbrSelectedPackets: number;
  statusList: string[] = [];
  pageSize: number = 100;

  first = 0;
  rows = 100;
  currentPage = 0;

  params: any = {
    page: 0,
    size: this.pageSize,
    beginDate: this.dateUtils.formatDateToString(this.today),
    endDate: this.dateUtils.formatDateToString(this.today),
    mandatoryDate: false
  };
  mandatoryDateCheckBox: boolean = false;
  oldDateFilterCheckBox: boolean = false;
  value: boolean = this.mandatoryDateCheckBox;
  selectedStatus: string[] = [NOT_CONFIRMED,UNREACHABLE];
  loading: boolean = false;
  statusItemsLabel: string =NOT_CONFIRMED;
  oldStatusItemsLabel: string =NOT_CONFIRMED;

  @Output()
  filterPacketsEmitter: EventEmitter<PacketFilterParams> = new EventEmitter()

  @Output()
  buttonPacketsEmitter: EventEmitter<String> = new EventEmitter();
  selectedItemStatus: Status;

  constructor(
    private dateUtils: DateUtils,
    private storageService: StorageService

    ) {
    this.statusList = statusList;
  }

  activeClass: boolean;
  userName: string;
  isLoggedIn: boolean;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  oldStatus: Status;
  statusFilter: boolean = true;

  ngOnInit(): void {
    this.storageService.isLoggedIn.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      this.userName = this.storageService.getUserName();
      this.isAdmin = this.storageService.hasRoleAdmin();
      this.isSuperAdmin = this.storageService.hasRoleSuperAdmin();
      this.activeClass = true;
    });
    this.rangeDates = [this.today,this.today];
    this.filterPackets('global');
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['filter']) {
      this.handleInputChange();
    }
  }
  handleInputChange() {
    const inputValue = this.filter;
    if(inputValue){
      const numbersCount = (inputValue.match(/\d/g) || []).length;

      if ( numbersCount === 5 || numbersCount === 8 || numbersCount === 12  ) {
        this.filterPackets('inputFilter');
      }
      if (this.filter === '') {
        this.filterPackets('inputFilter');
        this.statusItemsLabel = this.oldStatusItemsLabel;
      }
    }

  }

  resetTable(): void {
    this.buttonPacketsEmitter.next('reset');
    this.selectedStatus=[];
    this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now())];
    this.filterPackets('global');
  }



  addNewRow(): void {
    //console.log("activeIndex", this.activeIndex);
    this.filter ='';
    if (this.statusItemsLabel != NOT_CONFIRMED){
      this.statusItemsLabel =NOT_CONFIRMED;
      this.selectedStatus=[NOT_CONFIRMED];
      this.filterPackets('global');
    }
    else if (!this.loading) {
      console.log("new pack");
      this.buttonPacketsEmitter.next('add');
    }
  }
  deleteSelectedPackets() {
    this.buttonPacketsEmitter.next('delete');
  }

  checkPacketNotNull(packet: Packet): boolean {
    return (this.isValid(packet.address) || this.isValid(packet.customerName) ||
      this.isValid(packet.customerPhoneNb) || packet.cityId! >0 || this.isValid(packet.packetDescription));
  }
  isValid(field: any) {
    return field != null && field != '';
  }
  $unsubscribe($unsubscribe: any): import("rxjs").OperatorFunction<any, any> {
    throw new Error('Method not implemented.');
  }
  filterPackets2($event?: string): void {
    console.log("bbb");

  }
  filterPackets($event?: string): void {
    this.createRangeDate();
    if(this.endDate){
      let page = 0;
      if ($event == 'clear') {
        this.selectedStatus=[];
      } else if ($event == 'page')
        page = this.currentPage;

      if (this.selectedStatus == null) this.selectedStatus =[];
      if($event == "inputFilter")
        if(this.filter != null){
          this.oldStatusItemsLabel = this.statusItemsLabel;
          this.statusFilter = false;
        }
      if($event == "status")
        this.statusFilter = true;

      this.params = {
        page: page,
        size: this.pageSize,
        searchText: this.filter != null && this.filter != '' ? this.filter : null,
        beginDate: this.dateUtils.formatDateToString(this.beginDate),
        endDate: this.dateUtils.formatDateToString(this.endDate),
        status: this.selectedStatus.length == 0 ? null : this.selectedStatus,
        mandatoryDate: this.mandatoryDateCheckBox,
        statusFilter: this.statusFilter
      };
      this.filterPacketsEmitter.next(this.params);
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


  mandatoryDateChange(){
    this.mandatoryDateCheckBox = !this.mandatoryDateCheckBox;
    if(this.selectedStatus != null && this.selectedStatus.length > 0)
    this.filterPackets('filterDate')
  }
  mandatoryStatusChange(){
    this.filterPackets('filterStatus')
  }

  onStatusChange(item: Status) {
    if(this.oldStatus != item){
      this.selectedItemStatus = item;
       this.selectedStatus = item.items ? item.selectedOptions?.length==0 ? this.getArrayFromStatusItems(item.items) ?? [] : item.selectedOptions ?? [] : [item.label] ;
      this.statusItemsLabel = item.label;
      this.filterPackets('status')
      this.oldStatus= Object.assign({}, item);
    }
  }

  getArrayFromStatusItems(statusItems:any) : string[]{
    return statusItems.map((item :any) => item.value);
  }
  oldDateFilter(){
      this.rangeDates = [new Date(2023, 0, 1), new Date(Date.now() - 86400000)];
      this.filterPackets('date');
  }
  onClickOutside(){
    this.rangeDates[1] = this.rangeDates[1] ? this.rangeDates[1]
        : this.rangeDates[0];
        this.filterPackets('date');
  }
  todayDate(){
    if(this.rangeDates[0] != undefined && this.rangeDates[1]==undefined)
      {
        this.rangeDates[0]=this.beginDate;
        this.endDate = this.today;
        this.rangeDates= [this.beginDate,this.today];
      }
    else this.rangeDates = [this.today,this.today];
    this.filterPackets('date');
  }

  clearDate(){
    this.rangeDates = [];
    this.filterPackets('date');
  }
}
