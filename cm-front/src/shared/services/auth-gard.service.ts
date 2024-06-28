import { Injectable } from "@angular/core";
import { StorageService } from "./strorage.service";
import { StringUtils } from "../utils/string-utils";
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from "@angular/router";
@Injectable()
export class AuthGuard {
    constructor(
        private storageService: StorageService,
        private router: Router) { }
    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): boolean | Promise<boolean> {

        if (this.storageService.isUserLoggedIn()) {
            if (route.data['role'] && !StringUtils.checkExistence(route.data['role'], this.storageService.getRoles())) {
                this.storageService.getTenantName() ? this.router.navigate(['/login', this.storageService.getTenantName()]) :
                    this.router.navigateByUrl('/login/');
                return false;
            }
            return true;
        }
        this.storageService.getTenantName() ? this.router.navigate(['/login', this.storageService.getTenantName()]) :
            this.router.navigateByUrl('/login/');
        return false;
    }
}
