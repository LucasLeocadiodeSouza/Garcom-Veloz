import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigationCards } from './navigation-cards';

describe('NavigationCards', () => {
  let component: NavigationCards;
  let fixture: ComponentFixture<NavigationCards>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationCards]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavigationCards);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
