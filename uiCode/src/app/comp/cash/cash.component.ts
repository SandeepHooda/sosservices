import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-cash',
  templateUrl: './cash.component.html',
  styleUrls: ['./cash.component.css']
})
export class CashComponent implements OnInit {

  public txnAmount :String;
  constructor() { }

  ngOnInit() {
  }

  addCash():void{
    if (this.txnAmount){
      this.txnAmount= this.txnAmount.replace(/[^0-9\.]/g,'')
      if (this.txnAmount.length > 0){
        window.open("/AddCash?regID="+window.localStorage.getItem('regID')+"&amount="+this.txnAmount,"_self");
      }
      
    }
    
  }
}
