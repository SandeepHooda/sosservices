import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {LoginVO}  from '../login/LoginVO';
import {Contact}  from './contact';
import { Observable } from 'rxjs/Observable';
import {Global} from '../global/global';
import { HttpHeaders } from '@angular/common/http';

@Injectable()
export class ContactService {
    
  constructor(private http: HttpClient, private global: Global) { }

  addContact(contact: Contact, regID : String) :  Observable<Array<Contact>> {
    const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type':  'application/json',
          'Authorization': ''+regID
        })
      };
    return this.http.post<Array<Contact>>('/ws/contact', contact,httpOptions);
  }

  getContacts(regID: String) :  Observable<Array<Contact>> {
    const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type':  'application/json',
          'Authorization': ''+regID
        })
      };
    return this.http.get<Array<Contact>>('/ws/contact', httpOptions);
  }
}