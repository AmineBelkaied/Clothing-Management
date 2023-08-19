import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

// import modules, directives and services
import { AppRoutingModule } from './app-routing.module';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MultiSelectModule } from 'primeng/multiselect';
import { DropdownModule } from 'primeng/dropdown';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToolbarModule } from 'primeng/toolbar';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { SelectButtonModule } from 'primeng/selectbutton';
import { InputSwitchModule } from 'primeng/inputswitch';
import { CalendarModule } from 'primeng/calendar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { CardModule } from 'primeng/card';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TabViewModule } from 'primeng/tabview';
import { SplitterModule } from 'primeng/splitter';
import { HttpClientModule } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ChangeColorDirective } from 'src/shared/directives/change-color.directive';
import { TimelineModule } from 'primeng/timeline';
import { TreeTableModule } from 'primeng/treetable';

// import components
import { AppComponent } from './app.component';
import { AddCityComponent } from './config/city/add-city/add-city.component';
import { ListCitiesComponent } from './config/city/list-cities/list-cities.component';
import { AddColorComponent } from './config/color/add-color/add-color.component';
import { ListColorsComponent } from './config/color/list-colors/list-colors.component';
import { ConfigComponent } from './config/config.component';
import { AddFbpageComponent } from './config/fbpage/add-fbpage/add-fbpage.component';
import { ListFbpagesComponent } from './config/fbpage/list-fbpages/list-fbpages.component';
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
    ListFbpagesComponent,
    StockComponent,
    StatistiqueComponent,
    StockHistoryComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
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
    PaginatorModule
  ],
  providers: [MessageService, ConfirmationService,CityTreeService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
