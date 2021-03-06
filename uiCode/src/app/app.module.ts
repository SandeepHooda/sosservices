import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './comp/home/home.component';
import { CashComponent } from './comp/cash/cash.component';
import { LoginComponent } from './comp/login/login.component';


import {SidebarModule} from 'primeng/sidebar';

import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { HeaderComponent } from './comp/header/header.component';
import { FooterComponent } from './comp/footer/footer.component';
import {HashLocationStrategy, Location, LocationStrategy} from '@angular/common';
import {LoginService} from './comp/login/login.service';
import {Global} from './comp/global/global';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {ConfirmationService} from 'primeng/api';
import {ContactService} from './comp/home/contact.service';
import {ToastModule} from 'primeng/toast';
import {MessageService} from 'primeng/api';
import { AddCashSuccessComponent } from './comp/add-cash-success/add-cash-success.component';

const appRoutes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'addcash',      component: CashComponent },
  { path: 'login',      component: LoginComponent },
  { path: 'addCashSuccess',      component: AddCashSuccessComponent },
  { path: '',  redirectTo: '/home',  pathMatch: 'full'},
  { path: '**', component: HomeComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    CashComponent,
    LoginComponent,
    HeaderComponent,
    FooterComponent,
    AddCashSuccessComponent
  ],
  imports: [
    RouterModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: false } 
    ),
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
        TableModule,
        HttpClientModule,
        InputTextModule,
        DialogModule,
        ButtonModule,
        SidebarModule,
        MessagesModule,
        MessageModule,
        ToastModule,
        ConfirmDialogModule
  ],
  providers: [Location, {provide: LocationStrategy, useClass: HashLocationStrategy}, 
    LoginService, Global,ConfirmationService, ContactService, MessageService],
  bootstrap: [AppComponent]
})
export class AppModule { 

}
