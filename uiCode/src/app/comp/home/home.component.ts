import { Component, OnInit } from '@angular/core';
import {Contact} from './contact';
import {CountryCodes} from './isdCodes'
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {ConfirmationService} from 'primeng/api';



@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  public msgs = [];
  public contact : Contact = new Contact();
  public contactList : Array<Contact> = [];
  public countryCodes : CountryCodes = new CountryCodes();
  constructor(private confirmationService: ConfirmationService) { 
      
  }

  ngOnInit() {
    
  }

  addContact(userEntry : String):void {
    this.hideMsg();
    if (userEntry && userEntry.length>=13){
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
      this.fixUserInput(userEntry)
      
    
      let error = "";

      if (aContact.contactName && aContact.countryCode && aContact.phoneNumber){
        aContact.phoneNumber = aContact.phoneNumber.replace(/[^0-9]/g,'');
        let isdDetail = this.countryCodes.data[""+aContact.countryCode.substring(1)];
        if (aContact.phoneNumber.length >=7 && aContact.phoneNumber.length<=15 && isdDetail){
          this.contactList.push(aContact);
          this.contact = new Contact();
        }else {
          if (!isdDetail){
            error = "<br/> Country code "+aContact.countryCode+" is invalid. It must start with +  followed by digits."
          }
          if (aContact.phoneNumber.length < 7 || aContact.phoneNumber.length > 15){
            error = "<br/> Phone number must be between length 7 and 15 digits."
          }
        }
        
        
      }else {
        if (!aContact.contactName){
          error = "<br/> Please enter a valid contact name."
        }
        if (!aContact.phoneNumber){
          error = "<br/> Please enter a valid contact number."
        }
        if (!aContact.countryCode){
          error = "<br/> Please enter a valid ISD code."
        }
      }

      if (error !== ""){
        this.msgs.push({severity:'error', summary:'Invalid contact details', detail:error});
      }
      
    }else {
      this.msgs.push({severity:'error', summary:'Invalid contact details', detail:'Please enter valid contact details'});
      this.fixUserInput(userEntry);
    }
    
   
  }

  fixUserInput(userEntry : String):void{
    this.contact.userEntry = userEntry.substring(0,1) +" " + userEntry
      this.contact.userEntry = this.contact.userEntry.substring(1);
  }
  delete(i:number):void {
    let contact : Contact = this.contactList[i];
    this.confirmationService.confirm({
      message: 'Are you sure that you want to delete \"'+contact.contactName.trim()+'\" from your comtact list?',
      accept: () => {
        this.contactList.splice(i,1);
      }
  });
    
  }
  
  hideMsg() {
    this.msgs = [];
}
}

