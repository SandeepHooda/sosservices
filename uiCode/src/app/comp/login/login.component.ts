import { Component, OnInit } from '@angular/core';
import {LoginService} from './login.service';
import {Global} from '../global/global';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private loginService : LoginService, private global:Global, private _router: Router) { }

  ngOnInit() {
    if (!this.global.navigationDisabled){//It means user is already logged in
      this._router.navigate(['home']);
    }
  }

  signIN () :void{
    if(window.localStorage.getItem('regID')){
      localStorage.removeItem('regID');
      localStorage.removeItem('name');
    }
    
    window.open("/Oauth", "_self");
  }

  
}
