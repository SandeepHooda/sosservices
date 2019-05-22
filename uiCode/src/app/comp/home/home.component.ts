import { Component, OnInit } from '@angular/core';
import {Contact} from './contact';
import {CountryCodes} from './isdCodes'
import {ConfirmationService} from 'primeng/api';
import {ContactService} from './contact.service';
import {MessageService} from 'primeng/api';


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
  constructor(private confirmationService: ConfirmationService, private contactService : ContactService,
    private messageService: MessageService) { 
      
  }

  ngOnInit() {
    this.getContactList();
  }

  getContactList():void{
    
    this.contactService.getContacts( ).subscribe(
      (contactList: Array<Contact>) => {
        this.contactList = [];
        for (let i=0;i<contactList.length; i++){
          this.contactList.push(contactList[i]);
        }
        
      }, error => {
        console.log(error)
        let serverError:string = "Something unusual happened. Please try again later.";
        this.msgs.push({severity:'error', summary:'Server Error', detail:serverError});
        //this.messageService.add({severity:'error', summary:'Service Error!', detail:serverError});
      }
    );
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
      
    
      let errorMsg = "";

      if (aContact.contactName && aContact.countryCode && aContact.phoneNumber){
        aContact.phoneNumber = aContact.phoneNumber.replace(/[^0-9]/g,'');
        let isdDetail = this.countryCodes.data[""+aContact.countryCode.substring(1)];
        if (aContact.phoneNumber.length >=7 && aContact.phoneNumber.length<=15 && isdDetail){
          this.contactList.push(aContact);
         
              this.contactService.addContact(aContact ).subscribe(
                (contactList: Array<Contact>) => {
                  this.contactList = [];
                  for (let i=0;i<contactList.length; i++){
                    this.contactList.push(contactList[i]);
                  }
                  
                }, error => {
                  this.contactList.pop();
                  console.log(error)
                  let serverError:string = "Something unusual happened. Please try again later.";
                  this.msgs.push({severity:'error', summary:'Server Error', detail:serverError});
                  //this.messageService.add({severity:'error', summary:'Service Error!', detail:serverError});
                }
              );
          this.contact = new Contact();
        }else {
          if (!isdDetail){
            errorMsg = "<br/> Country code "+aContact.countryCode+" is invalid. It must start with +  followed by digits."
          }
          if (aContact.phoneNumber.length < 7 || aContact.phoneNumber.length > 15){
            errorMsg = "<br/> Phone number must be between length 7 and 15 digits."
          }
        }
        
        
      }else {
        if (!aContact.contactName){
          errorMsg = "<br/> Please enter a valid contact name."
        }
        if (!aContact.phoneNumber){
          errorMsg = "<br/> Please enter a valid contact number."
        }
        if (!aContact.countryCode){
          errorMsg = "<br/> Please enter a valid ISD code."
        }
      }

      if (errorMsg !== ""){
        this.msgs.push({severity:'error', summary:'Invalid contact details', detail:errorMsg});
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

        this.contactService.deleteContact(contact ).subscribe(
          (contactList: Array<Contact>) => {
            this.contactList = [];
            for (let i=0;i<contactList.length; i++){
              this.contactList.push(contactList[i]);
            }
            
          }, error => {
            this.contactList.splice(i,0, contact);
            console.log(error)
            let serverError:string = "Something unusual happened. Please try again later.";
            this.msgs.push({severity:'error', summary:'Server Error', detail:serverError});
            //this.messageService.add({severity:'error', summary:'Service Error!', detail:serverError});
          }
        );
      }
  });
    
  }
  
  hideMsg() {
    this.msgs = [];
}
}

