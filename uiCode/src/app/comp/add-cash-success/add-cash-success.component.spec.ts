import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCashSuccessComponent } from './add-cash-success.component';

describe('AddCashSuccessComponent', () => {
  let component: AddCashSuccessComponent;
  let fixture: ComponentFixture<AddCashSuccessComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddCashSuccessComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCashSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
