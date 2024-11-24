import { HTTP_INTERCEPTORS, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { StorageService } from '../services/strorage.service';
import { Router } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { environment } from 'src/environments/environment';

// const TOKEN_HEADER_KEY = 'Authorization';  // for Spring Boot back-end
// const TOKEN_HEADER_KEY = 'x-access-token';    // for Node.js Express back-end

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

   constructor(private storageService: StorageService,private confirmationService: ConfirmationService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.storageService.getToken();
    const cloned = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + token)
    });


    if (token) {
      return next.handle(cloned).pipe(
        catchError((error: HttpErrorResponse) => {
          if (!navigator.onLine && environment.production) {
            // alert('You are offline. Please check your network connection.');
             this.confirmationService.confirm({
               message: "Problème de connexion. Veuillez vérifier votre connexion svp !",
               header: 'Connection perdu',
               icon: 'pi pi-exclamation-triangle',
               accept: () => {
                 window.location.reload();
               },
               rejectVisible: false,
               acceptLabel: 'Reload'
             });
             return throwError(() => new Error('No Internet Connection'));
           }
          if (error.status === 500 || error.status === 501 || error.status === 502 || error.status === 503 ) {
            // alert('You are offline. Please check your network connection.');
             this.confirmationService.confirm({
               message: "Problème serveur. Veuillez informer Ahmed au 94 988 499 !",
               header: 'Problème envoie requete',
               icon: 'pi pi-exclamation-triangle',
               accept: () => {
                 //window.location.reload();
               },
               rejectVisible: false,
               acceptLabel: 'OK'
             });
             return throwError(() => error);
          }
          if (error.status === 0) {  // Network error or server down
            this.confirmationService.confirm({
              message: "Le serveur est actuellement injoignable. Veuillez réessayer plus tard. Veuillez informer Ahmed au 94 988 499 !",
              header: 'Problème de serveur',
              icon: 'pi pi-exclamation-triangle',
              accept: () => {
                // You might want to add any specific logic here or just acknowledge the message
              },
              rejectVisible: false,
              acceptLabel: 'OK'
            });
            return throwError(() => new Error('Server Unreachable'));
          }

          if (error.status === 401 || error.status === 403) {
            this.storageService.isLoggedIn.next(false);
            this.storageService.removeUser();
            this.router.navigate(['/login/' + this.storageService.getTenantName()])
              .then(() => console.log('Navigation to login successful'))
              .catch(navError => console.error('Navigation to login failed', navError));
          }
          return throwError(() => error);  // Updated throwError syntax
        })

      );
    } else {
      return next.handle(req);
    }
  }
}

export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];
