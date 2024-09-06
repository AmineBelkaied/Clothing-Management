import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/shared/services/auth.service';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { StorageService } from 'src/shared/services/strorage.service';


@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styles: [`
        :host ::ng-deep .pi-eye,
        :host ::ng-deep .pi-eye-slash {
            transform:scale(1.6);
            margin-right: 1rem;
            color: var(--primary-color) !important;
        }

    `]
})
export class LoginComponent {

    valCheck: string[] = ['remember'];

    form: any = {
        userName: null,
        password: null,
        tenantName: null
      };
      userName!: string;
      password!: string;
      isLoggedIn = false;
      isLoginFailed = false;
      errorMessage = '';
      roles: string[] = [];

      constructor(private authService: AuthService, private storageService: StorageService, private globalConfService: GlobalConfService, private router: Router, private route: ActivatedRoute) { }

      ngOnInit(): void {
        this.route.paramMap.subscribe(params => this.form.tenantName = params.get('tenantName'));
        if (this.storageService.isUserLoggedIn()) {
          this.isLoggedIn = true;
          this.roles = this.storageService.getUser().roles;
          this.router.navigate(["/"]);
        }

      }

      onSubmit(): void {
        this.form.tenantName ? this.login() : this.loginMaster();
      }

      login(): void {
        this.authService.login(this.form).subscribe({
          next: (data: any) => {
            this.storageService.saveUser(data);
            this.storageService.saveTenant(this.form.tenantName);

            this.isLoginFailed = false;
            this.isLoggedIn = true;
            this.storageService.isLoggedIn.next(true);
            this.roles = this.storageService.getUser().roles;
            this.router.navigate(["/packets"]);
            this.globalConfService.loadGlobalConf();
          },
          error: (err: any) => {
            console.log(err);

           // this.errorMessage = err.error.message;
            this.isLoginFailed = true;
          }
        });
      }

      loginMaster(): void {
        this.authService.loginMaster(this.form).subscribe({
          next: (data: any) => {
            this.storageService.saveUser(data);

            this.isLoginFailed = false;
            this.isLoggedIn = true;
            this.storageService.isLoggedIn.next(true);
            this.roles = this.storageService.getUser().roles;
            this.router.navigate(["/login/tenant"])
          },
          error: (err: any) => {
            console.log(err);
           // this.errorMessage = err.error.message;
            this.isLoginFailed = true;
          }
        });
      }

      reloadPage(): void {
        window.location.reload();
      }
}
