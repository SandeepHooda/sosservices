import { Component, OnInit } from '@angular/core';
import {Contact} from './contact';
import {CountryCodes} from './isdCodes'



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  public contact : Contact = new Contact();
  public contactList : Array<Contact> = [];
  public countryCodes : CountryCodes = new CountryCodes();
  constructor() { 
      
  }

  ngOnInit() {
    
  }

  addContact(userEntry : String):void {
    if (userEntry){
      //userEntry = userEntry.trim();
      let match =  userEntry.match(/([^\+]+)([^\s]+)(.+)/i);
      let aContact = new Contact();
      aContact.userEntry = userEntry;
      aContact.contactName = match[1];
      aContact.countryCode = match[2];
      aContact.phoneNumber = match[3];
      //if user press invalid entry and press enter the next line will remove enter from that
      console.log("#"+this.contact.userEntry+"#"+this.contact.userEntry.length)
      console.log("Match "+aContact.contactName+"#"+aContact.countryCode +"#"+aContact.phoneNumber)
      this.contact.userEntry = userEntry.substring(0,1) +" " + userEntry
      this.contact.userEntry = this.contact.userEntry.substring(1);
    
      
      if (aContact.contactName && aContact.countryCode && aContact.phoneNumber){
        
        if (aContact.countryCode.startsWith("+") && this.countryCodes.data[""+aContact.countryCode.substring(1)]){
          this.contactList.push(aContact);
          this.contact = new Contact();
        }
        
        
      }
    }
    
   
  }

  delete(i:number):void {
    this.contactList.splice(i,1);
  }
  
}

