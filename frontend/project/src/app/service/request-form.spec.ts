import { TestBed } from '@angular/core/testing';

import { RequestForm } from './request-form';

describe('RequestForm', () => {
  let service: RequestForm;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RequestForm);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
