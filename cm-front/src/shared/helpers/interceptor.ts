import { HTTP_INTERCEPTORS, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';



import { BehaviorSubject, Observable, pipe, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { StorageService } from '../services/strorage.service';
import { Router } from '@angular/router';

// const TOKEN_HEADER_KEY = 'Authorization';  // for Spring Boot back-end
const TOKEN_HEADER_KEY = 'x-access-token';    // for Node.js Express back-end

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

   constructor(private storageService: StorageService, private router: Router) {}

   intercept(req: HttpRequest<any>,
              next: HttpHandler): Observable<HttpEvent<any>> {

        const token = this.storageService.getToken();

        if (token) {
            const cloned = req.clone({
                headers: req.headers.set("Authorization",
                    "Bearer " + token)
            });

            return next.handle(cloned).pipe(
          catchError((error: HttpErrorResponse) => {
            if(error.status === 401 || error.status === 403) {
              this.storageService.isLoggedIn.next(false);
              this.storageService.removeUser();
              this.router.navigate(["/auth/login/" + this.storageService.getTenantName()]);
        }
        return throwError(error)
        })
            );
        }
        else {
            return next.handle(req);
        }
    }
}

export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];
