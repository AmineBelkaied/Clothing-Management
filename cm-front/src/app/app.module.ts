

// import modules, directives and services
import { AppRoutingModule } from './app-routing.module';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { MultiSelectModule } from 'primeng/multiselect';
import { DropdownModule } from 'primeng/dropdown';
import { CommonModule, DatePipe } from '@angular/common';
import { ToolbarModule } from 'primeng/toolbar';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { InputSwitchModule } from 'primeng/inputswitch';
import { CalendarModule } from 'primeng/calendar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TabViewModule } from 'primeng/tabview';
import { SplitterModule } from 'primeng/splitter';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ChangeColorDirective } from 'src/shared/directives/change-color.directive';
import { TimelineModule } from 'primeng/timeline';
import { TreeTableModule } from 'primeng/treetable';
import { RadioButtonModule } from 'primeng/radiobutton';
import { FileUploadModule } from 'primeng/fileupload';
import { TagModule } from 'primeng/tag';
import { CheckboxModule } from 'primeng/checkbox';
import { SplitButtonModule } from 'primeng/splitbutton';
import { SpeedDialModule } from 'primeng/speeddial';
import { ContextMenuModule } from 'primeng/contextmenu';
import { BadgeModule } from 'primeng/badge';
import { CardModule } from 'primeng/card';
import { ListboxModule } from 'primeng/listbox';
import { ChipModule } from 'primeng/chip';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { AvatarModule } from 'primeng/avatar';

import { ReactiveFormsModule } from '@angular/forms';
import { RippleModule } from 'primeng/ripple';



// import components
import { AppComponent } from './app.component';
import { AddCityComponent } from './config/city/add-city/add-city.component';
import { ListCitiesComponent } from './config/city/list-cities/list-cities.component';
import { AddColorComponent } from './config/color/add-color/add-color.component';
import { ListColorsComponent } from './config/color/list-colors/list-colors.component';
import { ConfigComponent } from './config/config.component';
import { AddFbpageComponent } from './config/fbpage/add-fbpage/add-fbpage.component';
import { AddSteLivraisonComponent } from './config/ste-livraison/add-ste-livraison/add-ste-livraison.component';
import { ListFbpagesComponent } from './config/fbpage/list-fbpages/list-fbpages.component';
import { ListSteLivraisonComponent } from './config/ste-livraison/list-ste-livraison/list-ste-livraison.component';
import { ListGovernoratesComponent } from './config/list-governorates/list-governorates.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { AddModelComponent } from './model/add-model/add-model.component';
import { AddOfferComponent } from './offer/add-offer/add-offer.component';
import { ListOffersComponent } from './offer/list-offers/list-offers.component';
import { AddPacketComponent } from './packet/add-packet/add-packet.component';
import { ListPacketsComponent } from './packet/list-packets/list-packets.component';
import { StockComponent } from './stock/stock.component';
import { ListModelsComponent } from './model/list-models/list-models.component';
import { AddSizeComponent } from './config/size/add-size/add-size.component';
import { ListSizesComponent } from './config/size/list-sizes/list-sizes.component';
import { ChartModule } from 'primeng/chart';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { CityTreeService } from 'src/shared/services/cityTree.service';
import { StatistiqueComponent } from './statistique/statistique.component';
import { StockHistoryComponent } from './stock-history/stock-history.component';
import { PaginatorModule } from 'primeng/paginator';
import { PayedReturnComponent } from './payed-return/payed-return.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { VerificationComponent } from './verification/verification.component';
import { PickListModule } from 'primeng/picklist';
import { GlobalConfComponent } from './config/global-conf/global-conf.component';
import { StepsModule } from 'primeng/steps';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { OverlayPanelModule } from 'primeng/overlaypanel';

import { AuthInterceptor } from 'src/shared/helpers/interceptor';
import { UserComponent } from './config/user/user.component';
import { PasswordModule } from 'primeng/password';

import 'tslib';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AuthGuard } from 'src/shared/services/auth-gard.service';

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    AddModelComponent,
    AddPacketComponent,
    ListModelsComponent,
    AddPacketComponent,
    ListPacketsComponent,
    ChangeColorDirective,
    AddOfferComponent,
    ListOffersComponent,
    AddColorComponent,
    ListColorsComponent,
    AddSizeComponent,
    ListSizesComponent,
    AddCityComponent,
    ListCitiesComponent,
    ConfigComponent,
    ListGovernoratesComponent,
    AddFbpageComponent,
    AddSteLivraisonComponent,
    ListFbpagesComponent,
    ListSteLivraisonComponent,
    StockComponent,
    StatistiqueComponent,
    StockHistoryComponent,
    PayedReturnComponent,
    DashboardComponent,
    VerificationComponent,
    PayedReturnComponent,
    UserComponent,
    GlobalConfComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    AppRoutingModule, TimelineModule,
    InputTextModule,
    InputSwitchModule,
    MultiSelectModule,
    DropdownModule,
    ToolbarModule,
    TableModule,
    CalendarModule,
    InputNumberModule,
    TabViewModule,
    SplitterModule,
    SelectButtonModule,
    DialogModule,
    CardModule,
    ConfirmDialogModule,
    ToastModule,
    ProgressSpinnerModule,
    InputTextareaModule,
    HttpClientModule,
    ChartModule,
    ToggleButtonModule,
    TreeTableModule,
    PaginatorModule,
    RadioButtonModule,
    FileUploadModule,
    TagModule,
    CheckboxModule,
    PasswordModule,
    CheckboxModule,
    SplitButtonModule,
    SpeedDialModule,
    ContextMenuModule,
    BadgeModule,
    ListboxModule,
    ChipModule,
    PickListModule,
    ConfirmPopupModule,
    AvatarGroupModule,
    AvatarModule,
    StepsModule,
    BreadcrumbModule,
    ReactiveFormsModule,
    RippleModule,
    OverlayPanelModule
  ],
  providers: [MessageService, ConfirmationService, CityTreeService, DatePipe,AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
