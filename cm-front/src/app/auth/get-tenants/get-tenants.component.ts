import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/shared/services/auth.service';
import { StorageService } from 'src/shared/services/strorage.service';

@Component({
  selector: 'app-get-tenants',
  templateUrl: './get-tenants.component.html',
  styleUrls: ['./get-tenants.component.scss']
})
export class GetTenantsComponent {

  tenants: any[] = [];

  valCheck: string[] = ['remember'];
    
  form: any = {
      userName: null,
      password: null,
      tenantOrClientId: ""
    };
    userName!: string;
    password!: string;
    isLoggedIn = false;
    isLoginFailed = false;
    errorMessage = '';
    roles: string[] = [];
  

  constructor(private authService: AuthService,private storageService: StorageService, private router: Router, private route: ActivatedRoute) { 
    this.form = this.router.getCurrentNavigation()!.extras.state;
  }
    
  ngOnInit(): void {
/*     let userName = this.route.snapshot.paramMap.get("username");
    this.form.userName = userName; */
    this.authService.getTenantsByUser(this.form.userName)
    .subscribe((result: any) => {
      console.log(result);
      this.tenants = result;
    });
    if (this.storageService.isUserLoggedIn()) {
      this.isLoggedIn = true;
      this.roles = this.storageService.getUser().roles;
    }
  }

    login(): void {
      //const { userName, password } = this.form;
        console.log("form " , this.form);
        
       this.authService.secondLogin(this.form).subscribe({
        next: (data: any) => {
          console.log("data", data);
          
          this.storageService.saveUser(data);
  
          this.isLoginFailed = false;
          this.isLoggedIn = true;
          this.roles = this.storageService.getUser().roles;
          this.router.navigate(["/"]);
          //this.reloadPage();
        },
        error: (err: any) => {
          console.log(err);
          
         // this.errorMessage = err.error.message;
          this.isLoginFailed = true;
        }
      });  

/*         this.authService.getTenantsByUser(this.form)
      .subscribe((result: any) => {
        console.log(result);
        
      })  */
    }
  
    reloadPage(): void {
      window.location.reload();
    }
}
