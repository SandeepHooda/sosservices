import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-add-cash-success',
  templateUrl: './add-cash-success.component.html',
  styleUrls: ['./add-cash-success.component.css']
})
export class AddCashSuccessComponent implements OnInit {

  public txnID : String ="";
  constructor() { }

  ngOnInit() {
    
    if (window.location.href.indexOf("txn=") >=0){
      this.txnID = window.location.href.substring(window.location.href.indexOf("txn=")+4);
    }

   
    
  }

}
