import { Component, OnInit } from '@angular/core';
import {Global} from '../global/global';
import {ContactService} from '../home/contact.service'

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  public balanceAmount: String = "";
  constructor(private global:Global, private contactService :ContactService) { }

   openNav():void {
     if (!this.global.navigationDisabled){
        document.getElementById("mySidenav").style.width = "250px";
     }else {
      document.getElementById("mySidenav").style.width = "0px";
     }
    
  }
  
   closeNav():void {
    document.getElementById("mySidenav").style.width = "0px";
  }

  ngOnInit() {
    this.checkBalance();
  }

  checkBalance():void{
    this.contactService.checkBalance( ).subscribe(
      (balanceAmount: String) => {
        this.balanceAmount = balanceAmount;
        
      }, error => {
        console.log(error)
       
      }
    );
  }
}
