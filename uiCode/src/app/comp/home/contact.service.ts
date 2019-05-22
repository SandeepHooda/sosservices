import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {LoginVO}  from '../login/LoginVO';
import {Contact}  from './contact';
import { Observable } from 'rxjs/Observable';
import {Global} from '../global/global';
import { HttpHeaders } from '@angular/common/http';

@Injectable()
export class ContactService {
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type':  'application/json',
      'Authorization': ''+window.localStorage.getItem('regID')
    })
  };
    
  constructor(private http: HttpClient, private global: Global) { }

  addContact(contact: Contact) :  Observable<Array<Contact>> {
    
    return this.http.post<Array<Contact>>('/ws/contact', contact,this.httpOptions);
  }

  getContacts() :  Observable<Array<Contact>> {
    return this.http.get<Array<Contact>>('/ws/contact', this.httpOptions);
  }

  deleteContact(contact: Contact) :  Observable<Array<Contact>> {
    return this.http.delete<Array<Contact>>('/ws/contact/entry/'+contact.userEntry, this.httpOptions);
  }
}